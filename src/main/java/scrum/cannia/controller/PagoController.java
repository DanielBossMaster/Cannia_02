package scrum.cannia.controller;

import com.stripe.Stripe;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import scrum.cannia.model.*;
import scrum.cannia.repository.UsuarioRepository;
import scrum.cannia.service.CarritoService;
import scrum.cannia.service.PagoService;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/pago")
@RequiredArgsConstructor // ⭐ INYECCIÓN POR CONSTRUCTOR AUTOMÁTICA
public class PagoController {

    private final PagoService pagoService;
    private final CarritoService carritoService;
    private final UsuarioRepository usuarioRepository;


    // inyectar API key desde application.properties
    @Value("${stripe.api.key}")
    private String stripeApiKey;

    @PostConstruct
    private void initStripe() {
        Stripe.apiKey = stripeApiKey;
    }

    @PostMapping("/crearCheckout")
    @ResponseBody
    public Map<String, String> crearCheckout(Authentication authentication) throws Exception {

        String username = authentication.getName();
        UsuarioModel usuario = usuarioRepository
                .findByUsuario(authentication.getName())
                .orElseThrow();

        PropietarioModel propietario = usuario.getPropietario();
        if (propietario == null) {
            throw new IllegalStateException("Solo propietarios pueden pagar");
        }

        List<ItemCarrito> carrito = carritoService.getItems(username);

        if (carrito == null || carrito.isEmpty()) {
            throw new RuntimeException("El carrito está vacío");
        }

        List<SessionCreateParams.LineItem> lineItems = carrito.stream().map(item ->
                SessionCreateParams.LineItem.builder()
                        .setQuantity((long) item.getCantidad())
                        .setPriceData(
                                SessionCreateParams.LineItem.PriceData.builder()
                                        .setCurrency("cop")
                                        .setUnitAmount((long) (item.getProducto().getValor() * 100))
                                        .setProductData(
                                                SessionCreateParams.LineItem.PriceData.ProductData
                                                        .builder()
                                                        .setName(item.getProducto().getNombre())
                                                        .build()
                                        )
                                        .build()
                        )
                        .build()
        ).toList();

        SessionCreateParams params = SessionCreateParams.builder()
                .addAllLineItem(lineItems)
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("http://localhost:8081/pago/exitoso")
                .setCancelUrl("http://localhost:8081/pago/cancelado")
                .build();

        Session sessionStripe = Session.create(params);

        return Map.of("url", sessionStripe.getUrl());
    }


    // ============================================
    //        PAGO EXITOSO
    // ============================================
    @GetMapping("/exitoso")
    public String pagoExitoso(Authentication authentication, Model model) {
        String username = authentication.getName();

        UsuarioModel usuario = usuarioRepository
                .findByUsuario(authentication.getName())
                .orElseThrow();

        PropietarioModel propietario = usuario.getPropietario();
        if (propietario == null) {
            return "redirect:/login";
        }

        VeterinariaModel veterinaria =
                propietario.getVeterinario().getVeterinaria();

        if (veterinaria == null) {
            model.addAttribute("mensaje",
                    "No hay veterinaria asociada a tu cuenta.");
            return "tienda/CompraExitosa";
        }

        List<ItemCarrito> carrito = carritoService.getItems(username);

        if (carrito.isEmpty()) {
            model.addAttribute("mensaje",
                    "El pago fue exitoso, pero el carrito estaba vacío.");
            return "tienda/CompraExitosa";
        }

        FacturaModel factura =
                pagoService.registrarFactura(propietario, veterinaria, carrito);

        model.addAttribute("factura", factura);

        carritoService.limpiar(username);

        return "tienda/CompraExitosa";
    }

    // ============================================
    //        PAGO CANCELADO
    // ============================================
    @GetMapping("/cancelado")
    public String pagoCancelado() {
        return "tienda/CompraCancelada";
    }
}

