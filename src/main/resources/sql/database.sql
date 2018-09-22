CREATE TABLE Pages (
  id SERIAL PRIMARY KEY,
  url varchar(255) NOT NULL
);

CREATE TABLE Product (
  id SERIAL PRIMARY KEY,
  timestamp timestamp(0) without time zone DEFAULT (now() at time zone 'EEST'),
  name varchar(255) NOT NULL,
  price decimal NOT NULL,
  url varchar(255) NOT NULL
);

CREATE TABLE Kaup24 (
  id SERIAL PRIMARY KEY,
  product_id integer REFERENCES Product (id) NOT NULL,
  coupon_discount integer NOT NULL,
  coupon_min_sum integer NOT NULL
);