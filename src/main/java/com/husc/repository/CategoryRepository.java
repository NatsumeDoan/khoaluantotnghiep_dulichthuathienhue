package com.husc.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.husc.models.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {

}
