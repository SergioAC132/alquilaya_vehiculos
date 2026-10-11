package com.api.alquilaya.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.api.alquilaya.models.Alquiler;

public interface IAlquilerRepository extends JpaRepository<Alquiler, Long> {

    boolean existsByVehiculo_IdVehiculoAndIdAlquilerGreaterThan(Long idVehiculo, Long idAlquiler);
}
