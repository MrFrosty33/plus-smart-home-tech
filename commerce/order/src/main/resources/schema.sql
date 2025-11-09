CREATE TABLE IF NOT EXISTS orders (
    order_id UUID PRIMARY KEY,
    shopping_cart_id UUID NOT NULL,
    payment_id UUID NOT NULL,
    delivery_iud UUID NOT NULL,
    delivery_weight NUMERIC(15, 2),
    delivery_volume NUMERIC(15, 2),
    fragile boolean,
    total_price NUMERIC(15, 2),
    delivery_price NUMERIC(15, 2),
    products_price NUMERIC(15, 2)
);

CREATE TABLE IF NOT EXISTS orders_products (
    order_id UUID NOT NULL,
    product_id UUID NOT NULL,
    quantity INTEGER,
    PRIMARY KEY (order_id, product_id),
    FOREIGN KEY (order_id) REFERENCES orders(id),
    CONSTRAINT check_quantity
        CHECK (quantity > 0)
);