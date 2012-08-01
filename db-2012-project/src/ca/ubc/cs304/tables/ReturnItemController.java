package ca.ubc.cs304.tables;

// File: ReturnItemController.java

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
 * ReturnItemController is a control class that handles action events 
 * on the ReturnItem Admin menu. It also updates the GUI based on 
 * which menu item the user selected. This class contains the following 
 * inner classes: ReturnItemInsertDialog, ReturnItemUpdateDialog, and 
 * ReturnItemDeleteDialog. ReturnItemInsertDialog is a dialog box that allows a 
 * user to insert a item. ReturnItemUpdateDialog is a dialog box that allows 
 * a user to an item. ReturnItemDeleteDialog is a dialog box 
 * that allows a user to delete an item.
 *
 * ReturnItemController implements the ExceptionListener interface which
 * allows it to be notified of any Exceptions that occur in ReturnItemModel
 * (ReturnItemModel contains the database transaction functions). It is defined
 * in ReturnItemModel.java. The ExceptionListener interface is defined in 
 * ExceptionListener.java. When an Exception occurs in ReturnItemModel, 
 * ReturnItemController will update the status text area of MvbView. 
 */
public class ReturnItemController implements ActionListener, ExceptionListener
{
    private MvbView mvb = null;
    private ReturnItemModel returnItem = null; 

    // constants used for describing the outcome of an operation
    public static final int OPERATIONSUCCESS = 0;
    public static final int OPERATIONFAILED = 1;
    public static final int VALIDATIONERROR = 2; 
    
    public ReturnItemController(MvbView mvb)
    {
	this.mvb = mvb;
	returnItem = new ReturnItemModel();

	// register to receive exception events from returnItem
	returnItem.addExceptionListener(this);
    }


    /*
     * This event handler gets called when the user makes a menu
     * item selection.
     */ 
    public void actionPerformed(ActionEvent e)
    {
	String actionCommand = e.getActionCommand();

	// you cannot use == for string comparisons
	if (actionCommand.equals("Insert ReturnItem"))
	{
	    ReturnItemInsertDialog iDialog = new ReturnItemInsertDialog(mvb);
	    iDialog.pack();
	    mvb.centerWindow(iDialog);
	    iDialog.setVisible(true);
	    return; 
	}

	if (actionCommand.equals("Update ReturnItem"))
	{
	    ReturnItemUpdateDialog uDialog = new ReturnItemUpdateDialog(mvb);
	    uDialog.pack();
	    mvb.centerWindow(uDialog);
	    uDialog.setVisible(true);
	    return; 
	}

	if (actionCommand.equals("Delete ReturnItem"))
	{
	    ReturnItemDeleteDialog dDialog = new ReturnItemDeleteDialog(mvb);
	    dDialog.pack();
	    mvb.centerWindow(dDialog);
	    dDialog.setVisible(true);
	    return; 
	}

	if (actionCommand.equals("Show ReturnItem"))
	{
	    showAllReturnItems();
	    return; 
	}

	if (actionCommand.equals("Edit ReturnItem"))
	{
	    editAllReturnItems();
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
     * This method displays all returnItem tuples in a non-editable JTable
     */
    private void showAllReturnItems()
    {
	ResultSet rs = returnItem.showReturnItem();
	
	// CustomTableModel maintains the result set's data, e.g., if  
	// the result set is updatable, it will update the database
	// when the table's data is modified.  
	CustomTableModel model = new CustomTableModel(returnItem.getConnection(), rs);
	CustomTable data = new CustomTable(model);

	// register to be notified of any exceptions that occur in the model and table
	model.addExceptionListener(this);
	data.addExceptionListener(this);
	    
	// Adds the table to the scrollpane.
	// By default, a JTable does not have scroll bars.
	mvb.addTable(data);
    }


    /*
     * This method displays all returnItem tuples in an editable JTable
     */
    private void editAllReturnItems()
    {
	ResultSet rs = returnItem.editReturnItem();
	
	CustomTableModel model = new CustomTableModel(returnItem.getConnection(), rs);
	CustomTable data = new CustomTable(model);

	model.addExceptionListener(this);
	data.addExceptionListener(this);
	    
	mvb.addTable(data);
    }


    /*
     * This class creates a dialog box for inserting a returnItem.
     */
    class ReturnItemInsertDialog extends JDialog implements ActionListener
    {
    private JTextField returnID = new JTextField(10);
	private JTextField itemUPC = new JTextField(10);
	private JTextField itemQuantity = new JTextField(10);
	
	/*
	 * Constructor. Creates the dialog's GUI.
	 */
	public ReturnItemInsertDialog(JFrame parent)
	{
	    super(parent, "Insert ReturnItem", true);
	    setResizable(false);

	    JPanel contentPane = new JPanel(new BorderLayout());
	    setContentPane(contentPane);
	    contentPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

	    // this panel will contain the text field labels and the text fields.
	    JPanel inputPane = new JPanel();
	    inputPane.setBorder(BorderFactory.createCompoundBorder(
			 new TitledBorder(new EtchedBorder(), "ReturnItem Fields"), 
			 new EmptyBorder(5, 5, 5, 5)));
	 
	    // add the text field labels and text fields to inputPane
	    // using the GridBag layout manager

	    GridBagLayout gb = new GridBagLayout();
	    GridBagConstraints c = new GridBagConstraints();
	    inputPane.setLayout(gb);

	    // create and place return id label
	    JLabel label = new JLabel("Return ID: ", SwingConstants.RIGHT);
	    c.gridwidth = GridBagConstraints.RELATIVE;
	    c.insets = new Insets(5, 0, 0, 5);
	    c.anchor = GridBagConstraints.EAST;
	    gb.setConstraints(label, c);
	    inputPane.add(label);

	    // place return id field
	    c.gridwidth = GridBagConstraints.REMAINDER;
	    c.insets = new Insets(5, 0, 0, 0);
	    c.anchor = GridBagConstraints.WEST;
	    gb.setConstraints(returnID, c);
	    inputPane.add(returnID);
	    
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
	 * Event handler for the OK button in ReturnItemInsertDialog
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
	 * Validates the text fields in ReturnItemInsertDialog and then
	 * calls returnItem.insertReturnItem() if the fields are valid.
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

		if (returnID.getText().trim().length() != 0 && isNumeric(returnID.getText().trim()))
		{
		    receiptID = Integer.valueOf(returnID.getText().trim()).intValue();
		} else {
			return VALIDATIONERROR;
		}

		if (itemUPC.getText().trim().length() != 0 && isNumeric(itemUPC.getText().trim()))
		{
		    upc = Integer.valueOf(itemUPC.getText().trim()).intValue();
		 
		    // check for duplicates
		    if (returnItem.findReturnItem(receiptID, upc))
		    {
			Toolkit.getDefaultToolkit().beep();
			mvb.updateStatusBar("ReturnItem (" + receiptID + "," + upc + ") already exists!");
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

		mvb.updateStatusBar("Inserting returnItem...");

		if (returnItem.insertReturnItem(receiptID, upc, quantity))
		{
		    mvb.updateStatusBar("Operation successful.");
		    showAllReturnItems();
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
     * This class creates a dialog box for updating a returnItem.
     */
    class ReturnItemUpdateDialog extends JDialog implements ActionListener
    {
    	private JTextField returnID = new JTextField(10);
    	private JTextField itemUPC = new JTextField(10);
    	private JTextField itemQuantity = new JTextField(10);
	
	/*
	 * Constructor. Creates the dialog's GUI.
	 */
	public ReturnItemUpdateDialog(JFrame parent)
	{
	    super(parent, "Update ReturnItem Quantity", true);
	    setResizable(false);

	    JPanel contentPane = new JPanel(new BorderLayout());
	    setContentPane(contentPane);
	    contentPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

	    // this panel contains the text field labels and 
	    // the text fields.
	    JPanel inputPane = new JPanel();
	    inputPane.setBorder(BorderFactory.createCompoundBorder(
			 new TitledBorder(new EtchedBorder(), "ReturnItem Fields"), 
			 new EmptyBorder(5, 5, 5, 5)));
	 
	    // add the text field labels and text fields to inputPane
	    // using the GridBag layout manager

	    GridBagLayout gb = new GridBagLayout();
	    GridBagConstraints c = new GridBagConstraints();
	    inputPane.setLayout(gb);

	    // create and place return id label
	    JLabel label = new JLabel("Return ID: ", SwingConstants.RIGHT);
	    c.gridwidth = GridBagConstraints.RELATIVE;
	    c.insets = new Insets(5, 0, 0, 5);
	    c.anchor = GridBagConstraints.EAST;
	    gb.setConstraints(label, c);
	    inputPane.add(label);

	    // place return id field
	    c.gridwidth = GridBagConstraints.REMAINDER;
	    c.insets = new Insets(5, 0, 0, 0);
	    c.anchor = GridBagConstraints.WEST;
	    gb.setConstraints(returnID, c);
	    inputPane.add(returnID);
	    
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
	 * Event handler for the OK button in ReturnItemUpdateDialog
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
	 * Validates the text fields in ReturnItemUpdateDialog and then
	 * calls returnItem.updateReturnItem() if the fields are valid.
	 * Returns the operation status.
	 */ 
	private int validateUpdate()
	{
		try
	    {
			int retID;
		    int upc;
		    int quantity;

		if (returnID.getText().trim().length() != 0)
		{
		    retID = Integer.valueOf(returnID.getText().trim()).intValue();
		} else {
			return VALIDATIONERROR;
		}

		if (itemUPC.getText().trim().length() != 0)
		{
		    upc = Integer.valueOf(itemUPC.getText().trim()).intValue();
		 
		    // check if returnItem exists
		    if (!returnItem.findReturnItem(retID, upc))
		    {
		    	Toolkit.getDefaultToolkit().beep();
		    	mvb.updateStatusBar("ReturnItem (" + retID + "," + upc + ") does not exist!");
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
		
		mvb.updateStatusBar("Updating returnItem...");

		if (returnItem.updateReturnItem(retID, upc, quantity))
		{
		    mvb.updateStatusBar("Operation successful.");
		    showAllReturnItems();
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
     * This class creates a dialog box for deleting a returnItem.
     */
    class ReturnItemDeleteDialog extends JDialog implements ActionListener
    {
	private JTextField returnID = new JTextField(10);
	private JTextField itemUPC = new JTextField(10);
	

	/*
	 * Constructor. Creates the dialog's GUI.
	 */
	public ReturnItemDeleteDialog(JFrame parent)
	{
	    super(parent, "Delete ReturnItem", true);
	    setResizable(false);

	    JPanel contentPane = new JPanel(new BorderLayout());
	    setContentPane(contentPane);
	    contentPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

	    // this panel contains the text field labels and the text fields.
	    JPanel inputPane = new JPanel();
	    inputPane.setBorder(BorderFactory.createCompoundBorder(
			 new TitledBorder(new EtchedBorder(), "ReturnItem Fields"), 
			 new EmptyBorder(5, 5, 5, 5)));

	    // add the text field labels and text fields to inputPane
	    // using the GridBag layout manager

	    GridBagLayout gb = new GridBagLayout();
	    GridBagConstraints c = new GridBagConstraints();
	    inputPane.setLayout(gb);

	    // create and place return ID label
	    JLabel label = new JLabel("Return ID: ", SwingConstants.RIGHT);	    
	    c.gridwidth = GridBagConstraints.RELATIVE;
	    c.insets = new Insets(0, 0, 0, 5);
	    c.anchor = GridBagConstraints.EAST;
	    gb.setConstraints(label, c);
	    inputPane.add(label);

	    // place receipt ID field
	    c.gridwidth = GridBagConstraints.REMAINDER;
	    c.insets = new Insets(0, 0, 0, 0);
	    c.anchor = GridBagConstraints.WEST;
	    gb.setConstraints(returnID, c);
	    inputPane.add(returnID);
	    
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
	    // returnItemID field, the action performed by the ok button
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
	 * Event handler for the OK button in ReturnItemDeleteDialog
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
	 * Validates the text fields in ReturnItemDeleteDialog and then
	 * calls returnItem.deleteReturnItem() if the fields are valid.
	 * Returns the operation status.
	 */ 
	private int validateDelete()
	{
	    try
	    {
	    int retID;
		int upc;

		if (returnID.getText().trim().length() != 0 && isNumeric(returnID.getText().trim()))
		{
		    retID = Integer.valueOf(returnID.getText().trim()).intValue();
		}
		else
		{
		    return VALIDATIONERROR; 
		}
		
		if (itemUPC.getText().trim().length() != 0 && isNumeric(itemUPC.getText().trim()))
		{
		    upc = Integer.valueOf(itemUPC.getText().trim()).intValue();

		    // check if returnItem tuple exists
		    if (!returnItem.findReturnItem(retID, upc))
		    {
			Toolkit.getDefaultToolkit().beep();
			mvb.updateStatusBar("ReturnItem (" + retID + "," + upc + ") does not exist!");
			return OPERATIONFAILED; 
		    }
		}
		else
		{
		    return VALIDATIONERROR; 
		}
	       
		mvb.updateStatusBar("Deleting returnItem...");

		if (returnItem.deleteReturnItem(retID, upc))
		{
		    mvb.updateStatusBar("Operation successful.");
		    showAllReturnItems();
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
