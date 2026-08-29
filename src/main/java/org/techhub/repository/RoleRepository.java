package org.techhub.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.techhub.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(String name);
}