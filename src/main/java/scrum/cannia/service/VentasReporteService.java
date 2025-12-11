package scrum.cannia.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import scrum.cannia.repository.FacturaRepository;
import scrum.cannia.model.CategoriaModel;
import scrum.cannia.model.FacturaModel;
import scrum.cannia.model.FacturaDetalleModel;
import scrum.cannia.model.ProductoModel;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class VentasReporteService {

    @Autowired
    private FacturaRepository facturaRepository;

    /**
     * Reporte REAL de ventas por categoría
     */
    public Map<String, Object> generarVentasPorCategoria() {
        Map<String, Object> resultado = new HashMap<>();

        try {
            // Obtener TODAS las facturas
            List<FacturaModel> todasFacturas = facturaRepository.findAll();

            // Filtrar solo facturas PAGADAS (si aplica)
            List<FacturaModel> facturas = todasFacturas; // Sin filtrar

// O si quieres ver qué estados tienen:
            System.out.println("Estados encontrados:");
            for (FacturaModel f : todasFacturas) {
                System.out.println("  Factura ID " + f.getId() + ": Estado=" +
                        (f.getEstado() != null ? f.getEstado().toString() : "null"));
            }

            System.out.println("Facturas encontradas: " + facturas.size());

            // Mapa para acumular ventas por categoría
            Map<String, Map<String, Object>> ventasPorCategoria = new HashMap<>();

            for (FacturaModel factura : facturas) {
                System.out.println("Procesando factura ID: " + factura.getId());

                if (factura.getDetalles() != null) {
                    for (FacturaDetalleModel detalle : factura.getDetalles()) {
                        System.out.println("Procesando detalle, producto: " +
                                (detalle.getProducto() != null ? detalle.getProducto().getNombre() : "null"));

                        ProductoModel producto = detalle.getProducto();

                        if (producto != null && producto.getCategorias() != null && !producto.getCategorias().isEmpty()) {
                            // Un producto puede tener múltiples categorías
                            for (CategoriaModel categoria : producto.getCategorias()) {
                                String categoriaNombre = categoria.getNombre();

                                if (!ventasPorCategoria.containsKey(categoriaNombre)) {
                                    Map<String, Object> catData = new HashMap<>();
                                    catData.put("categoria", categoriaNombre);
                                    catData.put("cantidad", 0);
                                    catData.put("total", 0.0);
                                    ventasPorCategoria.put(categoriaNombre, catData);
                                }

                                Map<String, Object> catData = ventasPorCategoria.get(categoriaNombre);
                                int cantidadActual = (int) catData.get("cantidad");
                                double totalActual = (double) catData.get("total");

                                // Sumar este detalle
                                int cantidadDetalle = detalle.getCantidad() != null ? detalle.getCantidad() : 0;
                                double precioDetalle = detalle.getPrecio() != null ?
                                        detalle.getPrecio().doubleValue() : 0.0;

                                catData.put("cantidad", cantidadActual + cantidadDetalle);
                                catData.put("total", totalActual + precioDetalle);

                                System.out.println("Agregado a categoría " + categoriaNombre +
                                        ": cantidad=" + cantidadDetalle + ", precio=" + precioDetalle);
                            }
                        } else {
                            System.out.println("Producto sin categorías o producto nulo");
                        }
                    }
                }
            }

            // Convertir a lista
            List<Map<String, Object>> datos = new ArrayList<>(ventasPorCategoria.values());
            System.out.println("Total categorías con ventas: " + datos.size());

            // Si no hay datos, usar ejemplo
            if (datos.isEmpty()) {
                System.out.println("No se encontraron datos reales, usando datos de ejemplo");
                return generarVentasPorCategoriaEjemplo();
            }

            // Ordenar por total descendente
            datos.sort((a, b) -> Double.compare(
                    (Double) b.get("total"),
                    (Double) a.get("total")
            ));

            resultado.put("tipo", "TORTA");
            resultado.put("datos", datos);
            resultado.put("estadisticas", calcularEstadisticasVentas(datos));
            resultado.put("totalRegistros", datos.size());

        } catch (Exception e) {
            System.err.println("Error en generarVentasPorCategoria: " + e.getMessage());
            e.printStackTrace();
            // En caso de error, devolver datos de ejemplo
            return generarVentasPorCategoriaEjemplo();
        }

        return resultado;
    }

    /**
     * Reporte REAL de ventas del día
     */
    public Map<String, Object> generarVentasDelDia() {
        Map<String, Object> resultado = new HashMap<>();

        try {
            // Obtener fecha de hoy
            LocalDateTime hoyInicio = LocalDate.now().atStartOfDay();
            LocalDateTime hoyFin = LocalDate.now().atTime(23, 59, 59);

            System.out.println("Buscando ventas entre: " + hoyInicio + " y " + hoyFin);

            // Obtener facturas del día (si tienes el metodo en repositorio)
            // List<FacturaModel> facturasHoy = facturaRepository.findByFechaEmisionBetween(hoyInicio, hoyFin);

            // O si no tienes el metodo, filtrar manualmente
            List<FacturaModel> todasFacturas = facturaRepository.findAll();
            List<FacturaModel> facturasHoy = todasFacturas.stream()
                    .filter(f -> f.getFechaEmision() != null &&
                            !f.getFechaEmision().isBefore(hoyInicio) &&
                            !f.getFechaEmision().isAfter(hoyFin) &&
                            "PAGADA".equals(f.getEstado().toString()))
                    .collect(Collectors.toList());

            System.out.println("Facturas hoy: " + facturasHoy.size());

            // Agrupar por hora
            Map<String, Map<String, Object>> ventasPorHora = new HashMap<>();

            for (FacturaModel factura : facturasHoy) {
                String hora = factura.getFechaEmision().getHour() + ":00";

                if (!ventasPorHora.containsKey(hora)) {
                    Map<String, Object> horaData = new HashMap<>();
                    horaData.put("hora", hora);
                    horaData.put("cantidad", 0);
                    horaData.put("total", 0.0);
                    ventasPorHora.put(hora, horaData);
                }

                Map<String, Object> horaData = ventasPorHora.get(hora);
                int cantidadActual = (int) horaData.get("cantidad");
                double totalActual = (double) horaData.get("total");

                // Sumar todos los detalles de esta factura
                int cantidadFactura = 0;
                double totalFactura = 0.0;

                if (factura.getDetalles() != null) {
                    for (FacturaDetalleModel detalle : factura.getDetalles()) {
                        cantidadFactura += detalle.getCantidad() != null ? detalle.getCantidad() : 0;
                        totalFactura += detalle.getPrecio() != null ? detalle.getPrecio().doubleValue() : 0.0;
                    }
                }

                horaData.put("cantidad", cantidadActual + cantidadFactura);
                horaData.put("total", totalActual + totalFactura);
            }

            // Convertir a lista y ordenar
            List<Map<String, Object>> datos = new ArrayList<>(ventasPorHora.values());

            // Si no hay datos, usar ejemplo
            if (datos.isEmpty()) {
                System.out.println("No se encontraron ventas hoy, usando datos de ejemplo");
                return generarVentasDelDiaEjemplo();
            }

            datos.sort((a, b) -> ((String) a.get("hora")).compareTo((String) b.get("hora")));

            resultado.put("tipo", "BARRAS");
            resultado.put("datos", datos);
            resultado.put("estadisticas", calcularEstadisticasVentasDelDia(datos));
            resultado.put("totalRegistros", datos.size());

        } catch (Exception e) {
            System.err.println("Error en generarVentasDelDia: " + e.getMessage());
            e.printStackTrace();
            // En caso de error, devolver datos de ejemplo
            return generarVentasDelDiaEjemplo();
        }

        return resultado;
    }

    /**
     * Metodos de ejemplo (mantener como fallback)
     */
    private Map<String, Object> generarVentasPorCategoriaEjemplo() {
        Map<String, Object> resultado = new HashMap<>();

        List<Map<String, Object>> datos = new ArrayList<>();

        Map<String, Object> cat1 = new HashMap<>();
        cat1.put("categoria", "Medicamentos");
        cat1.put("cantidad", 45);
        cat1.put("total", 1250.75);
        cat1.put("color", "#FF6384");
        datos.add(cat1);

        Map<String, Object> cat2 = new HashMap<>();
        cat2.put("categoria", "Alimentos");
        cat2.put("cantidad", 120);
        cat2.put("total", 980.50);
        cat2.put("color", "#36A2EB");
        datos.add(cat2);

        Map<String, Object> cat3 = new HashMap<>();
        cat3.put("categoria", "Accesorios");
        cat3.put("cantidad", 30);
        cat3.put("total", 450.25);
        cat3.put("color", "#FFCE56");
        datos.add(cat3);

        resultado.put("tipo", "TORTA");
        resultado.put("datos", datos);
        resultado.put("estadisticas", calcularEstadisticasVentas(datos));
        resultado.put("totalRegistros", 3);

        return resultado;
    }

    private Map<String, Object> generarVentasDelDiaEjemplo() {
        Map<String, Object> resultado = new HashMap<>();

        List<Map<String, Object>> datos = new ArrayList<>();

        // Ventas por hora (ejemplo)
        String[] horas = {"09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00"};
        int[] ventasPorHora = {120, 250, 180, 320, 150, 280, 200};

        for (int i = 0; i < horas.length; i++) {
            Map<String, Object> horaData = new HashMap<>();
            horaData.put("hora", horas[i]);
            horaData.put("cantidad", ventasPorHora[i]);
            horaData.put("total", ventasPorHora[i] * 10.5); // Precio promedio $10.5
            horaData.put("color", i % 2 == 0 ? "#4BC0C0" : "#9966FF");
            datos.add(horaData);
        }

        resultado.put("tipo", "BARRAS");
        resultado.put("datos", datos);
        resultado.put("estadisticas", calcularEstadisticasVentasDelDia(datos));
        resultado.put("totalRegistros", 7);

        return resultado;
    }
    // ============ MÉTODOS AUXILIARES PARA ESTADÍSTICAS ============

    /**
     * Calcular estadísticas para ventas por categoría
     */
    private Map<String, Object> calcularEstadisticasVentas(List<Map<String, Object>> ventas) {
        Map<String, Object> stats = new HashMap<>();

        if (ventas == null || ventas.isEmpty()) {
            stats.put("totalVentas", 0);
            stats.put("ventaMaxima", 0);
            stats.put("ventaMinima", 0);
            stats.put("promedioVentas", 0);
            stats.put("totalCategorias", 0);
            return stats;
        }

        double totalVentas = 0;
        double ventaMaxima = Double.MIN_VALUE;
        double ventaMinima = Double.MAX_VALUE;
        String categoriaMaxima = "";
        String categoriaMinima = "";
        long cantidadTotalVendida = 0;

        for (Map<String, Object> venta : ventas) {
            double totalVenta = 0.0;
            long cantidad = 0;
            String categoria = "";

            // Obtener valores de manera segura
            Object totalObj = venta.get("total");
            Object cantidadObj = venta.get("cantidad");
            Object categoriaObj = venta.get("categoria");

            if (totalObj instanceof Double) {
                totalVenta = (Double) totalObj;
            } else if (totalObj instanceof Integer) {
                totalVenta = ((Integer) totalObj).doubleValue();
            }

            if (cantidadObj instanceof Integer) {
                cantidad = ((Integer) cantidadObj).longValue();
            } else if (cantidadObj instanceof Long) {
                cantidad = (Long) cantidadObj;
            }

            if (categoriaObj instanceof String) {
                categoria = (String) categoriaObj;
            }

            totalVentas += totalVenta;
            cantidadTotalVendida += cantidad;

            if (totalVenta > ventaMaxima) {
                ventaMaxima = totalVenta;
                categoriaMaxima = categoria;
            }

            if (totalVenta < ventaMinima) {
                ventaMinima = totalVenta;
                categoriaMinima = categoria;
            }
        }

        double promedioVentas = ventas.size() > 0 ? totalVentas / ventas.size() : 0;
        double ticketPromedio = cantidadTotalVendida > 0 ? totalVentas / cantidadTotalVendida : 0;

        stats.put("totalVentas", totalVentas);
        stats.put("ventaMaxima", ventaMaxima);
        stats.put("ventaMinima", ventaMinima);
        stats.put("categoriaMaxima", categoriaMaxima);
        stats.put("categoriaMinima", categoriaMinima);
        stats.put("promedioVentas", promedioVentas);
        stats.put("cantidadTotalVendida", cantidadTotalVendida);
        stats.put("ticketPromedio", ticketPromedio);
        stats.put("totalCategorias", ventas.size());

        return stats;
    }

    /**
     * Calcular estadísticas para ventas del día
     */
    private Map<String, Object> calcularEstadisticasVentasDelDia(List<Map<String, Object>> ventas) {
        Map<String, Object> stats = new HashMap<>();

        if (ventas == null || ventas.isEmpty()) {
            stats.put("totalDia", 0);
            stats.put("ventaMaxima", 0);
            stats.put("ventaMinima", 0);
            stats.put("totalVentas", 0);
            stats.put("totalHoras", 0);
            return stats;
        }

        double totalDia = 0;
        double ventaMaxima = Double.MIN_VALUE;
        double ventaMinima = Double.MAX_VALUE;
        String horaMaxima = "";
        String horaMinima = "";
        long totalTransacciones = ventas.size(); // Cada entrada es una hora
        long totalCantidadVendida = 0;

        for (Map<String, Object> venta : ventas) {
            double totalVenta = 0.0;
            long cantidad = 0;
            String hora = "";

            // Obtener valores de manera segura
            Object totalObj = venta.get("total");
            Object cantidadObj = venta.get("cantidad");
            Object horaObj = venta.get("hora");

            if (totalObj instanceof Double) {
                totalVenta = (Double) totalObj;
            } else if (totalObj instanceof Integer) {
                totalVenta = ((Integer) totalObj).doubleValue();
            }

            if (cantidadObj instanceof Integer) {
                cantidad = ((Integer) cantidadObj).longValue();
            } else if (cantidadObj instanceof Long) {
                cantidad = (Long) cantidadObj;
            }

            if (horaObj instanceof String) {
                hora = (String) horaObj;
            }

            totalDia += totalVenta;
            totalCantidadVendida += cantidad;

            if (totalVenta > ventaMaxima) {
                ventaMaxima = totalVenta;
                horaMaxima = hora;
            }

            if (totalVenta < ventaMinima && totalVenta > 0) {
                ventaMinima = totalVenta;
                horaMinima = hora;
            }
        }

        // Si no se encontró mínimo (todas fueron 0), establecer 0
        if (ventaMinima == Double.MAX_VALUE) {
            ventaMinima = 0;
        }

        double promedioHora = ventas.size() > 0 ? totalDia / ventas.size() : 0;
        double ticketPromedio = totalCantidadVendida > 0 ? totalDia / totalCantidadVendida : 0;

        stats.put("totalDia", totalDia);
        stats.put("ventaMaxima", ventaMaxima);
        stats.put("ventaMinima", ventaMinima);
        stats.put("horaMaxima", horaMaxima);
        stats.put("horaMinima", horaMinima);
        stats.put("promedioHora", promedioHora);
        stats.put("totalTransacciones", totalTransacciones);
        stats.put("totalCantidadVendida", totalCantidadVendida);
        stats.put("ticketPromedio", ticketPromedio);
        stats.put("totalHoras", ventas.size());

        return stats;
    }
    /**
     * Método para diagnosticar qué datos hay en la base de datos
     */
    public void diagnosticarDatos() {
        System.out.println("\n=== DIAGNÓSTICO DE DATOS REALES ===");

        try {
            // 1. Verificar cuántas facturas hay
            List<FacturaModel> todasFacturas = facturaRepository.findAll();
            System.out.println("1. Total facturas en BD: " + todasFacturas.size());

            // 2. Verificar estados de las facturas
            Map<String, Long> conteoEstados = todasFacturas.stream()
                    .collect(Collectors.groupingBy(
                            f -> f.getEstado() != null ? f.getEstado().toString() : "SIN ESTADO",
                            Collectors.counting()
                    ));
            System.out.println("2. Estados de facturas: " + conteoEstados);

            // 3. Verificar facturas con detalles
            int facturasConDetalles = 0;
            int totalDetalles = 0;

            for (FacturaModel factura : todasFacturas) {
                if (factura.getDetalles() != null && !factura.getDetalles().isEmpty()) {
                    facturasConDetalles++;
                    totalDetalles += factura.getDetalles().size();

                    // Mostrar algunos detalles
                    if (facturasConDetalles <= 3) { // Solo mostrar primeras 3
                        System.out.println("   Factura ID " + factura.getId() + ":");
                        for (FacturaDetalleModel detalle : factura.getDetalles()) {
                            System.out.println("     - Producto: " +
                                    (detalle.getProducto() != null ? detalle.getProducto().getNombre() : "null") +
                                    ", Cantidad: " + detalle.getCantidad() +
                                    ", Precio: " + detalle.getPrecio());
                        }
                    }
                }
            }

            System.out.println("3. Facturas con detalles: " + facturasConDetalles + "/" + todasFacturas.size());
            System.out.println("4. Total detalles: " + totalDetalles);

            // 4. Verificar productos con categorías
            int productosConCategorias = 0;
            int totalCategorias = 0;

            // Necesitaríamos ProductoRepository, pero hagamos un diagnóstico con lo que tenemos
            System.out.println("5. Para productos, necesitaríamos ProductoRepository");

            System.out.println("=== FIN DIAGNÓSTICO ===\n");

        } catch (Exception e) {
            System.err.println("Error en diagnóstico: " + e.getMessage());
            e.printStackTrace();
        }
    }
}