CREATE TABLE IF NOT EXISTS products(
    product_id UUID PRIMARY KEY,
    fragile boolean,
    width NUMERIC(15, 2),
    height NUMERIC(15, 2),
    depth NUMERIC(15, 2),
    weight NUMERIC(15, 2),
    quantity INTEGER,
    CONSTRAINT check_width
             CHECK (width > 1),
    CONSTRAINT check_height
             CHECK (height > 1),
    CONSTRAINT check_depth
             CHECK (depth > 1),
    CONSTRAINT check_weight
             CHECK (weight > 1),
    CONSTRAINT check_quantity
                 CHECK (quantity >= 0)
);

CREATE TABLE IF NOT EXISTS order_bookings(
    order_booking_id UUID PRIMARY KEY
);

CREATE TABLE IF NOT EXISTS products_order_bookings(
    product_id UUID NOT NULL,
    order_booking_id UUID NOT NULL,
    quantity INTEGER,
    PRIMARY KEY (product_id, order_booking_id),
    FOREIGN KEY (order_booking_id) REFERENCES order_bookings(order_booking_id),
    CONSTRAINT check_quantity
        CHECK (quantity > 0)
);