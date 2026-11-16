ALTER TABLE invetory_movement
ADD COLUMN part_id BIGINT;

UPDATE invetory_movement
SET part_id = (
    SELECT stocks.part_id
    FROM stocks
    WHERE stocks.id = invetory_movement.stock_id
);

ALTER TABLE invetory_movement
MODIFY COLUMN part_id BIGINT NOT NULL;

ALTER TABLE invetory_movement
ADD CONSTRAINT fk_inventory_movement_part
FOREIGN KEY (part_id) REFERENCES parts (id);
