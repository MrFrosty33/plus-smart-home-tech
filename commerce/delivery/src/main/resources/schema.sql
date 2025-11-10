CREATE TABLE IF NOT EXISTS deliveries (
    delivery_id UUID PRIMARY KEY,
    order_id UUID NOT NULL,
    delivery_state VARCHAR(100) NOT NULL,
    CONSTRAINT check_delivery_state
        CHECK (delivery_state IN ('CREATED', 'IN_PROGRESS', 'DELIVERED', 'FAILED', 'CANCELLED'))
);

CREATE TABLE IF NOT EXISTS addresses (
    address_id UUID PRIMARY KEY,
    country VARCHAR(1000) NOT NULL,
    city VARCHAR(1000) NOT NULL,
    street VARCHAR(1000) NOT NULL,
    house VARCHAR(100) NOT NULL,
    flat VARCHAR(100)
);