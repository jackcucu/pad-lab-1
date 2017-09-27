package md.jack.broker;

import javafx.concurrent.Task;
import javaslang.Tuple;
import javaslang.Tuple2;
import md.jack.marshalling.JsonMarshaller;
import md.jack.model.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;

import static md.jack.model.ClientType.PUBLISHER;
import static md.jack.model.ClientType.SUBSCRIBER;
import static md.jack.utils.FunctionalUtils.executeIfElse;

@Component
@Scope("prototype")
public class Client implements Runnable
{
    @Autowired
    private Map<String, Tuple2<BlockingQueue<Message>, List<Client>>> topics;

    @Autowired
    private TaskExecutor taskExecutor;

    private Socket socket;

    Socket getSocket()
    {
        return socket;
    }

    void setSocket(final Socket socket)
    {
        this.socket = socket;
    }

    @Override
    public void run()
    {
        try
        {
            final BufferedReader reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));

            String message;
            while (true)
            {
                while ((message = reader.readLine()) != null)
                {
                    final Message payload = new JsonMarshaller().unmarshall(message);
                    if (payload.getClientType() == PUBLISHER)
                    {
                        if (payload.isRegister())
                        {
                            socket.close();
                            break;
                        }
                        executeIfElse(
                                () -> topics.containsKey(payload.getTopic()),
                                () -> topics.get(payload.getTopic())._1().add(payload),
                                () -> buildQueue(payload)
                        );
                    }
                    else if (payload.getClientType() == SUBSCRIBER)
                    {
                        if (payload.isRegister())
                        {
                            topics.get(payload.getTopic())._2().removeIf(it -> it.equals(this));
                            socket.close();
                            break;
                        }
                        topics.get(payload.getTopic())._2().add(this);
                    }
                }
            }

        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage());
        }
    }

    private void buildQueue(final Message payload)
    {
        final BlockingQueue<Message> channel = new ArrayBlockingQueue<>(1024);
        final List<Client> subscribers = new CopyOnWriteArrayList<>();

        taskExecutor.execute(new AsyncWriter(channel, subscribers));

        topics.put(payload.getTopic(), Tuple.of(channel, subscribers));
    }
}