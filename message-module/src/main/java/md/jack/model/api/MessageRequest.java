package md.jack.model.api;

public class MessageRequest
{
    private String registrationToken;

    private Message message;

    public String getRegistrationToken()
    {
        return registrationToken;
    }

    public void setRegistrationToken(final String registrationToken)
    {
        this.registrationToken = registrationToken;
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
