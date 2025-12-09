
package scrum.cannia.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    //Url qeu crea la busqueda por filtros
    @Bean
    public WebClient busquedaWebClient() {

        return WebClient.create("http://localhost:8081");
    }
}