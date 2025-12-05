package scrum.cannia.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import scrum.cannia.model.PetModel;
import scrum.cannia.repository.PetRepository;

import java.util.List;

@Service
public class PetService {

    @Autowired
    private PetRepository petRepository;

    public void guardarMascotas(List<PetModel> mascotas) {
        petRepository.saveAll(mascotas);
    }
}
