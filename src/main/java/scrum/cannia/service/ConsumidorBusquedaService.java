// src/main/java/scrum/cannia/service/ConsumidorBusquedaService.java
package scrum.cannia.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import scrum.cannia.Dto.ProductoBusquedaDto; // Importar el DTO
import java.util.List;

@Service
public class ConsumidorBusquedaService {

    private final WebClient busquedaWebClient;

    // Inyectamos el WebClient que configuramos
    public ConsumidorBusquedaService(@Qualifier("busquedaWebClient") WebClient busquedaWebClient) {
        this.busquedaWebClient = busquedaWebClient;
    }

    /**
     * CONSUME EL WEB SERVICE DE BÚSQUEDA EXTERNO.
     * Retorna una lista de ProductoBusquedaDTO (JSON limpio).
     */
    public List<ProductoBusquedaDto> obtenerProductosFiltrados(String query, Long idCategoria) {

        return busquedaWebClient.get()
                // Construye la URL con los parámetros de búsqueda
                .uri(uriBuilder -> uriBuilder
                        .path("/api/busqueda/productos")
                        .queryParam("q", query)
                        .queryParam("idCategoria", idCategoria)
                        .build())
                .retrieve()
                .bodyToFlux(ProductoBusquedaDto.class) // <--- CAMBIO CLAVE: Usamos el DTO aquí
                .collectList()
                .block();
    }
}