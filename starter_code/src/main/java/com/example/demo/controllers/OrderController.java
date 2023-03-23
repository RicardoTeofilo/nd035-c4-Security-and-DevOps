package com.example.demo.controllers;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order")
public class OrderController {
	
	private final static Logger log = LoggerFactory.getLogger(OrderController.class);
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private OrderRepository orderRepository;
	
	
	@PostMapping("/submit/{username}")
	public ResponseEntity<UserOrder> submit(@PathVariable String username) {
		log.info("Submit order for username: " + username);
		User user = userRepository.findByUsername(username);
		if(user == null) {
			log.error("Submit order failed. The user was not found for username: " + username);
			return ResponseEntity.notFound().build();
		}
		//Here I will add some sanity checks. It does not make sense to submit an order
		//if the user's cart has no items in it.
		Cart userCart = user.getCart();
		if(userCart == null || CollectionUtils.isEmpty(userCart.getItems())){
			log.error("Submit order failed. The user cart is empty for username: " + username);
			return ResponseEntity.badRequest().build();
		}
		UserOrder order = UserOrder.createFromCart(user.getCart());
		UserOrder savedUserOrder = orderRepository.save(order);
		log.info("Submit order success for username: " + username + ", order Id : " + savedUserOrder.getId() );
		return ResponseEntity.ok(savedUserOrder);
	}
	
	@GetMapping("/history/{username}")
	public ResponseEntity<List<UserOrder>> getOrdersForUser(@PathVariable String username) {
		User user = userRepository.findByUsername(username);
		if(user == null) {
			log.error("Get orders for user failed. The user was not found for username: " + username);
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(orderRepository.findByUser(user));
	}
}
