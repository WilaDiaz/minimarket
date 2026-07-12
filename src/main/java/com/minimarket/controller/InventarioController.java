package com.minimarket.controller;

import com.minimarket.entity.Inventario;
import com.minimarket.service.InventarioService;

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
@RequestMapping("/api/inventario")
@Tag(
    name = "Inventario",
    description = "Endpoints para la gestión de movimientos de inventario y stock"
)
public class InventarioController {

    @Autowired
    private InventarioService inventarioService;

    @Operation(
        summary = "Listar movimientos de inventario",
        description = "Obtiene todos los movimientos de inventario registrados en el sistema"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Movimientos de inventario obtenidos correctamente"
    )
    @GetMapping
    public CollectionModel<EntityModel<Inventario>> listarMovimientosDeInventario() {

        List<EntityModel<Inventario>> movimientos = inventarioService.findAll()
            .stream()
            .map(inventario -> EntityModel.of(
                inventario,

                linkTo(
                    methodOn(InventarioController.class)
                        .obtenerMovimientoPorId(inventario.getId())
                ).withSelfRel(),

                linkTo(
                    methodOn(InventarioController.class)
                        .listarMovimientosDeInventario()
                ).withRel("inventario")
            ))
            .toList();

        return CollectionModel.of(
            movimientos,
            linkTo(
                methodOn(InventarioController.class)
                    .listarMovimientosDeInventario()
            ).withSelfRel()
        );
    }

    @Operation(
        summary = "Obtener movimiento por ID",
        description = "Busca un movimiento de inventario específico mediante su identificador"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Movimiento de inventario encontrado correctamente"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Movimiento de inventario no encontrado"
        )
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Inventario>> obtenerMovimientoPorId(
            @Parameter(
                description = "ID del movimiento de inventario que se desea consultar",
                example = "1"
            )
            @PathVariable Long id) {

        Inventario inventario = inventarioService.findById(id);

        if (inventario == null) {
            return ResponseEntity.notFound().build();
        }

        EntityModel<Inventario> inventarioModel = EntityModel.of(
            inventario,

            linkTo(
                methodOn(InventarioController.class)
                    .obtenerMovimientoPorId(id)
            ).withSelfRel(),

            linkTo(
                methodOn(InventarioController.class)
                    .listarMovimientosDeInventario()
            ).withRel("inventario")
        );

        return ResponseEntity.ok(inventarioModel);
    }

    @Operation(
        summary = "Registrar movimiento de inventario",
        description = "Registra un nuevo movimiento de entrada o salida de stock"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Movimiento de inventario registrado correctamente"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Datos del movimiento inválidos"
        )
    })
    @PostMapping
    public Inventario registrarMovimiento(@RequestBody Inventario inventario) {
        return inventarioService.save(inventario);
    }

    @Operation(
        summary = "Actualizar movimiento de inventario",
        description = "Actualiza un movimiento de inventario existente utilizando su ID"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Movimiento de inventario actualizado correctamente"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Movimiento de inventario no encontrado"
        )
    })
    @PutMapping("/{id}")
    public ResponseEntity<Inventario> actualizarMovimiento(
            @Parameter(
                description = "ID del movimiento que se desea actualizar",
                example = "1"
            )
            @PathVariable Long id,
            @RequestBody Inventario inventario) {

        Inventario existente = inventarioService.findById(id);

        if (existente != null) {
            inventario.setId(id);
            return ResponseEntity.ok(inventarioService.save(inventario));
        }

        return ResponseEntity.notFound().build();
    }

    @Operation(
        summary = "Eliminar movimiento de inventario",
        description = "Elimina un movimiento de inventario utilizando su ID"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "204",
            description = "Movimiento de inventario eliminado correctamente"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Movimiento de inventario no encontrado"
        )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarMovimiento(
            @Parameter(
                description = "ID del movimiento que se desea eliminar",
                example = "1"
            )
            @PathVariable Long id) {

        Inventario inventario = inventarioService.findById(id);

        if (inventario != null) {
            inventarioService.deleteById(id);
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }
}