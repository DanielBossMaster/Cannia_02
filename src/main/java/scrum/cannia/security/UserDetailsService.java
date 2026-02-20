package scrum.cannia.security;

import lombok.AllArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import scrum.cannia.model.UsuarioModel;
import scrum.cannia.repository.UsuarioRepository;

import java.util.List;

@AllArgsConstructor
@Service
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        UsuarioModel usuario = usuarioRepository.findByUsuario(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("Usuario no encontrado"));

        List<SimpleGrantedAuthority> roles = List.of(
                new SimpleGrantedAuthority("ROLE_" + usuario.getRol())
        );

        return new User(
                usuario.getUsuario(),
                usuario.getContrasena(),
                roles
        );
    }

}
