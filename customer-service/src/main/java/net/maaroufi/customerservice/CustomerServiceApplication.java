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
			Customer c = new Customer("hatem", "elhaj", "jax_marof@hotmail.com", 33);
			Customer c2 = new Customer("kabila", "traore", "traore@hotmail.com", 77);
		
			customerRepository.save(c);
			customerRepository.save(c2);

	    	List<Customer> list = customerRepository.findAll();
            list.forEach(element -> System.out.println(element));
            Predicate<Customer> p = el -> el.getAge() < 77;
            List<Customer> newlist = list.stream().filter(p).collect(Collectors.toList());
			System.out.println("filtered list");
            newlist.forEach(el -> System.out.println(el));
			System.out.println("Customer service application started successfully!");

		};
	}

}
