package scrum.cannia.service;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import scrum.cannia.model.ItemCarrito;
import scrum.cannia.model.ProductoModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class CarritoService {

    private final ProductoService productoService;

    // Carritos por usuario (en memoria)
    private final Map<String, List<ItemCarrito>> carritos = new ConcurrentHashMap<>();

    // =========================================================
    // OBTENER / CREAR CARRITO DEL USUARIO
    // =========================================================
    private List<ItemCarrito> getCarritoUsuario(String username) {
        return carritos.computeIfAbsent(username, k -> new ArrayList<>());
    }

    // =========================================================
    // MÉTODOS PÚBLICOS
    // =========================================================
    public List<ItemCarrito> getItems(String username) {
        return getCarritoUsuario(username);
    }

    public double getTotal(String username) {
        return getCarritoUsuario(username).stream()
                .mapToDouble(ItemCarrito::getSubtotal)
                .sum();
    }

    // =========================================================
    // AGREGAR PRODUCTO
    // =========================================================
    public void agregar(String username, Integer idProducto) {

        ProductoModel producto = productoService.buscarPorId(idProducto);

        if (producto == null) {
            throw new IllegalArgumentException("Producto no encontrado");
        }

        List<ItemCarrito> carrito = getCarritoUsuario(username);

        ItemCarrito existente = buscarItem(carrito, idProducto);

        if (existente != null) {
            existente.setCantidad(existente.getCantidad() + 1);
            existente.calcularSubtotal();
        } else {
            carrito.add(new ItemCarrito(producto, 1));
        }
    }

    // =========================================================
    // ELIMINAR PRODUCTO
    // =========================================================
    public void eliminar(String username, Integer idProducto) {
        getCarritoUsuario(username)
                .removeIf(item ->
                        Objects.equals(item.getProducto().getId(), idProducto)
                );
    }

    // =========================================================
    // VACIAR CARRITO
    // =========================================================
    public void limpiar(String username) {
        carritos.remove(username);
    }

    // =========================================================
    // AUMENTAR CANTIDAD
    // =========================================================
    public void aumentarCantidad(String username, Integer idProducto) {

        ItemCarrito item =
                buscarItem(getCarritoUsuario(username), idProducto);

        if (item != null) {
            item.setCantidad(item.getCantidad() + 1);
            item.calcularSubtotal();
        }
    }

    // =========================================================
    // DISMINUIR CANTIDAD
    // =========================================================
    public void disminuirCantidad(String username, Integer idProducto) {

        List<ItemCarrito> carrito = getCarritoUsuario(username);
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
    // MeTODO AUXILIAR
    // =========================================================
    private ItemCarrito buscarItem(List<ItemCarrito> carrito, Integer idProducto) {
        return carrito.stream()
                .filter(item ->
                        Objects.equals(item.getProducto().getId(), idProducto)
                )
                .findFirst()
                .orElse(null);
    }
}