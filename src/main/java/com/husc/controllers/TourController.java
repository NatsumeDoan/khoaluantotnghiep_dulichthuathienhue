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

import com.husc.models.Booking;
import com.husc.models.CommentTour;
import com.husc.models.ListImageTour;
import com.husc.models.Tour;
import com.husc.repository.BookingRepository;
import com.husc.repository.ListImageTourRepository;
import com.husc.repository.TourRepository;
import com.husc.repository.UserRepository;
import com.husc.services.TourService;

@Controller
@RequestMapping("/tour")
public class TourController {

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
			Page<Tour> page = tourService.searchHomeTours(keyword, pageNo, pageSize);
			List<Tour> tourlist = page.getContent();
			model.addAttribute("totalPages", page.getTotalPages());
			model.addAttribute("totalItems", page.getTotalElements());
			model.addAttribute("tourList", tourlist);
			model.addAttribute("currentPage", pageNo);
			model.addAttribute("keyword", keyword);
		return "hometour";
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
	
	@GetMapping("/Statistical/{id}")
	public String statisticalTour(@PathVariable Long id,Model model) {
		Tour tour = tourRepository.findById(id).get();
		List<Booking> listbooking = bookingRepository.findByTour(tour);
		model.addAttribute("tour", tour);
		model.addAttribute("listbooking", listbooking);
		return "booking";
	}
	
}
