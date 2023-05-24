package com.husc.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.husc.models.User;

public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByUsername(String username);

	Boolean existsByUsername(String username);

	Boolean existsByEmail(String email);
	
	Boolean existsByNumberPhone(String numeberPhone);
	
}