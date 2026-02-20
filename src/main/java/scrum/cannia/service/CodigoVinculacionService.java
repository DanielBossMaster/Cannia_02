package scrum.cannia.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import scrum.cannia.model.CodigoVinculacionModel;
import scrum.cannia.model.PropietarioModel;
import scrum.cannia.model.VeterinarioModel;
import scrum.cannia.repository.CodigoVinculacionRepository;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Objects;

@AllArgsConstructor
@Service
public class CodigoVinculacionService {

    private final CodigoVinculacionRepository codigoRepository;

    // ============================================
    //      GENERAR CÓDIGO DE VINCULACIÓN
    // ============================================

    @Transactional
    public CodigoVinculacionModel generarCodigo(
            PropietarioModel propietario,
            VeterinarioModel veterinario
    ) {

        // Regla 1: propietario debe pertenecer al veterinario
        if (!Objects.equals(
                propietario.getVeterinario().getId(),
                veterinario.getId()
        )) {
            throw new IllegalStateException(
                    "El propietario no pertenece a este veterinario"
            );
        }
        // Regla 2: invalidar códigos anteriores no usados
        codigoRepository
                .findByPropietarioIdAndUsadoFalse(propietario.getId())
                .forEach(c -> {
                    c.setUsado(true);
                    codigoRepository.save(c);
                });


        // Crear nuevo código
        CodigoVinculacionModel codigo = new CodigoVinculacionModel();
        codigo.setCodigo(generarCodigoSeguro());
        codigo.setPropietario(propietario);
        codigo.setVeterinario(veterinario);
        codigo.setFechaCreacion(LocalDateTime.now());
        codigo.setFechaExpiracion(LocalDateTime.now().plusMinutes(30));
        codigo.setUsado(false);

        return codigoRepository.save(codigo);
    }

    // ============================================
    //      VALIDAR CÓDIGO
    // ============================================

    public CodigoVinculacionModel validarCodigo(String codigoTexto) {

        CodigoVinculacionModel codigo = codigoRepository
                .findByCodigo(codigoTexto)
                .orElseThrow(() ->
                        new IllegalArgumentException("Código inválido"));

        if (codigo.isUsado()) {
            throw new IllegalStateException("El código ya fue utilizado");
        }

        if (codigo.getFechaExpiracion().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("El código ha expirado");
        }

        return codigo;
    }

    // ============================================
    //      MARCAR CÓDIGO COMO USADO
    // ============================================

    @Transactional
    public void marcarComoUsado(CodigoVinculacionModel codigo) {
        codigo.setUsado(true);
        codigoRepository.save(codigo);
    }

    // ============================================
    //      GENERADOR SEGURO
    // ============================================

    private String generarCodigoSeguro() {
        final String caracteres = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
        SecureRandom random = new SecureRandom();

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            sb.append(caracteres.charAt(
                    random.nextInt(caracteres.length())
            ));
        }
        return sb.toString();
    }
}