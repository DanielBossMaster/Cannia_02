package scrum.cannia.service.creator;

import scrum.cannia.Dto.MascotaCargaDTO;
import scrum.cannia.model.FundacionModel;
import scrum.cannia.model.MascotaModel;
import scrum.cannia.model.TipoEstadoMascota;

public class MascotaFundacionCreator extends MascotaCreator {

    private final MascotaCargaDTO dto;
    private final FundacionModel fundacion;

    public MascotaFundacionCreator(MascotaCargaDTO dto,
                                   FundacionModel fundacion) {
        this.dto = dto;
        this.fundacion = fundacion;
    }

    @Override
    protected void asignarDatosComunes(MascotaModel mascota) {
        mascota.setNomMascota(dto.getNombre());
        mascota.setEspecie(dto.getEspecie());
        mascota.setRaza(dto.getRaza());
        mascota.setGenero(dto.getGenero());
        mascota.setColor(dto.getColor());
    }

    @Override
    protected void asignarDatosParticulares(MascotaModel mascota) {
        mascota.setFundacion(fundacion);
        mascota.setPropietario(null);
        mascota.setTipoEstado(TipoEstadoMascota.DISPONIBLE);
        mascota.setEdadFundacion(String.valueOf(dto.getEdad()));
        mascota.setMedicamento(dto.getMedicamento());
    }
}
