package md.jack.model;

public class MessageDto
{
    private Long id;

    private String payload;

    private String topic;

    private ClientType clientType;

    private TransportingType transportingType;

    private boolean isClosing;

    public Long getId()
    {
        return id;
    }

    public void setId(final Long id)
    {
        this.id = id;
    }

    public String getPayload()
    {
        return payload;
    }

    public void setPayload(final String payload)
    {
        this.payload = payload;
    }

    public String getTopic()
    {
        return topic;
    }

    public void setTopic(final String topic)
    {
        this.topic = topic;
    }

    public ClientType getClientType()
    {
        return clientType;
    }

    public void setClientType(final ClientType clientType)
    {
        this.clientType = clientType;
    }

    public TransportingType getTransportingType()
    {
        return transportingType;
    }

    public void setTransportingType(final TransportingType transportingType)
    {
        this.transportingType = transportingType;
    }

    public boolean isClosing()
    {
        return isClosing;
    }

    public void setClosing(final boolean closing)
    {
        isClosing = closing;
    }
}
