package md.jack.service.abs.impl;

import md.jack.model.db.abs.AbstractEntity;
import md.jack.service.abs.EntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public abstract class EntityServiceImpl <E extends AbstractEntity, R extends JpaRepository<E, Long>>
        implements EntityService<E>
{
    @Autowired
    private R repository;

    @Override
    public void add(final E entity)
    {
        repository.save(entity);
    }

    @Override
    public void edit(final E entity)
    {
        repository.save(entity);
    }

    @Override
    public void delete(final Long id)
    {
        repository.delete(id);
    }

    @Override
    public void deleteAll()
    {
        repository.deleteAll();
    }

    @Override
    public E get(final Long id)
    {
        return repository.getOne(id);
    }

    @Override
    public List<E> getAll()
    {
        return repository.findAll();
    }

    @Override
    public Page<E> getAll(final Pageable page)
    {
        return repository.findAll(page);
    }

    @Override
    public Long getCount()
    {
        return repository.count();
    }
}
