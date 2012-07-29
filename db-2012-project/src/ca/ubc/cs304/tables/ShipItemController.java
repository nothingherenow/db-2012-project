package ca.ubc.cs304.tables;

// File: ShipItemController.java

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
 * ShipItemController is a control class that handles action events 
 * on the ShipItem Admin menu. It also updates the GUI based on 
 * which menu item the user selected. This class contains the following 
 * inner classes: ShipItemInsertDialog, ShipItemUpdateDialog, and 
 * ShipItemDeleteDialog. ShipItemInsertDialog is a dialog box that allows a 
 * user to insert a item. ShipItemUpdateDialog is a dialog box that allows 
 * a user to an item. ShipItemDeleteDialog is a dialog box 
 * that allows a user to delete an item.
 *
 * ShipItemController implements the ExceptionListener interface which
 * allows it to be notified of any Exceptions that occur in ShipItemModel
 * (ShipItemModel contains the database transaction functions). It is defined
 * in ShipItemModel.java. The ExceptionListener interface is defined in 
 * ExceptionListener.java. When an Exception occurs in ShipItemModel, 
 * ShipItemController will update the status text area of MvbView. 
 */
public class ShipItemController implements ActionListener, ExceptionListener
{
    private MvbView mvb = null;
    private ShipItemModel shipItem = null; 

    // constants used for describing the outcome of an operation
    public static final int OPERATIONSUCCESS = 0;
    public static final int OPERATIONFAILED = 1;
    public static final int VALIDATIONERROR = 2; 
    
    public ShipItemController(MvbView mvb)
    {
	this.mvb = mvb;
	shipItem = new ShipItemModel();

	// register to receive exception events from shipItem
	shipItem.addExceptionListener(this);
    }


    /*
     * This event handler gets called when the user makes a menu
     * item selection.
     */ 
    public void actionPerformed(ActionEvent e)
    {
	String actionCommand = e.getActionCommand();

	// you cannot use == for string comparisons
	if (actionCommand.equals("Insert ShipItem"))
	{
	    ShipItemInsertDialog iDialog = new ShipItemInsertDialog(mvb);
	    iDialog.pack();
	    mvb.centerWindow(iDialog);
	    iDialog.setVisible(true);
	    return; 
	}

	if (actionCommand.equals("Update ShipItem"))
	{
	    ShipItemUpdateDialog uDialog = new ShipItemUpdateDialog(mvb);
	    uDialog.pack();
	    mvb.centerWindow(uDialog);
	    uDialog.setVisible(true);
	    return; 
	}

	if (actionCommand.equals("Delete ShipItem"))
	{
	    ShipItemDeleteDialog dDialog = new ShipItemDeleteDialog(mvb);
	    dDialog.pack();
	    mvb.centerWindow(dDialog);
	    dDialog.setVisible(true);
	    return; 
	}

	if (actionCommand.equals("Show ShipItem"))
	{
	    showAllShipItems();
	    return; 
	}

	if (actionCommand.equals("Edit ShipItem"))
	{
	    editAllShipItems();
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
     * This method displays all shipItem tuples in a non-editable JTable
     */
    private void showAllShipItems()
    {
	ResultSet rs = shipItem.showShipItem();
	
	// CustomTableModel maintains the result set's data, e.g., if  
	// the result set is updatable, it will update the database
	// when the table's data is modified.  
	CustomTableModel model = new CustomTableModel(shipItem.getConnection(), rs);
	CustomTable data = new CustomTable(model);

	// register to be notified of any exceptions that occur in the model and table
	model.addExceptionListener(this);
	data.addExceptionListener(this);
	    
	// Adds the table to the scrollpane.
	// By default, a JTable does not have scroll bars.
	mvb.addTable(data);
    }


    /*
     * This method displays all shipItem tuples in an editable JTable
     */
    private void editAllShipItems()
    {
	ResultSet rs = shipItem.editShipItem();
	
	CustomTableModel model = new CustomTableModel(shipItem.getConnection(), rs);
	CustomTable data = new CustomTable(model);

	model.addExceptionListener(this);
	data.addExceptionListener(this);
	    
	mvb.addTable(data);
    }


    /*
     * This class creates a dialog box for inserting a shipItem.
     */
    class ShipItemInsertDialog extends JDialog implements ActionListener
    {
    private JTextField shipID = new JTextField(10);
	private JTextField itemUPC = new JTextField(10);
	private JTextField supPrice = new JTextField(11);
	private JTextField itemQuantity = new JTextField(10);
	
	/*
	 * Constructor. Creates the dialog's GUI.
	 */
	public ShipItemInsertDialog(JFrame parent)
	{
	    super(parent, "Insert ShipItem", true);
	    setResizable(false);

	    JPanel contentPane = new JPanel(new BorderLayout());
	    setContentPane(contentPane);
	    contentPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

	    // this panel will contain the text field labels and the text fields.
	    JPanel inputPane = new JPanel();
	    inputPane.setBorder(BorderFactory.createCompoundBorder(
			 new TitledBorder(new EtchedBorder(), "ShipItem Fields"), 
			 new EmptyBorder(5, 5, 5, 5)));
	 
	    // add the text field labels and text fields to inputPane
	    // using the GridBag layout manager

	    GridBagLayout gb = new GridBagLayout();
	    GridBagConstraints c = new GridBagConstraints();
	    inputPane.setLayout(gb);

	    // create and place shipment id label
	    JLabel label = new JLabel("Ship ID: ", SwingConstants.RIGHT);
	    c.gridwidth = GridBagConstraints.RELATIVE;
	    c.insets = new Insets(5, 0, 0, 5);
	    c.anchor = GridBagConstraints.EAST;
	    gb.setConstraints(label, c);
	    inputPane.add(label);

	    // place shipment id field
	    c.gridwidth = GridBagConstraints.REMAINDER;
	    c.insets = new Insets(5, 0, 0, 0);
	    c.anchor = GridBagConstraints.WEST;
	    gb.setConstraints(shipID, c);
	    inputPane.add(shipID);
	    
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

	 // create and place supplier price label
	    label = new JLabel("Supplier price: ", SwingConstants.RIGHT);
	    c.gridwidth = GridBagConstraints.RELATIVE;
	    c.insets = new Insets(5, 0, 0, 5);
	    c.anchor = GridBagConstraints.EAST;
	    gb.setConstraints(label, c);
	    inputPane.add(label);

	    // place supplier price field
	    c.gridwidth = GridBagConstraints.REMAINDER;
	    c.insets = new Insets(5, 0, 0, 0);
	    c.anchor = GridBagConstraints.WEST;
	    gb.setConstraints(supPrice, c);
	    inputPane.add(supPrice);
	    
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
	 * Event handler for the OK button in ShipItemInsertDialog
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
	 * Validates the text fields in ShipItemInsertDialog and then
	 * calls shipItem.insertShipItem() if the fields are valid.
	 * Ships the operation status, which is one of OPERATIONSUCCESS, 
	 * OPERATIONFAILED, VALIDATIONERROR.
	 */ 
	private int validateInsert()
	{
	    try
	    {
	    int sid;
	    int upc;
	    BigDecimal price;
	    int quantity;

		if (shipID.getText().trim().length() != 0 && isNumeric(shipID.getText().trim()))
		{
		    sid = Integer.valueOf(shipID.getText().trim()).intValue();
		} else {
			return VALIDATIONERROR;
		}

		if (itemUPC.getText().trim().length() != 0 && isNumeric(itemUPC.getText().trim()))
		{
		    upc = Integer.valueOf(itemUPC.getText().trim()).intValue();
		 
		    // check for duplicates
		    if (shipItem.findShipItem(sid, upc))
		    {
			Toolkit.getDefaultToolkit().beep();
			mvb.updateStatusBar("ShipItem (" + sid + "," + upc + ") already exists!");
			return OPERATIONFAILED; 
		    }
		}
		else
		{
		    return VALIDATIONERROR; 
		}

		if(supPrice.getText().trim().length() != 0 && isPrice(supPrice.getText().trim()))
		{
			price = BigDecimal.valueOf(Double.valueOf(supPrice.getText().trim()));
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

		mvb.updateStatusBar("Inserting shipItem...");

		if (shipItem.insertShipItem(sid, upc, price, quantity))
		{
		    mvb.updateStatusBar("Operation successful.");
		    showAllShipItems();
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
     * This class creates a dialog box for updating a shipItem.
     */
    class ShipItemUpdateDialog extends JDialog implements ActionListener
    {
    	private JTextField shipID = new JTextField(10);
    	private JTextField itemUPC = new JTextField(10);
    	private JTextField supPrice = new JTextField(11);
    	private JTextField itemQuantity = new JTextField(10);
	
	/*
	 * Constructor. Creates the dialog's GUI.
	 */
	public ShipItemUpdateDialog(JFrame parent)
	{
	    super(parent, "Update ShipItem Quantity", true);
	    setResizable(false);

	    JPanel contentPane = new JPanel(new BorderLayout());
	    setContentPane(contentPane);
	    contentPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

	    // this panel contains the text field labels and 
	    // the text fields.
	    JPanel inputPane = new JPanel();
	    inputPane.setBorder(BorderFactory.createCompoundBorder(
			 new TitledBorder(new EtchedBorder(), "ShipItem Fields"), 
			 new EmptyBorder(5, 5, 5, 5)));
	 
	    // add the text field labels and text fields to inputPane
	    // using the GridBag layout manager

	    GridBagLayout gb = new GridBagLayout();
	    GridBagConstraints c = new GridBagConstraints();
	    inputPane.setLayout(gb);

	    // create and place shipment id label
	    JLabel label = new JLabel("Ship ID: ", SwingConstants.RIGHT);
	    c.gridwidth = GridBagConstraints.RELATIVE;
	    c.insets = new Insets(5, 0, 0, 5);
	    c.anchor = GridBagConstraints.EAST;
	    gb.setConstraints(label, c);
	    inputPane.add(label);

	    // place shipment id field
	    c.gridwidth = GridBagConstraints.REMAINDER;
	    c.insets = new Insets(5, 0, 0, 0);
	    c.anchor = GridBagConstraints.WEST;
	    gb.setConstraints(shipID, c);
	    inputPane.add(shipID);
	    
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

	    // create and place supplier price label
	    label = new JLabel("New supplier price: ", SwingConstants.RIGHT);
	    c.gridwidth = GridBagConstraints.RELATIVE;
	    c.insets = new Insets(5, 0, 0, 5);
	    c.anchor = GridBagConstraints.EAST;
	    gb.setConstraints(label, c);
	    inputPane.add(label);

	    // place supplier price field
	    c.gridwidth = GridBagConstraints.REMAINDER;
	    c.insets = new Insets(5, 0, 0, 0);
	    c.anchor = GridBagConstraints.WEST;
	    gb.setConstraints(supPrice, c);
	    inputPane.add(supPrice);
	    
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
	 * Event handler for the OK button in ShipItemUpdateDialog
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
	 * Validates the text fields in ShipItemUpdateDialog and then
	 * calls shipItem.updateShipItem() if the fields are valid.
	 * Ships the operation status.
	 */ 
	private int validateUpdate()
	{
		try
	    {
			int sid;
		    int upc;
		    BigDecimal price;
		    int quantity;

		if (shipID.getText().trim().length() != 0)
		{
		    sid = Integer.valueOf(shipID.getText().trim()).intValue();
		} else {
			return VALIDATIONERROR;
		}

		if (itemUPC.getText().trim().length() != 0)
		{
		    upc = Integer.valueOf(itemUPC.getText().trim()).intValue();
		 
		    // check if shipItem exists
		    if (!shipItem.findShipItem(sid, upc))
		    {
		    	Toolkit.getDefaultToolkit().beep();
		    	mvb.updateStatusBar("ShipItem (" + sid + "," + upc + ") does not exist!");
		    	return OPERATIONFAILED; 
		    }
		}
		else
		{
		    return VALIDATIONERROR; 
		}

		if(supPrice.getText().trim().length() != 0 && isPrice(supPrice.getText().trim()))
		{
			price = BigDecimal.valueOf(Double.valueOf(supPrice.getText().trim()));
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
		
		mvb.updateStatusBar("Updating shipItem...");

		if (shipItem.updateShipItem(sid, upc, price, quantity))
		{
		    mvb.updateStatusBar("Operation successful.");
		    showAllShipItems();
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
     * This class creates a dialog box for deleting a shipItem.
     */
    class ShipItemDeleteDialog extends JDialog implements ActionListener
    {
	private JTextField shipID = new JTextField(10);
	private JTextField itemUPC = new JTextField(10);
	

	/*
	 * Constructor. Creates the dialog's GUI.
	 */
	public ShipItemDeleteDialog(JFrame parent)
	{
	    super(parent, "Delete ShipItem", true);
	    setResizable(false);

	    JPanel contentPane = new JPanel(new BorderLayout());
	    setContentPane(contentPane);
	    contentPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

	    // this panel contains the text field labels and the text fields.
	    JPanel inputPane = new JPanel();
	    inputPane.setBorder(BorderFactory.createCompoundBorder(
			 new TitledBorder(new EtchedBorder(), "ShipItem Fields"), 
			 new EmptyBorder(5, 5, 5, 5)));

	    // add the text field labels and text fields to inputPane
	    // using the GridBag layout manager

	    GridBagLayout gb = new GridBagLayout();
	    GridBagConstraints c = new GridBagConstraints();
	    inputPane.setLayout(gb);

	    // create and place shipment ID label
	    JLabel label = new JLabel("Shipment ID: ", SwingConstants.RIGHT);	    
	    c.gridwidth = GridBagConstraints.RELATIVE;
	    c.insets = new Insets(0, 0, 0, 5);
	    c.anchor = GridBagConstraints.EAST;
	    gb.setConstraints(label, c);
	    inputPane.add(label);

	    // place shipment ID field
	    c.gridwidth = GridBagConstraints.REMAINDER;
	    c.insets = new Insets(0, 0, 0, 0);
	    c.anchor = GridBagConstraints.WEST;
	    gb.setConstraints(shipID, c);
	    inputPane.add(shipID);
	    
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
	    // shipItemID field, the action performed by the ok button
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
	 * Event handler for the OK button in ShipItemDeleteDialog
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
	 * Validates the text fields in ShipItemDeleteDialog and then
	 * calls shipItem.deleteShipItem() if the fields are valid.
	 * Ships the operation status.
	 */ 
	private int validateDelete()
	{
	    try
	    {
	    int sid;
		int upc;

		if (shipID.getText().trim().length() != 0 && isNumeric(shipID.getText().trim()))
		{
		    sid = Integer.valueOf(shipID.getText().trim()).intValue();
		}
		else
		{
		    return VALIDATIONERROR; 
		}
		
		if (itemUPC.getText().trim().length() != 0 && isNumeric(itemUPC.getText().trim()))
		{
		    upc = Integer.valueOf(itemUPC.getText().trim()).intValue();

		    // check if shipItem tuple exists
		    if (!shipItem.findShipItem(sid, upc))
		    {
			Toolkit.getDefaultToolkit().beep();
			mvb.updateStatusBar("ShipItem (" + sid + "," + upc + ") does not exist!");
			return OPERATIONFAILED; 
		    }
		}
		else
		{
		    return VALIDATIONERROR; 
		}
	       
		mvb.updateStatusBar("Deleting shipItem...");

		if (shipItem.deleteShipItem(sid, upc))
		{
		    mvb.updateStatusBar("Operation successful.");
		    showAllShipItems();
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
