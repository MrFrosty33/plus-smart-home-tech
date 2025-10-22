CREATE TABLE IF NOT EXISTS products (
    product_id VARCHAR(36) PRIMARY KEY,
    product_name VARCHAR(100) NOT NULL,
    description VARCHAR(1000) NOT NULL,
    image_src VARCHAR(1000),
    quantity_state VARCHAR,
    product_state VARCHAR,
    product_category VARCHAR,
    price NUMERIC(15, 2),
    CONSTRAINT check_quantity_state
        CHECK (quantity_state IN ('ENDED', 'FEW', 'ENOUGH', 'MANY')),
    CONSTRAINT check_product_state
        CHECK (product_state IN ('ACTIVATE', 'DEACTIVATE')),
    CONSTRAINT check_product_category
        CHECK (product_category IN ('LIGHTING', 'CONTROL', 'SENSORS'))
);