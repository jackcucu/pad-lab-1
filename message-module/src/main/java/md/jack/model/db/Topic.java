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
@Table(name = "TOPIC")
public class Topic extends AbstractEntity
{
    private String name;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "CONSUMER_TOPIC",
            joinColumns = @JoinColumn(name = "topic_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "message_id"))
    private Set<Message> messages = new HashSet<>();

    public String getName()
    {
        return name;
    }

    public void setName(final String name)
    {
        this.name = name;
    }

    public Set<Message> getMessages()
    {
        return messages;
    }

    public void setMessages(final Set<Message> messages)
    {
        this.messages = messages;
    }

    public static Builder getBuilder()
    {
        return new Builder();
    }

    public static Builder getBuilder(final Topic topic)
    {
        return new Builder(topic);
    }

    public static class Builder
    {
        private Topic topic;

        Builder()
        {
            topic = new Topic();
        }

        Builder(final Topic topic)
        {
            this.topic = topic;
        }

        public Builder name(final String name)
        {
            topic.setName(name);
            return this;
        }

        public Builder messages(final Set<Message> messages)
        {
            topic.setMessages(messages);
            return this;
        }

        public Topic build()
        {
            return topic;
        }
    }
}
