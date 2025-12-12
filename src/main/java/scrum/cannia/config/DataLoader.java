package scrum.cannia.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import scrum.cannia.model.PropietarioModel;
import scrum.cannia.model.MascotaModel;
import scrum.cannia.model.Genero;
import scrum.cannia.model.TipoEstadoMascota;
import scrum.cannia.repository.PropietarioRepository;
import scrum.cannia.repository.MascotaRepository;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Component
public class DataLoader implements CommandLineRunner {

    private final PropietarioRepository propietarioRepository;
    private final MascotaRepository mascotaRepository;

    public DataLoader(PropietarioRepository propietarioRepository, MascotaRepository mascotaRepository) {
        this.propietarioRepository = propietarioRepository;
        this.mascotaRepository = mascotaRepository;
    }

    @Override
    public void run(String... args) throws Exception {

        if (propietarioRepository.count() == 0) {

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            List<PropietarioModel> propietarios = new ArrayList<>();

            // 🧍 Propietarios de ejemplo (ID = null → se genera automático)
            propietarios.add(new PropietarioModel(null, "1001", "Ana", "Torres", "Calle 10 #5-22",
                    "3001112233", "ana@gmail.com", true, new ArrayList<>(), null, null));

            propietarios.add(new PropietarioModel(null, "1002", "Carlos", "Pérez", "Carrera 12 #8-40",
                    "3012223344", "carlos@gmail.com", true, new ArrayList<>(), null,null));

            propietarios.add(new PropietarioModel(null, "1003", "Laura", "Gómez", "Av. 6 #20-11",
                    "3023334455", "laura@gmail.com", true, new ArrayList<>(), null,null));

            propietarios.add(new PropietarioModel(null, "1004", "Pedro", "Ruiz", "Calle 15 #3-55",
                    "3034445566", "pedro@gmail.com", true, new ArrayList<>(), null,null));

            propietarios.add(new PropietarioModel(null, "1005", "Sofía", "Castro", "Carrera 9 #12-33",
                    "3045556677", "sofia@gmail.com", true, new ArrayList<>(), null,null));

            propietarios.add(new PropietarioModel(null, "1006", "David", "Mora", "Av. 3 #45-22",
                    "3056667788", "david@gmail.com", true, new ArrayList<>(), null,null));

            propietarios.add(new PropietarioModel(null, "1007", "María", "López", "Calle 8 #9-21",
                    "3067778899", "maria@gmail.com", true, new ArrayList<>(), null,null));

            propietarios.add(new PropietarioModel(null, "1008", "Luis", "Rojas", "Carrera 4 #7-10",
                    "3078889900", "luis@gmail.com", true, new ArrayList<>(), null,null));

            // Guardar propietarios
            propietarioRepository.saveAll(propietarios);

            // 🐾 Crear mascotas
            for (PropietarioModel propietario : propietarios) {
                List<MascotaModel> mascotas = new ArrayList<>();

                MascotaModel m1 = new MascotaModel();
                m1.setNomMascota("Firulais");
                m1.setEspecie("Perro");
                m1.setRaza("Labrador");
                m1.setFechaNacimiento(sdf.parse("2020-03-10"));
                m1.setFechaVacunacion(sdf.parse("2024-08-15"));
                m1.setMedicamento("Antiparasitario");
                m1.setColor("Marrón");
                m1.setGenero(Genero.MACHO);
                m1.setPropietario(propietario);
                m1.setTipoEstado(TipoEstadoMascota.PROPIA);
                m1.setFoto("");
                m1.setEdadFundacion("0");
                m1.setFundacion(null);

                MascotaModel m2 = new MascotaModel();
                m2.setNomMascota("Mishi");
                m2.setEspecie("Gato");
                m2.setRaza("Siames");
                m2.setFechaNacimiento(sdf.parse("2021-06-01"));
                m2.setFechaVacunacion(sdf.parse("2024-05-20"));
                m2.setMedicamento("Vacuna triple felina");
                m2.setColor("Gris");
                m2.setGenero(Genero.HEMBRA);
                m2.setPropietario(propietario);
                m2.setTipoEstado(TipoEstadoMascota.PROPIA);
                m2.setFoto("");
                m2.setEdadFundacion("0");
                m2.setFundacion(null);

                mascotas.add(m1);

                // Si propietario tiene ID par → agregar otra mascota
                if (propietario.getId() % 2 == 0) {
                    mascotas.add(m2);
                }

                mascotaRepository.saveAll(mascotas);

                propietario.setMascotas(mascotas);
                propietarioRepository.save(propietario);
            }

            System.out.println("✅ Datos de prueba cargados correctamente (8 propietarios + mascotas).");
        } else {
            System.out.println("ℹ️ Datos ya existen, no se cargaron nuevamente.");
        }
    }
}
