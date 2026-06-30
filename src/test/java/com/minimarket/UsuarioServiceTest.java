package com.minimarket;

import com.minimarket.entity.Rol;
import com.minimarket.entity.Usuario;
import com.minimarket.repository.UsuarioRepository;
import com.minimarket.security.service.CustomUserDetailsService;
import com.minimarket.service.impl.UsuarioServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioServiceImpl usuarioService;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void autenticarUsuarioValido() {
        Rol rol = new Rol();
        rol.setNombre("ROLE_ADMIN");

        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setUsername("admin");
        usuario.setPassword("1234");
        usuario.setRoles(Set.of(rol));

        when(usuarioRepository.findByUsername("admin")).thenReturn(Optional.of(usuario));

        UserDetails resultado = customUserDetailsService.loadUserByUsername("admin");

        assertNotNull(resultado);
        assertEquals("admin", resultado.getUsername());
        assertTrue(resultado.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
    }

    @Test
    void autenticarUsuarioInvalidoLanzaError() {
        when(usuarioRepository.findByUsername("desconocido")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () ->
                customUserDetailsService.loadUserByUsername("desconocido")
        );
    }

    @Test
    void guardarUsuarioCorrectamente() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setUsername("cliente");
        usuario.setPassword("1234");

        when(usuarioRepository.save(usuario)).thenReturn(usuario);

        Usuario resultado = usuarioService.save(usuario);

        assertNotNull(resultado);
        assertEquals("cliente", resultado.getUsername());
        verify(usuarioRepository, times(1)).save(usuario);
    }
    @Test
    void usuarioDebeTenerDatosObligatoriosCompletos() {
    Usuario usuario = new Usuario();
    usuario.setUsername("cliente");
    usuario.setPassword("1234");

    assertNotNull(usuario.getUsername());
    assertFalse(usuario.getUsername().isBlank());
    assertNotNull(usuario.getPassword());
    assertFalse(usuario.getPassword().isBlank());
}

}