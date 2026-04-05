package net.maaroufi.orderservice.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import net.maaroufi.core.order.OrderItem;

import java.math.BigDecimal;

/**
 * E-commerce implementation of OrderItem.
 * Renamed from ProductItem to clarify that this is the "web/ecommerce" concrete class.
 * Table name kept as "product_item" for backward compatibility.
 *
 * Football equivalent: FootSlot implements OrderItem
 */
@Entity
@Table(name = "product_item")
public class WebOrderItem implements OrderItem {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private long productId;

	@ManyToOne
	private Bill bill;

	@jakarta.persistence.Transient
	private Product product;

	private int quantity;

	private Double price;

	public WebOrderItem() {}

	public WebOrderItem(Long id, long productId, Bill bill, Product product) {
		this.id = id;
		this.productId = productId;
		this.bill = bill;
		this.product = product;
	}

	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }

	public long getProductId() { return productId; }
	public void setProductId(long productId) { this.productId = productId; }

	public Bill getBill() { return bill; }
	public void setBill(Bill bill) { this.bill = bill; }

	public Product getProduct() { return product; }
	public void setProduct(Product product) { this.product = product; }

	public int getQuantity() { return quantity; }
	public void setQuantity(int quantity) { this.quantity = quantity; }

	public Double getPrice() { return price; }
	public void setPrice(Double price) { this.price = price; }

	// --- OrderItem contract ---

	@Override
	public Long getCatalogItemId() { return productId; }

	@Override
	public BigDecimal getUnitPrice() {
		return price != null ? BigDecimal.valueOf(price) : BigDecimal.ZERO;
	}

}
