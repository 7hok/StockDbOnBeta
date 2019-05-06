# StockManagementFile
sql query process is in **Maniputor **
2 tables have to create
products and tb_statements
CREATE TABLE products(
 id serial PRIMARY KEY,
 name VARCHAR (50) ,
 unitPrice float8,
 stockQty INT,
 importedDate VARCHAR (50),
 status INT 
);
CREATE TABLE tb_statements(
 id serial PRIMARY KEY,
 statement VARCHAR (50)
);
