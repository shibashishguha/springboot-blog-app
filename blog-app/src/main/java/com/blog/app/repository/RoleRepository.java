package com.blog.app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.blog.app.entity.Role;



public interface RoleRepository extends JpaRepository<Role, Integer> {
	 Optional<Role> findByName(String name);
	 boolean existsByName(String name);
}
