package com.pruebatecnica.tablerodenotas.Repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pruebatecnica.tablerodenotas.entidades.User;

 

public interface UserRepo extends JpaRepository<User, Long> {
    
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, Long id);

    boolean existsByEmailAndIdNot(String email, Long id);

    long countByRoleIgnoreCaseAndActiveTrue(String role);

}
