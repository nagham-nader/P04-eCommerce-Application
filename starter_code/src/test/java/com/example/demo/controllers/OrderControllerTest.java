package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
public class OrderControllerTest {

    private OrderController orderController= mock(OrderController.class);
    private OrderRepository orderRepo = mock(OrderRepository.class);
    private UserRepository userRepo = mock(UserRepository.class);
    private User user;
    private Cart cart;
    private UserOrder order;

    @Before
    public void setUp() {
        orderController = new OrderController();
        TestUtils.injectObjects(orderController, "orderRepository", orderRepo);
        TestUtils.injectObjects(orderController, "userRepository", userRepo);

        mockUserCart();


    }


    @Test
    public void submitUserOrderHappyPath(){
        ResponseEntity<UserOrder> response = orderController.submit(user.getUsername());

        Assert.assertEquals(200, response.getStatusCode().value());
        Assert.assertEquals(cart.getUser(), response.getBody().getUser());
        Assert.assertEquals(cart.getTotal(), response.getBody().getTotal());
        Assert.assertEquals(cart.getItems(), response.getBody().getItems());


    }
    @Test
    public void getOrdersForUserTest(){

        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser(user.getUsername());
        Assert.assertEquals(200, response.getStatusCode().value());

        List<UserOrder> resOrders = response.getBody();
        Assert.assertNotNull(resOrders);
        Assert.assertEquals(user.getCart().getItems(), resOrders.get(0).getItems());
        Assert.assertEquals(user.getCart().getTotal(), resOrders.get(0).getTotal());

    }

    private void mockUserCart(){
        //Create Item
        Item item = new Item();
        item.setId(1L);
        item.setPrice(BigDecimal.valueOf(2.99));

        //Create Cart
        cart = new Cart();
        cart.addItem(item);

        //Create User
        user = new User();
        user.setUsername("test");
        user.setCart(cart);

        cart.setUser(user);

        //Create Order
        order = UserOrder.createFromCart(user.getCart());
        List<UserOrder> orders = new ArrayList<>();
        orders.add(order);
        

        when(userRepo.findByUsername(user.getUsername())).thenReturn(user);
        when(orderRepo.findByUser(user)).thenReturn(orders);

    }

}
