package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@SpringBootTest
public class UserControllerTest {

    private UserController userController = mock(UserController.class);

    private UserRepository userRepo = mock(UserRepository.class);

    private CartRepository cartRepo = mock(CartRepository.class);

    private BCryptPasswordEncoder encoder = mock(BCryptPasswordEncoder.class);

    @Before
    public void setUp(){
        userController = new UserController();
        TestUtils.injectObjects(userController, "userRepository", userRepo);
        TestUtils.injectObjects(userController, "cartRepository", cartRepo);
        TestUtils.injectObjects(userController, "bCryptPasswordEncoder", encoder);

    }

    @Test
    public void createUserHappyPath(){
        when(encoder.encode("testPassword")).thenReturn("encodedPassword");

        CreateUserRequest req = new CreateUserRequest();
        req.setUsername("test");
        req.setPassword("testPassword");
        req.setConfirmPassword("testPassword");

        ResponseEntity<User> response = userController.createUser(req);
        Assert.assertNotNull(response);
        Assert.assertEquals(200, response.getStatusCode().value());

        User user = response.getBody();
        Assert.assertEquals("test",user.getUsername());
        Assert.assertEquals("encodedPassword",user.getPassword());

        //Test the find user By Id
        when(userRepo.findById(user.getId())).thenReturn(Optional.of(user));
        ResponseEntity<User> response2 = userController.findById(user.getId());
        Assert.assertEquals(200, response2.getStatusCodeValue());

        //Test the find user By userame
        when(userRepo.findByUsername(user.getUsername())).thenReturn(user);
        ResponseEntity<User> response3 = userController.findByUserName(user.getUsername());
        Assert.assertEquals(200, response2.getStatusCodeValue());
    }

    @Test
    public void createUserBadPath(){
        CreateUserRequest req = new CreateUserRequest();
        req.setUsername("test");
        req.setPassword("testPassword");
        //Not same Password
        req.setConfirmPassword("testPassword123");

        ResponseEntity<User> response = userController.createUser(req);
        //bad Request
        Assert.assertEquals(400, response.getStatusCode().value());
    }

}
