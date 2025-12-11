// src/main/java/scrum/cannia/dto/ProductoBusquedaDTO.java

package scrum.cannia.Dto;

import scrum.cannia.model.ProductoModel;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Base64;
@Data
@NoArgsConstructor
public class ProductoBusquedaDto  {

    private int id;
    private String nombre;
    private String descripcion;
    private Integer valor;
    private String fotoBase64; // Para mostrar la imagen

    public ProductoBusquedaDto(ProductoModel producto) {
        this.id = producto.getId();
        this.nombre = producto.getNombre();
        this.descripcion = producto.getDescripcion();
        this.valor = producto.getValor();

        // ==========================================================
        // ¡SOLUCIÓN! CONVERTIR EL byte[] foto EN EL CAMPO fotoBase64
        // ==========================================================
        if (producto.getFoto() != null) {

            // 1. Convertir byte[] a Base64 String
            String base64String = Base64.getEncoder().encodeToString(producto.getFoto());

            // 2. Agregar el prefijo Data URL (asumiendo formato JPEG o PNG)
            // Si no estás seguro, usa 'data:image/png;base64,' o 'data:image/*;base64,'
            this.fotoBase64 = "data:image/jpeg;base64," + base64String;
        } else {
            this.fotoBase64 = null; // O una cadena vacía, o un placeholder.
        }

        // No necesitamos: this.fotoBase64 = producto.getFotoBase64();
    }
}