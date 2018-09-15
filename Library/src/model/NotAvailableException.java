package model;

public class NotAvailableException extends Exception {
	private static final long serialVersionUID = 1L;

	public NotAvailableException(String msg) {
		super(msg);
	}
}
