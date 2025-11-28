package scrum.cannia.model;

import lombok.Data;

@Data
public class ItemCarrito {

    private ProductoModel producto;
    private int cantidad;
    private double subtotal;

    public ItemCarrito(ProductoModel producto, int cantidad) {
        this.producto = producto;
        this.cantidad = cantidad;
        calcularSubtotal();
    }

//     ==========================================
//     METODO NECESARIO PARA QUE TODO FUNCIONE
//     ==========================================
    public void calcularSubtotal() {
        if (producto != null) {
            this.subtotal = producto.getValor() * cantidad;
        }
    }
}
