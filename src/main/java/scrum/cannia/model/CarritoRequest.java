// CarritoRequest.java
package scrum.cannia.model;

import lombok.Data;
import java.util.List;

@Data
public class CarritoRequest {
    private List<ItemCarritoRequest> items;

    @Data
    public static class ItemCarritoRequest {
        private Integer idProducto;
        private int cantidad;
    }
}
