package net.maaroufi.customerservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import net.maaroufi.customerservice.entities.Customer;

@RepositoryRestResource
public interface CustomerRepository extends JpaRepository<Customer,Long>{

	
		// Custom query method to find customers by name
	List<Customer> findByName(String name);
	
	// Additional query methods can be defined here as needed
}
