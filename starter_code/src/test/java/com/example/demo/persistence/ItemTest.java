package com.example.demo.persistence;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ItemTest {

    private final long id = 1l;
    private final String name = "Pencil";
    private final String description = "Wood Pencil";
    private final BigDecimal price =  new BigDecimal(5.99);

    private final long id2 = 2l;
    private final String name2 = "Pen";
    private final String description2 = "Plastic Pen";
    private final BigDecimal price2 =  new BigDecimal(5.99);

    private Item item1;
    private Item item2;
    private Item item3;
    private Item item4;

    @Before
    public void setup(){
        item1 = new Item(this.name, this.price, this.description);
        item1.setId(this.id);
        item2 = new Item(this.name2, this.price2, this.description2);
        item2.setId(this.id2);
        //item3 will have the same id as item 1, making them equal according to their
        //equals's contract
        item3 = new Item(this.name, this.price, this.description);
        item3.setId(this.id);
        //item4 will have all the same properties as item1, but a different Id,
        //making them NOT equal according to their equals's contract
        item4 = new Item(this.name, this.price, this.description);
        item4.setId(this.id2);
    }

    @Test
    public void testItemEquality(){
        assertTrue(item1.equals(item1));
        assertTrue(item1.equals(item3));
        assertTrue(!item1.equals(item4));
        assertTrue(!item1.equals(item2));

        assertTrue(!item1.equals(null));
        assertTrue(!item1.equals(new Cart()));
        item1.setId(null);
        assertTrue(!item1.equals(item3));
    }

    @Test
    public void testHashCodeEquality(){
        assertTrue(item1.hashCode() == item1.hashCode());
        assertTrue(item1.hashCode() == item3.hashCode());
        assertTrue(item1.hashCode() != item2.hashCode());
        assertTrue(item1.hashCode() != item4.hashCode());
    }


}
