package md.jack.converter;

import md.jack.dto.MessageDto;
import md.jack.model.db.Message;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class MessageDtoToMessageConverter implements Converter<MessageDto, Message>
{
    @Override
    public Message convert(final MessageDto source)
    {
        final Message message = new Message();

        message.setPayload(source.getPayload());

        return message;
    }
}
