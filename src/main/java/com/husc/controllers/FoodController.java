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

import com.husc.models.CommentFood;
import com.husc.models.Food;
import com.husc.models.ListImageFood;
import com.husc.repository.FoodRepository;
import com.husc.repository.ListImageFoodRepository;
import com.husc.repository.UserRepository;
import com.husc.services.FoodService;

@Controller
@RequestMapping("/food")
public class FoodController {
	
	@Autowired
	FoodRepository foodRepository;

	@Autowired
	ListImageFoodRepository listImageFoodRepository;

	@Autowired
	UserRepository userRepository;
	
	@Autowired
	FoodService foodService;
	
	@GetMapping
	public String showhome(Model model) {
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
			Page<Food> page = foodService.searchHomeFoods(keyword, pageNo, pageSize );
			List<Food> foodlist = page.getContent();
			model.addAttribute("totalPages", page.getTotalPages());
			model.addAttribute("totalItems", page.getTotalElements());
			model.addAttribute("listfood", foodlist);
			model.addAttribute("currentPage", pageNo);
			model.addAttribute("keyword", keyword);
		return "homefood";
	}
	
	@GetMapping("/getfood/{id}")
	public String getfood(@PathVariable Long id, Model model) {
		Optional<Food> food = foodRepository.findById(id);
		List<String> image = new ArrayList<>();
		List<CommentFood> listcomment = food.get().getComments();
		model.addAttribute("food", food.get());
		for (ListImageFood i : food.get().getListImageFood()) {
			image.add(i.getUrlImage());
		}
		model.addAttribute("image", image);
		model.addAttribute("listcomment", listcomment);
		return "foodid";
	}
}
