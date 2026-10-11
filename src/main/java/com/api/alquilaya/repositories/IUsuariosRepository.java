package com.api.alquilaya.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.api.alquilaya.models.UsuarioModel;

public interface IUsuariosRepository extends JpaRepository<UsuarioModel, Long> {
    

    Optional<UsuarioModel> findByEmail(String email);

    boolean existsByEmail(String email);
    
}
