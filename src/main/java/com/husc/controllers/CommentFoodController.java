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

import com.husc.models.CommentFood;
import com.husc.models.Food;
import com.husc.models.User;
import com.husc.repository.FoodRepository;
import com.husc.repository.ReviewFoodRepository;
import com.husc.repository.UserRepository;

@Controller
@RequestMapping("/reviewfoods")
public class CommentFoodController {

	@Autowired
	ReviewFoodRepository reviewFoodRepository;

	@Autowired
	FoodRepository foodRepository;

	@Autowired
	UserRepository userRepository;
	
	@PostMapping("/{id}")
	public String createdfoodcomment(@PathVariable Long id, @RequestParam String content) {
    	String username = SecurityContextHolder.getContext().getAuthentication().getName();
    	Optional<User> user = userRepository.findByUsername(username);
		Optional<Food> food = foodRepository.findById(id);
		CommentFood comment = new CommentFood();
		if (food.isPresent()) {
			if (user.isPresent()) {
				comment.setCreatedBy(user.get().getName());
				comment.setCreatedDate(new Date());
				comment.setContent(content);
				comment.setFood(food.get());
				comment.setUser(user.get());
			}
			else {
				return  "redirect:/food?error=true";
			}
		}
		reviewFoodRepository.save(comment);
		return "redirect:/food";

	}

	
}
