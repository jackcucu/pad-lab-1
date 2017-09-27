package md.jack.client;

import md.jack.marshalling.JsonMarshaller;
import md.jack.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

class Publisher implements Runnable
{
    private static final Logger LOGGER = LoggerFactory.getLogger(Publisher.class);
    private Socket clientSocket = null;

    Publisher(final Socket clientSocket)
    {
        this.clientSocket = clientSocket;
    }

    public void run()
    {
        LOGGER.info("Hi Publisher");
        try
        {
            if (clientSocket.isConnected())
            {
                LOGGER.info("Client connected to {} on port {}",
                        clientSocket.getInetAddress(),
                        clientSocket.getPort());

                final PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);
                final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

                while (true)
                {
                    System.out.println("Type your message to send to server..type 'EXIT' to exit");

                    final Message message = new Message();
                    message.setName("Eugene");
                    message.setPayload(reader.readLine());

                    if (message.getPayload().equals("EXIT"))
                    {
                        break;
                    }

                    final String marshall = new JsonMarshaller().marshall(message);
                    writer.println(marshall);
                    writer.flush();

                }
                clientSocket.close();
            }
        }
        catch (Exception exception)
        {
            LOGGER.error("Error {} with cause {}", exception.getMessage(), exception.getCause());
        }
    }
}
