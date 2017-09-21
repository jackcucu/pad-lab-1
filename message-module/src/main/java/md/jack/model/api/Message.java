package md.jack.model.api;

public class Message
{
    private String payload;

    public Message(final String payload)
    {
        this.payload = payload;
    }

    public String getPayload()
    {
        return payload;
    }

    public void setPayload(final String payload)
    {
        this.payload = payload;
    }
}
