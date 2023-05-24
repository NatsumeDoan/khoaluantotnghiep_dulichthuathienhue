package com.husc.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.husc.models.Hotel;
import com.husc.models.Type;

public interface HotelRepository extends JpaRepository<Hotel, Long>{
	List<Hotel> findByType(Type type);
	@Query("select f from Hotel f where f.title like %?1%")
    Page<Hotel> searchhotels(String keyword, Pageable pageable);
}
