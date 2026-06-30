package com.minimarket;

import com.minimarket.entity.Inventario;
import com.minimarket.entity.Producto;
import com.minimarket.entity.Rol;
import com.minimarket.entity.Usuario;
import com.minimarket.repository.InventarioRepository;
import com.minimarket.service.impl.InventarioServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.minimarket.entity.Rol;
import com.minimarket.entity.Usuario;
import java.util.Set;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventarioServiceTest {

    @Mock
    private InventarioRepository inventarioRepository;

    @InjectMocks
    private InventarioServiceImpl inventarioService;

    @Test
    void registrarMovimientoInventarioExitoso() {
        Producto producto = new Producto();
        producto.setId(1L);
        producto.setNombre("Arroz");

        Inventario inventario = new Inventario();
        inventario.setId(1L);
        inventario.setProducto(producto);
        inventario.setCantidad(10);
        inventario.setTipoMovimiento("Entrada");
        inventario.setFechaMovimiento(new Date());

        when(inventarioRepository.save(inventario)).thenReturn(inventario);

        Inventario resultado = inventarioService.save(inventario);

        assertNotNull(resultado);
        assertEquals("Entrada", resultado.getTipoMovimiento());
        assertTrue(resultado.getCantidad() > 0);
        verify(inventarioRepository, times(1)).save(inventario);
    }

    @Test
    void buscarMovimientoNoExistenteRetornaNull() {
        when(inventarioRepository.findById(99L)).thenReturn(Optional.empty());

        Inventario resultado = inventarioService.findById(99L);

        assertNull(resultado);
    }

    @Test
void usuarioSinPermisoNoPuedeRegistrarMovimiento() {

    Rol rol = new Rol();
    rol.setNombre("ROLE_CLIENTE");

    Usuario usuario = new Usuario();
    usuario.setRoles(Set.of(rol));

    boolean tienePermiso = usuario.getRoles()
            .stream()
            .anyMatch(r -> r.getNombre().equals("ROLE_ADMIN"));

    assertFalse(tienePermiso);

    verify(inventarioRepository, never()).save(any());
}
}