package com.husc.controllers;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.husc.models.CommentFood;
import com.husc.models.Food;
import com.husc.models.ListImageFood;
import com.husc.models.User;
import com.husc.repository.FoodRepository;
import com.husc.repository.ListImageFoodRepository;
import com.husc.repository.UserRepository;
import com.husc.services.FoodService;

@Controller
@RequestMapping("/admin/food")
public class FoodAdminController {
	private static final Path CURRENT_FOLDER = Paths.get(System.getProperty("user.dir"));
	public static Logger logger = LoggerFactory.getLogger(FoodController.class);
	
	@Autowired
	FoodRepository foodRepository;

	@Autowired
	ListImageFoodRepository listImageFoodRepository;

	@Autowired
	UserRepository userRepository;
	
	@Autowired
	FoodService foodService;

	@GetMapping
	public String getallFood(Model model) {
		String keyword = "";
		model.addAttribute("keyword", keyword);
		return findPaginated("", 1, "title", "asc", model);
	}
	
	@GetMapping("/search")
	public String showSearch(@RequestParam(value = "keyword") String keyword,Model model) {
		return findPaginated(keyword,1, "title", "asc", model);
	}
	
	@GetMapping("/formaddfood")
	public String formaddfood(Model model) {
		Food food = new Food();
		model.addAttribute("food",food);
		return "new_food";
	}
	@GetMapping("/editfood/{id}")
	public String formeditfood(@PathVariable long id,Model model) {
		Food food = foodRepository.findById(id).get();
		model.addAttribute("food",food);
		return "editfood";
	}

	@GetMapping("/page/{pageNo}")
	public String findPaginated(@RequestParam(value = "keyword") String keyword,@PathVariable (value = "pageNo") int pageNo, 
			@RequestParam("sortField") String sortField,
			@RequestParam("sortDir") String sortDir,
			Model model) {
		int pageSize = 5;
			Page<Food> page = foodService.searchFoods(keyword, pageNo, pageSize, sortField, sortDir);
			List<Food> foodlist = page.getContent();
			model.addAttribute("totalPages", page.getTotalPages());
			model.addAttribute("totalItems", page.getTotalElements());
			model.addAttribute("listfood", foodlist);
			model.addAttribute("currentPage", pageNo);
			model.addAttribute("keyword", keyword);
			model.addAttribute("sortField", sortField);
			model.addAttribute("sortDir", sortDir);
			model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");	
		return "food";
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

	@PostMapping("/addfood")
	public String create(@RequestParam String title, @RequestParam String address,
			@RequestParam String description, @RequestParam("image") MultipartFile image,
			@RequestParam("images") MultipartFile[] images, @RequestParam Double price) throws IOException {
		Path staticPath = Paths.get("static");
		Path imagePath = Paths.get("image");
		if (!Files.exists(CURRENT_FOLDER.resolve("src\\main\\resources").resolve(staticPath).resolve(imagePath))) {
			Files.createDirectories(
					CURRENT_FOLDER.resolve("src\\main\\resources").resolve(staticPath).resolve(imagePath));
		}
		String newName = UUID.randomUUID().toString()
				+ image.getOriginalFilename().substring(image.getOriginalFilename().lastIndexOf("."));

		Path file = CURRENT_FOLDER.resolve("src\\main\\resources").resolve(staticPath).resolve(imagePath)
				.resolve(newName);
		try (OutputStream os = Files.newOutputStream(file)) {
			os.write(image.getBytes());
		}
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
    	Optional<User> user = userRepository.findByUsername(username);
		Food food = new Food();
		if (user.isPresent()) {
			food.setAddress(address);
			food.setDescription(description);
			food.setTitle(title);
			food.setPrice(price);
			food.setUser(user.get());
			Date date = new Date();
			food.setCreatedDate(date);
			food.setCreatedBy(user.get().getName());
			food.setImage("/image/" + newName);
		}
		List<ListImageFood> listImage = new ArrayList<>();
		for (MultipartFile imagefile : images) {
			String newFileName = UUID.randomUUID().toString()
					+ imagefile.getOriginalFilename().substring(imagefile.getOriginalFilename().lastIndexOf("."));
			Path fileimage = CURRENT_FOLDER.resolve("src\\main\\resources").resolve(staticPath).resolve(imagePath)
					.resolve(newFileName);
			try (OutputStream os = Files.newOutputStream(fileimage)) {
				os.write(imagefile.getBytes());
			}
			ListImageFood newImage = new ListImageFood();
			newImage.setUrlImage("/image/" + newFileName);
			newImage.setFood(food);
			listImage.add(newImage);
		}
		food.setListImageFood(listImage);
		foodRepository.save(food);
		return "redirect:/admin/food";
	}

	@PostMapping("/editfood")
	public String updateplace(@RequestParam long id, @RequestParam String title, @RequestParam String address,@RequestParam Double price,
			@RequestParam String description, @RequestParam("image") MultipartFile image,
			@RequestParam("images") MultipartFile[] images) throws IOException {
		Food food = foodRepository.findById(id).get();
		Path staticPath = Paths.get("static");
		Path imagePath = Paths.get("image");
		if(image.getOriginalFilename() != null && !image.getOriginalFilename().isEmpty()) {
			String imgPath = CURRENT_FOLDER + "/src/main/resources/static" + food.getImage();
			File imgFile = new File(imgPath);
			if (imgFile.exists()) {
				imgFile.delete();
			}	
			if (!Files.exists(CURRENT_FOLDER.resolve("src\\main\\resources").resolve(staticPath).resolve(imagePath))) {
				Files.createDirectories(
						CURRENT_FOLDER.resolve("src\\main\\resources").resolve(staticPath).resolve(imagePath));
			}
			String newName = UUID.randomUUID().toString()
					+ image.getOriginalFilename().substring(image.getOriginalFilename().lastIndexOf("."));
			Path file = CURRENT_FOLDER.resolve("src\\main\\resources").resolve(staticPath).resolve(imagePath)
					.resolve(newName);
			try (OutputStream os = Files.newOutputStream(file)) {
				os.write(image.getBytes());
			}
			food.setImage("/image/" +newName);
		}
			food.setTitle(title);
			food.setPrice(price);
			food.setAddress(address);
			food.setDescription(description);
			
		if(images != null && images.length > 0 && !images[0].getOriginalFilename().equals("")) {
			for (ListImageFood img : food.getListImageFood()) {
				String imgPath = CURRENT_FOLDER + "/src/main/resources/static" + img.getUrlImage();
				File imageFile = new File(imgPath);
				if (imageFile.exists()) {
					imageFile.delete();
				}
				listImageFoodRepository.delete(img);
			}
			List<ListImageFood> listImage = new ArrayList<>();
			for (MultipartFile imagefile : images) {
				String newFileName = UUID.randomUUID().toString()
						+ imagefile.getOriginalFilename().substring(imagefile.getOriginalFilename().lastIndexOf("."));
				Path fileimage = CURRENT_FOLDER.resolve("src\\main\\resources").resolve(staticPath).resolve(imagePath)
						.resolve(newFileName);
				try (OutputStream os = Files.newOutputStream(fileimage)) {
					os.write(imagefile.getBytes());
				}
				ListImageFood newImage = new ListImageFood();
				newImage.setUrlImage("/image/" + newFileName);
				newImage.setFood(food);
				listImage.add(newImage);
			}
			food.setListImageFood(null);
			food.setListImageFood(listImage);
		}
		foodRepository.save(food);
		return "redirect:/admin/food";
	}
	@GetMapping("/deletefood/{id}")
	public String deleteFood(@PathVariable Long id) {
		Optional<Food> optionalFood = foodRepository.findById(id);
		if (optionalFood.isPresent()) {
			Food food = optionalFood.get();
			if (food.getImage() != null) {
				String imagePath = CURRENT_FOLDER + "/src/main/resources/static" + food.getImage();
				File imageFile = new File(imagePath);
				if (imageFile.exists()) {
					imageFile.delete();
				}
			}
			for (ListImageFood image : optionalFood.get().getListImageFood()) {
				String imagePath = CURRENT_FOLDER + "/src/main/resources/static" + image.getUrlImage();
				File imageFile = new File(imagePath);
				if (imageFile.exists()) {
					imageFile.delete();
				}
				listImageFoodRepository.delete(image);
			}
			foodRepository.deleteById(id);
			return "redirect:/admin/food";
		}
		return "redirect:/admin/food";
	}

}
