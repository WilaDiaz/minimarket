package com.minimarket.controller;

import com.minimarket.entity.Carrito;
import com.minimarket.service.CarritoService;

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
@RequestMapping("/api/carrito")
@Tag(
    name = "Carrito",
    description = "Endpoints para la gestión de productos del carrito de compras"
)
public class CarritoController {

    @Autowired
    private CarritoService carritoService;

    @Operation(
        summary = "Listar carrito",
        description = "Obtiene todos los productos registrados en el carrito"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Contenido del carrito obtenido correctamente"
    )
    @GetMapping
    public CollectionModel<EntityModel<Carrito>> listarCarrito() {

        List<EntityModel<Carrito>> carrito = carritoService.findAll()
            .stream()
            .map(item -> EntityModel.of(
                item,

                linkTo(
                    methodOn(CarritoController.class)
                        .obtenerCarritoPorId(item.getId())
                ).withSelfRel(),

                linkTo(
                    methodOn(CarritoController.class)
                        .listarCarrito()
                ).withRel("carrito")
            ))
            .toList();

        return CollectionModel.of(
            carrito,
            linkTo(
                methodOn(CarritoController.class)
                    .listarCarrito()
            ).withSelfRel()
        );
    }

    @Operation(
        summary = "Obtener elemento del carrito por ID",
        description = "Busca un elemento específico del carrito utilizando su identificador"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Elemento del carrito encontrado correctamente"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Elemento del carrito no encontrado"
        )
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Carrito>> obtenerCarritoPorId(
            @Parameter(
                description = "ID del elemento del carrito que se desea consultar",
                example = "1"
            )
            @PathVariable Long id) {

        Carrito carrito = carritoService.findById(id);

        if (carrito == null) {
            return ResponseEntity.notFound().build();
        }

        EntityModel<Carrito> carritoModel = EntityModel.of(
            carrito,

            linkTo(
                methodOn(CarritoController.class)
                    .obtenerCarritoPorId(id)
            ).withSelfRel(),

            linkTo(
                methodOn(CarritoController.class)
                    .listarCarrito()
            ).withRel("carrito")
        );

        return ResponseEntity.ok(carritoModel);
    }

    @Operation(
        summary = "Agregar producto al carrito",
        description = "Registra un nuevo producto en el carrito de compras"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Producto agregado al carrito correctamente"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Datos del carrito inválidos"
        )
    })
    @PostMapping
    public Carrito agregarProductoAlCarrito(@RequestBody Carrito carrito) {
        return carritoService.save(carrito);
    }

    @Operation(
        summary = "Actualizar elemento del carrito",
        description = "Actualiza un elemento existente del carrito utilizando su ID"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Elemento del carrito actualizado correctamente"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Elemento del carrito no encontrado"
        )
    })
    @PutMapping("/{id}")
    public ResponseEntity<Carrito> actualizarCarrito(
            @Parameter(
                description = "ID del elemento del carrito que se desea actualizar",
                example = "1"
            )
            @PathVariable Long id,
            @RequestBody Carrito carrito) {

        Carrito existente = carritoService.findById(id);

        if (existente != null) {
            carrito.setId(id);
            return ResponseEntity.ok(carritoService.save(carrito));
        }

        return ResponseEntity.notFound().build();
    }

    @Operation(
        summary = "Eliminar producto del carrito",
        description = "Elimina un elemento del carrito utilizando su ID"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "204",
            description = "Elemento eliminado del carrito correctamente"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Elemento del carrito no encontrado"
        )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProductoDelCarrito(
            @Parameter(
                description = "ID del elemento del carrito que se desea eliminar",
                example = "1"
            )
            @PathVariable Long id) {

        Carrito carrito = carritoService.findById(id);

        if (carrito != null) {
            carritoService.deleteById(id);
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }
}