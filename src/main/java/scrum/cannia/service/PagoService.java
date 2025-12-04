package scrum.cannia.service;

import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import scrum.cannia.model.*;
import scrum.cannia.repository.FacturaDetalleRepository;
import scrum.cannia.repository.FacturaRepository;
import scrum.cannia.repository.InventarioRepository;
import scrum.cannia.repository.ProductoRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class PagoService {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private FacturaRepository facturaRepository;

    @Autowired
    private FacturaDetalleRepository facturaDetalleRepository;

    @Autowired
    private InventarioRepository inventarioRepository;


    public List<SessionCreateParams.LineItem> convertirItemsDelCarrito(CarritoRequest carrito) {

        List<SessionCreateParams.LineItem> lineItems = new ArrayList<>();

        if (carrito == null || carrito.getItems() == null) {
            return lineItems;
        }

        for (CarritoRequest.ItemCarritoRequest item : carrito.getItems()) {

            Integer idProducto = item.getIdProducto();
            if (idProducto == null) {
                throw new IllegalArgumentException("El id del producto es nulo");
            }

            ProductoModel producto = productoRepository.findById(idProducto)
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado con id: " + idProducto));

            SessionCreateParams.LineItem lineItem =
                    SessionCreateParams.LineItem.builder()
                            .setQuantity((long) item.getCantidad())
                            .setPriceData(
                                    SessionCreateParams.LineItem.PriceData.builder()
                                            .setCurrency("cop")
                                            .setUnitAmount(producto.getValor() * 100L) // Stripe usa centavos
                                            .setProductData(
                                                    SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                            .setName(producto.getNombre())
                                                            .build()
                                            )
                                            .build()
                            )
                            .build();

            lineItems.add(lineItem);
        }

        return lineItems;
    }

    /**
     * Registra la factura y detalles. Valida stock antes, descuenta y guarda todo en una transacción.
     */
    @Transactional
    public FacturaModel registrarFactura(
            PropietarioModel propietario,
            VeterinariaModel veterinaria,
            List<ItemCarrito> carrito
    ) {

        if (carrito == null || carrito.isEmpty()) {
            throw new RuntimeException("Carrito vacío al intentar registrar factura.");
        }

        // 1. Crear factura
        FacturaModel factura = new FacturaModel();
        factura.setDescripcion("Compra en la tienda veterinaria");
        factura.setMetodoPago(MetodoPago.Tarjeta);
        factura.setVeterinaria(veterinaria);
        factura.setPropietario(propietario);

        BigDecimal total = BigDecimal.ZERO;

        // 2. Primero: VALIDAR que hay stock suficiente para todos los items
        for (ItemCarrito item : carrito) {

            var optInv = inventarioRepository.findByProductoIdAndVeterinariaId(
                    item.getProducto().getId(),
                    veterinaria.getId()
            );

            if (optInv.isEmpty()) {
                throw new RuntimeException("Inventario no encontrado para el producto: " + item.getProducto().getNombre());
            }

            InventarioModel inventario = optInv.get();

            if (inventario.getStockActual() < item.getCantidad()) {
                throw new RuntimeException("Stock insuficiente para el producto: " + item.getProducto().getNombre());
            }
        }

        // 3. Si todo OK, crear detalles, descontar stock y acumular total
        for (ItemCarrito item : carrito) {

            InventarioModel inventario = inventarioRepository
                    .findByProductoIdAndVeterinariaId(
                            item.getProducto().getId(),
                            veterinaria.getId()
                    )
                    .orElseThrow(() -> new RuntimeException("Inventario no encontrado"));

            // Descontar stock
            inventario.setStockActual(inventario.getStockActual() - item.getCantidad());
            inventarioRepository.save(inventario);

            // Crear detalle
            FacturaDetalleModel det = new FacturaDetalleModel();
            det.setFactura(factura);
            det.setProducto(item.getProducto());
            det.setInventario(inventario);
            det.setCantidad(item.getCantidad());

            BigDecimal precioDetalle = BigDecimal
                    .valueOf(item.getProducto().getValor())
                    .multiply(BigDecimal.valueOf(item.getCantidad()));

            det.setPrecio(precioDetalle);

            // Agregar detalle a la factura
            factura.getDetalles().add(det);

            total = total.add(precioDetalle);
        }

        factura.setPrecioTotal(total);

        // 4. Guardar (cascade guarda detalles)
        return facturaRepository.save(factura);
    }

}
