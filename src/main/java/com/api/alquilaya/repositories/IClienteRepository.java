package com.api.alquilaya.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.api.alquilaya.models.Cliente;

public interface IClienteRepository extends JpaRepository<Cliente, Long> {
}
