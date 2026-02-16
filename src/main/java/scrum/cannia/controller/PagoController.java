package scrum.cannia.controller;

import com.stripe.Stripe;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import scrum.cannia.model.*;
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
    private final HttpSession session;

    // inyectar API key desde application.properties
    @Value("${stripe.api.key}")
    private String stripeApiKey;

    @PostConstruct
    private void initStripe() {
        Stripe.apiKey = stripeApiKey;
    }

    @PostMapping("/crearCheckout")
    @ResponseBody
    public Map<String, String> crearCheckout() throws Exception {

        List<ItemCarrito> carrito = carritoService.getItems();

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


    @GetMapping("/exitoso")
    public String pagoExitoso(HttpSession session, Model model) {

        UsuarioModel usuario = (UsuarioModel) session.getAttribute("usuario");

        if (usuario == null) {
            return "redirect:/login";
        }

        // Recuperar propietario
        PropietarioModel propietario = usuario.getPropietario();
        if (propietario == null) {
            model.addAttribute("mensaje", "No se encontró un propietario asociado al usuario.");
            return "veterinario/CompraExitosa";
        }

        // Recuperar veterinaria desde el propietario
        VeterinariaModel veterinaria = propietario.getVeterinaria();
        if (veterinaria == null) {
            model.addAttribute("mensaje", "No hay veterinaria asociada al propietario.");
            return "veterinario/CompraExitosa";
        }

        // Carrito en sesión
        List<ItemCarrito> carrito = (List<ItemCarrito>) session.getAttribute("carrito");

        if (carrito == null || carrito.isEmpty()) {
            model.addAttribute("mensaje", "El pago fue exitoso pero el carrito está vacío.");
            return "veterinario/CompraExitosa";
        }

        // Crear factura
        FacturaModel factura = pagoService.registrarFactura(propietario, veterinaria, carrito);

        // Pasar datos a la vista
        model.addAttribute("factura", factura);

        // Limpiar carrito
        session.removeAttribute("carrito");

        return "veterinario/CompraExitosa";
    }

    @GetMapping("/cancelado")
    public String pagoCancelado() {
        return "veterinario/CompraCancelada";
    }
}

