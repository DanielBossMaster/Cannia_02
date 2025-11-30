package scrum.cannia.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import scrum.cannia.model.ProductoModel;
import scrum.cannia.model.ServicioModel;
import scrum.cannia.repository.ProductoRepository;
import scrum.cannia.service.ProductoService;

@Controller
@RequestMapping("/productos")
public class ProductoController {

    @GetMapping("/veterinario/inventario")  // o la ruta que uses
    public String mostrarInventario(Model model) {
        model.addAttribute("producto", new ProductoModel()); // ← IMPORTANTE
        model.addAttribute("servicio", new ServicioModel()); // ← IMPORTANTE
        model.addAttribute("productos", ProductoService.listarTodos());
        return "Veterinario/Inventario"; // o el nombre de tu template
    }

    @PostMapping("/veterinario/inventario/productos")
    public <Producto> String guardarProducto(@ModelAttribute
                                      Producto producto,
                                             @RequestParam("archivoImagen") MultipartFile archivo) {
        ProductoService.guardar((ProductoModel) producto, archivo);
        return "redirect:/Veterinario/Inventario"; // ← redirect para evitar reenvío
    }
}

//    private ProductoRepository ProductoRepository;
//
//    //cuando llame ProductoController, le paso mi ProductoRepository para que se guarde
//    public ProductoController(ProductoService productoService) {
//        this.ProductoRepository = ProductoRepository;
//    }
//
//    @PostMapping("/guardar")
//    public String guardarProducto(Model model) {
//
//        //a mi vista le voy a enviar un nuevo atributo "producto" y que le voy a dar valor... en mi ProductoRepository
//        //voy a usar mi metodo "findAll" que quiere decir entruentre todos
//        //SELECT * FROM
//        model.addAttribute("productos", ProductoRepository.findAll());
//
//        //usando el return "Veterinario/Inventario" yo puedo mirar este producto en mi modelo
//        return "Veterinario/Inventario";
//    }

//    @Autowired
//    private ProductoService productoService;
//
//    @GetMapping
//    public String listarProductos(Model model) {
//        model.addAttribute("productos", productoService.obtenerTodosProductos());
//        return "inventario/productos/lista";
//    }
//
//    @GetMapping("/nuevo")
//    public String mostrarFormularioNuevoProducto(Model model) {
//        model.addAttribute("producto", new ProductoModel());
//        return "inventario/productos/formulario";
//    }
//
//    @PostMapping("/guardar")
//    public String guardarProducto(@ModelAttribute ProductoModel producto) {
//        productoService.guardarProducto(producto);
//        return "redirect:/inventario";
//    }
//
//    @GetMapping("/editar/{id}")
//    public String mostrarFormularioEditarProducto(@PathVariable Integer id, Model model) {
//        productoService.obtenerProductoPorId(id).ifPresent(producto ->
//                model.addAttribute("producto", producto));
//        return "inventario/productos/formulario";
//    }
//
//    @GetMapping("/eliminar/{id}")
//    public String eliminarProducto(@PathVariable Integer id) {
//        productoService.eliminarProductoLogicamente(id);
//        return "redirect:/inventario";
//    }
