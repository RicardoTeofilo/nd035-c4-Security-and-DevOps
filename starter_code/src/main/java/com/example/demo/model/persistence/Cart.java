package com.example.demo.model.persistence;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cart")
public class Cart {

	private final static Logger log = LoggerFactory.getLogger(Cart.class);
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonProperty
	@Column
	private Long id;
	
	@ManyToMany
	@JsonProperty
	@Column
    private List<Item> items;
	
	@OneToOne(mappedBy = "cart")
	@JsonProperty
    private User user;
	
	@Column
	@JsonProperty
	private BigDecimal total;
	
	public BigDecimal getTotal() {
		return total;
	}

	public void setTotal(BigDecimal total) {
		this.total = total;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public List<Item> getItems() {
		return items;
	}

	public void setItems(List<Item> items) {
		this.items = items;
	}
	
	public void addItem(Item item) {
		if(items == null) {
			items = new ArrayList<>();
		}
		items.add(item);
		if(total == null) {
			total = new BigDecimal(0);
		}
		total = total.add(item.getPrice());
	}

	/**
	 * Here I will add some defensive logic to prevent the system from
	 * removing an item when:
	 * 1. The items list is null
	 * 2. The total is null
	 * 3. The items list does not contain item to be removed
	 *
	 * @param item
	 */
	public void removeItem(Item item) {
		if(items == null) {
			log.error("Attempting to remove an item with the items list null");
			items = new ArrayList<>();
			return;
		}
		if(total == null) {
			log.error("Attempting to remove an item with the total still null");
			total = new BigDecimal(0);
		}
		boolean itemRemoved = items.remove(item);
		if(!itemRemoved){
			log.error("Attempt to remove an item that does not exist in the list");
			return;
		}

		total = total.subtract(item.getPrice());
	}
}
