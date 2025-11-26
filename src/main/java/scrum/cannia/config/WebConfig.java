package scrum.cannia.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private LoginInterceptor loginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/veterinario/**") // PROTEGE TODO
                .excludePathPatterns(
                        "/login",           // Deja entrar al login
                        "/logout",          // logout permitido
                        "/css/**",          // recursos estáticos
                        "/js/**",
                        "/img/**",
                        "/dist/**"
                );
    }
}
