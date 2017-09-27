package md.jack;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server
{
    private static ExecutorService executorService = Executors.newFixedThreadPool(100, r -> {
        Thread thread = Executors.defaultThreadFactory().newThread(r);
        thread.setDaemon(true);
        return thread;
    });

    public static void main(String[] args) throws IOException
    {
        final int port = 9898;
        System.out.println("md.jack.Server waiting for connection on port " + port);
        ServerSocket serverSocket = new ServerSocket(port);
        while (true)
        {
            Socket clientSocket = serverSocket.accept();
            System.out.println("Recieved connection from " + clientSocket.getInetAddress() + " on port " + clientSocket.getPort());

            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        }
    }
}

class Reader implements Runnable
{
    private Socket clientSocket = null;
    private BufferedReader reader = null;

    Reader(Socket clientSocket)
    {
        this.clientSocket = clientSocket;
    }

    public void run()
    {
        try
        {
            reader = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));

            String messageString;
            while (true)
            {
                while ((messageString = reader.readLine()) != null)
                {
                    if (messageString.equals("EXIT"))
                    {
                        break;
                    }
                    System.out.println("From md.jack.Client: " + messageString);
                }
                this.clientSocket.close();
                System.exit(0);
            }

        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage());
        }
    }
}

class Writer implements Runnable
{
    private PrintWriter writer;
    private Socket clientSocket = null;

    Writer(Socket clientSock)
    {
        this.clientSocket = clientSock;
    }

    @Override
    public void run()
    {
        try
        {
            writer = new PrintWriter(new OutputStreamWriter(this.clientSocket.getOutputStream()));//get outputstream

            while (true)
            {
                writer.println();
                writer.flush();
                System.out.println("Please enter something to send back to client..");
            }
        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage());
        }
    }
}