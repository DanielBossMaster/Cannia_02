// src/main/java/scrum/cannia/service/CategoriaService.java
package scrum.cannia.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import scrum.cannia.model.CategoriaModel;
import scrum.cannia.repository.CategoriaRepository;
import scrum.cannia.repository.ProductoRepository;

import java.util.List;
import java.util.Optional;
@AllArgsConstructor
@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final ProductoRepository productoRepository;

    public CategoriaModel guardar(CategoriaModel categoria) {
        return categoriaRepository.save(categoria);
    }

    public CategoriaModel obtenerPorId(Long id) {
        Optional<CategoriaModel> optionalCategoria = categoriaRepository.findById(id);

        if (optionalCategoria.isEmpty()) {
            throw new RuntimeException("Categoría con ID " + id + " no encontrada.");
        }
        return optionalCategoria.get();
    }

    public List<CategoriaModel> listarTodas() {
        return categoriaRepository.findAll();
    }

    @Transactional
    public void eliminar(Long idCategoria) {
        productoRepository.desasociarProductosDeCategoria(idCategoria);
        categoriaRepository.deleteById(idCategoria);
    }
}


