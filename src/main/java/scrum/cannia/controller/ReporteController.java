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
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletResponse;
import scrum.cannia.service.VentasReporteService;

import java.io.ByteArrayOutputStream;
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
    public ResponseEntity<Map<String, Object>> generarReporte(
            @RequestParam String tipo,
            @RequestParam(required = false, defaultValue = "productos") String reporte) {

        Map<String, Object> reporteResult;

        switch (reporte) {
            case "ventas-categoria":
                reporteResult = ventasReporteService.generarVentasPorCategoria();
                break;
            case "ventas-dia":
                reporteResult = ventasReporteService.generarVentasDelDia();
                break;
            case "activos":
                reporteResult = reporteService.generarReporteProductos(tipo, true);
                break;
            case "inactivos":
                reporteResult = reporteService.generarReporteProductos(tipo, false);
                break;
            default:
                reporteResult = reporteService.generarReporteProductos(tipo, null);
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

    // 5. METODO PARA EXPORTAR PDF (DESCARGAR) - MODIFICAR ASÍ:
    @GetMapping("/exportar/pdf")
    public void exportarPDF(@RequestParam(required = false, defaultValue = "productos") String reporte,
                            @RequestParam(required = false) String estado,
                            @RequestParam(required = false, defaultValue = "BARRAS") String tipoGrafico,
                            HttpServletResponse response) throws Exception {

        response.setContentType("application/pdf");
        String filename = "reporte-" + reporte + "-" + new Date().getTime() + ".pdf";
        response.setHeader("Content-Disposition", "attachment; filename=" + filename);

        byte[] pdfBytes; // <-- DECLARARLO AQUÍ UNA SOLA VEZ

        switch (reporte) {
            case "ventas-categoria":
                pdfBytes = pdfExportService.generarPdfVentasPorCategoriaBytes();
                break;
            case "ventas-dia":
                pdfBytes = pdfExportService.generarPdfVentasDelDiaBytes();
                break;
            default:
                // Para reportes de productos
                Boolean estadoBoolean = null;
                if (estado != null && !estado.isEmpty()) {
                    estadoBoolean = Boolean.parseBoolean(estado);
                }

                List<ProductoModel> productos;
                if (estadoBoolean != null) {
                    productos = productoRepository.findByEstado(estadoBoolean);
                } else {
                    productos = productoRepository.findAll();
                }

                Map<String, Object> estadisticas = reporteService.calcularEstadisticas(productos);
                String tituloEstado = "";
                if (estadoBoolean != null) {
                    tituloEstado = estadoBoolean ? "ACTIVOS" : "INACTIVOS";
                } else {
                    tituloEstado = "TODOS LOS PRODUCTOS";
                }

                // Usar el método que genera ambas gráficas
                pdfBytes = generarPdfCompletoBytes(productos, estadisticas, tituloEstado, tipoGrafico);
                break;
        }

        // Escribir bytes en la respuesta
        response.getOutputStream().write(pdfBytes);
        response.getOutputStream().flush();
    }
    // 6. METODO PARA PREVISUALIZAR PDF (VER EN NAVEGADOR)
    @GetMapping("/ver/pdf")
    public ResponseEntity<byte[]> verReportePdf(
            @RequestParam(required = false, defaultValue = "productos") String reporte,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false, defaultValue = "BARRAS") String tipoGrafico) throws Exception {

        try {
            byte[] pdfBytes; // <-- DECLARARLO AQUÍ

            switch (reporte) {
                case "ventas-categoria":
                    pdfBytes = pdfExportService.generarPdfVentasPorCategoriaBytes();
                    break;
                case "ventas-dia":
                    pdfBytes = pdfExportService.generarPdfVentasDelDiaBytes();
                    break;
                default:
                    // Para reportes de productos
                    List<ProductoModel> productos;
                    Boolean estadoBoolean = null;

                    if (estado != null) {
                        estadoBoolean = Boolean.parseBoolean(estado);
                    }

                    if (estadoBoolean != null) {
                        productos = productoRepository.findByEstado(estadoBoolean);
                    } else {
                        productos = productoRepository.findAll();
                    }

                    Map<String, Object> estadisticas = reporteService.calcularEstadisticas(productos);
                    String tituloEstado = "";
                    if (estadoBoolean != null) {
                        tituloEstado = estadoBoolean ? "ACTIVOS" : "INACTIVOS";
                    } else {
                        tituloEstado = "TODOS LOS PRODUCTOS";
                    }

                    // Usar el método que genera ambas gráficas
                    pdfBytes = generarPdfCompletoBytes(productos, estadisticas, tituloEstado, tipoGrafico);
                    break;
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDisposition(ContentDisposition.inline()
                    .filename("reporte-" + reporte + ".pdf").build());

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    // 7. METODO PARA CONTAR PRODUCTOS POR ESTADO
    @GetMapping("/api/contar-estados")
    @ResponseBody
    public ResponseEntity<Map<String, Long>> contarProductosPorEstado() {
        return ResponseEntity.ok(reporteService.contarProductosPorEstado());
    }

    // 8. METODO SIMPLE PARA PROBAR PDF
    @GetMapping("/test-pdf")
    public void testPdfSimple(HttpServletResponse response) throws IOException {

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=test.pdf");

        try {
            Document documento = new Document();
            PdfWriter writer = PdfWriter.getInstance(documento, response.getOutputStream());
            documento.open();

            documento.add(new Paragraph("=== REPORTE DE PRUEBA ==="));
            documento.add(new Paragraph("Generado el: " + new java.util.Date()));
            documento.add(new Paragraph(" "));
            documento.add(new Paragraph("Este es un PDF de prueba para verificar que iText funciona."));
            documento.add(new Paragraph(" "));
            documento.add(new Paragraph("Total de productos: " + productoRepository.count()));

            documento.close();
            writer.close();

        } catch (Exception e) {
            e.printStackTrace();
            response.setContentType("text/plain");
            response.getWriter().println("ERROR AL GENERAR PDF: " + e.getMessage());
            e.printStackTrace(response.getWriter());
        }
    }

    // 9. METODO DE DIAGNÓSTICO
    @GetMapping("/diagnostico")
    @ResponseBody
    public String diagnostico() {
        ventasReporteService.diagnosticarDatos();
        return "Diagnóstico ejecutado. Revisa la consola de Spring Boot.";
    }

    // 10. MÉTODO AUXILIAR PARA GENERAR PDF COMPLETO EN BYTES
    private byte[] generarPdfCompletoBytes(List<ProductoModel> productos,
                                           Map<String, Object> estadisticas,
                                           String tituloReporte,
                                           String tipoGrafico) throws Exception {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4.rotate());
        PdfWriter writer = PdfWriter.getInstance(document, baos);
        document.open();

        // Título
        Paragraph titulo = new Paragraph("REPORTE DE PRODUCTOS - " + tituloReporte,
                com.itextpdf.text.FontFactory.getFont(com.itextpdf.text.FontFactory.HELVETICA_BOLD, 18));
        titulo.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
        titulo.setSpacingAfter(20);
        document.add(titulo);

        document.add(new Paragraph("Fecha: " + new java.util.Date()));
        document.add(new Paragraph("Total productos analizados: " + productos.size()));
        document.add(com.itextpdf.text.Chunk.NEWLINE);

        // Calcular activos e inactivos
        long activos = productos.stream().filter(ProductoModel::isEstado).count();
        long inactivos = productos.size() - activos;

        // Gráfica de pastel
        Paragraph subtituloPastel = new Paragraph("1. GRÁFICA DE PASTEL",
                com.itextpdf.text.FontFactory.getFont(com.itextpdf.text.FontFactory.HELVETICA_BOLD, 14));
        subtituloPastel.setSpacingBefore(15);
        document.add(subtituloPastel);

        try {
            byte[] imagenPastel = pdfExportService.getGraficaService().generarGraficaPastel(activos, inactivos);
            com.itextpdf.text.Image graficaPastel = com.itextpdf.text.Image.getInstance(imagenPastel);
            graficaPastel.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            graficaPastel.scaleToFit(400, 250);
            document.add(graficaPastel);
        } catch (Exception e) {
            document.add(new Paragraph("No se pudo generar gráfica de pastel"));
        }

        document.add(com.itextpdf.text.Chunk.NEWLINE);

        // Gráfica de barras
        Paragraph subtituloBarras = new Paragraph("2. GRÁFICA DE BARRAS",
                com.itextpdf.text.FontFactory.getFont(com.itextpdf.text.FontFactory.HELVETICA_BOLD, 14));
        subtituloBarras.setSpacingBefore(15);
        document.add(subtituloBarras);

        try {
            byte[] imagenBarras = pdfExportService.getGraficaService().generarGraficaBarras(activos, inactivos);
            com.itextpdf.text.Image graficaBarras = com.itextpdf.text.Image.getInstance(imagenBarras);
            graficaBarras.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            graficaBarras.scaleToFit(400, 250);
            document.add(graficaBarras);
        } catch (Exception e) {
            document.add(new Paragraph("No se pudo generar gráfica de barras"));
        }

        document.add(com.itextpdf.text.Chunk.NEWLINE);

        // Tabla resumen
        Paragraph subtituloTabla = new Paragraph("3. TABLA RESUMEN",
                com.itextpdf.text.FontFactory.getFont(com.itextpdf.text.FontFactory.HELVETICA_BOLD, 14));
        subtituloTabla.setSpacingBefore(15);
        document.add(subtituloTabla);

        com.itextpdf.text.pdf.PdfPTable tabla = new com.itextpdf.text.pdf.PdfPTable(3);
        tabla.setWidthPercentage(80);
        tabla.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_CENTER);

        // Encabezados
        tabla.addCell(crearCelda("ESTADO", true));
        tabla.addCell(crearCelda("CANTIDAD", true));
        tabla.addCell(crearCelda("PORCENTAJE", true));

        // Datos
        double porcentajeActivos = productos.size() > 0 ? (activos * 100.0 / productos.size()) : 0;
        double porcentajeInactivos = productos.size() > 0 ? (inactivos * 100.0 / productos.size()) : 0;

        tabla.addCell(crearCelda("ACTIVOS", false));
        tabla.addCell(crearCelda(String.valueOf(activos), false));
        tabla.addCell(crearCelda(String.format("%.1f%%", porcentajeActivos), false));

        tabla.addCell(crearCelda("INACTIVOS", false));
        tabla.addCell(crearCelda(String.valueOf(inactivos), false));
        tabla.addCell(crearCelda(String.format("%.1f%%", porcentajeInactivos), false));

        tabla.addCell(crearCelda("TOTAL", true));
        tabla.addCell(crearCelda(String.valueOf(productos.size()), true));
        tabla.addCell(crearCelda("100%", true));

        document.add(tabla);
        document.add(com.itextpdf.text.Chunk.NEWLINE);

        document.add(new Paragraph("--- Fin del Reporte ---",
                com.itextpdf.text.FontFactory.getFont(com.itextpdf.text.FontFactory.HELVETICA_OBLIQUE, 10)));

        document.close();
        return baos.toByteArray();
    }

    // Método auxiliar para crear celdas
    private com.itextpdf.text.pdf.PdfPCell crearCelda(String texto, boolean esEncabezado) {
        com.itextpdf.text.pdf.PdfPCell celda = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Phrase(texto));
        celda.setPadding(5);
        celda.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_CENTER);

        if (esEncabezado) {
            celda.setBackgroundColor(new com.itextpdf.text.BaseColor(220, 220, 220));
            celda.setPhrase(new com.itextpdf.text.Phrase(texto,
                    com.itextpdf.text.FontFactory.getFont(com.itextpdf.text.FontFactory.HELVETICA_BOLD, 10)));
        }

        return celda;
    }
}