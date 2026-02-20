package scrum.cannia.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import scrum.cannia.model.UsuarioModel;
import scrum.cannia.repository.UsuarioRepository;

import java.io.IOException;
@AllArgsConstructor
@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final UsuarioRepository usuarioRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {

        String username = authentication.getName();

        // Buscamos al usuario en la base de datos
        UsuarioModel usuario = usuarioRepository.findByUsuario(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado en la base de datos tras login exitoso"));

        String estado = usuario.getEstado();
        String rol = usuario.getRol();

        // LOG DE DEBUG: Esto aparecerá en tu consola de Spring Boot
        System.out.println("DEBUG LOGIN -> Usuario: " + username + " | Estado: " + estado + " | Rol: " + rol);

        // 1. Lógica por Estado (No Activos)
        if ("PENDIENTE".equalsIgnoreCase(estado)) {
            response.sendRedirect("/verificacion/proceso");
            return;
        }

        if ("RECHAZADO".equalsIgnoreCase(estado)) {
            response.sendRedirect("/login?rechazado");
            return;
        }

        if ("INACTIVO".equalsIgnoreCase(estado)) {
            response.sendRedirect("/verificacion");
            return;
        }

        // 2. Lógica por Rol (Solo si el estado es ACTIVO u otro no filtrado arriba)
        if ("ADMIN".equalsIgnoreCase(rol)) {
            response.sendRedirect("/admin/index");
        } else if ("PROPIETARIO".equalsIgnoreCase(rol)) {
            response.sendRedirect("/propietario/index");
        } else if ("VETERINARIO".equalsIgnoreCase(rol)) {
            response.sendRedirect("/veterinario/index");
        } else if ("FUNDACION".equalsIgnoreCase(rol)) {
            response.sendRedirect("/fundacion/index");
        } else {
            // Si el rol no coincide con ninguno, lo mandamos a inicio (ruta permitida en tu SecurityConfig)
            response.sendRedirect("/inicio");
        }
    }
}