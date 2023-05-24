package com.husc.controllers;

import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.husc.models.CommentPlace;
import com.husc.models.Place;
import com.husc.models.User;
import com.husc.repository.CommentRepository;
import com.husc.repository.PlaceRepository;
import com.husc.repository.UserRepository;

@CrossOrigin(origins = "*", maxAge = 3600)
@Controller
@RequestMapping("/comments")
public class CommentPlaceController {
	@Autowired
	CommentRepository commentRepository;

	@Autowired
	PlaceRepository placeRepository;

	@Autowired
	UserRepository userRepository;

	@PostMapping("/{id}")
	public String createdcomment(@PathVariable Long id, @RequestParam String content) {
    	String username = SecurityContextHolder.getContext().getAuthentication().getName();
    	Optional<User> user = userRepository.findByUsername(username);
		Optional<Place> place = placeRepository.findById(id);
		CommentPlace comment = new CommentPlace();
		if (place.isPresent()) {
			if (user.isPresent()) {
				comment.setCreatedBy(user.get().getName());
				comment.setCreatedDate(new Date());
				comment.setContent(content);
				comment.setPlace(place.get());
				comment.setUser(user.get());
				commentRepository.save(comment);
			}
			else {
				return  "redirect:/place/getplace/"+ place.get().getId() + "?error=true";
			}
		}
		return "redirect:/place/getplace/"+ place.get().getId();

	}
}


