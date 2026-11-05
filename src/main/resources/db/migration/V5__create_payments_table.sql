CREATE TABLE payment (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL UNIQUE REFERENCES orders(id),
    provider VARCHAR(30) NOT NULL,
    provider_payment_id VARCHAR(255) NOT NULL UNIQUE,
    status VARCHAR(30) NOT NULL,
    amount NUMERIC(10, 2) NOT NULL,
    confirmation_url TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);