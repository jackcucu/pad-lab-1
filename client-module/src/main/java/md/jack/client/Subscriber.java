package md.jack.client;

import md.jack.marshalling.JsonMarshaller;
import md.jack.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import static java.lang.Runtime.getRuntime;
import static md.jack.model.ClientType.SUBSCRIBER;

class Subscriber
{
    private static final Logger LOGGER = LoggerFactory.getLogger(Subscriber.class);
    private Socket socket;

    Subscriber(final Socket socket)
    {
        this.socket = socket;
    }

    public void run()
    {
        LOGGER.info("Hi Subscriber");
        try
        {
            final BufferedReader reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            final PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
            final Message payload = new Message();
            payload.setTopic("md.jack.topic");
            payload.setClientType(SUBSCRIBER);
            getRuntime().addShutdownHook(new ProcessorHook(socket, payload));

            final String marshall = new JsonMarshaller().marshall(payload);
            writer.println(marshall);
            writer.flush();
            String message;

            while ((message = reader.readLine()) != null)
            {
                final Message unmarshall = new JsonMarshaller().unmarshall(message);
                System.out.println(unmarshall.getName() + " " + unmarshall.getPayload());
            }
        }
        catch (Exception exception)
        {
            LOGGER.error("Error {} with cause {}", exception.getMessage(), exception.getCause());
        }
    }
}
