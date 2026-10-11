package com.api.alquilaya.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.api.alquilaya.models.Vehiculo;
import com.api.alquilaya.models.Vehiculo.EstadoVehiculo;

public interface IVehiculoRepository extends JpaRepository<Vehiculo, Long> {

    List<Vehiculo> findByEstado(EstadoVehiculo estado);

    boolean existsByMatricula(String matricula);

    boolean existsByMatriculaAndIdVehiculoNot(String matricula, Long idVehiculo);
}
