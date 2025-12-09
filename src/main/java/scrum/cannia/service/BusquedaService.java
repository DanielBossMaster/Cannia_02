package scrum.cannia.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import scrum.cannia.Dto.ProductoBusquedaDto;
import scrum.cannia.model.ProductoModel;
import scrum.cannia.repository.BusquedaRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BusquedaService {

  @Autowired
  BusquedaRepository busquedaRepository;

    public List<ProductoBusquedaDto> buscarProductos(String query, Long idCategoria) {

        List<ProductoModel> productosEncontrados;

        if (idCategoria != null) {
            // Usa la consulta JPQL con filtros
            productosEncontrados = busquedaRepository.buscarProductosPorFiltros(idCategoria, query);
        } else {
            // Búsqueda simple (ejemplo)
            productosEncontrados = busquedaRepository.findByNombreContainingIgnoreCase(query);
        }

        // 1. Convertir cada ProductoModel encontrado a un ProductoBusquedaDTO
        return productosEncontrados.stream()
                .map(ProductoBusquedaDto::new) // Usa el constructor del DTO
                .collect(Collectors.toList());
        // ------------------------------------
    }
}