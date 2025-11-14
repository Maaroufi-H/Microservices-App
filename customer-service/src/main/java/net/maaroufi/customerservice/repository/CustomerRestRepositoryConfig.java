package net.maaroufi.customerservice.repository;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

import net.maaroufi.customerservice.entities.Customer;



@Configuration
public class CustomerRestRepositoryConfig implements RepositoryRestConfigurer {

	@Override
	public void configureRepositoryRestConfiguration(org.springframework.data.rest.core.config.RepositoryRestConfiguration config
			,CorsRegistry corsRegistry
			) {
		// Expose the ProductWithIdProjection
		// Optionally, you can customize other aspects of the REST configuration here
		// For example, you can set the base path, expose IDs, etc.
		config.setBasePath("/api");
		config.exposeIdsFor(Customer.class);
	}
	
}