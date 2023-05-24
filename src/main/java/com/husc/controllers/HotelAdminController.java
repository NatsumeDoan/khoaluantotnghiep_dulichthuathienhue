package com.husc.controllers;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.husc.models.Category;
import com.husc.models.Hotel;
import com.husc.models.ListImageHotel;
import com.husc.models.Type;
import com.husc.models.User;
import com.husc.repository.CategoryRepository;
import com.husc.repository.HotelRepository;
import com.husc.repository.ListImageHotelRepository;
import com.husc.repository.TypeRepository;
import com.husc.repository.UserRepository;
import com.husc.services.HotelService;

@Controller
@RequestMapping("/admin/hotel")
public class HotelAdminController {

	private static final Path CURRENT_FOLDER = Paths.get(System.getProperty("user.dir"));
	public static Logger logger = LoggerFactory.getLogger(HotelAdminController.class);

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
	
	@GetMapping("/formnewhotel")
	public String formnewhotel(Model model) {
		List<Type> type = typeRepository.findAll();
		List<Category> categories = categoryRepository.findAll();
		model.addAttribute("types", type);
		model.addAttribute("categories", categories);
		return "addhotel";
	}
	
	@GetMapping("/formedithotel/{id}")
	public String formedithotel(@PathVariable Long id,Model model) {
		Hotel hotel = hotelRepository.findById(id).get();
		List<Category> categories = categoryRepository.findAll();
		List<Type> type = typeRepository.findAll();
		model.addAttribute("types", type);
		model.addAttribute("categories", categories);
		model.addAttribute("hotel", hotel);
		return "edithotel";
	}
	
	@GetMapping
	public String getallhotel(Model model) {
		List<Category> categoried= new ArrayList<>();
		model.addAttribute("cate", categoried);
		List<Category> categories = categoryRepository.findAll();
		model.addAttribute("categories", categories);
		List<Hotel> listHotel =  hotelRepository.findAll();
		model.addAttribute("listHotel", listHotel);
		return "hotel";
	}
	@GetMapping("/type/{typeid}")
	public String getallhotel(@PathVariable Long typeid, Model model) {
		List<Category> categoried= new ArrayList<>();
		model.addAttribute("cate", categoried);
		List<Category> categories = categoryRepository.findAll();
		model.addAttribute("categories", categories);
		Type type = typeRepository.findById(typeid).get();
		List<Hotel> listHotel =  hotelRepository.findByType(type);
		model.addAttribute("listHotel", listHotel);
		return "hotel";
	}
	@GetMapping("/loc")
	public String getallhotel(@RequestParam List<Long> categories, Model model) {
		List<Category> cate = categoryRepository.findAll();
		model.addAttribute("categories", cate);
		if(categories.isEmpty()) {
			List<Hotel> listHotel =  hotelRepository.findAll();
			model.addAttribute("listHotel", listHotel);
		}
		else {
		 Set<Category> category = new HashSet<>();
			for (Long c_id : categories) {
				category.add(categoryRepository.findById(c_id).get());
			}
		model.addAttribute("cate", category);
		List<Hotel> listHotel =  hotelService.findByCategories(categories);
		model.addAttribute("listHotel", listHotel);}
		return "hotel";
	}

	@PostMapping
	public String addhotel(@RequestParam Long type,
			@RequestParam List<Long> categories, @RequestParam String address, @RequestParam String description,
			@RequestParam String title, @RequestParam Double price, @RequestParam MultipartFile image,
			@RequestParam("images") MultipartFile[] images) throws IOException {
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
		Hotel hotel = new Hotel();
		Set<Category> category = new HashSet<>();
		for (Long c_id : categories) {
			category.add(categoryRepository.findById(c_id).get());
		}
		Date now = new Date();
		hotel.setCreatedDate(now);
		hotel.setCreatedBy(user.get().getName());
		hotel.setUser(user.get());
		hotel.setAddress(address);
		hotel.setDescription(description);
		hotel.setTitle(title);
		hotel.setPrice(price);
		hotel.setImage("/image/" + newName);
		hotel.setType(typeRepository.findById(type).get());
		hotel.setCategories(category);
		List<ListImageHotel> listImage = new ArrayList<>();
		for (MultipartFile imagefile : images) {
			String newFileName = UUID.randomUUID().toString()
					+ imagefile.getOriginalFilename().substring(imagefile.getOriginalFilename().lastIndexOf("."));
			Path fileimage = CURRENT_FOLDER.resolve("src\\main\\resources").resolve(staticPath).resolve(imagePath)
					.resolve(newFileName);
			try (OutputStream os = Files.newOutputStream(fileimage)) {
				os.write(imagefile.getBytes());
			}
			ListImageHotel newImage = new ListImageHotel();
			newImage.setUrlImage("/image/" + newFileName);
			newImage.setHotel(hotel);
			listImage.add(newImage);
		}
		hotel.setListImageHotel(listImage);
		hotelRepository.save(hotel);
		return "redirect:/admin/hotel";
	}
	@PostMapping("/updatehotel")
	public String updatehotel(@RequestParam long id, @RequestParam String title,
			@RequestParam Long type, @RequestParam List<Long> categories,@RequestParam String address,@RequestParam Double price,
			@RequestParam String description, @RequestParam("image") MultipartFile image,
			@RequestParam("images") MultipartFile[] images) throws IOException {
		Hotel hotel = hotelRepository.findById(id).get();
		Path staticPath = Paths.get("static");
		Path imagePath = Paths.get("image");
		if(image.getOriginalFilename() != null && !image.getOriginalFilename().isEmpty()) {
			String imgPath = CURRENT_FOLDER + "/src/main/resources/static" + hotel.getImage();
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
			hotel.setImage("/image/" +newName);
		}
		Set<Category> category = new HashSet<>();
		for (Long c_id : categories) {
			category.add(categoryRepository.findById(c_id).get());
		}
			hotel.setCategories(category);
			hotel.setType(typeRepository.findById(type).get());
			hotel.setTitle(title);
			hotel.setPrice(price);
			hotel.setAddress(address);
			hotel.setDescription(description);
			
		if(images != null && images.length > 0 && !images[0].getOriginalFilename().equals("")) {
			for (ListImageHotel img : hotel.getListImageHotel()) {
				String imgPath = CURRENT_FOLDER + "/src/main/resources/static" + img.getUrlImage();
				File imageFile = new File(imgPath);
				if (imageFile.exists()) {
					imageFile.delete();
				}
				listImageHotelRepository.delete(img);
			}
			List<ListImageHotel> listImage = new ArrayList<>();
			for (MultipartFile imagefile : images) {
				String newFileName = UUID.randomUUID().toString()
						+ imagefile.getOriginalFilename().substring(imagefile.getOriginalFilename().lastIndexOf("."));
				Path fileimage = CURRENT_FOLDER.resolve("src\\main\\resources").resolve(staticPath).resolve(imagePath)
						.resolve(newFileName);
				try (OutputStream os = Files.newOutputStream(fileimage)) {
					os.write(imagefile.getBytes());
				}
				ListImageHotel newImage = new ListImageHotel();
				newImage.setUrlImage("/image/" + newFileName);
				newImage.setHotel(hotel);
				listImage.add(newImage);
			}
			hotel.setListImageHotel(listImage);
		}
		hotelRepository.save(hotel);
		return "redirect:/admin/hotel";
	}

	@GetMapping("/findhotelbyid/{id}")
	public ResponseEntity<Hotel> gethotelbyid(@PathVariable Long id) {
		Optional<Hotel> hotel = hotelRepository.findById(id);
		return new ResponseEntity<>(hotelRepository.save(hotel.get()), HttpStatus.OK);
	}

	@GetMapping("/deletehotel/{id}")
	public String deletehotel(@PathVariable long id) {
		Optional<Hotel> optionalHotel = hotelRepository.findById(id);
		if (optionalHotel.isPresent()) {
			Hotel hotel = optionalHotel.get();
			if (hotel.getImage() != null) {
				String imagePath = CURRENT_FOLDER + "/src/main/resources/static" + hotel.getImage();
				File imageFile = new File(imagePath);
				if (imageFile.exists()) {
					imageFile.delete();
				}
			}
			for (ListImageHotel image : optionalHotel.get().getListImageHotel()) {
				String imagePath = CURRENT_FOLDER + "/src/main/resources/static" + image.getUrlImage();
				File imageFile = new File(imagePath);
				if (imageFile.exists()) {
					imageFile.delete();
				}
				listImageHotelRepository.delete(image);
			}
			hotelRepository.deleteById(id);
		}
		return "redirect:/admin/hotel";
	}
}
