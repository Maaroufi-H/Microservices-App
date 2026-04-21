-- =============================================================
--  Microservices-App — PostgreSQL Schema
--  Database: railway  (single shared DB, one schema per service)
--  Generated for PostgreSQL 15+
--  Run once before starting the application, or let Hibernate
--  handle DDL automatically (spring.jpa.hibernate.ddl-auto=update).
-- =============================================================


-- =============================================================
--  customer-service
-- =============================================================

CREATE TABLE IF NOT EXISTS customers (
    id                  BIGSERIAL       PRIMARY KEY,
    name                VARCHAR(255),
    surname             VARCHAR(255),
    email               VARCHAR(255),
    age                 INTEGER,
    -- ML features
    gender              VARCHAR(10),                -- 'M', 'F', 'other'
    country             VARCHAR(10),                -- ISO 3166-1 alpha-2, e.g. 'FR', 'MA', 'US'
    preferred_category  VARCHAR(255)                -- e.g. 'electronics', 'sports'
);


-- =============================================================
--  product-service
-- =============================================================

CREATE TABLE IF NOT EXISTS products (
    id          BIGSERIAL       PRIMARY KEY,
    name        VARCHAR(255),
    description TEXT,
    price       DOUBLE PRECISION,
    quantity    INTEGER,
    -- ML features
    category    VARCHAR(255),                       -- content-based filtering feature
    tags        VARCHAR(1000)                       -- comma-separated, e.g. 'wireless,bluetooth,audio'
);


-- =============================================================
--  Inventory-service
--  Uses the same column structure as product-service but manages
--  stock quantities independently.
-- =============================================================

CREATE TABLE IF NOT EXISTS inventory_products (
    id          BIGSERIAL       PRIMARY KEY,
    name        VARCHAR(255),
    description TEXT,
    price       DOUBLE PRECISION,
    quantity    INTEGER
);


-- =============================================================
--  order-service
--  Bill = order header; web_order_items = order lines.
-- =============================================================

CREATE TABLE IF NOT EXISTS bills (
    id              BIGSERIAL       PRIMARY KEY,
    customer_id     BIGINT,
    billing_date    DATE
);

CREATE TABLE IF NOT EXISTS web_order_items (
    id          BIGSERIAL           PRIMARY KEY,
    bill_id     BIGINT              NOT NULL,
    product_id  BIGINT              NOT NULL,
    quantity    INTEGER             NOT NULL,
    price       DOUBLE PRECISION,
    CONSTRAINT fk_web_order_items_bill
        FOREIGN KEY (bill_id) REFERENCES bills (id)
        ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_web_order_items_bill_id  ON web_order_items (bill_id);
CREATE INDEX IF NOT EXISTS idx_web_order_items_product  ON web_order_items (product_id);
CREATE INDEX IF NOT EXISTS idx_bills_customer           ON bills (customer_id);


-- =============================================================
--  payment-service
--  No persistent tables — payment-service processes payments
--  via REST and does not store state in the database.
--  If you need an audit trail, add this optional table:
-- =============================================================

-- CREATE TABLE IF NOT EXISTS payment_records (
--     id              BIGSERIAL       PRIMARY KEY,
--     order_id        VARCHAR(255),
--     status          VARCHAR(50),                 -- 'PaymentProcessed', 'Failed'
--     processed_at    TIMESTAMP WITH TIME ZONE    DEFAULT NOW()
-- );


-- =============================================================
--  tracking-service
--  Two tables: raw events + aggregated per-product stats.
-- =============================================================

-- Behaviour events — one row per user action
CREATE TABLE IF NOT EXISTS behavior_events (
    id                      BIGSERIAL               PRIMARY KEY,
    session_id              VARCHAR(255),
    customer_id             BIGINT,
    event_type              VARCHAR(50),             -- PRODUCT_VIEW | ADD_TO_CART | REMOVE_FROM_CART |
                                                     -- CHECKOUT_START | TRANSACTION_COMPLETE |
                                                     -- SEARCH | PAGE_VIEW | SESSION_START | SESSION_END
    product_id              BIGINT,
    search_query            VARCHAR(500),
    page_url                VARCHAR(1000),
    -- UTM tracking parameters
    utm_source              VARCHAR(255),
    utm_medium              VARCHAR(255),
    utm_campaign            VARCHAR(255),
    utm_content             VARCHAR(255),
    utm_term                VARCHAR(255),
    -- Timestamps
    client_timestamp        TIMESTAMP WITH TIME ZONE,
    server_timestamp        TIMESTAMP WITH TIME ZONE,
    -- Extended data
    raw_json                TEXT,                    -- full event payload
    domain_namespace        VARCHAR(255),            -- 'ecommerce', 'football', ...
    -- ML features / labels
    view_duration_ms        BIGINT,                  -- time on product page in ms
    converted               BOOLEAN,                 -- true if session led to purchase
    session_item_path_json  TEXT                     -- JSON array of product IDs seen in session
);

CREATE INDEX IF NOT EXISTS idx_behavior_events_session    ON behavior_events (session_id);
CREATE INDEX IF NOT EXISTS idx_behavior_events_customer   ON behavior_events (customer_id);
CREATE INDEX IF NOT EXISTS idx_behavior_events_product    ON behavior_events (product_id);
CREATE INDEX IF NOT EXISTS idx_behavior_events_event_type ON behavior_events (event_type);
CREATE INDEX IF NOT EXISTS idx_behavior_events_server_ts  ON behavior_events (server_timestamp);

-- Aggregated stats per product — upserted on every PRODUCT_VIEW event
CREATE TABLE IF NOT EXISTS product_tracking_stats (
    product_id              BIGINT          PRIMARY KEY,
    view_count              BIGINT          NOT NULL DEFAULT 0,
    total_view_duration_ms  BIGINT          NOT NULL DEFAULT 0,
    last_viewed_at          TIMESTAMP WITH TIME ZONE,
    domain_namespace        VARCHAR(255)
);


-- =============================================================
--  geolocation-service
-- =============================================================

CREATE TABLE IF NOT EXISTS user_geolocations (
    id              BIGSERIAL               PRIMARY KEY,
    customer_id     BIGINT,                          -- NULL for anonymous users
    session_id      VARCHAR(255),
    latitude        DOUBLE PRECISION,
    longitude       DOUBLE PRECISION,
    city            VARCHAR(255),
    country         VARCHAR(255),
    country_code    VARCHAR(10),                     -- ISO 3166-1 alpha-2
    full_address    TEXT,                            -- display_name from Nominatim
    state           VARCHAR(255),
    postcode        VARCHAR(20),
    located_at      TIMESTAMP WITH TIME ZONE,
    accuracy_meters DOUBLE PRECISION,                -- GPS browser accuracy
    timezone        VARCHAR(100),                    -- IANA timezone, e.g. 'Europe/Paris'
    source          VARCHAR(10)                      -- 'GPS' or 'IP'
);

CREATE INDEX IF NOT EXISTS idx_user_geolocations_customer ON user_geolocations (customer_id);
CREATE INDEX IF NOT EXISTS idx_user_geolocations_session  ON user_geolocations (session_id);
CREATE INDEX IF NOT EXISTS idx_user_geolocations_country  ON user_geolocations (country_code);


-- =============================================================
--  monitoring-service
--  No persistent tables — data is aggregated at query time
--  from tracking-service via Feign.
-- =============================================================


-- =============================================================
--  recommendation-service
--  No persistent tables — recommendations are computed on the
--  fly via Azure ML or popularity fallback.
-- =============================================================


-- =============================================================
--  claude-assistant-service
--  No persistent tables — conversations are stateless (held
--  in browser memory; history sent with each request).
-- =============================================================


-- =============================================================
--  Useful views (optional)
-- =============================================================

-- Top products by views
CREATE OR REPLACE VIEW v_top_products AS
SELECT
    product_id,
    view_count,
    total_view_duration_ms,
    CASE WHEN view_count = 0 THEN 0
         ELSE ROUND((total_view_duration_ms / view_count)::NUMERIC, 2)
    END AS avg_view_duration_ms,
    last_viewed_at,
    domain_namespace
FROM product_tracking_stats
ORDER BY view_count DESC;

-- Conversion rate per session
CREATE OR REPLACE VIEW v_session_conversion AS
SELECT
    session_id,
    MAX(customer_id)   AS customer_id,
    MAX(converted)     AS converted,
    COUNT(*)           AS event_count,
    MIN(server_timestamp) AS session_start,
    MAX(server_timestamp) AS session_end
FROM behavior_events
GROUP BY session_id;

-- Users by country (for geolocation stats endpoint)
CREATE OR REPLACE VIEW v_users_by_country AS
SELECT
    country,
    country_code,
    COUNT(*) AS user_count
FROM user_geolocations
GROUP BY country, country_code
ORDER BY user_count DESC;
