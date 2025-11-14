package net.maaroufi.orderservice;

import java.util.Collection;
import java.util.Date;
import java.util.Random;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

import net.maaroufi.orderservice.entities.Bill;
import net.maaroufi.orderservice.entities.Customer;
import net.maaroufi.orderservice.entities.Product;
import net.maaroufi.orderservice.entities.ProductItem;
import net.maaroufi.orderservice.feign.CustomerRestClient;
import net.maaroufi.orderservice.feign.ProductRestClient;
import net.maaroufi.orderservice.repository.OrderRepository;
import net.maaroufi.orderservice.repository.ItemRepository;

@SpringBootApplication
@EnableFeignClients
public class OrderServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrderServiceApplication.class, args);
	}

	@Bean
	CommandLineRunner runner (OrderRepository billRepo,
			                 ItemRepository itemRepo,
			                 CustomerRestClient customerRestClient,
			                 ProductRestClient productRestClient
			) {
		
		
		return args -> {
			
			Collection<Customer> customerCollection = customerRestClient.getAllCustomers().getContent();
			
			Collection<Product> productCollection  = productRestClient.getAllProducts().getContent();
			
			
			customerCollection.forEach(cust -> {
				Bill bill = new Bill();
				bill.setCustomer(cust);
				bill.setBillingDate(new Date());
				billRepo.save(bill);
				productCollection.forEach(prod -> {
					ProductItem ite = new ProductItem();
                    ite.setBill(bill);
                    ite.setProductId(prod.getId());
                    ite.setQuantity(new Random().nextInt(10));
                    ite.setPrice(prod.getPrice());
                    itemRepo.save(ite);
				});
				
			});
			
			
		};
		
		
	}
}
