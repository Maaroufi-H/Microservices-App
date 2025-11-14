package net.maaroufi.inventoryservice;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import net.maaroufi.inventoryservice.entities.Product;
import net.maaroufi.inventoryservice.repository.ProductRepository;

@SpringBootApplication
public class InventoryServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(InventoryServiceApplication.class, args);
	}

	
	@Bean
	CommandLineRunner commandLineRunner(ProductRepository productRepository) {
		
		return args -> {
			Product p1 = new Product("tv", "LED", 500.00, 3);
			Product p2 = new Product("tv", "LED", 300.00, 3);

			Product p3 = new Product("computer", "ASUS", 600.00, 8);
			productRepository.save(p1);
			productRepository.save(p2);
			Set<Product> set = new HashSet<>();
			set.add(p1);
			set.add(p2);
		    try {
			Optional<Product> op = set.stream().filter(e -> e.getPrice().equals(600.00)).findFirst();
		    Product p = op.get();
		    System.out.print(p);

		    }catch (Exception x){
		    	System.out.println("erreur : " +x);
		    }
		    
		    
		    Long somme = (long) set.stream().filter(e -> e.getPrice() < 600.00).mapToDouble(i -> i.getPrice()).sum();
		    System.out.println("la somme est : "+  somme);
		    
		    

		};
		
	}
}
