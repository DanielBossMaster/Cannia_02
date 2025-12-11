package scrum.cannia.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import scrum.cannia.model.*;
import scrum.cannia.repository.FacturaDetalleRepository;
import scrum.cannia.repository.FacturaRepository;
import scrum.cannia.repository.ProductoRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PagoService {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private FacturaRepository facturaRepository;

    @Autowired
    private FacturaDetalleRepository facturaDetalleRepository;

    // Clase de excepción personalizada para manejo de errores de inventario
    public static class StockInsuficienteException extends RuntimeException {
        public StockInsuficienteException(String message) {
            super(message);
        }
    }

    @Transactional
    public FacturaModel registrarFactura(
            PropietarioModel propietario,
            VeterinariaModel veterinaria,
            List<ItemCarrito> carrito
    ) {

        // 1. Crear factura (sin guardar aún)
        FacturaModel factura = new FacturaModel();
        factura.setFechaEmision(LocalDateTime.now());
        factura.setDescripcion("Compra en la tienda veterinaria");
        factura.setMetodoPago(MetodoPago.Tarjeta);
        factura.setVeterinaria(veterinaria);
        factura.setPropietario(propietario);

        BigDecimal total = BigDecimal.ZERO;

        // 2. Crear detalles Y DESCONTAR INVENTARIO
        for (ItemCarrito item : carrito) {

            // --- LÓGICA DE STOCK INICIA AQUÍ ---
            ProductoModel producto = item.getProducto();
            int cantidadVendida = item.getCantidad();
            int stockActual = producto.getCantidad(); // Asumimos que la entidad ProductoModel tiene el campo 'cantidad'

            // A. Verificación de stock
            if (stockActual < cantidadVendida) {
                // Si falla la verificación, lanzamos excepción.
                // @Transactional asegura que NADA se guardará (ni la factura ni los descuentos anteriores).
                throw new StockInsuficienteException(
                        "Stock insuficiente para el producto: " + producto.getNombre() +
                                ". Stock disponible: " + stockActual + ", Cantidad requerida: " + cantidadVendida
                );
            }

            // B. Descuento de stock
            producto.setCantidad(stockActual - cantidadVendida);

            // C. Guardar el producto con el stock actualizado
            productoRepository.save(producto);
            // Esto persiste el cambio en la cantidad del producto en la base de datos.
            // --- LÓGICA DE STOCK TERMINA AQUÍ ---


            // Creación del detalle de factura (Como ya lo tenías)
            FacturaDetalleModel det = new FacturaDetalleModel();
            det.setFactura(factura);
            det.setProducto(producto); // Usamos el objeto actualizado
            det.setCantidad(cantidadVendida);

            BigDecimal precioDetalle = BigDecimal
                    .valueOf(producto.getValor())
                    .multiply(BigDecimal.valueOf(cantidadVendida));

            det.setPrecio(precioDetalle);

            factura.getDetalles().add(det);
            total = total.add(precioDetalle);
        }

        factura.setPrecioTotal(total);

        // 3. Guardar factura (cascade guarda detalles)
        return facturaRepository.save(factura);
    }
}