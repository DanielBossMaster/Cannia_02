package scrum.cannia.controller;

import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import scrum.cannia.Dto.RegistroDTO;
import scrum.cannia.model.*;
import scrum.cannia.repository.*;
import scrum.cannia.service.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@AllArgsConstructor
@Controller
@RequestMapping("/registro")
public class RegistroController {



    private final PasswordResetTokenService tokenService;
    private final EmailService emailService;



    private final UsuarioService usuarioService;

    private final CodigoVinculacionService codigoVinculacionService;
    private final PropietarioService propietarioService;

    @GetMapping
    public String mostrarFormulario(Model model) {
        model.addAttribute("registro", new RegistroDTO());

        return "registro/registrar";
    }

    @PostMapping
    public String registrarUsuario(
            @ModelAttribute("registro") RegistroDTO registroDTO,
                                   Model model, RedirectAttributes redirectAttributes) {

        try {

            usuarioService.registrarUsuario(registroDTO);

            redirectAttributes.addFlashAttribute("mensaje",
                    "Registro exitoso. Ahora puedes iniciar sesión.");

            return "redirect:/login";

        } catch (IllegalArgumentException e) {

            model.addAttribute("errorUsuario", e.getMessage());
            return "registro/registrar";
        }
    }

    @PostMapping("/propietario")
    @Transactional
    public String registrarPropietario(
            @RequestParam String usuario,
            @RequestParam String contrasena,

            @RequestParam String numDoc,
            @RequestParam String codigo,
            RedirectAttributes redirect
    ) {

        try {

            //  Validar código y obtener propietario
            CodigoVinculacionModel codigoVinculo =
                    codigoVinculacionService.validarCodigo(codigo);

            PropietarioModel propietario = codigoVinculo.getPropietario();

            //  Validar documento
            if (!propietario.getNumDoc().equals(numDoc)) {
                redirect.addFlashAttribute("error", "El documento no corresponde al propietario");
                return "redirect:/registro/propietario";
            }

            //  Validar si ya tiene cuenta
            if (propietario.isCuentaCreada()) {
                redirect.addFlashAttribute("error", "Este propietario ya tiene una cuenta creada");
                return "redirect:/registro/propietario";
            }

            //  Crear usuario
            UsuarioModel usuarioNuevo =
                    usuarioService.crearUsuarioPropietario(usuario, contrasena);

            // Asociar usuario al propietario
            propietarioService.asociarUsuario(propietario, usuarioNuevo);

            // Marcar código como usado
            codigoVinculacionService.marcarComoUsado(codigoVinculo);

            // Éxito → login
            return "redirect:/login";

        } catch (IllegalArgumentException | IllegalStateException e) {
            redirect.addFlashAttribute("error", e.getMessage());
            return "redirect:/registro/propietario";
        }
    }

    @GetMapping("/propietario")
    public String mostrarRegistroPropietario(Model model) {


        if (!model.containsAttribute("error")) {
            model.addAttribute("error", null);
        }

        return "registro/RegistroPropietario";
    }


    @PostMapping("/forgot-password")
    public String procesarForgotPassword(

            @RequestParam String username,
            @RequestParam String email,

            RedirectAttributes redirectAttributes){

        UsuarioModel usuario =
                usuarioService.buscarPorUsuario(username);

        boolean datosValidos = false;

        if(usuario != null){

            UsuarioModel usuarioPorEmail =
                    usuarioService.buscarPorEmail(email);

            if(usuarioPorEmail != null &&
                    usuarioPorEmail.getIdUsuario()
                            .equals(usuario.getIdUsuario())){

                datosValidos = true;
            }
        }

        if(datosValidos){

            String token =
                    UUID.randomUUID().toString();

            tokenService.crearToken(usuario, token);

            emailService.enviarCorreoRecuperacion(
                    email,
                    token
            );
            // mensaje de éxito
            redirectAttributes.addFlashAttribute(
                "mensajeRecuperacion",
                "Si los datos coinciden, recibirás un enlace para recuperar tu contraseña."
            );
        } else {
            // mensaje de error
            redirectAttributes.addFlashAttribute(
                "errorRecuperacion",
                "El usuario y el correo no coinciden."
            );
        }
        return "redirect:/login";
    }

    @GetMapping("/reset-password")
    public String mostrarResetPassword(
            @RequestParam String token,
            Model model){

        System.out.println("ENTRO AL POST RESET PASSWORD");

        PasswordResetTokenModel tokenModel =
                tokenService.buscarPorToken(token);

        if(tokenModel == null){

            return "token-invalido";
        }

        model.addAttribute("token", token);

        return "login/NuevaPassword";
    }

    @PostMapping("/reset-password")
    public String cambiarPassword(
            @RequestParam String token,
            @RequestParam String password){

        PasswordResetTokenModel tokenModel =
                tokenService.buscarPorToken(token);

        if(tokenModel == null){

            System.out.println("TOKEN INVALIDO");

            return "token-invalido";
        }

        if(tokenModel.getFechaExpiracion()
                .isBefore(LocalDateTime.now())){

            System.out.println("TOKEN EXPIRADO");

            return "token-expirado";
        }

        UsuarioModel usuario =
                tokenModel.getUsuario();

        System.out.println(
                "ACTUALIZANDO PASSWORD USUARIO ID: "
                        + usuario.getIdUsuario()
        );

        usuarioService.actualizarPassword(
                usuario,
                password
        );

        tokenService.eliminar(tokenModel);

        return "redirect:/login?passwordActualizada";
    }

    // ============================================
    //        EDITAR NOMBRE DE USUARIO
    // ============================================

    @PostMapping("/actualizar-username")
    @ResponseBody
    public String actualizarUsername(
            @RequestBody Map<String,String> datos,
            Authentication authentication
    ){
        String usernameActual =
                authentication.getName();
        usuarioService.actualizarUsername(
                usernameActual,
                datos.get("username")
        );
        return "logout";
    }

    // ============================================
    //              EDITAR PASSWORD
    // ============================================

    @PostMapping("/cambiar-password")
    @ResponseBody
    public String cambiarPassword(
            @RequestBody Map<String,String> datos,
            Authentication authentication
    ){
        try{
            usuarioService.cambiarPasswordSeguro(
                    authentication.getName(),
                    datos.get("actual"),
                    datos.get("nueva")

            );
            return "ok";
        }
        catch(Exception e){
            return e.getMessage();
        }
    }
}
