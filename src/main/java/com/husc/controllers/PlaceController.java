package com.husc.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.husc.models.CommentPlace;
import com.husc.models.ListImagePlace;
import com.husc.models.Place;
import com.husc.repository.CommentRepository;
import com.husc.repository.PlaceRepository;
import com.husc.repository.UserRepository;
import com.husc.security.jwt.JwtUtils;
import com.husc.services.PlaceService;

@Controller
@RequestMapping("/place")
public class PlaceController {

	@Autowired
	PlaceRepository placeRepository;
	
	@Autowired
	PlaceService placeService;

	@Autowired
	UserRepository userRepository;
	
	@Autowired
	CommentRepository commentRepository;
	
	@Autowired
	JwtUtils jwtUtils;
	
	@GetMapping
	public String showhome(Model model) {
		String keyword = "";
		model.addAttribute("keyword", keyword);
		return findPaginated("", 1, model);
	}
	@GetMapping("/search")
	public String showSearchhome(@RequestParam(value = "keyword") String keyword,Model model) {
		return findPaginated(keyword,1, model);
	}
	@GetMapping("/page/{pageNo}")
	public String findPaginated(@RequestParam(value = "keyword") String keyword,@PathVariable (value = "pageNo") int pageNo,
			Model model) {
		int pageSize = 12;
			Page<Place> page = placeService.searchHomePlaces(keyword, pageNo, pageSize);
			List<Place> placelist = page.getContent();
			model.addAttribute("totalPages", page.getTotalPages());
			model.addAttribute("totalItems", page.getTotalElements());
			model.addAttribute("placeList", placelist);
			model.addAttribute("currentPage", pageNo);
			model.addAttribute("keyword", keyword);
		return "home";
	}
	@GetMapping("/getplace/{id}")
	public String getplace(@PathVariable Long id, Model model) {
		Optional<Place> place = placeRepository.findById(id);
		List<String> image = new ArrayList<>();
		List<CommentPlace> listcomment = place.get().getComments();
		model.addAttribute("place", place.get());
		for (ListImagePlace i : place.get().getListImagePlace()) {
			image.add(i.getUrlImage());
		}
		model.addAttribute("image", image);
		model.addAttribute("listcomment", listcomment);
		return "placeid";
	}
}
