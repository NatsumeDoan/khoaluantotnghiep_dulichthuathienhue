package com.husc.controllers;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.husc.models.User;
import com.husc.payload.request.PasswordChangeRequest;
import com.husc.repository.RoleRepository;
import com.husc.repository.UserRepository;
import com.husc.security.jwt.JwtUtils;

@Controller
@RequestMapping("/user")
public class UserController {
	@Autowired
	UserRepository userRepository;

	@Autowired
	RoleRepository roleRepository;
	
	@Autowired
	PasswordEncoder passwordEncoder;
	
	@Autowired
	JwtUtils jwtUtils;
	
	@GetMapping("/formchangepassword")
	public String getformchangepassword(Model model) {
		PasswordChangeRequest passwordChangeRequest = new PasswordChangeRequest();
		model.addAttribute("passwordchange", passwordChangeRequest);
		return "changepassword";
	}

	@GetMapping("/profile")
	public String  getUserById( Model model) {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		Optional<User> user = userRepository.findByUsername(username);
		if(user.isEmpty()) {
			 return "redirect:/auth/formsignin";
		}
			model.addAttribute("user", user.get());
			return "profile";
	}

	@PostMapping("/edituser")
	public String updateUser(@RequestParam String username, @RequestParam String name, @RequestParam String gender) {
		Optional<User> optionalUser = userRepository.findByUsername(username);
		if (optionalUser.isPresent()) {
			User user = optionalUser.get();
			user.setName(name);
			user.setGender(gender);
			userRepository.save(user);
		}
		return "redirect:/user/profile";
	}

	@PostMapping("/changepassword")
	public String changePassword(
			@ModelAttribute("user") PasswordChangeRequest passwordChangeRequest) {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		Optional<User> optionalUser = userRepository.findByUsername(username);
		if (optionalUser.isPresent()) {
			User user = optionalUser.get();
			if (passwordEncoder.matches(passwordChangeRequest.getOldPassword(), user.getPassword()) &&passwordEncoder.matches(passwordChangeRequest.getOldPassword2(), user.getPassword())) {
				user.setPassword(passwordEncoder.encode(passwordChangeRequest.getNewPassword()));
				userRepository.save(user);
			}
		}
				return "redirect:/user/profile";
	}
	
}
