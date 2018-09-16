package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

/**
 * Class works with SQL database.
 * 
 * @author daewoo
 *
 */
public class Bridge {
	public static Bridge BRIDGE = null;

	private final int portNum = 1433;
	private Connection connection;
	private Statement statement;

	private Bridge() {
		String connectionUrl = "jdbc:sqlserver://localhost:" + portNum + ";integratedSecurity=true;database=Library";
		try {
			connection = DriverManager.getConnection(connectionUrl);
			System.out.println("Connected");
			statement = connection.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void createBridge() {
		if (BRIDGE == null) {
			BRIDGE = new Bridge();
		}
	}

	/**
	 * Returns a set of all books that contains the given title or author in
	 * their title or author respectively.
	 * 
	 * @param title
	 * @param author
	 * @return
	 */
	public Set<Book> search(String title, String author) {
		Set<Book> books = new HashSet<>();
		// SELECT TOP NUM_MAX_RECOMMENDATIONS b.book_id, title, author, quantity, CASE WHEN rating is NULL THEN 0 ELSE rating END rating
		// FROM 
		//	(SELECT * FROM Books
		//	WHERE title LIKE '%title%' OR author LIKE '%author%') b LEFT JOIN
		//	(SELECT r.book_id, AVG(rating) rating FROM Ratings r GROUP BY r.book_id) r
		//	ON b.book_id = r.book_id
		// ORDER BY rating DESC;
		String query = "SELECT TOP " + Constant.NUM_MAX_RECOMMENDATIONS + 
						" b.book_id, title, author, quantity, CASE WHEN rating IS NULL THEN 0 ELSE rating END rating " +
						"FROM (SELECT * FROM Books WHERE title LIKE '%" + title + "%' OR author LIKE '%" + author + 
						"%') b LEFT JOIN (SELECT r.book_id, AVG(rating) rating FROM Ratings r GROUP BY r.book_id) r " +
						"ON b.book_id = r.book_id ORDER BY rating DESC";
		try {
			ResultSet rs = statement.executeQuery(query);
			while (rs.next()) {
				books.add(new Book(rs.getInt("book_id"), rs.getString("title"), rs.getString("author"),
						rs.getInt("quantity"), rs.getInt("rating")));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return books;
	}
	
	/**
	 * If the book is not available return false, return true otherwise.
	 * @param customer
	 * @param book
	 */
	public void borrow(Customer customer, Book book) throws NotAvailableException {
		// UPDATE Books SET quantity=book.quantity-1 WHERE title='title' AND author='author';
		String query = "UPDATE Books SET quantity=" + (book.getQuantity() - 1) + " WHERE title='" + book.getTitle()
				+ "' AND author='" + book.getAuthor() + "'";
		try {
			statement.executeUpdate(query);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new NotAvailableException("Book is currently not available\n");
		}

		// INSERT INTO Transactions (transaction_id, customer_id, book_id)
		// VALUES ((SELECT 
		// 			CASE
		//				WHEN max(transaction_id) + 1 IS NULL 
		//					THEN 0
		//				ELSE max(transaction_id) + 1
		//			END
		//			FROM Transactions), 0, 2);
		query = "INSERT INTO Transactions (transaction_id, customer_id, book_id) " +
				"VALUES ((SELECT " +
							"CASE " +
								"WHEN max(transaction_id) + 1 IS NULL " +
									"THEN 0 " +
								"ELSE max(transaction_id) + 1 " +
							"END " +
						"FROM Transactions), " + customer.getID() + ", " + book.getID() + ")";
		try {
			statement.executeUpdate(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void returns(Customer customer, Book book) {
		// UPDATE Books SET quantity=book.quantity-1 WHERE title='title' AND author='author';
		String query = "UPDATE Books SET quantity=" + (book.getQuantity() + 1) + " WHERE title='" + book.getTitle()
				+ "' AND author='" + book.getAuthor() + "'";
		try {
			statement.executeUpdate(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		// DELETE FROM Transactions WHERE customer_id=customer.ID AND book_id=book.ID;
		query = "DELETE FROM Transactions WHERE customer_id=" + customer.getID() + " AND book_id=" + book.getID();
		try {
			statement.executeUpdate(query);
		} catch (SQLException e) {
			e.setNextException(e);
		}
		
		// INSERT INTO Expired_transactions (transaction_id, customer_id, book_id)
		// VALUES ((SELECT 
		// 				CASE
		//					WHEN max(transaction_id) + 1 IS NULL 
		//						THEN 0
		//					ELSE max(transaction_id) + 1
		//				END
		//			FROM Expired_transactions), customer.ID, book.ID);
		query = "INSERT INTO Expired_transactions (transaction_id, customer_id, book_id) " +
				"VALUES ((SELECT " +
							"CASE " +
								"WHEN max(transaction_id) + 1 IS NULL " +
									"THEN 0 " +
								"ELSE max(transaction_id) + 1 " +
							"END " +
						"FROM Expired_transactions), " + customer.getID() + ", " + book.getID() + ")";
		try {
			statement.executeUpdate(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public Customer getCustomer(String firstName, String lastName) {
		// SELECT * FROM Customers WHERE first_name=firstName AND last_name=lastName;
		String query = "SELECT * FROM Customers WHERE first_name='" + firstName + "' AND last_name='" + lastName + "'";
		Customer customer = null;
		try {
			ResultSet rs = statement.executeQuery(query);
			if (rs.next()) {
				customer = new Customer(rs.getInt("customer_id"), rs.getString("first_name"), rs.getString("last_name"));
			} else {
				return null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		// SELECT b2.title, b2.author, b2.quantity, b1.rating
		// FROM Books_with_ratings b1 JOIN Books b2 ON b1.book_id = b2.book_id
		// WHERE b1.book_id IN (SELECT book_id FROM Transactions WHERE customer_id=customer.ID);
		query = "SELECT b2.book_id, b2.title, b2.author, b2.quantity, b1.rating FROM Books_with_ratings b1 JOIN Books b2 ON b1.book_id = b2.book_id " +
				"WHERE b1.book_id IN (SELECT book_id FROM Transactions WHERE customer_id = " + customer.getID() + ")";
		try {
			ResultSet rs = statement.executeQuery(query);
			while (rs.next()) {
				customer.addBook(new Book(rs.getInt("book_id"), rs.getString("title"), rs.getString("author"), rs.getInt("quantity"), rs.getInt("rating")));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return customer;
	}

	public Customer createCustomer(String firstName, String lastName) {
		// INSERT INTO Customer (customer_id, first_name, last_name)
		// VALUES ((SELECT 
		// 				CASE
		//					WHEN max(customer_id) + 1 IS NULL 
		//						THEN 0
		//					ELSE max(customer_id) + 1
		//				END
		//			FROM Customer), firstName, lastName);
		String query = "INSERT INTO Customers (customer_id, first_name, last_name) " + 
						"VALUES ((SELECT CASE WHEN max(customer_id) + 1 IS NULL THEN 0 ELSE max(customer_id) + 1 END " + 
								"FROM Customers), '" + firstName + "', '" + lastName + "')";
		try {
			statement.executeUpdate(query);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		
		return getCustomer(firstName, lastName);
	}
	
	public boolean recordRating(Customer customer, Book book, int rating, String comment) {
		// INSERT INTO Ratings (rating_id, customer_id, book_id, comment, rating)
		// VALUES ((SELECT CASE WHEN MAX(rating_id) + 1 IS NULL THEN 0 ELSE MAX(rating_id) + 1 END FROM Ratings), 
		// customer.ID, book.ID, comment, rating);
		String query = "INSERT INTO Ratings (rating_id, customer_id, book_id, comment, rating) " + 
						"VALUES ((SELECT CASE WHEN MAX(rating_id) + 1 IS NULL THEN 0 ELSE MAX(rating_id) + 1 END FROM Ratings), " +
						customer.getID() + ", " + book.getID() + ", '" + comment + "', " + rating + ")";
		try {
			statement.executeUpdate(query);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
