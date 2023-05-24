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

import com.husc.models.Booking;
import com.husc.models.CommentTour;
import com.husc.models.ListImageTour;
import com.husc.models.Tour;
import com.husc.models.User;
import com.husc.repository.BookingRepository;
import com.husc.repository.ListImageTourRepository;
import com.husc.repository.TourRepository;
import com.husc.repository.UserRepository;
import com.husc.services.TourService;

@Controller
@RequestMapping("/admin/tour")
public class TourAdminController {

	private static final Path CURRENT_FOLDER = Paths.get(System.getProperty("user.dir"));
	public static Logger logger = LoggerFactory.getLogger(TourController.class);

	@Autowired
	TourRepository tourRepository;
	
	@Autowired
	ListImageTourRepository listImageTourRepository;

	@Autowired
	UserRepository userRepository;
	
	@Autowired 
	BookingRepository bookingRepository;
	
	@Autowired
	TourService tourService;

	@GetMapping
	private String getalltour(Model model) {
		String keyword = "";
		model.addAttribute("keyword", keyword);
		return findPaginated("", 1, "title", "asc", model);
	}
	@GetMapping("/formedittour/{id}")
	public String showedittour(@PathVariable long id, Model model) {
		Tour tour = tourRepository.findById(id).get();
		model.addAttribute("tour", tour);
		return "edittour";
	}
	@GetMapping("/search")
	public String showSearch(@RequestParam(value = "keyword") String keyword,Model model) {
		return findPaginated(keyword,1, "title", "asc", model);
	}@GetMapping("/page/{pageNo}")
	public String findPaginated(@RequestParam(value = "keyword") String keyword,@PathVariable (value = "pageNo") int pageNo, 
			@RequestParam("sortField") String sortField,
			@RequestParam("sortDir") String sortDir,
			Model model) {
		int pageSize = 5;
			Page<Tour> page = tourService.searchTours(keyword, pageNo, pageSize, sortField, sortDir);
			List<Tour> tourlist = page.getContent();
			model.addAttribute("totalPages", page.getTotalPages());
			model.addAttribute("totalItems", page.getTotalElements());
			model.addAttribute("listtour", tourlist);
			model.addAttribute("currentPage", pageNo);
			model.addAttribute("keyword", keyword);
			model.addAttribute("sortField", sortField);
			model.addAttribute("sortDir", sortDir);
			model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
		return "tour";
	}
	
	@GetMapping("/gettour/{id}")
	public String gettour(@PathVariable Long id, Model model) {
		Optional<Tour> tour = tourRepository.findById(id);
		model.addAttribute("tour", tour.get());
		List<String> image = new ArrayList<>();
		List<CommentTour> listcomment = tour.get().getComments();
		for (ListImageTour i : tour.get().getListImageTour()) {
			image.add(i.getUrlImage());
		}
		model.addAttribute("image", image);
		model.addAttribute("listcomment", listcomment);
		return "tourid";
	}
	@GetMapping("/{id}")
	public String gettour(@PathVariable long id ,Model model) {
		List<Booking> booking = bookingRepository.findByTour(tourRepository.findById(id).get());  
		model.addAttribute("bookings", booking);
		return "tourbooking";
	}
	
	@GetMapping("/hometour")
	public String gethometour(Model model) {
		List<Tour> tour = tourRepository.findAll();
		model.addAttribute("tourList", tour);
		return "hometour";
	}
	
	@GetMapping("/formaddtour")
	public String getformaddtour(Model model) {
		Tour tour = new Tour();
		model.addAttribute("tour", tour);
		return "addtour";
	}

	
	@PostMapping
	private String addtour( @RequestParam String address,
			@RequestParam String description, @RequestParam String title, @RequestParam Double price,
			@RequestParam("image") MultipartFile image,@RequestParam("images") MultipartFile[] images) throws IOException {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		Optional<User> user = userRepository.findByUsername(username);
		if(user.isEmpty()) {
			return "redirect:/auth/formsigin";
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
		
		Tour tour = new Tour();
		Date now = new Date();
		tour.setAddress(address);
		tour.setUser(user.get());
		tour.setDescription(description);
		tour.setCreatedDate(now);
		tour.setCreatedBy(user.get().getName());
		tour.setPrice(price);
		tour.setTitle(title);
		tour.setTourCode( UUID.randomUUID().toString().substring(0, 5));
		tour.setImage("/image/" + newName);
		List<ListImageTour> listImage = new ArrayList<>();
		for (MultipartFile imagefile : images) {
			String newFileName = UUID.randomUUID().toString()
					+ imagefile.getOriginalFilename().substring(imagefile.getOriginalFilename().lastIndexOf("."));
			Path fileimage = CURRENT_FOLDER.resolve("src\\main\\resources").resolve(staticPath).resolve(imagePath)
					.resolve(newFileName);
			try (OutputStream os = Files.newOutputStream(fileimage)) {
				os.write(imagefile.getBytes());
			}
			ListImageTour newImage = new ListImageTour();
			newImage.setUrlImage("/image/" + newFileName);
			newImage.setTour(tour);
			listImage.add(newImage);
		}
		tour.setListImageTour(listImage);
		tourRepository.save(tour);
		return "redirect:/admin/tour";
	}
	
	@PostMapping("/edittour")
	public String updateplace(@RequestParam long id, @RequestParam String title, @RequestParam String address,@RequestParam("price") Double price,
			@RequestParam String description, @RequestParam("image") MultipartFile image,
			@RequestParam("images") MultipartFile[] images) throws IOException {
		Tour tour = tourRepository.findById(id).get();
		Path staticPath = Paths.get("static");
		Path imagePath = Paths.get("image");
		if(image.getOriginalFilename() != null && !image.getOriginalFilename().isEmpty()) {
			String imgPath = CURRENT_FOLDER + "/src/main/resources/static" + tour.getImage();
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
			tour.setImage("/image/" +newName);
		}
		
			tour.setTitle(title);
			tour.setPrice(price);
			tour.setAddress(address);
			tour.setDescription(description);
			
		if(images != null && images.length > 0 && !images[0].getOriginalFilename().equals("")) {
			for (ListImageTour img : tour.getListImageTour()) {
				String imgPath = CURRENT_FOLDER + "/src/main/resources/static" + img.getUrlImage();
				File imageFile = new File(imgPath);
				if (imageFile.exists()) {
					imageFile.delete();
				}
				listImageTourRepository.delete(img);
			}
			List<ListImageTour> listImage = new ArrayList<>();
			for (MultipartFile imagefile : images) {
				String newFileName = UUID.randomUUID().toString()
						+ imagefile.getOriginalFilename().substring(imagefile.getOriginalFilename().lastIndexOf("."));
				Path fileimage = CURRENT_FOLDER.resolve("src\\main\\resources").resolve(staticPath).resolve(imagePath)
						.resolve(newFileName);
				try (OutputStream os = Files.newOutputStream(fileimage)) {
					os.write(imagefile.getBytes());
				}
				ListImageTour newImage = new ListImageTour();
				newImage.setUrlImage("/image/" + newFileName);
				newImage.setTour(tour);
				listImage.add(newImage);
			}
			tour.setListImageTour(null);
			tour.setListImageTour(listImage);
		}
		tourRepository.save(tour);
		return "redirect:/admin/tour";
	}
	
	@GetMapping("/statistical/{id}")
	public String statisticalTour(@PathVariable Long id,Model model) {
		Tour tour = tourRepository.findById(id).get();
		List<Booking> listbooking = bookingRepository.findByTour(tour);
		model.addAttribute("tour", tour);
		model.addAttribute("listbooking", listbooking);
		return "booking";
	}
	
	@GetMapping("/deletetour/{id}")
	public String deleteTour(@PathVariable Long id) {
		Optional<Tour> tour = tourRepository.findById(id);
		if (tour.isPresent()) {
			if (tour.get().getImage() != null) {
				String imagePath = CURRENT_FOLDER + "/src/main/resources/static/image" + tour.get().getImage();
				File imageFile = new File(imagePath);
				if (imageFile.exists()) {
					imageFile.delete();
				}
			}
			for (ListImageTour image : tour.get().getListImageTour()) {
				String imagePath = CURRENT_FOLDER + "/src/main/resources/static" + image.getUrlImage();
				File imageFile = new File(imagePath);
				if (imageFile.exists()) {
					imageFile.delete();
				}
				listImageTourRepository.delete(image);
			}
			tourRepository.deleteById(id);
		}
		return "redirect:/admin/tour";
	}
}
