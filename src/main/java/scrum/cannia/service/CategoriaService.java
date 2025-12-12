// src/main/java/scrum/cannia/service/CategoriaService.java
package scrum.cannia.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import scrum.cannia.model.CategoriaModel;
import scrum.cannia.repository.CategoriaRepository;
import scrum.cannia.repository.ProductoRepository;

import java.util.List;
import java.util.Optional;

@Service
public class CategoriaService {

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private ProductoRepository productoRepository;


    /**
     * Guarda o actualiza una categoría en la base de datos.
     * @param categoria El objeto CategoriaModel a guardar.
     * @return La categoría guardada (con ID asignado si es nueva).
     */
    public CategoriaModel guardar(CategoriaModel categoria) {
        // En este nivel puedes añadir validaciones de negocio antes de guardar
        return categoriaRepository.save(categoria);
    }

    /**
     * Obtiene una categoría por su ID.
     * @param id El ID de la categoría a buscar.
     * @return El objeto CategoriaModel, o lanza una excepción si no se encuentra.
     */
    public CategoriaModel obtenerPorId(Long id) {
        // Usamos Optional para manejar la posibilidad de que la categoría no exista
        Optional<CategoriaModel> optionalCategoria = categoriaRepository.findById(id);

        if (optionalCategoria.isEmpty()) {
            // Manejo de errores: Puedes cambiar esto por una excepción personalizada si lo deseas.
            throw new RuntimeException("Categoría con ID " + id + " no encontrada.");
        }
        return optionalCategoria.get();
    }

    /**
     * Lista todas las categorías existentes.
     * @return Lista de CategoriaModel.
     */
    public List<CategoriaModel> listarTodas() {
        return categoriaRepository.findAll();
    }


    @Transactional
    public void eliminar(Long idCategoria) {

        // Paso 1: Desasociar los productos (la función en el repository espera Long)
        productoRepository.desasociarProductosDeCategoria(idCategoria);

        // Paso 2: Eliminar la categoría (el deleteById en JpaRepository para Long funciona)
        categoriaRepository.deleteById(idCategoria);
    }
}


