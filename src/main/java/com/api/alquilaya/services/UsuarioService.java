package com.api.alquilaya.services;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api.alquilaya.models.dto.UsuarioDto;
import com.api.alquilaya.models.UsuarioModel;
import com.api.alquilaya.repositories.IUsuarioRepository;
import com.api.alquilaya.utils.Validar;

@Service
@Transactional
public class UsuarioService {

    private final IUsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(IUsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<UsuarioDto> listar() {
        return usuarioRepository.findAll(Sort.by("idUsuario")).stream().map(this::aDto).toList();
    }

    @Transactional(readOnly = true)
    public UsuarioDto obtener(Long id) {
        return aDto(buscar(id));
    }

    public UsuarioDto crear(UsuarioDto dto) {
        UsuarioModel usuario = new UsuarioModel();
        String email = Validar.texto(dto.getEmail(), "El email");
        if (usuarioRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Ya existe un usuario con ese email.");
        }
        usuario.setEmail(email);
        usuario.setPassword(passwordEncoder.encode(Validar.texto(dto.getPassword(), "La contraseña")));
        usuario.setRol(normalizarRol(dto.getRol()));
        usuario.setActivo(dto.isActivo());
        return aDto(usuarioRepository.save(usuario));
    }

    public UsuarioDto actualizar(Long id, UsuarioDto dto) {
        UsuarioModel usuario = buscar(id);
        String email = Validar.texto(dto.getEmail(), "El email");
        if (usuarioRepository.existsByEmailAndIdUsuarioNot(email, id)) {
            throw new IllegalArgumentException("Ya existe otro usuario con ese email.");
        }
        usuario.setEmail(email);
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            usuario.setPassword(passwordEncoder.encode(dto.getPassword().trim()));
        }
        usuario.setRol(normalizarRol(dto.getRol()));
        usuario.setActivo(dto.isActivo());
        return aDto(usuarioRepository.save(usuario));
    }

    public void eliminar(Long id) {
        buscar(id);
        usuarioRepository.deleteById(id);
    }

    private UsuarioModel buscar(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado: " + id));
    }

    private String normalizarRol(String rol) {
        String limpio = Validar.texto(rol, "El rol").toUpperCase();
        if (limpio.startsWith("ROLE_")) {
            limpio = limpio.substring(5);
        }
        return limpio;
    }

    private UsuarioDto aDto(UsuarioModel u) {
        UsuarioDto dto = new UsuarioDto();
        dto.setIdUsuario(u.getIdUsuario());
        dto.setEmail(u.getEmail());
        dto.setRol(u.getRol());
        dto.setActivo(u.isActivo());
        return dto;
    }
}