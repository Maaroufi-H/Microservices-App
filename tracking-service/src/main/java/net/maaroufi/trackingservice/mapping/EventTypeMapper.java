package net.maaroufi.trackingservice.mapping;

import net.maaroufi.core.tracking.CoreEventType;
import net.maaroufi.trackingservice.enums.EventType;

/**
 * Maps the ecommerce-specific EventType enum to the generic CoreEventType vocabulary
 * used across all domain namespaces in the ML pipeline.
 *
 * Each new domain app must provide its own mapping (or extend this utility).
 */
public final class EventTypeMapper {

    private EventTypeMapper() {}

    public static CoreEventType toCoreEventType(EventType eventType) {
        if (eventType == null) return CoreEventType.PAGE_VIEW;
        switch (eventType) {
            case PRODUCT_VIEW:     return CoreEventType.ITEM_DETAIL_VIEW;
            case PRODUCT_CLICK:    return CoreEventType.ITEM_CLICK;
            case ADD_TO_CART:      return CoreEventType.ADD_TO_INTENT;
            case REMOVE_FROM_CART: return CoreEventType.REMOVE_FROM_INTENT;
            case PURCHASE:         return CoreEventType.TRANSACTION_COMPLETE;
            case SEARCH:           return CoreEventType.SEARCH;
            case PAGE_VIEW:        return CoreEventType.PAGE_VIEW;
            case FB_AD_CLICK:      return CoreEventType.AD_CLICK;
            default:               return CoreEventType.PAGE_VIEW;
        }
    }
}
