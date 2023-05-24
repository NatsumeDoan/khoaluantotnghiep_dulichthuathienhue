package com.husc.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.husc.models.Booking;
import com.husc.repository.BookingRepository;
import com.husc.repository.TourRepository;
import com.husc.repository.UserRepository;
import com.husc.services.BookingService;
import com.husc.services.CurrencyService;

@Controller
@RequestMapping("/admin/booking")
public class BookingAdmincontroller {

	@Autowired
	BookingRepository bookingRepository;

	@Autowired
	TourRepository tourRepository;
	
	@Autowired
	BookingService bookingService;

	@Autowired
	UserRepository userRepository;
	
	@Autowired
	CurrencyService currencyService;
	
	@GetMapping("/bookingtour")
	public String showPlace(Model model) {
		String keyword = "";
		model.addAttribute("keyword", keyword);
		return findPaginatedadmin("", 1,model);
	}
	
	@GetMapping("/search")
	public String showSearch(@RequestParam(value = "keyword") String keyword,Model model) {
		return findPaginatedadmin(keyword,1, model);
	}
	
	@GetMapping("/page/{pageNo}")
	public String findPaginatedadmin(@RequestParam(value = "keyword") String keyword,@PathVariable (value = "pageNo") int pageNo, 
			Model model) {
		int pageSize = 10;
			Page<Booking> page = bookingService.searchBookings(keyword, pageNo, pageSize);
			List<Booking> bookings = page.getContent();
			model.addAttribute("totalPages", page.getTotalPages());
			model.addAttribute("totalItems", page.getTotalElements());
			model.addAttribute("bookingtours", bookings);
			model.addAttribute("currentPage", pageNo);
			model.addAttribute("keyword", keyword);
		return "bookingtour";
	}
	
	@GetMapping("/use/{id}")
	public String usebooking(@PathVariable long id ,Model model) {
		Booking booking = bookingRepository.findById(id).get();
		booking.setIsUse(true);
		bookingRepository.save(booking);
		List<Booking> bookings = bookingRepository.findAll();
		model.addAttribute("bookingtours", bookings);
		return "bookingtour";
	}
}
