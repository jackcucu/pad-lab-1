package md.jack.client;

import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Client
{
    private static ExecutorService executorService = Executors.newFixedThreadPool(10);

    public static void main(final String[] args)
    {
        try
        {
            if (args.length >= 1)
            {
                final Socket clientSocket = new Socket("localhost", 9898);

                if (args[0].equalsIgnoreCase("p"))
                {
                    executorService.execute(new Publisher(clientSocket));
                }
                else if (args[0].equalsIgnoreCase("s"))
                {
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

