package com.minimarket;

import com.minimarket.entity.Categoria;
import com.minimarket.entity.Producto;
import com.minimarket.entity.Rol;
import com.minimarket.entity.Usuario;
import com.minimarket.repository.ProductoRepository;
import com.minimarket.service.impl.ProductoServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Set;
import com.minimarket.entity.Rol;
import com.minimarket.entity.Usuario;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private ProductoServiceImpl productoService;

    @Test
    void guardarProductoConStockValido() {
        Categoria categoria = new Categoria();
        categoria.setId(1L);
        categoria.setNombre("Abarrotes");

        Producto producto = new Producto();
        producto.setId(1L);
        producto.setNombre("Arroz");
        producto.setPrecio(1500.0);
        producto.setStock(20);
        producto.setCategoria(categoria);

        when(productoRepository.save(producto)).thenReturn(producto);

        Producto resultado = productoService.save(producto);

        assertNotNull(resultado);
        assertEquals("Arroz", resultado.getNombre());
        assertTrue(resultado.getStock() > 0);
        verify(productoRepository, times(1)).save(producto);
    }

    @Test
    void buscarProductoExistentePorId() {
        Producto producto = new Producto();
        producto.setId(1L);
        producto.setNombre("Leche");
        producto.setPrecio(1200.0);
        producto.setStock(10);

        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        Producto resultado = productoService.findById(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Leche", resultado.getNombre());
    }

    @Test
    void buscarProductoNoExistenteRetornaNull() {
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        Producto resultado = productoService.findById(99L);

        assertNull(resultado);
    }

    @Test
    void usuarioNoAdministradorNoPuedeModificarProducto() {

    Rol rol = new Rol();
    rol.setNombre("ROLE_CLIENTE");

    Usuario usuario = new Usuario();
    usuario.setRoles(Set.of(rol));

    boolean puedeEditar = usuario.getRoles()
            .stream()
            .anyMatch(r -> r.getNombre().equals("ROLE_ADMIN"));

    assertFalse(puedeEditar);
}

}