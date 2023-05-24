package com.husc.services;

import org.springframework.data.domain.Page;

import com.husc.models.Booking;

public interface BookingService {
	Page<Booking> findPaginated(int pageNo, int pageSize);
	Page<Booking> searchBookings(String keyword, int pageNo, int pageSize);
}
