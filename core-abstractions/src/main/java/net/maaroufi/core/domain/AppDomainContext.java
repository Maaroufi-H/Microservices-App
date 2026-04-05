package net.maaroufi.core.domain;

/**
 * Domain-specific configuration bean.
 * Each service declares one @Bean of this type with its domain's metadata.
 *
 * Example (ecommerce):
 *   new AppDomainContext("ecommerce", "Product", "Customer", "Order", "behavior-events")
 *
 * Example (football):
 *   new AppDomainContext("football", "Match", "Player", "Booking", "behavior-events")
 */
public class AppDomainContext {

    private final String namespace;
    private final String productLabel;
    private final String customerLabel;
    private final String orderLabel;
    private final String kafkaTopic;

    public AppDomainContext(String namespace,
                            String productLabel,
                            String customerLabel,
                            String orderLabel,
                            String kafkaTopic) {
        this.namespace     = namespace;
        this.productLabel  = productLabel;
        this.customerLabel = customerLabel;
        this.orderLabel    = orderLabel;
        this.kafkaTopic    = kafkaTopic;
    }

    public String getNamespace()     { return namespace; }
    public String getProductLabel()  { return productLabel; }
    public String getCustomerLabel() { return customerLabel; }
    public String getOrderLabel()    { return orderLabel; }
    public String getKafkaTopic()    { return kafkaTopic; }
}
