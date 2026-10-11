package com.api.alquilaya.services;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.api.alquilaya.models.UsuarioModel;
import com.api.alquilaya.repositories.IUsuariosRepository;

@Service 
public class UsuariosService {
    
    private final IUsuariosRepository usuariosRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuariosService(IUsuariosRepository usuariosRepository, PasswordEncoder passwordEncoder) {
        this.usuariosRepository = usuariosRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UsuarioModel crearUsuario(String email, String password, String rol) {

        if (usuariosRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("El email ya está registrado.");
        }
        UsuarioModel usuario = new UsuarioModel();
        usuario.setEmail(email);
        usuario.setPassword(passwordEncoder.encode(password));
        usuario.setRol(rol);
        return usuariosRepository.save(usuario);

    }
    
}
