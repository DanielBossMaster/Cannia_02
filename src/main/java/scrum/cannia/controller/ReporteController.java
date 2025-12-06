package scrum.cannia.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import scrum.cannia.service.ReporteService;

import java.util.Map;

@Controller
@RequestMapping("/inventario/reportes")
@CrossOrigin(origins = "*")
public class ReporteController {

    @Autowired
    private ReporteService reporteService;

    // 1. MÉTODO PARA MOSTRAR LA PÁGINA HTML
    @GetMapping
    public String mostrarPaginaReportes(Model model) {
        System.out.println("✅ ReporteController: Mostrando página de reportes");
        System.out.println("✅ Buscando archivo: Inventario/Reporte.html");
        model.addAttribute("titulo", "Reportes de Productos");
        return "Inventario/Reporte";
    }

    // 2. MÉTODO PARA LA API (JSON)
    @GetMapping("/api/productos")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> generarReporteApi(
            @RequestParam(required = false, defaultValue = "BARRAS") String tipo,
            @RequestParam(required = false) Boolean estado) {

        try {
            Map<String, Object> reporte = reporteService.generarReporteProductos(tipo, estado);
            return ResponseEntity.ok(reporte);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error al generar reporte: " + e.getMessage()));
        }
    }

    // 3. MÉTODO PARA OPCIONES DE FILTRO
    @GetMapping("/api/opciones-filtro")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> obtenerOpcionesFiltro() {
        return ResponseEntity.ok(reporteService.obtenerOpcionesFiltro());
    }

    // 4. MÉTODO PARA RESUMEN GENERAL
    @GetMapping("/api/resumen")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> obtenerResumenGeneral() {
        try {
            Map<String, Object> reporte = reporteService.generarReporteProductos("TORTA", null);
            return ResponseEntity.ok(reporte);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error al obtener resumen"));
        }
    }
}