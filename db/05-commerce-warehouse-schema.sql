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