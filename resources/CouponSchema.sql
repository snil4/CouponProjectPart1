-- Uncomment the next line to restart the schema
-- drop schema coupons_db;

-- create the schema
create schema coupons_db;
use coupons_db;

-- create the tables
create table `companies`(
 id int primary key auto_increment, 
 `name` varchar(20),
 email varchar(30), 
 `password` varchar(25)
);
create table `customers`(
 id int primary key auto_increment, 
 `first_name` varchar(20), 
 last_name varchar(20), 
 email varchar(30), 
 `password` varchar(25)
);
create table `coupons`(
 id int primary key auto_increment, 
 company_id int, 
 foreign key (company_id) references companies(id),
 category enum('SPORT','CLOTHING','ELECTRICITY','CAMPING'), 
 title varchar(25), 
 `description` varchar(100), 
 start_date date, 
 end_date date,
 amount int, 
 price double, 
 image varchar(40)
);
create table `customers_vs_coupons`(
 customer_id int, 
 foreign key (customer_id) references customers(id),
 coupon_id int,
 foreign key (coupon_id) references coupons(id),
 primary key (customer_id,coupon_id)
);

-- values to test coupon expiration job
insert into companies values(5,'telma','telma@email','12345');
insert into coupons values(6, 5, 'CLOTHING', 'cool headphones', 'cool headphones', '2020-05-22', '2021-10-15',100,50,'image.jpg');