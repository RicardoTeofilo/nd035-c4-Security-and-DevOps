package com.example.demo.controller;

import com.example.demo.TestUtils;
import com.example.demo.controllers.OrderController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
public class OrderControllerTest {

    private OrderController orderController;
    private OrderRepository orderRepository = mock(OrderRepository.class);
    private UserRepository userRepository = mock(UserRepository.class);

    private final long userId = 1l;
    private final String username1 = "John";
    private final String password = "password11";
    private final String name = "Pencil";
    private final String description = "Wood Pencil";
    private final BigDecimal price =  new BigDecimal(5.99);

    @Before
    public void setup(){
        orderController = new OrderController();
        TestUtils.injectObjects(orderController, "orderRepository", orderRepository);
        TestUtils.injectObjects(orderController, "userRepository", userRepository);
    }

    @Test
    public void testSubmitOrderSuccess(){
        User user = UserControllerTest.createUser(this.username1, this.password, this.userId);
        Item item = ItemControllerTest.createItem(this.name, this.description, this.price);
        Cart cart = CartControllerTest.createCart(this.userId, item, user, 1);
        when(userRepository.findByUsername(this.username1)).thenReturn(user);

        UserOrder userOrder = UserOrder.createFromCart(cart);
        userOrder.setId(1l);
        when(orderRepository.save(any())).thenReturn(userOrder);
        ResponseEntity<UserOrder> response = orderController.submit(this.username1);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        UserOrder returnedOrder = response.getBody();
        assertNotNull(returnedOrder);
        assertNotNull(returnedOrder.getId());
        assertNotNull(returnedOrder.getItems());
        assertEquals(1, returnedOrder.getItems().size());
        assertEquals(returnedOrder.getUser().getId(), userOrder.getUser().getId());
        assertEquals(cart.getTotal(), returnedOrder.getTotal());

    }

    @Test

    public void testSubmitOrderWithUserNotFound(){
        when(userRepository.findByUsername(this.username1)).thenReturn(null);

        ResponseEntity<UserOrder> response = orderController.submit(this.username1);
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCodeValue());
    }

    @Test
    public void testGetOrdersForUser(){
        User user = UserControllerTest.createUser(this.username1, this.password, this.userId);
        Item item = ItemControllerTest.createItem(this.name, this.description, this.price);
        Cart cart = CartControllerTest.createCart(this.userId, item, user, 1);
        when(userRepository.findByUsername(this.username1)).thenReturn(user);

        UserOrder userOrder = UserOrder.createFromCart(cart);
        when(orderRepository.findByUser(any())).thenReturn(Collections.singletonList(userOrder));
        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser(this.username1);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        List<UserOrder> returnedOrderList = response.getBody();
        assertNotNull(returnedOrderList);
        assertTrue(!returnedOrderList.isEmpty());
        assertEquals(returnedOrderList.get(0).getUser().getId(), userOrder.getUser().getId());
        assertEquals(cart.getTotal(), returnedOrderList.get(0).getTotal());
        assertEquals(1, returnedOrderList.size());
    }

    @Test
    public void testGetOrdersWithUserNotFound(){
        when(userRepository.findByUsername(this.username1)).thenReturn(null);

        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser(this.username1);
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCodeValue());
    }

}
