package scrum.cannia.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/servicios")
public class ServicioController {

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
}