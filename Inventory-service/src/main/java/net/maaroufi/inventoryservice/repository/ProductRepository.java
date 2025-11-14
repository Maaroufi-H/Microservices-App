package net.maaroufi.inventoryservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.stereotype.Repository;

import net.maaroufi.inventoryservice.entities.Product;

@RepositoryRestResource
public interface ProductRepository extends JpaRepository<Product, Long> {

	@RestResource(path = "by-name-price" ,rel = "by-name-price")
	@Query("SELECT p FROM Product p WHERE p.name =?1 and p.price < ?2")
	List<Product> findByNameAndPrice(String name,Double price);
	
	@RestResource(path = "by-name" ,rel = "by-name")
	@Query("SELECT p FROM Product p WHERE p.name =:name")
	List<Product> findByNameAndPrice(@Param("name")String name);
}
