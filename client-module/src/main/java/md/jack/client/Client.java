package md.jack.client;

import md.jack.marshalling.JsonMarshaller;
import md.jack.model.Message;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static md.jack.model.ClientType.PUBLISHER;
import static md.jack.model.ClientType.SUBSCRIBER;
import static md.jack.utils.Constants.Server.BIND_PORT;

public class Client
{
    private static ExecutorService executorService = Executors.newFixedThreadPool(10);

    public static void main(final String[] args)
    {
        try
        {
            if (args.length >= 1)
            {
                final Socket clientSocket = new Socket("localhost", BIND_PORT);
                final PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);

                if (args[0].equalsIgnoreCase("p"))
                {
                    final Message message = new Message();
                    message.setClientType(PUBLISHER);
                    message.setTopic("md.jack.topic");

                    final String marshall = new JsonMarshaller().marshall(message);
                    System.out.println(marshall);
                    writer.println(marshall);
                    writer.flush();
                    executorService.execute(new Publisher(clientSocket));
                }
                else if (args[0].equalsIgnoreCase("s"))
                {
                    final Message message = new Message();
                    message.setTopic("md.jack.topic");
                    message.setClientType(SUBSCRIBER);

                    final String marshall = new JsonMarshaller().marshall(message);
                    writer.println(marshall);
                    writer.flush();
                    executorService.execute(new Subscriber(clientSocket));
                }
                else
                {
                    System.out.println("Enter valid client type(p-publisher, s-subscriber)");
                }

            }
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
    }
}

