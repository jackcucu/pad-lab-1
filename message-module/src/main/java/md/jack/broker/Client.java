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
import org.springframework.core.convert.converter.Converter;
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
import java.util.stream.Collectors;

import static md.jack.model.ClientType.PUBLISHER;
import static md.jack.model.ClientType.SUBSCRIBER;
import static md.jack.model.TransportingType.PERSISTENT;
import static md.jack.utils.FunctionalUtils.executeIfElse;

@Component
@Scope("prototype")
public class Client implements Runnable
{
    private final static Logger LOGGER = LoggerFactory.getLogger(Client.class);

    @Autowired
    private Converter<MessageDto, Message> converter;

    @Autowired
    private Converter<Message, MessageDto> messageDtoConverter;

    @Autowired
    private Map<String, Tuple3<Boolean, BlockingQueue<Message>, List<Client>>> topics;

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

                            LOGGER.warn(
                                    "Connection with publisher from {} on port {} closed improperly last will {}",
                                    socket.getInetAddress(),
                                    socket.getPort(),
                                    payload.getPayload());
                            break;
                        }

                        cleanUp(payload);

                        executeIfElse(
                                () -> topics.containsKey(payload.getTopic()),
                                () -> addToQueue(payload),
                                () -> buildQueue(payload)
                        );
                    }
                    else if (payload.getClientType() == SUBSCRIBER)
                    {
                        final String regex = payload.getTopic()
                                .replace(".", "\\.")
                                .replace("*", ".*");

                        if (payload.isClosing())
                        {
                            LOGGER.warn(
                                    "Connection with subscriber from {} on port {} closed improperly last will {}",
                                    socket.getInetAddress(),
                                    socket.getPort(),
                                    payload.getPayload());

                            topics.entrySet().stream()
                                    .filter(it -> it.getKey().matches(regex))
                                    .map(it -> it.getValue()._3())
                                    .forEach(it -> it.removeIf(o -> o.equals(this)));

                            socket.close();
                            break;
                        }

                        final List<List<Client>> topics = this.topics.entrySet().stream()
                                .filter(it -> it.getKey().matches(regex))
                                .map(Map.Entry::getValue)
                                .map(Tuple3::_3)
                                .collect(Collectors.toList());

                        if (!topics.isEmpty())
                        {
                            topics.forEach(it -> it.add(this));
                        }
                        else
                        {
                            socket.close();
                        }
                    }
                }
            }

        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage());
        }
    }

    private void cleanUp(final MessageDto payload)
    {
        if (topics.containsKey(payload.getTopic()))
        {
            final Tuple3<Boolean, BlockingQueue<Message>, List<Client>> queue = topics.get(payload
                    .getTopic());

            if (!queue._1() && queue._2().isEmpty() && queue._3().isEmpty())
            {
                topics.remove(payload.getTopic());
            }
        }
    }

    private void addToQueue(final MessageDto payload)
    {
        final Tuple3<Boolean, BlockingQueue<Message>, List<Client>> queue = topics.get(payload.getTopic());

        if (queue._1())
        {
            final Message message = saveMessage(payload);

            queue._2().add(message);
        }
        else
        {
            queue._2().add(converter.convert(payload));
        }

    }

    private Message saveMessage(final MessageDto payload)
    {
        final Topic topic = topicService.getByName(payload.getTopic());

        final Message message = new Message();

        message.setPayload(payload.getPayload());
        message.getTopics().add(topic);

        if (message.getPayload() != null)
        {
            messageService.add(message);
        }

        return message;
    }

    private void buildQueue(final MessageDto payload)
    {
        final BlockingQueue<Message> channel = new ArrayBlockingQueue<>(1024);

        final List<Client> subscribers = new CopyOnWriteArrayList<>();

        channel.add(converter.convert(payload));

        final boolean isPersistent = payload.getTransportingType().equals(PERSISTENT);

        topics.put(payload.getTopic(), Tuple.of(isPersistent, channel, subscribers));

        if (isPersistent)
        {
            final Topic topic = Topic.getBuilder()
                    .name(payload.getTopic())
                    .build();

            topicService.add(topic);
        }

        final AsyncWriter asyncWriter = new AsyncWriter();

        asyncWriter.setChannel(channel);
        asyncWriter.setSubscribers(subscribers);
        asyncWriter.setPersistent(isPersistent);
        asyncWriter.setMessageService(messageService);
        asyncWriter.setMessageDtoConverter(messageDtoConverter);

        taskExecutor.execute(asyncWriter);
    }
}