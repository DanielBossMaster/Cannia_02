package scrum.cannia.service;

import org.springframework.web.multipart.MultipartFile;
import scrum.cannia.model.ProductoModel;
import scrum.cannia.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;

    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    public static List<Producto> listarTodos() {
        return productoRepository.findAll();
    }

    public static <Producto> void guardar(Producto producto, MultipartFile archivo) {
    }

    public List<ProductoModel> obtenerProductosActivos() {
        return productoRepository.findByEstadoTrue();
    }

    public ProductoModel guardarProducto(ProductoModel producto) {
        return productoRepository.save(producto);
    }
}
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

