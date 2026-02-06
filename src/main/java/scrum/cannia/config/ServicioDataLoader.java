package scrum.cannia.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import scrum.cannia.model.ServicioModel;
import scrum.cannia.model.VeterinariaModel;
import scrum.cannia.repository.ServicioRepository;
import scrum.cannia.repository.VeterinariaRepository;

@Configuration
public class ServicioDataLoader {

    @Bean
    CommandLineRunner cargarServicios(
            ServicioRepository servicioRepository,
            VeterinariaRepository veterinariaRepository
    ) {
        return args -> {

            // ⚠️ Evita duplicados si ya existen servicios
            if (servicioRepository.count() > 0) return;

            // 🔎 Obtener una veterinaria existente
            VeterinariaModel veterinaria = veterinariaRepository.findAll()
                    .stream()
                    .findFirst()
                    .orElse(null);

            if (veterinaria == null) {
                System.out.println("❌ No hay veterinaria registrada");
                return;
            }

            ServicioModel[] servicios = {
                    new ServicioModel(null, "Consulta General", "Revisión médica básica", 30, 30000, true, veterinaria),
                    new ServicioModel(null, "Vacunación", "Aplicación de vacunas", 20, 25000, true, veterinaria),
                    new ServicioModel(null, "Desparasitación", "Tratamiento antiparasitario", 20, 20000, true, veterinaria),
                    new ServicioModel(null, "Baño y Corte", "Baño completo y estética", 60, 45000, true, veterinaria),
                    new ServicioModel(null, "Cirugía Menor", "Procedimientos quirúrgicos menores", 90, 120000, true, veterinaria),
                    new ServicioModel(null, "Cirugía Mayor", "Cirugía especializada", 180, 300000, true, veterinaria),
                    new ServicioModel(null, "Rayos X", "Diagnóstico por imagen", 40, 80000, true, veterinaria),
                    new ServicioModel(null, "Ecografía", "Ultrasonido veterinario", 45, 90000, true, veterinaria),
                    new ServicioModel(null, "Hospitalización", "Cuidado médico prolongado", 1440, 150000, true, veterinaria),
                    new ServicioModel(null, "Consulta de Emergencia", "Atención inmediata", 40, 60000, true, veterinaria),
                    new ServicioModel(null, "Corte de Uñas", "Higiene básica", 15, 10000, true, veterinaria),
                    new ServicioModel(null, "Limpieza Dental", "Higiene bucal", 60, 70000, true, veterinaria),
                    new ServicioModel(null, "Control Postoperatorio", "Seguimiento médico", 30, 25000, true, veterinaria),
                    new ServicioModel(null, "Fisioterapia", "Rehabilitación física", 50, 85000, true, veterinaria),
                    new ServicioModel(null, "Eutanasia", "Procedimiento humanitario", 40, 100000, true, veterinaria),
                    new ServicioModel(null, "Certificado Médico", "Documento veterinario", 15, 20000, true, veterinaria)
            };

            for (ServicioModel s : servicios) {
                servicioRepository.save(s);
            }

            System.out.println("✅ 16 servicios cargados correctamente");
        };
    }
}
