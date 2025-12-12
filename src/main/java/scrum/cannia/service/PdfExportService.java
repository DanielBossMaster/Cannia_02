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
import java.util.*;

@Service
@SuppressWarnings("unchecked")
public class PdfExportService {

    @Autowired
    private GraficaService graficaService;

    @Autowired
    private ReporteService reporteService;

    @Autowired
    private VentasReporteService ventasReporteService;

    // ============ MÉTODOS PARA PRODUCTOS ============

    public byte[] generarPdfReporteProductos(java.util.List<ProductoModel> productos,
                                             Map<String, Object> estadisticas,
                                             String filtro,
                                             String tipo) throws Exception {
        return generarPdfReporteProductosBytes(productos, estadisticas, filtro, tipo);
    }

    public void generarPdfReporteProductos(java.util.List<ProductoModel> productos,
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

    public byte[] generarPdfReporteProductosBytes(java.util.List<ProductoModel> productos,
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

    // ============ MÉTODOS PARA VENTAS POR CATEGORÍA ============

    public void generarPdfVentasPorCategoria(HttpServletResponse response) throws Exception {
        response.setContentType("application/pdf");

        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();

        // Título PRINCIPAL
        Paragraph tituloPrincipal = new Paragraph("REPORTE DE VENTAS POR CATEGORÍA",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18));
        tituloPrincipal.setAlignment(Element.ALIGN_CENTER);
        tituloPrincipal.setSpacingAfter(10);
        document.add(tituloPrincipal);

        // ============ INFORMACIÓN DEL FILTRO ============
        Paragraph infoFiltro = new Paragraph("FILTROS APLICADOS:",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12));
        infoFiltro.setSpacingAfter(5);
        document.add(infoFiltro);

        // Crear tabla para filtros
        PdfPTable tablaFiltros = new PdfPTable(2);
        tablaFiltros.setWidthPercentage(80);
        tablaFiltros.setHorizontalAlignment(Element.ALIGN_LEFT);

        // Fila 1: Tipo de reporte
        tablaFiltros.addCell(crearCeldaFiltro("Tipo de Reporte:"));
        tablaFiltros.addCell(crearCeldaValor("Ventas por Categoría"));

        // Fila 2: Fecha
        tablaFiltros.addCell(crearCeldaFiltro("Fecha de generación:"));
        tablaFiltros.addCell(crearCeldaValor(new java.util.Date().toString()));

        // Fila 3: Período
        tablaFiltros.addCell(crearCeldaFiltro("Período analizado:"));
        tablaFiltros.addCell(crearCeldaValor("Últimos 30 días"));

        document.add(tablaFiltros);
        document.add(Chunk.NEWLINE);

        // ============ RESUMEN ESTADÍSTICO ============
        Paragraph subtituloEstadisticas = new Paragraph("RESUMEN ESTADÍSTICO",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14));
        subtituloEstadisticas.setSpacingBefore(10);
        document.add(subtituloEstadisticas);

        // Obtener datos y estadísticas
        Map<String, Object> reporteVentas = ventasReporteService.generarVentasPorCategoria();
        java.util.List<Map<String, Object>> ventas = (java.util.List<Map<String, Object>>) reporteVentas.get("datos");
        Map<String, Object> estadisticasVentas = (Map<String, Object>) reporteVentas.get("estadisticas");

        // ============ GRÁFICA DE VENTAS POR CATEGORÍA ============
        if (ventas != null && !ventas.isEmpty()) {
            Paragraph subtituloGrafica = new Paragraph("GRÁFICA - VENTAS POR CATEGORÍA",
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14));
            subtituloGrafica.setSpacingBefore(10);
            document.add(subtituloGrafica);

            try {
                byte[] imagenGrafica = graficaService.generarGraficaVentasPorCategoria(ventas);
                Image grafica = Image.getInstance(imagenGrafica);
                grafica.setAlignment(Element.ALIGN_CENTER);
                grafica.scaleToFit(500, 300);
                document.add(grafica);
                document.add(Chunk.NEWLINE);
            } catch (Exception e) {
                document.add(new Paragraph("No se pudo generar la gráfica: " + e.getMessage()));
            }
        }

        // Tabla de estadísticas
        PdfPTable tablaEstadisticas = new PdfPTable(2);
        tablaEstadisticas.setWidthPercentage(60);
        tablaEstadisticas.setHorizontalAlignment(Element.ALIGN_CENTER);

        if (estadisticasVentas != null && !estadisticasVentas.isEmpty()) {
            agregarFilaEstadisticaPDF(tablaEstadisticas, "Total Categorías",
                    String.valueOf(estadisticasVentas.get("totalCategorias")));
            agregarFilaEstadisticaPDF(tablaEstadisticas, "Total Ventas",
                    "$" + String.format("%,.2f", estadisticasVentas.get("totalVentas")));
            agregarFilaEstadisticaPDF(tablaEstadisticas, "Venta Máxima",
                    "$" + String.format("%,.2f", estadisticasVentas.get("ventaMaxima")) +
                            " (" + estadisticasVentas.get("categoriaMaxima") + ")");
            agregarFilaEstadisticaPDF(tablaEstadisticas, "Venta Mínima",
                    "$" + String.format("%,.2f", estadisticasVentas.get("ventaMinima")) +
                            " (" + estadisticasVentas.get("categoriaMinima") + ")");
        }

        document.add(tablaEstadisticas);
        document.add(Chunk.NEWLINE);

        // ============ TABLA DETALLADA ============
        Paragraph subtituloDetalle = new Paragraph("DETALLE POR CATEGORÍA",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14));
        subtituloDetalle.setSpacingBefore(10);
        document.add(subtituloDetalle);

        if (ventas == null || ventas.isEmpty()) {
            document.add(new Paragraph("No hay datos de ventas disponibles."));
        } else {
            // Crear tabla principal
            PdfPTable tabla = new PdfPTable(4);
            tabla.setWidthPercentage(100);

            // Encabezados
            String[] headers = {"CATEGORÍA", "CANTIDAD VENDIDA", "TOTAL VENTAS ($)", "% PARTICIPACIÓN"};
            for (String header : headers) {
                tabla.addCell(crearCeldaEncabezado(header));
            }

            // Calcular total general
            double totalGeneral = 0;
            for (Map<String, Object> venta : ventas) {
                Object totalObj = venta.get("total");
                if (totalObj instanceof Double) {
                    totalGeneral += (Double) totalObj;
                } else if (totalObj instanceof Integer) {
                    totalGeneral += ((Integer) totalObj).doubleValue();
                }
            }

            // Datos
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

                // Calcular porcentaje
                double porcentaje = totalGeneral > 0 ? (total / totalGeneral) * 100 : 0;

                tabla.addCell(crearCeldaDato(categoria != null ? categoria : "Sin categoría"));
                tabla.addCell(crearCeldaDato(String.valueOf(cantidad)));
                tabla.addCell(crearCeldaDato("$" + String.format("%,.2f", total)));
                tabla.addCell(crearCeldaDato(String.format("%.1f%%", porcentaje)));
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

    public byte[] generarPdfVentasPorCategoriaBytes() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, baos);
        document.open();

        // Título PRINCIPAL
        Paragraph tituloPrincipal = new Paragraph("REPORTE DE VENTAS POR CATEGORÍA",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18));
        tituloPrincipal.setAlignment(Element.ALIGN_CENTER);
        tituloPrincipal.setSpacingAfter(10);
        document.add(tituloPrincipal);

        // ============ INFORMACIÓN DEL FILTRO ============
        Paragraph infoFiltro = new Paragraph("FILTROS APLICADOS:",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12));
        infoFiltro.setSpacingAfter(5);
        document.add(infoFiltro);

        PdfPTable tablaFiltros = new PdfPTable(2);
        tablaFiltros.setWidthPercentage(80);
        tablaFiltros.setHorizontalAlignment(Element.ALIGN_LEFT);

        tablaFiltros.addCell(crearCeldaFiltro("Tipo de Reporte:"));
        tablaFiltros.addCell(crearCeldaValor("Ventas por Categoría"));

        tablaFiltros.addCell(crearCeldaFiltro("Fecha de generación:"));
        tablaFiltros.addCell(crearCeldaValor(new java.util.Date().toString()));

        tablaFiltros.addCell(crearCeldaFiltro("Período analizado:"));
        tablaFiltros.addCell(crearCeldaValor("Últimos 30 días"));

        document.add(tablaFiltros);
        document.add(Chunk.NEWLINE);

        // ============ RESUMEN ESTADÍSTICO ============
        Paragraph subtituloEstadisticas = new Paragraph("RESUMEN ESTADÍSTICO",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14));
        subtituloEstadisticas.setSpacingBefore(10);
        document.add(subtituloEstadisticas);

        // Obtener datos y estadísticas
        Map<String, Object> reporteVentas = ventasReporteService.generarVentasPorCategoria();
        java.util.List<Map<String, Object>> ventas = (java.util.List<Map<String, Object>>) reporteVentas.get("datos");
        Map<String, Object> estadisticasVentas = (Map<String, Object>) reporteVentas.get("estadisticas");

        // ============ GRÁFICA DE VENTAS POR CATEGORÍA ============
        if (ventas != null && !ventas.isEmpty()) {
            Paragraph subtituloGrafica = new Paragraph("GRÁFICA - VENTAS POR CATEGORÍA",
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14));
            subtituloGrafica.setSpacingBefore(10);
            document.add(subtituloGrafica);

            try {
                byte[] imagenGrafica = graficaService.generarGraficaVentasPorCategoria(ventas);
                Image grafica = Image.getInstance(imagenGrafica);
                grafica.setAlignment(Element.ALIGN_CENTER);
                grafica.scaleToFit(500, 300);
                document.add(grafica);
                document.add(Chunk.NEWLINE);
            } catch (Exception e) {
                document.add(new Paragraph("No se pudo generar la gráfica: " + e.getMessage()));
            }
        }

        // Tabla de estadísticas
        PdfPTable tablaEstadisticas = new PdfPTable(2);
        tablaEstadisticas.setWidthPercentage(60);
        tablaEstadisticas.setHorizontalAlignment(Element.ALIGN_CENTER);

        if (estadisticasVentas != null && !estadisticasVentas.isEmpty()) {
            agregarFilaEstadisticaPDF(tablaEstadisticas, "Total Categorías",
                    String.valueOf(estadisticasVentas.get("totalCategorias")));
            agregarFilaEstadisticaPDF(tablaEstadisticas, "Total Ventas",
                    "$" + String.format("%,.2f", estadisticasVentas.get("totalVentas")));
            agregarFilaEstadisticaPDF(tablaEstadisticas, "Venta Máxima",
                    "$" + String.format("%,.2f", estadisticasVentas.get("ventaMaxima")) +
                            " (" + estadisticasVentas.get("categoriaMaxima") + ")");
            agregarFilaEstadisticaPDF(tablaEstadisticas, "Venta Mínima",
                    "$" + String.format("%,.2f", estadisticasVentas.get("ventaMinima")) +
                            " (" + estadisticasVentas.get("categoriaMinima") + ")");
        }

        document.add(tablaEstadisticas);
        document.add(Chunk.NEWLINE);

        // ============ TABLA DETALLADA ============
        Paragraph subtituloDetalle = new Paragraph("DETALLE POR CATEGORÍA",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14));
        subtituloDetalle.setSpacingBefore(10);
        document.add(subtituloDetalle);

        if (ventas == null || ventas.isEmpty()) {
            document.add(new Paragraph("No hay datos de ventas disponibles."));
        } else {
            // Crear tabla principal
            PdfPTable tabla = new PdfPTable(4);
            tabla.setWidthPercentage(100);

            // Encabezados
            String[] headers = {"CATEGORÍA", "CANTIDAD VENDIDA", "TOTAL VENTAS ($)", "% PARTICIPACIÓN"};
            for (String header : headers) {
                tabla.addCell(crearCeldaEncabezado(header));
            }

            // Calcular total general
            double totalGeneral = 0;
            for (Map<String, Object> venta : ventas) {
                Object totalObj = venta.get("total");
                if (totalObj instanceof Double) {
                    totalGeneral += (Double) totalObj;
                } else if (totalObj instanceof Integer) {
                    totalGeneral += ((Integer) totalObj).doubleValue();
                }
            }

            // Datos
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

                // Calcular porcentaje
                double porcentaje = totalGeneral > 0 ? (total / totalGeneral) * 100 : 0;

                tabla.addCell(crearCeldaDato(categoria != null ? categoria : "Sin categoría"));
                tabla.addCell(crearCeldaDato(String.valueOf(cantidad)));
                tabla.addCell(crearCeldaDato("$" + String.format("%,.2f", total)));
                tabla.addCell(crearCeldaDato(String.format("%.1f%%", porcentaje)));
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
        return baos.toByteArray();
    }

    // ============ MÉTODOS PARA VENTAS DEL DÍA ============

    public void generarPdfVentasDelDia(HttpServletResponse response) throws Exception {
        response.setContentType("application/pdf");

        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();

        // Título PRINCIPAL
        Paragraph tituloPrincipal = new Paragraph("REPORTE DE VENTAS DEL DÍA",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18));
        tituloPrincipal.setAlignment(Element.ALIGN_CENTER);
        tituloPrincipal.setSpacingAfter(10);
        document.add(tituloPrincipal);

        // ============ INFORMACIÓN DEL FILTRO ============
        Paragraph infoFiltro = new Paragraph("FILTROS APLICADOS:",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12));
        infoFiltro.setSpacingAfter(5);
        document.add(infoFiltro);

        PdfPTable tablaFiltros = new PdfPTable(2);
        tablaFiltros.setWidthPercentage(80);
        tablaFiltros.setHorizontalAlignment(Element.ALIGN_LEFT);

        // Filtros específicos para ventas del día
        tablaFiltros.addCell(crearCeldaFiltro("Tipo de Reporte:"));
        tablaFiltros.addCell(crearCeldaValor("Ventas del Día"));

        tablaFiltros.addCell(crearCeldaFiltro("Fecha del reporte:"));
        tablaFiltros.addCell(crearCeldaValor(new java.util.Date().toString()));

        tablaFiltros.addCell(crearCeldaFiltro("Período analizado:"));
        tablaFiltros.addCell(crearCeldaValor("Día completo"));

        document.add(tablaFiltros);
        document.add(Chunk.NEWLINE);

        // ============ RESUMEN ESTADÍSTICO ============
        Paragraph subtituloEstadisticas = new Paragraph("RESUMEN DEL DÍA",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14));
        subtituloEstadisticas.setSpacingBefore(10);
        document.add(subtituloEstadisticas);

        // Obtener datos
        Map<String, Object> reporteVentas = ventasReporteService.generarVentasDelDia();
        java.util.List<Map<String, Object>> ventas = (java.util.List<Map<String, Object>>) reporteVentas.get("datos");
        Map<String, Object> estadisticas = (Map<String, Object>) reporteVentas.get("estadisticas");

        // ============ GRÁFICA DE VENTAS DEL DÍA ============
        if (ventas != null && !ventas.isEmpty()) {
            Paragraph subtituloGrafica = new Paragraph("GRÁFICA - VENTAS DEL DÍA",
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14));
            subtituloGrafica.setSpacingBefore(10);
            document.add(subtituloGrafica);

            try {
                byte[] imagenGrafica = graficaService.generarGraficaVentasDelDia(ventas);
                Image grafica = Image.getInstance(imagenGrafica);
                grafica.setAlignment(Element.ALIGN_CENTER);
                grafica.scaleToFit(500, 300);
                document.add(grafica);
                document.add(Chunk.NEWLINE);
            } catch (Exception e) {
                document.add(new Paragraph("No se pudo generar la gráfica: " + e.getMessage()));
            }
        }

        // Tabla de estadísticas del día
        PdfPTable tablaEstadisticas = new PdfPTable(2);
        tablaEstadisticas.setWidthPercentage(60);
        tablaEstadisticas.setHorizontalAlignment(Element.ALIGN_CENTER);

        if (estadisticas != null && !estadisticas.isEmpty()) {
            agregarFilaEstadisticaPDF(tablaEstadisticas, "Total del Día",
                    "$" + String.format("%,.2f", estadisticas.get("totalDia")));
            agregarFilaEstadisticaPDF(tablaEstadisticas, "Horas con ventas",
                    String.valueOf(estadisticas.get("totalHoras")));
            agregarFilaEstadisticaPDF(tablaEstadisticas, "Venta Máxima por Hora",
                    "$" + String.format("%,.2f", estadisticas.get("ventaMaxima")) +
                            " (" + estadisticas.get("horaMaxima") + ")");
            agregarFilaEstadisticaPDF(tablaEstadisticas, "Ticket Promedio",
                    "$" + String.format("%,.2f", estadisticas.get("ticketPromedio")));
        }

        document.add(tablaEstadisticas);
        document.add(Chunk.NEWLINE);

        // ============ TABLA DETALLADA POR HORA ============
        Paragraph subtituloDetalle = new Paragraph("VENTAS POR HORA",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14));
        subtituloDetalle.setSpacingBefore(10);
        document.add(subtituloDetalle);

        if (ventas == null || ventas.isEmpty()) {
            document.add(new Paragraph("No hay ventas registradas para el día de hoy."));
        } else {
            // Crear tabla principal
            PdfPTable tabla = new PdfPTable(4);
            tabla.setWidthPercentage(100);

            // Encabezados
            String[] headers = {"HORA", "CANTIDAD VENDIDA", "TOTAL VENTAS ($)", "TENDENCIA"};
            for (String header : headers) {
                tabla.addCell(crearCeldaEncabezado(header));
            }

            // Datos
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

                // Determinar tendencia (simple)
                String tendencia = "ESTABLE";
                if (total > 1000) tendencia = "ALTA";
                else if (total < 100) tendencia = "BAJA";

                tabla.addCell(crearCeldaDato(hora != null ? hora : "N/A"));
                tabla.addCell(crearCeldaDato(String.valueOf(cantidad)));
                tabla.addCell(crearCeldaDato("$" + String.format("%,.2f", total)));
                tabla.addCell(crearCeldaDato(tendencia));
            }

            document.add(tabla);
            document.add(Chunk.NEWLINE);

            // Total del día
            if (estadisticas != null && estadisticas.containsKey("totalDia")) {
                Paragraph total = new Paragraph("TOTAL DEL DÍA: $" +
                        String.format("%,.2f", estadisticas.get("totalDia")),
                        FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14));
                total.setAlignment(Element.ALIGN_RIGHT);
                document.add(total);
            }
        }

        document.add(Chunk.NEWLINE);
        document.add(new Paragraph("--- Fin del Reporte Diario ---",
                FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10)));

        document.close();
    }

    public byte[] generarPdfVentasDelDiaBytes() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, baos);
        document.open();

        // Título PRINCIPAL
        Paragraph tituloPrincipal = new Paragraph("REPORTE DE VENTAS DEL DÍA",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18));
        tituloPrincipal.setAlignment(Element.ALIGN_CENTER);
        tituloPrincipal.setSpacingAfter(10);
        document.add(tituloPrincipal);

        // ============ INFORMACIÓN DEL FILTRO ============
        Paragraph infoFiltro = new Paragraph("FILTROS APLICADOS:",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12));
        infoFiltro.setSpacingAfter(5);
        document.add(infoFiltro);

        PdfPTable tablaFiltros = new PdfPTable(2);
        tablaFiltros.setWidthPercentage(80);
        tablaFiltros.setHorizontalAlignment(Element.ALIGN_LEFT);

        tablaFiltros.addCell(crearCeldaFiltro("Tipo de Reporte:"));
        tablaFiltros.addCell(crearCeldaValor("Ventas del Día"));

        tablaFiltros.addCell(crearCeldaFiltro("Fecha del reporte:"));
        tablaFiltros.addCell(crearCeldaValor(new java.util.Date().toString()));

        tablaFiltros.addCell(crearCeldaFiltro("Período analizado:"));
        tablaFiltros.addCell(crearCeldaValor("Día completo"));

        document.add(tablaFiltros);
        document.add(Chunk.NEWLINE);

        // ============ RESUMEN ESTADÍSTICO ============
        Paragraph subtituloEstadisticas = new Paragraph("RESUMEN DEL DÍA",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14));
        subtituloEstadisticas.setSpacingBefore(10);
        document.add(subtituloEstadisticas);

        // Obtener datos
        Map<String, Object> reporteVentas = ventasReporteService.generarVentasDelDia();
        java.util.List<Map<String, Object>> ventas = (java.util.List<Map<String, Object>>) reporteVentas.get("datos");
        Map<String, Object> estadisticas = (Map<String, Object>) reporteVentas.get("estadisticas");

        // ============ GRÁFICA DE VENTAS DEL DÍA ============
        if (ventas != null && !ventas.isEmpty()) {
            Paragraph subtituloGrafica = new Paragraph("GRÁFICA - VENTAS DEL DÍA",
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14));
            subtituloGrafica.setSpacingBefore(10);
            document.add(subtituloGrafica);

            try {
                byte[] imagenGrafica = graficaService.generarGraficaVentasDelDia(ventas);
                Image grafica = Image.getInstance(imagenGrafica);
                grafica.setAlignment(Element.ALIGN_CENTER);
                grafica.scaleToFit(500, 300);
                document.add(grafica);
                document.add(Chunk.NEWLINE);
            } catch (Exception e) {
                document.add(new Paragraph("No se pudo generar la gráfica: " + e.getMessage()));
            }
        }

        // Tabla de estadísticas del día
        PdfPTable tablaEstadisticas = new PdfPTable(2);
        tablaEstadisticas.setWidthPercentage(60);
        tablaEstadisticas.setHorizontalAlignment(Element.ALIGN_CENTER);

        if (estadisticas != null && !estadisticas.isEmpty()) {
            agregarFilaEstadisticaPDF(tablaEstadisticas, "Total del Día",
                    "$" + String.format("%,.2f", estadisticas.get("totalDia")));
            agregarFilaEstadisticaPDF(tablaEstadisticas, "Horas con ventas",
                    String.valueOf(estadisticas.get("totalHoras")));
            agregarFilaEstadisticaPDF(tablaEstadisticas, "Venta Máxima por Hora",
                    "$" + String.format("%,.2f", estadisticas.get("ventaMaxima")) +
                            " (" + estadisticas.get("horaMaxima") + ")");
            agregarFilaEstadisticaPDF(tablaEstadisticas, "Ticket Promedio",
                    "$" + String.format("%,.2f", estadisticas.get("ticketPromedio")));
        }

        document.add(tablaEstadisticas);
        document.add(Chunk.NEWLINE);

        // ============ TABLA DETALLADA POR HORA ============
        Paragraph subtituloDetalle = new Paragraph("VENTAS POR HORA",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14));
        subtituloDetalle.setSpacingBefore(10);
        document.add(subtituloDetalle);

        if (ventas == null || ventas.isEmpty()) {
            document.add(new Paragraph("No hay ventas registradas para el día de hoy."));
        } else {
            // Crear tabla principal
            PdfPTable tabla = new PdfPTable(4);
            tabla.setWidthPercentage(100);

            // Encabezados
            String[] headers = {"HORA", "CANTIDAD VENDIDA", "TOTAL VENTAS ($)", "TENDENCIA"};
            for (String header : headers) {
                tabla.addCell(crearCeldaEncabezado(header));
            }

            // Datos
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

                // Determinar tendencia (simple)
                String tendencia = "ESTABLE";
                if (total > 1000) tendencia = "ALTA";
                else if (total < 100) tendencia = "BAJA";

                tabla.addCell(crearCeldaDato(hora != null ? hora : "N/A"));
                tabla.addCell(crearCeldaDato(String.valueOf(cantidad)));
                tabla.addCell(crearCeldaDato("$" + String.format("%,.2f", total)));
                tabla.addCell(crearCeldaDato(tendencia));
            }

            document.add(tabla);
            document.add(Chunk.NEWLINE);

            // Total del día
            if (estadisticas != null && estadisticas.containsKey("totalDia")) {
                Paragraph total = new Paragraph("TOTAL DEL DÍA: $" +
                        String.format("%,.2f", estadisticas.get("totalDia")),
                        FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14));
                total.setAlignment(Element.ALIGN_RIGHT);
                document.add(total);
            }
        }

        document.add(Chunk.NEWLINE);
        document.add(new Paragraph("--- Fin del Reporte Diario ---",
                FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10)));

        document.close();
        return baos.toByteArray();
    }

    // ============ MÉTODOS AUXILIARES PARA CELDAS ============

    private PdfPCell crearCeldaFiltro(String texto) {
        PdfPCell celda = new PdfPCell(new Phrase(texto,
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10)));
        celda.setPadding(3);
        celda.setHorizontalAlignment(Element.ALIGN_LEFT);
        celda.setBackgroundColor(new BaseColor(240, 240, 240));
        celda.setBorderWidth(0.5f);
        return celda;
    }

    private PdfPCell crearCeldaValor(String texto) {
        PdfPCell celda = new PdfPCell(new Phrase(texto));
        celda.setPadding(3);
        celda.setHorizontalAlignment(Element.ALIGN_LEFT);
        celda.setBorderWidth(0.5f);
        return celda;
    }

    private PdfPCell crearCeldaEncabezado(String texto) {
        PdfPCell celda = new PdfPCell(new Phrase(texto,
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11)));
        celda.setPadding(5);
        celda.setHorizontalAlignment(Element.ALIGN_CENTER);
        celda.setBackgroundColor(new BaseColor(200, 200, 200));
        celda.setBorderWidth(1);
        return celda;
    }

    private PdfPCell crearCeldaDato(String texto) {
        PdfPCell celda = new PdfPCell(new Phrase(texto));
        celda.setPadding(5);
        celda.setHorizontalAlignment(Element.ALIGN_CENTER);
        celda.setBorderWidth(0.5f);
        return celda;
    }

    private void agregarFilaEstadisticaPDF(PdfPTable tabla, String titulo, String valor) {
        tabla.addCell(crearCeldaFiltro(titulo));
        tabla.addCell(crearCeldaValor(valor));
    }

    private void agregarContenidoReporteProductos(Document document,
                                                  java.util.List<ProductoModel> productos,
                                                  Map<String, Object> estadisticas,
                                                  String tipoReporte) throws Exception {

        // ============ ENCABEZADO ============
        Paragraph titulo = new Paragraph("REPORTE DE PRODUCTOS - " + tipoReporte,
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18));
        titulo.setAlignment(Element.ALIGN_CENTER);
        titulo.setSpacingAfter(20);
        document.add(titulo);

        // ============ FILTROS APLICADOS ============
        Paragraph infoFiltro = new Paragraph("FILTROS APLICADOS:",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12));
        infoFiltro.setSpacingAfter(5);
        document.add(infoFiltro);

        PdfPTable tablaFiltros = new PdfPTable(2);
        tablaFiltros.setWidthPercentage(80);
        tablaFiltros.setHorizontalAlignment(Element.ALIGN_LEFT);

        tablaFiltros.addCell(crearCeldaFiltro("Tipo de Reporte:"));
        tablaFiltros.addCell(crearCeldaValor(tipoReporte));

        tablaFiltros.addCell(crearCeldaFiltro("Fecha:"));
        tablaFiltros.addCell(crearCeldaValor(new java.util.Date().toString()));

        tablaFiltros.addCell(crearCeldaFiltro("Total Productos:"));
        tablaFiltros.addCell(crearCeldaValor(String.valueOf(productos.size())));

        tablaFiltros.addCell(crearCeldaFiltro("Gráficos incluidos:"));
        tablaFiltros.addCell(crearCeldaValor("Torta y Barras"));

        document.add(tablaFiltros);
        document.add(Chunk.NEWLINE);

        // ============ CÁLCULOS ============
        long activos = productos.stream().filter(ProductoModel::isEstado).count();
        long inactivos = productos.size() - activos;

        // ============ SECCIÓN: AMBAS GRÁFICAS ============
        Paragraph seccionGraficas = new Paragraph("GRÁFICAS DE DISTRIBUCIÓN",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16));
        seccionGraficas.setAlignment(Element.ALIGN_CENTER);
        seccionGraficas.setSpacingAfter(10);
        document.add(seccionGraficas);

        // Crear tabla para mostrar ambas gráficas lado a lado
        PdfPTable tablaGraficas = new PdfPTable(2);
        tablaGraficas.setWidthPercentage(100);
        tablaGraficas.setSpacingBefore(10);

        try {
            // ============ GRÁFICA 1: TORTA ============
            Paragraph tituloTorta = new Paragraph("GRÁFICO DE TORTA",
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12));
            tituloTorta.setAlignment(Element.ALIGN_CENTER);
            tituloTorta.setSpacingAfter(5);

            // Generar gráfica de pastel
            byte[] imagenPastel = graficaService.generarGraficaPastel(activos, inactivos);
            Image graficaPastel = Image.getInstance(imagenPastel);
            graficaPastel.setAlignment(Image.ALIGN_CENTER);
            graficaPastel.scaleToFit(250, 180);

            PdfPCell celdaTorta = new PdfPCell();
            celdaTorta.setHorizontalAlignment(Element.ALIGN_CENTER);
            celdaTorta.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celdaTorta.setBorder(Rectangle.BOX);
            celdaTorta.setPadding(10);
            celdaTorta.addElement(tituloTorta);
            celdaTorta.addElement(graficaPastel);

            // ============ GRÁFICA 2: BARRAS ============
            Paragraph tituloBarras = new Paragraph("GRÁFICO DE BARRAS",
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12));
            tituloBarras.setAlignment(Element.ALIGN_CENTER);
            tituloBarras.setSpacingAfter(5);

            // Generar gráfica de barras
            byte[] imagenBarras = graficaService.generarGraficaBarras(activos, inactivos);
            Image graficaBarras = Image.getInstance(imagenBarras);
            graficaBarras.setAlignment(Image.ALIGN_CENTER);
            graficaBarras.scaleToFit(250, 180);

            PdfPCell celdaBarras = new PdfPCell();
            celdaBarras.setHorizontalAlignment(Element.ALIGN_CENTER);
            celdaBarras.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celdaBarras.setBorder(Rectangle.BOX);
            celdaBarras.setPadding(10);
            celdaBarras.addElement(tituloBarras);
            celdaBarras.addElement(graficaBarras);

            // Agregar celdas a la tabla
            tablaGraficas.addCell(celdaTorta);
            tablaGraficas.addCell(celdaBarras);

        } catch (Exception e) {
            document.add(new Paragraph("Error al generar gráficas: " + e.getMessage()));
            e.printStackTrace();
        }

        document.add(tablaGraficas);
        document.add(Chunk.NEWLINE);

        // ============ SECCIÓN: INTERPRETACIÓN DE GRÁFICAS ============
        Paragraph interpretacionGraficas = new Paragraph("INTERPRETACIÓN DE LAS GRÁFICAS",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14));
        interpretacionGraficas.setSpacingBefore(15);
        document.add(interpretacionGraficas);

        // Explicación de las gráficas
        double porcentajeActivos = productos.size() > 0 ? (activos * 100.0 / productos.size()) : 0;
        double porcentajeInactivos = productos.size() > 0 ? (inactivos * 100.0 / productos.size()) : 0;

        String explicacion = "El gráfico de TORTA muestra la proporción de productos activos vs inactivos. " +
                "El gráfico de BARRAS permite comparar visualmente las cantidades.\n\n" +
                "• Productos Activos: " + activos + " (" + String.format("%.1f%%", porcentajeActivos) + ")\n" +
                "• Productos Inactivos: " + inactivos + " (" + String.format("%.1f%%", porcentajeInactivos) + ")\n" +
                "• Total: " + productos.size() + " productos";

        document.add(new Paragraph(explicacion));
        document.add(Chunk.NEWLINE);

        // ============ TABLA RESUMEN ============
        Paragraph subtituloTabla = new Paragraph("TABLA RESUMEN DETALLADA",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14));
        subtituloTabla.setSpacingBefore(15);
        document.add(subtituloTabla);

        PdfPTable tablaResumen = new PdfPTable(4);
        tablaResumen.setWidthPercentage(90);
        tablaResumen.setHorizontalAlignment(Element.ALIGN_CENTER);

        // Encabezados mejorados
        tablaResumen.addCell(crearCeldaEncabezado("ESTADO"));
        tablaResumen.addCell(crearCeldaEncabezado("CANTIDAD"));
        tablaResumen.addCell(crearCeldaEncabezado("PORCENTAJE"));
        tablaResumen.addCell(crearCeldaEncabezado("ANÁLISIS"));

        // Datos con análisis
        tablaResumen.addCell(crearCeldaDato("ACTIVOS"));
        tablaResumen.addCell(crearCeldaDato(String.valueOf(activos)));
        tablaResumen.addCell(crearCeldaDato(String.format("%.1f%%", porcentajeActivos)));

        String analisisActivos = porcentajeActivos >= 70 ? "BUENO" :
                porcentajeActivos >= 50 ? "⚠ REGULAR" : "CRÍTICO";
        tablaResumen.addCell(crearCeldaDato(analisisActivos));

        tablaResumen.addCell(crearCeldaDato("INACTIVOS"));
        tablaResumen.addCell(crearCeldaDato(String.valueOf(inactivos)));
        tablaResumen.addCell(crearCeldaDato(String.format("%.1f%%", porcentajeInactivos)));

        String analisisInactivos = porcentajeInactivos <= 30 ? "ACEPTABLE" :
                porcentajeInactivos <= 50 ? "⚠ ALTO" : "MUY ALTO";
        tablaResumen.addCell(crearCeldaDato(analisisInactivos));

        tablaResumen.addCell(crearCeldaEncabezado("TOTAL"));
        tablaResumen.addCell(crearCeldaEncabezado(String.valueOf(productos.size())));
        tablaResumen.addCell(crearCeldaEncabezado("100%"));

        String analisisTotal = productos.size() > 0 ? productos.size() + " productos analizados" : "📭 Sin datos";
        tablaResumen.addCell(crearCeldaEncabezado(analisisTotal));

        document.add(tablaResumen);
        document.add(Chunk.NEWLINE);

        // ============ TABLA DETALLADA DE PRODUCTOS ============
        if (productos.size() <= 30) {
            Paragraph subtituloDetalle = new Paragraph("DETALLE DE PRODUCTOS",
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14));
            subtituloDetalle.setSpacingBefore(15);
            document.add(subtituloDetalle);

            PdfPTable tablaDetalle = new PdfPTable(5);
            tablaDetalle.setWidthPercentage(100);

            String[] headers = {"PRODUCTO", "CANTIDAD", "PRECIO", "VALOR TOTAL", "ESTADO"};
            for (String header : headers) {
                tablaDetalle.addCell(crearCeldaEncabezado(header));
            }

            for (ProductoModel producto : productos) {
                tablaDetalle.addCell(crearCeldaDato(producto.getNombre()));
                tablaDetalle.addCell(crearCeldaDato(String.valueOf(producto.getCantidad())));
                tablaDetalle.addCell(crearCeldaDato("$" + producto.getValor()));

                double valorTotal = producto.getCantidad() * producto.getValor();
                tablaDetalle.addCell(crearCeldaDato("$" + String.format("%.2f", valorTotal)));

                String estado = producto.isEstado() ? "ACTIVO" : "INACTIVO";
                PdfPCell celdaEstado = crearCeldaDato(estado);
                if (producto.isEstado()) {
                    celdaEstado.setBackgroundColor(new BaseColor(220, 255, 220));
                } else {
                    celdaEstado.setBackgroundColor(new BaseColor(255, 220, 220));
                }
                tablaDetalle.addCell(celdaEstado);
            }

            document.add(tablaDetalle);
            document.add(Chunk.NEWLINE);
        }

        // ============ ESTADÍSTICAS FINANCIERAS ============
        if (estadisticas != null && !estadisticas.isEmpty()) {
            Paragraph subtituloEstadisticas = new Paragraph("ESTADÍSTICAS FINANCIERAS",
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14));
            subtituloEstadisticas.setSpacingBefore(15);
            document.add(subtituloEstadisticas);

            PdfPTable tablaEstadisticas = new PdfPTable(2);
            tablaEstadisticas.setWidthPercentage(60);
            tablaEstadisticas.setHorizontalAlignment(Element.ALIGN_CENTER);

            agregarFilaEstadisticaPDF(tablaEstadisticas, "Valor Total Inventario",
                    "$" + estadisticas.getOrDefault("valorTotalInventario", "0"));
            agregarFilaEstadisticaPDF(tablaEstadisticas, "Productos Activos",
                    String.valueOf(estadisticas.getOrDefault("totalActivos", "0")));
            agregarFilaEstadisticaPDF(tablaEstadisticas, "Productos Inactivos",
                    String.valueOf(estadisticas.getOrDefault("totalInactivos", "0")));
            agregarFilaEstadisticaPDF(tablaEstadisticas, "Cantidad Total Stock",
                    String.valueOf(estadisticas.getOrDefault("cantidadTotalStock", "0")) + " unidades");

            document.add(tablaEstadisticas);
            document.add(Chunk.NEWLINE);
        }

        // ============ INTERPRETACIÓN FINAL ============
        Paragraph interpretacionFinal = new Paragraph("CONCLUSIÓN Y RECOMENDACIONES",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14));
        interpretacionFinal.setSpacingBefore(15);
        document.add(interpretacionFinal);

        String interpretacionTexto = generarInterpretacionMejorada(activos, productos.size());
        document.add(new Paragraph(interpretacionTexto));

        document.add(Chunk.NEWLINE);
        document.add(new Paragraph("--- Fin del Reporte | Gráficas generadas automáticamente ---",
                FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10)));
    }

    private String generarInterpretacionMejorada(long activos, long total) {
        double porcentajeActivos = total > 0 ? (activos * 100.0 / total) : 0;
        double porcentajeInactivos = 100 - porcentajeActivos;

        StringBuilder interpretacion = new StringBuilder();

        if (activos == total) {
            interpretacion.append("EXCELENTE: 100% de productos activos.\n");
            interpretacion.append("   • Todos los productos están disponibles para venta.\n");
            interpretacion.append("   • Inventario optimizado al máximo.");
        } else if (porcentajeActivos >= 80) {
            interpretacion.append("MUY BUENO: ").append(String.format("%.1f", porcentajeActivos)).append("% de productos activos.\n");
            interpretacion.append("   • Alto nivel de disponibilidad.\n");
            interpretacion.append("   • Solo ").append(String.format("%.1f", porcentajeInactivos)).append("% requiere revisión.");
        } else if (porcentajeActivos >= 60) {
            interpretacion.append("⚠ REGULAR: ").append(String.format("%.1f", porcentajeActivos)).append("% de productos activos.\n");
            interpretacion.append("   • Nivel aceptable pero puede mejorarse.\n");
            interpretacion.append("   • Considere reactivar productos inactivos.");
        } else if (porcentajeActivos >= 40) {
            interpretacion.append("⚠ REQUIERE ATENCIÓN: ").append(String.format("%.1f", porcentajeActivos)).append("% de productos activos.\n");
            interpretacion.append("   • Nivel bajo de disponibilidad.\n");
            interpretacion.append("   • Revise los ").append(String.format("%.1f", porcentajeInactivos)).append("% de productos inactivos.");
        } else {
            interpretacion.append("CRÍTICO: Solo ").append(String.format("%.1f", porcentajeActivos)).append("% de productos activos.\n");
            interpretacion.append("   • Nivel muy bajo de disponibilidad.\n");
            interpretacion.append("   • Se recomienda revisión inmediata del inventario.");
        }

        return interpretacion.toString();
    }

    // Getter para GraficaService
    public GraficaService getGraficaService() {
        return graficaService;
    }
}