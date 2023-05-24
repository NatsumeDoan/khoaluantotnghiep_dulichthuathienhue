package com.husc.controllers;

import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.husc.models.CommentHotel;
import com.husc.models.Hotel;
import com.husc.models.User;
import com.husc.repository.HotelRepository;
import com.husc.repository.ReviewHotelRepository;
import com.husc.repository.UserRepository;

@Controller
@RequestMapping("/reviewhotels")
public class CommentHotelController {

	@Autowired
	ReviewHotelRepository reviewHotelRepository;

	@Autowired
	HotelRepository hotelRepository;

	@Autowired
	UserRepository userRepository;

	@PostMapping("/{id}")
	public String createdcomment(@PathVariable Long id, @RequestParam String content) {
    	String username = SecurityContextHolder.getContext().getAuthentication().getName();
    	Optional<User> user = userRepository.findByUsername(username);
		Optional<Hotel> hotel = hotelRepository.findById(id);
		CommentHotel comment = new CommentHotel();
		if (hotel.isPresent()) {
			if (user.isPresent()) {
				comment.setCreatedBy(user.get().getName());
				comment.setCreatedDate(new Date());
				comment.setContent(content);
				comment.setHotel(hotel.get());
				comment.setUser(user.get());
			}
			else {
				return  "redirect:/hotel/?error=true";
			}
		}
		reviewHotelRepository.save(comment);
		return "redirect:/hotel";
	}
}