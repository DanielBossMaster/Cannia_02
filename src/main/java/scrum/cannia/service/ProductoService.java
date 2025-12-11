package scrum.cannia.service;

import org.springframework.web.multipart.MultipartFile;
import scrum.cannia.Dto.ProductoBusquedaDto;
import scrum.cannia.model.ProductoModel;
import scrum.cannia.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductoService {

    @Autowired
    private final ProductoRepository productoRepository;

    public ProductoService(ProductoRepository productoRepository) {this.productoRepository = productoRepository;}

    public  List<ProductoModel> listarTodos() { return productoRepository.findAll();}

    public static <Producto> void guardar (Producto producto, MultipartFile archivo) {}

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



    public List<ProductoBusquedaDto> obtenerProductosActivosFiltrados(String q, Long idCategoria) {

        List<ProductoModel> productosModel;

        // --- 1. Lógica de Filtrado (en la BD) ---
        // Usamos los métodos del Repository que creamos en el paso anterior
        if (q != null && idCategoria != null) {
            productosModel = productoRepository.findByNombreOrDescripcionAndCategoriaIdAndEstadoTrue(q, idCategoria);
        } else if (q != null) {
            productosModel = productoRepository.findByNombreOrDescripcionContainingAndEstadoTrue(q);
        } else if (idCategoria != null) {
            productosModel = productoRepository.findByCategoriaIdAndEstadoTrue(idCategoria);
        } else {
            // Caso base: Obtener TODOS los productos con estado=TRUE
            productosModel = productoRepository.findByEstadoTrue();
        }

        // --- 2. Mapeo a DTO (en memoria) ---
        // Usamos el constructor que creaste en ProductoBusquedaDto
        return productosModel.stream()
                .map(ProductoBusquedaDto::new)
                .collect(Collectors.toList()); // Usar .toList() si estás en Java 16+
    }

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

    public void actualizarC(ProductoModel producto, MultipartFile archivo) {

        ProductoModel original = productoRepository.findById(producto.getId()).orElse(null);
        if (original == null) return;

        original.setNombre(producto.getNombre());
        original.setDescripcion(producto.getDescripcion());

        original.setValor(producto.getValor());



        if (producto.getCategorias() != null) {
            original.setCategorias(producto.getCategorias());
        }

        if (archivo != null && !archivo.isEmpty()) {
            try {
                original.setFoto(archivo.getBytes());
            } catch (IOException e) {
                throw new RuntimeException("Error procesando la imagen", e);
            }
        }

        // 5. Guardar los cambios (JPA gestiona la tabla @ManyToMany)
        productoRepository.save(original);
    }


}
