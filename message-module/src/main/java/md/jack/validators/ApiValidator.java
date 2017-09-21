package md.jack.validators;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ApiValidator
{
    @Value("${api.key}")
    private String apiKey;

    public boolean isApiKeyValid(final String key)
    {
        return apiKey.equals(key);
    }
}
