package md.jack.repository;

import md.jack.model.db.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TopicRepository extends JpaRepository<Topic, Long>
{
    void deleteByName(String name);

    Topic findByName(String name);
}
