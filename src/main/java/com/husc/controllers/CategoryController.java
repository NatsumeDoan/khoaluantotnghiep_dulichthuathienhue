package com.husc.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.husc.models.Category;
import com.husc.repository.CategoryRepository;

@Controller
@RequestMapping("/category")
public class CategoryController {
	@Autowired
	CategoryRepository categoryRepository;
	
	@GetMapping("/formaddcatogory")
	public String formaddcategory(Model model) {
		Category category = new Category();
		model.addAttribute("category", category);
		return "addcategory";
	}
	
	@GetMapping("/formeditcategory/{id}")
	public String formeditcategory(@PathVariable Long id, Model model) {
		Optional<Category> category = categoryRepository.findById(id);
		model.addAttribute("category", category.get());
		return "editcategory";
	}
	
	@GetMapping
	public String  getlistcategory(Model model){
		List<Category> categories = categoryRepository.findAll();
		model.addAttribute("categories", categories);
		return "category";
	}
	
	@PostMapping("/addcategory")
	public String addcategory(@ModelAttribute("category") Category category){
		categoryRepository.save(category);
		return "redirect:/category";
	}
	
	@PostMapping("/editcategory/{id}")
	public String editcategory(@PathVariable Long id,@RequestParam String name){
		Optional<Category> category= categoryRepository.findById(id);
		category.get().setName(name);
		categoryRepository.save(category.get());
		return "redirect:/category";
	}
	
	@GetMapping("/deletecategory/{id}")
	public String deletecategory(@PathVariable Long id) {
		categoryRepository.deleteById(id);
		return "redirect:/category";
	}
}
