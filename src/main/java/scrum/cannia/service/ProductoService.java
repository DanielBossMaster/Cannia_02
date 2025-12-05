package scrum.cannia.service;

import org.springframework.web.multipart.MultipartFile;
import scrum.cannia.model.ProductoModel;
import scrum.cannia.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
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

    //Guardar productos
    public void guardar(ProductoModel producto, MultipartFile archivo) {
        try {
            if (!archivo.isEmpty()) {
                producto.setFoto(archivo.getBytes());
            }
            productoRepository.save(producto);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // ============================================
    //         EDITAR PRODUCTO
    // ============================================
    public void actualizar(ProductoModel producto, MultipartFile archivo) {
        ProductoModel original = productoRepository.findById(producto.getId()).orElse(null);
        if (original == null) return;

        original.setNombre(producto.getNombre());
        original.setDescripcion(producto.getDescripcion());
        original.setCantidad(producto.getCantidad());
        original.setValor(producto.getValor());
        original.setUnidadMedida(producto.getUnidadMedida());
        original.setEstado(producto.isEstado());

        if (archivo != null && !archivo.isEmpty()) {
            try {
                original.setFoto(archivo.getBytes());
            } catch (IOException e) {
                throw new RuntimeException("Error procesando la imagen", e);
            }
        }

        productoRepository.save(original);
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