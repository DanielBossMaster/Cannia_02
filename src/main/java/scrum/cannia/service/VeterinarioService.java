package scrum.cannia.service;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import scrum.cannia.model.VeterinariaModel;
import scrum.cannia.model.VeterinarioModel;
import scrum.cannia.repository.VeterinariaRepository;
import scrum.cannia.repository.VeterinarioRepository;

@AllArgsConstructor
@Service
public class VeterinarioService {

    private final VeterinarioRepository veterinarioRepo;
    private final VeterinariaRepository veterinariaRepo;

//Para obtener veterinario por id

    public VeterinarioModel obtenerVeterinario(long id) {
        return veterinarioRepo.findById(id).orElse(null);
    }


//Para verificar si ya tiene una veterinaria asociada

    public boolean tieneVeterinaria(long id) {
        VeterinarioModel vet = obtenerVeterinario(id);
        return vet != null && vet.getVeterinaria() != null;
    }


    //Para obtener la veterinaria de un veterinario
    public VeterinariaModel obtenerVeterinariaDeVeterinario(long id) {
        VeterinarioModel vet = obtenerVeterinario(id);
        return (vet != null) ? vet.getVeterinaria() : null;
    }


    //Crear nueva veterinaria para un veterinario
    public VeterinariaModel crearVeterinaria(long vetid, VeterinariaModel nuevaVeterinaria) {
        VeterinarioModel vet = obtenerVeterinario(vetid);
        if (vet != null && vet.getVeterinaria() == null) {

            nuevaVeterinaria.setVeterinario(vet);
            vet.setVeterinaria(nuevaVeterinaria);

            return veterinariaRepo.save(nuevaVeterinaria);
        }
        return null;// Si ya se creo la veterinaria
    }

    public VeterinarioModel buscarPorUsuario(String username) {
        return veterinarioRepo.findByUsuarioUsuario(username)
                .orElseThrow(() ->
                        new IllegalStateException("Veterinario no encontrado"));
    }

    public VeterinarioModel actualizarCampo(
            String username,
            String campo,
            String valor
    ) {
        VeterinarioModel veterinario =
                buscarPorUsuario(username);

        switch (campo) {
            case "numLicencia":
                veterinario.setNumLicencia(valor);
                break;
            case "nombreVete":
                veterinario.setNombreVete(valor);
                break;
            case "apellidoVete":
                veterinario.setApellidoVete(valor);
                break;
            case "direccionVete":
                veterinario.setDireccionVete(valor);
                break;
            case "telefonoVete":
                veterinario.setTelefonoVete(valor);
                break;
            case "correoVete":
                veterinario.setCorreoVete(valor);
                break;
            default:
                throw new IllegalArgumentException("Campo no válido");
        }
        return veterinarioRepo.save(veterinario);
    }
}