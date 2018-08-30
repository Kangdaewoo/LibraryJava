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
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;

import control.Desk;
import model.Book;

public class Application {
	private JFrame frame;

	// Search list: result of a search
	private DefaultListModel<String> searchedBooks;
	// Borrowed list: borrowed books
	private DefaultListModel<String> borrowedBooks;

	private Desk desk;

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
				JOptionPane.showMessageDialog(null, "Not available!");
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
			
			setMainPage();
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
