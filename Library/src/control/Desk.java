package control;

import java.util.Set;

import model.Book;
import model.Bridge;
import model.Constant;
import model.Customer;
import model.NotAvailableException;
import view.Application;

public class Desk {
	private Customer customer;
	
	public Desk() {
		customer = null;
		Bridge.createBridge();
	}
	
	public Set<Book> getBorrowedBooks() {
		return customer.getBooks();
	}
	
	/**
	 * Returns a set of all books that include the given title or author
	 * in their title or author respectively.
	 * @param title
	 * @param author
	 * @return
	 */
	public Set<Book> search(String title, String author) {
		return Bridge.BRIDGE.search(title, author);
	}

	/**
	 * true when successful, false otherwise.
	 * @param book
	 * @return
	 */
	public boolean borrow(Book book) {
		if (book.isAvailable() && customer.canBorrow(book)) {
			try {
				Bridge.BRIDGE.borrow(customer, book);
				customer.borrow(book);
				book.borrow();
				return true;
			} catch (NotAvailableException e) {
				e.printStackTrace();
				return false;
			}
		}
		return false;
	}
	
	/**
	 * Assumption: The customer has borrowed the given book.
	 * @param book
	 */
	public void returns(Book book) {
		Bridge.BRIDGE.returns(customer, book);
		customer.returns(book);
	}
	
	/**
	 * true when successful, false otherwise.
	 * @param firstName
	 * @param lastName
	 * @return
	 */
	public boolean signIn(String firstName, String lastName) {
		customer = Bridge.BRIDGE.getCustomer(firstName, lastName);
		return customer != null;
	}
	
	public boolean signOut() {
		customer = null;
		return true;
	}
	
	/**
	 * true when successful, false otherwise.
	 * @param firstName
	 * @param lastName
	 * @return
	 */
	public boolean signUp(String firstName, String lastName) {
		if (firstName.isEmpty() || lastName.isEmpty()) {
			return false;
		}
		customer = Bridge.BRIDGE.createCustomer(firstName, lastName);
		return customer == null;
	}
	
	public boolean recordRating(Book book, int rating, String comment) {
		if (comment.length() > Constant.MAX_COMMENT_LENGTH) {
			return false;
		}
		return Bridge.BRIDGE.recordRating(customer, book, rating, comment);
	}
	
	public static void main(String[] args) {
		new Application(new Desk());
	}
}
