#!/bin/bash
set -e

# создаем базы
psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
    CREATE DATABASE telemetry_analyzer;
    CREATE DATABASE commerce_shopping_store;
EOSQL

# подключаемся к каждой базе и применяем схемы
echo "Initializing schema for telemetry_analyzer..."
psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "telemetry_analyzer" -f /docker-entrypoint-initdb.d/02-telemetry-analyzer-schema.sql

echo "Initializing schema for commerce_shopping_store..."
psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "commerce_shopping_store" -f /docker-entrypoint-initdb.d/03-commerce-shopping-store-schema.sql
