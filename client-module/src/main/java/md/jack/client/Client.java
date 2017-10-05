package md.jack.client;

import java.net.Socket;

import static md.jack.utils.Constants.Server.BIND_PORT;

public class Client
{
    public static void main(final String[] args)
    {
        try
        {
            if (args.length >= 1)
            {
                final Socket clientSocket = new Socket("localhost", BIND_PORT);

                if (args[0].equalsIgnoreCase("p"))
                {
                    new Publisher(clientSocket).run();
                }
                else if (args[0].equalsIgnoreCase("s"))
                {
                    new Subscriber(clientSocket).run();
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

