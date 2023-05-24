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

import com.husc.models.CommentPlace;
import com.husc.models.ListImagePlace;
import com.husc.models.Place;
import com.husc.models.User;
import com.husc.repository.CommentRepository;
import com.husc.repository.ListImagePlaceRepository;
import com.husc.repository.PlaceRepository;
import com.husc.repository.UserRepository;
import com.husc.security.jwt.JwtUtils;
import com.husc.services.PlaceService;

@Controller
@RequestMapping("/admin/place")
public class PlaceAdminController {
	private static final Path CURRENT_FOLDER = Paths.get(System.getProperty("user.dir"));
	public static Logger logger = LoggerFactory.getLogger(PlaceAdminController.class);

	@Autowired
	ListImagePlaceRepository listImagePlaceRepository;

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
	public String showPlace(Model model) {
		String keyword = "";
		model.addAttribute("keyword", keyword);
		return findPaginatedadmin("", 1, "title", "asc", model);
	}
	
	@GetMapping("/search")
	public String showSearch(@RequestParam(value = "keyword") String keyword,Model model) {
		return findPaginatedadmin(keyword,1, "title", "asc", model);
	}
	
	@GetMapping("/page/{pageNo}")
	public String findPaginatedadmin(@RequestParam(value = "keyword") String keyword,@PathVariable (value = "pageNo") int pageNo, 
			@RequestParam("sortField") String sortField,
			@RequestParam("sortDir") String sortDir,
			Model model) {
		int pageSize = 5;
			Page<Place> page = placeService.searchPlaces(keyword, pageNo, pageSize, sortField, sortDir);
			List<Place> placelist = page.getContent();
			model.addAttribute("totalPages", page.getTotalPages());
			model.addAttribute("totalItems", page.getTotalElements());
			model.addAttribute("placeList", placelist);
			model.addAttribute("currentPage", pageNo);
			model.addAttribute("keyword", keyword);
			model.addAttribute("sortField", sortField);
			model.addAttribute("sortDir", sortDir);
			model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
		return "place";
	}

	@GetMapping("/showupdateplace/{id}")
	public String showupdateplace(@PathVariable long id, Model model) {
		Place place = placeRepository.findById(id).get();
		model.addAttribute("place", place);
		return "updateplace";
	}
	
	@GetMapping("/showNewPlaceForm")
	public String shownewplaceform(Model model) {
		Place place = new Place();
		model.addAttribute("place",place);
		return "new_place";
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


	@PostMapping("/addplace")
	public String create(@RequestParam String title, @RequestParam String address,@RequestParam String shortDescription,
			@RequestParam String description, @RequestParam("image") MultipartFile image,
			@RequestParam("images") MultipartFile[] images) throws IOException {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
    	Optional<User> user = userRepository.findByUsername(username);
    	Place place = new Place();
    	if(user.isEmpty()) {
    		return "redirect:/auth/formsignin";
    	}
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
		place.setTitle(title);
		place.setShortDescription(shortDescription);
		place.setAddress(address);
		place.setDescription(description);
		place.setUser(user.get());
		Date date = new Date();
		place.setCreatedDate(date);
		place.setCreatedBy(user.get().getName());
		place.setImage("/image/" +newName);
		
		List<ListImagePlace> listImage = new ArrayList<>();
		for (MultipartFile imagefile : images) {
			String newFileName = UUID.randomUUID().toString()
					+ imagefile.getOriginalFilename().substring(imagefile.getOriginalFilename().lastIndexOf("."));
			Path fileimage = CURRENT_FOLDER.resolve("src\\main\\resources").resolve(staticPath).resolve(imagePath)
					.resolve(newFileName);
			try (OutputStream os = Files.newOutputStream(fileimage)) {
				os.write(imagefile.getBytes());
			}
			ListImagePlace newImage = new ListImagePlace();
			newImage.setUrlImage("/image/" + newFileName);
			newImage.setPlace(place);
			listImage.add(newImage);
		}
		place.setListImagePlace(listImage);
		placeRepository.save(place);
		return "redirect:/admin/place";
	}
	@PostMapping("/updateplace")
	public String updateplace(@RequestParam long id, @RequestParam String title, @RequestParam String address,@RequestParam String shortDescription,
			@RequestParam String description, @RequestParam("image") MultipartFile image,
			@RequestParam("images") MultipartFile[] images) throws IOException {
		Place place = placeRepository.findById(id).get();
		Path staticPath = Paths.get("static");
		Path imagePath = Paths.get("image");
		if(image.getOriginalFilename() != null && !image.getOriginalFilename().isEmpty()) {
			String imgPath = CURRENT_FOLDER + "/src/main/resources/static" + place.getImage();
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
			place.setImage("/image/" +newName);
		}
		
			place.setTitle(title);
			place.setShortDescription(shortDescription);
			place.setAddress(address);
			place.setDescription(description);
			
		if(images != null && images.length > 0 && !images[0].getOriginalFilename().equals("")) {
			for (ListImagePlace img : place.getListImagePlace()) {
				String imgPath = CURRENT_FOLDER + "/src/main/resources/static" + img.getUrlImage();
				File imageFile = new File(imgPath);
				if (imageFile.exists()) {
					imageFile.delete();
				}
				listImagePlaceRepository.delete(img);
			}
			List<ListImagePlace> listImage = new ArrayList<>();
			for (MultipartFile imagefile : images) {
				String newFileName = UUID.randomUUID().toString()
						+ imagefile.getOriginalFilename().substring(imagefile.getOriginalFilename().lastIndexOf("."));
				Path fileimage = CURRENT_FOLDER.resolve("src\\main\\resources").resolve(staticPath).resolve(imagePath)
						.resolve(newFileName);
				try (OutputStream os = Files.newOutputStream(fileimage)) {
					os.write(imagefile.getBytes());
				}
				ListImagePlace newImage = new ListImagePlace();
				newImage.setUrlImage("/image/" + newFileName);
				newImage.setPlace(place);
				listImage.add(newImage);
			}
			place.setListImagePlace(null);
			place.setListImagePlace(listImage);
		}
		placeRepository.save(place);
		return "redirect:/admin/place";
	}
	@GetMapping("/deleteplace/{id}")
	public String deletePlace(@PathVariable Long id) {
		Optional<Place> optionalPlace = placeRepository.findById(id);
		if (optionalPlace.isPresent()) {
			Place place = optionalPlace.get();
			if (place.getImage() != null) {
				String imagePath = CURRENT_FOLDER + "/src/main/resources/static" + place.getImage();
				File imageFile = new File(imagePath);
				if (imageFile.exists()) {
					imageFile.delete();
				}
			}
			for (ListImagePlace image : optionalPlace.get().getListImagePlace()) {
				String imagePath = CURRENT_FOLDER + "/src/main/resources/static" + image.getUrlImage();
				File imageFile = new File(imagePath);
				if (imageFile.exists()) {
					imageFile.delete();
				}
				listImagePlaceRepository.delete(image);
			}
			placeRepository.deleteById(id);
			return "redirect:/admin/place";
		}
		return "redirect:/admin/place";
	}
}
