package net.maaroufi.orderservice.feign;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.hateoas.PagedModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import net.maaroufi.orderservice.entities.Customer;

@FeignClient(name = "customer-service")
public interface CustomerRestClient {

	
	@GetMapping("api/customers/{id}")
	Customer getCustomerByID(@PathVariable Long id);
	
	@GetMapping("api/customers")
	PagedModel<Customer> getAllCustomers();
	
}
