package com.minimarket.controller;

import com.minimarket.entity.Usuario;
import com.minimarket.service.UsuarioService;

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
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/usuarios")
@Tag(
    name = "Usuarios",
    description = "Endpoints para la administración de usuarios del Minimarket Plus"
)
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Operation(
        summary = "Listar usuarios",
        description = "Obtiene todos los usuarios registrados en el sistema"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Lista de usuarios obtenida correctamente"
    )
    @GetMapping
    public CollectionModel<EntityModel<Usuario>> listarUsuarios() {

        List<EntityModel<Usuario>> usuarios = usuarioService.findAll()
            .stream()
            .map(usuario -> EntityModel.of(
                usuario,

                linkTo(
                    methodOn(UsuarioController.class)
                        .obtenerUsuarioPorId(usuario.getId())
                ).withSelfRel(),

                linkTo(
                    methodOn(UsuarioController.class)
                        .listarUsuarios()
                ).withRel("usuarios")
            ))
            .toList();

        return CollectionModel.of(
            usuarios,
            linkTo(
                methodOn(UsuarioController.class)
                    .listarUsuarios()
            ).withSelfRel()
        );
    }

    @Operation(
        summary = "Obtener usuario por ID",
        description = "Busca un usuario específico utilizando su identificador"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Usuario encontrado correctamente"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Usuario no encontrado"
        )
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Usuario>> obtenerUsuarioPorId(
            @Parameter(
                description = "ID del usuario que se desea consultar",
                example = "1"
            )
            @PathVariable Long id) {

        Optional<Usuario> usuario = usuarioService.findById(id);

        if (usuario.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        EntityModel<Usuario> usuarioModel = EntityModel.of(
            usuario.get(),

            linkTo(
                methodOn(UsuarioController.class)
                    .obtenerUsuarioPorId(id)
            ).withSelfRel(),

            linkTo(
                methodOn(UsuarioController.class)
                    .listarUsuarios()
            ).withRel("usuarios")
        );

        return ResponseEntity.ok(usuarioModel);
    }

    @Operation(
        summary = "Crear usuario",
        description = "Registra un nuevo usuario en el sistema"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Usuario creado correctamente"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Datos del usuario inválidos"
        )
    })
    @PostMapping
    public Usuario guardarUsuario(@RequestBody Usuario usuario) {
        return usuarioService.save(usuario);
    }

    @Operation(
        summary = "Actualizar usuario",
        description = "Actualiza los datos de un usuario existente utilizando su ID"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Usuario actualizado correctamente"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Usuario no encontrado"
        )
    })
    @PutMapping("/{id}")
    public ResponseEntity<Usuario> actualizarUsuario(
            @Parameter(
                description = "ID del usuario que se desea actualizar",
                example = "1"
            )
            @PathVariable Long id,
            @RequestBody Usuario usuario) {

        Optional<Usuario> usuarioExistente = usuarioService.findById(id);

        if (usuarioExistente.isPresent()) {
            usuario.setId(id);
            return ResponseEntity.ok(usuarioService.save(usuario));
        }

        return ResponseEntity.notFound().build();
    }

    @Operation(
        summary = "Eliminar usuario",
        description = "Elimina un usuario existente utilizando su ID"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "204",
            description = "Usuario eliminado correctamente"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Usuario no encontrado"
        )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarUsuario(
            @Parameter(
                description = "ID del usuario que se desea eliminar",
                example = "1"
            )
            @PathVariable Long id) {

        Optional<Usuario> usuario = usuarioService.findById(id);

        if (usuario.isPresent()) {
            usuarioService.deleteById(id);
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }
}