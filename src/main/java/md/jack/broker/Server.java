package md.jack.broker;

import javaslang.Tuple;
import javaslang.Tuple2;
import md.jack.marshalling.JsonMarshaller;
import md.jack.model.Message;
import md.jack.utils.FunctionalUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static md.jack.broker.ClientType.PUBLISHER;
import static md.jack.broker.ClientType.SUBSCRIBER;
import static md.jack.utils.Constants.Server.BIND_PORT;
import static md.jack.utils.FunctionalUtils.executeIfElse;

public class Server
{
    private static final Map<String, Tuple2<BlockingQueue<Message>, List<Client>>> TOPICS = new HashMap<>();
    private static ExecutorService executorService = Executors.newFixedThreadPool(100);

    public static void main(String[] args) throws IOException
    {
        System.out.println("Server waiting for connection on port " + BIND_PORT);
        ServerSocket serverSocket = new ServerSocket(BIND_PORT);

        while (true)
        {
            executorService.execute(new Client(serverSocket.accept()));
        }
    }

    static class Client implements Runnable
    {
        private Socket socket;

        private BufferedReader reader;

        Client(Socket clientSocket)
        {
            this.socket = clientSocket;
        }

        public Socket getSocket()
        {
            return socket;
        }

        public void setSocket(final Socket socket)
        {
            this.socket = socket;
        }

        @Override
        public void run()
        {
            try
            {
                reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));

                String message;
                while (true)
                {
                    while ((message = reader.readLine()) != null)
                    {
                        final Message payload = new JsonMarshaller().unmarshall(message);

                        if (payload.getClientType() == PUBLISHER)
                        {
                            executeIfElse(
                                    () -> TOPICS.containsKey(payload.getTopic()),
                                    () -> buildQueue(payload),
                                    () -> TOPICS.get(payload.getTopic())._1().add(payload)
                            );
                        }
                        else if(payload.getClientType() == SUBSCRIBER)
                        {
                            TOPICS.get(payload.getTopic())._2().add(this);
                        }
                    }
                }

            }
            catch (Exception ex)
            {
                System.out.println(ex.getMessage());
            }
        }

        private void buildQueue(final Message payload)
        {
            final ArrayBlockingQueue<Message> channel = new ArrayBlockingQueue<>(1024);
            final CopyOnWriteArrayList<Client> subscribers = new CopyOnWriteArrayList<>();

            executorService.execute(new AsyncWriter(channel, subscribers));

            TOPICS.put(payload.getTopic(), Tuple.of(channel, subscribers));
        }
    }

    static class AsyncWriter implements Runnable
    {
        private BlockingQueue<Message> channel;
        private List<Client> subscribers;

        AsyncWriter(BlockingQueue<Message> channel, List<Client> subscribers)
        {
            this.channel = channel;
            this.subscribers = subscribers;
        }

        @Override
        public void run()
        {
            while (true)
            {
                while (!subscribers.isEmpty())
                {
                    while (!channel.isEmpty())
                    {
                        for (Client subscriber : subscribers)
                        {
                            try
                            {
                                final PrintWriter writer = new PrintWriter(subscriber.getSocket()
                                        .getOutputStream(), true);

                                final Message message = channel.peek();
                                final String marshall = new JsonMarshaller().marshall(message);

                                writer.println(marshall);
                                writer.flush();
                            }
                            catch (IOException e)
                            {
                                e.printStackTrace();
                            }
                        }
                        channel.poll();
                    }
                }
            }
        }
    }
}