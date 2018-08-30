IF db_id('Library') IS NULL
	CREATE DATABASE Library
GO

USE Library;

CREATE TABLE Books (
	book_id INTEGER PRIMARY KEY,
	title CHAR(50) NOT NULL,
	author CHAR(50) NOT NULL,
	rating FLOAT(24) NOT NULL CHECK (rating >= 0 AND rating <=10),
	quantity INTEGER DEFAULT 0 CHECK (quantity >= 0),

	UNIQUE (title, author),
);

CREATE TABLE Customers (
	customer_id INTEGER PRIMARY KEY,
	first_name CHAR(50) NOT NULL,
	last_name CHAR(50) NOT NULL,

	UNIQUE (first_name, last_name)
);

CREATE TABLE Transactions (
	transaction_id INTEGER PRIMARY KEY,
	customer_id INTEGER FOREIGN KEY REFERENCES Customers(customer_id),
	book_id INTEGER FOREIGN KEY REFERENCES Books(book_id),

	UNIQUE (customer_id, book_id)
);


INSERT INTO Books (book_id, title, author, rating, quantity)
VALUES 
	(0, 'Title 0', 'Author 0', 1.0, 1),
	(1, 'Title 1', 'Author 1', 3.0, 1),
	(2, 'Title 2', 'Author 2', 2.0, 4),
	(3, 'Title 3', 'Author 3', 1.0, 7),
	(4, 'Title 4', 'Author 4', 5.0, 1),
	(5, 'Title 5', 'Author 5', 1.0, 5),
	(6, 'Title 6', 'Author 6', 2.0, 3),
	(7, 'Title 7', 'Author 7', 3.0, 2),
	(8, 'Title 8', 'Author 8', 2.0, 2),
	(9, 'Title 9', 'Author 9', 2.0, 3),
	(10, 'Title 10', 'Author 10', 1.0, 2);

INSERT INTO Customers (customer_id, first_name, last_name)
VALUES (0, 'Customer 0', 'Customer 0');