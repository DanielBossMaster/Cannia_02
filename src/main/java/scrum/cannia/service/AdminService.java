package scrum.cannia.service;

import org.springframework.stereotype.Service;
import scrum.cannia.model.UsuarioModel;
import scrum.cannia.repository.UsuarioRepository;

import java.util.List;

@Service
public class AdminService {

    private final UsuarioRepository usuarioRepository;

    public AdminService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    // Obtener usuarios pendientes
    public List<UsuarioModel> obtenerPendientes() {
        return usuarioRepository.findByEstadoAndRolIn(
                "PENDIENTE",
                List.of("VETERINARIO", "FUNDACION")
        );
    }

    // Aprobar usuario
    public void aprobarUsuario(Long id) {
        UsuarioModel usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        usuario.setEstado("ACTIVO");
        usuarioRepository.save(usuario);

    }

    // Rechazar usuario
    public void rechazarUsuario(Long id) {
        UsuarioModel usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        usuario.setEstado("RECHAZADO");
        usuarioRepository.save(usuario);
    }
}
