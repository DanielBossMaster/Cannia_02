package scrum.cannia.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import scrum.cannia.model.MascotaModel;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class ResultadoCargaMascotasDTO {

    private List<MascotaModel> guardadas;
    private List<ErrorCargaDTO> errores;
}
