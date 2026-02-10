package scrum.cannia.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import scrum.cannia.model.UsuarioModel;
import scrum.cannia.repository.UsuarioRepository;

@Configuration
public class Admin {

    @Bean
    CommandLineRunner initAdmin(UsuarioRepository usuarioRepository,
                                PasswordEncoder passwordEncoder) {
        return args -> {

            boolean existeAdmin = usuarioRepository.existsByRol("ADMIN");

            if (!existeAdmin) {
                UsuarioModel admin = new UsuarioModel();
                admin.setUsuario("admin");
                admin.setContrasena(passwordEncoder.encode("admin123"));
                admin.setRol("ADMIN");
                admin.setEstado("ACTIVO");

                usuarioRepository.save(admin);

                System.out.println("✅ ADMIN creado correctamente");
            }
        };
    }
}
