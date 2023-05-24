package com.husc.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.husc.models.Booking;
import com.husc.models.Tour;
import com.husc.models.User;

public interface BookingRepository extends JpaRepository<Booking, Long>{
	List<Booking> findByTour(Tour tour);
	List<Booking> findByUser(User user);
	
	
	@Query("select f from Booking f where f.bookingCode like %?1% or f.fullname like %?1% or f.tourcode like %?1%" )
	Page<Booking> searchProducts(String keyword, Pageable pageable);

}
