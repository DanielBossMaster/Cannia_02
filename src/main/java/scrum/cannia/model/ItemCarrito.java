package scrum.cannia.model;

import lombok.Data;

@Data
public class ItemCarrito {

    private ProductoModel producto;
    private int cantidad;
    private int subtotal;

    public ItemCarrito(ProductoModel producto, int cantidad) {
        this.producto = producto;
        this.cantidad = cantidad;
        calcularSubtotal();
    }

    public void calcularSubtotal() {
        if (producto != null) {
            this.subtotal = producto.getValor() * cantidad;
        }
    }
}
