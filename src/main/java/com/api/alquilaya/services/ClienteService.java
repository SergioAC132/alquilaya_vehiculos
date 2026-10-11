package com.api.alquilaya.services;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api.alquilaya.models.Cliente;
import com.api.alquilaya.repositories.IClienteRepository;
import com.api.alquilaya.utils.Validar;

@Service
@Transactional
public class ClienteService {

    private final IClienteRepository clienteRepository;

    public ClienteService(IClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @Transactional(readOnly = true)
    public List<Cliente> listar() {
        return clienteRepository.findAll(Sort.by("idCliente"));
    }

    @Transactional(readOnly = true)
    public Cliente obtener(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Cliente no encontrado: " + id));
    }

    public Cliente crear(Cliente datos) {
        Cliente cliente = new Cliente();
        aplicar(cliente, datos);
        return clienteRepository.save(cliente);
    }

    public Cliente actualizar(Long id, Cliente datos) {
        Cliente cliente = obtener(id);
        aplicar(cliente, datos);
        return clienteRepository.save(cliente);
    }

    public void eliminar(Long id) {
        obtener(id);
        try {
            clienteRepository.deleteById(id);
            clienteRepository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("No se puede eliminar el cliente porque tiene alquileres registrados.");
        }
    }

    private void aplicar(Cliente destino, Cliente datos) {
        destino.setNombre(Validar.texto(datos.getNombre(), "El nombre"));
        destino.setApellido(Validar.texto(datos.getApellido(), "El apellido"));
        destino.setEmail(Validar.texto(datos.getEmail(), "El email"));
        destino.setLicenciaConducir(Validar.texto(datos.getLicenciaConducir(), "La licencia de conducir"));
        destino.setTelefono(Validar.texto(datos.getTelefono(), "El teléfono"));
    }
}
