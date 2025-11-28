package scrum.cannia.service;

import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import scrum.cannia.model.CarritoRequest;
import scrum.cannia.model.ProductoModel;
import scrum.cannia.repository.ProductoRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class PagoService {

    @Autowired
    private ProductoRepository productoRepository;

    public List<SessionCreateParams.LineItem> convertirItemsDelCarrito(CarritoRequest carrito) {

        List<SessionCreateParams.LineItem> lineItems = new ArrayList<>();

        if (carrito == null || carrito.getItems() == null) {
            return lineItems;
        }

        for (CarritoRequest.ItemCarritoRequest item : carrito.getItems()) {

            Integer idProducto = item.getIdProducto();
            if (idProducto == null) {
                throw new IllegalArgumentException("El id del producto es nulo");
            }

            ProductoModel producto = productoRepository.findById(idProducto)
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado con id: " + idProducto));

            SessionCreateParams.LineItem lineItem =
                    SessionCreateParams.LineItem.builder()
                            .setQuantity((long) item.getCantidad())
                            .setPriceData(
                                    SessionCreateParams.LineItem.PriceData.builder()
                                            .setCurrency("cop")
                                            .setUnitAmount(producto.getValor() * 100L) // Stripe usa centavos
                                            .setProductData(
                                                    SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                            .setName(producto.getNombre())
                                                            .build()
                                            )
                                            .build()
                            )
                            .build();

            lineItems.add(lineItem);
        }

        return lineItems;
    }
}
