package scrum.cannia.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import scrum.cannia.model.ProductoModel;
import scrum.cannia.repository.ProductoRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReporteService {

    @Autowired
    private ProductoRepository productoRepository;

    /**
     * Método PÚBLICO para calcular estadísticas
     */
    public Map<String, Object> calcularEstadisticas(List<ProductoModel> productos) {
        Map<String, Object> stats = new HashMap<>();

        long totalActivos = productos.stream().filter(ProductoModel::isEstado).count();
        long totalInactivos = productos.stream().filter(p -> !p.isEstado()).count();
        long totalPublicados = productos.stream().filter(ProductoModel::isPublicado).count();

        int cantidadTotal = productos.stream()
                .mapToInt(ProductoModel::getCantidad)
                .sum();

        int valorTotal = productos.stream()
                .mapToInt(ProductoModel::getValor)
                .sum();

        int valorPromedio = productos.isEmpty() ? 0 : valorTotal / productos.size();

        stats.put("totalActivos", totalActivos);
        stats.put("totalInactivos", totalInactivos);
        stats.put("totalPublicados", totalPublicados);
        stats.put("cantidadTotalStock", cantidadTotal);
        stats.put("valorTotalInventario", valorTotal);
        stats.put("valorPromedioProducto", valorPromedio);

        // Calcular porcentaje solo si hay productos
        if (!productos.isEmpty()) {
            int porcentajeActivos = (int) ((totalActivos * 100) / productos.size());
            stats.put("porcentajeActivos", porcentajeActivos);
        } else {
            stats.put("porcentajeActivos", 0);
        }

        return stats;
    }

    /**
     * Genera reporte de productos según filtros
     */
    public Map<String, Object> generarReporteProductos(String tipoGrafico, Boolean estadoFiltro) {
        List<ProductoModel> productos;

        // Obtener productos según filtro
        if (estadoFiltro != null) {
            productos = productoRepository.findByEstado(estadoFiltro);
        } else {
            productos = productoRepository.findAll();
        }

        Map<String, Object> resultado = new HashMap<>();

        // Determinar tipo de gráfico
        if ("TORTA".equalsIgnoreCase(tipoGrafico)) {
            resultado.put("tipo", "TORTA");
            resultado.put("datos", generarDatosTorta(productos));
        } else {
            resultado.put("tipo", "BARRAS");
            resultado.put("datos", generarDatosBarras(productos));
        }

        // Estadísticas - usa el método público
        resultado.put("estadisticas", calcularEstadisticas(productos));
        resultado.put("totalProductos", productos.size());
        resultado.put("filtroAplicado", estadoFiltro != null ?
                (estadoFiltro ? "ACTIVOS" : "INACTIVOS") : "TODOS");

        return resultado;
    }

    /**
     * Datos para gráfico de torta (por estado)
     */
    private List<Map<String, Object>> generarDatosTorta(List<ProductoModel> productos) {
        // Contar productos activos vs inactivos
        long activos = productos.stream().filter(ProductoModel::isEstado).count();
        long inactivos = productos.stream().filter(p -> !p.isEstado()).count();

        List<Map<String, Object>> datos = new ArrayList<>();

        if (activos > 0) {
            Map<String, Object> activoItem = new HashMap<>();
            activoItem.put("etiqueta", "Productos Activos");
            activoItem.put("valor", activos);
            activoItem.put("color", "#28a745");
            datos.add(activoItem);
        }

        if (inactivos > 0) {
            Map<String, Object> inactivoItem = new HashMap<>();
            inactivoItem.put("etiqueta", "Productos Inactivos");
            inactivoItem.put("valor", inactivos);
            inactivoItem.put("color", "#dc3545");
            datos.add(inactivoItem);
        }

        // Si no hay datos, agregar uno vacío
        if (datos.isEmpty()) {
            Map<String, Object> vacioItem = new HashMap<>();
            vacioItem.put("etiqueta", "Sin datos");
            vacioItem.put("valor", 0);
            vacioItem.put("color", "#6c757d");
            datos.add(vacioItem);
        }

        return datos;
    }

    /**
     * Datos para gráfico de barras (por producto)
     */
    private List<Map<String, Object>> generarDatosBarras(List<ProductoModel> productos) {
        List<Map<String, Object>> datos = new ArrayList<>();

        for (ProductoModel producto : productos) {
            Map<String, Object> item = new HashMap<>();
            item.put("producto", producto.getNombre());
            item.put("cantidad", producto.getCantidad());
            item.put("valor", producto.getValor());
            item.put("estado", producto.isEstado());
            item.put("publicado", producto.isPublicado());
            item.put("unidadMedida", producto.getUnidadMedida() != null ?
                    producto.getUnidadMedida().toString() : "N/A");

            // Color según estado
            if (producto.isEstado()) {
                item.put("color", "#007bff");
            } else {
                item.put("color", "#6c757d");
            }

            datos.add(item);
        }

        // Ordenar por cantidad descendente
        datos.sort((a, b) -> Integer.compare(
                (Integer) b.get("cantidad"),
                (Integer) a.get("cantidad")
        ));

        return datos;
    }

    /**
     * Obtener opciones de filtro para el frontend
     */
    public Map<String, Object> obtenerOpcionesFiltro() {
        Map<String, Object> opciones = new HashMap<>();

        List<Map<String, String>> estados = new ArrayList<>();
        estados.add(Map.of("valor", "", "texto", "Todos los productos"));
        estados.add(Map.of("valor", "true", "texto", "Solo activos"));
        estados.add(Map.of("valor", "false", "texto", "Solo inactivos"));

        List<Map<String, String>> tiposGrafico = new ArrayList<>();
        tiposGrafico.add(Map.of("valor", "BARRAS", "texto", "Gráfico de Barras"));
        tiposGrafico.add(Map.of("valor", "TORTA", "texto", "Gráfico de Torta"));

        opciones.put("estados", estados);
        opciones.put("tiposGrafico", tiposGrafico);

        return opciones;
    }

    /**
     * Método auxiliar para contar productos por estado
     */
    public Map<String, Long> contarProductosPorEstado() {
        List<ProductoModel> todos = productoRepository.findAll();

        Map<String, Long> conteo = new HashMap<>();
        conteo.put("activos", todos.stream().filter(ProductoModel::isEstado).count());
        conteo.put("inactivos", todos.stream().filter(p -> !p.isEstado()).count());
        conteo.put("total", (long) todos.size());

        return conteo;
    }
}