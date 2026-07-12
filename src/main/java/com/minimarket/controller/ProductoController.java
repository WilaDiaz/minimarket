package com.minimarket.controller;

import com.minimarket.entity.Producto;
import com.minimarket.service.ProductoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/productos")
@Tag(
    name = "Productos",
    description = "Endpoints para la gestión de productos del Minimarket Plus"
)
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @Operation(
        summary = "Listar productos",
        description = "Obtiene la lista completa de productos registrados en el sistema"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Lista de productos obtenida correctamente"
    )
    @GetMapping
    public CollectionModel<EntityModel<Producto>> listarProductos() {

        List<EntityModel<Producto>> productos = productoService.findAll()
            .stream()
            .map(producto -> EntityModel.of(
                producto,
                linkTo(
                    methodOn(ProductoController.class)
                        .obtenerProductoPorId(producto.getId())
                ).withSelfRel(),

                linkTo(
                    methodOn(ProductoController.class)
                        .listarProductos()
                ).withRel("productos")
            ))
            .toList();

        return CollectionModel.of(
            productos,
            linkTo(
                methodOn(ProductoController.class)
                    .listarProductos()
            ).withSelfRel()
        );
    }

    @Operation(
        summary = "Obtener producto por ID",
        description = "Busca un producto específico utilizando su identificador"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Producto encontrado correctamente"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Producto no encontrado"
        )
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Producto>> obtenerProductoPorId(
            @Parameter(
                description = "ID del producto que se desea consultar",
                example = "1"
            )
            @PathVariable Long id) {

        Producto producto = productoService.findById(id);

        if (producto == null) {
            return ResponseEntity.notFound().build();
        }

        EntityModel<Producto> productoModel = EntityModel.of(
            producto,

            linkTo(
                methodOn(ProductoController.class)
                    .obtenerProductoPorId(id)
            ).withSelfRel(),

            linkTo(
                methodOn(ProductoController.class)
                    .listarProductos()
            ).withRel("productos")
        );

        return ResponseEntity.ok(productoModel);
    }

    @Operation(
        summary = "Crear producto",
        description = "Registra un nuevo producto en el sistema"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Producto creado correctamente"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Datos del producto inválidos"
        )
    })
    @PostMapping
    public Producto guardarProducto(@RequestBody Producto producto) {
        return productoService.save(producto);
    }

    @Operation(
        summary = "Actualizar producto",
        description = "Actualiza los datos de un producto existente utilizando su ID"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Producto actualizado correctamente"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Producto no encontrado"
        )
    })
    @PutMapping("/{id}")
    public ResponseEntity<Producto> actualizarProducto(
            @Parameter(
                description = "ID del producto que se desea actualizar",
                example = "1"
            )
            @PathVariable Long id,
            @RequestBody Producto producto) {

        Producto productoExistente = productoService.findById(id);

        if (productoExistente != null) {
            producto.setId(id);
            return ResponseEntity.ok(productoService.save(producto));
        }

        return ResponseEntity.notFound().build();
    }

    @Operation(
        summary = "Eliminar producto",
        description = "Elimina un producto existente utilizando su ID"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "204",
            description = "Producto eliminado correctamente"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Producto no encontrado"
        )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProducto(
            @Parameter(
                description = "ID del producto que se desea eliminar",
                example = "1"
            )
            @PathVariable Long id) {

        Producto producto = productoService.findById(id);

        if (producto != null) {
            productoService.deleteById(id);
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }
}