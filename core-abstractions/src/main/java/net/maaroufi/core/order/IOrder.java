package net.maaroufi.core.order;

import java.time.Instant;
import java.util.List;

/**
 * Contract for any transaction across any domain.
 *
 * E-commerce : Bill implements IOrder
 * Football   : FootBooking implements IOrder
 */
public interface IOrder {

    Long getId();

    /** ID of the ICustomer who placed this order. */
    Long getCustomerId();

    List<? extends OrderItem> getOrderItems();

    OrderStatus getStatus();

    Instant getOrderDate();

    enum OrderStatus {
        PENDING,
        CONFIRMED,
        CANCELLED,
        COMPLETED
    }
}
