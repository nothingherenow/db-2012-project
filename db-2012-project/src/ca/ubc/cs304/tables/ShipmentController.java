package ca.ubc.cs304.tables;

// File: ShipmentController.java

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*; 

import ca.ubc.cs304.main.CustomTable;
import ca.ubc.cs304.main.CustomTableModel;
import ca.ubc.cs304.main.ExceptionEvent;
import ca.ubc.cs304.main.ExceptionListener;
import ca.ubc.cs304.main.MvbView;

import java.sql.*;


/*
 * ShipmentController is a control class that handles action events 
 * on the shipment Admin menu. It also updates the GUI based on 
 * which menu item the user selected. This class contains the following 
 * inner classes: ShipmentInsertDialog, ShipmentUpdateDialog, and 
 * ShipmentDeleteDialog. ShipmentInsertDialog is a dialog box that allows a 
 * user to insert a customer. ShipmentUpdateDialog is a dialog box that allows 
 * a user to update the name of a shipment. ShipmentDeleteDialog is a dialog box 
 * that allows a user to delete a shipment.
 *
 * ShipmentController implements the ExceptionListener interface which
 * allows it to be notified of any Exceptions that occur in ShipmentModel
 * (ShipmentModel contains the database transaction functions). It is defined
 * in Shipment.java. The ExceptionListener interface is defined in 
 * ShipmentListener.java. When an Exception occurs in ShipmentModel, 
 * ShipmentController will update the status text area of MvbView. 
 */
public class ShipmentController implements ActionListener, ExceptionListener
{
    private MvbView mvb = null;
    private ShipmentModel shipment = null; 

    // constants used for describing the outcome of an operation
    public static final int OPERATIONSUCCESS = 0;
    public static final int OPERATIONFAILED = 1;
    public static final int VALIDATIONERROR = 2; 
    
    public ShipmentController(MvbView mvb)
    {
	this.mvb = mvb;
	shipment = new ShipmentModel();

	// register to receive exception events from customer
	shipment.addExceptionListener(this);
    }


    /*
     * This event handler gets called when the user makes a menu
     * item selection.
     */ 
    public void actionPerformed(ActionEvent e)
    {
	String actionCommand = e.getActionCommand();

	// you cannot use == for string comparisons
	if (actionCommand.equals("Insert Shipment"))
	{
	    ShipmentInsertDialog iDialog = new ShipmentInsertDialog(mvb);
	    iDialog.pack();
	    mvb.centerWindow(iDialog);
	    iDialog.setVisible(true);
	    return; 
	}

	if (actionCommand.equals("Update Shipment"))
	{
	    ShipmentUpdateDialog uDialog = new ShipmentUpdateDialog(mvb);
	    uDialog.pack();
	    mvb.centerWindow(uDialog);
	    uDialog.setVisible(true);
	    return; 
	}
	

	if (actionCommand.equals("Delete Shipment"))
	{
	    ShipmentDeleteDialog dDialog = new ShipmentDeleteDialog(mvb);
	    dDialog.pack();
	    mvb.centerWindow(dDialog);
	    dDialog.setVisible(true);
	    return; 
	}

	if (actionCommand.equals("Show Shipment"))
	{
	    showAllShipments();
	    return; 
	}

	if (actionCommand.equals("Edit Shipment"))
	{
	    editAllShipments();
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
     * This method displays all shipments in a non-editable JTable
     */
    private void showAllShipments()
    {
	ResultSet rs = shipment.showShipment();
	
	// CustomTableModel maintains the result set's data, e.g., if  
	// the result set is updatable, it will update the database
	// when the table's data is modified.  
	CustomTableModel model = new CustomTableModel(shipment.getConnection(), rs);
	CustomTable data = new CustomTable(model);

	// register to be notified of any exceptions that occur in the model and table
	model.addExceptionListener(this);
	data.addExceptionListener(this);
	    
	// Adds the table to the scrollpane.
	// By default, a JTable does not have scroll bars.
	mvb.addTable(data);
    }


    /*
     * This method displays all shipments in an editable JTable
     */
    private void editAllShipments()
    {
	ResultSet rs = shipment.editShipment();
	
	CustomTableModel model = new CustomTableModel(shipment.getConnection(), rs);
	CustomTable data = new CustomTable(model);

	model.addExceptionListener(this);
	data.addExceptionListener(this);
	    
	mvb.addTable(data);
    }


    /*
     * This class creates a dialog box for inserting a shipment.
     */
    class ShipmentInsertDialog extends JDialog implements ActionListener
    {
	
	private JTextField shipId = new JTextField(10);
	private JTextField shipSupName = new JTextField(20);
	private JTextField shipDate = new JTextField(10);

	
	/*
	 * Constructor. Creates the dialog's GUI.
	 */
	public ShipmentInsertDialog(JFrame parent)
	{
	    super(parent, "Insert Shipment", true);
	    setResizable(false);

	    JPanel contentPane = new JPanel(new BorderLayout());
	    setContentPane(contentPane);
	    contentPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

	    // this panel will contain the text field labels and the text fields.
	    JPanel inputPane = new JPanel();
	    inputPane.setBorder(BorderFactory.createCompoundBorder(
			 new TitledBorder(new EtchedBorder(), "Shipment Fields"), 
			 new EmptyBorder(5, 5, 5, 5)));
	 
	    // add the text field labels and text fields to inputPane
	    // using the GridBag layout manager

	    GridBagLayout gb = new GridBagLayout();
	    GridBagConstraints c = new GridBagConstraints();
	    inputPane.setLayout(gb);

	    // create and place shipment id label
	    JLabel label = new JLabel("Shipment ID: ", SwingConstants.RIGHT);
	    c.gridwidth = GridBagConstraints.RELATIVE;
	    c.insets = new Insets(5, 0, 0, 5);
	    c.anchor = GridBagConstraints.EAST;
	    gb.setConstraints(label, c);
	    inputPane.add(label);

	    // place shipment id field
	    c.gridwidth = GridBagConstraints.REMAINDER;
	    c.insets = new Insets(5, 0, 0, 0);
	    c.anchor = GridBagConstraints.WEST;
	    gb.setConstraints(shipId, c);
	    inputPane.add(shipId);

	    // create and place supplier name label
	    label = new JLabel("Supplier Name: ", SwingConstants.RIGHT);
	    c.gridwidth = GridBagConstraints.RELATIVE;
	    c.insets = new Insets(5, 0, 0, 5);
	    c.anchor = GridBagConstraints.EAST;
	    gb.setConstraints(label, c);
	    inputPane.add(label);

	    // place supplier name field
	    c.gridwidth = GridBagConstraints.REMAINDER;
	    c.insets = new Insets(5, 0, 0, 0);
	    c.anchor = GridBagConstraints.WEST;
	    gb.setConstraints(shipSupName, c);
	    inputPane.add(shipSupName);

	    // create and place shipment date label
	    label = new JLabel("Shipment Date: ", SwingConstants.RIGHT);
	    c.gridwidth = GridBagConstraints.RELATIVE;
	    c.insets = new Insets(5, 0, 0, 5);
	    c.anchor = GridBagConstraints.EAST;
	    gb.setConstraints(label, c);
	    inputPane.add(label);

	    // place customer phone field
	    c.gridwidth = GridBagConstraints.REMAINDER;
	    c.insets = new Insets(5, 0, 0, 0);
	    c.anchor = GridBagConstraints.WEST;
	    gb.setConstraints(shipDate, c);
	    inputPane.add(shipDate);

	    // when the return key is pressed in the last field
	    // of this form, the action performed by the ok button
	    // is executed
	    shipDate.addActionListener(this);
	    shipDate.setActionCommand("OK");

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
	 * Event handler for the OK button in CustomerInsertDialog
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
	 * Validates the text fields in ShipmentInsertDialog and then
	 * calls shipment.insertCustomer() if the fields are valid.
	 * Returns the operation status, which is one of OPERATIONSUCCESS, 
	 * OPERATIONFAILED, VALIDATIONERROR.
	 */ 
	private int validateInsert()
	{
	    try
	    {
		Integer sid;
		String supName;
		Date sdate;

		if (shipId.getText().trim().length() != 0)
		{
		    sid = Integer.valueOf(shipId.getText().trim());

		    // check for duplicates
		    if (shipment.findShipment(sid.intValue()))
		    {
			Toolkit.getDefaultToolkit().beep();
			mvb.updateStatusBar("Shipment ID " + sid.toString() + " already exists!");
			return OPERATIONFAILED; 
		    }
		}
		else
		{
			return VALIDATIONERROR; 
		}

		if (shipSupName.getText().trim().length() != 0)
		{
		    supName = shipSupName.getText().trim();
		}
		else
		{
			return VALIDATIONERROR; 
		}

		if (shipDate.getText().trim().length() != 0 && isNumeric(shipDate.getText()))
		{
		    sdate = shipDate.getText().trim();
		}
		else
		{
		    sdate = null; 
		}

		mvb.updateStatusBar("Inserting shipment...");

		if (shipment.insertShipment(sid, supName, sdate))
		{
		    mvb.updateStatusBar("Operation successful.");
		    showAllShipments();
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
     * This class creates a dialog box for updating a customer.
     */
    class ShipmentUpdateDialog extends JDialog implements ActionListener
    {
	private JTextField custID = new JTextField(4);
	private JTextField custName = new JTextField(40);
	private JTextField custAddr = new JTextField(60);
	private JTextField custPhone = new JTextField(10);
	
	/*
	 * Constructor. Creates the dialog's GUI.
	 */
	public ShipmentUpdateDialog(JFrame parent)
	{
	    super(parent, "Update Customer", true);
	    setResizable(false);

	    JPanel contentPane = new JPanel(new BorderLayout());
	    setContentPane(contentPane);
	    contentPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

	    // this panel contains the text field labels and 
	    // the text fields.
	    JPanel inputPane = new JPanel();
	    inputPane.setBorder(BorderFactory.createCompoundBorder(
			 new TitledBorder(new EtchedBorder(), "Customer Fields"), 
			 new EmptyBorder(5, 5, 5, 5)));
	 
	    // add the text field labels and text fields to inputPane
	    // using the GridBag layout manager

	    GridBagLayout gb = new GridBagLayout();
	    GridBagConstraints c = new GridBagConstraints();
	    inputPane.setLayout(gb);

	    // create and place customer id label
	    JLabel label= new JLabel("Customer ID: ", SwingConstants.RIGHT);	    
	    c.gridwidth = GridBagConstraints.RELATIVE;
	    c.insets = new Insets(0, 0, 0, 5);
	    c.anchor = GridBagConstraints.EAST;
	    gb.setConstraints(label, c);
	    inputPane.add(label);

	    // place customer id field
	    c.gridwidth = GridBagConstraints.REMAINDER;
	    c.insets = new Insets(0, 0, 0, 0);
	    c.anchor = GridBagConstraints.WEST;
	    gb.setConstraints(custID, c);
	    inputPane.add(custID);

	    // create and place customer name label
	    label = new JLabel("New Customer Name: ", SwingConstants.RIGHT);
	    c.gridwidth = GridBagConstraints.RELATIVE;
	    c.insets = new Insets(5, 0, 0, 5);
	    c.anchor = GridBagConstraints.EAST;
	    gb.setConstraints(label, c);
	    inputPane.add(label);

	    // place customer name field
	    c.gridwidth = GridBagConstraints.REMAINDER;
	    c.insets = new Insets(5, 0, 0, 0);
	    c.anchor = GridBagConstraints.WEST;
	    gb.setConstraints(custName, c);
	    inputPane.add(custName);

	    // create and place customer address label
	    label = new JLabel("New Customer Address: ", SwingConstants.RIGHT);
	    c.gridwidth = GridBagConstraints.RELATIVE;
	    c.insets = new Insets(5, 0, 0, 5);
	    c.anchor = GridBagConstraints.EAST;
	    gb.setConstraints(label, c);
	    inputPane.add(label);

	    // place customer address field
	    c.gridwidth = GridBagConstraints.REMAINDER;
	    c.insets = new Insets(5, 0, 0, 0);
	    c.anchor = GridBagConstraints.WEST;
	    gb.setConstraints(custAddr, c);
	    inputPane.add(custAddr);
	    
	    // create and place customer phone label
	    label = new JLabel("New Customer Phone: ", SwingConstants.RIGHT);
	    c.gridwidth = GridBagConstraints.RELATIVE;
	    c.insets = new Insets(5, 0, 0, 5);
	    c.anchor = GridBagConstraints.EAST;
	    gb.setConstraints(label, c);
	    inputPane.add(label);

	    // place customer name field
	    c.gridwidth = GridBagConstraints.REMAINDER;
	    c.insets = new Insets(5, 0, 0, 0);
	    c.anchor = GridBagConstraints.WEST;
	    gb.setConstraints(custPhone, c);
	    inputPane.add(custPhone);
	    
	    // when the return key is pressed in the last field
	    // of this form, the action performed by the ok button
	    // is executed
	    custPhone.addActionListener(this);
	    custPhone.setActionCommand("OK");

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
	 * Event handler for the OK button in CustomerUpdateDialog
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
	 * Validates the text fields in CustomerUpdateDialog and then
	 * calls customer.customerUpdate() if the fields are valid.
	 * Returns the operation status.
	 */ 
	private int validateUpdate()
	{
	    try
	    {
		int cid;
		String cname;
		String caddr;
		String cphone;

		if (custID.getText().trim().length() != 0)
		{
		    cid = Integer.valueOf(custID.getText().trim()).intValue();

		    // check if customer exists
		    if (!shipment.findCustomer(cid))
		    {
			Toolkit.getDefaultToolkit().beep();
			mvb.updateStatusBar("Customer " + cid + " does not exist!");
			return OPERATIONFAILED; 
		    }
		}
		else
		{
		    return VALIDATIONERROR; 
		}

		if (custName.getText().trim().length() != 0)
		{
		    cname = custName.getText().trim();
		}
		else
		{
		    cname = null; 
		}
		
		if (custAddr.getText().trim().length() != 0)
		{
		    caddr = custAddr.getText().trim();
		}
		else
		{
		    caddr = null; 
		}

		if (custPhone.getText().trim().length() != 0 && isNumeric(custPhone.getText()))
		{
		    cphone = custPhone.getText().trim();
		}
		else
		{
		    cphone = null; 
		}
		

		mvb.updateStatusBar("Updating customer...");

		if (shipment.updateCustomer(cid, cname, caddr, cphone))
		{
		    mvb.updateStatusBar("Operation successful.");
		    showAllShipments();
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


    /*
     * This class creates a dialog box for deleting a customer.
     */
    class ShipmentDeleteDialog extends JDialog implements ActionListener
    {
	private JTextField custID = new JTextField(4);
	

	/*
	 * Constructor. Creates the dialog's GUI.
	 */
	public ShipmentDeleteDialog(JFrame parent)
	{
	    super(parent, "Delete Customer", true);
	    setResizable(false);

	    JPanel contentPane = new JPanel(new BorderLayout());
	    setContentPane(contentPane);
	    contentPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

	    // this panel contains the text field labels and the text fields.
	    JPanel inputPane = new JPanel();
	    inputPane.setBorder(BorderFactory.createCompoundBorder(
			 new TitledBorder(new EtchedBorder(), "Customer Fields"), 
			 new EmptyBorder(5, 5, 5, 5)));

	    // add the text field labels and text fields to inputPane
	    // using the GridBag layout manager

	    GridBagLayout gb = new GridBagLayout();
	    GridBagConstraints c = new GridBagConstraints();
	    inputPane.setLayout(gb);

	    // create and place customer id label
	    JLabel label= new JLabel("Customer ID: ", SwingConstants.RIGHT);	    
	    c.gridwidth = GridBagConstraints.RELATIVE;
	    c.insets = new Insets(0, 0, 0, 5);
	    c.anchor = GridBagConstraints.EAST;
	    gb.setConstraints(label, c);
	    inputPane.add(label);

	    // place customer id field
	    c.gridwidth = GridBagConstraints.REMAINDER;
	    c.insets = new Insets(0, 0, 0, 0);
	    c.anchor = GridBagConstraints.WEST;
	    gb.setConstraints(custID, c);
	    inputPane.add(custID);

	    // when the return key is pressed while in the
	    // customerID field, the action performed by the ok button
	    // is executed
	    custID.addActionListener(this);
	    custID.setActionCommand("OK");

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
	 * Event handler for the OK button in CustomerDeleteDialog
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
	 * Validates the text fields in CustomerDeleteDialog and then
	 * calls customer.deleteCustomer() if the fields are valid.
	 * Returns the operation status.
	 */ 
	private int validateDelete()
	{
	    try
	    {
		int cid;

		if (custID.getText().trim().length() != 0)
		{
		    cid = Integer.valueOf(custID.getText().trim()).intValue();

		    // check if customer exists
		    if (!shipment.findCustomer(cid))
		    {
			Toolkit.getDefaultToolkit().beep();
			mvb.updateStatusBar("Customer " + cid + " does not exist!");
			return OPERATIONFAILED; 
		    }
		}
		else
		{
		    return VALIDATIONERROR; 
		}
	       
		mvb.updateStatusBar("Deleting customer...");

		if (shipment.deleteCustomer(cid))
		{
		    mvb.updateStatusBar("Operation successful.");
		    showAllShipments();
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
