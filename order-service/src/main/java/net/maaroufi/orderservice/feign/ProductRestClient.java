package net.maaroufi.orderservice.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.hateoas.PagedModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import net.maaroufi.orderservice.entities.Product;

@FeignClient(name = "product-service")
public interface ProductRestClient {

	@GetMapping("api/products/{id}")
	Product getProdcutByID(@PathVariable Long id);
	
	@GetMapping("api/products")
	PagedModel<Product> getAllProducts();

	
}
