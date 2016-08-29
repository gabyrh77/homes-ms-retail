CREATE TABLE store (
    store_id     BIGINT      NOT NULL AUTO_INCREMENT,
    store_name   VARCHAR(50) NOT NULL UNIQUE,
    PRIMARY KEY (store_id)
)
    ENGINE = InnoDB
    AUTO_INCREMENT = 1;

CREATE TABLE product (
    product_id          BIGINT         NOT NULL AUTO_INCREMENT,
    product_store_id    BIGINT         NOT NULL,
    product_name        VARCHAR(50)    NOT NULL,
    product_description VARCHAR(200)   NOT NULL,
    product_sku         VARCHAR(10)    NOT NULL,
    product_price       DECIMAL(15, 2) NOT NULL,
    PRIMARY KEY (product_id),
    CONSTRAINT unique_product_sku_store UNIQUE (product_sku, product_store_id),
    FOREIGN KEY (product_store_id) REFERENCES store(store_id)
)
    ENGINE = InnoDB
    AUTO_INCREMENT = 1;

CREATE TABLE stock (
    stock_id   BIGINT  NOT NULL AUTO_INCREMENT,
    stock_product_id BIGINT NOT NULL,
    stock_count      BIGINT NOT NULL,
    PRIMARY KEY (stock_id),
    FOREIGN KEY (stock_product_id) REFERENCES product(product_id)
)
    ENGINE = InnoDB
    AUTO_INCREMENT = 1;

CREATE TABLE client (
    client_id         BIGINT       NOT NULL AUTO_INCREMENT,
    client_first_name VARCHAR(50)  NOT NULL,
    client_last_name  VARCHAR(50)  NOT NULL,
    client_email      VARCHAR(50)  NOT NULL UNIQUE,
    client_phone      VARCHAR(10)  NOT NULL,
    PRIMARY KEY (client_id)
)
    ENGINE = InnoDB
    AUTO_INCREMENT = 1;

CREATE TABLE order_table (
    order_id            BIGINT       NOT NULL AUTO_INCREMENT,
    order_store_id      BIGINT       NOT NULL,
    order_client_id     BIGINT       NOT NULL,
    order_created_date  DATETIME     NOT NULL,
    order_status        INT          NOT NULL,
    PRIMARY KEY (order_id),
    FOREIGN KEY (order_store_id) REFERENCES store(store_id),
    FOREIGN KEY (order_client_id) REFERENCES client(client_id)
)
    ENGINE = InnoDB
    AUTO_INCREMENT = 1;

CREATE TABLE order_detail (
    detail_id               BIGINT NOT NULL AUTO_INCREMENT,
    detail_product_id       BIGINT NOT NULL,
    detail_order_id         BIGINT NOT NULL,
    detail_status           INT    NOT NULL,
    detail_product_count    BIGINT NOT NULL,
    PRIMARY KEY (detail_id),
    CONSTRAINT unique_order_product_status UNIQUE (detail_product_id, detail_order_id, detail_status),
    FOREIGN KEY (detail_product_id) REFERENCES product(product_id),
    FOREIGN KEY (detail_order_id) REFERENCES order_table(order_id)
)
    ENGINE = InnoDB
    AUTO_INCREMENT = 1;
