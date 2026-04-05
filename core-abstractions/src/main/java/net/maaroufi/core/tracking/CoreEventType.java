package net.maaroufi.core.tracking;

/**
 * Generic event vocabulary used across all domain namespaces in the ML pipeline.
 * Each domain maps its local EventType enum to these core types via EventTypeMapper.
 *
 * Mapping examples (ecommerce):
 *   PRODUCT_VIEW    -> ITEM_DETAIL_VIEW
 *   PRODUCT_CLICK   -> ITEM_CLICK
 *   ADD_TO_CART     -> ADD_TO_INTENT
 *   REMOVE_FROM_CART-> REMOVE_FROM_INTENT
 *   PURCHASE        -> TRANSACTION_COMPLETE
 *   SEARCH          -> SEARCH
 *   PAGE_VIEW       -> PAGE_VIEW
 *   FB_AD_CLICK     -> AD_CLICK
 */
public enum CoreEventType {
    PAGE_VIEW,
    ITEM_CLICK,
    ITEM_DETAIL_VIEW,
    ADD_TO_INTENT,
    REMOVE_FROM_INTENT,
    TRANSACTION_COMPLETE,
    SEARCH,
    AD_CLICK
}
