// src/main/java/scrum/cannia/dto/ProductoBusquedaDTO.java
package scrum.cannia.Dto;

import scrum.cannia.model.ProductoModel;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProductoBusquedaDto  {

    private int id;
    private String nombre;
    private String descripcion;
    private Integer valor;
    private String fotoBase64; // Para mostrar la imagen

    // Constructor que acepta el ProductoModel y copia solo los datos necesarios
    public ProductoBusquedaDto(ProductoModel producto) {
        this.id = producto.getId();
        this.nombre = producto.getNombre();
        this.descripcion = producto.getDescripcion();
        this.valor = producto.getValor();
        this.fotoBase64 = producto.getFotoBase64();
        // No se copian las relaciones (veterinaria, inventarios)
    }
}