package com.example.demo.controller;

import com.example.demo.TestUtils;
import com.example.demo.controllers.CartController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
@RunWith(SpringRunner.class)
@SpringBootTest
public class CartControllerTest {

    private CartController cartController;
    private CartRepository cartRepository = mock(CartRepository.class);
    private UserRepository userRepository = mock(UserRepository.class);
    private ItemRepository itemRepository = mock(ItemRepository.class);
    private final long userId = 1l;
    private final String username1 = "John";
    private final String password = "password11";
    private final String name = "Pencil";
    private final String description = "Wood Pencil";
    private final BigDecimal price =  new BigDecimal(5.99);

    @Before
    public void setup(){
        cartController = new CartController();
        TestUtils.injectObjects(cartController, "cartRepository", cartRepository);
        TestUtils.injectObjects(cartController, "userRepository", userRepository);
        TestUtils.injectObjects(cartController, "itemRepository", itemRepository);
    }

    @Test
    public void testAddToCart(){
        User user = UserControllerTest.createUser(this.username1, this.password, this.userId);
        when(userRepository.findByUsername(this.username1)).thenReturn(user);
        Item item = ItemControllerTest.createItem(this.name, this.description, this.price);
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(cartRepository.save(any())).thenReturn(user.getCart());

        ModifyCartRequest modifyCartRequest = createModifyItemRequest(user.getUsername(), item.getId(), 1);
        ResponseEntity<Cart> response = cartController.addToCart(modifyCartRequest);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        Cart returnedCart = response.getBody();
        assertNotNull(returnedCart);
        assertEquals(user.getCart().getId(), returnedCart.getId());
        assertEquals(user.getId(), returnedCart.getUser().getId());
        assertEquals(item.getId(), returnedCart.getItems().get(0).getId());
        assertEquals(item.getPrice(), returnedCart.getTotal());
    }

    @Test
    public void testAddToCartWithUserNotFound(){
        User user = UserControllerTest.createUser(this.username1, this.password, this.userId);
        when(userRepository.findByUsername(this.username1)).thenReturn(null);
        Item item = ItemControllerTest.createItem(this.name, this.description, this.price);
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        ModifyCartRequest modifyCartRequest = createModifyItemRequest(user.getUsername(), item.getId(), 1);
        ResponseEntity<Cart> response = cartController.addToCart(modifyCartRequest);
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCodeValue());
    }

    @Test
    public void testAddToCartWithItemNotFound(){
        User user = UserControllerTest.createUser(this.username1, this.password, this.userId);
        when(userRepository.findByUsername(this.username1)).thenReturn(user);
        Item item = ItemControllerTest.createItem(this.name, this.description, this.price);
        when(itemRepository.findById(item.getId())).thenReturn(Optional.empty());

        ModifyCartRequest modifyCartRequest = createModifyItemRequest(user.getUsername(), item.getId(), 1);
        ResponseEntity<Cart> response = cartController.addToCart(modifyCartRequest);
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCodeValue());
    }

    @Test
    public void testAddToCartWithZeroQuantity(){
        User user = UserControllerTest.createUser(this.username1, this.password, this.userId);
        when(userRepository.findByUsername(this.username1)).thenReturn(user);
        Item item = ItemControllerTest.createItem(this.name, this.description, this.price);
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        ModifyCartRequest modifyCartRequest = createModifyItemRequest(user.getUsername(), item.getId(), 0);
        ResponseEntity<Cart> response = cartController.addToCart(modifyCartRequest);
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCodeValue());

    }

    @Test
    public void testAddAndRemoveFromCart(){
        User user = UserControllerTest.createUser(this.username1, this.password, this.userId);
        when(userRepository.findByUsername(this.username1)).thenReturn(user);
        Item item = ItemControllerTest.createItem(this.name, this.description, this.price);
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(cartRepository.save(any())).thenReturn(user.getCart());

        ModifyCartRequest modifyCartRequest = createModifyItemRequest(user.getUsername(), item.getId(), 1);
        ResponseEntity<Cart> response = cartController.addToCart(modifyCartRequest);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        Cart returnedCart = response.getBody();
        assertNotNull(returnedCart);
        assertEquals(user.getCart().getId(), returnedCart.getId());
        assertEquals(user.getId(), returnedCart.getUser().getId());
        assertEquals(1, returnedCart.getItems().size());
        assertEquals(item.getPrice(), returnedCart.getTotal());

        when(cartRepository.save(any())).thenReturn(user.getCart());
        ResponseEntity<Cart> removeResponse = cartController.removeFromCart(modifyCartRequest);
        assertNotNull(removeResponse);
        assertEquals(200, removeResponse.getStatusCodeValue());
        Cart returnedCart2 = removeResponse.getBody();
        assertNotNull(returnedCart2);
        assertEquals(user.getCart().getId(), returnedCart2.getId());
        assertEquals(user.getId(), returnedCart2.getUser().getId());
        assertEquals(0, returnedCart2.getItems().size());
        assertTrue(BigDecimal.ZERO.compareTo(returnedCart.getTotal()) == 0);

    }

    @Test
    public void testRemoveFromCartWithUserNotFound(){
        User user = UserControllerTest.createUser(this.username1, this.password, this.userId);
        when(userRepository.findByUsername(this.username1)).thenReturn(null);
        Item item = ItemControllerTest.createItem(this.name, this.description, this.price);
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        ModifyCartRequest modifyCartRequest = createModifyItemRequest(user.getUsername(), item.getId(), 1);
        ResponseEntity<Cart> response = cartController.removeFromCart(modifyCartRequest);
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCodeValue());
    }

    @Test
    public void testRemoveFromCartWithItemNotFound(){
        User user = UserControllerTest.createUser(this.username1, this.password, this.userId);
        when(userRepository.findByUsername(this.username1)).thenReturn(user);
        Item item = ItemControllerTest.createItem(this.name, this.description, this.price);
        when(itemRepository.findById(item.getId())).thenReturn(Optional.empty());

        ModifyCartRequest modifyCartRequest = createModifyItemRequest(user.getUsername(), item.getId(), 1);
        ResponseEntity<Cart> response = cartController.addToCart(modifyCartRequest);
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCodeValue());

    }

    public static ModifyCartRequest createModifyItemRequest(String username, long itemId, int quantity){
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setUsername(username);
        modifyCartRequest.setItemId(itemId);
        modifyCartRequest.setQuantity(quantity);
        return modifyCartRequest;
    }

    public static Cart createCart(long userId, Item item, User user, int quantity){
        Cart cart;
        if(user.getCart() == null)
            cart = new Cart();
        else
            cart = user.getCart();

        cart.setId(userId);
        cart.setUser(user);
        IntStream.range(0, quantity).forEach(i -> cart.addItem(item));

        return cart;
    }

}
