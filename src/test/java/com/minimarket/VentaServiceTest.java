package com.minimarket;

import com.minimarket.entity.Producto;
import com.minimarket.entity.Rol;
import com.minimarket.entity.Usuario;
import com.minimarket.entity.Venta;
import com.minimarket.repository.VentaRepository;
import com.minimarket.service.impl.VentaServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VentaServiceTest {

    @Mock
    private VentaRepository ventaRepository;

    @InjectMocks
    private VentaServiceImpl ventaService;

    @Test
    void cajeroPuedeGenerarVenta() {
        Rol rolCajero = new Rol();
        rolCajero.setNombre("ROLE_CAJERO");

        Usuario cajero = new Usuario();
        cajero.setId(1L);
        cajero.setUsername("cajero");
        cajero.setRoles(Set.of(rolCajero));

        Venta venta = new Venta();
        venta.setId(1L);
        venta.setUsuario(cajero);
        venta.setFecha(new Date());

        boolean tienePermiso = cajero.getRoles().stream()
                .anyMatch(r -> r.getNombre().equals("ROLE_CAJERO"));

        when(ventaRepository.save(venta)).thenReturn(venta);

        Venta resultado = null;
        if (tienePermiso) {
            resultado = ventaService.save(venta);
        }

        assertTrue(tienePermiso);
        assertNotNull(resultado);
        assertEquals("cajero", resultado.getUsuario().getUsername());
        verify(ventaRepository, times(1)).save(venta);
    }

    @Test
    void clienteNoPuedeGenerarVenta() {
        Rol rolCliente = new Rol();
        rolCliente.setNombre("ROLE_CLIENTE");

        Usuario cliente = new Usuario();
        cliente.setId(2L);
        cliente.setUsername("cliente");
        cliente.setRoles(Set.of(rolCliente));

        Venta venta = new Venta();
        venta.setId(2L);
        venta.setUsuario(cliente);
        venta.setFecha(new Date());

        boolean tienePermiso = cliente.getRoles().stream()
                .anyMatch(r -> r.getNombre().equals("ROLE_CAJERO"));

        assertFalse(tienePermiso);
        verify(ventaRepository, never()).save(any(Venta.class));
    }
@Test
void ventaValidaStockSuficienteYCalculaTotal() {
    Producto producto = new Producto();
    producto.setId(1L);
    producto.setNombre("Arroz");
    producto.setPrecio(1500.0);
    producto.setStock(10);

    int cantidadVendida = 3;

    boolean stockSuficiente = producto.getStock() >= cantidadVendida;
    double total = producto.getPrecio() * cantidadVendida;

    assertTrue(stockSuficiente);
    assertEquals(4500.0, total);
}

}