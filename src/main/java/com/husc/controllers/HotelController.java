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

import com.husc.models.Hotel;
import com.husc.repository.CategoryRepository;
import com.husc.repository.HotelRepository;
import com.husc.repository.ListImageHotelRepository;
import com.husc.repository.TypeRepository;
import com.husc.repository.UserRepository;
import com.husc.services.HotelService;

@Controller
@RequestMapping("/hotel")
public class HotelController {

	@Autowired
	HotelRepository hotelRepository;
	
	@Autowired
	HotelService hotelService;

	@Autowired
	UserRepository userRepository;

	@Autowired
	TypeRepository typeRepository;

	@Autowired
	CategoryRepository categoryRepository;

	@Autowired
	ListImageHotelRepository listImageHotelRepository;
	
	@GetMapping
	public String showhomehotel(Model model) {
		String keyword = "";
		model.addAttribute("keyword", keyword);
		return findPaginated("", 1, model);
	}
	
	@GetMapping("/search")
	public String showSearch(@RequestParam(value = "keyword") String keyword,Model model) {
		return findPaginated(keyword,1, model);
	}
	
	@GetMapping("/page/{pageNo}")
	public String findPaginated(@RequestParam(value = "keyword") String keyword,@PathVariable (value = "pageNo") int pageNo, 
			Model model) {
		int pageSize = 5;
			Page<Hotel> page = hotelService.searchHomeHotels(keyword, pageNo, pageSize);
			List<Hotel> hotellist = page.getContent();
			model.addAttribute("totalPages", page.getTotalPages());
			model.addAttribute("totalItems", page.getTotalElements());
			model.addAttribute("hotelList", hotellist);
			model.addAttribute("currentPage", pageNo);
			model.addAttribute("keyword", keyword);
		return "homehotel";
	}
}
