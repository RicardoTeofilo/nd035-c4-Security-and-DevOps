package com.example.demo.controller;

import com.example.demo.TestUtils;
import com.example.demo.controllers.ItemController;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.requests.CreateItemRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ItemControllerTest {

    private ItemController itemController;
    private ItemRepository itemRepository = mock(ItemRepository.class);

    private final String name = "Pencil";
    private final String description = "Wood Pencil";
    private final BigDecimal price =  new BigDecimal(5.99);

    @Before
    public void setup(){
        itemController = new ItemController();
        TestUtils.injectObjects(itemController, "itemRepository", itemRepository);
    }

    @Test
    public void testCreateItemEmptyName(){

        CreateItemRequest createItemRequest = createItemRequest(null, this.description, this.price);
        ResponseEntity<Item> response = itemController.createItem(createItemRequest);

        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    public void testCreateItemEmptyDescription(){

        CreateItemRequest createItemRequest = createItemRequest(this.name, null, this.price);
        ResponseEntity<Item> response = itemController.createItem(createItemRequest);

        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    public void testCreateItemEmptyPrice(){

        CreateItemRequest createItemRequest = createItemRequest(this.name, this.description, null);
        ResponseEntity<Item> response = itemController.createItem(createItemRequest);

        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    public void testGetItemById(){
        Item item = createItem(this.name, this.description, this.price);
        when(itemRepository.save(any())).thenReturn(item);

        CreateItemRequest createItemRequest = createItemRequest(this.name, this.description, this.price);
        ResponseEntity<Item> response = itemController.createItem(createItemRequest);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        Item returnedItem = response.getBody();
        assertNotNull(returnedItem);

        when(itemRepository.findById(any())).thenReturn(Optional.of(item));
        ResponseEntity<Item> fetchedResponse = itemController.getItemById(returnedItem.getId());
        assertNotNull(fetchedResponse);
        Item fetchedItem = fetchedResponse.getBody();
        assertNotNull(fetchedItem);
        assertEquals(item.getName(), fetchedItem.getName());
        assertEquals(item.getDescription(), fetchedItem.getDescription());
        assertTrue(fetchedItem.getPrice().compareTo(returnedItem.getPrice()) == 0);

    }

    @Test
    public void testGetAllItems(){
        Item item = createItem(this.name, this.description, this.price);
        when(itemRepository.save(any())).thenReturn(item);

        CreateItemRequest createItemRequest = createItemRequest(this.name, this.description, this.price);
        ResponseEntity<Item> response = itemController.createItem(createItemRequest);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        Item returnedItem = response.getBody();

        when(itemRepository.findAll()).thenReturn(Collections.singletonList(item));
        ResponseEntity<List<Item>> fetchedResponse = itemController.getItems();
        assertNotNull(fetchedResponse);
        Item fetchedItem = fetchedResponse.getBody().get(0);
        assertNotNull(fetchedItem);
        assertEquals(item.getName(), fetchedItem.getName());
        assertTrue(fetchedItem.getPrice().compareTo(returnedItem.getPrice()) == 0);

    }

    @Test
    public void testGetItemsByName(){
        Item item = createItem(this.name, this.description, this.price);
        when(itemRepository.save(any())).thenReturn(item);

        CreateItemRequest createItemRequest = createItemRequest(this.name, this.description, this.price);
        ResponseEntity<Item> response = itemController.createItem(createItemRequest);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        Item returnedItem = response.getBody();
        assertNotNull(returnedItem);

        when(itemRepository.findByName(any())).thenReturn(Collections.singletonList(item));
        ResponseEntity<List<Item>> fetchedResponse = itemController.getItemsByName(returnedItem.getName());
        assertNotNull(fetchedResponse);
        List<Item> fetchedItemList = fetchedResponse.getBody();
        Item fetchedItem = fetchedItemList.get(0);
        assertNotNull(fetchedItem);
        assertEquals(item.getName(), fetchedItem.getName());
    }

    public static Item createItem(String name, String description, BigDecimal price){
        Item item = new Item();
        item.setId(1l);
        item.setName(name);
        item.setDescription(description);
        item.setPrice(price);
        return item;
    }

    public static CreateItemRequest createItemRequest(String name, String description, BigDecimal price){
        CreateItemRequest item = new CreateItemRequest();
        item.setName(name);
        item.setDescription(description);
        item.setPrice(price);
        return item;
    }

}
