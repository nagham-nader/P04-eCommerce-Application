package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
public class CartControllerTest {

    private CartController cartController = mock(CartController.class);

    private UserRepository userRepo = mock(UserRepository.class);

    private CartRepository cartRepo = mock(CartRepository.class);

    private ItemRepository itemRepo = mock(ItemRepository.class);

    @Before
    public void setUp(){
        cartController = new CartController();
        TestUtils.injectObjects(cartController, "userRepository", userRepo);
        TestUtils.injectObjects(cartController, "cartRepository", cartRepo);
        TestUtils.injectObjects(cartController, "itemRepository", itemRepo);

    }

    @Test
    public void addToCartHappyPath(){
        User user = mockUser();
        Item item = mockUItemWithPrise(3.50);

        ModifyCartRequest req = new ModifyCartRequest();
        req.setItemId(item.getId());
        req.setQuantity(2);
        req.setUsername(user.getUsername());
        ResponseEntity<Cart> response = cartController.addTocart(req);

        Assert.assertEquals(200, response.getStatusCode().value());
        Assert.assertEquals(BigDecimal.valueOf(7.0), response.getBody().getTotal());

        //Test not valid Item id
        req.setItemId(4);
        ResponseEntity<Cart> response2 = cartController.addTocart(req);
        Assert.assertEquals(404, response2.getStatusCode().value());

    }

    @Test
    public void addToCartBadPath(){
        //Fake User
        User user = new User();
        user.setUsername("FakeUser");


        Item item = mockUItemWithPrise(3.50);

        ModifyCartRequest req = new ModifyCartRequest();
        req.setItemId(item.getId());
        req.setQuantity(2);
        req.setUsername(user.getUsername());
        ResponseEntity<Cart> response = cartController.addTocart(req);

        Assert.assertEquals(404, response.getStatusCode().value());
    }

    @Test
    public void removeFromCartTest(){

        User user = mockUser();
        Item item = mockUItemWithPrise(3.50);

        ModifyCartRequest req = new ModifyCartRequest();
        req.setItemId(item.getId());
        req.setQuantity(2);
        req.setUsername(user.getUsername());

        ResponseEntity<Cart> response = cartController.removeFromcart(req);
        Assert.assertEquals(200, response.getStatusCode().value());
        Assert.assertEquals(BigDecimal.valueOf(-7.0), response.getBody().getTotal());


    }
    @Test
    public void removeFromCartBadPathItem(){
        User user = mockUser();

        //Fake Item
        Item item = new Item();
        item.setId(23L);
        ModifyCartRequest req = new ModifyCartRequest();
        req.setItemId(item.getId());
        req.setQuantity(2);
        req.setUsername(user.getUsername());

        ResponseEntity<Cart> response = cartController.removeFromcart(req);
        Assert.assertEquals(404, response.getStatusCode().value());
    }

    @Test
    public void removeFromCartBadPathUser(){

        //Fake User
        User user = new User();
        user.setUsername("FakeUser");
        Item item = mockUItemWithPrise(3.50);

        ModifyCartRequest req = new ModifyCartRequest();
        req.setItemId(item.getId());
        req.setQuantity(2);
        req.setUsername(user.getUsername());

        ResponseEntity<Cart> response = cartController.removeFromcart(req);
        Assert.assertEquals(404, response.getStatusCode().value());

    }
    private User mockUser(){
        //Create User
        User user = new User();
        user.setUsername("test");
        user.setCart(new Cart());
        when(userRepo.findByUsername(user.getUsername())).thenReturn(user);
        return user;
    }

    private Item mockUItemWithPrise(double prise){
        //Create Item
        Item item = new Item();
        item.setId(1L);
        item.setPrice(BigDecimal.valueOf(prise));
        when(itemRepo.findById(item.getId())).thenReturn(Optional.of(item));
        return item;
    }
}
