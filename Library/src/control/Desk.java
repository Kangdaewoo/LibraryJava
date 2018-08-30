package control;

import java.util.Set;

import model.Book;
import model.Bridge;
import model.Customer;
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
		if (book.isAvailable() && Bridge.BRIDGE.canBorrow(customer, book) && customer.borrow(book)) {
			Bridge.BRIDGE.borrow(customer, book);
			book.borrow();
			return true;
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
	
	public static void main(String[] args) {
		Application app = new Application(new Desk());
	}
}