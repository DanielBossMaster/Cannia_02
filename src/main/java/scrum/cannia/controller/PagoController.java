package scrum.cannia.controller;

import com.stripe.Stripe;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import scrum.cannia.model.ItemCarrito;
import scrum.cannia.model.PropietarioModel;
import scrum.cannia.model.VeterinariaModel;
import scrum.cannia.model.VeterinarioModel;
import scrum.cannia.service.PagoService;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/pago")
public class PagoController {

    @Autowired
    private PagoService pagoService;

    public PagoController() {
        Stripe.apiKey = "sk_test_51SYKWZCjwJYz7HpJKzOWlAU9rgBp457N4myuyAnnn4haZ5Buh6ymGykzSHljC4c4NLIb8XNGWUWjqzUDB89h7tHD00sxJgS3Sl"; // TU CLAVE TEST
    }

    @PostMapping("/crearCheckout")
    @ResponseBody
    public Map<String, String> crearCheckout(HttpSession session) throws Exception {

        List<ItemCarrito> carrito = (List<ItemCarrito>) session.getAttribute("carrito");

        if (carrito == null || carrito.isEmpty()) {
            throw new RuntimeException("El carrito está vacío");
        }

        List<SessionCreateParams.LineItem> lineItems = carrito.stream().map(item ->
                SessionCreateParams.LineItem.builder()
                        .setQuantity((long) item.getCantidad())
                        .setPriceData(
                                SessionCreateParams.LineItem.PriceData.builder()
                                        .setCurrency("cop")
                                        .setUnitAmount((long) (item.getProducto().getValor() * 100)) // Centavos
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
    public String pagoExitoso(HttpSession session) {

        // 1. Obtener carrito
        List<ItemCarrito> carrito = (List<ItemCarrito>) session.getAttribute("carrito");

        if (carrito == null || carrito.isEmpty()) {
            return "redirect:/veterinario/TiendaPreview"; // o donde quieras
        }

        // 2. Obtener propietario (logueado)
        PropietarioModel propietario = (PropietarioModel) session.getAttribute("propietario");
        if (propietario == null) {
            throw new RuntimeException("Propietario no encontrado en sesión");
        }

        // 3. Obtener veterinaria
        VeterinarioModel veterinario = (VeterinarioModel) session.getAttribute("veterinario");
        VeterinariaModel veterinaria = veterinario.getVeterinaria();


        // 4. Registrar factura en el servicio
        pagoService.registrarFactura(propietario, veterinaria, carrito);

        // 5. Vaciar carrito
        session.removeAttribute("carrito");

        // 6. Mostrar vista de éxito
        return "veterinario/CompraExitosa";
    }

    @GetMapping("/cancelado")
    public String pagoCancelado() {
        return "veterinario/CompraCancelada";
    }



}
