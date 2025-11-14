package net.maaroufi.orderservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import net.maaroufi.orderservice.entities.ProductItem;

public interface ItemRepository extends JpaRepository<ProductItem, Long> {

	
	
}
