package scrum.cannia.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import scrum.cannia.model.ServicioModel;
import scrum.cannia.model.UsuarioModel;

import scrum.cannia.repository.ServicioRepository;

import scrum.cannia.service.ServicioService;

@Controller
@RequestMapping("/inventario/servicios")
public class ServicioController {

    private final ServicioRepository servicioRepository;

    public ServicioController(
            ServicioRepository servicioRepository) {

        this.servicioRepository = servicioRepository;
    }

        @Autowired
        private ServicioService servicioService;

        // ============================================
        //   LISTAR INVENTARIO Y MOSTRAR FORMULARIOS
        // ============================================
        @GetMapping
        public String servicio(Model model, HttpSession session) {
            // Obtener el usuario en sesión
            UsuarioModel usuario = (UsuarioModel) session.getAttribute("usuario");
            if (usuario == null) {
                return "redirect:/login";  // Seguridad
            }

            model.addAttribute("servicio", new ServicioModel());
            model.addAttribute("servicios", servicioService.listarTodos());

            return "Inventario/Servicio";

        }

    // ============================================
    //         GUARDAR PRODUCTO
    // ============================================

    @PostMapping("/guardar")
    public String guardar(@Validated @ModelAttribute ServicioModel servicio,
                          BindingResult br,
                          HttpSession session,
                          Model model
    ) {


        // Obtener el usuario en sesión
        UsuarioModel usuario = (UsuarioModel) session.getAttribute("usuario");
        if (usuario == null) {
            return "redirect:/login";  // Seguridad
        }

        // Validación del formulario
        if (br.hasErrors()) {
            model.addAttribute("mensaje", "Error al insertar los datos.");
            model.addAttribute("servicio", new ServicioModel());
            model.addAttribute("servicios", servicioService.listarTodos());
            return "/Inventario/Servicio";

        }

        servicioRepository.save(servicio);

        return "redirect:/inventario/servicios";
        // ruta de la paginba, para que vuelva a la misma pagina
    }
    }
//    @Autowired
//    private ServicioService servicioService;
//
//    @GetMapping
//    public String listarServicios(Model model) {
//        model.addAttribute("servicios", servicioService.obtenerTodosServicios());
//        return "inventario/servicios/lista";
//    }
//
//    @GetMapping("/nuevo")
//    public String mostrarFormularioNuevoServicio(Model model) {
//        model.addAttribute("servicio", new ServicioModel());
//        return "inventario/servicios/formulario";
//    }
//
//    @PostMapping("/guardar")
//    public String guardarServicio(@ModelAttribute ServicioModel servicio) {
//        servicioService.guardarServicio(servicio);
//        return "redirect:/inventario";
//    }
//
//    @GetMapping("/editar/{id}")
//    public String mostrarFormularioEditarServicio(@PathVariable Integer id, Model model) {
//        servicioService.obtenerServicioPorId(id).ifPresent(servicio ->
//                model.addAttribute("servicio", servicio));
//        return "inventario/servicios/formulario";
//    }
//
//    @GetMapping("/eliminar/{id}")
//    public String eliminarServicio(@PathVariable Integer id) {
//        servicioService.eliminarServicioLogicamente(id);
//        return "redirect:/inventario";
//    }