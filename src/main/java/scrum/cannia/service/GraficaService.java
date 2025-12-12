package scrum.cannia.service;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
@SuppressWarnings("unchecked")
public class GraficaService {

    // Método existente para gráfica de pastel
    public byte[] generarGraficaPastel(long activos, long inactivos) throws IOException {
        DefaultPieDataset dataset = new DefaultPieDataset();
        dataset.setValue("Activos", activos);
        dataset.setValue("Inactivos", inactivos);

        JFreeChart chart = ChartFactory.createPieChart(
                "Distribución de Productos",
                dataset,
                true,
                true,
                false
        );

        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setSectionPaint("Activos", new Color(76, 175, 80));
        plot.setSectionPaint("Inactivos", new Color(244, 67, 54));
        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlinePaint(Color.WHITE);

        return convertirChartABytes(chart, 600, 400);
    }

    // Método existente para gráfica de barras
    public byte[] generarGraficaBarras(long activos, long inactivos) throws IOException {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(activos, "Productos", "Activos");
        dataset.addValue(inactivos, "Productos", "Inactivos");

        JFreeChart chart = ChartFactory.createBarChart(
                "Productos por Estado",
                "Estado",
                "Cantidad",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        CategoryPlot plot = chart.getCategoryPlot();
        plot.getRenderer().setSeriesPaint(0, new Color(63, 81, 181));
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(Color.GRAY);

        return convertirChartABytes(chart, 600, 400);
    }

    // NUEVO: Gráfica para ventas por categoría
    public byte[] generarGraficaVentasPorCategoria(List<Map<String, Object>> ventas) throws IOException {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (Map<String, Object> venta : ventas) {
            String categoria = (String) venta.get("categoria");
            Object totalObj = venta.get("total");

            if (categoria != null && totalObj != null) {
                double total = 0.0;
                if (totalObj instanceof Number) {
                    total = ((Number) totalObj).doubleValue();
                }
                dataset.addValue(total, "Ventas", categoria);
            }
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "Ventas por Categoría",
                "Categoría",
                "Monto ($)",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(Color.GRAY);
        plot.getRenderer().setSeriesPaint(0, new Color(156, 39, 176));

        return convertirChartABytes(chart, 800, 500);
    }

    // NUEVO: Gráfica para ventas del día
    public byte[] generarGraficaVentasDelDia(List<Map<String, Object>> ventas) throws IOException {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        // Ordenar por hora si es necesario
        java.util.Collections.sort(ventas, (v1, v2) -> {
            String h1 = (String) v1.get("hora");
            String h2 = (String) v2.get("hora");
            if (h1 == null && h2 == null) return 0;
            if (h1 == null) return -1;
            if (h2 == null) return 1;
            return h1.compareTo(h2);
        });

        for (Map<String, Object> venta : ventas) {
            String hora = (String) venta.get("hora");
            Object totalObj = venta.get("total");

            if (hora != null && totalObj != null) {
                double total = 0.0;
                if (totalObj instanceof Number) {
                    total = ((Number) totalObj).doubleValue();
                }
                dataset.addValue(total, "Ventas", hora);
            }
        }

        JFreeChart chart = ChartFactory.createLineChart(
                "Ventas del Día por Hora",
                "Hora",
                "Monto ($)",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(Color.GRAY);
        plot.getRenderer().setSeriesPaint(0, new Color(33, 150, 243));

        return convertirChartABytes(chart, 800, 500);
    }

    // Método auxiliar para convertir gráfica a bytes
    private byte[] convertirChartABytes(JFreeChart chart, int width, int height) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ChartUtils.writeChartAsPNG(baos, chart, width, height);
        return baos.toByteArray();
    }
}