package md.jack.client;

import md.jack.marshalling.JsonMarshaller;
import md.jack.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

class Subscriber implements Runnable
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
            String message;

            while ((message = reader.readLine()) != null)
            {
                final Message payload = new JsonMarshaller().unmarshall(message);
                System.out.println(payload.getName() + " " + payload.getPayload());
            }
        }
        catch (Exception exception)
        {
            LOGGER.error("Error {} with cause {}", exception.getMessage(), exception.getCause());
        }
    }
}
