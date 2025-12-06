package scrum.cannia.service;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;
import scrum.cannia.model.ProductoModel;
import com.itextpdf.text.FontFactory;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;

@Service
public class PdfExportService {

    public byte[] generarPdfReporteProductos(List<ProductoModel> productos,
                                             Map<String, Object> estadisticas,
                                             String filtro,
                                             String tipo) throws DocumentException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document();
        PdfWriter.getInstance(document, baos);

        document.open();

        // ====================
        // 1. TÍTULO PRINCIPAL
        // ====================
        document.add(new Paragraph("REPORTE ESTADÍSTICO - CANNIA",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18)));
        document.add(new Paragraph(" "));

        // ====================
        // 2. INFORMACIÓN GENERAL
        // ====================
        document.add(new Paragraph("INFORMACIÓN DEL REPORTE",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14)));
        document.add(new Paragraph("Fecha de generación: " + new java.util.Date()));
        document.add(new Paragraph("Filtro aplicado: " + filtro));
        document.add(new Paragraph("Tipo de reporte: " + tipo));
        document.add(new Paragraph(" "));

        // ====================
        // 3. RESUMEN EJECUTIVO
        // ====================
        document.add(new Paragraph("RESUMEN EJECUTIVO",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14)));
        document.add(new Paragraph(" "));

        document.add(new Paragraph("• Total de productos analizados: " + productos.size()));

        // Estadísticas de estado
        long activos = productos.stream().filter(ProductoModel::isEstado).count();
        long inactivos = productos.size() - activos;
        double porcentajeActivos = productos.size() > 0 ? (activos * 100.0 / productos.size()) : 0;

        document.add(new Paragraph("• Productos activos: " + activos +
                " (" + String.format("%.1f", porcentajeActivos) + "%)"));
        document.add(new Paragraph("• Productos inactivos: " + inactivos +
                " (" + String.format("%.1f", 100 - porcentajeActivos) + "%)"));
        document.add(new Paragraph(" "));

        // ====================
        // 4. DISTRIBUCIÓN POR ESTADO
        // ====================
        document.add(new Paragraph("DISTRIBUCIÓN POR ESTADO",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14)));
        document.add(new Paragraph(" "));

        if (activos > 0) {
            document.add(new Paragraph("✓ " + activos + " productos están ACTIVOS y disponibles para venta."));
        }
        if (inactivos > 0) {
            document.add(new Paragraph("✗ " + inactivos + " productos están INACTIVOS y no disponibles."));
        }
        document.add(new Paragraph(" "));

        // ====================
        // 5. RECOMENDACIONES
        // ====================
        document.add(new Paragraph("RECOMENDACIONES",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14)));
        document.add(new Paragraph(" "));

        if (inactivos > productos.size() * 0.3) {
            document.add(new Paragraph("• Se recomienda revisar el alto número de productos inactivos (" +
                    inactivos + "). Considerar reactivación o eliminación."));
        }

        if (productos.size() < 10) {
            document.add(new Paragraph("• El inventario es limitado (" + productos.size() +
                    " productos). Considerar ampliar la variedad."));
        } else if (productos.size() > 50) {
            document.add(new Paragraph("• Inventario amplio (" + productos.size() +
                    " productos). Buen nivel de variedad."));
        }

        document.add(new Paragraph("• Mantener al menos " +
                String.format("%.0f", productos.size() * 0.7) +
                " productos activos para óptimo funcionamiento."));
        document.add(new Paragraph(" "));

        // ====================
        // 6. CONCLUSIONES
        // ====================
        document.add(new Paragraph("CONCLUSIONES",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14)));
        document.add(new Paragraph(" "));

        document.add(new Paragraph("1. El inventario cuenta con " + productos.size() + " productos registrados."));
        document.add(new Paragraph("2. " + String.format("%.1f", porcentajeActivos) +
                "% del inventario está disponible para venta."));

        if (porcentajeActivos > 80) {
            document.add(new Paragraph("3. Excelente nivel de disponibilidad del inventario."));
        } else if (porcentajeActivos > 60) {
            document.add(new Paragraph("3. Nivel aceptable de disponibilidad."));
        } else {
            document.add(new Paragraph("3. Se requiere atención a la disponibilidad del inventario."));
        }

        document.add(new Paragraph(" "));

        // ====================
        // 7. FIRMA Y VALIDEZ
        // ====================
        document.add(new Paragraph("_________________________"));
        document.add(new Paragraph("Generado automáticamente"));
        document.add(new Paragraph("Sistema Cannia - " + new java.util.Date()));

        document.close();
        return baos.toByteArray();
    }
}