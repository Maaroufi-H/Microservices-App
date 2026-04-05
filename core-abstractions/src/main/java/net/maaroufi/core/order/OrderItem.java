package net.maaroufi.core.order;

import java.math.BigDecimal;

/**
 * Contract for a single line item within an IOrder.
 *
 * E-commerce : WebOrderItem implements OrderItem  (renamed from ProductItem)
 * Football   : FootSlot implements OrderItem      (player slot in a booking)
 */
public interface OrderItem {

    /** ID of the IProduct (or equivalent catalog item) in this line. */
    Long getCatalogItemId();

    int getQuantity();

    BigDecimal getUnitPrice();
}
