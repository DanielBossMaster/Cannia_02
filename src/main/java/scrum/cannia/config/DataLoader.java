//package scrum.cannia.config;
//
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//import scrum.cannia.model.PropietarioModel;
//import scrum.cannia.model.MascotaModel;
//import scrum.cannia.model.Genero;
//import scrum.cannia.repository.PropietarioRepository;
//import scrum.cannia.repository.MascotaRepository;
//
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.List;
//
//@Component
//public class DataLoader implements CommandLineRunner {
//
//    private final PropietarioRepository propietarioRepository;
//    private final MascotaRepository mascotaRepository;
//
//    public DataLoader(PropietarioRepository propietarioRepository, MascotaRepository mascotaRepository) {
//        this.propietarioRepository = propietarioRepository;
//        this.mascotaRepository = mascotaRepository;
//    }
//
//    @Override
//    public void run(String... args) throws Exception {
//
//        if (propietarioRepository.count() == 0) {
//
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//
//            List<PropietarioModel> propietarios = new ArrayList<>();
//
//            // 🧍 Propietarios de ejemplo
//            propietarios.add(new PropietarioModel(0, "1001", "Ana", "Torres", "Calle 10 #5-22",
//                    "3001112233", "ana@gmail.com", true, new ArrayList<>(), null));
//
//            propietarios.add(new PropietarioModel(0, "1002", "Carlos", "Pérez", "Carrera 12 #8-40",
//                    "3012223344", "carlos@gmail.com", true, new ArrayList<>(), null));
//
//            propietarios.add(new PropietarioModel(0, "1003", "Laura", "Gómez", "Av. 6 #20-11",
//                    "3023334455", "laura@gmail.com", true, new ArrayList<>(), null));
//
//            propietarios.add(new PropietarioModel(0, "1004", "Pedro", "Ruiz", "Calle 15 #3-55",
//                    "3034445566", "pedro@gmail.com", true, new ArrayList<>(), null));
//
//            propietarios.add(new PropietarioModel(0, "1005", "Sofía", "Castro", "Carrera 9 #12-33",
//                    "3045556677", "sofia@gmail.com", true, new ArrayList<>(), null));
//
//            propietarios.add(new PropietarioModel(0, "1006", "David", "Mora", "Av. 3 #45-22",
//                    "3056667788", "david@gmail.com", true, new ArrayList<>(), null));
//
//            propietarios.add(new PropietarioModel(0, "1007", "María", "López", "Calle 8 #9-21",
//                    "3067778899", "maria@gmail.com", true, new ArrayList<>(), null));
//
//            propietarios.add(new PropietarioModel(0, "1008", "Luis", "Rojas", "Carrera 4 #7-10",
//                    "3078889900", "luis@gmail.com", true, new ArrayList<>(), null));
//
//
//            propietarioRepository.saveAll(propietarios);
//
//            // 🐾 Mascotas asociadas
//            for (PropietarioModel propietario : propietarios) {
//                List<MascotaModel> mascotas = new ArrayList<>();
//
//                MascotaModel m1 = new MascotaModel(0,
//                        "Firulais", "Perro", "Labrador",
//                        sdf.parse("2020-03-10"), sdf.parse("2024-08-15"),
//                        "Antiparasitario", "Marrón", Genero.Macho,
//                        propietario);
//
//                MascotaModel m2 = new MascotaModel(0,
//                        "Mishi", "Gato", "Siames",
//                        sdf.parse("2021-06-01"), sdf.parse("2024-05-20"),
//                        "Vacuna triple felina", "Gris", Genero.Hembra,
//                        propietario);
//
//                mascotas.add(m1);
//
//                // Cada propietario con ID par tendrá 2 mascotas
//                Long idLong = Long.valueOf(propietario.getId());
//                if (idLong % 2 == 0) {
//                    mascotas.add(m2);
//                }
//
//                mascotaRepository.saveAll(mascotas);
//                propietario.setMascotas(mascotas);
//                propietarioRepository.save(propietario);
//            }
//
//            System.out.println("✅ Datos de prueba cargados correctamente (8 propietarios y sus mascotas).");
//        } else {
//            System.out.println("ℹ️ Datos ya existen, no se cargaron nuevamente.");
//        }
//    }
//}
