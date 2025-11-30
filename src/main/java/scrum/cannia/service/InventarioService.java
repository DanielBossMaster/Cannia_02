package scrum.cannia.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class InventarioService {

//    @Autowired
//    private InventarioRepository inventarioRepository;
//
//    public List<InventarioModel> obtenerTodoInventario() {
//        return inventarioRepository.findAll();
//    }
//
//    public InventarioModel guardarInventario(InventarioModel inventario) {
//        inventario.setFechaActualizacion(java.time.LocalDate.now());
//        return inventarioRepository.save(inventario);
//    }
//
//    public Map<String, Object> obtenerDatosGraficos() {
//        Map<String, Object> datos = new HashMap<>();
//
//        // Alertas de stock
//        List<InventarioModel> stockCritico = inventarioRepository.findByStockActualLessThan(10);
//        List<InventarioModel> stockNormal = inventarioRepository.findByStockActualBetween(10, 50);
//        List<InventarioModel> stockAlto = inventarioRepository.findByStockActualBetween(51, 100);
//
//        datos.put("stockCritico", stockCritico);
//        datos.put("stockNormal", stockNormal);
//        datos.put("stockAlto", stockAlto);
//        datos.put("totalProductos", inventarioRepository.count());
//
//        return datos;
//    }
}