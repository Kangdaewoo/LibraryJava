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

	private Connection connection;
	private Statement statement;

	private Bridge() {
		String connectionUrl = "jdbc:sqlserver://localhost:1433;integratedSecurity=true;database=Library";
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
	 * Returns a set of all books that contans the given title or author in
	 * their title or author respectively.
	 * 
	 * @param title
	 * @param author
	 * @return
	 */
	public Set<Book> search(String title, String author) {
		Set<Book> books = new HashSet<>();
		// SELECT * FROM Books WHERE title LIKE '%Title%' OR author LIKE '%Author%';
		String query = "SELECT * FROM Books WHERE title LIKE '%" + title + "%' OR author LIKE '%" + author + "%'";
		try {
			ResultSet rs = statement.executeQuery(query);
			int breaker = 0;
			// Only up to NUM_MAX_RECOMMENDATIONS number of books.
			while (rs.next() && breaker < Constant.NUM_MAX_RECOMMENDATIONS) {
				books.add(new Book(rs.getInt("book_id"), rs.getString("title"), rs.getString("author"),
						rs.getInt("quantity")));
				breaker++;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return books;
	}

	public boolean canBorrow(Customer customer, Book book) {
		// SELECT * FROM Transactions WHERE customer_id=customer.ID AND book_id=book.ID;
		String query = "SELECT * FROM Transactions WHERE customer_id=" + customer.getID() + " AND book_id="
				+ book.getID();
		try {
			ResultSet rs = statement.executeQuery(query);
			if (rs.next()) {
				return false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return true;
	}

	/**
	 * Assumption: canBorrow method in Bridge has output true for the given
	 * customer and book.
	 * @param customer
	 * @param book
	 */
	public void borrow(Customer customer, Book book) {
		// UPDATE Books SET quantity=book.quantity-1 WHERE title='title' AND author='author';
		String query = "UPDATE Books SET quantity=" + (book.getQuantity() - 1) + " WHERE title='" + book.getTitle()
				+ "' AND author='" + book.getAuthor() + "'";
		try {
			statement.executeUpdate(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		// SELECT max(transaction_id) AS max_transaction_id FROM transactions;
		int transaction_id = 0;
		query = "SELECT max(transaction_id) AS max_transaction_id FROM Transactions";
		try {
			ResultSet rs = statement.executeQuery(query);
			if (rs.next()) {
				transaction_id = rs.getInt("max_transaction_id") + 1;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		// INSERT INTO Transactions (transaction_id, customer_id, book_id)
		// VALUES (transaction_id, customer.ID, book.ID);
		query = "INSERT INTO Transactions (transaction_id, customer_id, book_id) VALUES (" + transaction_id + ", "
				+ customer.getID() + ", " + book.getID() + ")";
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
		
		// SELECT * FROM Books WHERE book_id IN (SELECT book_id FROM Transactions WHERE customer_id=customer.ID);
		query = "SELECT * FROM Books WHERE book_id IN (SELECT book_id FROM Transactions WHERE customer_id=" + customer.getID() + ")";
		try {
			ResultSet rs = statement.executeQuery(query);
			while (rs.next()) {
				customer.addBook(new Book(rs.getInt("book_id"), rs.getString("title"), rs.getString("author"), rs.getInt("quantity")));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return customer;
	}

	public Customer createCustomer(String firstName, String lastName) {
		// SELECT max(customer_id) FROM Customers;
		String query = "SELECT max(customer_id) as max_customer_id FROM Customers";
		int customer_id = 0;
		try {
			ResultSet rs = statement.executeQuery(query);
			if (rs.next()) {
				customer_id = rs.getInt("max_customer_id") + 1;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		// SELECT customer_id FROM Customers WHERE first_name=firstName AND last_name=lastName;
		query = "SELECT customer_id FROM Customers WHERE first_name='" + firstName + "' AND last_name='" + lastName
				+ "'";
		Customer customer = null;
		try {
			ResultSet rs = statement.executeQuery(query);
			if (!rs.next()) {
				customer = new Customer(customer_id, firstName, lastName);
			} else {
				return customer;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		// INSERT INTO Customers (customer_id, first_name, last_name)
		// VALUES (customer_id+1, firstName, lastName);
		query = "INSERT INTO Customers (customer_id, first_name, last_name) VALUES (" + customer_id + ", '" + firstName
				+ "', '" + lastName + "')";
		try {
			statement.executeUpdate(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return customer;
	}
}
