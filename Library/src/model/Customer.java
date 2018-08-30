package model;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class Customer {
	private final int ID;
	private final String FIRST_NAME;
	private final String LAST_NAME;

	// Books this customer has borrowed
	private Set<Book> books;

	public Customer(int id, String firstName, String lastName) {
		ID = id;
		FIRST_NAME = firstName;
		LAST_NAME = lastName;

		books = new HashSet<>();
	}
	
	public void addBook(Book book) {
		books.add(book);
	}

	public boolean borrow(Book book) {
		if (books.size() >= Constant.NUM_BOOK_LIMIT)
			return false;

		books.add(book);
		return true;
	}

	public void returns(Book book) {
		Iterator<Book> iterator = books.iterator();
		while (iterator.hasNext()) {
			Book b = iterator.next();
			if (b.getID() == book.getID()) {
				books.remove(b);
				return;
			}
		}
	}

	////////////////////////////////////////////////////////////
	// Getters.
	////////////////////////////////////////////////////////////

	public String getFirstName() {
		return FIRST_NAME;
	}
	
	public int getID() {
		return ID;
	}

	public String getLastName() {
		return LAST_NAME;
	}

	public Set<Book> getBooks() {
		return books;
	}
}
