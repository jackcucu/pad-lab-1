package md.jack.model;

public class MessageDto
{
    private String name;

    private String payload;

    private String topic;

    private ClientType clientType;

    private boolean isRegister;

    public String getTopic()
    {
        return topic;
    }

    public void setTopic(final String topic)
    {
        this.topic = topic;
    }

    public String getName()
    {
        return name;
    }

    public void setName(final String name)
    {
        this.name = name;
    }

    public String getPayload()
    {
        return payload;
    }

    public void setPayload(final String payload)
    {
        this.payload = payload;
    }

    public boolean isRegister()
    {
        return isRegister;
    }

    public void setRegister(final boolean register)
    {
        isRegister = register;
    }

    public ClientType getClientType()
    {
        return clientType;
    }

    public void setClientType(final ClientType clientType)
    {
        this.clientType = clientType;
    }
}
