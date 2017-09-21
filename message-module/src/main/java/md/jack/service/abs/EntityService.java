package md.jack.service.abs;

import md.jack.model.db.abs.AbstractEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EntityService<E extends AbstractEntity>
{
    void add(E entity);

    void edit(E entity);

    void delete(Long id);

    void deleteAll();

    E get(Long id);

    List<E> getAll();

    Page<E> getAll(Pageable page);

    Long getCount();
}
