package scrum.cannia.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudinaryConfig {

    @Bean
    public Cloudinary cloudinary(){

        return new Cloudinary(ObjectUtils.asMap(

                "cloud_name", "duluqo73x",
                "api_key", "696228332137235",
                "api_secret", "Int7Hkr-RwFNDce60aGaB9sTP0Q"

        ));
    }

}