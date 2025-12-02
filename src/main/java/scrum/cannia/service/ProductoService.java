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

    @Autowired
    private final ProductoRepository productoRepository;

    public ProductoService(ProductoRepository productoRepository)
    {this.productoRepository = productoRepository;}

    public  List<ProductoModel> listarTodos()
    { return productoRepository.findAll();}

    public static <Producto> void guardar
            (Producto producto, MultipartFile archivo) {}

    public List<ProductoModel> obtenerProductosActivos() {
        return productoRepository.findByEstadoTrue();
    }

    public ProductoModel guardarProducto(ProductoModel producto) {
        return productoRepository.save(producto);
    }
    // Se utiliza en el carrito de compras, no tocar  | |
    //                                               \   /
    //                                                \ /
    public ProductoModel buscarPorId(Integer id)
    {return productoRepository.findById(id).orElse(null);}





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