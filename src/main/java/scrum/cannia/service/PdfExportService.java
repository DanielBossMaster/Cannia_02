package scrum.cannia.service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import scrum.cannia.model.ProductoModel;

import com.itextpdf.text.BaseColor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;
import java.util.List;

@Service
public class PdfExportService {

    @Autowired
    private GraficaService graficaService;

    @Autowired
    private ReporteService reporteService;

    @Autowired
    private VentasReporteService ventasReporteService;

    // Metodo para generar PDF de productos (bytes)
    public byte[] generarPdfReporteProductos(List<ProductoModel> productos,
                                             Map<String, Object> estadisticas,
                                             String filtro,
                                             String tipo) throws Exception {
        return generarPdfReporteProductosBytes(productos, estadisticas, filtro, tipo);
    }

    // Metodo para generar PDF de productos coresponse
    public void generarPdfReporteProductos(List<ProductoModel> productos,
                                           Map<String, Object> estadisticas,
                                           String tipoReporte,
                                           String formato,
                                           HttpServletResponse response) throws Exception {

        response.setContentType("application/pdf");
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();

        agregarContenidoReporteProductos(document, productos, estadisticas, tipoReporte);

        document.close();
    }

    // Metodo para generar PDF de productos (bytes)
    public byte[] generarPdfReporteProductosBytes(List<ProductoModel> productos,
                                                  Map<String, Object> estadisticas,
                                                  String tipoReporte,
                                                  String formato) throws Exception {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, baos);
        document.open();

        agregarContenidoReporteProductos(document, productos, estadisticas, tipoReporte);

        document.close();
        return baos.toByteArray();
    }

    // Metodo privado para agregar contenido común de productos
    private void agregarContenidoReporteProductos(Document document,
                                                  List<ProductoModel> productos,
                                                  Map<String, Object> estadisticas,
                                                  String tipoReporte) throws Exception {

        // ============ ENCABEZADO ============
        Paragraph titulo = new Paragraph("REPORTE DE PRODUCTOS - " + tipoReporte,
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
        Paragraph subtituloPastel = new Paragraph("1. DISTRIBUCIÓN POR ESTADO",
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
        Paragraph subtituloBarras = new Paragraph("2. COMPARATIVA DE ESTADOS",
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

        // ============ TABLA RESUMEN ============
        Paragraph subtituloTabla = new Paragraph("3. TABLA RESUMEN",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14));
        subtituloTabla.setSpacingBefore(15);
        document.add(subtituloTabla);

        PdfPTable tablaResumen = new PdfPTable(3);
        tablaResumen.setWidthPercentage(80);
        tablaResumen.setHorizontalAlignment(Element.ALIGN_CENTER);

        // Encabezados
        tablaResumen.addCell(crearCelda("ESTADO", true));
        tablaResumen.addCell(crearCelda("CANTIDAD", true));
        tablaResumen.addCell(crearCelda("PORCENTAJE", true));

        // Datos
        double porcentajeActivos = productos.size() > 0 ? (activos * 100.0 / productos.size()) : 0;
        double porcentajeInactivos = productos.size() > 0 ? (inactivos * 100.0 / productos.size()) : 0;

        tablaResumen.addCell(crearCelda("ACTIVOS", false));
        tablaResumen.addCell(crearCelda(String.valueOf(activos), false));
        tablaResumen.addCell(crearCelda(String.format("%.1f%%", porcentajeActivos), false));

        tablaResumen.addCell(crearCelda("INACTIVOS", false));
        tablaResumen.addCell(crearCelda(String.valueOf(inactivos), false));
        tablaResumen.addCell(crearCelda(String.format("%.1f%%", porcentajeInactivos), false));

        tablaResumen.addCell(crearCelda("TOTAL", true));
        tablaResumen.addCell(crearCelda(String.valueOf(productos.size()), true));
        tablaResumen.addCell(crearCelda("100%", true));

        document.add(tablaResumen);
        document.add(Chunk.NEWLINE);

        // ============ TABLA DETALLADA DE PRODUCTOS ============
        if (productos.size() <= 50) {
            Paragraph subtituloDetalle = new Paragraph("4. DETALLE DE PRODUCTOS",
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14));
            subtituloDetalle.setSpacingBefore(15);
            document.add(subtituloDetalle);

            PdfPTable tablaDetalle = new PdfPTable(5);
            tablaDetalle.setWidthPercentage(100);

            // Encabezados de tabla detallada
            String[] headers = {"PRODUCTO", "CANTIDAD", "PRECIO", "VALOR TOTAL", "ESTADO"};
            for (String header : headers) {
                tablaDetalle.addCell(crearCelda(header, true));
            }

            // Datos de productos
            for (ProductoModel producto : productos) {
                tablaDetalle.addCell(crearCelda(producto.getNombre(), false));
                tablaDetalle.addCell(crearCelda(String.valueOf(producto.getCantidad()), false));
                tablaDetalle.addCell(crearCelda("$" + producto.getValor(), false));

                double valorTotal = producto.getCantidad() * producto.getValor();
                tablaDetalle.addCell(crearCelda("$" + String.format("%.2f", valorTotal), false));

                String estado = producto.isEstado() ? "ACTIVO" : "INACTIVO";
                tablaDetalle.addCell(crearCelda(estado, false));
            }

            document.add(tablaDetalle);
            document.add(Chunk.NEWLINE);
        }

        // ============ ESTADÍSTICAS FINANCIERAS ============
        if (estadisticas != null && !estadisticas.isEmpty()) {
            Paragraph subtituloEstadisticas = new Paragraph("5. ESTADÍSTICAS FINANCIERAS",
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14));
            subtituloEstadisticas.setSpacingBefore(15);
            document.add(subtituloEstadisticas);

            PdfPTable tablaEstadisticas = new PdfPTable(2);
            tablaEstadisticas.setWidthPercentage(60);
            tablaEstadisticas.setHorizontalAlignment(Element.ALIGN_CENTER);

            agregarFilaEstadistica(tablaEstadisticas, "Valor Total Inventario",
                    "$" + estadisticas.getOrDefault("valorTotalInventario", "0"));
            agregarFilaEstadistica(tablaEstadisticas, "Productos Activos",
                    String.valueOf(estadisticas.getOrDefault("totalActivos", "0")));
            agregarFilaEstadistica(tablaEstadisticas, "Productos Inactivos",
                    String.valueOf(estadisticas.getOrDefault("totalInactivos", "0")));
            agregarFilaEstadistica(tablaEstadisticas, "Cantidad Total Stock",
                    String.valueOf(estadisticas.getOrDefault("cantidadTotalStock", "0")) + " unidades");

            document.add(tablaEstadisticas);
            document.add(Chunk.NEWLINE);
        }

        // ============ INTERPRETACIÓN ============
        Paragraph interpretacion = new Paragraph("6. INTERPRETACIÓN",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14));
        interpretacion.setSpacingBefore(15);
        document.add(interpretacion);

        String interpretacionTexto = generarInterpretacion(activos, productos.size());
        document.add(new Paragraph(interpretacionTexto));

        document.add(Chunk.NEWLINE);
        document.add(new Paragraph("--- Fin del Reporte ---",
                FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10)));
    }

    // Metodo para generar PDF de ventas por categoría
    public void generarPdfVentasPorCategoria(HttpServletResponse response) throws Exception {
        response.setContentType("application/pdf");

        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();

        // Título
        Paragraph titulo = new Paragraph("REPORTE DE VENTAS POR CATEGORÍA",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18));
        titulo.setAlignment(Element.ALIGN_CENTER);
        titulo.setSpacingAfter(20);
        document.add(titulo);

        // Fecha
        document.add(new Paragraph("Fecha: " + new java.util.Date()));
        document.add(Chunk.NEWLINE);

        // Obtener datos
        Map<String, Object> reporteVentas = ventasReporteService.generarVentasPorCategoria();
        List<Map<String, Object>> ventas = (List<Map<String, Object>>) reporteVentas.get("datos");

        if (ventas == null || ventas.isEmpty()) {
            document.add(new Paragraph("No hay datos de ventas disponibles."));
        } else {
            // Crear tabla
            PdfPTable tabla = new PdfPTable(3);
            tabla.setWidthPercentage(100);

            // Encabezados
            tabla.addCell(crearCelda("CATEGORÍA", true));
            tabla.addCell(crearCelda("CANTIDAD", true));
            tabla.addCell(crearCelda("TOTAL ($)", true));

            // Datos
            double totalGeneral = 0;
            for (Map<String, Object> venta : ventas) {
                String categoria = (String) venta.get("categoria");
                Object cantidadObj = venta.get("cantidad");
                Object totalObj = venta.get("total");

                int cantidad = 0;
                if (cantidadObj instanceof Integer) {
                    cantidad = (Integer) cantidadObj;
                } else if (cantidadObj instanceof Long) {
                    cantidad = ((Long) cantidadObj).intValue();
                }

                double total = 0.0;
                if (totalObj instanceof Double) {
                    total = (Double) totalObj;
                } else if (totalObj instanceof Integer) {
                    total = ((Integer) totalObj).doubleValue();
                }

                tabla.addCell(crearCelda(categoria != null ? categoria : "Sin categoría", false));
                tabla.addCell(crearCelda(String.valueOf(cantidad), false));
                tabla.addCell(crearCelda("$" + String.format("%,.2f", total), false));

                totalGeneral += total;
            }

            document.add(tabla);
            document.add(Chunk.NEWLINE);

            // Total general
            Paragraph total = new Paragraph("TOTAL GENERAL: $" + String.format("%,.2f", totalGeneral),
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14));
            total.setAlignment(Element.ALIGN_RIGHT);
            document.add(total);
        }

        document.add(Chunk.NEWLINE);
        document.add(new Paragraph("--- Fin del Reporte ---",
                FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10)));

        document.close();
    }

    // Metodo para generar PDF de ventas por categoría (bytes)
    public byte[] generarPdfVentasPorCategoriaBytes() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, baos);
        document.open();

        // Título
        Paragraph titulo = new Paragraph("REPORTE DE VENTAS POR CATEGORÍA",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18));
        titulo.setAlignment(Element.ALIGN_CENTER);
        titulo.setSpacingAfter(20);
        document.add(titulo);

        // Fecha
        document.add(new Paragraph("Fecha: " + new java.util.Date()));
        document.add(new Paragraph("Este reporte está en desarrollo."));
        document.add(new Paragraph("Próximamente mostrará datos reales."));

        document.close();
        return baos.toByteArray();
    }

    // Metodo para generar PDF de ventas del día
    public void generarPdfVentasDelDia(HttpServletResponse response) throws Exception {
        response.setContentType("application/pdf");

        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();

        // Título
        Paragraph titulo = new Paragraph("REPORTE DE VENTAS DEL DÍA",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18));
        titulo.setAlignment(Element.ALIGN_CENTER);
        titulo.setSpacingAfter(20);
        document.add(titulo);

        // Fecha
        document.add(new Paragraph("Fecha del reporte: " + new java.util.Date()));
        document.add(Chunk.NEWLINE);

        // Obtener datos
        Map<String, Object> reporteVentas = ventasReporteService.generarVentasDelDia();
        List<Map<String, Object>> ventas = (List<Map<String, Object>>) reporteVentas.get("datos");

        if (ventas == null || ventas.isEmpty()) {
            document.add(new Paragraph("No hay ventas registradas para el día de hoy."));
        } else {
            // Crear tabla
            PdfPTable tabla = new PdfPTable(3);
            tabla.setWidthPercentage(100);

            // Encabezados
            tabla.addCell(crearCelda("HORA", true));
            tabla.addCell(crearCelda("CANTIDAD", true));
            tabla.addCell(crearCelda("TOTAL ($)", true));

            // Datos
            double totalDia = 0;
            for (Map<String, Object> venta : ventas) {
                String hora = (String) venta.get("hora");
                Object cantidadObj = venta.get("cantidad");
                Object totalObj = venta.get("total");

                int cantidad = 0;
                if (cantidadObj instanceof Integer) {
                    cantidad = (Integer) cantidadObj;
                } else if (cantidadObj instanceof Long) {
                    cantidad = ((Long) cantidadObj).intValue();
                }

                double total = 0.0;
                if (totalObj instanceof Double) {
                    total = (Double) totalObj;
                } else if (totalObj instanceof Integer) {
                    total = ((Integer) totalObj).doubleValue();
                }

                tabla.addCell(crearCelda(hora != null ? hora : "N/A", false));
                tabla.addCell(crearCelda(String.valueOf(cantidad), false));
                tabla.addCell(crearCelda("$" + String.format("%,.2f", total), false));

                totalDia += total;
            }

            document.add(tabla);
            document.add(Chunk.NEWLINE);

            // Total del día
            Paragraph total = new Paragraph("TOTAL DEL DÍA: $" + String.format("%,.2f", totalDia),
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14));
            total.setAlignment(Element.ALIGN_RIGHT);
            document.add(total);
        }

        document.add(Chunk.NEWLINE);
        document.add(new Paragraph("--- Fin del Reporte Diario ---",
                FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10)));

        document.close();
    }

    // Metodo para generar PDF de ventas del día (bytes)
    public byte[] generarPdfVentasDelDiaBytes() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, baos);
        document.open();

        // Título
        Paragraph titulo = new Paragraph("REPORTE DE VENTAS DEL DÍA",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18));
        titulo.setAlignment(Element.ALIGN_CENTER);
        titulo.setSpacingAfter(20);
        document.add(titulo);

        // Fecha
        document.add(new Paragraph("Fecha: " + new java.util.Date()));
        document.add(new Paragraph("Este reporte está en desarrollo."));

        document.close();
        return baos.toByteArray();
    }

    // ============ MetodoS AUXILIARES ============

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

    private void agregarFilaEstadistica(PdfPTable tabla, String titulo, String valor) {
        tabla.addCell(crearCelda(titulo, true));
        tabla.addCell(crearCelda(valor, false));
    }

    private String generarInterpretacion(long activos, long total) {
        double porcentajeActivos = total > 0 ? (activos * 100.0 / total) : 0;

        if (activos == total) {
            return "EXCELENTE: 100% de productos activos";
        } else if (porcentajeActivos >= 70) {
            return "BUENO: " + String.format("%.1f", porcentajeActivos) + "% de productos disponibles";
        } else if (porcentajeActivos >= 50) {
            return "REGULAR: " + String.format("%.1f", porcentajeActivos) + "% de disponibilidad";
        } else {
            return "REQUIERE ATENCIÓN: Solo " + String.format("%.1f", porcentajeActivos) + "% activos";
        }
    }

    // Metodo anterior (mantener para compatibilidad)
    public void exportarVentasPorCategoriaPDF(HttpServletResponse response) throws Exception {
        generarPdfVentasPorCategoria(response);
    }
}