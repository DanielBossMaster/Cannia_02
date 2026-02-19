package scrum.cannia.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import scrum.cannia.model.UsuarioModel;
import scrum.cannia.repository.UsuarioRepository;

import java.util.List;

    @Service
    @AllArgsConstructor
    public class AdminService {

        private final UsuarioRepository usuarioRepository;

        // Usuarios pendientes CON datos
        public List<UsuarioModel> obtenerPendientes() {
            return usuarioRepository.findUsuariosPendientesConDatos();
        }

        public void aprobarUsuario(Long id) {
            UsuarioModel usuario = usuarioRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            if (!"PENDIENTE".equals(usuario.getEstado())) {
                throw new IllegalStateException("El usuario no está pendiente");
            }

            usuario.setEstado("ACTIVO");
            usuarioRepository.save(usuario);
        }

        public void rechazarUsuario(Long id) {
            UsuarioModel usuario = usuarioRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            if (!"PENDIENTE".equals(usuario.getEstado())) {
                throw new IllegalStateException("El usuario no está pendiente");
            }

            usuario.setEstado("RECHAZADO");
            usuarioRepository.save(usuario);
        }
    }



