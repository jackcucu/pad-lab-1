package md.jack.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import md.jack.marshalling.JsonMarshaller;
import md.jack.model.MessageDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import static java.lang.Runtime.getRuntime;
import static md.jack.model.ClientType.SUBSCRIBER;

class Subscriber
{
    private static final Logger LOGGER = LoggerFactory.getLogger(Subscriber.class);
    private Socket socket;

    Subscriber(final Socket socket)
    {
        this.socket = socket;
        setLastWillAndTestament();
    }

    void run()
    {
        LOGGER.info("Hi Subscriber");
        try
        {
            final BufferedReader reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            final PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);

            connectToBroker(writer);

            String message;

            while ((message = reader.readLine()) != null)
            {
                final MessageDto unmarshall = new JsonMarshaller().unmarshall(message);
                System.out.println("message : " + unmarshall.getPayload());
            }
        }
        catch (Exception exception)
        {
            LOGGER.error("Error {} with cause {}", exception.getMessage(), exception.getCause());
        }
    }

    private void connectToBroker(final PrintWriter writer) throws JsonProcessingException
    {
        final MessageDto payload = new MessageDto();
        payload.setTopic(getTopic());
        payload.setClientType(SUBSCRIBER);

        final String marshall = new JsonMarshaller().marshall(payload);
        writer.println(marshall);
    }

    private String getTopic()
    {
        String topic;

        System.out.println("Enter topic name(format org.dep.product.message_type)");

        final Scanner scanner = new Scanner(System.in);

        while (!(topic = scanner.nextLine()).matches(".*\\..*\\..*\\..*"))
        {
            System.out.println("Invalid topic format(org.dep.product.message_type)");
        }
        return topic;
    }

    private void setLastWillAndTestament()
    {
        System.out.println("Last will ?");

        final MessageDto payload = new MessageDto();
        payload.setPayload(new Scanner(System.in).nextLine());
        payload.setTopic("lastwill");
        payload.setClientType(SUBSCRIBER);

        getRuntime().addShutdownHook(new ProcessorHook(socket, payload));
    }
}
