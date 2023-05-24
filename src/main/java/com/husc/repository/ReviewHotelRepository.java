package com.husc.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.husc.models.CommentHotel;

public interface ReviewHotelRepository extends JpaRepository<CommentHotel, Long> {

}
