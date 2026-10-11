package com.api.alquilaya.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.api.alquilaya.repositories.IUsuariosRepository;
import com.api.alquilaya.services.UsuariosService;

@Configuration
public class InicializadorDatos {
    
    @Bean 
    public CommandLineRunner crearUsuarios(
        IUsuariosRepository usuariosRepository,
        UsuariosService usuariosService,
        @Value("${app.admin.email}") String adminEmail,
        @Value("${app.admin.password}") String adminPassword,
        @Value("${app.operador.email}") String operadorEmail,
        @Value("${app.operador.password}") String operadorPassword) {

            return args -> {
                if (!usuariosRepository.existsByEmail(adminEmail)) {
                    usuariosService.crearUsuario(adminEmail, adminPassword, "ADMIN");
                }
                if (!usuariosRepository.existsByEmail(operadorEmail)) {
                    usuariosService.crearUsuario(operadorEmail, operadorPassword, "OPERADOR");
                }
            };
        }
    
}
