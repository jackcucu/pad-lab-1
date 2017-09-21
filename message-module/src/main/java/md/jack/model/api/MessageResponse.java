package md.jack.model.api;

public class MessageResponse
{
    private Message message;

    public MessageResponse(final Message message)
    {
        this.message = message;
    }

    public Message getMessage()
    {
        return message;
    }

    public void setMessage(final Message message)
    {
        this.message = message;
    }
}
