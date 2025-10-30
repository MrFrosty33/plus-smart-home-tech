CREATE TABLE IF NOT EXISTS carts (
    cart_id UUID PRIMARY KEY,
    username VARCHAR(100) NOT NULL,
    active boolean
);

CREATE TABLE carts_products (
    cart_id UUID REFERENCES carts(cart_id) ON DELETE CASCADE,
    product_id UUID,
    quantity INTEGER NOT NULL,
    PRIMARY KEY (cart_id, product_id),
    CONSTRAINT check_quantity
         CHECK (quantity > 0)
);