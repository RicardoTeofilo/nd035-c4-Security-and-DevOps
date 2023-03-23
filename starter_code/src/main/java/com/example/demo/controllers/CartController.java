package com.example.demo.controllers;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

@RestController
@RequestMapping("/api/cart")
public class CartController {

	private final static Logger log = LoggerFactory.getLogger(CartController.class);
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private CartRepository cartRepository;
	
	@Autowired
	private ItemRepository itemRepository;
	
	@PostMapping("/addToCart")
	public ResponseEntity<Cart> addToCart(@RequestBody ModifyCartRequest request) {
		log.info("Add item to Cart for: " + request.toString());

		User user = userRepository.findByUsername(request.getUsername());
		if(user == null) {
			log.error("Add to Cart failed. Could not find user with username: " + request.getUsername());
			return ResponseEntity.notFound().build();
		}
		Optional<Item> item = itemRepository.findById(request.getItemId());
		if(!item.isPresent()) {
			log.error("Add to Cart failed. Could not find item with item Id: " + request.getItemId());
			return ResponseEntity.notFound().build();
		}
		if(request.getQuantity() <= 0){
			log.error("Add to Cart failed. Quantity must be greater than 0.");
			return ResponseEntity.badRequest().build();
		}
		Cart cart = user.getCart();
		IntStream.range(0, request.getQuantity())
			.forEach(i -> cart.addItem(item.get()));
		Cart savedCart = cartRepository.save(cart);
		return ResponseEntity.ok(savedCart);
	}
	
	@PostMapping("/removeFromCart")
	public ResponseEntity<Cart> removeFromCart(@RequestBody ModifyCartRequest request) {
		User user = userRepository.findByUsername(request.getUsername());
		if(user == null) {
			log.error("Could not find user with username: " + request.getUsername());
			return ResponseEntity.notFound().build();
		}
		Optional<Item> item = itemRepository.findById(request.getItemId());
		if(!item.isPresent()) {
			log.error("Could not find item with item Id: " + request.getItemId());
			return ResponseEntity.notFound().build();
		}
		if(request.getQuantity() <= 0){
			log.error("Removed from Cart failed. Quantity must be greater than 0.");
			return ResponseEntity.badRequest().build();
		}

		Cart cart = user.getCart();
		if(cart == null || CollectionUtils.isEmpty(cart.getItems())){
			log.error("Removed from Cart failed. The user cart is empty for username: " + request.getUsername());
			return ResponseEntity.badRequest().build();
		}
		List<Item> itemList = cart.getItems();
		if(!itemList.contains(item.get())){
			log.error("Removed from Cart failed. The Cart does not contain the item to be removed.");
			return ResponseEntity.badRequest().build();
		}
		IntStream.range(0, request.getQuantity())
			.forEach(i -> cart.removeItem(item.get()));
		Cart savedCart = cartRepository.save(cart);
		return ResponseEntity.ok(savedCart);
	}
		
}
