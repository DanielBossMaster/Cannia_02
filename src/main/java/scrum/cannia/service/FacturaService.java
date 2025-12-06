package scrum.cannia.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import scrum.cannia.model.EstadoFactura;
import scrum.cannia.model.FacturaModel;
import scrum.cannia.repository.FacturaRepository;

import java.util.List;

@Service
public class FacturaService {

    @Autowired
    private FacturaRepository facturaRepository;

    public List<FacturaModel> obtenerVentasVeterinaria(Integer veterinariaId) {
        return facturaRepository.findByVeterinaria_IdOrderByFechaEmisionDesc(veterinariaId);
    }

    @Transactional
    public void cambiarEstado(Long facturaId, EstadoFactura nuevoEstado) {
        FacturaModel factura = facturaRepository.findById(facturaId)
                .orElseThrow();

        factura.setEstado(nuevoEstado);
    }
}
