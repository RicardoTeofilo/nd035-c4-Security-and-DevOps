package com.example.demo.controllers;

import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.requests.CreateItemRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/item")
public class ItemController {

	private static final Logger log = LoggerFactory.getLogger(ItemController.class);
	@Autowired
	private ItemRepository itemRepository;
	
	@GetMapping
	public ResponseEntity<List<Item>> getItems() {
		return ResponseEntity.ok(itemRepository.findAll());
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<Item> getItemById(@PathVariable Long id) {
		return ResponseEntity.of(itemRepository.findById(id));
	}
	
	@GetMapping("/name/{name}")
	public ResponseEntity<List<Item>> getItemsByName(@PathVariable String name) {
		List<Item> items = itemRepository.findByName(name);
		return items == null || items.isEmpty() ? ResponseEntity.notFound().build()
				: ResponseEntity.ok(items);
			
	}

	@PostMapping("/create")
	public ResponseEntity<Item> createItem(@RequestBody CreateItemRequest createItemRequest){

		if(StringUtils.isEmpty(createItemRequest.getName())){
			log.error("The item name is required");
			return ResponseEntity.badRequest().build();
		}else if(StringUtils.isEmpty(createItemRequest.getDescription())){
			log.error("The item description is required");
			return ResponseEntity.badRequest().build();
		}else if(createItemRequest.getPrice() == null){
			log.error("The item description is required");
			return ResponseEntity.badRequest().build();
		}

		Item item = new Item();
		item.setName(createItemRequest.getName());
		item.setDescription(createItemRequest.getDescription());
		item.setPrice(createItemRequest.getPrice());
		Item newItem = itemRepository.save(item);
		return ResponseEntity.ok(newItem);
	}
	
}
