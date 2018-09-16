package model;

public class TooLongStringException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public TooLongStringException(String msg) {
		super(msg);
	}
}
