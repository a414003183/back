-- 1. Change default of order_source from MALL to WEB_MALL
ALTER TABLE order_info MODIFY COLUMN order_source VARCHAR(20) NOT NULL DEFAULT 'WEB_MALL';

-- 2. Update historical MALL records to WEB_MALL
UPDATE order_info SET order_source = 'WEB_MALL' WHERE order_source = 'MALL';

-- 3. Add composite index for order_source + created_time (idempotent)
DROP INDEX IF EXISTS idx_order_info_source_time ON order_info;
CREATE INDEX idx_order_info_source_time ON order_info(order_source, created_time);
