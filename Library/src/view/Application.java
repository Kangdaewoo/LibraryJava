package view;

import java.awt.Dimension;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowEvent;
import java.util.Iterator;
import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;

import control.Desk;
import model.Book;
import model.Constant;

public class Application {
	private JFrame frame;

	// Search list: result of a search
	private DefaultListModel<String> searchedBooks;
	// Borrowed list: borrowed books
	private DefaultListModel<String> borrowedBooks;

	private Desk desk;
	
	private Book toRate;

	public Application(Desk desk) {
		this.desk = desk;
		searchedBooks = new DefaultListModel<>();
		borrowedBooks = new DefaultListModel<>();

		frame = new JFrame();
		frame.setSize(new Dimension(500, 500));
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Initial page
		setSignInPage();

		frame.setVisible(true);
	}

	private void setSignInPage() {
		frame.getContentPane().removeAll();
		frame.setLayout(null);
		frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));

		HintTextField firstNameField = new HintTextField("Enter first name");
		HintTextField lastNameField = new HintTextField("Enter last name");
		frame.add(firstNameField);
		frame.add(lastNameField);

		JButton logInButton = new JButton("Log in");
		// Sign in
		logInButton.addActionListener(e -> {
			if (firstNameField.getText().equals(firstNameField.getHint()) || lastNameField.getText().equals(lastNameField.getHint())) {
				JOptionPane.showMessageDialog(null, "Enter first and last name!");
			} else if (desk.signIn(firstNameField.getText(), lastNameField.getText())) {
				JOptionPane.showMessageDialog(null, "Welcome!");
				setMainPage();
			} else {
				JOptionPane.showMessageDialog(null, "Wrong log in information!");
			}
		});
		frame.add(logInButton);

		JButton signUpButton = new JButton("Sign up");
		// Sign up
		signUpButton.addActionListener(e -> {
			if (firstNameField.getText().equals(firstNameField.getHint()) || lastNameField.getText().equals(lastNameField.getHint())) {
				JOptionPane.showMessageDialog(null, "Enter first and last name!");
			}
			else if (!desk.signUp(firstNameField.getText(), lastNameField.getText())) {
				JOptionPane.showMessageDialog(null, "Welcome!");
				setMainPage();
			} else {
				JOptionPane.showMessageDialog(null, "Already exists!");
			}
		});
		frame.add(signUpButton);
		
		JButton exitButton = new JButton("Exit");
		// Exit the app
		exitButton.addActionListener(e -> {
			frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
		});
		frame.add(exitButton);
		
		frame.revalidate();
		frame.repaint();
	}

	private void setMainPage() {
		frame.getContentPane().removeAll();
		frame.setLayout(null);
		frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));

		HintTextField titleField = new HintTextField("Enter title");
		HintTextField authorField = new HintTextField("Enter author");
		frame.add(titleField);
		frame.add(authorField);

		// Populate search list with some books
		populateSearchList(desk.search("", ""));
		JList<String> searchList = new JList<>(searchedBooks);
		JScrollPane scrollPaneSearch = new JScrollPane(searchList);
		scrollPaneSearch.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		frame.add(scrollPaneSearch);
		
		JButton searchButton = new JButton("Search");
		// Search for books
		searchButton.addActionListener(e -> {
			searchedBooks.removeAllElements();
			populateSearchList(desk.search(titleField.getText(), authorField.getText()));
		});
		frame.add(searchButton);
		
		JButton borrowButton = new JButton("borrow");
		// Borrow a book
		borrowButton.addActionListener(e -> {
			String selected = searchList.getSelectedValue();
			if (selected == null) {
				JOptionPane.showMessageDialog(null, "Pick a book");
				return;
			}
			
			Book book = Book.stringToBook(selected);
			if (desk.borrow(book)) {
				JOptionPane.showMessageDialog(null, "Borrowed!");
				setMainPage();
			} else {
				JOptionPane.showMessageDialog(null, "Not available or borrowed already!");
			}
		});
		frame.add(borrowButton);

		// Populate borrowed list
		populateBorrowedList(desk.getBorrowedBooks());
		JList<String> borrowedList = new JList<>(borrowedBooks);
		JScrollPane scrollPaneBorrow = new JScrollPane(borrowedList);
		scrollPaneBorrow.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		frame.add(scrollPaneBorrow);
		
		JButton returnButton = new JButton("Return");
		// Return a book
		returnButton.addActionListener(e -> {
			String selected = borrowedList.getSelectedValue();
			if (selected == null) {
				JOptionPane.showMessageDialog(null, "Pick a book");
				return;
			}
			
			Book book = Book.stringToBook(selected);
			desk.returns(book);
			
			// Refresh borrowed list
			populateBorrowedList(desk.getBorrowedBooks());
			
			JOptionPane.showMessageDialog(null, "Returned!");
			
			int reply = JOptionPane.showConfirmDialog(null, "Would you rate this book?");
			if (reply == JOptionPane.YES_OPTION) {
				toRate = book;
				setRatePage();
			} else {
				setMainPage();
			}
		});
		frame.add(returnButton);

		JButton signOutButton = new JButton("Sign out");
		// Sign out which directs to the initial page
		signOutButton.addActionListener(e -> {
			desk.signOut();
			JOptionPane.showMessageDialog(null, "Good bye!");
			
			setSignInPage();
		});
		frame.add(signOutButton);

		frame.revalidate();
		frame.repaint();
	}
	
	private void setRatePage() {
		frame.getContentPane().removeAll();
		frame.setLayout(null);
		frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
		
		JLabel bookLabel = new JLabel(toRate.toString());
		frame.add(bookLabel);
		
		JSlider ratingSlider = new JSlider(JSlider.HORIZONTAL, Constant.MIN_RATE, Constant.MAX_RATE, Constant.MAX_RATE);
		JLabel sliderLabel = new JLabel("" + Constant.MAX_RATE);
		ratingSlider.addChangeListener(e -> {
			sliderLabel.setText("" + ratingSlider.getValue());
		});
		frame.add(ratingSlider);
		frame.add(sliderLabel);
		
		HintTextField commentField = new HintTextField("Your thoughts on this book? (Less than 120 characters.)");
		frame.add(commentField);
		
		JButton recordRating = new JButton("Record");
		recordRating.addActionListener(e -> {
			String comment = commentField.getText();
			if (comment.length() > Constant.MAX_COMMENT_LENGTH) {
				JOptionPane.showMessageDialog(null, "Comment must be less than 120 characters!");
				return;
			}
			
			if (desk.recordRating(toRate, ratingSlider.getValue(), comment)) {
				JOptionPane.showMessageDialog(null, "Successfully recorded!");
			} else {
				JOptionPane.showMessageDialog(null, "You've already rated this book!");
			}
			setMainPage();
		});
		frame.add(recordRating);
		
		frame.revalidate();
		frame.repaint();
	}
	
	private void populateSearchList(Set<Book> books) {
		searchedBooks.removeAllElements();
		Iterator<Book> iterator = books.iterator();
		while (iterator.hasNext()) {
			Book book = iterator.next();
			searchedBooks.addElement(book.toString());
		}
	}
	
	private void populateBorrowedList(Set<Book> books) {
		borrowedBooks.removeAllElements();
		Iterator<Book> iterator = books.iterator();
		while (iterator.hasNext()) {
			Book book = iterator.next();
			borrowedBooks.addElement(book.toString());
		}
	}
	
	public class HintTextField extends JTextField {
		private static final long serialVersionUID = 1L;
		
		private final String hint;
		
		private HintTextField(String hint) {
			this.hint= hint;
			setText(hint);
			this.addFocusListener(new FocusListener() {

				@Override
				public void focusGained(FocusEvent e) {
					if (getText().equals(hint)) {
						setText("");
					}
				}

				@Override
				public void focusLost(FocusEvent e) {
					if (getText().isEmpty()) {
						setText(hint);
					}
				}
			});
		}
		
		public String getHint() {
			return hint;
		}
	}
}
