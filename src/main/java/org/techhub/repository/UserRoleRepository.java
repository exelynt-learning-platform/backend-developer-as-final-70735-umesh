package org.techhub.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.techhub.entity.UserRole;

public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
}