package ca.ubc.cs304.tables;

// File: PurchaseItemController.java

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


/*
 * PurchaseItemController is a control class that handles action events 
 * on the PurchaseItem Admin menu. It also updates the GUI based on 
 * which menu item the user selected. This class contains the following 
 * inner classes: PurchaseItemInsertDialog, PurchaseItemUpdateDialog, and 
 * PurchaseItemDeleteDialog. PurchaseItemInsertDialog is a dialog box that allows a 
 * user to insert a item. PurchaseItemUpdateDialog is a dialog box that allows 
 * a user to an item. PurchaseItemDeleteDialog is a dialog box 
 * that allows a user to delete an item.
 *
 * PurchaseItemController implements the ExceptionListener interface which
 * allows it to be notified of any Exceptions that occur in PurchaseItemModel
 * (PurchaseItemModel contains the database transaction functions). It is defined
 * in PurchaseItemModel.java. The ExceptionListener interface is defined in 
 * ExceptionListener.java. When an Exception occurs in PurchaseItemModel, 
 * PurchaseItemController will update the status text area of MvbView. 
 */
public class PurchaseItemController implements ActionListener, ExceptionListener
{
    private MvbView mvb = null;
    private PurchaseItemModel purchaseItem = null; 

    // constants used for describing the outcome of an operation
    public static final int OPERATIONSUCCESS = 0;
    public static final int OPERATIONFAILED = 1;
    public static final int VALIDATIONERROR = 2; 
    
    public PurchaseItemController(MvbView mvb)
    {
	this.mvb = mvb;
	purchaseItem = new PurchaseItemModel();

	// register to receive exception events from purchaseItem
	purchaseItem.addExceptionListener(this);
    }


    /*
     * This event handler gets called when the user makes a menu
     * item selection.
     */ 
    public void actionPerformed(ActionEvent e)
    {
	String actionCommand = e.getActionCommand();

	// you cannot use == for string comparisons
	if (actionCommand.equals("Insert PurchaseItem"))
	{
	    PurchaseItemInsertDialog iDialog = new PurchaseItemInsertDialog(mvb);
	    iDialog.pack();
	    mvb.centerWindow(iDialog);
	    iDialog.setVisible(true);
	    return; 
	}

	if (actionCommand.equals("Update PurchaseItem"))
	{
	    PurchaseItemUpdateDialog uDialog = new PurchaseItemUpdateDialog(mvb);
	    uDialog.pack();
	    mvb.centerWindow(uDialog);
	    uDialog.setVisible(true);
	    return; 
	}

	if (actionCommand.equals("Delete PurchaseItem"))
	{
	    PurchaseItemDeleteDialog dDialog = new PurchaseItemDeleteDialog(mvb);
	    dDialog.pack();
	    mvb.centerWindow(dDialog);
	    dDialog.setVisible(true);
	    return; 
	}

	if (actionCommand.equals("Show PurchaseItem"))
	{
	    showAllPurchaseItems();
	    return; 
	}

	if (actionCommand.equals("Edit PurchaseItem"))
	{
	    editAllPurchaseItems();
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
     * This method displays all purchaseItem tuples in a non-editable JTable
     */
    private void showAllPurchaseItems()
    {
	ResultSet rs = purchaseItem.showPurchaseItem();
	
	// CustomTableModel maintains the result set's data, e.g., if  
	// the result set is updatable, it will update the database
	// when the table's data is modified.  
	CustomTableModel model = new CustomTableModel(purchaseItem.getConnection(), rs);
	CustomTable data = new CustomTable(model);

	// register to be notified of any exceptions that occur in the model and table
	model.addExceptionListener(this);
	data.addExceptionListener(this);
	    
	// Adds the table to the scrollpane.
	// By default, a JTable does not have scroll bars.
	mvb.addTable(data);
    }


    /*
     * This method displays all purchaseItem tuples in an editable JTable
     */
    private void editAllPurchaseItems()
    {
	ResultSet rs = purchaseItem.editPurchaseItem();
	
	CustomTableModel model = new CustomTableModel(purchaseItem.getConnection(), rs);
	CustomTable data = new CustomTable(model);

	model.addExceptionListener(this);
	data.addExceptionListener(this);
	    
	mvb.addTable(data);
    }


    /*
     * This class creates a dialog box for inserting a purchaseItem.
     */
    class PurchaseItemInsertDialog extends JDialog implements ActionListener
    {
    private JTextField piReceiptID = new JTextField(10);
	private JTextField itemUPC = new JTextField(10);
	private JTextField itemQuantity = new JTextField(10);
	
	/*
	 * Constructor. Creates the dialog's GUI.
	 */
	public PurchaseItemInsertDialog(JFrame parent)
	{
	    super(parent, "Insert PurchaseItem", true);
	    setResizable(false);

	    JPanel contentPane = new JPanel(new BorderLayout());
	    setContentPane(contentPane);
	    contentPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

	    // this panel will contain the text field labels and the text fields.
	    JPanel inputPane = new JPanel();
	    inputPane.setBorder(BorderFactory.createCompoundBorder(
			 new TitledBorder(new EtchedBorder(), "PurchaseItem Fields"), 
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
	    gb.setConstraints(piReceiptID, c);
	    inputPane.add(piReceiptID);
	    
	    // create and place item UPC label
	    label = new JLabel("Item UPC: ", SwingConstants.RIGHT);
	    c.gridwidth = GridBagConstraints.RELATIVE;
	    c.insets = new Insets(5, 0, 0, 5);
	    c.anchor = GridBagConstraints.EAST;
	    gb.setConstraints(label, c);
	    inputPane.add(label);

	    // place item UPC field
	    c.gridwidth = GridBagConstraints.REMAINDER;
	    c.insets = new Insets(5, 0, 0, 0);
	    c.anchor = GridBagConstraints.WEST;
	    gb.setConstraints(itemUPC, c);
	    inputPane.add(itemUPC);

	    // create and place item quantity label
	    label = new JLabel("Item quantity: ", SwingConstants.RIGHT);
	    c.gridwidth = GridBagConstraints.RELATIVE;
	    c.insets = new Insets(5, 0, 0, 5);
	    c.anchor = GridBagConstraints.EAST;
	    gb.setConstraints(label, c);
	    inputPane.add(label);

	    // place item quantity field
	    c.gridwidth = GridBagConstraints.REMAINDER;
	    c.insets = new Insets(5, 0, 0, 0);
	    c.anchor = GridBagConstraints.WEST;
	    gb.setConstraints(itemQuantity, c);
	    inputPane.add(itemQuantity);
	    
	    // when the return key is pressed in the last field
	    // of this form, the action performed by the ok button
	    // is executed
	    itemQuantity.addActionListener(this);
	    itemQuantity.setActionCommand("OK");

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
	 * Event handler for the OK button in PurchaseItemInsertDialog
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
	 * Validates the text fields in PurchaseItemInsertDialog and then
	 * calls purchaseItem.insertPurchaseItem() if the fields are valid.
	 * Returns the operation status, which is one of OPERATIONSUCCESS, 
	 * OPERATIONFAILED, VALIDATIONERROR.
	 */ 
	private int validateInsert()
	{
	    try
	    {
	    int receiptID;
	    int upc;
	    int quantity;

		if (piReceiptID.getText().trim().length() != 0)
		{
		    receiptID = Integer.valueOf(piReceiptID.getText().trim()).intValue();
		} else {
			return VALIDATIONERROR;
		}

		if (itemUPC.getText().trim().length() != 0)
		{
		    upc = Integer.valueOf(itemUPC.getText().trim()).intValue();
		 
		    // check for duplicates
		    if (purchaseItem.findPurchaseItem(receiptID, upc))
		    {
			Toolkit.getDefaultToolkit().beep();
			mvb.updateStatusBar("PurchaseItem (" + receiptID + "," + upc + ") already exists!");
			return OPERATIONFAILED; 
		    }
		}
		else
		{
		    return VALIDATIONERROR; 
		}

		if (itemQuantity.getText().trim().length() != 0 && isNumeric(itemQuantity.getText().trim()))
		{
		    quantity = Integer.valueOf(itemQuantity.getText().trim()).intValue();
		}
		else
		{
		    return VALIDATIONERROR; 
		}

		mvb.updateStatusBar("Inserting purchaseItem...");

		if (purchaseItem.insertPurchaseItem(receiptID, upc, quantity))
		{
		    mvb.updateStatusBar("Operation successful.");
		    showAllPurchaseItems();
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
	private boolean isPrice(String string) {
		return string.matches("\\d{0,8}\\.\\d\\d");
	}
    }


    /*
     * This class creates a dialog box for updating a purchaseItem.
     */
    class PurchaseItemUpdateDialog extends JDialog implements ActionListener
    {
    	private JTextField piReceiptID = new JTextField(10);
    	private JTextField itemUPC = new JTextField(10);
    	private JTextField itemQuantity = new JTextField(10);
	
	/*
	 * Constructor. Creates the dialog's GUI.
	 */
	public PurchaseItemUpdateDialog(JFrame parent)
	{
	    super(parent, "Update PurchaseItem Quantity", true);
	    setResizable(false);

	    JPanel contentPane = new JPanel(new BorderLayout());
	    setContentPane(contentPane);
	    contentPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

	    // this panel contains the text field labels and 
	    // the text fields.
	    JPanel inputPane = new JPanel();
	    inputPane.setBorder(BorderFactory.createCompoundBorder(
			 new TitledBorder(new EtchedBorder(), "PurchaseItem Fields"), 
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
	    gb.setConstraints(piReceiptID, c);
	    inputPane.add(piReceiptID);
	    
	    // create and place item UPC label
	    label = new JLabel("Item UPC: ", SwingConstants.RIGHT);
	    c.gridwidth = GridBagConstraints.RELATIVE;
	    c.insets = new Insets(5, 0, 0, 5);
	    c.anchor = GridBagConstraints.EAST;
	    gb.setConstraints(label, c);
	    inputPane.add(label);

	    // place item UPC field
	    c.gridwidth = GridBagConstraints.REMAINDER;
	    c.insets = new Insets(5, 0, 0, 0);
	    c.anchor = GridBagConstraints.WEST;
	    gb.setConstraints(itemUPC, c);
	    inputPane.add(itemUPC);

	    // create and place item quantity label
	    label = new JLabel("New Item quantity: ", SwingConstants.RIGHT);
	    c.gridwidth = GridBagConstraints.RELATIVE;
	    c.insets = new Insets(5, 0, 0, 5);
	    c.anchor = GridBagConstraints.EAST;
	    gb.setConstraints(label, c);
	    inputPane.add(label);

	    // place item quantity field
	    c.gridwidth = GridBagConstraints.REMAINDER;
	    c.insets = new Insets(5, 0, 0, 0);
	    c.anchor = GridBagConstraints.WEST;
	    gb.setConstraints(itemQuantity, c);
	    inputPane.add(itemQuantity);
	    
	    // when the return key is pressed in the last field
	    // of this form, the action performed by the ok button
	    // is executed
	    itemQuantity.addActionListener(this);
	    itemQuantity.setActionCommand("OK");

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
	 * Event handler for the OK button in PurchaseItemUpdateDialog
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
	 * Validates the text fields in PurchaseItemUpdateDialog and then
	 * calls purchaseItem.updatePurchaseItem() if the fields are valid.
	 * Returns the operation status.
	 */ 
	private int validateUpdate()
	{
		try
	    {
			int receiptID;
		    int upc;
		    int quantity;

		if (piReceiptID.getText().trim().length() != 0)
		{
		    receiptID = Integer.valueOf(piReceiptID.getText().trim()).intValue();
		} else {
			return VALIDATIONERROR;
		}

		if (itemUPC.getText().trim().length() != 0)
		{
		    upc = Integer.valueOf(itemUPC.getText().trim()).intValue();
		 
		    // check if purchaseItem exists
		    if (!purchaseItem.findPurchaseItem(receiptID, upc))
		    {
		    	Toolkit.getDefaultToolkit().beep();
		    	mvb.updateStatusBar("PurchaseItem (" + receiptID + "," + upc + ") does not exist!");
		    	return OPERATIONFAILED; 
		    }
		}
		else
		{
		    return VALIDATIONERROR; 
		}

		if (itemQuantity.getText().trim().length() != 0 && isNumeric(itemQuantity.getText().trim()))
		{
		    quantity = Integer.valueOf(itemQuantity.getText().trim()).intValue();
		}
		else
		{
		    return VALIDATIONERROR; 
		}
		
		mvb.updateStatusBar("Updating purchaseItem...");

		if (purchaseItem.updatePurchaseItem(receiptID, upc, quantity))
		{
		    mvb.updateStatusBar("Operation successful.");
		    showAllPurchaseItems();
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
	private boolean isPrice(String string) {
		// match 0-8 decimals, then a period, and then two more decimals
		return string.matches("\\d{0,8}\\.\\d\\d");
	}
    }


    /*
     * This class creates a dialog box for deleting a purchaseItem.
     */
    class PurchaseItemDeleteDialog extends JDialog implements ActionListener
    {
	private JTextField piReceiptID = new JTextField(10);
	private JTextField itemUPC = new JTextField(10);
	

	/*
	 * Constructor. Creates the dialog's GUI.
	 */
	public PurchaseItemDeleteDialog(JFrame parent)
	{
	    super(parent, "Delete PurchaseItem", true);
	    setResizable(false);

	    JPanel contentPane = new JPanel(new BorderLayout());
	    setContentPane(contentPane);
	    contentPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

	    // this panel contains the text field labels and the text fields.
	    JPanel inputPane = new JPanel();
	    inputPane.setBorder(BorderFactory.createCompoundBorder(
			 new TitledBorder(new EtchedBorder(), "PurchaseItem Fields"), 
			 new EmptyBorder(5, 5, 5, 5)));

	    // add the text field labels and text fields to inputPane
	    // using the GridBag layout manager

	    GridBagLayout gb = new GridBagLayout();
	    GridBagConstraints c = new GridBagConstraints();
	    inputPane.setLayout(gb);

	    // create and place receipt ID label
	    JLabel label = new JLabel("Receipt ID: ", SwingConstants.RIGHT);	    
	    c.gridwidth = GridBagConstraints.RELATIVE;
	    c.insets = new Insets(0, 0, 0, 5);
	    c.anchor = GridBagConstraints.EAST;
	    gb.setConstraints(label, c);
	    inputPane.add(label);

	    // place receipt ID field
	    c.gridwidth = GridBagConstraints.REMAINDER;
	    c.insets = new Insets(0, 0, 0, 0);
	    c.anchor = GridBagConstraints.WEST;
	    gb.setConstraints(piReceiptID, c);
	    inputPane.add(piReceiptID);
	    
	    // create and place upc label
	    label = new JLabel("Item UPC: ", SwingConstants.RIGHT);	    
	    c.gridwidth = GridBagConstraints.RELATIVE;
	    c.insets = new Insets(0, 0, 0, 5);
	    c.anchor = GridBagConstraints.EAST;
	    gb.setConstraints(label, c);
	    inputPane.add(label);

	    // place upc field
	    c.gridwidth = GridBagConstraints.REMAINDER;
	    c.insets = new Insets(0, 0, 0, 0);
	    c.anchor = GridBagConstraints.WEST;
	    gb.setConstraints(itemUPC, c);
	    inputPane.add(itemUPC);

	    // when the return key is pressed while in the
	    // purchaseItemID field, the action performed by the ok button
	    // is executed
	    itemUPC.addActionListener(this);
	    itemUPC.setActionCommand("OK");

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
	 * Event handler for the OK button in PurchaseItemDeleteDialog
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
	 * Validates the text fields in PurchaseItemDeleteDialog and then
	 * calls purchaseItem.deletePurchaseItem() if the fields are valid.
	 * Returns the operation status.
	 */ 
	private int validateDelete()
	{
	    try
	    {
	    int receiptID;
		int upc;

		if (piReceiptID.getText().trim().length() != 0)
		{
		    receiptID = Integer.valueOf(piReceiptID.getText().trim()).intValue();
		}
		else
		{
		    return VALIDATIONERROR; 
		}
		
		if (itemUPC.getText().trim().length() != 0)
		{
		    upc = Integer.valueOf(itemUPC.getText().trim()).intValue();

		    // check if purchaseItem tuple exists
		    if (!purchaseItem.findPurchaseItem(receiptID, upc))
		    {
			Toolkit.getDefaultToolkit().beep();
			mvb.updateStatusBar("PurchaseItem with UPC " + upc + " does not exist!");
			return OPERATIONFAILED; 
		    }
		}
		else
		{
		    return VALIDATIONERROR; 
		}
	       
		mvb.updateStatusBar("Deleting purchaseItem...");

		if (purchaseItem.deletePurchaseItem(receiptID, upc))
		{
		    mvb.updateStatusBar("Operation successful.");
		    showAllPurchaseItems();
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
