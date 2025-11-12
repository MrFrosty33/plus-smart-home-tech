CREATE TABLE IF NOT EXISTS payments (
    payment_id UUID PRIMARY KEY,
    order_id UUID NOT NULL,
    total_payment NUMERIC(15, 2) NOT NULL,
    delivery_total NUMERIC(15, 2)NOT NULL,
    product_total NUMERIC(15, 2) NOT NULL,
    payment_state VARCHAR(100) NOT NULL,
    CONSTRAINT check_payment_state
        CHECK(payment_state IN('PENDING', 'SUCCESS', 'FAILED'))
);