package scrum.cannia.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import scrum.cannia.model.ProductoModel;
import scrum.cannia.repository.ProductoRepository;
import scrum.cannia.service.PdfExportService;
import scrum.cannia.service.ReporteService;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletResponse;
import scrum.cannia.service.VentasReporteService;

import java.io.IOException;


import java.util.Date;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/inventario/reportes")
@CrossOrigin(origins = "*")
public class ReporteController {

    @Autowired
    private ReporteService reporteService;

    @Autowired
    private PdfExportService pdfExportService;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private VentasReporteService ventasReporteService;

    // 1. METODO PARA MOSTRAR LA PÁGINA HTML
    @GetMapping("")
    public String mostrarPaginaReportes(Model model) {
        model.addAttribute("titulo", "Reportes de Productos");
        model.addAttribute("modulo", "Inventario");
        return "Inventario/Reporte";
    }

    // 2. METODO PARA LA API (JSON)
    @GetMapping("/api/productos")
    public ResponseEntity<Map<String, Object>> generarReporte(  // CAMBIAR a Map
                                                                @RequestParam String tipo,
                                                                @RequestParam(required = false, defaultValue = "productos") String reporte) {

        Map<String, Object> reporteResult;  // CAMBIAR a Map

        switch (reporte) {
            case "ventas-categoria":
                reporteResult = ventasReporteService.generarVentasPorCategoria();
                break;
            case "ventas-dia":
                reporteResult = ventasReporteService.generarVentasDelDia();
                break;
            case "activos":
                reporteResult = reporteService.generarReporteProductos(tipo, true);  // ← tipo PRIMERO
                break;
            case "inactivos":
                reporteResult = reporteService.generarReporteProductos(tipo, false); // ← tipo PRIMERO
                break;
            default:
                reporteResult = reporteService.generarReporteProductos(tipo, null);  // ← tipo PRIMERO
                break;
        }

        return ResponseEntity.ok(reporteResult);
    }

    // 3. METODO PARA OPCIONES DE FILTRO
    @GetMapping("/api/opciones-filtro")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> obtenerOpcionesFiltro() {
        return ResponseEntity.ok(reporteService.obtenerOpcionesFiltro());
    }

    // 4. METODO PARA RESUMEN GENERAL
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

    // 5. METODO PARA EXPORTAR PDF (DESCARGAR)
    @GetMapping("/exportar/pdf")
    public ResponseEntity<byte[]> exportarReportePdf(
            @RequestParam(required = false) Boolean estado,
            @RequestParam(required = false, defaultValue = "Detallado") String tipo) {

        try {
            // Obtener productos según filtro
            List<ProductoModel> productos;
            if (estado != null) {
                productos = productoRepository.findByEstado(estado);
            } else {
                productos = productoRepository.findAll();
            }

            // Calcular estadísticas usando el servicio
            Map<String, Object> estadisticas = reporteService.calcularEstadisticas(productos);

            // Generar PDF
            byte[] pdfBytes = pdfExportService.generarPdfReporteProductos(
                    productos,
                    estadisticas,
                    estado != null ? (estado ? "ACTIVOS" : "INACTIVOS") : "TODOS",
                    tipo
            );

            // Configurar respuesta
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDisposition(ContentDisposition.attachment()
                    .filename("reporte-productos-" + new Date().getTime() + ".pdf").build());
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    // 6. METODO PARA PREVISUALIZAR PDF (VER EN NAVEGADOR)
    @GetMapping("/ver/pdf")
    public ResponseEntity<byte[]> verReportePdf(
            @RequestParam(required = false) Boolean estado) {

        try {
            List<ProductoModel> productos;
            if (estado != null) {
                productos = productoRepository.findByEstado(estado);
            } else {
                productos = productoRepository.findAll();
            }

            Map<String, Object> estadisticas = reporteService.calcularEstadisticas(productos);

            byte[] pdfBytes = pdfExportService.generarPdfReporteProductos(
                    productos, estadisticas,
                    estado != null ? (estado ? "ACTIVOS" : "INACTIVOS") : "TODOS",
                    "Detallado"
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDisposition(ContentDisposition.inline()
                    .filename("reporte-productos.pdf").build());

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // 7. MeTODO PARA CONTAR PRODUCTOS POR ESTADO
    @GetMapping("/api/contar-estados")
    @ResponseBody
    public ResponseEntity<Map<String, Long>> contarProductosPorEstado() {
        return ResponseEntity.ok(reporteService.contarProductosPorEstado());
    }


    // 8. MeTODO SIMPLE PARA PROBAR PDF (ELIMINA ERRORES)
    @GetMapping("/test-pdf")
    public void testPdfSimple(HttpServletResponse response) throws IOException {

        // CONFIGURACIÓN OBLIGATORIA
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=test.pdf");

        try {
            // 1. Crear documento
            Document documento = new Document();

            // 2. Obtener OutputStream ANTES de crear PdfWriter
            PdfWriter writer = PdfWriter.getInstance(documento, response.getOutputStream());

            // 3. Abrir documento
            documento.open();

            // 4. CONTENIDO SIMPLE
            documento.add(new Paragraph("=== REPORTE DE PRUEBA ==="));
            documento.add(new Paragraph("Generado el: " + new java.util.Date()));
            documento.add(new Paragraph(" "));
            documento.add(new Paragraph("Este es un PDF de prueba para verificar que iText funciona."));
            documento.add(new Paragraph(" "));
            documento.add(new Paragraph("Total de productos: " + productoRepository.count()));

            // 5. Cerrar en orden CORRECTO
            documento.close();
            writer.close(); // IMPORTANTE: cerrar writer también

        } catch (Exception e) {
            e.printStackTrace();
            // Enviar error como texto para debug
            response.setContentType("text/plain");
            response.getWriter().println("ERROR AL GENERAR PDF: " + e.getMessage());
            e.printStackTrace(response.getWriter());
        }
    }
    // Metodo de diagnóstico
    @GetMapping("/diagnostico")
    @ResponseBody
    public String diagnostico() {
        ventasReporteService.diagnosticarDatos();
        return "Diagnóstico ejecutado. Revisa la consola de Spring Boot.";
    }
}