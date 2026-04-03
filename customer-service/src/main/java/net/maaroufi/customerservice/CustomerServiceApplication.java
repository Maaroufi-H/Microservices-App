package net.maaroufi.customerservice;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import net.maaroufi.customerservice.entities.Customer;
import net.maaroufi.customerservice.repository.CustomerRepository;

@SpringBootApplication
public class CustomerServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CustomerServiceApplication.class, args);
	}

	@Bean
	CommandLineRunner commandLineRunner(CustomerRepository customerRepository) {
		return args -> {
			customerRepository.save(new Customer("Ahmed",   "Benali",   "ahmed.benali@email.com",   28));
			customerRepository.save(new Customer("Fatima",  "Zahra",    "fatima.zahra@email.com",   35));
			customerRepository.save(new Customer("Mohamed", "Amine",    "m.amine@email.com",        42));
			customerRepository.save(new Customer("Sara",    "Dupont",   "sara.dupont@email.com",    24));
			customerRepository.save(new Customer("Karim",   "Rousseau", "k.rousseau@email.com",     55));
			customerRepository.save(new Customer("Leila",   "Martin",   "leila.martin@email.com",   31));
			customerRepository.save(new Customer("Youssef", "Petit",    "y.petit@email.com",        19));
			customerRepository.save(new Customer("Nadia",   "Bernard",  "nadia.bernard@email.com",  47));

			List<Customer> list = customerRepository.findAll();
			Predicate<Customer> p = el -> el.getAge() < 40;
			List<Customer> young = list.stream().filter(p).collect(Collectors.toList());
			System.out.println("=== Customers loaded: " + list.size() + " | Under 40: " + young.size() + " ===");
		};
	}

}
