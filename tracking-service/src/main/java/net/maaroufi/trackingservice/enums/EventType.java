package net.maaroufi.trackingservice.enums;

public enum EventType {
    PAGE_VIEW,        // User lands on a page (captures FB UTM params)
    PRODUCT_VIEW,     // User views a product detail page
    PRODUCT_CLICK,    // User clicks on a product card
    ADD_TO_CART,      // User adds a product to cart
    REMOVE_FROM_CART, // User removes a product from cart
    PURCHASE,         // User completes a purchase
    SEARCH,           // User performs a product search
    FB_AD_CLICK       // User arrives from a Facebook ad
}
