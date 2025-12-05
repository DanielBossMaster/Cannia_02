package scrum.cannia.service;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import scrum.cannia.model.ItemCarrito;
import scrum.cannia.model.ProductoModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CarritoService {

    private final HttpSession session;
    private final ProductoService productoService;

    // =========================================================
    // OBTENER / CREAR CARRITO
    // =========================================================
    @SuppressWarnings("unchecked")
    private List<ItemCarrito> getCarrito() {
        List<ItemCarrito> carrito = (List<ItemCarrito>) session.getAttribute("carrito");

        if (carrito == null) {
            carrito = new ArrayList<>();
            session.setAttribute("carrito", carrito);
        }
        return carrito;
    }

    // =========================================================
    // MÉTODOS PÚBLICOS
    // =========================================================
    public List<ItemCarrito> getItems() {
        return getCarrito();
    }

    public double getTotal() {
        return getCarrito().stream()
                .mapToDouble(ItemCarrito::getSubtotal)
                .sum();
    }

    // =========================================================
    // AGREGAR PRODUCTO
    // =========================================================
    public void agregar(Integer idProducto) {
        List<ItemCarrito> carrito = getCarrito();
        ProductoModel producto = productoService.buscarPorId(idProducto);

        ItemCarrito existente = buscarItem(carrito, idProducto);

        if (existente != null) {
            existente.setCantidad(existente.getCantidad() + 1);
            existente.calcularSubtotal();
        } else {
            ItemCarrito nuevo = new ItemCarrito(producto, 1);
            carrito.add(nuevo);
        }
    }

    // =========================================================
    // ELIMINAR PRODUCTO
    // =========================================================
    public void eliminar(Integer idProducto) {
        List<ItemCarrito> carrito = getCarrito();
        carrito.removeIf(item -> Objects.equals(item.getProducto().getId(), idProducto));
    }

    // =========================================================
    // VACIAR CARRITO
    // =========================================================
    public void vaciar() {
        session.setAttribute("carrito", new ArrayList<>());
    }

    // =========================================================
    // AUMENTAR CANTIDAD
    // =========================================================
    public void aumentarCantidad(Integer idProducto) {
        List<ItemCarrito> carrito = getCarrito();
        ItemCarrito item = buscarItem(carrito, idProducto);

        if (item != null) {
            item.setCantidad(item.getCantidad() + 1);
            item.calcularSubtotal();
        }
    }

    // =========================================================
    // DISMINUIR CANTIDAD
    // =========================================================
    public void disminuirCantidad(Integer idProducto) {
        List<ItemCarrito> carrito = getCarrito();
        ItemCarrito item = buscarItem(carrito, idProducto);

        if (item != null) {
            if (item.getCantidad() > 1) {
                item.setCantidad(item.getCantidad() - 1);
                item.calcularSubtotal();
            } else {
                carrito.remove(item);
            }
        }
    }

    // =========================================================
    // Metodo AUXILIAR: Buscar Item
    // =========================================================
    private ItemCarrito buscarItem(List<ItemCarrito> carrito, Integer idProducto) {
        return carrito.stream()
                .filter(item -> Objects.equals(item.getProducto().getId(), idProducto))
                .findFirst()
                .orElse(null);
    }
}
