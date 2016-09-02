CREATE TABLE store (
    id     BIGINT      NOT NULL AUTO_INCREMENT,
    name   VARCHAR(50) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT unique_store_name UNIQUE (name)
);

CREATE TABLE product (
    id          BIGINT         NOT NULL AUTO_INCREMENT,
    store_id    BIGINT         NOT NULL,
    name        VARCHAR(50)    NOT NULL,
    description VARCHAR(200)   NOT NULL,
    sku         VARCHAR(10)    NOT NULL,
    price       DECIMAL(15, 2) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT unique_product_sku_store UNIQUE (sku, store_id),
    FOREIGN KEY (store_id) REFERENCES store(id)
);

CREATE TABLE stock (
    id              BIGINT  NOT NULL AUTO_INCREMENT,
    product_id      BIGINT NOT NULL,
    product_count   BIGINT NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (product_id) REFERENCES product(id),
    CONSTRAINT unique_product_stock UNIQUE (product_id)
);

CREATE TABLE client (
    id         BIGINT       NOT NULL AUTO_INCREMENT,
    first_name VARCHAR(50)  NOT NULL,
    last_name  VARCHAR(50)  NOT NULL,
    email      VARCHAR(50)  NOT NULL,
    phone      VARCHAR(10)  NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT unique_client_email UNIQUE (email)
);

CREATE TABLE order_table (
    id            BIGINT       NOT NULL AUTO_INCREMENT,
    store_id      BIGINT       NOT NULL,
    client_id     BIGINT       NOT NULL,
    created_date  DATETIME     NOT NULL,
    status        INT          NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (store_id) REFERENCES store(id),
    FOREIGN KEY (client_id) REFERENCES client(id)
);

CREATE TABLE order_detail (
    id               BIGINT NOT NULL AUTO_INCREMENT,
    product_id       BIGINT NOT NULL,
    order_id         BIGINT NOT NULL,
    status           INT    NOT NULL,
    product_count    BIGINT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT unique_order_product_status UNIQUE (product_id, order_id, status),
    FOREIGN KEY (product_id) REFERENCES product(id),
    FOREIGN KEY (order_id) REFERENCES order_table(id)
);
