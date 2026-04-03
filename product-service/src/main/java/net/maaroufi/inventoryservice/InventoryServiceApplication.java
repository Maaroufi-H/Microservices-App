package net.maaroufi.inventoryservice;

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
			productRepository.save(new Product("Laptop Dell XPS 15",       "Ultra-thin 15-inch laptop with Intel Core i7",       1299.99, 15));
			productRepository.save(new Product("iPhone 15 Pro",            "Apple smartphone derniere generation 256GB",          1199.00, 30));
			productRepository.save(new Product("Samsung TV 4K 55\"",       "Television QLED 55 pouces 4K HDR",                     799.99,  8));
			productRepository.save(new Product("AirPods Pro 2",            "Ecouteurs sans fil avec reduction de bruit active",    279.00, 50));
			productRepository.save(new Product("PlayStation 5",            "Console de jeux Sony derniere generation",             549.99,  5));
			productRepository.save(new Product("Casque Sony WH-1000XM5",  "Casque audio premium sans fil 30h autonomie",          349.00, 20));
			productRepository.save(new Product("iPad Air M2",             "Tablette Apple 10.9 pouces puce M2",                   699.00, 25));
			productRepository.save(new Product("Clavier Logitech MX Keys","Clavier mecanique sans fil retroeclaire",              119.99, 40));

			long count = productRepository.count();
			System.out.println("=== Products loaded: " + count + " ===");
		};
	}
}
