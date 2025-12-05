package scrum.cannia.factory;

import scrum.cannia.model.PetModel;

public interface PetFactory {
    PetModel crearMascota(String nombre, String raza, int edad, String color);
}
