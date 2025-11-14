package net.maaroufi.customerservice.controllers;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;
import net.maaroufi.customerservice.entities.Customer;
import net.maaroufi.customerservice.repository.CustomerRepository;

@RestController
@RequestMapping("/customers")	
public class CustomerController {

	
	    private CustomerRepository customerRepository;
	    
	    
		public CustomerController(CustomerRepository customerRepository) {
			this.customerRepository = customerRepository;
		}
	
		@GetMapping("/name")
		public List<Customer> getCustomersByName(@RequestParam String name) {
			return customerRepository.findByName(name);
		}
}
