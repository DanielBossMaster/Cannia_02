package scrum.cannia.controller;

import scrum.cannia.model.ProductoModel;
import scrum.cannia.model.ServicioModel;
import scrum.cannia.service.ProductoService;
import scrum.cannia.service.ServicioService;
import scrum.cannia.service.InventarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/inventario")
public class ApiController {

    @Autowired
    private ProductoService productoService;

    @Autowired
    private ServicioService servicioService;

    @Autowired
    private InventarioService inventarioService;

    // Web Services para Productos
    @GetMapping("/productos")
    public List<ProductoModel> obtenerProductos() {
        return productoService.obtenerProductosActivos();
    }

    @PostMapping("/productos")
    public ProductoModel crearProducto(@RequestBody ProductoModel producto) {
        return productoService.guardarProducto(producto);
    }

    @PutMapping("/productos/{id}")
    public ProductoModel actualizarProducto(@PathVariable Integer id, @RequestBody ProductoModel producto) {
        producto.setId(id);
        return productoService.guardarProducto(producto);
    }

    @DeleteMapping("/productos/{id}")
    public void eliminarProducto(@PathVariable Integer id) {
        productoService.eliminarProductoLogicamente(id);
    }

    // Web Services para Servicios
    @GetMapping("/servicios")
    public List<ServicioModel> obtenerServicios() {
        return servicioService.obtenerTodosServicios();
    }

    @PostMapping("/servicios")
    public ServicioModel crearServicio(@RequestBody ServicioModel servicio) {
        return servicioService.guardarServicio(servicio);
    }

    // Web Services para datos del dashboard
    @GetMapping("/dashboard")
    public Map<String, Object> obtenerDashboard() {
        return inventarioService.obtenerDatosGraficos();
    }
}