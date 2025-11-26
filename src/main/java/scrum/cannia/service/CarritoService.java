package scrum.cannia.service;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;
import scrum.cannia.model.ItemCarrito;
import scrum.cannia.model.ProductoModel;
import java.util.Objects;
import java.util.ArrayList;
import java.util.List;

@Service
public class CarritoService {

    private static final String SESSION_KEY = "carrito";

    @SuppressWarnings("unchecked")
    private List<ItemCarrito> getCarrito(HttpSession session) {
        List<ItemCarrito> carrito = (List<ItemCarrito>) session.getAttribute(SESSION_KEY);

        if (carrito == null) {
            carrito = new ArrayList<>();
            session.setAttribute(SESSION_KEY, carrito);
        }

        return carrito;
    }

    public void agregarProducto(HttpSession session, ProductoModel producto) {
        List<ItemCarrito> carrito = getCarrito(session);

        for (ItemCarrito item : carrito) {
            if (Objects.equals(item.getProducto().getId(), producto.getId())) {
                item.setCantidad(item.getCantidad() + 1);
                return;
            }
        }

        carrito.add(new ItemCarrito(producto, 1));
    }

    public void eliminarProducto(HttpSession session, Integer idProducto) {
        List<ItemCarrito> carrito = getCarrito(session);
        carrito.removeIf(item -> Objects.equals(item.getProducto().getId(), idProducto));

    }

    public double getTotal(HttpSession session) {
        return getCarrito(session).stream()
                .mapToDouble(ItemCarrito::getSubtotal)
                .sum();
    }

    public List<ItemCarrito> listarItems(HttpSession session) {
        return getCarrito(session);
    }

    public void vaciarCarrito(HttpSession session) {
        session.removeAttribute(SESSION_KEY);
    }
}
