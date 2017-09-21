package md.jack.repository;

import md.jack.model.db.Consumer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConsumerRepository extends JpaRepository<Consumer, Long>
{
    Consumer getByRegistrationToken(String registrationToken);
}
