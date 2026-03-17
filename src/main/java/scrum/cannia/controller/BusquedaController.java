package scrum.cannia.controller;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import scrum.cannia.Dto.ProductoBusquedaDto; // Importa el DTO
import scrum.cannia.service.BusquedaService;
import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/busqueda")
public class BusquedaController {


    private final BusquedaService busquedaService;


    @GetMapping("/productos")
    public List<ProductoBusquedaDto> buscar(
            @RequestParam(name = "q", required = false, defaultValue = "") String query,
            @RequestParam(name = "idCategoria", required = false) Long idCategoria) {

        return busquedaService.buscarProductos(query, idCategoria);
        // Spring automáticamente serializa la lista de DTOs a un JSON limpio
    }
}