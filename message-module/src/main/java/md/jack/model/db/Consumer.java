package md.jack.model.db;

import md.jack.model.db.abs.AbstractEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.Set;

@Entity
@Table(name = "CONSUMER")
public class Consumer extends AbstractEntity
{
    @Column(name = "registration_token")
    private String registrationToken;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "CONSUMER_TOPIC",
            joinColumns = @JoinColumn(name = "consumer_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "topic_id"))
    private Set<Topic> topics;

    public String getRegistrationToken()
    {
        return registrationToken;
    }

    public void setRegistrationToken(final String registrationToken)
    {
        this.registrationToken = registrationToken;
    }

    public Set<Topic> getTopics()
    {
        return topics;
    }

    public void setTopics(final Set<Topic> topics)
    {
        this.topics = topics;
    }
}
