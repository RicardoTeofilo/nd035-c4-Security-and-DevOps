package com.example.demo.controller;

import com.example.demo.TestUtils;
import com.example.demo.controllers.UserController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserControllerTest {

    private UserController userController;
    private UserRepository userRepository = mock(UserRepository.class);
    private CartRepository cartRepository = mock(CartRepository.class);
    private BCryptPasswordEncoder bCryptPasswordEncoder = mock(BCryptPasswordEncoder.class);
    private final long userId = 1l;
    private final String username1 = "John";
    private final String username2 = "Bob";
    private final String password = "password11";
    private final String invalidPassword = "pass";
    private final String mockHashedPassword = "hashPassword11";

    @Before
    public void setup(){
        userController = new UserController();
        TestUtils.injectObjects(userController, "userRepository", userRepository);
        TestUtils.injectObjects(userController, "cartRepository", cartRepository);
        TestUtils.injectObjects(userController, "bCryptPasswordEncoder", bCryptPasswordEncoder);
    }

    @Test
    public void createUserSuccess(){

        when(bCryptPasswordEncoder.encode(this.password)).thenReturn(this.mockHashedPassword);
        User newUser = createUser(this.username1, this.mockHashedPassword, this.userId);
        when(userRepository.save(any())).thenReturn(newUser);

        CreateUserRequest createUserRequest = createUserRequest1(this.username1, this.password, this.password);
        final ResponseEntity<User> response = userController.createUser(createUserRequest);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        User user = response.getBody();
        assertNotNull(user);
        assertNotNull(user.getId());
        assertEquals(this.username1, user.getUsername());
        assertEquals(this.mockHashedPassword, user.getPassword());
        assertNotNull(user.getCart());
    }

    @Test
    public void createUserWithEmptyPassword(){

        CreateUserRequest createUserRequest = createUserRequest1(this.username1, null, null);
        final ResponseEntity<User> response = userController.createUser(createUserRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCodeValue());
    }

    @Test
    public void createUserWithInvalidPassword(){

        CreateUserRequest createUserRequest = createUserRequest1(this.username1,
                this.invalidPassword, this.invalidPassword);
        final ResponseEntity<User> response = userController.createUser(createUserRequest);

        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    public void createUserWithMismatchPassword(){

        CreateUserRequest createUserRequest = createUserRequest1(this.username1,
                this.password, this.invalidPassword);
        final ResponseEntity<User> response = userController.createUser(createUserRequest);

        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    public void testFindUserById(){

        when(bCryptPasswordEncoder.encode(this.password)).thenReturn(this.mockHashedPassword);
        User newUser = createUser(this.username1, this.mockHashedPassword, this.userId);
        when(userRepository.save(any())).thenReturn(newUser);
        when(userRepository.findById(any())).thenReturn(Optional.of(newUser));

        CreateUserRequest createUserRequest = createUserRequest1(this.username1, this.password, this.password);
        final ResponseEntity<User> response = userController.createUser(createUserRequest);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        User user = response.getBody();

        final ResponseEntity<User> findByIdResponse = userController.findById(user.getId());
        assertNotNull(findByIdResponse);
        assertEquals(200, findByIdResponse.getStatusCodeValue());
        User fetchedUser = findByIdResponse.getBody();

        assertNotNull(fetchedUser);
        assertEquals(user.getId(), fetchedUser.getId());
        assertEquals(this.username1, fetchedUser.getUsername());
        assertEquals(this.mockHashedPassword, fetchedUser.getPassword());
        assertNotNull(fetchedUser.getCart());
    }

    @Test
    public void testFindUsername(){

        when(bCryptPasswordEncoder.encode(this.password)).thenReturn(this.mockHashedPassword);
        User newUser = createUser(this.username1, this.mockHashedPassword, this.userId);
        when(userRepository.save(any())).thenReturn(newUser);
        when(userRepository.findByUsername(any())).thenReturn(newUser);

        CreateUserRequest createUserRequest = createUserRequest1(this.username1, this.password, this.password);
        final ResponseEntity<User> response = userController.createUser(createUserRequest);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        User user = response.getBody();

        final ResponseEntity<User> findByUserName = userController.findByUserName(user.getUsername());
        assertNotNull(findByUserName);
        assertEquals(200, findByUserName.getStatusCodeValue());
        User fetchedUser = findByUserName.getBody();

        assertNotNull(fetchedUser);
        assertEquals(user.getId(), fetchedUser.getId());
        assertEquals(this.username1, fetchedUser.getUsername());
        assertEquals(this.mockHashedPassword, fetchedUser.getPassword());
        assertNotNull(fetchedUser.getCart());
    }

    public static CreateUserRequest createUserRequest1(String username, String password, String confirmPassword){
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername(username);
        createUserRequest.setPassword(password);
        createUserRequest.setConfirmPassword(confirmPassword);
        return createUserRequest;
    }

    public static User createUser(String username, String password, long userId){
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setId(userId);
        user.setCart(new Cart());
        user.getCart().setUser(user);
        return user;
    }

    public static User createUser(String username, String password){
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setCart(new Cart());
        user.getCart().setUser(user);
        return user;
    }

}
