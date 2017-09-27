package md.jack.marshalling;

import com.fasterxml.jackson.core.JsonProcessingException;
import md.jack.model.Message;

import java.io.IOException;

public interface ObjectMarshaller
{
    String marshall(Message message) throws JsonProcessingException;

    Message unmarshall(String message) throws IOException;
}
