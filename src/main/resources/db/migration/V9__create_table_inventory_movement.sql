CREATE TABLE invetory_movement(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    stock_id BIGINT NOT NULL,
    movement_type VARCHAR(255) NOT NULL,
    quantity INTEGER NOT NULL,
    reason VARCHAR(255),
    employee_id BIGINT NOT NULL,
    sale_id BIGINT NOT NULL,
    transaction_date TIMESTAMP NOT NULL DEFAULT (CURRENT_TIMESTAMP),


    CONSTRAINT fk_stocks_id
    FOREIGN KEY (stock_id) REFERENCES stocks (id),

    CONSTRAINT fk_employees_id
    FOREIGN KEY (employee_id) REFERENCES employees (id),

    CONSTRAINT fk_sales_id_new
    FOREIGN KEY (sale_id) REFERENCES sales (id)
);