package scrum.cannia.service.VeterinarioService;

import scrum.cannia.model.FundacionModel;
import java.util.List;

public interface FundacionService {

    List<FundacionModel> listarFundaciones();

    FundacionModel obtenerFundacionPorId(Long id);

    FundacionModel guardarFundacion(FundacionModel fundacion);

    FundacionModel actualizarFundacion(Long id, FundacionModel fundacion);

    void eliminarFundacion(Long id);
}