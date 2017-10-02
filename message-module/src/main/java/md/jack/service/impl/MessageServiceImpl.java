package md.jack.service.impl;

import md.jack.model.db.Message;
import md.jack.repository.MessageRepository;
import md.jack.service.MessageService;
import md.jack.service.abs.impl.EntityServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class MessageServiceImpl extends EntityServiceImpl<Message, MessageRepository>
        implements MessageService
{
    
}
