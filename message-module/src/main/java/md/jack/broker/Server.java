package md.jack.broker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.ServerSocket;

import static md.jack.utils.Constants.Server.BIND_PORT;

@Component
public class Server
{
    @Autowired
    private TaskExecutor taskExecutor;

    @Autowired
    private ApplicationContext context;

    @PostConstruct
    public void startServer() throws IOException
    {
        System.out.println("Server waiting for connection on port " + BIND_PORT);
        final ServerSocket serverSocket = new ServerSocket(BIND_PORT);

        while (true)
        {
            final Client client = (Client) context.getBean("client");
            client.setSocket(serverSocket.accept());

            taskExecutor.execute(client);
        }
    }
}