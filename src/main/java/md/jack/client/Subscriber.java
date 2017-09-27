package md.jack.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

class Subscriber implements Runnable
{
    private static final Logger LOGGER = LoggerFactory.getLogger(Subscriber.class);
    private Socket clientSocket;

    Subscriber(final Socket clientSocket)
    {
        this.clientSocket = clientSocket;
    }

    public void run()
    {
        LOGGER.info("Hi Subscriber");
        try
        {
            final BufferedReader reader = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
            String message;

            while ((message = reader.readLine()) != null)
            {
                System.out.println("Server: " + message);
            }
        }
        catch (Exception exception)
        {
            LOGGER.error("Error {} with cause {}", exception.getMessage(), exception.getCause());
        }
    }
}
