package net.maaroufi.orderservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import net.maaroufi.orderservice.entities.Bill;

public interface  OrderRepository extends JpaRepository<Bill, Long> {

}
