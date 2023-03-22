package com.example.demo.integration;

import com.example.demo.SareetaApplication;
import com.example.demo.controller.ItemControllerTest;
import com.example.demo.controllers.ItemController;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.requests.CreateItemRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.*;

@Transactional
@SpringBootTest(classes = SareetaApplication.class)
@RunWith(SpringRunner.class)
public class ItemControllerFunctionalTest {

    @Autowired
    private ItemController itemController;

    private final String name = "Pencil";
    private final String description = "Wood Pencil";
    private final BigDecimal price =  new BigDecimal(5.99);

    @Test
    public void testCreateItem(){
        CreateItemRequest createItemRequest = ItemControllerTest.createItemRequest(this.name, this.description, this.price);
        ResponseEntity<Item> response = itemController.createItem(createItemRequest);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        Item returnedItem = response.getBody();
        assertNotNull(returnedItem);
        assertNotNull(returnedItem.getId());
        assertEquals(this.name, returnedItem.getName());
        assertEquals(this.description, returnedItem.getDescription());
        assertTrue(this.price.compareTo(returnedItem.getPrice()) == 0);
    }

    @Test
    public void testCreateItemEmptyName(){

        CreateItemRequest createItemRequest = ItemControllerTest.createItemRequest(null, this.description, this.price);
        ResponseEntity<Item> response = itemController.createItem(createItemRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCodeValue());
    }

    @Test
    public void testCreateItemEmptyDescription(){

        CreateItemRequest createItemRequest = ItemControllerTest.createItemRequest(this.name, null, this.price);
        ResponseEntity<Item> response = itemController.createItem(createItemRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCodeValue());
    }

    @Test
    public void testCreateItemEmptyPrice(){

        CreateItemRequest createItemRequest = ItemControllerTest.createItemRequest(this.name, this.description, null);
        ResponseEntity<Item> response = itemController.createItem(createItemRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCodeValue());
    }

    @Test
    public void testCreateItemAndFindById(){
        CreateItemRequest createItemRequest = ItemControllerTest.createItemRequest(this.name, this.description, this.price);
        ResponseEntity<Item> response = itemController.createItem(createItemRequest);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        Item savedItem = response.getBody();
        assertNotNull(savedItem);
        assertNotNull(savedItem.getId());
        assertEquals(this.name, savedItem.getName());
        assertEquals(this.description, savedItem.getDescription());
        assertTrue(this.price.compareTo(savedItem.getPrice()) == 0);

        ResponseEntity<Item> fetchedResponse = itemController.getItemById(savedItem.getId());
        assertNotNull(fetchedResponse);
        Item fetchedItem = fetchedResponse.getBody();
        assertNotNull(fetchedItem);
        assertEquals(savedItem.getName(), fetchedItem.getName());
        assertEquals(savedItem.getDescription(), fetchedItem.getDescription());
        assertTrue(fetchedItem.getPrice().compareTo(savedItem.getPrice()) == 0);
    }

    @Test
    public void testGetAllItems(){

        CreateItemRequest createItemRequest = ItemControllerTest.createItemRequest(this.name, this.description, this.price);
        ResponseEntity<Item> response = itemController.createItem(createItemRequest);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        Item returnedItem = response.getBody();

        ResponseEntity<List<Item>> fetchedResponse = itemController.getItems();
        assertNotNull(fetchedResponse);
        List<Item> itemList = fetchedResponse.getBody();
        assertTrue(itemList.size() > 0);
        assertTrue(itemList.contains(returnedItem));
    }

    @Test
    public void testGetItemsByName(){

        CreateItemRequest createItemRequest = ItemControllerTest.createItemRequest(this.name, this.description, this.price);
        ResponseEntity<Item> response = itemController.createItem(createItemRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
        Item returnedItem = response.getBody();
        assertNotNull(returnedItem);

        ResponseEntity<List<Item>> fetchedResponse = itemController.getItemsByName(returnedItem.getName());
        assertNotNull(fetchedResponse);
        List<Item> fetchedItemList = fetchedResponse.getBody();
        Item fetchedItem = fetchedItemList.get(0);
        assertNotNull(fetchedItem);
        assertEquals(returnedItem.getName(), fetchedItem.getName());
    }
}
