package scrum.cannia.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import scrum.cannia.model.HistoriaClinicaModel;
import scrum.cannia.model.VacunaModel;
import scrum.cannia.model.PropietarioModel;
import scrum.cannia.model.MascotaModel;
import scrum.cannia.repository.HistoriaClinicaRepository;
import scrum.cannia.repository.VacunaRepository;
import scrum.cannia.repository.MascotaRepository;
import scrum.cannia.repository.PropietarioRepository;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook; // Para archivos .xlsx
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;

@Controller
public class HistoriaClinicaController {

    @Autowired
    private VacunaRepository vacunaRepository;

    @Autowired
    private HistoriaClinicaRepository historiaRepository;

    @Autowired
    private PropietarioRepository propietarioRepository;

    @Autowired
    private MascotaRepository mascotaRepository;

    // ★★★ ENDPOINT CORREGIDO PARA OBTENER HISTORIAS CLÍNICAS ★★★
    @GetMapping("/obtenerHistoriasClinicas/{mascotaId}")
    @ResponseBody
    public ResponseEntity<?> obtenerHistoriasClinicas(@PathVariable Long mascotaId) {
        System.out.println(" Solicitando historias para mascota: " + mascotaId);

        try {
            List<HistoriaClinicaModel> historias = historiaRepository.findByMascotaIdOrderByFechaHoraDesc(mascotaId);
            System.out.println(" Historias encontradas: " + historias.size());

            // Crear una lista simplificada para evitar problemas de serialización
            List<Map<String, Object>> historiasSimplificadas = new ArrayList<>();

            for (HistoriaClinicaModel historia : historias) {
                Map<String, Object> historiaMap = new HashMap<>();
                historiaMap.put("idHistoriaClinica", historia.getIdHistoriaClinica());
                historiaMap.put("fechaHora", historia.getFechaHora());
                historiaMap.put("peso", historia.getPeso());
                historiaMap.put("anamnesis", historia.getAnamnesis());
                historiaMap.put("diagnostico", historia.getDiagnostico());
                historiaMap.put("tratamiento", historia.getTratamiento());

                // Solo información básica de la mascota para evitar relaciones circulares
                if (historia.getMascota() != null) {
                    Map<String, Object> mascotaMap = new HashMap<>();
                    mascotaMap.put("idMascota", historia.getMascota().getId());
                    mascotaMap.put("nomMascota", historia.getMascota().getNomMascota());
                    historiaMap.put("mascota", mascotaMap);
                }

                historiasSimplificadas.add(historiaMap);
            }

            return ResponseEntity.ok(historiasSimplificadas);

        } catch (Exception e) {
            System.out.println(" Error en controller: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.ok(new ArrayList<>()); // Devolver lista vacía en caso de error
        }
    }

    /**
     * Guardar vacuna
     */
    @PostMapping("/guardarVacuna")
    public String guardarVacuna(
            @ModelAttribute VacunaModel vacuna,
            @RequestParam("idMascota") Long idMascota) {

        // Buscar la mascota
        MascotaModel mascota = mascotaRepository.findById(idMascota)
                .orElseThrow(() -> new RuntimeException("Mascota no encontrada"));

        // Relacionar la vacuna con la mascota
        vacuna.setMascota(mascota);

        // Guardar vacuna
        vacunaRepository.save(vacuna);

        return "redirect:/veterinario/historiaclinica";
    }

    /**
     * Ver propietario con sus mascotas
     */
    @GetMapping("/propietario/{id}")
    public String verPropietario(@PathVariable Long id, Model model) {
        PropietarioModel propietario = propietarioRepository.findById(id).orElse(null);
        List<MascotaModel> mascotas = mascotaRepository.findByPropietarioId(id);

        model.addAttribute("propietario", propietario);
        model.addAttribute("mascotas", mascotas);
        // 👇 agregar el objeto vacío para que el formulario no falle
        model.addAttribute("historiaClinica", new HistoriaClinicaModel());

        return "veterinario/historiaclinica";
    }

    /**
     * Guardar historia clínica
     */
    @PostMapping("/guardarHistoria")
    public ResponseEntity<Map<String, Object>> guardarHistoria(
            @ModelAttribute HistoriaClinicaModel historia,
            @RequestParam("mascotaId") Long mascotaId) {

        MascotaModel mascota = mascotaRepository.findById(mascotaId)
                .orElseThrow(() -> new RuntimeException("Mascota no encontrada"));

        historia.setMascota(mascota);

        HistoriaClinicaModel historiaGuardada = historiaRepository.save(historia);

        Map<String, Object> response = new HashMap<>();
        response.put("id", historiaGuardada.getIdHistoriaClinica());
        response.put("mensaje", "Historia guardada con éxito");

        return ResponseEntity.ok(response);
    }
}