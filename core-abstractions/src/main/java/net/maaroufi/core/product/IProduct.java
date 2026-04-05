package net.maaroufi.core.product;

import java.math.BigDecimal;
import java.util.List;

/**
 * Contract for any item that can be browsed, clicked, and purchased/booked.
 *
 * E-commerce : Product implements IProduct
 * Football   : FootMatch implements IProduct
 */
public interface IProduct {

    Long getId();

    String getDisplayName();

    /**
     * ML feature — nominal price / value of the item.
     * Named getNominalPrice() to avoid return-type conflict with domain-specific Double getPrice() getters.
     */
    BigDecimal getNominalPrice();

    /**
     * ML feature — content-based filtering category (e.g. "electronics", "accessories", "match").
     */
    String getCategory();

    /**
     * ML feature — list of tags (e.g. ["summer", "promo", "trending"]).
     * Named getTagList() to avoid return-type conflict with domain-specific String getTags() getters.
     */
    List<String> getTagList();

    /** Whether the item is currently purchasable / bookable. */
    boolean isAvailable();

    /**
     * Domain namespace used to route tracking events and ML models.
     * E-commerce : "ecommerce"
     * Football   : "football"
     */
    String getDomainNamespace();
}
