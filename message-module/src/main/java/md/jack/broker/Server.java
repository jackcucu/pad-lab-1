package md.jack.broker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import static md.jack.utils.Constants.Server.BIND_PORT;

@Component
public class Server
{
    private static final Logger LOGGER = LoggerFactory.getLogger(Server.class);

    @Autowired
    private TaskExecutor taskExecutor;

    @Autowired
    private ApplicationContext context;

    @PostConstruct
    public void startServer() throws IOException
    {
        LOGGER.info("Server waiting for connection on port {}", BIND_PORT);

        final ServerSocket serverSocket = new ServerSocket(BIND_PORT);

        while (true)
        {
            final Socket socket = serverSocket.accept();

            LOGGER.info("New connection received from {} on port {}", socket.getInetAddress(), socket.getPort());

            final Client client = (Client) context.getBean("client");
            client.setSocket(socket);

            taskExecutor.execute(client);
        }
    }
}