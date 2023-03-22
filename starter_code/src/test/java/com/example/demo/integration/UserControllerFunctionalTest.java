package com.example.demo.integration;

import com.example.demo.SareetaApplication;
import com.example.demo.controller.UserControllerTest;
import com.example.demo.controllers.UserController;
import com.example.demo.model.persistence.User;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;

import static org.junit.Assert.*;
@Transactional
@SpringBootTest(classes = SareetaApplication.class)
@RunWith(SpringRunner.class)
public class UserControllerFunctionalTest {

    @Autowired
    private UserController userController;

    private final String username1 = "John";
    private final String username2 = "Bob";
    private final String password = "password11";
    private final String invalidPassword = "pass";
    private final String mockHashedPassword = "hashPassword11";



    @Test
    public void testCreateUser(){
        CreateUserRequest createUserRequest = UserControllerTest.createUserRequest1(
                this.username1, this.password, this.password);
        ResponseEntity<User> response = userController.createUser(createUserRequest);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        User user = response.getBody();
        assertNotNull(user);
        assertNotNull(user.getId());
        assertEquals(this.username1, user.getUsername());
        assertNotEquals(this.password, user.getPassword());
        assertNotNull(user.getCart());
    }

    @Test
    public void testCreateUserWithEmptyPassword(){
        CreateUserRequest createUserRequest = UserControllerTest.createUserRequest1(
                this.username1, null, this.password);
        ResponseEntity<User> response = userController.createUser(createUserRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCodeValue());
    }

    @Test
    public void testCreateUserWithInvalidPassword(){
        CreateUserRequest createUserRequest = UserControllerTest.createUserRequest1(
                this.username1, this.invalidPassword, this.invalidPassword);
        ResponseEntity<User> response = userController.createUser(createUserRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCodeValue());
    }

    @Test
    public void testCreateUserWithMismatchedPassword(){
        CreateUserRequest createUserRequest = UserControllerTest.createUserRequest1(
                this.username1, this.invalidPassword, this.invalidPassword);
        ResponseEntity<User> response = userController.createUser(createUserRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCodeValue());
    }


    @Test
    public void testCreateUserAndFindUserByUsername(){
        CreateUserRequest createUserRequest = UserControllerTest.createUserRequest1(
                this.username1, this.password, this.password);
        ResponseEntity<User> response = userController.createUser(createUserRequest);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        User createdUser = response.getBody();
        assertNotNull(createdUser);
        assertNotNull(createdUser.getId());

        ResponseEntity<User> fetchedUserResponse = userController.findByUserName(this.username1);
        assertNotNull(fetchedUserResponse);
        assertEquals(HttpStatus.OK.value(), fetchedUserResponse.getStatusCodeValue());
        User fetchedUser = fetchedUserResponse.getBody();
        assertNotNull(fetchedUser);
        assertEquals(createdUser.getId(), fetchedUser.getId());
        assertEquals(createdUser.getUsername(), fetchedUser.getUsername());
    }

    @Test
    public void testCreateUserAndFindUserById(){
        CreateUserRequest createUserRequest = UserControllerTest.createUserRequest1(
                this.username1, this.password, this.password);
        ResponseEntity<User> response = userController.createUser(createUserRequest);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        User createdUser = response.getBody();
        assertNotNull(createdUser);
        assertNotNull(createdUser.getId());

        ResponseEntity<User> fetchedUserResponse = userController.findById(createdUser.getId());
        assertNotNull(fetchedUserResponse);
        assertEquals(HttpStatus.OK.value(), fetchedUserResponse.getStatusCodeValue());
        User fetchedUser = fetchedUserResponse.getBody();
        assertNotNull(fetchedUser);
        assertEquals(createdUser.getId(), fetchedUser.getId());
        assertEquals(createdUser.getUsername(), fetchedUser.getUsername());
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void testUsernameUniqueness(){
        CreateUserRequest createUserRequest = UserControllerTest.createUserRequest1(
                this.username1, this.password, this.password);
        ResponseEntity<User> response = userController.createUser(createUserRequest);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        User user = response.getBody();
        assertNotNull(user);
        assertNotNull(user.getId());

        //Attempting to create a second user with the same username
        //this should throw an exception based on the username uniqueness constraint
        CreateUserRequest createUserRequest2 = UserControllerTest.createUserRequest1(
                this.username1, this.password, this.password);
        ResponseEntity<User> response2 = userController.createUser(createUserRequest);

    }


}
