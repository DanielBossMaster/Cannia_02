//package scrum.cannia.config;
//
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import scrum.cannia.model.ProductoModel;
//import scrum.cannia.model.UnidadMedida;
//import scrum.cannia.repository.ProductoRepository;
//
//import java.util.ArrayList;
//
//@Configuration
//public class DataInitializer {
//
//    @Bean
//    public CommandLineRunner initData(ProductoRepository productoRepository) {
//        return args -> {
//
//            if (productoRepository.count() == 0) {
//
//                ProductoModel p1 = new ProductoModel(
//                        0,
//                        null,
//                        "Concentrado Dog Chow",
//                        "Alimento completo para perros adultos.",
//                        100,
//                        25000,
//                        UnidadMedida.Kg,
//                        true,
//                        true,
//                        null,
//                        null,// ← este es fotoBase64
//                        new ArrayList<>() // ← inventarios
//                );
//
//
//                ProductoModel p2 = new ProductoModel(
//                        0,
//                        null,
//                        "Galletas para perro",
//                        "Snacks nutritivos para entrenamiento.",
//                        100,
//                        8000,
//                        UnidadMedida.Kg,
//                        true,
//                        true,
//                        null,
//                        null,
//                        new ArrayList<>()
//                );
//
//                ProductoModel p3 = new ProductoModel(
//                        0,
//                        null,
//                        "Arena para gatos",
//                        "Arena absorbente con control de olores.",
//                        70,
//                        18000,
//                        UnidadMedida.Kg,
//                        true,
//                        true,
//                        null,
//                        null,
//                        new ArrayList<>()
//                );
//
//                ProductoModel p4 = new ProductoModel(
//                        0,
//                        null,
//                        "Shampoo antipulgas",
//                        "Shampoo medicinal para eliminar pulgas y garrapatas.",
//                        40,
//                        15000,
//                        UnidadMedida.Kg,
//                        true,
//                        true,
//                        null,
//                        null,
//                        new ArrayList<>()
//                );
//
//                ProductoModel p5 = new ProductoModel(
//                        0,
//                        null,
//                        "Collar ajustable",
//                        "Collar para perros medianos ajustable.",
//                        60,
//                        12000,
//                        UnidadMedida.Kg,
//                        true,
//                        true,
//                        null,
//                        null,
//                        new ArrayList<>()
//                );
//
//                ProductoModel p6 = new ProductoModel(
//                        0,
//                        null,
//                        "Cepillo para perro",
//                        "Cepillo suave para perros de pelo corto.",
//                        30,
//                        9000,
//                        UnidadMedida.Kg,
//                        true,
//                        true,
//                        null,
//                        null,
//                        new ArrayList<>()
//                );
//
//                ProductoModel p7 = new ProductoModel(
//                        0,
//                        null,
//                        "Correa reforzada",
//                        "Correa para perros grandes, extra resistente.",
//                        20,
//                        22000,
//                        UnidadMedida.Kg,
//                        true,
//                        true,
//                        null,
//                        null,
//                        new ArrayList<>()
//                );
//
//                ProductoModel p8 = new ProductoModel(
//                        0,
//                        null,
//                        "Vitaminas para gato",
//                        "Suplemento vitamínico para gatos adultos.",
//                        45,
//                        17000,
//                        UnidadMedida.Kg,
//                        true,
//                        true,
//                        null,
//                        null,
//                        new ArrayList<>()
//                );
//
//                ProductoModel p9 = new ProductoModel(
//                        0,
//                        null,
//                        "Pouch húmedo",
//                        "Comida húmeda premium para gatos.",
//                        150,
//                        3500,
//                        UnidadMedida.Kg,
//                        true,
//                        true,
//                        null,
//                        null,
//                        new ArrayList<>()
//                );
//
//                ProductoModel p10 = new ProductoModel(
//                        0,
//                        null,
//                        "Juguete de cuerda",
//                        "Cuerda resistente para juegos de jalón.",
//                        80,
//                        6000,
//                        UnidadMedida.Kg,
//                        true,
//                        true,
//                        null,
//                        null,
//                        new ArrayList<>()
//                );
//
//                productoRepository.save(p1);
//                productoRepository.save(p2);
//                productoRepository.save(p3);
//                productoRepository.save(p4);
//                productoRepository.save(p5);
//                productoRepository.save(p6);
//                productoRepository.save(p7);
//                productoRepository.save(p8);
//                productoRepository.save(p9);
//                productoRepository.save(p10);
//
//                System.out.println("✔ 10 productos iniciales cargados correctamente.");
//            } else {
//                System.out.println("✔ Ya existen productos, no se cargaron datos iniciales.");
//            }
//        };
//    }
//}
