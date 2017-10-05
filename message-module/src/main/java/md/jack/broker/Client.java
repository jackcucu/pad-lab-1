package md.jack.broker;

import javaslang.Tuple;
import javaslang.Tuple3;
import md.jack.dto.MessageDto;
import md.jack.marshalling.JsonMarshaller;
import md.jack.model.db.Message;
import md.jack.model.db.Topic;
import md.jack.service.MessageService;
import md.jack.service.TopicService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import static java.util.Optional.ofNullable;
import static md.jack.model.ClientType.PUBLISHER;
import static md.jack.model.ClientType.SUBSCRIBER;
import static md.jack.utils.FunctionalUtils.executeIfElse;

@Component
@Scope("prototype")
public class Client implements Runnable
{
    private final static Logger LOGGER = LoggerFactory.getLogger(Client.class);

    @Autowired
    private Map<String, Tuple3<Boolean, BlockingQueue<MessageDto>, List<Client>>> topics;

    @Autowired
    private TaskExecutor taskExecutor;

    @Autowired
    private MessageService messageService;

    @Autowired
    private TopicService topicService;

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
                    final MessageDto payload = new JsonMarshaller().unmarshall(message);
                    if (payload.getClientType() == PUBLISHER)
                    {
                        if (payload.isClosing())
                        {
                            socket.close();
                            LOGGER.warn("Connection with client closed un properly last will {}", payload.getPayload());
                            break;
                        }
                        executeIfElse(
                                () -> topics.containsKey(payload.getTopic()),
                                () -> addToQueue(payload),
                                () -> buildQueue(payload)
                        );
                    }
                    else if (payload.getClientType() == SUBSCRIBER)
                    {
                        if (payload.isClosing())
                        {
                            LOGGER.warn("Connection with client closed un properly last will {}", payload.getPayload());
                            socket.close();
                            topics.get(payload.getTopic())._3().removeIf(it -> it.equals(this));
                            break;
                        }

                        final String regex = payload.getTopic()
                                .replace(".", "\\.")
                                .replace("*", ".*");

                        topics.entrySet().stream()
                                .filter(it -> it.getKey().matches(regex))
                                .forEach(it -> it.getValue()._3().add(this));
                    }
                }
            }

        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage());
        }
    }

    private void addToQueue(final MessageDto payload)
    {
        final Tuple3<Boolean, BlockingQueue<MessageDto>, List<Client>> queue = topics.get(payload.getTopic());

        if (!queue._1())
        {
            taskExecutor.execute(new AsyncWriter(queue._2(), queue._3(), false));
            topics.computeIfPresent(payload.getTopic(), (key, it) -> queue.update1(true));
        }

        ofNullable(payload.getPayload()).ifPresent(it -> {
            queue._2().add(payload);
            saveMessage(payload);
        });
    }

    private void saveMessage(final MessageDto payload)
    {
        final Topic topic = topicService.getByName(payload.getTopic());

        final Message message = new Message();

        message.setPayload(payload.getPayload());
        message.getTopics().add(topic);

        messageService.add(message);
    }

    private void buildQueue(final MessageDto payload)
    {
        final BlockingQueue<MessageDto> channel = new ArrayBlockingQueue<>(1024);

        channel.add(payload);

        final List<Client> subscribers = new CopyOnWriteArrayList<>();

        topics.put(payload.getTopic(), Tuple.of(true, channel, subscribers));

        taskExecutor.execute(new AsyncWriter(channel, subscribers, false));

        final Topic topic = Topic.getBuilder()
                .name(payload.getTopic())
                .build();

        topicService.add(topic);
    }
}