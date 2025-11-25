// Inventario.js - Funcionalidad completa para los módulos

// Datos de ejemplo (en una implementación real, estos vendrían de una API)
let productos = [
    { id: 1, nombre: "Antipulgas", categoria: "medicamento", precio: 25.99, stock: 15, estado: "activo", promocion: false, precioPromocion: null, descripcion: "Antipulgas para perros y gatos", eliminado: false },
    { id: 2, nombre: "Comida Premium", categoria: "alimento", precio: 45.50, stock: 8, estado: "activo", promocion: true, precioPromocion: 38.99, descripcion: "Alimento balanceado para perros adultos", eliminado: false },
    { id: 3, nombre: "Juguete Masticable", categoria: "accesorio", precio: 12.75, stock: 0, estado: "inactivo", promocion: false, precioPromocion: null, descripcion: "Juguete resistente para perros", eliminado: false }
];

let servicios = [
    { id: 1, nombre: "Consulta General", descripcion: "Consulta veterinaria general", precio: 30.00, duracion: 30, estado: "activo", eliminado: false },
    { id: 2, nombre: "Vacunación", descripcion: "Aplicación de vacunas", precio: 45.00, duracion: 20, estado: "activo", eliminado: false },
    { id: 3, nombre: "Limpieza Dental", descripcion: "Limpieza dental profesional", precio: 80.00, duracion: 60, estado: "inactivo", eliminado: false }
];

// Inicialización cuando el DOM está listo
document.addEventListener('DOMContentLoaded', function() {
    inicializarNavegacionModulos();
    inicializarModuloProductos();
    inicializarModuloServicios();
    inicializarModuloInventarios();
    cargarDatosIniciales();
});

// Navegación entre módulos
function inicializarNavegacionModulos() {
    const botonesModulos = document.querySelectorAll('.btn-modulo');

    botonesModulos.forEach(boton => {
        boton.addEventListener('click', function() {
            const moduloId = this.getAttribute('data-modulo');

            // Actualizar botones activos
            botonesModulos.forEach(btn => btn.classList.remove('activo'));
            this.classList.add('activo');

            // Mostrar módulo activo
            document.querySelectorAll('.modulo').forEach(modulo => {
                modulo.classList.remove('activo');
            });
            document.getElementById(`modulo-${moduloId}`).classList.add('activo');
        });
    });
}

// MÓDULO PRODUCTOS
function inicializarModuloProductos() {
    const btnAgregarProducto = document.getElementById('btn-agregar-producto');
    const modalProducto = document.getElementById('modal-producto');
    const formProducto = document.getElementById('form-producto');
    const checkboxPromocion = document.getElementById('promocion-producto');
    const grupoPrecioPromocion = document.getElementById('grupo-precio-promocion');

    // Abrir modal para agregar producto
    btnAgregarProducto.addEventListener('click', function() {
        abrirModalProducto();
    });

    // Mostrar/ocultar campo de precio de promoción
    checkboxPromocion.addEventListener('change', function() {
        if (this.checked) {
            grupoPrecioPromocion.style.display = 'block';
        } else {
            grupoPrecioPromocion.style.display = 'none';
            document.getElementById('precio-promocion').value = '';
        }
    });

    // Enviar formulario de producto
    formProducto.addEventListener('submit', function(e) {
        e.preventDefault();
        guardarProducto();
    });

    // Validación de precios en tiempo real
    document.getElementById('precio-producto').addEventListener('change', validarPrecioProducto);
    document.getElementById('precio-promocion').addEventListener('change', validarPrecioPromocion);

    // Cerrar modal
    document.querySelectorAll('.cerrar-modal').forEach(btn => {
        btn.addEventListener('click', function() {
            modalProducto.style.display = 'none';
        });
    });

    // Cerrar modal al hacer clic fuera
    window.addEventListener('click', function(e) {
        if (e.target === modalProducto) {
            modalProducto.style.display = 'none';
        }
    });
}

function validarPrecioProducto() {
    const precio = parseFloat(this.value);
    if (precio < 0) {
        mostrarMensaje('El precio no puede ser negativo', 'error');
        this.value = 0;
    }
}

function validarPrecioPromocion() {
    const precioPromocion = parseFloat(this.value);
    const precioNormal = parseFloat(document.getElementById('precio-producto').value);

    if (precioPromocion < 0) {
        mostrarMensaje('El precio de promoción no puede ser negativo', 'error');
        this.value = 0;
    } else if (precioPromocion >= precioNormal) {
        mostrarMensaje('El precio de promoción debe ser menor al precio normal', 'error');
        this.value = precioNormal * 0.9; // Sugerir 10% de descuento
    }
}

function abrirModalProducto(producto = null) {
    const modal = document.getElementById('modal-producto');
    const titulo = document.getElementById('titulo-modal-producto');
    const form = document.getElementById('form-producto');

    if (producto) {
        titulo.textContent = 'Editar Producto';
        document.getElementById('producto-id').value = producto.id;
        document.getElementById('nombre-producto').value = producto.nombre;
        document.getElementById('categoria-producto').value = producto.categoria;
        document.getElementById('precio-producto').value = producto.precio;
        document.getElementById('stock-producto').value = producto.stock;
        document.getElementById('descripcion-producto').value = producto.descripcion || '';
        document.getElementById('promocion-producto').checked = producto.promocion;

        if (producto.promocion) {
            document.getElementById('grupo-precio-promocion').style.display = 'block';
            document.getElementById('precio-promocion').value = producto.precioPromocion;
        } else {
            document.getElementById('grupo-precio-promocion').style.display = 'none';
        }
    } else {
        titulo.textContent = 'Agregar Producto';
        form.reset();
        document.getElementById('producto-id').value = '';
        document.getElementById('grupo-precio-promocion').style.display = 'none';
    }

    modal.style.display = 'block';
}

function guardarProducto() {
    const id = document.getElementById('producto-id').value;
    const nombre = document.getElementById('nombre-producto').value;
    const categoria = document.getElementById('categoria-producto').value;
    const precio = parseFloat(document.getElementById('precio-producto').value);
    const stock = parseInt(document.getElementById('stock-producto').value);
    const descripcion = document.getElementById('descripcion-producto').value;
    const promocion = document.getElementById('promocion-producto').checked;
    const precioPromocion = promocion ? parseFloat(document.getElementById('precio-promocion').value) : null;

    // Validaciones
    if (precio < 0) {
        mostrarMensaje('El precio no puede ser negativo', 'error');
        return;
    }

    if (stock < 0) {
        mostrarMensaje('El stock no puede ser negativo', 'error');
        return;
    }

    if (promocion && precioPromocion >= precio) {
        mostrarMensaje('El precio de promoción debe ser menor al precio normal', 'error');
        return;
    }

    if (promocion && precioPromocion < 0) {
        mostrarMensaje('El precio de promoción no puede ser negativo', 'error');
        return;
    }

    if (id) {
        // Editar producto existente
        const index = productos.findIndex(p => p.id == id);
        if (index !== -1) {
            productos[index] = {
                ...productos[index],
                nombre,
                categoria,
                precio,
                stock,
                descripcion,
                promocion,
                precioPromocion
            };
        }
    } else {
        // Agregar nuevo producto
        const nuevoId = Math.max(...productos.map(p => p.id), 0) + 1;
        productos.push({
            id: nuevoId,
            nombre,
            categoria,
            precio,
            stock,
            estado: 'activo',
            promocion,
            precioPromocion,
            descripcion,
            eliminado: false
        });
    }

    actualizarTablaProductos();
    document.getElementById('modal-producto').style.display = 'none';
    mostrarMensaje('Producto guardado correctamente');
    actualizarDashboardInventario();
}

function cambiarEstadoProducto(id) {
    const producto = productos.find(p => p.id === id && !p.eliminado);
    if (producto) {
        producto.estado = producto.estado === 'activo' ? 'inactivo' : 'activo';
        actualizarTablaProductos();
        actualizarDashboardInventario();
        mostrarMensaje(`Producto ${producto.estado === 'activo' ? 'activado' : 'desactivado'} correctamente`);
    }
}

function eliminarProducto(id) {
    if (confirm('¿Estás seguro de que quieres eliminar este producto? No se eliminará de la base de datos, pero dejará de aparecer en la interfaz.')) {
        const producto = productos.find(p => p.id === id);
        if (producto) {
            producto.eliminado = true;
            actualizarTablaProductos();
            actualizarDashboardInventario();
            mostrarMensaje('Producto eliminado de la interfaz correctamente');
        }
    }
}

function actualizarTablaProductos() {
    const tbody = document.getElementById('tabla-productos');
    tbody.innerHTML = '';

    // Filtrar solo productos no eliminados
    const productosVisibles = productos.filter(p => !p.eliminado);

    productosVisibles.forEach(producto => {
        const fila = document.createElement('tr');

        fila.innerHTML = `
            <td>${producto.id}</td>
            <td>${producto.nombre}</td>
            <td>${producto.categoria}</td>
            <td>$${producto.promocion && producto.precioPromocion ?
                `<span style="text-decoration: line-through; color: #999">$${producto.precio.toFixed(2)}</span> $${producto.precioPromocion.toFixed(2)}` :
                producto.precio.toFixed(2)}</td>
            <td>${producto.stock}</td>
            <td>
                <button class="btn-punto-estado" onclick="cambiarEstadoProducto(${producto.id})" title="${producto.estado === 'activo' ? 'Desactivar' : 'Activar'}">
                    <span class="punto-estado ${producto.estado === 'activo' ? 'activo' : 'inactivo'}"></span>
                </button>
            </td>
            <td>${producto.promocion ? '<span class="promocion">PROMOCIÓN</span>' : '-'}</td>
            <td>
                <button class="btn-accion btn-editar" onclick="abrirModalProducto(${JSON.stringify(producto).replace(/"/g, '&quot;')})" title="Editar">
                    <i class="bi bi-pencil"></i>
                </button>
                <button class="btn-accion btn-eliminar" onclick="eliminarProducto(${producto.id})" title="Eliminar">
                    <i class="bi bi-trash"></i>
                </button>
            </td>
        `;

        tbody.appendChild(fila);
    });
}

// MÓDULO SERVICIOS
function inicializarModuloServicios() {
    const btnAgregarServicio = document.getElementById('btn-agregar-servicio');
    const modalServicio = document.getElementById('modal-servicio');
    const formServicio = document.getElementById('form-servicio');

    // Abrir modal para agregar servicio
    btnAgregarServicio.addEventListener('click', function() {
        abrirModalServicio();
    });

    // Enviar formulario de servicio
    formServicio.addEventListener('submit', function(e) {
        e.preventDefault();
        guardarServicio();
    });

    // Validación de precios en tiempo real
    document.getElementById('precio-servicio').addEventListener('change', validarPrecioServicio);

    // Cerrar modal
    document.querySelectorAll('.cerrar-modal').forEach(btn => {
        btn.addEventListener('click', function() {
            modalServicio.style.display = 'none';
        });
    });

    // Cerrar modal al hacer clic fuera
    window.addEventListener('click', function(e) {
        if (e.target === modalServicio) {
            modalServicio.style.display = 'none';
        }
    });
}

function validarPrecioServicio() {
    const precio = parseFloat(this.value);
    if (precio < 0) {
        mostrarMensaje('El precio no puede ser negativo', 'error');
        this.value = 0;
    }
}

function abrirModalServicio(servicio = null) {
    const modal = document.getElementById('modal-servicio');
    const titulo = document.getElementById('titulo-modal-servicio');
    const form = document.getElementById('form-servicio');

    if (servicio) {
        titulo.textContent = 'Editar Servicio';
        document.getElementById('servicio-id').value = servicio.id;
        document.getElementById('nombre-servicio').value = servicio.nombre;
        document.getElementById('descripcion-servicio').value = servicio.descripcion;
        document.getElementById('precio-servicio').value = servicio.precio;
        document.getElementById('duracion-servicio').value = servicio.duracion;
    } else {
        titulo.textContent = 'Agregar Servicio';
        form.reset();
        document.getElementById('servicio-id').value = '';
    }

    modal.style.display = 'block';
}

function guardarServicio() {
    const id = document.getElementById('servicio-id').value;
    const nombre = document.getElementById('nombre-servicio').value;
    const descripcion = document.getElementById('descripcion-servicio').value;
    const precio = parseFloat(document.getElementById('precio-servicio').value);
    const duracion = parseInt(document.getElementById('duracion-servicio').value);

    // Validaciones
    if (precio < 0) {
        mostrarMensaje('El precio no puede ser negativo', 'error');
        return;
    }

    if (duracion <= 0) {
        mostrarMensaje('La duración debe ser mayor a 0 minutos', 'error');
        return;
    }

    if (id) {
        // Editar servicio existente
        const index = servicios.findIndex(s => s.id == id);
        if (index !== -1) {
            servicios[index] = {
                ...servicios[index],
                nombre,
                descripcion,
                precio,
                duracion
            };
        }
    } else {
        // Agregar nuevo servicio
        const nuevoId = Math.max(...servicios.map(s => s.id), 0) + 1;
        servicios.push({
            id: nuevoId,
            nombre,
            descripcion,
            precio,
            duracion,
            estado: 'activo',
            eliminado: false
        });
    }

    actualizarTablaServicios();
    document.getElementById('modal-servicio').style.display = 'none';
    mostrarMensaje('Servicio guardado correctamente');
    actualizarDashboardInventario();
}

function cambiarEstadoServicio(id) {
    const servicio = servicios.find(s => s.id === id && !s.eliminado);
    if (servicio) {
        servicio.estado = servicio.estado === 'activo' ? 'inactivo' : 'activo';
        actualizarTablaServicios();
        actualizarDashboardInventario();
        mostrarMensaje(`Servicio ${servicio.estado === 'activo' ? 'activado' : 'desactivado'} correctamente`);
    }
}

function eliminarServicio(id) {
    if (confirm('¿Estás seguro de que quieres eliminar este servicio? No se eliminará de la base de datos, pero dejará de aparecer en la interfaz.')) {
        const servicio = servicios.find(s => s.id === id);
        if (servicio) {
            servicio.eliminado = true;
            actualizarTablaServicios();
            actualizarDashboardInventario();
            mostrarMensaje('Servicio eliminado de la interfaz correctamente');
        }
    }
}

function actualizarTablaServicios() {
    const tbody = document.getElementById('tabla-servicios');
    tbody.innerHTML = '';

    // Filtrar solo servicios no eliminados
    const serviciosVisibles = servicios.filter(s => !s.eliminado);

    serviciosVisibles.forEach(servicio => {
        const fila = document.createElement('tr');

        fila.innerHTML = `
            <td>${servicio.id}</td>
            <td>${servicio.nombre}</td>
            <td>${servicio.descripcion}</td>
            <td>$${servicio.precio.toFixed(2)}</td>
            <td>${servicio.duracion} min</td>
            <td>
                <button class="btn-punto-estado" onclick="cambiarEstadoServicio(${servicio.id})" title="${servicio.estado === 'activo' ? 'Desactivar' : 'Activar'}">
                    <span class="punto-estado ${servicio.estado === 'activo' ? 'activo' : 'inactivo'}"></span>
                </button>
            </td>
            <td>
                <button class="btn-accion btn-editar" onclick="abrirModalServicio(${JSON.stringify(servicio).replace(/"/g, '&quot;')})" title="Editar">
                    <i class="bi bi-pencil"></i>
                </button>
                <button class="btn-accion btn-eliminar" onclick="eliminarServicio(${servicio.id})" title="Eliminar">
                    <i class="bi bi-trash"></i>
                </button>
            </td>
        `;

        tbody.appendChild(fila);
    });
}

// MÓDULO INVENTARIOS
function inicializarModuloInventarios() {
    const btnVerGraficos = document.getElementById('btn-ver-graficos');
    const btnCerrarDashboard = document.getElementById('btn-cerrar-dashboard');
    const dashboard = document.getElementById('dashboard-inventario');

    // Mostrar dashboard de gráficos
    btnVerGraficos.addEventListener('click', function() {
        dashboard.classList.remove('dashboard-oculto');
        dashboard.classList.add('dashboard-visible');
        actualizarDashboardInventario();
    });

    // Cerrar dashboard
    btnCerrarDashboard.addEventListener('click', function() {
        dashboard.classList.remove('dashboard-visible');
        dashboard.classList.add('dashboard-oculto');
    });
}

function actualizarDashboardInventario() {
    actualizarGraficoStock();
    actualizarAlertasInventario();
    actualizarInfoInventario();
}

function actualizarGraficoStock() {
    const grafico = document.getElementById('grafico-stock');
    grafico.innerHTML = '';

    // Obtener productos activos y no eliminados
    const productosActivos = productos.filter(p => p.estado === 'activo' && !p.eliminado);

    // Crear barras para cada producto
    productosActivos.forEach(producto => {
        const maxStock = Math.max(...productosActivos.map(p => p.stock), 10);
        const altura = (producto.stock / maxStock) * 100;

        const barra = document.createElement('div');
        barra.className = 'barra';
        barra.style.height = `${altura}%`;

        // Asignar color según el nivel de stock
        if (producto.stock === 0) {
            barra.classList.add('barra-alerta-roja');
        } else if (producto.stock < 5) {
            barra.classList.add('barra-alerta-amarilla');
        } else {
            barra.classList.add('barra-alerta-verde');
        }

        const etiqueta = document.createElement('div');
        etiqueta.className = 'barra-etiqueta';
        etiqueta.textContent = producto.nombre;

        barra.appendChild(etiqueta);
        grafico.appendChild(barra);
    });
}

function actualizarAlertasInventario() {
    const alertasContainer = document.getElementById('alertas-inventario');
    alertasContainer.innerHTML = '';

    // Productos sin stock (solo activos y no eliminados)
    const productosSinStock = productos.filter(p => p.estado === 'activo' && !p.eliminado && p.stock === 0);
    if (productosSinStock.length > 0) {
        const alerta = document.createElement('div');
        alerta.className = 'alerta alerta-roja';
        alerta.innerHTML = `
            <i class="bi bi-exclamation-triangle icono-alerta"></i>
            <div>
                <strong>${productosSinStock.length} productos sin stock</strong>
                <p>Revisar y reponer inventario</p>
            </div>
        `;
        alertasContainer.appendChild(alerta);
    }

    // Productos con stock bajo
    const productosStockBajo = productos.filter(p => p.estado === 'activo' && !p.eliminado && p.stock > 0 && p.stock < 5);
    if (productosStockBajo.length > 0) {
        const alerta = document.createElement('div');
        alerta.className = 'alerta alerta-amarilla';
        alerta.innerHTML = `
            <i class="bi bi-exclamation-circle icono-alerta"></i>
            <div>
                <strong>${productosStockBajo.length} productos con stock bajo</strong>
                <p>Considerar reponer pronto</p>
            </div>
        `;
        alertasContainer.appendChild(alerta);
    }

    // Estado general del inventario
    const productosActivos = productos.filter(p => p.estado === 'activo' && !p.eliminado);
    const serviciosActivos = servicios.filter(s => s.estado === 'activo' && !s.eliminado);

    if (productosActivos.length > 0 && serviciosActivos.length > 0) {
        const alerta = document.createElement('div');
        alerta.className = 'alerta alerta-verde';
        alerta.innerHTML = `
            <i class="bi bi-check-circle icono-alerta"></i>
            <div>
                <strong>Inventario en buen estado</strong>
                <p>${productosActivos.length} productos y ${serviciosActivos.length} servicios activos</p>
            </div>
        `;
        alertasContainer.appendChild(alerta);
    }
}

function actualizarInfoInventario() {
    const productosActivos = productos.filter(p => p.estado === 'activo' && !p.eliminado);
    const productosStockBajo = productos.filter(p => p.estado === 'activo' && !p.eliminado && p.stock > 0 && p.stock < 5);
    const serviciosActivos = servicios.filter(s => s.estado === 'activo' && !s.eliminado);

    document.getElementById('total-productos').textContent = productosActivos.length;
    document.getElementById('stock-bajo').textContent = productosStockBajo.length;
    document.getElementById('servicios-activos').textContent = serviciosActivos.length;
}

// Función para mostrar mensajes temporales
function mostrarMensaje(mensaje, tipo = 'exito') {
    // Crear elemento de mensaje
    const mensajeDiv = document.createElement('div');
    mensajeDiv.className = `mensaje-flotante mensaje-${tipo}`;
    mensajeDiv.textContent = mensaje;

    // Estilos para el mensaje
    mensajeDiv.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        padding: 12px 20px;
        border-radius: 4px;
        color: white;
        font-weight: 600;
        z-index: 10000;
        animation: deslizarEntrada 0.3s ease-out;
    `;

    if (tipo === 'exito') {
        mensajeDiv.style.backgroundColor = 'var(--color-exito)';
    } else if (tipo === 'error') {
        mensajeDiv.style.backgroundColor = 'var(--color-peligro)';
    }

    document.body.appendChild(mensajeDiv);

    // Remover después de 3 segundos
    setTimeout(() => {
        mensajeDiv.style.animation = 'deslizarSalida 0.3s ease-in';
        setTimeout(() => {
            if (mensajeDiv.parentNode) {
                mensajeDiv.parentNode.removeChild(mensajeDiv);
            }
        }, 300);
    }, 3000);
}

// Cargar datos iniciales
function cargarDatosIniciales() {
    actualizarTablaProductos();
    actualizarTablaServicios();
    actualizarDashboardInventario();
}

// Simular venta o compra para actualizar gráficos automáticamente
function simularVenta(productoId, cantidad) {
    const producto = productos.find(p => p.id === productoId && !p.eliminado);
    if (producto && producto.stock >= cantidad) {
        producto.stock -= cantidad;
        actualizarTablaProductos();
        actualizarDashboardInventario();
        mostrarMensaje('Venta registrada correctamente');
    }
}

function simularCompra(productoId, cantidad) {
    const producto = productos.find(p => p.id === productoId && !p.eliminado);
    if (producto) {
        producto.stock += cantidad;
        actualizarTablaProductos();
        actualizarDashboardInventario();
        mostrarMensaje('Compra registrada correctamente');
    }
}