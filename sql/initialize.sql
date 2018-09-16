IF db_id('Library') IS NULL
	CREATE DATABASE Library
GO

USE Library;

CREATE TABLE Books (
	book_id INTEGER PRIMARY KEY,
	title CHAR(50) NOT NULL,
	author CHAR(50) NOT NULL,
	quantity INTEGER DEFAULT 0 CHECK (quantity >= 0),

	UNIQUE (title, author)
);

CREATE TABLE Customers (
	customer_id INTEGER PRIMARY KEY,
	first_name CHAR(50) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	last_name CHAR(50) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,

	UNIQUE (first_name, last_name)
);

CREATE TABLE Transactions (
	transaction_id INTEGER PRIMARY KEY,
	customer_id INTEGER FOREIGN KEY REFERENCES Customers(customer_id),
	book_id INTEGER FOREIGN KEY REFERENCES Books(book_id),

	UNIQUE (customer_id, book_id)
);

CREATE TABLE Expired_transactions (
	transaction_id INTEGER PRIMARY KEY,
	customer_id INTEGER FOREIGN KEY REFERENCES Customers(customer_id),
	book_id INTEGER FOREIGN KEY REFERENCES Books(book_id)
);

CREATE TABLE Ratings (
	rating_id INTEGER PRIMARY KEY,
	customer_id INTEGER FOREIGN KEY REFERENCES Customers(customer_id),
	book_id INTEGER FOREIGN KEY REFERENCES Books(book_id),
	comment CHAR(120) NOT NULL,
	rating INTEGER NOT NULL CHECK (rating >= 0 AND rating <=10),

	UNIQUE (customer_id, book_id)
);

GO

CREATE VIEW Books_with_ratings AS
SELECT b1.book_id, CASE WHEN AVG(rating) IS NULL THEN 0 ELSE AVG(rating) END rating
FROM Books b1 LEFT JOIN Ratings r1 ON b1.book_id = r1.book_id
GROUP BY b1.book_id;

GO

INSERT INTO Books (book_id, title, author, quantity)
VALUES 
	(0, 'Title 0', 'Author 0', 1),
	(1, 'Title 1', 'Author 1', 1),
	(2, 'Title 2', 'Author 2', 4),
	(3, 'Title 3', 'Author 3', 7),
	(4, 'Title 4', 'Author 4', 1),
	(5, 'Title 5', 'Author 5', 5),
	(6, 'Title 6', 'Author 6', 3),
	(7, 'Title 7', 'Author 7', 2),
	(8, 'Title 8', 'Author 8', 2),
	(9, 'Title 9', 'Author 9', 3),
	(10, 'Title 10', 'Author 10', 2);

INSERT INTO Customers (customer_id, first_name, last_name)
VALUES (0, 'Customer 0', 'Customer 0');