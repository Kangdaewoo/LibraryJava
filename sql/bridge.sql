-- search(title, author) method --
-- SELECT * FROM Books WHERE title LIKE '%Title%' OR author LIKE '%Author%';

-- canBorrow(customer, book) method --
-- SELECT * FROM Transactions WHERE customer_id=customer.ID AND book_id=book.ID;

-- borrow(book) method --
-- UPDATE Books SET quantity=book.quantity-1 WHERE title='title' AND author='author';
-- SELECT max(transaction_id) AS max_transaction_id FROM transactions;
-- INSERT INTO Transactions (transaction_id, customer_id, book_id) VALUES (transaction_id, customer.ID, book.ID);

-- returns(book) method --
-- UPDATE Books SET quantity=book.quantity-1 WHERE title='title' AND author='author';
-- DELETE FROM Transactions WHERE customer_id=customer.ID AND book_id=book.ID;

-- getCustomer(firstName, lastName) method --
-- SELECT * FROM Customers WHERE first_name=firstName AND last_name=lastName;
-- SELECT * FROM Books WHERE book_id IN (SELECT book_id FROM Transactions WHERE customer_id=customer.ID);

-- createCustomer(firstName, lastName) method --
-- SELECT max(customer_id) FROM Customers;
-- SELECT customer_id FROM Customers WHERE first_name=firstName AND last_name=lastName;
-- INSERT INTO Customers (customer_id, first_name, last_name) VALUES (customer_id+1, firstName, lastName);