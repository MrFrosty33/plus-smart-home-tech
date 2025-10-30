-- в модулях shopping-store, shopping-cart, warehouse, analyzer
-- это используется только на GitHub Actions
-- т.к. я не могу использовать свой compose - в контейнере уже создаются БД
-- GitHub сам подставить ссылку на БД должен будет
-- вопрос, будет ли это работать
-- не забыть закомментировать url, username, password в конфиге

-- для запуска с моим compose это всё закомментировать, отключить sql.init.mode в конфиге каждого модуля
-- и раскомментировать url, username, password в конфиге

-- ох и геморр же. Почему просто нельзя мой compose использовать :((((((
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