package com.api.alquilaya.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.api.alquilaya.models.Mantenimiento;

public interface IMantenimientoRepository extends JpaRepository<Mantenimiento, Long> {

    boolean existsByVehiculo_IdVehiculoAndIdMantenimientoGreaterThan(Long idVehiculo, Long idMantenimiento);
}