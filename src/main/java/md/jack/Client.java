package md.jack;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client
{
    public static void main(String[] args)
    {
        try
        {
            Socket sock = new Socket("localhost", 9898);
            SendThread sendThread = new SendThread(sock);
            Thread thread = new Thread(sendThread);
            thread.start();
            RecieveThread recieveThread = new RecieveThread(sock);
            Thread thread2 = new Thread(recieveThread);
            thread2.start();
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
    }
}

class RecieveThread implements Runnable
{
    Socket sock = null;
    BufferedReader recieve = null;

    public RecieveThread(Socket sock)
    {
        this.sock = sock;
    }//end constructor

    public void run()
    {
        try
        {
            recieve = new BufferedReader(new InputStreamReader(this.sock.getInputStream()));
            String msgRecieved = null;
            while ((msgRecieved = recieve.readLine()) != null)
            {
                System.out.println("From md.jack.Server: " + msgRecieved);
                System.out.println("Please enter something to send to server..");
            }
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
    }//end run
}//end class recievethread

class SendThread implements Runnable
{
    private Socket sock = null;
    private PrintWriter print = null;
    private BufferedReader reader = null;

    public SendThread(Socket sock)
    {
        this.sock = sock;
    }//end constructor

    public void run()
    {
        try
        {
            if (sock.isConnected())
            {
                System.out.println("md.jack.Client connected to " + sock.getInetAddress() + " on port " + sock.getPort());
                this.print = new PrintWriter(sock.getOutputStream(), true);
                while (true)
                {
                    System.out.println("Type your message to send to server..type 'EXIT' to exit");
                    reader = new BufferedReader(new InputStreamReader(System.in));
                    String msgtoServerString = null;
                    msgtoServerString = reader.readLine();
                    this.print.println(msgtoServerString);
                    this.print.flush();

                    if (msgtoServerString.equals("EXIT"))
                    {
                        break;
                    }
                }
                sock.close();
            }
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
    }
}