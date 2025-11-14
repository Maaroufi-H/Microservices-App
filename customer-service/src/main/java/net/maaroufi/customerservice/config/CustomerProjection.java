package net.maaroufi.customerservice.config;

import org.springframework.data.rest.core.config.Projection;

import net.maaroufi.customerservice.entities.Customer;

@Projection(name = "all" , types = Customer.class)
public interface CustomerProjection {

    Long getId();
    String getName();
	String getSurname();
	String getEmail();
	int getAge();
}
