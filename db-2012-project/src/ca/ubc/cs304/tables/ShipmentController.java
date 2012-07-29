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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;


/*
 * ShipmentController is a control class that handles action events 
 * on the shipment Admin menu. It also updates the GUI based on 
 * which menu item the user selected. This class contains the following 
 * inner classes: ShipmentInsertDialog, ShipmentUpdateDialog, and 
 * ShipmentDeleteDialog. ShipmentInsertDialog is a dialog box that allows a 
 * user to insert a shipment. ShipmentUpdateDialog is a dialog box that allows 
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

	// register to receive exception events from shipment
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
    	
	private JTextField shipSupName = new JTextField(20);
	private JTextField shipDate = new JTextField(8);

	
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

	    // create and place supplier name label
	    JLabel label = new JLabel("Supplier Name: ", SwingConstants.RIGHT);
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
	    label = new JLabel("Shipment Date (dd-MM-yyyy): ", SwingConstants.RIGHT);
	    c.gridwidth = GridBagConstraints.RELATIVE;
	    c.insets = new Insets(5, 0, 0, 5);
	    c.anchor = GridBagConstraints.EAST;
	    gb.setConstraints(label, c);
	    inputPane.add(label);

	    // place shipment date field
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
	 * Event handler for the OK button in ShipmentInsertDialog
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
	 * calls shipment.insertShipment() if the fields are valid.
	 * Returns the operation status, which is one of OPERATIONSUCCESS, 
	 * OPERATIONFAILED, VALIDATIONERROR.
	 */ 
	private int validateInsert()
	{
		String supName;
		Date sdate;

		if (shipSupName.getText().trim().length() != 0)
		{
		    supName = shipSupName.getText().trim();
		}
		else
		{
			return VALIDATIONERROR; 
		}

		String stringDate = shipDate.getText().trim();
		
		if (stringDate.length() == 10)
		{
			SimpleDateFormat fm = new SimpleDateFormat("dd-MM-yyyy");
			java.util.Date utilDate;
			try {
				utilDate = fm.parse(stringDate);
			} catch (ParseException ex) {
				return VALIDATIONERROR;
			}
			sdate = new java.sql.Date(utilDate.getTime());
		}
		else
		{
		    return VALIDATIONERROR; 
		}

		mvb.updateStatusBar("Inserting shipment...");

		if (shipment.insertShipment(supName, sdate))
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
    }


    /*
     * This class creates a dialog box for updating a shipment.
     */
    class ShipmentUpdateDialog extends JDialog implements ActionListener
    {
	private JTextField shipID = new JTextField(10);
	private JTextField shipSupName = new JTextField(20);
	private JTextField shipDate = new JTextField(8);
	
	/*
	 * Constructor. Creates the dialog's GUI.
	 */
	public ShipmentUpdateDialog(JFrame parent)
	{
	    super(parent, "Update Shipment", true);
	    setResizable(false);

	    JPanel contentPane = new JPanel(new BorderLayout());
	    setContentPane(contentPane);
	    contentPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

	    // this panel contains the text field labels and 
	    // the text fields.
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
	    JLabel label= new JLabel("Shipment ID: ", SwingConstants.RIGHT);	    
	    c.gridwidth = GridBagConstraints.RELATIVE;
	    c.insets = new Insets(0, 0, 0, 5);
	    c.anchor = GridBagConstraints.EAST;
	    gb.setConstraints(label, c);
	    inputPane.add(label);

	    // place shipment id field
	    c.gridwidth = GridBagConstraints.REMAINDER;
	    c.insets = new Insets(0, 0, 0, 0);
	    c.anchor = GridBagConstraints.WEST;
	    gb.setConstraints(shipID, c);
	    inputPane.add(shipID);

	    // create and place shipment supplier name label
	    label = new JLabel("New Shipment Supplier Name: ", SwingConstants.RIGHT);
	    c.gridwidth = GridBagConstraints.RELATIVE;
	    c.insets = new Insets(5, 0, 0, 5);
	    c.anchor = GridBagConstraints.EAST;
	    gb.setConstraints(label, c);
	    inputPane.add(label);

	    // place shipment supplier name field
	    c.gridwidth = GridBagConstraints.REMAINDER;
	    c.insets = new Insets(5, 0, 0, 0);
	    c.anchor = GridBagConstraints.WEST;
	    gb.setConstraints(shipSupName, c);
	    inputPane.add(shipSupName);
	    
	    // create and place shipment date label
	    label = new JLabel("New Shipment Date (dd-MM-yyyy): ", SwingConstants.RIGHT);
	    c.gridwidth = GridBagConstraints.RELATIVE;
	    c.insets = new Insets(5, 0, 0, 5);
	    c.anchor = GridBagConstraints.EAST;
	    gb.setConstraints(label, c);
	    inputPane.add(label);

	    // place shipment name field
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
	 * Event handler for the OK button in ShipmentUpdateDialog
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
	 * Validates the text fields in ShipmentUpdateDialog and then
	 * calls shipment.shipmentUpdate() if the fields are valid.
	 * Returns the operation status.
	 */ 
	private int validateUpdate()
	{
	    try
	    {
		int sid;
		String sname;
		Date sdate;

		if (shipID.getText().trim().length() != 0 && isNumeric(shipID.getText().trim()))
		{
		    sid = Integer.valueOf(shipID.getText().trim()).intValue();

		    // check if shipment exists
		    if (!shipment.findShipment(sid))
		    {
			Toolkit.getDefaultToolkit().beep();
			mvb.updateStatusBar("Shipment " + sid + " does not exist!");
			return OPERATIONFAILED; 
		    }
		}
		else
		{
		    return VALIDATIONERROR; 
		}

		if (shipSupName.getText().trim().length() != 0)
		{
		    sname = shipSupName.getText().trim();
		}
		else
		{
			System.err.println("A");
		    return VALIDATIONERROR;
		}
		
		String stringDate = shipDate.getText().trim();
		
		if (stringDate.length() == 10)
		{
			SimpleDateFormat fm = new SimpleDateFormat("dd-MM-yyyy");
			java.util.Date utilDate;
			try {
				utilDate = fm.parse(stringDate);
			} catch (ParseException ex) {
				System.err.println("B");
				return VALIDATIONERROR;
			}
			sdate = new java.sql.Date(utilDate.getTime());
		}
		else
		{
			System.err.println("C");
		    return VALIDATIONERROR; 
		}
		

		mvb.updateStatusBar("Updating shipment...");

		if (shipment.updateShipment(sid, sname, sdate))
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
     * This class creates a dialog box for deleting a shipment.
     */
    class ShipmentDeleteDialog extends JDialog implements ActionListener
    {
	private JTextField shipID = new JTextField(10);
	

	/*
	 * Constructor. Creates the dialog's GUI.
	 */
	public ShipmentDeleteDialog(JFrame parent)
	{
	    super(parent, "Delete Shipment", true);
	    setResizable(false);

	    JPanel contentPane = new JPanel(new BorderLayout());
	    setContentPane(contentPane);
	    contentPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

	    // this panel contains the text field labels and the text fields.
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
	    JLabel label= new JLabel("Shipment ID: ", SwingConstants.RIGHT);	    
	    c.gridwidth = GridBagConstraints.RELATIVE;
	    c.insets = new Insets(0, 0, 0, 5);
	    c.anchor = GridBagConstraints.EAST;
	    gb.setConstraints(label, c);
	    inputPane.add(label);

	    // place shipment id field
	    c.gridwidth = GridBagConstraints.REMAINDER;
	    c.insets = new Insets(0, 0, 0, 0);
	    c.anchor = GridBagConstraints.WEST;
	    gb.setConstraints(shipID, c);
	    inputPane.add(shipID);

	    // when the return key is pressed while in the
	    // shipmentID field, the action performed by the ok button
	    // is executed
	    shipID.addActionListener(this);
	    shipID.setActionCommand("OK");

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
	 * Event handler for the OK button in ShipmentDeleteDialog
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
	 * Validates the text fields in ShipmentDeleteDialog and then
	 * calls shipment.deleteShipment() if the fields are valid.
	 * Returns the operation status.
	 */ 
	private int validateDelete()
	{
	    try
	    {
		int sid;

		if (shipID.getText().trim().length() != 0 && isNumeric(shipID.getText().trim()))
		{
		    sid = Integer.valueOf(shipID.getText().trim()).intValue();

		    // check if shipment exists
		    if (!shipment.findShipment(sid))
		    {
			Toolkit.getDefaultToolkit().beep();
			mvb.updateStatusBar("Shipment " + sid + " does not exist!");
			return OPERATIONFAILED; 
		    }
		}
		else
		{
		    return VALIDATIONERROR; 
		}
	       
		mvb.updateStatusBar("Deleting shipment...");

		if (shipment.deleteShipment(sid))
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
    
}
