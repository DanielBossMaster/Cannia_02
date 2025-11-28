//package scrum.cannia.config;
//
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import scrum.cannia.model.ProductoModel;
//import scrum.cannia.model.UnidadMedida;
//import scrum.cannia.repository.ProductoRepository;
//
//@Configuration
//public class DataInitializer {
//
//    @Bean
//    public CommandLineRunner initData(ProductoRepository productoRepository) {
//        return args -> {
//
//            // Solo cargar si no existen productos
//            if (productoRepository.count() == 0) {
//
//                ProductoModel p1 = new ProductoModel(0,"Concentrado Dog Chow",
//                        "Alimento completo para perros adultos.",
//                        50, 25000, true, (byte) 0, true, UnidadMedida.Kg, null);
//
//                ProductoModel p2 = new ProductoModel(0, "Galletas para perro",
//                        "Snacks nutritivos para entrenamiento.",
//                        100, 8000, true, (byte) 0, true, UnidadMedida.Paquete, null);
//
//                ProductoModel p3 = new ProductoModel(0, "Arena para gatos",
//                        "Arena absorbente con control de olores.",
//                        70, 18000, true, (byte) 0, true, UnidadMedida.Kg, null);
//
//                ProductoModel p4 = new ProductoModel(0, "Shampoo antipulgas",
//                        "Shampoo medicinal para eliminar pulgas y garrapatas.",
//                        40, 15000, true, (byte) 0, true, UnidadMedida.Und, null);
//
//                ProductoModel p5 = new ProductoModel(0, "Collar ajustable",
//                        "Collar para perros medianos ajustable.",
//                        60, 12000, true, (byte) 0, true, UnidadMedida.Und, null);
//
//                ProductoModel p6 = new ProductoModel(0, "Cepillo para perro",
//                        "Cepillo suave para perros de pelo corto.",
//                        30, 9000, true, (byte) 0, true, UnidadMedida.Und, null);
//
//                ProductoModel p7 = new ProductoModel(0, "Correa reforzada",
//                        "Correa para perros grandes, extra resistente.",
//                        20, 22000, true, (byte) 0, true, UnidadMedida.Und, null);
//
//                ProductoModel p8 = new ProductoModel(0, "Vitaminas para gato",
//                        "Suplemento vitamínico para gatos adultos.",
//                        45, 17000, true, (byte) 0, true, UnidadMedida.Und, null);
//
//                ProductoModel p9 = new ProductoModel(0, "Pouch húmedo",
//                        "Comida húmeda premium para gatos.",
//                        150, 3500, true, (byte) 0, true, UnidadMedida.Und, null);
//
//                ProductoModel p10 = new ProductoModel(0, "Juguete de cuerda",
//                        "Cuerda resistente para juegos de jalón.",
//                        80, 6000, true, (byte) 0, true, UnidadMedida.Und, null);
//
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
