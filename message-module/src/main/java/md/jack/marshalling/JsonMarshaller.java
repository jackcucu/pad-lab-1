package md.jack.marshalling;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import md.jack.model.Message;

import java.io.IOException;

public class JsonMarshaller implements ObjectMarshaller
{
    @Override
    public String marshall(final Message message) throws JsonProcessingException
    {
        return new ObjectMapper().writeValueAsString(message);
    }

    @Override
    public Message unmarshall(final String message) throws IOException
    {
        return new ObjectMapper().readValue(message, Message.class);
    }
}
