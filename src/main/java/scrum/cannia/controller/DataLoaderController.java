package scrum.cannia.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import scrum.cannia.strategy.DataLoaderContext;
import scrum.cannia.strategy.factory.DataLoaderFactory;
import scrum.cannia.strategy.DataLoaderStrategy;
import scrum.cannia.model.MascotaModel;
import scrum.cannia.repository.MascotaRepository;

import java.util.List;

@Controller
public class DataLoaderController {

    @Autowired
    private MascotaRepository mascotaRepository;

    // ✔ Muestra el formulario HTML para subir archivo
    @GetMapping("/fundacion/cargar-archivo")
    public String mostrarCargarArchivo() {
        return "fundacion/cargarArchivo"; // tu vista HTML
    }

    // ✔ Procesa el archivo subido
    @PostMapping("/fundacion/cargar-archivo")
    public String procesarCargaArchivo(
            @RequestParam("archivo") MultipartFile file,
            Model model) {

        try {
            if (file.isEmpty()) {
                model.addAttribute("error", "Debe cargar un archivo");
                return "fundacion/cargarArchivo";
            }

            // Detecta Strategy
            DataLoaderStrategy strategy =
                    DataLoaderFactory.getStrategy(file.getOriginalFilename());

            // Crear context y asignar estrategia
            DataLoaderContext context = new DataLoaderContext();
            context.setStrategy(strategy);

            // 3. Ejecutar carga del archivo
            List<MascotaModel> mascotas = context.executeStrategy(file);

            // 4. Guardar en la base de datos
            mascotaRepository.saveAll(mascotas);

            model.addAttribute("mensaje", "Archivo cargado exitosamente");
            model.addAttribute("cantidad", mascotas.size());

        } catch (Exception e) {
            model.addAttribute("error", "Error: " + e.getMessage());
        }

        return "fundacion/cargarArchivo";
    }
}

