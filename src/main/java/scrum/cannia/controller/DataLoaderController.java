package scrum.cannia.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import scrum.cannia.dto.MascotaCargaDTO;
import scrum.cannia.repository.MascotaRepository;
import scrum.cannia.repository.UsuarioRepository;
import scrum.cannia.service.MascotaService;
import scrum.cannia.strategy.DataLoaderContext;
import scrum.cannia.strategy.factory.DataLoaderFactory;
import scrum.cannia.strategy.DataLoaderStrategy;

import java.util.List;

@Controller
public class DataLoaderController {

    @Autowired
    private MascotaService mascotaService;

    @Autowired
    private UsuarioRepository usuarioRepository;

//    @GetMapping("/CargarMascotas")
//    public String vistaCargarMascotas() {
//        return "fundacion/CargarMascotas";
//    }
//
//    //  Procesa el archivo subido
//    @PostMapping("/upload")
//    public String procesarCargaArchivo(
//            @RequestParam("archivo") MultipartFile file, @RequestParam("usuario") String username,
//            Model model) {
//
//        try {
//            if (file.isEmpty()) {
//                model.addAttribute("error", "Debe cargar un archivo");
//                return "fundacion/cargarArchivo";
//            }
//
//            // Detecta Strategy
//            DataLoaderStrategy strategy = DataLoaderFactory.getStrategy(file.getOriginalFilename());
//
//            // Crear context y asignar estrategia
//            DataLoaderContext context = new DataLoaderContext();
//            context.setStrategy(strategy);
//
//            // Ejecutar carga del archivo
//            List<MascotaCargaDTO> mascotas = context.executeStrategy(file);
//
//            mascotaService.guardarMascotasDesdeFundacion(mascotas, username);
//
//            model.addAttribute("mensaje", "Archivo cargado exitosamente");
//            model.addAttribute("cantidad", mascotas.size());
//
//        } catch (Exception e) {
//            model.addAttribute("error", "Error: " + e.getMessage());
//        }
//
//        return "fundacion/dashboard";
//    }
}

