package scrum.cannia.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import scrum.cannia.model.AgendaModel;
import scrum.cannia.model.PropietarioModel;
import scrum.cannia.repository.AgendaRepository;

import java.util.List;

@Service
public class AgendaService {

    @Autowired
    private AgendaRepository agendaRepository;

    public List<AgendaModel> citasPorPropietario (PropietarioModel propietario){
    return agendaRepository.findByPropietario(propietario);
}


}
