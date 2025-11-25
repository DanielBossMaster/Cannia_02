package scrum.cannia.service;

import scrum.cannia.model.FundacionModel;
import scrum.cannia.repository.FundacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FundacionService {

    @Autowired
    private FundacionRepository fundacionRepository;


    public List<FundacionModel> listarFundaciones() {
        return fundacionRepository.findAll();
    }


    public FundacionModel obtenerFundacionPorId(Long id) {
        return fundacionRepository.findById(id).orElse(null);
    }


    public FundacionModel guardarFundacion(FundacionModel fundacion) {
        return fundacionRepository.save(fundacion);
    }


    public FundacionModel actualizarFundacion(Long id, FundacionModel fundacion) {

        FundacionModel existente = obtenerFundacionPorId(id);

        if (existente == null) {
            return null;
        }

        existente.setNombre(fundacion.getNombre());
        existente.setDescripcion(fundacion.getDescripcion());
        existente.setDireccion(fundacion.getDireccion());
        existente.setTelefono(fundacion.getTelefono());
        existente.setEmail(fundacion.getEmail());
        existente.setSitioWeb(fundacion.getSitioWeb());
        existente.setLogo(fundacion.getLogo());

        return fundacionRepository.save(existente);
    }


    public void eliminarFundacion(Long id) {
        fundacionRepository.deleteById(id);
    }
}