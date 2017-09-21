package md.jack.web;

import md.jack.validators.ApiValidator;
import md.jack.web.interceptor.AuthenticationInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
@EnableWebMvc
public class WebMvcConfig extends WebMvcConfigurerAdapter
{
    @Autowired
    private ApiValidator apiValidator;

    @Override
    public void addInterceptors(final InterceptorRegistry registry)
    {
        registry.addInterceptor(new AuthenticationInterceptor(apiValidator))
                .addPathPatterns("/api/**");
    }
}
