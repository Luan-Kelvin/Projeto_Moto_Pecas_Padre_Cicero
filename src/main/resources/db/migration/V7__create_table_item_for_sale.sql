CREATE TABLE items_for_sales(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    quantity INTEGER NOT NULL,
    unit_price NUMERIC(10,2) NOT NULL,
    subtotal NUMERIC(10,2),
    sale_id BIGINT NOT NULL,
    part_id BIGINT NOT NULL,

    CONSTRAINT fk_sale_id
    FOREIGN KEY (sale_id) REFERENCES sales (id),

    CONSTRAINT fk_parts_id
    FOREIGN KEY (part_id) REFERENCES parts (id)
);