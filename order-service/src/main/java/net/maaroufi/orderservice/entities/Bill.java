package net.maaroufi.orderservice.entities;

import java.time.Instant;
import java.util.Date;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Transient;
import net.maaroufi.core.order.IOrder;

@Entity
public class Bill implements IOrder {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/** ID of the customer who placed this order (persisted). */
	private Long customerId;

	private Date billingDate;

	@OneToMany
	private List<WebOrderItem> customerItem;

	@Transient
	private Customer customer;

	@Transient
	private Product product;


	public Bill(Long id, Date billingDate, List<WebOrderItem> customerItem, Customer customer, Product product) {
		super();
		this.id = id;
		this.billingDate = billingDate;
		this.customerItem = customerItem;
		this.customer = customer;
		this.product = product;
	}


	public Bill() {
		super();
	}


	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public Date getBillingDate() {
		return billingDate;
	}


	public void setBillingDate(Date billingDate) {
		this.billingDate = billingDate;
	}


	public List<WebOrderItem> getCustomerItem() {
		return customerItem;
	}


	public void setCustomerItem(List<WebOrderItem> customerItem) {
		this.customerItem = customerItem;
	}


	public Customer getCustomer() {
		return customer;
	}


	public void setCustomer(Customer customer) {
		this.customer = customer;
	}


	public Product getProduct() {
		return product;
	}


	public void setProduct(Product product) {
		this.product = product;
	}

	public Long getCustomerId() { return customerId; }
	public void setCustomerId(Long customerId) { this.customerId = customerId; }

	// --- IOrder contract ---

	@Override
	public List<WebOrderItem> getOrderItems() {
		return customerItem;
	}

	@Override
	public OrderStatus getStatus() {
		return OrderStatus.CONFIRMED;
	}

	@Override
	public Instant getOrderDate() {
		return billingDate != null ? billingDate.toInstant() : null;
	}

}

