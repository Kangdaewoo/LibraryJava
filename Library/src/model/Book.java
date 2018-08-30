package model;

/**
 * Temporary object created each time a book is needed.
 * 
 * @author daewo
 *
 */
public class Book {
	private final int ID;
	private final String TITLE;
	private final String AUTHOR;
	
	// Number of books available for lent
	private int quantity;

	public Book(int id, String title, String author, int quantity) {
		ID = id;
		TITLE = title;
		AUTHOR = author;
		
		this.quantity = quantity;
	}

	public boolean isAvailable() {
		return quantity > 0;
	}

	public void borrow() {
		quantity -= 1;
	}

	/**
	 * The given string has a form: "ID, TITLE, AUTHOR, quantity".
	 * 
	 * @param bookInString
	 * @return
	 */
	public static Book stringToBook(String bookInString) {
		String[] strings = bookInString.split(", ");
		return new Book(Integer.parseInt(strings[0]), strings[1], strings[2], Integer.parseInt(strings[3]));
	}

	/**
	 * "ID, TITLE, AUTHOR, quantity".
	 */
	@Override
	public String toString() {
		return ID + ", " + TITLE + ", " + AUTHOR + ", " + quantity;
	}

	////////////////////////////////////////////////////////////
	// Getters.
	////////////////////////////////////////////////////////////

	public String getTitle() {
		return TITLE;
	}

	public int getID() {
		return ID;
	}

	public String getAuthor() {
		return AUTHOR;
	}

	public int getQuantity() {
		return quantity;
	}
}
