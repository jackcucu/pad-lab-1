package md.jack.broker;

import md.jack.dto.MessageDto;
import md.jack.marshalling.JsonMarshaller;
import md.jack.model.db.Message;
import md.jack.service.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public class AsyncWriter implements Runnable
{
    private static final Logger LOGGER = LoggerFactory.getLogger(AsyncWriter.class);

    private Converter<Message, MessageDto> messageDtoConverter;

    private MessageService messageService;

    private BlockingQueue<Message> channel;

    private List<Client> subscribers;

    private boolean isPersistent;

    @Override
    public void run()
    {
        while (true)
        {
            while (channel.size() != 0)
            {
                while (!subscribers.isEmpty())
                {
                    while (!channel.isEmpty())
                    {
                        for (Client subscriber : subscribers)
                        {
                            try
                            {
                                final PrintWriter writer = new PrintWriter(subscriber.getSocket()
                                        .getOutputStream(), true);

                                final MessageDto message = messageDtoConverter.convert(channel.peek());
                                final String marshall = new JsonMarshaller().marshall(message);

                                writer.println(marshall);
                            }
                            catch (IOException exception)
                            {
                                LOGGER.error(
                                        "Cannot connect to subscriber from {} with port",
                                        subscriber.getSocket().getInetAddress(),
                                        subscriber.getSocket().getPort());
                            }
                        }

                        final Message message = channel.peek();
                        message.setConsumed(true);

                        if (message.getPayload() != null)
                        {
                            messageService.edit(message);
                        }

                        channel.poll();
                    }
                }
            }
            if (!isPersistent)
            {
                break;
            }
        }
        LOGGER.info("Queue was destroyed");
    }

    public void setMessageDtoConverter(final Converter<Message, MessageDto> messageDtoConverter)
    {
        this.messageDtoConverter = messageDtoConverter;
    }

    public void setMessageService(final MessageService messageService)
    {
        this.messageService = messageService;
    }

    public void setChannel(final BlockingQueue<Message> channel)
    {
        this.channel = channel;
    }

    public void setSubscribers(final List<Client> subscribers)
    {
        this.subscribers = subscribers;
    }

    public void setPersistent(final boolean persistent)
    {
        isPersistent = persistent;
    }
}