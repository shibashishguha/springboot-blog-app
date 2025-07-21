package com.blog.app.config;

import com.blog.app.entity.Role;
import com.blog.app.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataFeeder {

    @Bean
    public CommandLineRunner feedRoles(RoleRepository roleRepository) {
        return args -> {
            if (roleRepository.findByName("USER").isEmpty()) {
                roleRepository.save(new Role("USER"));
            }
            if (roleRepository.findByName("ADMIN").isEmpty()) {
                roleRepository.save(new Role("ADMIN"));
            }
            if (roleRepository.findByName("AUTHOR").isEmpty()) {
                roleRepository.save(new Role("AUTHOR"));
            }
        };
    }
}