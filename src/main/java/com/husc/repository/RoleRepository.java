package com.husc.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.husc.models.ERole;
import com.husc.models.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
	Optional<Role> findByName(ERole name);
}
