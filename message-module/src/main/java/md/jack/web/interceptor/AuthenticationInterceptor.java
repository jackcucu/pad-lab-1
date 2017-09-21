package md.jack.web.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import md.jack.model.api.Message;
import md.jack.validators.ApiValidator;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static md.jack.utils.Constants.Http.Header.TOKEN_HEADER_API_KEY;
import static md.jack.utils.Constants.Http.Response.MESSAGE_INVALID_API_KEY;
import static md.jack.utils.FunctionalUtils.throwableExecuteIf;


public class AuthenticationInterceptor extends HandlerInterceptorAdapter
{
    private final ApiValidator apiValidator;

    public AuthenticationInterceptor(final ApiValidator apiValidator)
    {
        this.apiValidator = apiValidator;
    }

    @Override
    public boolean preHandle(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final Object handler) throws Exception
    {
        final boolean isValidVersion = apiValidator.isApiKeyValid(request.getHeader(TOKEN_HEADER_API_KEY));

        throwableExecuteIf(() -> !isValidVersion, () -> sendResponse(response));

        return isValidVersion;
    }

    private void sendResponse(final HttpServletResponse response) throws IOException
    {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        final String error = new ObjectMapper().writeValueAsString(new Message(MESSAGE_INVALID_API_KEY));
        response.getWriter().write(error);
    }
}
