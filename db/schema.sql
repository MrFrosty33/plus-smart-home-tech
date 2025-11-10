CREATE DATABASE telemetry_analyzer;
CREATE DATABASE commerce_shopping_store;
CREATE DATABASE commerce_shopping_cart;
CREATE DATABASE commerce_warehouse;
CREATE DATABASE commerce_delivery;
CREATE DATABASE commerce_order;
CREATE DATABASE commerce_payment;

\connect telemetry_analyzer;

-- создаём таблицу scenarios
CREATE TABLE IF NOT EXISTS scenarios (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    hub_id VARCHAR,
    name VARCHAR,
    UNIQUE(hub_id, name)
);

-- создаём таблицу sensors
CREATE TABLE IF NOT EXISTS sensors (
    id VARCHAR PRIMARY KEY,
    hub_id VARCHAR
);

-- создаём таблицу conditions
CREATE TABLE IF NOT EXISTS conditions (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    type VARCHAR,
    operation VARCHAR,
    value INTEGER
);

-- создаём таблицу actions
CREATE TABLE IF NOT EXISTS actions (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    type VARCHAR,
    value INTEGER
);

-- создаём таблицу scenario_conditions, связывающую сценарий, датчик и условие активации сценария
CREATE TABLE IF NOT EXISTS scenario_conditions (
    scenario_id BIGINT REFERENCES scenarios(id),
    sensor_id VARCHAR REFERENCES sensors(id),
    condition_id BIGINT REFERENCES conditions(id),
    PRIMARY KEY (scenario_id, sensor_id, condition_id)
);

-- создаём таблицу scenario_actions, связывающую сценарий, датчик и действие, которое нужно выполнить при активации сценария
CREATE TABLE IF NOT EXISTS scenario_actions (
    scenario_id BIGINT REFERENCES scenarios(id),
    sensor_id VARCHAR REFERENCES sensors(id),
    action_id BIGINT REFERENCES actions(id),
    PRIMARY KEY (scenario_id, sensor_id, action_id)
);

-- создаём функцию для проверки, что связываемые сценарий и датчик работают с одним и тем же хабом
CREATE OR REPLACE FUNCTION check_hub_id()
RETURNS TRIGGER AS
'
BEGIN
    IF (SELECT hub_id FROM scenarios WHERE id = NEW.scenario_id) != (SELECT hub_id FROM sensors WHERE id = NEW.sensor_id) THEN
        RAISE EXCEPTION ''Hub IDs do not match for scenario_id % and sensor_id %'', NEW.scenario_id, NEW.sensor_id;
    END IF;
    RETURN NEW;
END;
'
LANGUAGE plpgsql;

-- создаём триггер, проверяющий, что «условие» связывает корректные сценарий и датчик
CREATE OR REPLACE TRIGGER tr_bi_scenario_conditions_hub_id_check
BEFORE INSERT ON scenario_conditions
FOR EACH ROW
EXECUTE FUNCTION check_hub_id();

-- создаём триггер, проверяющий, что «действие» связывает корректные сценарий и датчик
CREATE OR REPLACE TRIGGER tr_bi_scenario_actions_hub_id_check
BEFORE INSERT ON scenario_actions
FOR EACH ROW
EXECUTE FUNCTION check_hub_id();

\connect commerce_shopping_store;

CREATE TABLE IF NOT EXISTS products (
    product_id UUID PRIMARY KEY,
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
        CHECK (product_state IN ('ACTIVE', 'DEACTIVATE')),
    CONSTRAINT check_product_category
        CHECK (product_category IN ('LIGHTING', 'CONTROL', 'SENSORS'))
);

\connect commerce_shopping_cart;

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

\connect commerce_warehouse;

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
    order_booking_id UUID PRIMARY KEY,
    delivery_id UUID
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

\connect commerce_delivery;

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

\connect commerce_order;

CREATE TABLE IF NOT EXISTS orders (
    order_id UUID PRIMARY KEY,
    shopping_cart_id UUID NOT NULL,
    payment_id UUID NOT NULL,
    delivery_iud UUID NOT NULL,
    state VARCHAR(100) NOT NULL,
    delivery_weight NUMERIC(15, 2),
    delivery_volume NUMERIC(15, 2),
    fragile boolean,
    total_price NUMERIC(15, 2),
    delivery_price NUMERIC(15, 2),
    products_price NUMERIC(15, 2),
    CONSTRAINT check_state
        CHECK (state IN ('NEW', 'ON_PAYMENT', 'ON_DELIVERY', 'DONE', 'DELIVERED', 'ASSEMBLED', 'PAID', 'COMPLETED',
                'DELIVERY_FAILED', 'ASSEMBLY_FAILED', 'PAYMENT_FAILED', 'PRODUCT_RETURNED', 'CANCELED'))
);

CREATE TABLE IF NOT EXISTS orders_products (
    order_id UUID NOT NULL,
    product_id UUID NOT NULL,
    quantity INTEGER,
    PRIMARY KEY (order_id, product_id),
    FOREIGN KEY (order_id) REFERENCES orders(order_id),
    CONSTRAINT check_quantity
        CHECK (quantity > 0)
);

\connect commerce_payment

CREATE TABLE IF NOT EXISTS payments (
    payment_id UUID PRIMARY KEY,
    total_payment NUMERIC(15, 2) NOT NULL,
    delivery_total NUMERIC(15, 2)NOT NULL,
    fee_total NUMERIC(15, 2) NOT NULL
);

