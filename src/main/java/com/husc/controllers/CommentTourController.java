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

import com.husc.models.CommentTour;
import com.husc.models.Tour;
import com.husc.models.User;
import com.husc.repository.ReviewTourRepository;
import com.husc.repository.TourRepository;
import com.husc.repository.UserRepository;

@Controller
@RequestMapping("/commenttours")
public class CommentTourController {

	@Autowired
	ReviewTourRepository reviewTourRepository;

	@Autowired
	TourRepository tourRepository;

	@Autowired
	UserRepository userRepository;
	@PostMapping("/{id}")
	public String createdcomment(@PathVariable Long id, @RequestParam String content) {
    	String username = SecurityContextHolder.getContext().getAuthentication().getName();
    	Optional<User> user = userRepository.findByUsername(username);
		Optional<Tour> tour = tourRepository.findById(id);
		CommentTour comment = new CommentTour();
		if (tour.isPresent()) {
			if (user.isPresent()) {
				comment.setCreatedBy(user.get().getName());
				comment.setCreatedDate(new Date());
				comment.setContent(content);
				comment.setTour(tour.get());
				comment.setUser(user.get());
			}
			else {
				return  "redirect:/tour/gettour/"+ tour.get().getId() + "?error=true";
			}
		}
		reviewTourRepository.save(comment);
		return "redirect:/tour/gettour/"+ tour.get().getId();
	}
}