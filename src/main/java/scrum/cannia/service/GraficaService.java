package scrum.cannia.service;

import com.itextpdf.text.*;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

@Service
public class GraficaService {

    // Generar gráfica de pastel (torta)
    public byte[] generarGraficaPastel(long activos, long inactivos) throws Exception {
        DefaultPieDataset dataset = new DefaultPieDataset();
        dataset.setValue("Activos", activos);
        dataset.setValue("Inactivos", inactivos);

        JFreeChart chart = ChartFactory.createPieChart(
                "Distribución de Productos",  // Título
                dataset,                      // Datos
                true,                         // Leyenda
                true,                         // Tooltips
                false                         // URLs
        );

        // Personalizar colores
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setSectionPaint("Activos", new Color(46, 204, 113)); // Verde
        plot.setSectionPaint("Inactivos", new Color(231, 76, 60)); // Rojo

        return convertirChartABytes(chart, 500, 300);
    }

    // Generar gráfica de barras
    public byte[] generarGraficaBarras(long activos, long inactivos) throws Exception {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(activos, "Productos", "Activos");
        dataset.addValue(inactivos, "Productos", "Inactivos");

        JFreeChart chart = ChartFactory.createBarChart(
                "Productos por Estado",       // Título
                "Estado",                     // Etiqueta X
                "Cantidad",                   // Etiqueta Y
                dataset                       // Datos
        );

        // Personalizar colores
        CategoryPlot plot = chart.getCategoryPlot();
        plot.getRenderer().setSeriesPaint(0, new Color(52, 152, 219)); // Azul

        return convertirChartABytes(chart, 500, 300);
    }

    // Convertir JFreeChart a bytes PNG
    private byte[] convertirChartABytes(JFreeChart chart, int width, int height) throws Exception {
        BufferedImage image = chart.createBufferedImage(width, height);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        return baos.toByteArray();
    }
}