package com.api.alquilaya.services;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api.alquilaya.models.dto.VehiculoDto;
import com.api.alquilaya.models.Coche;
import com.api.alquilaya.models.CocheElectrico;
import com.api.alquilaya.models.Furgoneta;
import com.api.alquilaya.models.Moto;
import com.api.alquilaya.models.Oficina;
import com.api.alquilaya.models.Vehiculo;
import com.api.alquilaya.models.Vehiculo.EstadoVehiculo;
import com.api.alquilaya.models.interfaces.Electrico;
import com.api.alquilaya.repositories.IOficinaRepository;
import com.api.alquilaya.repositories.IVehiculoRepository;
import com.api.alquilaya.utils.Validar;

@Service
@Transactional
public class VehiculoService {

    private final IVehiculoRepository vehiculoRepository;
    private final IOficinaRepository oficinaRepository;

    public VehiculoService(IVehiculoRepository vehiculoRepository, IOficinaRepository oficinaRepository) {
        this.vehiculoRepository = vehiculoRepository;
        this.oficinaRepository = oficinaRepository;
    }

    @Transactional(readOnly = true)
    public List<VehiculoDto> listar() {
        return vehiculoRepository.findAll(Sort.by("idVehiculo")).stream().map(this::aDto).toList();
    }

    @Transactional(readOnly = true)
    public List<VehiculoDto> listarDisponibles() {
        return vehiculoRepository.findByEstado(EstadoVehiculo.DISPONIBLE).stream().map(this::aDto).toList();
    }

    @Transactional(readOnly = true)
    public VehiculoDto obtener(Long id) {
        return aDto(buscar(id));
    }

    public VehiculoDto crear(VehiculoDto dto) {
        Vehiculo vehiculo = switch (normalizarTipo(dto.getTipo())) {
            case "COCHE" -> new Coche();
            case "COCHE_ELECTRICO" -> new CocheElectrico();
            case "FURGONETA" -> new Furgoneta();
            case "MOTO" -> new Moto();
            default -> throw new IllegalArgumentException(
                    "Tipo de vehículo no válido. Usa COCHE, COCHE_ELECTRICO, FURGONETA o MOTO.");
        };
        aplicar(vehiculo, dto);
        return aDto(vehiculoRepository.save(vehiculo));
    }

    public VehiculoDto actualizar(Long id, VehiculoDto dto) {
        Vehiculo vehiculo = buscar(id);
        if (dto.getTipo() != null && !tipoDe(vehiculo).equals(normalizarTipo(dto.getTipo()))) {
            throw new IllegalArgumentException("No se puede cambiar el tipo de un vehículo ya registrado.");
        }
        aplicar(vehiculo, dto);
        return aDto(vehiculoRepository.save(vehiculo));
    }

    public void eliminar(Long id) {
        buscar(id);
        try {
            vehiculoRepository.deleteById(id);
            vehiculoRepository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException(
                    "No se puede eliminar el vehículo porque tiene alquileres o mantenimientos registrados.");
        }
    }

    public VehiculoDto cargarBateria(Long id) {
        Vehiculo vehiculo = buscar(id);
        if (!(vehiculo instanceof Electrico electrico)) {
            throw new IllegalArgumentException("Solo los coches eléctricos se pueden cargar.");
        }
        electrico.cargarBateria();
        return aDto(vehiculoRepository.save(vehiculo));
    }

    @Transactional(readOnly = true)
    public double calcularPrecio(Long id, int dias) {
        double precio = buscar(id).calcularPrecioPorAlquiler(dias);
        return Math.round(precio * 100) / 100.0;
    }

    private Vehiculo buscar(Long id) {
        return vehiculoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Vehículo no encontrado: " + id));
    }

    private String normalizarTipo(String tipo) {
        return Validar.texto(tipo, "El tipo de vehículo").toUpperCase();
    }

    private String tipoDe(Vehiculo v) {
        if (v instanceof CocheElectrico) return "COCHE_ELECTRICO";
        if (v instanceof Coche) return "COCHE";
        if (v instanceof Furgoneta) return "FURGONETA";
        if (v instanceof Moto) return "MOTO";
        return "DESCONOCIDO";
    }

    private String etiquetaDe(String tipo) {
        return switch (tipo) {
            case "COCHE" -> "Coche";
            case "COCHE_ELECTRICO" -> "Coche electrico";
            case "FURGONETA" -> "Furgoneta";
            case "MOTO" -> "Moto";
            default -> tipo;
        };
    }

    private int entero(Integer valor) {
        return valor == null ? 0 : valor;
    }

    private void aplicar(Vehiculo v, VehiculoDto dto) {
        Long idOficina = Validar.noNulo(dto.getIdOficina(), "La oficina");
        Oficina oficina = oficinaRepository.findById(idOficina)
                .orElseThrow(() -> new NoSuchElementException("Oficina no encontrada: " + idOficina));

        String matricula = Validar.texto(dto.getMatricula(), "La matrícula").toUpperCase();
        boolean duplicada = v.getIdVehiculo() == null
                ? vehiculoRepository.existsByMatricula(matricula)
                : vehiculoRepository.existsByMatriculaAndIdVehiculoNot(matricula, v.getIdVehiculo());
        if (duplicada) {
            throw new IllegalArgumentException("Ya existe un vehículo con la matrícula " + matricula + ".");
        }

        Integer anio = Validar.noNulo(dto.getAnio(), "El año");
        if (anio < 1900 || anio > 2100) {
            throw new IllegalArgumentException("El año no es válido.");
        }
        Double tarifa = Validar.noNulo(dto.getTarifaDiaria(), "La tarifa diaria");
        if (tarifa <= 0) {
            throw new IllegalArgumentException("La tarifa diaria debe ser mayor que cero.");
        }

        v.setOficina(oficina);
        v.setMarca(Validar.texto(dto.getMarca(), "La marca"));
        v.setModelo(Validar.texto(dto.getModelo(), "El modelo"));
        v.setAnio(anio);
        v.setMatricula(matricula);
        v.setTarifaDiaria(tarifa);

        if (v.getEstado() == null) {
            v.setEstado(EstadoVehiculo.DISPONIBLE);
        }
        v.setDetalle(dto.getDetalle() == null || dto.getDetalle().isBlank() ? null : dto.getDetalle().trim());

        if (v instanceof Moto moto) {
            int cilindrada = entero(dto.getCilindrada());
            if (cilindrada <= 0) throw new IllegalArgumentException("La cilindrada debe ser mayor que cero.");
            moto.setCilindrada(cilindrada);
        }
        if (v instanceof Furgoneta furgoneta) {
            int carga = entero(dto.getCapacidadCargaKg());
            if (carga <= 0) throw new IllegalArgumentException("La capacidad de carga debe ser mayor que cero.");
            furgoneta.setCapacidadCargaKg(carga);
        }
        if (v instanceof CocheElectrico electrico) {
            int bateria = entero(dto.getNivelBateria());
            int autonomia = entero(dto.getAutonomiaMaximaKm());
            if (bateria < 0 || bateria > 100) {
                throw new IllegalArgumentException("El nivel de batería debe estar entre 0 y 100.");
            }
            if (autonomia <= 0) throw new IllegalArgumentException("La autonomía debe ser mayor que cero.");
            electrico.setNivelBateria(bateria);
            electrico.setAutonomiaMaximaKm(autonomia);
        }
    }

    private VehiculoDto aDto(Vehiculo v) {
        VehiculoDto dto = new VehiculoDto();
        String tipo = tipoDe(v);
        dto.setIdVehiculo(v.getIdVehiculo());
        dto.setTipo(tipo);
        dto.setTipoEtiqueta(etiquetaDe(tipo));
        dto.setIdOficina(v.getOficina().getIdOficina());
        dto.setOficinaNombre(v.getOficina().getNombre());
        dto.setMarca(v.getMarca());
        dto.setModelo(v.getModelo());
        dto.setAnio(v.getAnio());
        dto.setMatricula(v.getMatricula());
        dto.setTarifaDiaria(v.getTarifaDiaria());
        dto.setEstado(v.getEstado().name());
        dto.setDetalle(v.getDetalle());

        if (v instanceof Moto moto) {
            dto.setCilindrada(moto.getCilindrada());
            dto.setResumen(moto.getCilindrada() + " cc");
        } else if (v instanceof Furgoneta furgoneta) {
            dto.setCapacidadCargaKg(furgoneta.getCapacidadCargaKg());
            dto.setResumen(furgoneta.getCapacidadCargaKg() + " kg de carga");
        } else if (v instanceof CocheElectrico electrico) {
            dto.setNivelBateria(electrico.getNivelBateria());
            dto.setAutonomiaMaximaKm(electrico.getAutonomiaMaximaKm());
            dto.setResumen("Batería " + electrico.getNivelBateria() + "%, " + electrico.getAutonomiaMaximaKm() + " km");
        }
        return dto;
    }
}