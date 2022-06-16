package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
public class ItemControllerTest {
    private ItemController itemController;
    private ItemRepository itemRepo = mock(ItemRepository.class);

    private List<Item> savedItems;

    @Before
    public void setUp(){
        itemController = new ItemController();
        TestUtils.injectObjects(itemController, "itemRepository", itemRepo);

        savedItems = new ArrayList<Item>();
        savedItems.add( new Item(1L,"Round Widget", 2.99 , "A widget that is round"));
        savedItems.add(new Item(2L,"Square Widget", 1.99, "A widget that is square"));
        itemController.saveItems(savedItems);

    }
    @Test
    public void TestGetItems(){
        when(itemRepo.findAll()).thenReturn(savedItems);

        ResponseEntity<List<Item>> response = itemController.getItems();
        Assert.assertEquals(200, response.getStatusCode().value());
        List<Item> responseItems = response.getBody();
        Assert.assertEquals(2, responseItems.size());
        Assert.assertEquals(savedItems.get(0), responseItems.get(0));
        Assert.assertEquals(savedItems.get(1), responseItems.get(1));
    }

    @Test
    public void TestGetItemById() {
        when(itemRepo.findById(savedItems.get(0).getId())).thenReturn(Optional.ofNullable(savedItems.get(0)));

        ResponseEntity<Item> response = itemController.getItemById(savedItems.get(0).getId());
        Assert.assertEquals(200, response.getStatusCode().value());
        Assert.assertEquals(savedItems.get(0).getName(), response.getBody().getName());
        Assert.assertEquals(savedItems.get(0).getPrice(), response.getBody().getPrice());
    }

    @Test
    public void TestGetItemByName() {
        List<Item> items = new ArrayList<>();
        items.add(savedItems.get(0));
        when(itemRepo.findByName(savedItems.get(0).getName())).thenReturn(items);


        ResponseEntity<List<Item>> response = itemController.getItemsByName(savedItems.get(0).getName());
        Assert.assertEquals(200, response.getStatusCode().value());
        Assert.assertEquals(savedItems.get(0).getName(), response.getBody().get(0).getName());
        Assert.assertEquals(savedItems.get(0).getPrice(), response.getBody().get(0).getPrice());
    }

    @Test
    public void TestGetItemByIdNotFound() {
        //Comment the return from the Repo Code - > item Not Found
        //when(itemRepo.findById(savedItems.get(0).getId())).thenReturn(Optional.ofNullable(savedItems.get(0)));

        ResponseEntity<Item> response = itemController.getItemById(savedItems.get(0).getId());
        Assert.assertEquals(404, response.getStatusCode().value());
    }

}
