package com.husc.controllers;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.husc.models.ERole;
import com.husc.models.Role;
import com.husc.models.User;
import com.husc.repository.RoleRepository;
import com.husc.repository.UserRepository;

@Controller
@RequestMapping("/admin/user")
public class UserAdminController {
	@Autowired
	UserRepository userRepository;

	@Autowired
	RoleRepository roleRepository;
	
	@GetMapping("/list")
	public String getUserList(Model model) {
        List<User> userList = userRepository.findAll();
        List<Role> roleList = roleRepository.findAll();
        model.addAttribute("userList", userList);
        model.addAttribute("roleList", roleList);
        return "user";
    }
	
	@GetMapping("/deleteuser/{id}")
	public String deleteUser(@PathVariable Long id) {
		userRepository.deleteById(id);
		return  "redirect:/admin/user/list";
	}
	@GetMapping("/removerole/{id}")
	public String removerole(@PathVariable Long id, @RequestParam String role) {
		Optional<User> optionalUser = userRepository.findById(id);
		User user = optionalUser.get();
		Set<Role> r = user.getRoles();
		switch (role) {
		case "3":
			Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
					.orElseThrow(() -> new RuntimeException());
			r.remove(adminRole);

			break;
		case "2":
			Role modRole = roleRepository.findByName(ERole.ROLE_MODERATOR)
					.orElseThrow(() -> new RuntimeException());
			r.remove(modRole);

			break;
		default:
			Role userRole = roleRepository.findByName(ERole.ROLE_USER)
					.orElseThrow(() -> new RuntimeException());
			r.remove(userRole);
		}

		user.setRoles(r);
		userRepository.save(user);
		return "redirect:/admin/user/list";
	}


	@GetMapping("/addrole/{id}")
	public String addrole(@PathVariable Long id, @RequestParam String role) {
		Optional<User> optionalUser = userRepository.findById(id);
		User user = optionalUser.get();
		Set<Role> r = user.getRoles();

		switch (role) {
		case "3":
			Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
					.orElseThrow(() -> new RuntimeException());
			r.add(adminRole);

			break;
		case "2":
			Role modRole = roleRepository.findByName(ERole.ROLE_MODERATOR)
					.orElseThrow(() -> new RuntimeException());
			r.add(modRole);

			break;
		default:
			Role userRole = roleRepository.findByName(ERole.ROLE_USER)
					.orElseThrow(() -> new RuntimeException());
			r.add(userRole);
		}

		user.setRoles(r);
		userRepository.save(user);
		return "redirect:/admin/user/list";
	}
}
