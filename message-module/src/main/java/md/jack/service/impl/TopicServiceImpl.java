package md.jack.service.impl;

import md.jack.model.db.Topic;
import md.jack.repository.TopicRepository;
import md.jack.service.TopicService;
import md.jack.service.abs.impl.EntityServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class TopicServiceImpl extends EntityServiceImpl<Topic, TopicRepository> implements TopicService
{
    @Override
    public void deleteByName(final String name)
    {
        repository.deleteByName(name);
    }

    @Override
    public Topic getByName(final String name)
    {
        return repository.findByName(name);
    }
}
