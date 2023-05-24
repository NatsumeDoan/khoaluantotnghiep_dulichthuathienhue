package com.husc.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.husc.models.CommentTour;

public interface ReviewTourRepository extends JpaRepository<CommentTour, Long> {

}
