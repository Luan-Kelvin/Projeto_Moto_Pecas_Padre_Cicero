CREATE TABLE parts(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    mark  VARCHAR(255),
    category VARCHAR(255),
    part_code VARCHAR(255) NOT NULL UNIQUE,
    cost_price NUMERIC(10,2) NOT NULL,
    selling_price NUMERIC(10,2) NOT NULL,
    minimum_stock INTEGER NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    creation_date DATE DEFAULT (CURRENT_DATE),
    creator_id BIGINT NOT NULL,

    CONSTRAINT fk_creator_id
    FOREIGN KEY (creator_id) REFERENCES employees (id)
);