package com.husc.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.husc.models.CommentPlace;

public interface CommentRepository extends JpaRepository<CommentPlace, Long> {
}