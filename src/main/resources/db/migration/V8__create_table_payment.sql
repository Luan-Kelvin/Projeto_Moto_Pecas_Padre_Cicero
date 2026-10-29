CREATE TABLE payments(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    value_payment NUMERIC(10,2) NOT NULL,
    payment_method VARCHAR(255) NOT NULL,
    payment_status VARCHAR(255) NOT NULL DEFAULT 'PENDENTE',
    payment_date TIMESTAMP NOT NULL DEFAULT (CURRENT_TIMESTAMP),
    external_reference VARCHAR(255),
    sale_id BIGINT NOT NULL,

    CONSTRAINT fk_sales_id
    FOREIGN KEY (sale_id) REFERENCES sales (id)
);