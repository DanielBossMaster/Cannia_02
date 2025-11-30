package scrum.cannia.service;

import scrum.cannia.model.ProductoModel;
import scrum.cannia.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    public List<ProductoModel> obtenerTodosProductos() {
        return productoRepository.findAll();
    }
    public ProductoModel buscarPorId(Integer id) {
        return productoRepository.findById(id).orElse(null);
    }
    public List<ProductoModel> listarTodos() {
        return productoRepository.findAll();
    }

    public List<ProductoModel> obtenerProductosActivos() {
        return productoRepository.findByEstadoTrue();
    }
//
//    public ProductoModel guardarProducto(ProductoModel producto) {
//        return productoRepository.save(producto);
//    }
//
//    public Optional<ProductoModel> obtenerProductoPorId(Integer id) {
//        return productoRepository.findById(id);
//    }
//    public void eliminarProductoLogicamente(Integer id) {
//        productoRepository.findById(id).ifPresent(producto -> {
//            producto.setEstado(false);
//            productoRepository.save(producto);
//        });
//    }

//

}