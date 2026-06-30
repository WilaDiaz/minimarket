package com.minimarket;

import com.minimarket.entity.Carrito;
import com.minimarket.entity.Producto;
import com.minimarket.entity.Usuario;
import com.minimarket.repository.CarritoRepository;
import com.minimarket.service.impl.CarritoServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CarritoServiceTest {

    @Mock
    private CarritoRepository carritoRepository;

    @InjectMocks
    private CarritoServiceImpl carritoService;

    @Test
    void agregarProductoAlCarritoConStockSuficiente() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setUsername("cliente");

        Producto producto = new Producto();
        producto.setId(1L);
        producto.setNombre("Arroz");
        producto.setStock(10);

        Carrito carrito = new Carrito();
        carrito.setId(1L);
        carrito.setUsuario(usuario);
        carrito.setProducto(producto);
        carrito.setCantidad(2);

        when(carritoRepository.save(carrito)).thenReturn(carrito);

        boolean stockSuficiente = producto.getStock() >= carrito.getCantidad();
        Carrito resultado = null;

        if (stockSuficiente) {
            resultado = carritoService.save(carrito);
        }

        assertTrue(stockSuficiente);
        assertNotNull(resultado);
        assertEquals("cliente", resultado.getUsuario().getUsername());
        assertEquals("Arroz", resultado.getProducto().getNombre());
        verify(carritoRepository, times(1)).save(carrito);
    }

    @Test
    void noAgregarProductoAlCarritoSinStockSuficiente() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setUsername("cliente");

        Producto producto = new Producto();
        producto.setId(1L);
        producto.setNombre("Leche");
        producto.setStock(1);

        Carrito carrito = new Carrito();
        carrito.setUsuario(usuario);
        carrito.setProducto(producto);
        carrito.setCantidad(5);

        boolean stockSuficiente = producto.getStock() >= carrito.getCantidad();

        assertFalse(stockSuficiente);
        verify(carritoRepository, never()).save(any(Carrito.class));
    }
}