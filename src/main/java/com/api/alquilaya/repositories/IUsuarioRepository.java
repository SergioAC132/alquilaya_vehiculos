package com.api.alquilaya.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.api.alquilaya.models.UsuarioModel;

public interface IUsuarioRepository extends JpaRepository<UsuarioModel, Long> {

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdUsuarioNot(String email, Long idUsuario);
}
