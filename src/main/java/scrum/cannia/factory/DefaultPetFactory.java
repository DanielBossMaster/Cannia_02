package scrum.cannia.factory;

import org.springframework.stereotype.Component;
import scrum.cannia.model.PetModel;

@Component
public class DefaultPetFactory implements PetFactory {

    @Override
    public PetModel crearMascota(String nombre, String raza, int edad, String color) {
// Se implemento el builder
        return PetModel.builder()
                .nombrePet(nombre)
                .razaPet(raza)
                .edadPet(edad)
                .colorPet(color)
                .build();
    }
}
