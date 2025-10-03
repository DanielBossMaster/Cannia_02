package scrum.cannia.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import scrum.cannia.model.VacunaModel;
import scrum.cannia.repository.VacunaRepository;
import java.util.*;

@RestController
@RequestMapping("/api")
public class VacunaController {

    private final VacunaRepository vacunaRepository;

    public VacunaController(VacunaRepository vacunaRepository) {
        this.vacunaRepository = vacunaRepository;
    }

    @GetMapping("/obtenerVacunas/{mascotaId}")
    @ResponseBody
    public ResponseEntity<?> obtenerVacunas(@PathVariable Long mascotaId) {
        System.out.println("Solicitando vacunas para mascota: " + mascotaId);

        try {
            // Usa el método SIMPLE que SÍ existe
            List<VacunaModel> vacunas = vacunaRepository.findByMascotaId(mascotaId);

            // Ordena manualmente por fecha de aplicación (más reciente primero)
            if (vacunas != null && !vacunas.isEmpty()) {
                vacunas.sort((v1, v2) -> {
                    if (v1.getFechaAplicacion() == null) return 1;
                    if (v2.getFechaAplicacion() == null) return -1;
                    return v2.getFechaAplicacion().compareTo(v1.getFechaAplicacion());
                });
            }

            System.out.println("Vacunas encontradas: " + (vacunas != null ? vacunas.size() : 0));

            // Crear respuesta simplificada
            List<Map<String, Object>> response = new ArrayList<>();

            if (vacunas != null) {
                for (VacunaModel vacuna : vacunas) {
                    Map<String, Object> item = new HashMap<>();
                    item.put("lote", vacuna.getLote());
                    item.put("fechaAplicacion", vacuna.getFechaAplicacion());
                    item.put("fechaRefuerzo", vacuna.getFechaRefuerzo());
                    item.put("fechaVencimiento", vacuna.getFechaVencimiento());
                    item.put("laboratorio", vacuna.getLaboratorio());
                    response.add(item);
                }
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.out.println("Error obteniendo vacunas: " + e.getMessage());
            e.printStackTrace(); // Para ver el error completo
            return ResponseEntity.ok(new ArrayList<>());
        }
    }
}