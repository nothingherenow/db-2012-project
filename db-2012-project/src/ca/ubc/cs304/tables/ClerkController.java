package ca.ubc.cs304.tables;

//File: ClerkController.java

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*;

import ca.ubc.cs304.main.CustomTable;
import ca.ubc.cs304.main.CustomTableModel;
import ca.ubc.cs304.main.ExceptionEvent;
import ca.ubc.cs304.main.ExceptionListener;
import ca.ubc.cs304.main.MvbView;

import java.math.BigDecimal;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class ClerkController implements ActionListener, ExceptionListener {

	private MvbView mvb;
	private ClerkTransactions clerk = null;
	private Integer rid = null;
	private BigDecimal cost = null;
	private PurchaseItemModel item = null;

	// constants used for describing the outcome of an operation
	public static final int OPERATIONSUCCESS = 0;
	public static final int OPERATIONFAILED = 1;
	public static final int VALIDATIONERROR = 2;

	public ClerkController(MvbView mvb) {
		this.mvb = mvb;
		clerk = new ClerkTransactions();
		item = new PurchaseItemModel();

		// register to receive exception events from customer
		clerk.addExceptionListener(this);
		item.addExceptionListener(this);
	}

	/*
	 * This event handler gets called when the user makes a menu item selection.
	 */
	public void actionPerformed(ActionEvent e) {
		String actionCommand = e.getActionCommand();

		if (actionCommand.equals("Checkout Store")) {
			CheckoutStoreDialog iDialog = new CheckoutStoreDialog(mvb);
			iDialog.pack();
			mvb.centerWindow(iDialog);
			iDialog.setVisible(true);
			return;
		}

		if (actionCommand.equals("Return")) {
			ReturnDialog iDialog = new ReturnDialog(mvb);
			iDialog.pack();
			mvb.centerWindow(iDialog);
			iDialog.setVisible(true);
			return;
		}

	}

	/*
	 * This event handler gets called when an exception event is generated. It
	 * displays the exception message on the status text area of the main GUI.
	 */
	public void exceptionGenerated(ExceptionEvent ex) {
		String message = ex.getMessage();

		// annoying beep sound
		Toolkit.getDefaultToolkit().beep();

		if (message != null) {
			mvb.updateStatusBar(ex.getMessage());
		} else {
			mvb.updateStatusBar("An exception occurred!");
		}

	}

	/*
	 * This method displays the customer's shopping cart in a non-editable
	 * JTable
	 */
	private void showAdded(Integer iupc) {
		ResultSet rs = clerk.showItem(iupc);
		// show all the items lined up to be purchased

		// CustomTableModel maintains the result set's data, e.g., if
		// the result set is updatable, it will update the database
		// when the table's data is modified.
		CustomTableModel model = new CustomTableModel(clerk.getConnection(), rs);
		CustomTable data = new CustomTable(model);

		// register to be notified of any exceptions that occur in the model and
		// table
		model.addExceptionListener(this);
		data.addExceptionListener(this);

		// Adds the table to the scrollpane.
		// By default, a JTable does not have scroll bars.
		mvb.addTable(data);

	}

	/*
	 * This method displays the customer's Checked out items in a non-editable
	 * JTable
	 */
	private void showItems() {
		ResultSet rs = clerk.receiptItems(rid);

		// CustomTableModel maintains the result set's data, e.g., if
		// the result set is updatable, it will update the database
		// when the table's data is modified.
		CustomTableModel model = new CustomTableModel(clerk.getConnection(), rs);
		CustomTable data = new CustomTable(model);

		// register to be notified of any exceptions that occur in the model and
		// table
		model.addExceptionListener(this);
		data.addExceptionListener(this);

		// Adds the table to the scrollpane.
		// By default, a JTable does not have scroll bars.
		mvb.addTable(data);
	}

	// This class creates a dialog box for Checking out items in store.

	class CheckoutStoreDialog extends JDialog implements ActionListener {

		private JTextField upc = new JTextField(12);
		private JTextField quant = new JTextField(12);

		/*
		 * Constructor. Creates the dialog's GUI.
		 */
		public CheckoutStoreDialog(JFrame parent) {
			super(parent, "Checkout Items", true);
			setResizable(false);

			JPanel contentPane = new JPanel(new BorderLayout());
			setContentPane(contentPane);
			contentPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10,
					10));

			// this panel will contain the text field labels and the text
			// fields.
			JPanel inputPane = new JPanel();
			inputPane.setBorder(BorderFactory.createCompoundBorder(
					new TitledBorder(new EtchedBorder(), "Checkout fields"),
					new EmptyBorder(5, 5, 5, 5)));

			// add the text field labels and text fields to inputPane
			// using the GridBag layout manager

			GridBagLayout gb = new GridBagLayout();
			GridBagConstraints c = new GridBagConstraints();
			inputPane.setLayout(gb);

			// create and place upc label
			JLabel label = new JLabel("UPC: ", SwingConstants.RIGHT);
			c.gridwidth = GridBagConstraints.RELATIVE;
			c.insets = new Insets(5, 0, 0, 5);
			c.anchor = GridBagConstraints.EAST;
			gb.setConstraints(label, c);
			inputPane.add(label);

			// place upc field
			c.gridwidth = GridBagConstraints.REMAINDER;
			c.insets = new Insets(5, 0, 0, 0);
			c.anchor = GridBagConstraints.WEST;
			gb.setConstraints(upc, c);
			inputPane.add(upc);

			// create and place quantity label
			label = new JLabel("Quantity: ", SwingConstants.RIGHT);
			c.gridwidth = GridBagConstraints.RELATIVE;
			c.insets = new Insets(5, 0, 0, 5);
			c.anchor = GridBagConstraints.EAST;
			gb.setConstraints(label, c);
			inputPane.add(label);

			// place quantity field
			c.gridwidth = GridBagConstraints.REMAINDER;
			c.insets = new Insets(5, 0, 0, 0);
			c.anchor = GridBagConstraints.WEST;
			gb.setConstraints(quant, c);
			inputPane.add(quant);

			// panel for the PURCHASE, ADD and Cancel buttons
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.X_AXIS));
			buttonPane.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 2));

			JButton PurchButton = new JButton("PURCHASE");
			JButton cancelButton = new JButton("Cancel");
			JButton ADDButton = new JButton("ADD");
			PurchButton.addActionListener(this);
			PurchButton.setActionCommand("PURCHASE");
			ADDButton.addActionListener(this);
			ADDButton.setActionCommand("ADD");
			cancelButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					dispose();
				}
			});

			// add the buttons to buttonPane
			buttonPane.add(Box.createHorizontalGlue());
			buttonPane.add(ADDButton);
			buttonPane.add(PurchButton);
			buttonPane.add(Box.createRigidArea(new Dimension(10, 0)));
			buttonPane.add(cancelButton);

			contentPane.add(inputPane, BorderLayout.CENTER);
			contentPane.add(buttonPane, BorderLayout.SOUTH);

			addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					dispose();
				}
			});
		}

		/*
		 * Event handler for the PURCHASE and ADD buttons in CheckoutStoreDialog
		 */
		public void actionPerformed(ActionEvent e) {
			String actionCommand = e.getActionCommand();

			if (actionCommand.equals("PURCHASE")) {
				dispose(); // throw away add window
				PaymentDialog iDialog = new PaymentDialog(mvb);
				iDialog.pack();
				mvb.centerWindow(iDialog);
				iDialog.setVisible(true);
				return;
			}

			if (actionCommand.equals("ADD")) {
				if (validateAdd() != VALIDATIONERROR) {
					// for now the window stays but the fields remain
					// May add code later to open a new window
				} else {
					Toolkit.getDefaultToolkit().beep();

					// display a popup to inform the user of the validation
					// error
					JOptionPane errorPopup = new JOptionPane();
					errorPopup.showMessageDialog(this, "Invalid Input",
							"Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		}

		/*
		 * Validates the text fields in CheckoutStoreDialog and then adds the
		 * items to the bill if the fields are valid. Returns the operation
		 * status, which is one of OPERATIONSUCCESS, OPERATIONFAILED,
		 * VALIDATIONERROR.
		 */
		private int validateAdd() {
			try {
				Integer iupc;
				Integer iquant;

				//if (rid == null) // if no rid for purchase make a new one
					rid = clerk.instorePurchase();

				// Disallow blank searches
				if (upc.getText().trim().length() != 0) {
					iupc = Integer.valueOf(upc.getText().trim());
				} else {
					return VALIDATIONERROR;
				}

				// Default adds 1 item per upc
				if (quant.getText().trim().length() != 0) {
					iquant = Integer.valueOf(quant.getText().trim());
				} else {
					iquant = 1;
				}

				mvb.updateStatusBar("Adding Item to Bill...");
				mvb.updateStatusBar("generated rid: " + rid.toString());

				if (rid == 0) {
					mvb.updateStatusBar("Unable to generate receipt id");
					return OPERATIONFAILED;
				}

				
				else {
					if(item.insertPurchaseItem(rid, iupc, iquant) != true){
						mvb.updateStatusBar("Unable to insert item");
						return OPERATIONFAILED;
					}
				}

				mvb.updateStatusBar("Add to bill complete.");

				// Shows the entered items in a table

				showAdded(iupc); 
				
				return OPERATIONSUCCESS;

			} catch (NumberFormatException ex) {
				// this exception is thrown when a string
				// cannot be converted to a number
				return VALIDATIONERROR;
			}
		}

	}

	/*
	 * This class creates a dialog box for Returning items in store.
	 */
	class ReturnDialog extends JDialog implements ActionListener {

		private JTextField rid = new JTextField(12);
		private JTextField upc = new JTextField(12);
		private JTextField quant = new JTextField(12);

		/*
		 * Constructor. Creates the dialog's GUI.
		 */
		public ReturnDialog(JFrame parent) {
			super(parent, "Process Return", true);
			setResizable(false);

			JPanel contentPane = new JPanel(new BorderLayout());
			setContentPane(contentPane);
			contentPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10,
					10));

			// this panel will contain the text field labels and the text
			// fields.
			JPanel inputPane = new JPanel();
			inputPane.setBorder(BorderFactory.createCompoundBorder(
					new TitledBorder(new EtchedBorder(), "Return fields"),
					new EmptyBorder(5, 5, 5, 5)));

			// add the text field labels and text fields to inputPane
			// using the GridBag layout manager

			GridBagLayout gb = new GridBagLayout();
			GridBagConstraints c = new GridBagConstraints();
			inputPane.setLayout(gb);

			// create and place receipt id label
			JLabel label = new JLabel("Receipt ID: ", SwingConstants.RIGHT);
			c.gridwidth = GridBagConstraints.RELATIVE;
			c.insets = new Insets(5, 0, 0, 5);
			c.anchor = GridBagConstraints.EAST;
			gb.setConstraints(label, c);
			inputPane.add(label);

			// place receipt id field
			c.gridwidth = GridBagConstraints.REMAINDER;
			c.insets = new Insets(5, 0, 0, 0);
			c.anchor = GridBagConstraints.WEST;
			gb.setConstraints(rid, c);
			inputPane.add(rid);

			// create and place upc label
			label = new JLabel("UPC: ", SwingConstants.RIGHT);
			c.gridwidth = GridBagConstraints.RELATIVE;
			c.insets = new Insets(5, 0, 0, 5);
			c.anchor = GridBagConstraints.EAST;
			gb.setConstraints(label, c);
			inputPane.add(label);

			// place upc field
			c.gridwidth = GridBagConstraints.REMAINDER;
			c.insets = new Insets(5, 0, 0, 0);
			c.anchor = GridBagConstraints.WEST;
			gb.setConstraints(upc, c);
			inputPane.add(upc);

			// create and place quantity label
			label = new JLabel("Quantity: ", SwingConstants.RIGHT);
			c.gridwidth = GridBagConstraints.RELATIVE;
			c.insets = new Insets(5, 0, 0, 5);
			c.anchor = GridBagConstraints.EAST;
			gb.setConstraints(label, c);
			inputPane.add(label);

			// place quantity field
			c.gridwidth = GridBagConstraints.REMAINDER;
			c.insets = new Insets(5, 0, 0, 0);
			c.anchor = GridBagConstraints.WEST;
			gb.setConstraints(quant, c);
			inputPane.add(quant);

			// when the return key is pressed in the last field
			// of this form, the action performed by the ok button
			// is executed
			quant.addActionListener(this);
			quant.setActionCommand("OK");

			// panel for the OK and cancel buttons
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.X_AXIS));
			buttonPane.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 2));

			JButton OKButton = new JButton("OK");
			JButton cancelButton = new JButton("Cancel");
			OKButton.addActionListener(this);
			OKButton.setActionCommand("OK");
			cancelButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					dispose();
				}
			});

			// add the buttons to buttonPane
			buttonPane.add(Box.createHorizontalGlue());
			buttonPane.add(OKButton);
			buttonPane.add(Box.createRigidArea(new Dimension(10, 0)));
			buttonPane.add(cancelButton);

			contentPane.add(inputPane, BorderLayout.CENTER);
			contentPane.add(buttonPane, BorderLayout.SOUTH);

			addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					dispose();
				}
			});
		}

		/*
		 * Event handler for the OK button in ReturnDialog
		 */
		public void actionPerformed(ActionEvent e) {
			String actionCommand = e.getActionCommand();

			if (actionCommand.equals("OK")) {
				if (validateRid() != VALIDATIONERROR) {
					dispose();
				} else {
					Toolkit.getDefaultToolkit().beep();

					// display a popup to inform the user of the validation
					// error
					JOptionPane errorPopup = new JOptionPane();
					errorPopup.showMessageDialog(this, "Invalid Input",
							"Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		}

		/*
		 * Validates the text fields in ReturnDialog and then processes the
		 * return for items if the fields are valid. Returns the operation
		 * status, which is one of OPERATIONSUCCESS, OPERATIONFAILED,
		 * VALIDATIONERROR.
		 */
		private int validateRid() {
			try {
				Integer receiptId;
				Integer rupc;
				Integer quantity;

				// Disallow blank searches
				if (rid.getText().trim().length() != 0) {
					receiptId = Integer.valueOf(rid.getText().trim());
				} else {
					return VALIDATIONERROR;
				}

				if (upc.getText().trim().length() != 0) {
					rupc = Integer.valueOf(upc.getText().trim());
				} else {
					return VALIDATIONERROR;
				}

				if (quant.getText().trim().length() != 0) {
					quantity = Integer.valueOf(quant.getText().trim());
				} else {
					return VALIDATIONERROR;
				}

				mvb.updateStatusBar("Validating Receipt ID...");

				if (clerk.checkReturn(receiptId) != true) {
					mvb.updateStatusBar("Invalid Receipt ID");
					return OPERATIONFAILED;
				}

				mvb.updateStatusBar("Validation complete.");

				if (clerk.processReturn(receiptId, rupc, quantity) == 0) {
					mvb.updateStatusBar("Database error");
					return OPERATIONFAILED;
				}
				mvb.updateStatusBar("Refund Complete");
				return OPERATIONSUCCESS;

			} catch (NumberFormatException ex) {
				// this exception is thrown when a string
				// cannot be converted to a number
				return VALIDATIONERROR;
			}
		}
	}

	/*
	 * This class creates a dialog box for Payment of items in store.
	 */
	class PaymentDialog extends JDialog implements ActionListener {

		private JTextField cardNo = new JTextField(16);
		private JTextField cardExp = new JTextField(5);

		/*
		 * Constructor. Creates the dialog's GUI.
		 */
		public PaymentDialog(JFrame parent) {
			super(parent, "Enter Credit Card Details or Pay Cash", true);
			setResizable(false);

			JPanel contentPane = new JPanel(new BorderLayout());
			setContentPane(contentPane);
			contentPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10,
					10));

			// this panel will contain the text field labels and the text
			// fields.
			JPanel inputPane = new JPanel();
			inputPane.setBorder(BorderFactory.createCompoundBorder(
					new TitledBorder(new EtchedBorder(), "Credit Card fields"),
					new EmptyBorder(5, 5, 5, 5)));

			// add the text field labels and text fields to inputPane
			// using the GridBag layout manager

			GridBagLayout gb = new GridBagLayout();
			GridBagConstraints c = new GridBagConstraints();
			inputPane.setLayout(gb);

			// create and place card number label
			JLabel label = new JLabel("Card number: ", SwingConstants.RIGHT);
			c.gridwidth = GridBagConstraints.RELATIVE;
			c.insets = new Insets(5, 0, 0, 5);
			c.anchor = GridBagConstraints.EAST;
			gb.setConstraints(label, c);
			inputPane.add(label);

			// place card number field
			c.gridwidth = GridBagConstraints.REMAINDER;
			c.insets = new Insets(5, 0, 0, 0);
			c.anchor = GridBagConstraints.WEST;
			gb.setConstraints(cardNo, c);
			inputPane.add(cardNo);

			// create and place card expiry label
			label = new JLabel("Card expiry (MM/yy): ", SwingConstants.RIGHT);
			c.gridwidth = GridBagConstraints.RELATIVE;
			c.insets = new Insets(5, 0, 0, 5);
			c.anchor = GridBagConstraints.EAST;
			gb.setConstraints(label, c);
			inputPane.add(label);

			// place card number field
			c.gridwidth = GridBagConstraints.REMAINDER;
			c.insets = new Insets(5, 0, 0, 0);
			c.anchor = GridBagConstraints.WEST;
			gb.setConstraints(cardExp, c);
			inputPane.add(cardExp);

			// when the return key is pressed in the last field
			// of this form, the action performed by the credit button
			// is executed
			cardExp.addActionListener(this);
			cardExp.setActionCommand("CREDIT");

			// panel for the CASH, CREDIT and Cancel buttons
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.X_AXIS));
			buttonPane.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 2));

			JButton CASHButton = new JButton("CASH");
			JButton CREDITButton = new JButton("CREDIT");
			JButton cancelButton = new JButton("Cancel");

			CASHButton.addActionListener(this);
			CASHButton.setActionCommand("CASH");
			CREDITButton.addActionListener(this);
			CREDITButton.setActionCommand("CREDIT");
			cancelButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					dispose();
				}
			});

			// add the buttons to buttonPane
			buttonPane.add(Box.createHorizontalGlue());
			buttonPane.add(CASHButton);
			buttonPane.add(CREDITButton);
			buttonPane.add(Box.createRigidArea(new Dimension(10, 0)));
			buttonPane.add(cancelButton);

			contentPane.add(inputPane, BorderLayout.CENTER);
			contentPane.add(buttonPane, BorderLayout.SOUTH);

			addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					dispose();
				}
			});

		}

		/*
		 * Event handler for the CASH and CREDIT buttons in CheckoutStoreDialog
		 */
		public void actionPerformed(ActionEvent e) {
			String actionCommand = e.getActionCommand();

			if (actionCommand.equals("CASH")) {
				if (validateCash() != VALIDATIONERROR) {
					dispose();
				} else {
					Toolkit.getDefaultToolkit().beep();

					// display a popup to inform the user of the validation
					// error
					JOptionPane errorPopup = new JOptionPane();
					errorPopup.showMessageDialog(this, "Invalid Input",
							"Error", JOptionPane.ERROR_MESSAGE);
				}
			}

			if (actionCommand.equals("CREDIT")) {
				if (validateCredit() != VALIDATIONERROR) {
					// for now the window stays but the fields remain
					// May add code later to open a new window
					dispose();
				} else {
					Toolkit.getDefaultToolkit().beep();

					// display a popup to inform the user of the validation
					// error
					JOptionPane errorPopup = new JOptionPane();
					errorPopup.showMessageDialog(this, "Invalid Input",
							"Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		}

		/*
		 * Validates the text fields in Payment and then adds the prints receipt
		 * if the fields are valid. Returns the operation status, which is one
		 * of OPERATIONSUCCESS, OPERATIONFAILED, VALIDATIONERROR.
		 */
		private int validateCash() {
			try {

				mvb.updateStatusBar("Checking out Items...");
				// no fields to validate on cash purchase
				mvb.updateStatusBar("Checkout complete.");
				
				//formatting to print receiptID and date of purchase in status bar
				ResultSet rdate = clerk.receiptDate(rid);
				rdate.next();
				Date recdate = rdate.getDate(1);
				SimpleDateFormat rdformat = new SimpleDateFormat("DD-MM-YYYY");
				String strdate = rdformat.format(recdate);
				mvb.updateStatusBar("Receipt# : " + rid.toString() + " Date: " + strdate);

				showItems(); // show items in a table
				cost = clerk.receiptTotal(rid);
				mvb.updateStatusBar("Your total cost is: $" + cost);

				rid = null; // clear rid for next purchase process
				cost = null; //clear cost for next purchase process

				return OPERATIONSUCCESS;

			// this exception is thrown when a string cannot be converted to an integer	
			} catch (NumberFormatException ex) {
				return VALIDATIONERROR;
			// this exception is thrown when an error occurs in any SQL query when
			// clerk.receiptDate is called.
			} catch (SQLException ex) {
				return VALIDATIONERROR;
			}
		}

		private int validateCredit() {
			try {
				String cardnumber;
				Date expire;

				mvb.updateStatusBar("Validating Credit Card...");
				
				if (cardNo.getText().trim().length() != 0
						&& cardNo.getText().trim().length() == 16
						&& isNumeric(cardNo.getText().trim())) {
					cardnumber = cardNo.getText().trim();
				} else {
					return VALIDATIONERROR;
				}

				String stringDate = cardExp.getText().trim();

				if (stringDate.length() != 0) {
					if (stringDate.length() != 5)
						return VALIDATIONERROR;
					SimpleDateFormat fm = new SimpleDateFormat("MM/yy");
					java.util.Date utilDate;
					try {
						utilDate = fm.parse(stringDate);
					} catch (ParseException ex) {
						return VALIDATIONERROR;
					}
					expire = new java.sql.Date(utilDate.getTime());
				} else {
					return VALIDATIONERROR;
				}
				
				if(clerk.updateCreditCard(rid, cardnumber, expire) == 0){
					mvb.updateStatusBar("Validate Failed");
					return OPERATIONFAILED;
				}
				
				mvb.updateStatusBar("Checkout complete.");
				
				//formatting to print receiptID and date of purchase in status bar
				ResultSet rdate = clerk.receiptDate(rid);
				rdate.next();
				Date recdate = rdate.getDate(1);
				SimpleDateFormat rdformat = new SimpleDateFormat("DD-MM-YYYY");
				String strdate = rdformat.format(recdate);
				mvb.updateStatusBar("Receipt# : " + rid.toString() + " Date: " + strdate);

				showItems(); // show purchased items in a table
				
				String lastfive = cardnumber.substring(cardnumber.length() - 6, cardnumber.length() - 1);
				mvb.updateStatusBar("Credit card# : XXXX XXXX XXXX " + lastfive);
				cost = clerk.receiptTotal(rid);
				mvb.updateStatusBar("Your total cost is: $" + cost.toString());
				rid = null; // clear rid for next Purchase process
				cost = null; // clear cost for next Purchase process
				return OPERATIONSUCCESS;

			// this exception is thrown when a string cannot be converted to a number
			} catch (NumberFormatException ex) {
				return VALIDATIONERROR;
			// this exception is thrown when an error occurs in any SQL query when
			// clerk.receiptDate is called.
			} catch (SQLException ex) {
				return VALIDATIONERROR;
			}
		}
		
		private boolean isNumeric(String string) {
			try {
				Double.valueOf(string);
			} catch (NumberFormatException ex) {
				return false;
			}
			return true;
		}

	}

}
