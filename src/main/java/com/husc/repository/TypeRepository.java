package com.husc.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.husc.models.Type;

public interface TypeRepository extends JpaRepository<Type, Long> {
	Optional<Type> findById(Long id);
}
