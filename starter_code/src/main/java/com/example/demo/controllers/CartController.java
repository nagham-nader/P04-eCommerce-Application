package com.example.demo.controllers;

import java.util.Optional;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;

@RestController
@RequestMapping("/api/cart")
public class CartController {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private CartRepository cartRepository;
	
	@Autowired
	private ItemRepository itemRepository;

	public static final Logger log = LoggerFactory.getLogger(CartController.class);


	@PostMapping("/addToCart")
	public ResponseEntity<Cart> addTocart(@RequestBody ModifyCartRequest request) {
		User user = userRepository.findByUsername(request.getUsername());
		if(user == null) {
			log.error("User Not Found" + request.getUsername());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		Optional<Item> item = itemRepository.findById(request.getItemId());
		if(!item.isPresent()) {
			log.error("Item Not Found : " + request.getItemId());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		Cart cart = user.getCart();
		cart.setUser(user);
		IntStream.range(0, request.getQuantity())
			.forEach(i -> cart.addItem(item.get()));
		log.info("item Added to Cart : " + item.get().getId());

		cartRepository.save(cart);
		log.info("Cart saved for User : " + cart.getUser().getUsername());

		return ResponseEntity.ok(cart);
	}
	
	@PostMapping("/removeFromCart")
	public ResponseEntity<Cart> removeFromcart(@RequestBody ModifyCartRequest request) {
		User user = userRepository.findByUsername(request.getUsername());
		if(user == null) {
			log.error("User Not Found" + request.getUsername());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		Optional<Item> item = itemRepository.findById(request.getItemId());
		if(!item.isPresent()) {
			log.error("Item Not Found : " + request.getItemId());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		Cart cart = user.getCart();
		cart.setUser(user);
		IntStream.range(0, request.getQuantity())
			.forEach(i -> cart.removeItem(item.get()));
		log.info("item Removed from Cart : " + item.get().getId());

		cartRepository.save(cart);
		log.info("Cart Updated after remove for User : " + cart.getUser().getUsername());

		return ResponseEntity.ok(cart);
	}
		
}
