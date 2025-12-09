package scrum.cannia.service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import scrum.cannia.model.ProductoModel;

import com.itextpdf.text.BaseColor;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;

@Service
public class PdfExportService {

    @Autowired
    private GraficaService graficaService;

    public byte[] generarPdfReporteProductos(List<ProductoModel> productos,
                                             Map<String, Object> estadisticas,
                                             String filtro,
                                             String tipo) throws Exception {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, baos);

        document.open();

        // ============ ENCABEZADO ============
        Paragraph titulo = new Paragraph("REPORTE ESTADÍSTICO VISUAL",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18));
        titulo.setAlignment(Element.ALIGN_CENTER);
        titulo.setSpacingAfter(20);
        document.add(titulo);

        document.add(new Paragraph("Fecha: " + new java.util.Date()));
        document.add(new Paragraph("Total productos analizados: " + productos.size()));
        document.add(Chunk.NEWLINE);

        // ============ CÁLCULOS ============
        long activos = productos.stream().filter(ProductoModel::isEstado).count();
        long inactivos = productos.size() - activos;

        // ============ GRÁFICA DE PASTEL ============
        Paragraph subtituloPastel = new Paragraph("1. GRÁFICA DE PASTEL",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14));
        subtituloPastel.setSpacingBefore(15);
        document.add(subtituloPastel);

        // Generar y agregar gráfica de pastel
        byte[] imagenPastel = graficaService.generarGraficaPastel(activos, inactivos);
        Image graficaPastel = Image.getInstance(imagenPastel);
        graficaPastel.setAlignment(Element.ALIGN_CENTER);
        graficaPastel.scaleToFit(400, 250);
        document.add(graficaPastel);
        document.add(Chunk.NEWLINE);

        // ============ GRÁFICA DE BARRAS ============
        Paragraph subtituloBarras = new Paragraph("2. GRÁFICA DE BARRAS",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14));
        subtituloBarras.setSpacingBefore(15);
        document.add(subtituloBarras);

        // Generar y agregar gráfica de barras
        byte[] imagenBarras = graficaService.generarGraficaBarras(activos, inactivos);
        Image graficaBarras = Image.getInstance(imagenBarras);
        graficaBarras.setAlignment(Element.ALIGN_CENTER);
        graficaBarras.scaleToFit(400, 250);
        document.add(graficaBarras);
        document.add(Chunk.NEWLINE);

        // ============ TABLA DE DATOS ============
        Paragraph subtituloTabla = new Paragraph("3. TABLA RESUMEN",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14));
        subtituloTabla.setSpacingBefore(15);
        document.add(subtituloTabla);

        PdfPTable tabla = new PdfPTable(3);
        tabla.setWidthPercentage(80);
        tabla.setHorizontalAlignment(Element.ALIGN_CENTER);

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
        document.add(Chunk.NEWLINE);

        // ============ INTERPRETACIÓN ============
        Paragraph interpretacion = new Paragraph("4. INTERPRETACIÓN",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14));
        interpretacion.setSpacingBefore(15);
        document.add(interpretacion);

        if (activos == productos.size()) {
            document.add(new Paragraph("✓ EXCELENTE: 100% de productos activos"));
        } else if (porcentajeActivos >= 70) {
            document.add(new Paragraph("✓ BUENO: " + String.format("%.1f", porcentajeActivos) +
                    "% de productos disponibles"));
        } else if (porcentajeActivos >= 50) {
            document.add(new Paragraph("○ REGULAR: " + String.format("%.1f", porcentajeActivos) +
                    "% de disponibilidad"));
        } else {
            document.add(new Paragraph("⚠ REQUIERE ATENCIÓN: Solo " +
                    String.format("%.1f", porcentajeActivos) + "% activos"));
        }

        document.add(Chunk.NEWLINE);
        document.add(new Paragraph("--- Fin del Reporte ---",
                FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10)));

        document.close();
        return baos.toByteArray();
    }

    private PdfPCell crearCelda(String texto, boolean esEncabezado) {
        PdfPCell celda = new PdfPCell(new Phrase(texto));
        celda.setPadding(5);
        celda.setHorizontalAlignment(Element.ALIGN_CENTER);

        if (esEncabezado) {
            celda.setBackgroundColor(new BaseColor(220, 220, 220));
            celda.setPhrase(new Phrase(texto,
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10)));
        }

        return celda;
    }
}