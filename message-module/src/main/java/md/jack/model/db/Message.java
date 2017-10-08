package md.jack.model.db;

import md.jack.model.db.abs.AbstractEntity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "MESSAGE")
public class Message extends AbstractEntity
{
    private String payload;

    private boolean isConsumed;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "MESSAGE_TOPIC",
            joinColumns = @JoinColumn(name = "message_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "topic_id"))
    private Set<Topic> topics = new HashSet<>();

    public static Builder getBuilder()
    {
        return new Builder();
    }

    public static Builder getBuilder(final Message message)
    {
        return new Builder(message);
    }

    public String getPayload()
    {
        return payload;
    }

    public void setPayload(final String payload)
    {
        this.payload = payload;
    }

    public boolean isConsumed()
    {
        return isConsumed;
    }

    public void setConsumed(final boolean consumed)
    {
        isConsumed = consumed;
    }

    public Set<Topic> getTopics()
    {
        return topics;
    }

    public void setTopics(final Set<Topic> topics)
    {
        this.topics = topics;
    }

    public static class Builder
    {
        private Message message;

        Builder()
        {
            message = new Message();
        }

        Builder(final Message message)
        {
            this.message = message;
        }

        public Builder payload(final String payload)
        {
            message.setPayload(payload);
            return this;
        }

        public Builder isConsumed(final boolean isConsumed)
        {
            message.setConsumed(isConsumed);
            return this;
        }

        public Builder topics(final Set<Topic> topics)
        {
            message.setTopics(topics);
            return this;
        }

        public Message build()
        {
            return message;
        }
    }
}
