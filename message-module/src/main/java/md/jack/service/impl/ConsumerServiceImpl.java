package md.jack.service.impl;

import md.jack.model.db.Consumer;
import md.jack.repository.ConsumerRepository;
import md.jack.service.ConsumerService;
import md.jack.service.abs.impl.EntityServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class ConsumerServiceImpl extends EntityServiceImpl<Consumer, ConsumerRepository> implements ConsumerService
{
    @Override
    public Consumer getByToken(final String token)
    {
        return repository.getByRegistrationToken(token);
    }
}
