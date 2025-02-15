package com.example.demo.controllers;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.codehaus.plexus.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

	private final static Logger log = LoggerFactory.getLogger(UserController.class);
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private CartRepository cartRepository;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@GetMapping("/id/{id}")
	public ResponseEntity<User> findById(@PathVariable Long id) {
		return ResponseEntity.of(userRepository.findById(id));
	}
	
	@GetMapping("/{username}")
	public ResponseEntity<User> findByUserName(@PathVariable String username) {
		User user = userRepository.findByUsername(username);
		return user == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(user);
	}
	
	@PostMapping("/create")
	public ResponseEntity<User> createUser(@RequestBody CreateUserRequest createUserRequest) {
		log.info("Create user request for username: " + createUserRequest.getUsername());

		//Some validation code here for the password
		if(StringUtils.isEmpty(createUserRequest.getPassword()) ||
			StringUtils.isEmpty(createUserRequest.getConfirmPassword())){
			log.error("Create User request failed. The password and confirm password are required");
			return ResponseEntity.badRequest().build();
		}else if(createUserRequest.getPassword().length() < 7){
			log.error("Create User request failed. The password length does not meet the minimum Length requirements");
			return ResponseEntity.badRequest().build();
		}else if(!createUserRequest.getPassword().equals(createUserRequest.getConfirmPassword())){
			log.error("Create User request failed. Password and Confirm password don't match");
			return ResponseEntity.badRequest().build();
		}

		User user = new User();
		user.setUsername(createUserRequest.getUsername());

		Cart cart = new Cart();
		cartRepository.save(cart);
		user.setCart(cart);

		user.setPassword(bCryptPasswordEncoder.encode(createUserRequest.getPassword()));
		User savedUser = userRepository.save(user);
		log.info("Create User request was successful for username : " + createUserRequest.getUsername());
		return ResponseEntity.ok(savedUser);
	}
	
}
