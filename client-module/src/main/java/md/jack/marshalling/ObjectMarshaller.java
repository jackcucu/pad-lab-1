package md.jack.marshalling;

import com.fasterxml.jackson.core.JsonProcessingException;
import md.jack.model.MessageDto;

import java.io.IOException;

public interface ObjectMarshaller
{
    String marshall(MessageDto message) throws JsonProcessingException;

    MessageDto unmarshall(String message) throws IOException;
}
