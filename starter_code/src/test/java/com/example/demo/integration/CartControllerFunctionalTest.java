package com.example.demo.integration;

import com.example.demo.SareetaApplication;
import com.example.demo.controller.CartControllerTest;
import com.example.demo.controller.ItemControllerTest;
import com.example.demo.controller.UserControllerTest;
import com.example.demo.controllers.CartController;
import com.example.demo.controllers.ItemController;
import com.example.demo.controllers.UserController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.requests.CreateItemRequest;
import com.example.demo.model.requests.CreateUserRequest;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;
import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@Transactional
@SpringBootTest(classes = SareetaApplication.class)
@RunWith(SpringRunner.class)
public class CartControllerFunctionalTest {

    @Autowired
    private CartController cartController;

    @Autowired
    private ItemController itemController;

    @Autowired
    private UserController userController;

    private final String username1 = "John";
    private final String username2 = "Bob";
    private final String password = "password11";
    private final String name = "Pencil";
    private final String description = "Wood Pencil";
    private final BigDecimal price =  new BigDecimal(5.99);

    @Test
    public void testAddToCart(){

        CreateUserRequest createUserRequest = UserControllerTest.createUserRequest1(
                this.username1, this.password, this.password);
        ResponseEntity<User> response = userController.createUser(createUserRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
        User createdUser = response.getBody();
        assertNotNull(createdUser);

        CreateItemRequest createItemRequest = ItemControllerTest.createItemRequest(this.name, this.description, this.price);
        ResponseEntity<Item> createItemResponse = itemController.createItem(createItemRequest);

        assertNotNull(createItemResponse);
        assertEquals(HttpStatus.OK.value(), createItemResponse.getStatusCodeValue());
        Item returnedItem = createItemResponse.getBody();
        assertNotNull(returnedItem);

        ModifyCartRequest modifyCartRequest = CartControllerTest.createModifyItemRequest(createdUser.getUsername(), returnedItem.getId(), 1);
        ResponseEntity<Cart> addToCartResponse = cartController.addToCart(modifyCartRequest);
        assertNotNull(addToCartResponse);
        assertEquals(HttpStatus.OK.value(), addToCartResponse.getStatusCodeValue());
        Cart returnedCart = addToCartResponse.getBody();
        assertNotNull(returnedCart);
        assertEquals(createdUser.getCart().getId(), returnedCart.getId());
        assertEquals(returnedItem.getId(), returnedCart.getItems().get(0).getId());
        assertEquals(returnedItem.getPrice(), returnedCart.getTotal());
    }

    @Test
    public void testAddToCartWithUserNotFound(){

        ModifyCartRequest modifyCartRequest = CartControllerTest.createModifyItemRequest(this.username1, 1l, 1);
        ResponseEntity<Cart> response = cartController.addToCart(modifyCartRequest);
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCodeValue());
    }

    @Test
    public void testAddToCartWithItemNotFound(){

        CreateUserRequest createUserRequest = UserControllerTest.createUserRequest1(
                this.username1, this.password, this.password);
        ResponseEntity<User> response = userController.createUser(createUserRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
        User createdUser = response.getBody();
        assertNotNull(createdUser);

        ModifyCartRequest modifyCartRequest = CartControllerTest.createModifyItemRequest(createdUser.getUsername(), 100l, 1);
        ResponseEntity<Cart> addToCartResponse = cartController.addToCart(modifyCartRequest);
        assertNotNull(addToCartResponse);
        assertEquals(HttpStatus.NOT_FOUND.value(), addToCartResponse.getStatusCodeValue());
    }

    @Test
    public void testRemoveFromCart(){

        CreateUserRequest createUserRequest = UserControllerTest.createUserRequest1(
                this.username1, this.password, this.password);
        ResponseEntity<User> response = userController.createUser(createUserRequest);
        User createdUser = response.getBody();

        CreateItemRequest createItemRequest = ItemControllerTest.createItemRequest(this.name, this.description, this.price);
        ResponseEntity<Item> createItemResponse = itemController.createItem(createItemRequest);
        Item returnedItem = createItemResponse.getBody();

        ModifyCartRequest modifyCartRequest = CartControllerTest.createModifyItemRequest(createdUser.getUsername(), returnedItem.getId(), 1);
        ResponseEntity<Cart> addToCartResponse = cartController.addToCart(modifyCartRequest);
        Cart returnedCart = addToCartResponse.getBody();
        assertNotNull(returnedCart);
        assertEquals(1, returnedCart.getItems().size());

        ResponseEntity<Cart> removeFromCartResponse = cartController.removeFromCart(modifyCartRequest);
        assertNotNull(removeFromCartResponse);
        assertEquals(HttpStatus.OK.value(), removeFromCartResponse.getStatusCodeValue());
        Cart returnedCart2 = addToCartResponse.getBody();
        assertNotNull(returnedCart2);
        assertEquals(0, returnedCart.getItems().size());
    }

    @Test
    public void testRemoveFromCartWithUserNotFound(){

        ModifyCartRequest modifyCartRequest = CartControllerTest.createModifyItemRequest(this.username1, 1l, 1);
        ResponseEntity<Cart> response = cartController.removeFromCart(modifyCartRequest);
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCodeValue());
    }

    @Test
    public void testRemoveFromCartWithItemNotFound(){

        CreateUserRequest createUserRequest = UserControllerTest.createUserRequest1(
                this.username1, this.password, this.password);
        ResponseEntity<User> response = userController.createUser(createUserRequest);
        User createdUser = response.getBody();

        ModifyCartRequest modifyCartRequest = CartControllerTest.createModifyItemRequest(createdUser.getUsername(), 100l, 1);
        ResponseEntity<Cart> removeFromCartResponse = cartController.removeFromCart(modifyCartRequest);
        assertNotNull(removeFromCartResponse);
        assertEquals(HttpStatus.NOT_FOUND.value(), removeFromCartResponse.getStatusCodeValue());
    }

    @Test
    public void testRemoveFromCartWithInvalidQuantity(){

        CreateUserRequest createUserRequest = UserControllerTest.createUserRequest1(
                this.username1, this.password, this.password);
        ResponseEntity<User> response = userController.createUser(createUserRequest);
        User createdUser = response.getBody();

        CreateItemRequest createItemRequest = ItemControllerTest.createItemRequest(this.name, this.description, this.price);
        ResponseEntity<Item> createItemResponse = itemController.createItem(createItemRequest);
        Item returnedItem = createItemResponse.getBody();

        ModifyCartRequest modifyCartRequest = CartControllerTest.createModifyItemRequest(createdUser.getUsername(), returnedItem.getId(), 1);
        ResponseEntity<Cart> addToCartResponse = cartController.addToCart(modifyCartRequest);
        Cart returnedCart = addToCartResponse.getBody();
        assertNotNull(returnedCart);
        assertEquals(1, returnedCart.getItems().size());

        ModifyCartRequest modifyCartRequest2 = CartControllerTest.createModifyItemRequest(createdUser.getUsername(), returnedItem.getId(), 0);
        ResponseEntity<Cart> removeFromCartResponse = cartController.removeFromCart(modifyCartRequest2);
        assertNotNull(removeFromCartResponse);
        assertEquals(HttpStatus.BAD_REQUEST.value(), removeFromCartResponse.getStatusCodeValue());
    }

    @Test
    public void testRemoveFromCartWithEmptyCart(){

        CreateUserRequest createUserRequest = UserControllerTest.createUserRequest1(
                this.username1, this.password, this.password);
        ResponseEntity<User> response = userController.createUser(createUserRequest);
        User createdUser = response.getBody();

        CreateItemRequest createItemRequest = ItemControllerTest.createItemRequest(this.name, this.description, this.price);
        ResponseEntity<Item> createItemResponse = itemController.createItem(createItemRequest);
        Item returnedItem = createItemResponse.getBody();

        ModifyCartRequest modifyCartRequest2 = CartControllerTest.createModifyItemRequest(createdUser.getUsername(), returnedItem.getId(), 1);
        ResponseEntity<Cart> removeFromCartResponse = cartController.removeFromCart(modifyCartRequest2);
        assertNotNull(removeFromCartResponse);
        assertEquals(HttpStatus.BAD_REQUEST.value(), removeFromCartResponse.getStatusCodeValue());
    }

    @Test
    public void testAddToCartWithInvalidItem(){

        CreateUserRequest createUserRequest = UserControllerTest.createUserRequest1(
                this.username1, this.password, this.password);
        ResponseEntity<User> response = userController.createUser(createUserRequest);
        User createdUser = response.getBody();

        CreateItemRequest createItemRequest = ItemControllerTest.createItemRequest(this.name, this.description, this.price);
        ResponseEntity<Item> createItemResponse = itemController.createItem(createItemRequest);
        Item returnedItem = createItemResponse.getBody();

        ModifyCartRequest modifyCartRequest = CartControllerTest.createModifyItemRequest(createdUser.getUsername(), returnedItem.getId(), 1);
        ResponseEntity<Cart> addToCartResponse = cartController.addToCart(modifyCartRequest);
        Cart returnedCart = addToCartResponse.getBody();

        //Let's create a second item. This item will not be added to the user's cart
        CreateItemRequest createItemRequest2 = ItemControllerTest.createItemRequest("Pen", "Plastic Pen", new BigDecimal(2.99));
        ResponseEntity<Item> createItemResponse2 = itemController.createItem(createItemRequest2);
        Item returnedItem2 = createItemResponse2.getBody();

        //Now let's try to remove from the User's cart an item that does not exist there
        ModifyCartRequest modifyCartRequest2 = CartControllerTest.createModifyItemRequest(createdUser.getUsername(), returnedItem2.getId(), 1);
        ResponseEntity<Cart> removeFromCartResponse = cartController.removeFromCart(modifyCartRequest2);
        assertNotNull(removeFromCartResponse);
        assertEquals(HttpStatus.BAD_REQUEST.value(), removeFromCartResponse.getStatusCodeValue());
    }

}
