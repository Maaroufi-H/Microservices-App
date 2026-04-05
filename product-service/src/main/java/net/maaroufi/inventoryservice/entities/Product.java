package net.maaroufi.inventoryservice.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import net.maaroufi.core.product.IProduct;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Entity
public class Product implements IProduct {

	 @Id
	 @GeneratedValue(strategy = GenerationType.IDENTITY)
	 private Long id;
	 private String name;
	 private String description;
	 private Double price;
	 private Integer quantity;
	 // ML features: used for content-based filtering and Azure ML training
	 private String category;  // ex: "electronics", "clothing", "accessories"
	 private String tags;      // comma-separated, ex: "summer,beach,promo,trending"

	 public Product() {
		super();
	}
	 public Product(String name, String description, Double price, Integer quantity) {
		super();
		this.name = name;
		this.description = description;
		this.price = price;
		this.quantity = quantity;
	}
	 public Product(String name, String description, Double price, Integer quantity,
			 String category, String tags) {
		super();
		this.name = name;
		this.description = description;
		this.price = price;
		this.quantity = quantity;
		this.category = category;
		this.tags = tags;
	}

	 public Long getId() { return id; }
	 public void setId(Long id) { this.id = id; }

	 public String getName() { return name; }
	 public void setName(String name) { this.name = name; }

	 public String getDescription() { return description; }
	 public void setDescription(String description) { this.description = description; }

	 public Double getPrice() { return price; }
	 public void setPrice(Double price) { this.price = price; }

	 public Integer getQuantity() { return quantity; }
	 public void setQuantity(Integer quantity) { this.quantity = quantity; }

	 public String getCategory() { return category; }
	 public void setCategory(String category) { this.category = category; }

	 public String getTags() { return tags; }
	 public void setTags(String tags) { this.tags = tags; }

	// --- IProduct contract ---

	@Override
	public String getDisplayName() { return name; }

	@Override
	public BigDecimal getNominalPrice() {
		return price != null ? BigDecimal.valueOf(price) : BigDecimal.ZERO;
	}

	@Override
	public List<String> getTagList() {
		if (tags == null || tags.isBlank()) return Collections.emptyList();
		return Arrays.asList(tags.split(","));
	}

	@Override
	public boolean isAvailable() {
		return quantity != null && quantity > 0;
	}

	@Override
	public String getDomainNamespace() {
		return "ecommerce";
	}

}
