package ca.ubc.cs304.tables;

// File: PurchaseController.java

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


/*
 * PurchaseController is a control class that handles action events 
 * on the Purchase Admin menu. It also updates the GUI based on 
 * which menu item the user selected. This class contains the following 
 * inner classes: PurchaseInsertDialog, PurchaseUpdateDialog, and 
 * PurchaseDeleteDialog. PurchaseInsertDialog is a dialog box that allows a 
 * user to insert a purchase. PurchaseUpdateDialog is a dialog box that allows 
 * a user to a purchase. PurchaseDeleteDialog is a dialog box 
 * that allows a user to delete a purchase.
 *
 * PurchaseController implements the ExceptionListener interface which
 * allows it to be notified of any Exceptions that occur in PurchaseModel
 * (PurchaseModel contains the database transaction functions). It is defined
 * in PurchaseModel.java. The ExceptionListener interface is defined in 
 * ExceptionListener.java. When an Exception occurs in PurchaseModel, 
 * PurchaseController will update the status text area of MvbView. 
 */
public class PurchaseController implements ActionListener, ExceptionListener
{
    private MvbView mvb = null;
    private PurchaseModel purchase = null; 

    // constants used for describing the outcome of an operation
    public static final int OPERATIONSUCCESS = 0;
    public static final int OPERATIONFAILED = 1;
    public static final int VALIDATIONERROR = 2; 
    
    public PurchaseController(MvbView mvb)
    {
	this.mvb = mvb;
	purchase = new PurchaseModel();

	// register to receive exception events from purchase
	purchase.addExceptionListener(this);
    }


    /*
     * This event handler gets called when the user makes a menu
     * item selection.
     */ 
    public void actionPerformed(ActionEvent e)
    {
	String actionCommand = e.getActionCommand();

	// you cannot use == for string comparisons
	if (actionCommand.equals("Insert Purchase"))
	{
	    PurchaseInsertDialog iDialog = new PurchaseInsertDialog(mvb);
	    iDialog.pack();
	    mvb.centerWindow(iDialog);
	    iDialog.setVisible(true);
	    return; 
	}

	if (actionCommand.equals("Update Purchase"))
	{
	    PurchaseUpdateDialog uDialog = new PurchaseUpdateDialog(mvb);
	    uDialog.pack();
	    mvb.centerWindow(uDialog);
	    uDialog.setVisible(true);
	    return; 
	}

	if (actionCommand.equals("Delete Purchase"))
	{
	    PurchaseDeleteDialog dDialog = new PurchaseDeleteDialog(mvb);
	    dDialog.pack();
	    mvb.centerWindow(dDialog);
	    dDialog.setVisible(true);
	    return; 
	}

	if (actionCommand.equals("Show Purchase"))
	{
	    showAllPurchases();
	    return; 
	}

	if (actionCommand.equals("Edit Purchase"))
	{
	    editAllPurchases();
	    return; 
	}	
    }


    /*
     * This event handler gets called when an exception event 
     * is generated. It displays the exception message on the status 
     * text area of the main GUI.
     */ 
    public void exceptionGenerated(ExceptionEvent ex)
    {
	String message = ex.getMessage();
	
	// annoying beep sound
	Toolkit.getDefaultToolkit().beep();

	if (message != null)
	{	
	    mvb.updateStatusBar(ex.getMessage());
	}
	else
	{
	    mvb.updateStatusBar("An exception occurred!");
	}
    }    


    /*
     * This method displays all purchases in a non-editable JTable
     */
    private void showAllPurchases()
    {
	ResultSet rs = purchase.showPurchase();
	
	// CustomTableModel maintains the result set's data, e.g., if  
	// the result set is updatable, it will update the database
	// when the table's data is modified.  
	CustomTableModel model = new CustomTableModel(purchase.getConnection(), rs);
	CustomTable data = new CustomTable(model);

	// register to be notified of any exceptions that occur in the model and table
	model.addExceptionListener(this);
	data.addExceptionListener(this);
	    
	// Adds the table to the scrollpane.
	// By default, a JTable does not have scroll bars.
	mvb.addTable(data);
    }


    /*
     * This method displays all purchases in an editable JTable
     */
    private void editAllPurchases()
    {
	ResultSet rs = purchase.editPurchase();
	
	CustomTableModel model = new CustomTableModel(purchase.getConnection(), rs);
	CustomTable data = new CustomTable(model);

	model.addExceptionListener(this);
	data.addExceptionListener(this);
	    
	mvb.addTable(data);
    }


    /*
     * This class creates a dialog box for inserting a purchase.
     */
    class PurchaseInsertDialog extends JDialog implements ActionListener
    {
	private JTextField custID = new JTextField(12);
	private JTextField cardNumber = new JTextField(16);
	private JTextField cardExpiry = new JTextField(5);
	private JTextField purExpected = new JTextField(10);
	private JTextField purDelivered = new JTextField(10);
	
	/*
	 * Constructor. Creates the dialog's GUI.
	 */
	public PurchaseInsertDialog(JFrame parent)
	{
	    super(parent, "Insert Purchase", true);
	    setResizable(false);

	    JPanel contentPane = new JPanel(new BorderLayout());
	    setContentPane(contentPane);
	    contentPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

	    // this panel will contain the text field labels and the text fields.
	    JPanel inputPane = new JPanel();
	    inputPane.setBorder(BorderFactory.createCompoundBorder(
			 new TitledBorder(new EtchedBorder(), "Purchase Fields"), 
			 new EmptyBorder(5, 5, 5, 5)));
	 
	    // add the text field labels and text fields to inputPane
	    // using the GridBag layout manager

	    GridBagLayout gb = new GridBagLayout();
	    GridBagConstraints c = new GridBagConstraints();
	    inputPane.setLayout(gb);

	    // create and place customer id label
	    JLabel label = new JLabel("Customer ID: ", SwingConstants.RIGHT);
	    c.gridwidth = GridBagConstraints.RELATIVE;
	    c.insets = new Insets(5, 0, 0, 5);
	    c.anchor = GridBagConstraints.EAST;
	    gb.setConstraints(label, c);
	    inputPane.add(label);

	    // place customer id field
	    c.gridwidth = GridBagConstraints.REMAINDER;
	    c.insets = new Insets(5, 0, 0, 0);
	    c.anchor = GridBagConstraints.WEST;
	    gb.setConstraints(custID, c);
	    inputPane.add(custID);
	    
	    // create and place card number label
	    label = new JLabel("Card number: ", SwingConstants.RIGHT);
	    c.gridwidth = GridBagConstraints.RELATIVE;
	    c.insets = new Insets(5, 0, 0, 5);
	    c.anchor = GridBagConstraints.EAST;
	    gb.setConstraints(label, c);
	    inputPane.add(label);

	    // place card number field
	    c.gridwidth = GridBagConstraints.REMAINDER;
	    c.insets = new Insets(5, 0, 0, 0);
	    c.anchor = GridBagConstraints.WEST;
	    gb.setConstraints(cardNumber, c);
	    inputPane.add(cardNumber);

	    // create and place card expiry label
	    label = new JLabel("Card expiry (MM/YY): ", SwingConstants.RIGHT);
	    c.gridwidth = GridBagConstraints.RELATIVE;
	    c.insets = new Insets(5, 0, 0, 5);
	    c.anchor = GridBagConstraints.EAST;
	    gb.setConstraints(label, c);
	    inputPane.add(label);

	    // place card expiry field
	    c.gridwidth = GridBagConstraints.REMAINDER;
	    c.insets = new Insets(5, 0, 0, 0);
	    c.anchor = GridBagConstraints.WEST;
	    gb.setConstraints(cardExpiry, c);
	    inputPane.add(cardExpiry);

	    // create and place expected date label
	    label = new JLabel("Expected date (dd-MM-yyyy): ", SwingConstants.RIGHT);
	    c.gridwidth = GridBagConstraints.RELATIVE;
	    c.insets = new Insets(5, 0, 0, 5);
	    c.anchor = GridBagConstraints.EAST;
	    gb.setConstraints(label, c);
	    inputPane.add(label);

	    // place expected date field
	    c.gridwidth = GridBagConstraints.REMAINDER;
	    c.insets = new Insets(5, 0, 0, 0);
	    c.anchor = GridBagConstraints.WEST;
	    gb.setConstraints(purExpected, c);
	    inputPane.add(purExpected);

	    // create and place delivered date label
	    label = new JLabel("Delivered date (dd-MM-yyyy): ", SwingConstants.RIGHT);
	    c.gridwidth = GridBagConstraints.RELATIVE;
	    c.insets = new Insets(5, 0, 0, 5);
	    c.anchor = GridBagConstraints.EAST;
	    gb.setConstraints(label, c);
	    inputPane.add(label);

	    // place delivered date field
	    c.gridwidth = GridBagConstraints.REMAINDER;
	    c.insets = new Insets(5, 0, 0, 0);
	    c.anchor = GridBagConstraints.WEST;
	    gb.setConstraints(purDelivered, c);
	    inputPane.add(purDelivered);
	    
	    // when the return key is pressed in the last field
	    // of this form, the action performed by the ok button
	    // is executed
	    purDelivered.addActionListener(this);
	    purDelivered.setActionCommand("OK");

	    // panel for the OK and cancel buttons
	    JPanel buttonPane = new JPanel();
	    buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.X_AXIS));
	    buttonPane.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 2));

	    JButton OKButton = new JButton("OK");
	    JButton cancelButton = new JButton("Cancel");
	    OKButton.addActionListener(this);
	    OKButton.setActionCommand("OK");
	    cancelButton.addActionListener(new ActionListener()
		{
		    public void actionPerformed(ActionEvent e)
		    {
			dispose();
		    }
		});

	    // add the buttons to buttonPane
	    buttonPane.add(Box.createHorizontalGlue());
	    buttonPane.add(OKButton);
	    buttonPane.add(Box.createRigidArea(new Dimension(10,0)));
	    buttonPane.add(cancelButton);

	    contentPane.add(inputPane, BorderLayout.CENTER);
	    contentPane.add(buttonPane, BorderLayout.SOUTH);

	    addWindowListener(new WindowAdapter() 
		{
		    public void windowClosing(WindowEvent e)
		    {
			dispose();
		    }
		});
	}


	/*
	 * Event handler for the OK button in PurchaseInsertDialog
	 */ 
	public void actionPerformed(ActionEvent e)
	{
	    String actionCommand = e.getActionCommand();

	    if (actionCommand.equals("OK"))
	    {
		if (validateInsert() != VALIDATIONERROR)
		{
		    dispose();
		}
		else
		{
		    Toolkit.getDefaultToolkit().beep();

		    // display a popup to inform the user of the validation error
		    JOptionPane errorPopup = new JOptionPane();
		    errorPopup.showMessageDialog(this, "Invalid Input", "Error", JOptionPane.ERROR_MESSAGE);
		}	
	    }
	}


	/*
	 * Validates the text fields in PurchaseInsertDialog and then
	 * calls purchase.insertPurchase() if the fields are valid.
	 * Returns the operation status, which is one of OPERATIONSUCCESS, 
	 * OPERATIONFAILED, VALIDATIONERROR.
	 */ 
	private int validateInsert()
	{
	    try
	    {
	    String cid;
	    String cardno;
	    Date expire;
	    Date expected;
	    Date delivered;

		if (custID.getText().trim().length() != 0)
		{
		    cid = custID.getText().trim();
		} else {
			return VALIDATIONERROR;
		}

		if (cardNumber.getText().trim().length() == 16 && isNumeric(cardNumber.getText().trim()))
		{
		    cardno = cardNumber.getText().trim();
		}
		else
		{
		    cardno = null; 
		}

		String stringDate = cardExpiry.getText().trim();
		
		if (stringDate.length() != 0)
		{
			if(stringDate.length() != 5) return VALIDATIONERROR;
			SimpleDateFormat fm = new SimpleDateFormat("MM/yy");
			java.util.Date utilDate;
			try {
				utilDate = fm.parse(stringDate);
			} catch (ParseException ex) {
				return VALIDATIONERROR;
			}
			expire = new java.sql.Date(utilDate.getTime());
		}
		else
		{
		    expire = null;
		}

		stringDate = purExpected.getText().trim();
		
		if (stringDate.length() != 0)
		{
			if(stringDate.length() != 10) return VALIDATIONERROR;
			SimpleDateFormat fm = new SimpleDateFormat("dd-MM-yyyy");
			java.util.Date utilDate;
			try {
				utilDate = fm.parse(stringDate);
			} catch (ParseException ex) {
				return VALIDATIONERROR;
			}
			expected = new java.sql.Date(utilDate.getTime());
		}
		else
		{
		    expected = null;
		}
		
		stringDate = purDelivered.getText().trim();
		
		if (stringDate.length() != 0)
		{
			if(stringDate.length() != 10) return VALIDATIONERROR;
			SimpleDateFormat fm = new SimpleDateFormat("dd-MM-yyyy");
			java.util.Date utilDate;
			try {
				utilDate = fm.parse(stringDate);
			} catch (ParseException ex) {
				return VALIDATIONERROR;
			}
			delivered = new java.sql.Date(utilDate.getTime());
		}
		else
		{
		    delivered = null;
		}
		
		mvb.updateStatusBar("Inserting purchase...");

		if (purchase.insertPurchase(cid, cardno, expire, expected, delivered))
		{
		    mvb.updateStatusBar("Operation successful.");
		    showAllPurchases();
		    return OPERATIONSUCCESS; 
		}
		else
		{
		    Toolkit.getDefaultToolkit().beep();
		    mvb.updateStatusBar("Operation failed.");
		    return OPERATIONFAILED; 
		}
	    }
	    catch (NumberFormatException ex)
	    {
		// this exception is thrown when a string 
		// cannot be converted to a number
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


    /*
     * This class creates a dialog box for updating a purchase.
     */
    class PurchaseUpdateDialog extends JDialog implements ActionListener
    {
    	private JTextField recID = new JTextField(10);
    	private JTextField custID = new JTextField(12);
    	private JTextField cardNumber = new JTextField(16);
    	private JTextField cardExpiry = new JTextField(5);
    	private JTextField purExpected = new JTextField(10);
    	private JTextField purDelivered = new JTextField(10);
	
	/*
	 * Constructor. Creates the dialog's GUI.
	 */
	public PurchaseUpdateDialog(JFrame parent)
	{
	    super(parent, "Update Purchase", true);
	    setResizable(false);

	    JPanel contentPane = new JPanel(new BorderLayout());
	    setContentPane(contentPane);
	    contentPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

	    // this panel contains the text field labels and 
	    // the text fields.
	    JPanel inputPane = new JPanel();
	    inputPane.setBorder(BorderFactory.createCompoundBorder(
			 new TitledBorder(new EtchedBorder(), "Purchase Fields"), 
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

	    // place purchase receipt ID field
	    c.gridwidth = GridBagConstraints.REMAINDER;
	    c.insets = new Insets(5, 0, 0, 0);
	    c.anchor = GridBagConstraints.WEST;
	    gb.setConstraints(recID, c);
	    inputPane.add(recID);
	    
	    // create and place customer id label
	    label = new JLabel("Customer ID: ", SwingConstants.RIGHT);
	    c.gridwidth = GridBagConstraints.RELATIVE;
	    c.insets = new Insets(5, 0, 0, 5);
	    c.anchor = GridBagConstraints.EAST;
	    gb.setConstraints(label, c);
	    inputPane.add(label);

	    // place customer id field
	    c.gridwidth = GridBagConstraints.REMAINDER;
	    c.insets = new Insets(5, 0, 0, 0);
	    c.anchor = GridBagConstraints.WEST;
	    gb.setConstraints(custID, c);
	    inputPane.add(custID);
	    
	    // create and place card number label
	    label = new JLabel("Card number: ", SwingConstants.RIGHT);
	    c.gridwidth = GridBagConstraints.RELATIVE;
	    c.insets = new Insets(5, 0, 0, 5);
	    c.anchor = GridBagConstraints.EAST;
	    gb.setConstraints(label, c);
	    inputPane.add(label);

	    // place card number field
	    c.gridwidth = GridBagConstraints.REMAINDER;
	    c.insets = new Insets(5, 0, 0, 0);
	    c.anchor = GridBagConstraints.WEST;
	    gb.setConstraints(cardNumber, c);
	    inputPane.add(cardNumber);

	    // create and place card expiry label
	    label = new JLabel("Card expiry (MM/YY): ", SwingConstants.RIGHT);
	    c.gridwidth = GridBagConstraints.RELATIVE;
	    c.insets = new Insets(5, 0, 0, 5);
	    c.anchor = GridBagConstraints.EAST;
	    gb.setConstraints(label, c);
	    inputPane.add(label);

	    // place card expiry field
	    c.gridwidth = GridBagConstraints.REMAINDER;
	    c.insets = new Insets(5, 0, 0, 0);
	    c.anchor = GridBagConstraints.WEST;
	    gb.setConstraints(cardExpiry, c);
	    inputPane.add(cardExpiry);

	    // create and place expected date label
	    label = new JLabel("Expected date (dd-MM-yyyy): ", SwingConstants.RIGHT);
	    c.gridwidth = GridBagConstraints.RELATIVE;
	    c.insets = new Insets(5, 0, 0, 5);
	    c.anchor = GridBagConstraints.EAST;
	    gb.setConstraints(label, c);
	    inputPane.add(label);

	    // place expected date field
	    c.gridwidth = GridBagConstraints.REMAINDER;
	    c.insets = new Insets(5, 0, 0, 0);
	    c.anchor = GridBagConstraints.WEST;
	    gb.setConstraints(purExpected, c);
	    inputPane.add(purExpected);

	    // create and place delivered date label
	    label = new JLabel("Delivered date (dd-MM-yyyy): ", SwingConstants.RIGHT);
	    c.gridwidth = GridBagConstraints.RELATIVE;
	    c.insets = new Insets(5, 0, 0, 5);
	    c.anchor = GridBagConstraints.EAST;
	    gb.setConstraints(label, c);
	    inputPane.add(label);

	    // place delivered date field
	    c.gridwidth = GridBagConstraints.REMAINDER;
	    c.insets = new Insets(5, 0, 0, 0);
	    c.anchor = GridBagConstraints.WEST;
	    gb.setConstraints(purDelivered, c);
	    inputPane.add(purDelivered);
	    
	    // when the return key is pressed in the last field
	    // of this form, the action performed by the ok button
	    // is executed
	    purDelivered.addActionListener(this);
	    purDelivered.setActionCommand("OK");

	    // panel for the OK and cancel buttons
	    JPanel buttonPane = new JPanel();
	    buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.X_AXIS));
	    buttonPane.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 2));

	    JButton OKButton = new JButton("OK");
	    JButton cancelButton = new JButton("Cancel");
	    OKButton.addActionListener(this);
	    OKButton.setActionCommand("OK");
	    cancelButton.addActionListener(new ActionListener()
		{
		    public void actionPerformed(ActionEvent e)
		    {
			dispose();
		    }
		});

	    // add buttons to buttonPane
	    buttonPane.add(Box.createHorizontalGlue());
	    buttonPane.add(OKButton);
	    buttonPane.add(Box.createRigidArea(new Dimension(10,0)));
	    buttonPane.add(cancelButton);

	    contentPane.add(inputPane, BorderLayout.CENTER);
	    contentPane.add(buttonPane, BorderLayout.SOUTH);

	    addWindowListener(new WindowAdapter() 
		{
		    public void windowClosing(WindowEvent e)
		    {
			dispose();
		    }
		});
	}


	/*
	 * Event handler for the OK button in PurchaseUpdateDialog
	 */ 
	public void actionPerformed(ActionEvent e)
	{
	    String actionCommand = e.getActionCommand();

	    if (actionCommand.equals("OK"))
	    {
		if (validateUpdate() != VALIDATIONERROR)
		{
		    dispose();
		}
		else
		{
		    Toolkit.getDefaultToolkit().beep();

		    // display a popup to inform the user of the error
		    JOptionPane errorPopup = new JOptionPane();
		    errorPopup.showMessageDialog(this, "Invalid Input", "Error", JOptionPane.ERROR_MESSAGE);
		}
	    }
	}


	/*
	 * Validates the text fields in PurchaseUpdateDialog and then
	 * calls purchase.updatePurchase() if the fields are valid.
	 * Returns the operation status.
	 */ 
	private int validateUpdate()
	{
		try
	    {
			int receiptID;
			String cid;
		    String cardno;
		    Date expire;
		    Date expected;
		    Date delivered;

		if (recID.getText().trim().length() != 0 && isNumeric(recID.getText().trim()))
		{
		    receiptID = Integer.valueOf(recID.getText().trim()).intValue();
		    
		    // check if purchase exists
		    if (!purchase.findPurchase(receiptID))
		    {
		    	Toolkit.getDefaultToolkit().beep();
		    	mvb.updateStatusBar("Purchase with ID " + receiptID + " does not exist!");
		    	return OPERATIONFAILED; 
		    }
		} else {
			return VALIDATIONERROR;
		}

		if (custID.getText().trim().length() != 0)
		{
		    cid = custID.getText().trim();
		} else {
			return VALIDATIONERROR;
		}

		if (cardNumber.getText().trim().length() == 16 && isNumeric(cardNumber.getText().trim()))
		{
		    cardno = cardNumber.getText().trim();
		}
		else
		{
		    cardno = null; 
		}

		String stringDate = cardExpiry.getText().trim();
		
		if (stringDate.length() != 0)
		{
			if(stringDate.length() != 5) return VALIDATIONERROR;
			SimpleDateFormat fm = new SimpleDateFormat("MM/yy");
			java.util.Date utilDate;
			try {
				utilDate = fm.parse(stringDate);
			} catch (ParseException ex) {
				return VALIDATIONERROR;
			}
			expire = new java.sql.Date(utilDate.getTime());
		}
		else
		{
		    expire = null;
		}

		stringDate = purExpected.getText().trim();
		
		if (stringDate.length() != 0)
		{
			if(stringDate.length() != 10) return VALIDATIONERROR;
			SimpleDateFormat fm = new SimpleDateFormat("dd-MM-yyyy");
			java.util.Date utilDate;
			try {
				utilDate = fm.parse(stringDate);
			} catch (ParseException ex) {
				return VALIDATIONERROR;
			}
			expected = new java.sql.Date(utilDate.getTime());
		}
		else
		{
		    expected = null;
		}
		
		stringDate = purDelivered.getText().trim();
		
		if (stringDate.length() != 0)
		{
			if(stringDate.length() != 10) return VALIDATIONERROR;
			SimpleDateFormat fm = new SimpleDateFormat("dd-MM-yyyy");
			java.util.Date utilDate;
			try {
				utilDate = fm.parse(stringDate);
			} catch (ParseException ex) {
				return VALIDATIONERROR;
			}
			delivered = new java.sql.Date(utilDate.getTime());
		}
		else
		{
		    delivered = null;
		}
		
		mvb.updateStatusBar("Updating purchase...");

		if (purchase.updatePurchase(receiptID, cid, cardno, expire, expected, delivered))
		{
		    mvb.updateStatusBar("Operation successful.");
		    showAllPurchases();
		    return OPERATIONSUCCESS; 
		}
		else
		{
		    Toolkit.getDefaultToolkit().beep();
		    mvb.updateStatusBar("Operation failed.");
		    return OPERATIONFAILED; 
		}
	    }
	    catch (NumberFormatException ex)
	    {
		// this exception is thrown when a string 
		// cannot be converted to a number
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


    /*
     * This class creates a dialog box for deleting a purchase.
     */
    class PurchaseDeleteDialog extends JDialog implements ActionListener
    {
	private JTextField recID = new JTextField(10);
	

	/*
	 * Constructor. Creates the dialog's GUI.
	 */
	public PurchaseDeleteDialog(JFrame parent)
	{
	    super(parent, "Delete Purchase", true);
	    setResizable(false);

	    JPanel contentPane = new JPanel(new BorderLayout());
	    setContentPane(contentPane);
	    contentPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

	    // this panel contains the text field labels and the text fields.
	    JPanel inputPane = new JPanel();
	    inputPane.setBorder(BorderFactory.createCompoundBorder(
			 new TitledBorder(new EtchedBorder(), "Purchase Fields"), 
			 new EmptyBorder(5, 5, 5, 5)));

	    // add the text field labels and text fields to inputPane
	    // using the GridBag layout manager

	    GridBagLayout gb = new GridBagLayout();
	    GridBagConstraints c = new GridBagConstraints();
	    inputPane.setLayout(gb);

	    // create and place receipt id label
	    JLabel label= new JLabel("UPC: ", SwingConstants.RIGHT);	    
	    c.gridwidth = GridBagConstraints.RELATIVE;
	    c.insets = new Insets(0, 0, 0, 5);
	    c.anchor = GridBagConstraints.EAST;
	    gb.setConstraints(label, c);
	    inputPane.add(label);

	    // place receipt id field
	    c.gridwidth = GridBagConstraints.REMAINDER;
	    c.insets = new Insets(0, 0, 0, 0);
	    c.anchor = GridBagConstraints.WEST;
	    gb.setConstraints(recID, c);
	    inputPane.add(recID);

	    // when the return key is pressed while in the
	    // purchaseID field, the action performed by the ok button
	    // is executed
	    recID.addActionListener(this);
	    recID.setActionCommand("OK");

	    // panel for the OK and cancel buttons
	    JPanel buttonPane = new JPanel();
	    buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.X_AXIS));
	    buttonPane.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 2));

	    JButton OKButton = new JButton("OK");
	    JButton cancelButton = new JButton("Cancel");
	    OKButton.addActionListener(this);
	    OKButton.setActionCommand("OK");
	    cancelButton.addActionListener(new ActionListener()
		{
		    public void actionPerformed(ActionEvent e)
		    {
			dispose();
		    }
		});

	    // add buttons to buttonPane
	    buttonPane.add(Box.createHorizontalGlue());
	    buttonPane.add(OKButton);
	    buttonPane.add(Box.createRigidArea(new Dimension(10,0)));
	    buttonPane.add(cancelButton);

	    contentPane.add(inputPane, BorderLayout.CENTER);
	    contentPane.add(buttonPane, BorderLayout.SOUTH);

	    addWindowListener(new WindowAdapter() 
		{
		    public void windowClosing(WindowEvent e)
		    {
			dispose();
		    }
		});
	}


	/*
	 * Event handler for the OK button in PurchaseDeleteDialog
	 */ 
	public void actionPerformed(ActionEvent e)
	{
	    String actionCommand = e.getActionCommand();

	    if (actionCommand.equals("OK"))
	    {
		if (validateDelete() != VALIDATIONERROR)
		{
		    dispose();
		}
		else
		{
		    Toolkit.getDefaultToolkit().beep();

		    // display a popup to inform the user of the error
		    JOptionPane errorPopup = new JOptionPane();
		    errorPopup.showMessageDialog(this, "Invalid Input", "Error", JOptionPane.ERROR_MESSAGE);
		}
	    }
	}


	/*
	 * Validates the text fields in PurchaseDeleteDialog and then
	 * calls purchase.deletePurchase() if the fields are valid.
	 * Returns the operation status.
	 */ 
	private int validateDelete()
	{
	    try
	    {
		int receiptID;

		if (recID.getText().trim().length() != 0)
		{
		    receiptID = Integer.valueOf(recID.getText().trim()).intValue();

		    // check if purchase exists
		    if (!purchase.findPurchase(receiptID))
		    {
			Toolkit.getDefaultToolkit().beep();
			mvb.updateStatusBar("Purchase with ID " + receiptID + " does not exist!");
			return OPERATIONFAILED; 
		    }
		}
		else
		{
		    return VALIDATIONERROR; 
		}
	       
		mvb.updateStatusBar("Deleting purchase...");

		if (purchase.deletePurchase(receiptID))
		{
		    mvb.updateStatusBar("Operation successful.");
		    showAllPurchases();
		    return OPERATIONSUCCESS;
		}
		else
		{
		    Toolkit.getDefaultToolkit().beep();
		    mvb.updateStatusBar("Operation failed.");
		    return OPERATIONFAILED;
		}
	    }
	    catch (NumberFormatException ex)
	    {
		return VALIDATIONERROR; 
	    }
	}
    }
}
