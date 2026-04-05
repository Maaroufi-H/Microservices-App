package net.maaroufi.core.tracking;

import java.time.Instant;
import java.util.List;

/**
 * Contract for any behavioral event tracked across all domain namespaces.
 *
 * The 3 ML-critical fields are:
 *   - viewDurationMs    : time spent viewing the item (ML feature #1)
 *   - converted         : did the session end in a transaction? (ML label #2, updated post-hoc)
 *   - sessionItemPath   : ordered list of item IDs browsed in the session (ML feature #3)
 */
public interface IBehaviorEvent {

    Long getId();

    String getSessionId();

    Long getCustomerId();

    CoreEventType getCoreEventType();

    /** ID of the IProduct (catalog item) this event is about. */
    Long getProductId();

    String getSearchQuery();

    String getPageUrl();

    IUtmContext getUtmContext();

    Instant getClientTimestamp();

    Instant getServerTimestamp();

    String getDomainNamespace();

    // --- ML features ---

    /** Time the user spent viewing the item in milliseconds. Null if not applicable. */
    Long getViewDurationMs();

    /**
     * Whether this session ultimately converted (purchase/booking completed).
     * Set to false at ingestion; updated to true by OrderEventConsumer (delayed labeling).
     */
    boolean isConverted();

    /**
     * Ordered list of catalogItemIds browsed during the session before this event.
     * Maintained client-side in sessionStorage and sent with each event.
     */
    List<Long> getSessionItemPath();
}
