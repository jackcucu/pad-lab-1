package md.jack.model.db;

import md.jack.model.db.abs.AbstractEntity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.Set;

@Entity
@Table(name = "TOPIC")
public class Topic extends AbstractEntity
{
    private String name;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private Consumer consumer;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "CONSUMER_TOPIC",
            joinColumns = @JoinColumn(name = "topic_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "consumer_id"))
    private Set<Consumer> consumers;

    public String getName()
    {
        return name;
    }

    public void setName(final String name)
    {
        this.name = name;
    }

    public Consumer getConsumer()
    {
        return consumer;
    }

    public void setConsumer(final Consumer consumer)
    {
        this.consumer = consumer;
    }

    public Set<Consumer> getConsumers()
    {
        return consumers;
    }

    public void setConsumers(final Set<Consumer> consumers)
    {
        this.consumers = consumers;
    }
}
