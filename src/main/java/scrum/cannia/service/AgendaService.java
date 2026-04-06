package scrum.cannia.service;


import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import scrum.cannia.model.AgendaModel;
import scrum.cannia.model.PropietarioModel;
import scrum.cannia.model.ServicioModel;
import scrum.cannia.model.VeterinariaModel;
import scrum.cannia.repository.AgendaRepository;
import scrum.cannia.repository.ServicioRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@AllArgsConstructor
@Service
public class AgendaService {

    private final AgendaRepository agendaRepository;
    private final ServicioRepository servicioRepository;


    public void agendarServicio(
            Integer servicioId,
            LocalDate fecha,
            LocalTime hora,
            PropietarioModel propietario
    ) {

        ServicioModel servicio = servicioRepository.findById(servicioId)
                .orElseThrow(() -> new RuntimeException("Servicio no encontrado"));

        VeterinariaModel veterinaria = servicio.getVeterinaria();

        boolean ocupado = agendaRepository.existsByServicioAndFechaAndHoraAndEstado(
                servicio,
                fecha,
                hora,
                "AGENDADA"
        );

        if (ocupado) {
            throw new RuntimeException("HORARIO_OCUPADO");
        }

        AgendaModel agenda = new AgendaModel();
        agenda.setServicio(servicio);
        agenda.setVeterinaria(veterinaria);
        agenda.setPropietario(propietario);
        agenda.setFecha(fecha);
        agenda.setHora(hora);
        agenda.setEstado("AGENDADA");

        agendaRepository.save(agenda);
    }

    public List<AgendaModel> citasPorPropietario(PropietarioModel propietario) {
        return agendaRepository.findByPropietario(propietario);
    }

    public List<AgendaModel> citasPorVeterinaria(
            VeterinariaModel veterinaria
    ){
        return agendaRepository
                .findByVeterinariaOrderByFechaDescHoraDesc(
                        veterinaria
                );
    }
}

