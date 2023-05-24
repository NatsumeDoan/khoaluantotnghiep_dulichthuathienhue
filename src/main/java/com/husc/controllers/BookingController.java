package com.husc.controllers;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.husc.models.Booking;
import com.husc.models.Tour;
import com.husc.models.User;
import com.husc.repository.BookingRepository;
import com.husc.repository.TourRepository;
import com.husc.repository.UserRepository;
import com.husc.services.CurrencyService;

@Controller
@RequestMapping("/booking")
public class BookingController {

	@Autowired
	BookingRepository bookingRepository;

	@Autowired
	TourRepository tourRepository;

	@Autowired
	UserRepository userRepository;
	
	@Autowired
	CurrencyService currencyService;

	@GetMapping("payment/{id}")
	public String payment(@PathVariable long id, Model model) {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		Optional<User> user = userRepository.findByUsername(username);
		if(user.isEmpty()) {
			return "redirect:/auth/formsignin";
		}
		Booking booking = bookingRepository.findById(id).get();
		model.addAttribute("user", user.get());
		double usd = currencyService.convertVndToUsd(booking.getPrice());
		Tour tour = tourRepository.findByTourCode(booking.getTourcode());
		model.addAttribute("tour", tour);
		model.addAttribute("booking", booking);	
		model.addAttribute("priceUsd", usd);
		if(booking.getIsPaying()) {
			return "bookingdetail";		
		}
		if(username.equals(booking.getUser().getUsername())){
			return "payment";
		}
		return "redirect:/place";
	}
	@GetMapping("detail/{id}")
	public String detail(@PathVariable long id, Model model) {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		Optional<User> user = userRepository.findByUsername(username);
		if(user.isEmpty()) {
			return "redirect:/auth/formsignin";
		}
		Booking booking = bookingRepository.findById(id).get();
		model.addAttribute("user", user.get());
		double usd = currencyService.convertVndToUsd(booking.getPrice());
		Tour tour = tourRepository.findByTourCode(booking.getTourcode());
		
		model.addAttribute("tour", tour);
		model.addAttribute("booking", booking);	
		model.addAttribute("priceUsd", usd);
		if(username.equals(booking.getUser().getUsername())){
			return "bookingdetail";	
		}
			return "redirect:/place";		
	}
	@GetMapping("/listbooking")
	public String listbooking(Model model) {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		Optional<User> user = userRepository.findByUsername(username);
		if(user.isEmpty()) {
			model.addAttribute("listbooking", null);
		}
		else {
			List<Booking> listbooking = bookingRepository.findByUser(user.get());
			model.addAttribute("listbooking", listbooking);
		}
		return "statistical";
	}
	
	@PostMapping("/{id}")
	public String addBooking(@PathVariable Long id,
			@RequestParam LocalDate bookingDate, @RequestParam String email, @RequestParam String fullname,  
			@RequestParam String numberphone, @RequestParam Integer quanlity) {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		Optional<User> user = userRepository.findByUsername(username);
		Optional<Tour> tour = tourRepository.findById(id);
		List<Tour> t = new ArrayList<>();
		t.add(tour.get());
		Date now = new Date();
		Booking booking = new Booking();
		if (user.isPresent()) {
			if (tour.isPresent()) {
				booking.setIsPaying(false);
				booking.setIsUse(false);
				booking.setBookingDate(bookingDate);
				booking.setEmail(email);
				booking.setFullname(fullname);
				booking.setNgaydat(now);
				booking.setNumberPhone(numberphone);
				booking.setPrice(tour.get().getPrice());
				booking.setQuantity(quanlity);
				booking.setTotal(tour.get().getPrice() * quanlity);
				booking.setTour(t);
				booking.setBookingCode( UUID.randomUUID().toString().substring(0, 5));
				booking.setIsPaying(false);
				booking.setIsUse(false);
				booking.setTourcode(tour.get().getTourCode());
				booking.setUser(user.get());
				bookingRepository.save(booking);
			}
		}
		else return  "redirect:/auth/formsignin";
		return "redirect:/booking/payment/"+booking.getId();
	}
	
}
