package net.maaroufi.orderservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import net.maaroufi.orderservice.entities.WebOrderItem;

public interface ItemRepository extends JpaRepository<WebOrderItem, Long> {

	
	
}
