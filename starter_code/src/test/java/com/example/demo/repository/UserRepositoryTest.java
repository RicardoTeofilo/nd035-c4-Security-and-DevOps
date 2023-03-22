package com.example.demo.repository;

import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

@DataJpaTest
@RunWith(SpringRunner.class)
public class UserRepositoryTest {

    @Autowired
    TestEntityManager testEntityManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    CartRepository cartRepository;

    @Test
    public void testCreateUser(){
        String username = "Bob";
        User user = createUser1(username, "password11");
        User newUser = testEntityManager.persist(user);

        Optional<User> fetchedUser = userRepository.findById(newUser.getId());
        Assert.assertTrue(fetchedUser.isPresent());
        Assert.assertEquals(username, fetchedUser.get().getUsername());
    }

    @Test
    public void testFindUserByUsername(){
        String username = "Bob";
        User user = createUser1(username, "password11");
        User newUser = testEntityManager.persist(user);

        User fetchedUser = userRepository.findByUsername(username);
        Assert.assertEquals(username, fetchedUser.getUsername());
    }


    private static User createUser1(String userName, String password){
        User user = new User();
        user.setUsername(userName);
        user.setPassword(password);
        return user;
    }
}
