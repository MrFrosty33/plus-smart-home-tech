CREATE TABLE IF NOT EXISTS payments (
    payment_id UUID PRIMARY KEY,
    total_payment NUMERIC(15, 2) NOT NULL,
    delivery_total NUMERIC(15, 2)NOT NULL,
    fee_total NUMERIC(15, 2) NOT NULL
);