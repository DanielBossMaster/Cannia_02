package scrum.cannia.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemCarrito {

    private ProductoModel producto;
    private int cantidad;

    public double getSubtotal() {
        return producto.getValor() * cantidad;
    }
}
