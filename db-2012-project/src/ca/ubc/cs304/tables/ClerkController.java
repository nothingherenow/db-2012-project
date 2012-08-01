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
import java.sql.Date;
import java.util.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class ClerkController implements ActionListener, ExceptionListener {

	private MvbView mvb;
	private ClerkTransactions clerk = null;
	private PurchaseItemModel purchit = null;
	// arraylist to temporarily store instore purchases
	// each item takes 2 spaces in the array, one for upc and one for quantity
	private ArrayList tempitems = null; 

	// constants used for describing the outcome of an operation
	public static final int OPERATIONSUCCESS = 0;
	public static final int OPERATIONFAILED = 1;
	public static final int VALIDATIONERROR = 2;

	public ClerkController(MvbView mvb) {
		this.mvb = mvb;
		clerk = new ClerkTransactions();
		purchit = new PurchaseItemModel();
		tempitems = new ArrayList(50);  //default size, every instore purchase can have maximum 25 items
		// register to receive exception events from customer
		clerk.addExceptionListener(this);
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
	 * This method displays the most recent item added to a purchase in a non-editable
	 * JTable
	 */
	private void showAdded(Integer iupc) {
		ResultSet rs = clerk.showItem(iupc); 
		// show all the items lined up to be purchased

		//CustomTableModel maintains the result set's data, e.g., if
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
	 * This method displays a portion of the receipt in a non-editable
	 * JTable, specifically the list of items, their prices and quantities purchased.
	 */
	private void receiptlist(Integer rid) {
		ResultSet rs = clerk.receiptItems(rid); 
		// show all the items lined up to be purchased

		//CustomTableModel maintains the result set's data, e.g., if
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
				if (validateCheckout() != VALIDATIONERROR) {
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

			if (actionCommand.equals("ADD")) {
				if (validateAdd() != VALIDATIONERROR) {
					dispose();
					// for now the window closes but the bill table remains
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
				tempitems.add(iupc);
				tempitems.add(iquant);
				
				mvb.updateStatusBar("Add to bill complete.");

				showAdded(iupc); // Shows the entered item in a table

				return OPERATIONSUCCESS;

			} catch (NumberFormatException ex) {
				// this exception is thrown when a string
				// cannot be converted to a number
				return VALIDATIONERROR;
			}
		}

		/*
		 * Validates the text fields in CheckoutStoreDialog and then adds the
		 * items to the bill if the fields are valid. Returns the operation
		 * status, which is one of OPERATIONSUCCESS, OPERATIONFAILED,
		 * VALIDATIONERROR.
		 */
		private int validateCheckout() {
			try {

				mvb.updateStatusBar("Checking out Items...");

				//create a new purchase, and obtain that purchase's receiptID
				Integer recid = clerk.instorePurchase();
				if (recid == 0){
					return OPERATIONFAILED;
				}
				int i;
				for (i = 0; i < tempitems.size(); i+=2) {
					purchit.insertPurchaseItem(recid, (Integer) tempitems.get(i), (Integer) tempitems.get(i+1));
				}

				mvb.updateStatusBar("Checkout complete.");

				//formatting to print receiptID and date of purchase in status bar
				ResultSet rdate = clerk.receiptDate(recid);
				rdate.next();
				Date recdate = rdate.getDate(1);
				SimpleDateFormat rdformat = new SimpleDateFormat("DD-MM-YYYY");
				String strdate = rdformat.format(recdate);
				mvb.updateStatusBar("Receipt# : " + recid.toString() + " Date: " + strdate);
				
				//output table with list of items purchased and relevant information
				receiptlist(recid);
				
				//formatting to print total in status bar
				BigDecimal ptotal = clerk.receiptTotal(recid);
				mvb.updateStatusBar("Purchase Total: " + ptotal.toString());
				
				//delete everything in the temporary array
				tempitems.clear();
				
				return OPERATIONSUCCESS;

			} catch (NumberFormatException ex) {
				// this exception is thrown when a string
				// cannot be converted to a number
				return VALIDATIONERROR;
			} catch (SQLException ex) {
				
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

				if (clerk.checkReturn(receiptId) != true){
					mvb.updateStatusBar("Invalid Receipt ID");
					return OPERATIONFAILED;
				}

				mvb.updateStatusBar("Validation complete.");

				if (clerk.processReturn(receiptId, rupc, quantity) == 0){
					mvb.updateStatusBar("Database error");
					return OPERATIONFAILED;
				}

				return OPERATIONSUCCESS;

			} catch (NumberFormatException ex) {
				// this exception is thrown when a string
				// cannot be converted to a number
				return VALIDATIONERROR;
			}
		}
	}

}
