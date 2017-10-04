package md.jack.converter;

import md.jack.dto.MessageDto;
import md.jack.model.db.Message;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class MessageToMessageDtoConverter implements Converter<Message, MessageDto>
{
    @Override
    public MessageDto convert(final Message source)
    {
        final MessageDto target = new MessageDto();

        target.setPayload(source.getPayload());
        target.setId(source.getId());

        return target;
    }
}
