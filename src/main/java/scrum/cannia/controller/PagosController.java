package scrum.cannia.controller;

import com.stripe.Stripe;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import scrum.cannia.model.CarritoRequest;
import scrum.cannia.model.ItemCarrito;
import scrum.cannia.service.PagoService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pagos")
public class PagosController {

    @Autowired
    private PagoService pagoService;

    public PagosController() {
        Stripe.apiKey = "sk_test_51SYKWZCjwJYz7HpJKzOWlAU9rgBp457N4myuyAnnn4haZ5Buh6ymGykzSHljC4c4NLIb8XNGWUWjqzUDB89h7tHD00sxJgS3Sl"; // TU CLAVE TEST
    }

    @PostMapping("/crear-checkout")
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
                .setSuccessUrl("http://localhost:8081/success")
                .setCancelUrl("http://localhost:8081/cancel")
                .build();

        Session sessionStripe = Session.create(params);

        return Map.of("url", sessionStripe.getUrl());
    }

}
