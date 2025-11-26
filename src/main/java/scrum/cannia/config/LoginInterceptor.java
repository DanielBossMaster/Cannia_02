package scrum.cannia.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        HttpSession session = request.getSession(false);

        // Validar si hay usuario en sesión
        if (session == null || session.getAttribute("usuario") == null) {
            response.sendRedirect("/login");
            return false;   // Detiene la petición
        }

        return true; // Permite continuar si hay sesión
    }
}
