package md.jack.client;

import md.jack.marshalling.JsonMarshaller;
import md.jack.model.MessageDto;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class ProcessorHook extends Thread
{

    private Socket socket;

    private MessageDto message;

    ProcessorHook(final Socket socket, final MessageDto message)
    {
        this.socket = socket;
        this.message = message;
    }

    @Override
    public void run()
    {
        try
        {
            final PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
            message.setClosing(true);

            final String marshall = new JsonMarshaller().marshall(message);
            writer.println(marshall);
            socket.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}