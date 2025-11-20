package scrum.cannia.service.VeterinarioService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import scrum.cannia.model.ProductoModel;
import scrum.cannia.repository.ProductoRepository;

import java.util.List;

@Service
public class ProductoService {

@Autowired
    private ProductoRepository productoRepository;


    public List<ProductoModel> listarTodos() {
        return productoRepository.findAll();
    }

    public ProductoModel guardar(ProductoModel p) {
        return productoRepository.save(p);
    }

    public ProductoModel buscarPorId(Integer id) {
        return productoRepository.findById(id).orElse(null);
    }
}
