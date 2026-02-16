package scrum.cannia.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import scrum.cannia.Dto.ProductoBusquedaDto;
import scrum.cannia.model.ProductoModel;
import scrum.cannia.model.VeterinariaModel;
import scrum.cannia.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import scrum.cannia.repository.VeterinariaRepository;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final VeterinariaRepository veterinariaRepository;

    // ===============================
    //    PARA CARRITO CONTROLLER
    // ===============================

    public ProductoModel buscarPorId(Integer id) {
        return productoRepository.findById(id).orElse(null);}

    // ===============================
    //           GUARDAR
    // ===============================

    public void guardarProductoVeterinaria(
            ProductoModel producto,
            MultipartFile archivo,
            VeterinariaModel veterinaria
    ) {

        try {
            // 1. Asociar veterinaria (CRÍTICO)
            producto.setVeterinaria(veterinaria);

            // 2. Valores por defecto (opcional pero recomendado)
            producto.setEstado(true);

            if (producto.getCategorias() == null) {
                producto.setCategorias(List.of());
            }

            // 3. Imagen
            if (archivo != null && !archivo.isEmpty()) {
                producto.setFoto(archivo.getBytes());
            }

            // 4. Guardar
            productoRepository.save(producto);

        } catch (Exception e) {
            throw new RuntimeException("Error al guardar producto", e);
        }
    }

    // ===============================
    //        LISTAR INVENTARIO
    // ===============================
    public Page<ProductoModel> listarPorVeterinaria(
            Integer veterinariaId,
            int page,
            int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return productoRepository.findByVeterinaria_Id(veterinariaId, pageable);
    }

    public Page<ProductoModel> listarActivosPaginado(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productoRepository.findByEstadoTrue(pageable);
    }


    public List<ProductoBusquedaDto> obtenerProductosActivosFiltrados(String q, Long idCategoria) {

        List<ProductoModel> productosModel;

        if (q != null && idCategoria != null) {
            productosModel = productoRepository
                    .findByNombreOrDescripcionAndCategoriaIdAndEstadoTrue(q, idCategoria);
        } else if (q != null) {
            productosModel = productoRepository
                    .findByNombreOrDescripcionContainingAndEstadoTrue(q);
        } else if (idCategoria != null) {
            productosModel = productoRepository
                    .findByCategoriaIdAndEstadoTrue(idCategoria);
        } else {
            // Caso base: Obtener TODOS los productos con estado=TRUE
            productosModel = productoRepository
                    .findByEstadoTrue();
        }
        // --- 2. Mapeo a DTO (en memoria) ---
        return productosModel.stream()
                .map(ProductoBusquedaDto::new)
                .collect(Collectors.toList());
    }


    // ============================================
    //              EDITAR PRODUCTO
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


    // ===============================
    //      ACTUALIZAR PRODUCTO
    // ===============================
    public void actualizarProductoVeterinaria(
            ProductoModel productoForm,
            MultipartFile imagen,
            Integer veterinariaId
    ) throws IOException {

        ProductoModel existente = productoRepository
                .findByIdAndVeterinaria_Id(productoForm.getId(), veterinariaId)
                .orElseThrow(() ->
                        new RuntimeException("Producto no autorizado"));

        existente.setNombre(productoForm.getNombre());
        existente.setDescripcion(productoForm.getDescripcion());
        existente.setValor(productoForm.getValor());
        existente.setCategorias(productoForm.getCategorias());

        if (imagen != null && !imagen.isEmpty()) {
            existente.setFoto(imagen.getBytes());
        }

        productoRepository.save(existente);
    }


    public ProductoModel obtenerProductoVeterinaria(
            int productoId,
            Integer veterinariaId
    ) {
        return productoRepository
                .findByIdAndVeterinaria_Id(productoId, veterinariaId)
                .orElseThrow(() -> new RuntimeException("Producto no autorizado"));
    }
}
