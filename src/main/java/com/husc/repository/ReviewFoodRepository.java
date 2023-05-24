package com.husc.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.husc.models.CommentFood;

public interface ReviewFoodRepository extends JpaRepository<CommentFood, Long>{

}
