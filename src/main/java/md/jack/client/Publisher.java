package md.jack.client;

import md.jack.marshalling.JsonMarshaller;
import md.jack.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import static md.jack.model.ClientType.PUBLISHER;

class Publisher implements Runnable
{
    private static final Logger LOGGER = LoggerFactory.getLogger(Publisher.class);
    private Socket socket;

    Publisher(final Socket socket)
    {
        this.socket = socket;
    }

    public void run()
    {
        LOGGER.info("Hi Publisher");
        try
        {
            if (socket.isConnected())
            {
                LOGGER.info("Client connected to {} on port {}",
                        socket.getInetAddress(),
                        socket.getPort());

                final PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
                final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

                while (true)
                {
                    System.out.println("Type your message to send to server..type 'EXIT' to exit");

                    final Message message = new Message();
                    message.setName("Eugene");
                    message.setClientType(PUBLISHER);
                    message.setTopic("md.jack.topic");
                    message.setPayload(reader.readLine());

                    final String marshall = new JsonMarshaller().marshall(message);
                    writer.println(marshall);
                    writer.flush();

                    if (message.getPayload().equals("EXIT"))
                    {
                        break;
                    }
                }
                socket.close();
            }
        }
        catch (Exception exception)
        {
            LOGGER.error("Error {} with cause {}", exception.getMessage(), exception.getCause());
        }
    }
}
