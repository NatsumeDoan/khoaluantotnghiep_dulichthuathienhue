package com.husc.controllers;

import java.util.Date;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.husc.models.ERole;
import com.husc.models.Role;
import com.husc.models.User;
import com.husc.payload.request.LoginRequest;
import com.husc.payload.request.SignupRequest;
import com.husc.repository.RoleRepository;
import com.husc.repository.UserRepository;
import com.husc.security.jwt.JwtUtils;
import com.husc.security.services.UserDetailsImpl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@CrossOrigin(origins = "*", maxAge = 3600)
@Controller
@RequestMapping("/auth")
public class AuthController {
	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	UserRepository userRepository;

	@Autowired
	RoleRepository roleRepository;

	@Autowired
	PasswordEncoder encoder;

	@Autowired
	JwtUtils jwtUtils;

	@GetMapping("/formsignin")
	public String showformsignin(Model model, HttpServletRequest request) {
		String token = jwtUtils.getJwtFromCookies(request);
    	if (token == null || token.isEmpty()) {
    		LoginRequest loginRequest = new LoginRequest();
    		model.addAttribute("user", loginRequest);
    		return "signin";
    	}
    	return "redirect:/place";
	}

	@GetMapping("/formsignup")
	public String showRegistrationForm(Model model,HttpServletRequest request) {
		String token = jwtUtils.getJwtFromCookies(request);
		if(token == null || token.isEmpty()) {
			SignupRequest signupRequest = new SignupRequest();
			model.addAttribute("user", signupRequest);
			return "signup";
		}
		return "redirect:/place";
	}
	

	@PostMapping("/signin")
	public String authenticateUser(@Valid @RequestParam String username, @RequestParam String password,
			HttpServletResponse httpServletResponse, Model model) {
		LoginRequest loginRequest = new LoginRequest();
		loginRequest.setPassword(password);
		loginRequest.setUsername(username);
		try {
			Authentication authentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

			SecurityContextHolder.getContext().setAuthentication(authentication);

			UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

			ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);

			httpServletResponse.addHeader(HttpHeaders.SET_COOKIE, jwtCookie.toString());
			return "redirect:/place";
		} catch (AuthenticationException e) {
			model.addAttribute("status", false);
			return "redirect:/auth/formsignin";
		}

	}

	@GetMapping("/logout")
	public String logout(HttpServletResponse response) {
		ResponseCookie cookie = jwtUtils.getCleanJwtCookie();
		response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
		SecurityContextHolder.clearContext();
		return "redirect:/auth/formsignin";
	}

	@PostMapping("/signup")
	public String registerUser(@Valid @ModelAttribute("user") SignupRequest signUpRequest) {
		if (userRepository.existsByUsername(signUpRequest.getUsername())) {
			return "redirect:/api/auth";
		}

		if (userRepository.existsByEmail(signUpRequest.getEmail())) {
			return "redirect:/api/auth";
		}
		if (userRepository.existsByNumberPhone(signUpRequest.getNumberPhone())) {
			return "redirect:/api/auth";
		}

		// Create new user's account
		User user = new User(signUpRequest.getUsername(), signUpRequest.getEmail(), signUpRequest.getNumberPhone(),
				signUpRequest.getName(), signUpRequest.getDateOfBirth(), signUpRequest.getGender(),
				encoder.encode(signUpRequest.getPassword()), new Date());

		Set<Role> roles = new HashSet<>();
		Optional<Role> userRole = roleRepository.findByName(ERole.ROLE_USER);
		roles.add(userRole.get());
		user.setRoles(roles);
		userRepository.save(user);

		return "redirect:/auth/formsignin";
	}
}