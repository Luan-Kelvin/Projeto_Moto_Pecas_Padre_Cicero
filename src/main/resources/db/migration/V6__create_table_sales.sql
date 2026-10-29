CREATE TABLE sales(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sale_code VARCHAR(255) NOT NULL UNIQUE,
    date_of_sale DATE DEFAULT (CURRENT_DATE),
    total_value NUMERIC(10,2),
    status_sale VARCHAR(255) NOT NULL DEFAULT 'ABERTA',
    employee_id BIGINT NOT NULL,

    CONSTRAINT fk_employee_id
    FOREIGN KEY (employee_id) REFERENCES employees (id)
);