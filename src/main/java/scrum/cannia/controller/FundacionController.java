package scrum.cannia.controller;

import scrum.cannia.model.FundacionModel;
import scrum.cannia.service.FundacionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.BindingResult;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.validation.Valid;
import jakarta.servlet.http.HttpSession;
import scrum.cannia.model.UsuarioModel;


@Controller
@RequestMapping("/fundaciones")
public class FundacionController {

    @Autowired
    private FundacionService fundacionService;

    // RUTA RAÍZ - REDIRIGE AL LISTADO
    @GetMapping("")
    public String redirigirAListar() {
        return "redirect:/fundaciones/listar";
    }

    // LISTAR FUNDACIONES ADMON
    @GetMapping("/listar")
    public String listarFundaciones(Model model) {
        model.addAttribute("fundaciones", fundacionService.listarFundaciones());
        return "fundacion/listar";
    }

    // FORMULARIO DE CREACIÓN ADMON
    @GetMapping("/crear")
    public String mostrarFormularioCrear(Model model) {
        model.addAttribute("fundacion", new FundacionModel());
        return "fundacion/form";
    }

    // GUARDAR NUEVA FUNDACIÓN ADMON
    @PostMapping("/guardar")
    public String guardarFundacion(@Valid @ModelAttribute("fundacion") FundacionModel fundacion,
                                   BindingResult result) {

        if (result.hasErrors()) {
            return "fundacion/form";
        }

        fundacionService.guardarFundacion(fundacion);
        return "redirect:/fundaciones/listar";
    }

    // EDITAR ADMON
    @GetMapping("/editar/{id}")
    public String editarFundacion(@PathVariable Long id, Model model) {
        FundacionModel fundacion = fundacionService.obtenerFundacionPorId(id);

        if (fundacion == null) {
            return "redirect:/fundaciones/listar";
        }

        model.addAttribute("fundacion", fundacion);
        return "fundacion/form";
    }

    // ACTUALIZAR ADMON
    @PostMapping("/actualizar/{id}")
    public String actualizarFundacion(@PathVariable Long id,
                                      @Valid @ModelAttribute("fundacion") FundacionModel fundacion,
                                      BindingResult result) {

        if (result.hasErrors()) {
            return "fundacion/form";
        }

        fundacionService.actualizarFundacion(id, fundacion);
        return "redirect:/fundaciones/listar";
    }

    // ELIMINAR/DESACTIVAR FUNBDACION ADMON
    @GetMapping("/eliminar/{id}")
    public String eliminarFundacion(@PathVariable Long id) {
        fundacionService.eliminarFundacion(id);
        return "redirect:/fundaciones/listar";
    }

    // DETALLE DE FUNDACIÓN admon o usuario
    @GetMapping("/detalle/{id}")
    public String detalleFundacion(@PathVariable Long id, Model model) {

        FundacionModel fundacion = fundacionService.obtenerFundacionPorId(id);

        if (fundacion == null) {
            return "redirect:/fundaciones/listar";
        }

        model.addAttribute("fundacion", fundacion);
        return "fundacion/detalle"; // templates/fundaciones/detalle.html
    }
    // dashboard de fundaicon
    @GetMapping("/dashboard")
    public String dashboardFundacion(HttpSession session, Model model) {

        UsuarioModel usuario = (UsuarioModel) session.getAttribute("usuario");

        // si no hay usuario logueado o no es fundación retorna al login
        if (usuario == null || usuario.getFundacion() == null) {
            return "redirect:/login";
        }

        //Long fundacionId = usuario.getFundacion().getId();

        model.addAttribute("fundacionId", usuario.getFundacion().getId());
        model.addAttribute("fundacionNombre", usuario.getFundacion().getNombre());

        return "fundacion/dashboard";
    }
    //ver fundacion en dashboard
    @GetMapping("/mi-fundacion")
    public String miFundacion(HttpSession session, Model model) {

        UsuarioModel usuario = (UsuarioModel) session.getAttribute("usuario");
        if (usuario == null || usuario.getFundacion() == null) {
            return "redirect:/login";
        }

        model.addAttribute("fundacion", usuario.getFundacion());
        return "fundacion/detalle";
    }

    //editar fundacion desde el dashboard
    @GetMapping("/mi-fundacion/editar")
    public String editarMiFundacion(HttpSession session, Model model) {
        UsuarioModel usuario = (UsuarioModel) session.getAttribute("usuario");

        if (usuario == null || usuario.getFundacion() == null) {
            return "redirect:/login";
        }

        // Se carga la fundación del usuario autenticado
        //FundacionModel fundacion = usuario.getFundacion();

        model.addAttribute("fundacion", usuario.getFundacion());
        return "fundacion/form";
    }
    @PostMapping("/mi-fundacion/actualizar")
    public String actualizarMiFundacion(
            HttpSession session,
            @Valid @ModelAttribute("fundacion") FundacionModel fundacionForm,
            BindingResult result,
            Model model) {

        if (result.hasErrors()) {
            // si hay errores, devolvemos la vista y dejamos al usuario corregir
            model.addAttribute("fundacion", fundacionForm);
            return "fundacion/form";
        }

        UsuarioModel usuario = (UsuarioModel) session.getAttribute("usuario");

        if (usuario == null || usuario.getFundacion() == null) {
            return "redirect:/login";

        }

        Long id = usuario.getFundacion().getId();
        //Recuperar fundación original desde la BD
        FundacionModel fundacionBD = fundacionService.obtenerFundacionPorId(id);

        // Copiar los datos editados desde el formulario
        fundacionBD.setNombre(fundacionForm.getNombre());
        fundacionBD.setDescripcion(fundacionForm.getDescripcion());
        fundacionBD.setDireccion(fundacionForm.getDireccion());
        fundacionBD.setTelefono(fundacionForm.getTelefono());
        fundacionBD.setEmail(fundacionForm.getEmail());



        fundacionService.actualizarFundacion(id, fundacionBD);

        // ACTUALIZAR LA FUNDACIÓN
        usuario.setFundacion(fundacionBD);
        session.setAttribute("usuario", usuario);
        session.setAttribute("fundacionId", fundacionBD.getId());

        return "redirect:/fundaciones/dashboard";

    }

    // FIX PARA REDIRIGIR CORRECTAMENTE A MASCOTAS SIN NULL
    @GetMapping("/mis-mascotas")
    public String misMascotas(HttpSession session) {

        UsuarioModel usuario = (UsuarioModel) session.getAttribute("usuario");
        if (usuario == null || usuario.getFundacion() == null) {
            return "redirect:/login";
        }

        Long id = usuario.getFundacion().getId();
        return "redirect:/fundaciones/" + id + "/mascotas";
    }
}