package ca.ubc.cs304.tables;

// File: LeadSingerController.java

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
 * LeadSingerController is a control class that handles action events 
 * on the shipment Admin menu. It also updates the GUI based on 
 * which menu item the user selected. This class contains the following 
 * inner classes: LeadSingerInsertDialog and 
 * LeadSingerDeleteDialog. LeadSingerInsertDialog is a dialog box that allows a 
 * user to insert a shipment. LeadSingerDeleteDialog is a dialog box 
 * that allows a user to delete a shipment.
 *
 * LeadSingerController implements the ExceptionListener interface which
 * allows it to be notified of any Exceptions that occur in LeadSingerModel
 * (LeadSingerModel contains the database transaction functions). It is defined
 * in LeadSingerModel.java. The ExceptionListener interface is defined in 
 * ExceptionListener.java. When an Exception occurs in LeadSingerModel, 
 * LeadSingerController will update the status text area of MvbView. 
 */
public class LeadSingerController implements ActionListener, ExceptionListener
{
    private MvbView mvb = null;
    private LeadSingerModel leadSinger = null; 

    // constants used for describing the outcome of an operation
    public static final int OPERATIONSUCCESS = 0;
    public static final int OPERATIONFAILED = 1;
    public static final int VALIDATIONERROR = 2; 
    
    public LeadSingerController(MvbView mvb)
    {
	this.mvb = mvb;
	leadSinger = new LeadSingerModel();

	// register to receive exception events from leadSinger
	leadSinger.addExceptionListener(this);
    }


    /*
     * This event handler gets called when the user makes a menu
     * item selection.
     */ 
    public void actionPerformed(ActionEvent e)
    {
	String actionCommand = e.getActionCommand();

	// you cannot use == for string comparisons
	if (actionCommand.equals("Insert LeadSinger"))
	{
	    LeadSingerInsertDialog iDialog = new LeadSingerInsertDialog(mvb);
	    iDialog.pack();
	    mvb.centerWindow(iDialog);
	    iDialog.setVisible(true);
	    return; 
	}
	
	if (actionCommand.equals("Delete LeadSinger"))
	{
	    LeadSingerDeleteDialog dDialog = new LeadSingerDeleteDialog(mvb);
	    dDialog.pack();
	    mvb.centerWindow(dDialog);
	    dDialog.setVisible(true);
	    return; 
	}

	if (actionCommand.equals("Show LeadSinger"))
	{
	    showAllLeadSingers();
	    return; 
	}

	if (actionCommand.equals("Edit LeadSinger"))
	{
	    editAllLeadSingers();
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
     * This method displays all leadSinger tuples in a non-editable JTable
     */
    private void showAllLeadSingers()
    {
	ResultSet rs = leadSinger.showLeadSinger();
	
	// CustomTableModel maintains the result set's data, e.g., if  
	// the result set is updatable, it will update the database
	// when the table's data is modified.  
	CustomTableModel model = new CustomTableModel(leadSinger.getConnection(), rs);
	CustomTable data = new CustomTable(model);

	// register to be notified of any exceptions that occur in the model and table
	model.addExceptionListener(this);
	data.addExceptionListener(this);
	    
	// Adds the table to the scrollpane.
	// By default, a JTable does not have scroll bars.
	mvb.addTable(data);
    }


    /*
     * This method displays all leadSingers in an editable JTable
     */
    private void editAllLeadSingers()
    {
	ResultSet rs = leadSinger.editLeadSinger();
	
	CustomTableModel model = new CustomTableModel(leadSinger.getConnection(), rs);
	CustomTable data = new CustomTable(model);

	model.addExceptionListener(this);
	data.addExceptionListener(this);
	    
	mvb.addTable(data);
    }


    /*
     * This class creates a dialog box for inserting a leadSinger tuple.
     */
    class LeadSingerInsertDialog extends JDialog implements ActionListener
    {
    	
	private JTextField itemUPC = new JTextField(10);
	private JTextField singerName = new JTextField(30);

	
	/*
	 * Constructor. Creates the dialog's GUI.
	 */
	public LeadSingerInsertDialog(JFrame parent)
	{
	    super(parent, "Insert LeadSinger", true);
	    setResizable(false);

	    JPanel contentPane = new JPanel(new BorderLayout());
	    setContentPane(contentPane);
	    contentPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

	    // this panel will contain the text field labels and the text fields.
	    JPanel inputPane = new JPanel();
	    inputPane.setBorder(BorderFactory.createCompoundBorder(
			 new TitledBorder(new EtchedBorder(), "LeadSinger Fields"), 
			 new EmptyBorder(5, 5, 5, 5)));
	 
	    // add the text field labels and text fields to inputPane
	    // using the GridBag layout manager

	    GridBagLayout gb = new GridBagLayout();
	    GridBagConstraints c = new GridBagConstraints();
	    inputPane.setLayout(gb);

	    // create and place item upc label
	    JLabel label = new JLabel("UPC: ", SwingConstants.RIGHT);
	    c.gridwidth = GridBagConstraints.RELATIVE;
	    c.insets = new Insets(5, 0, 0, 5);
	    c.anchor = GridBagConstraints.EAST;
	    gb.setConstraints(label, c);
	    inputPane.add(label);

	    // place item upc field
	    c.gridwidth = GridBagConstraints.REMAINDER;
	    c.insets = new Insets(5, 0, 0, 0);
	    c.anchor = GridBagConstraints.WEST;
	    gb.setConstraints(itemUPC, c);
	    inputPane.add(itemUPC);

	    // create and place lead singer label
	    label = new JLabel("Leading Singer: ", SwingConstants.RIGHT);
	    c.gridwidth = GridBagConstraints.RELATIVE;
	    c.insets = new Insets(5, 0, 0, 5);
	    c.anchor = GridBagConstraints.EAST;
	    gb.setConstraints(label, c);
	    inputPane.add(label);

	    // place lead singer field
	    c.gridwidth = GridBagConstraints.REMAINDER;
	    c.insets = new Insets(5, 0, 0, 0);
	    c.anchor = GridBagConstraints.WEST;
	    gb.setConstraints(singerName, c);
	    inputPane.add(singerName);

	    // when the return key is pressed in the last field
	    // of this form, the action performed by the ok button
	    // is executed
	    singerName.addActionListener(this);
	    singerName.setActionCommand("OK");

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
	 * Event handler for the OK button in LeadSingerInsertDialog
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
	 * Validates the text fields in LeadSingerInsertDialog and then
	 * calls leadSinger.insertLeadSinger() if the fields are valid.
	 * Returns the operation status, which is one of OPERATIONSUCCESS, 
	 * OPERATIONFAILED, VALIDATIONERROR.
	 */ 
	private int validateInsert()
	{
		int upc;
		String name;

		if (itemUPC.getText().trim().length() != 0 && isNumeric(itemUPC.getText().trim()))
		{
		    upc = Integer.valueOf(itemUPC.getText().trim());
		} else {
			return VALIDATIONERROR;
		}

		if (singerName.getText().trim().length() != 0) {
			name = singerName.getText().trim();
			// check for duplicates
		    if (leadSinger.findLeadSinger(upc, name))
		    {
			Toolkit.getDefaultToolkit().beep();
			mvb.updateStatusBar("LeadSinger (" + upc + "," + name + ") already exists!");
			return OPERATIONFAILED; 
		    }
		} else {
			return VALIDATIONERROR;
		}

		mvb.updateStatusBar("Inserting leadSinger...");

		if (leadSinger.insertLeadSinger(upc, name))
		{
		    mvb.updateStatusBar("Operation successful.");
		    showAllLeadSingers();
		    return OPERATIONSUCCESS; 
		}
		else
		{
		    Toolkit.getDefaultToolkit().beep();
		    mvb.updateStatusBar("Operation failed.");
		    return OPERATIONFAILED; 
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
     * This class creates a dialog box for deleting a leadSinger tuple.
     */
    class LeadSingerDeleteDialog extends JDialog implements ActionListener
    {
	private JTextField itemUPC = new JTextField(10);
	private JTextField singerName = new JTextField(30);
	

	/*
	 * Constructor. Creates the dialog's GUI.
	 */
	public LeadSingerDeleteDialog(JFrame parent)
	{
	    super(parent, "Delete LeadSinger", true);
	    setResizable(false);

	    JPanel contentPane = new JPanel(new BorderLayout());
	    setContentPane(contentPane);
	    contentPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

	    // this panel contains the text field labels and the text fields.
	    JPanel inputPane = new JPanel();
	    inputPane.setBorder(BorderFactory.createCompoundBorder(
			 new TitledBorder(new EtchedBorder(), "LeadSinger Fields"), 
			 new EmptyBorder(5, 5, 5, 5)));

	    // add the text field labels and text fields to inputPane
	    // using the GridBag layout manager

	    GridBagLayout gb = new GridBagLayout();
	    GridBagConstraints c = new GridBagConstraints();
	    inputPane.setLayout(gb);

	    // create and place item UPC label
	    JLabel label= new JLabel("Item UPC: ", SwingConstants.RIGHT);	    
	    c.gridwidth = GridBagConstraints.RELATIVE;
	    c.insets = new Insets(0, 0, 0, 5);
	    c.anchor = GridBagConstraints.EAST;
	    gb.setConstraints(label, c);
	    inputPane.add(label);

	    // place item UPC field
	    c.gridwidth = GridBagConstraints.REMAINDER;
	    c.insets = new Insets(0, 0, 0, 0);
	    c.anchor = GridBagConstraints.WEST;
	    gb.setConstraints(itemUPC, c);
	    inputPane.add(itemUPC);
	    
	    // create and place lead singer label
	    label= new JLabel("Lead singer: ", SwingConstants.RIGHT);	    
	    c.gridwidth = GridBagConstraints.RELATIVE;
	    c.insets = new Insets(0, 0, 0, 5);
	    c.anchor = GridBagConstraints.EAST;
	    gb.setConstraints(label, c);
	    inputPane.add(label);

	    // place lead singer field
	    c.gridwidth = GridBagConstraints.REMAINDER;
	    c.insets = new Insets(0, 0, 0, 0);
	    c.anchor = GridBagConstraints.WEST;
	    gb.setConstraints(singerName, c);
	    inputPane.add(singerName);

	    // when the return key is pressed while in the
	    // shipmentID field, the action performed by the ok button
	    // is executed
	    singerName.addActionListener(this);
	    singerName.setActionCommand("OK");

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
	 * Event handler for the OK button in LeadSingerDeleteDialog
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
	 * Validates the text fields in LeadSingerDeleteDialog and then
	 * calls shipment.deleteLeadSinger() if the fields are valid.
	 * Returns the operation status.
	 */ 
	private int validateDelete()
	{
	    try
	    {
		int upc;
		String name;

		if (itemUPC.getText().trim().length() != 0 && isNumeric(itemUPC.getText().trim()))
		{
		    upc = Integer.valueOf(itemUPC.getText().trim()).intValue();
		}
		else
		{
		    return VALIDATIONERROR; 
		}
		
		if (singerName.getText().trim().length() != 0)
		{
			name = singerName.getText().trim();
		}
		else
		{
			return VALIDATIONERROR;
		}
	       
		mvb.updateStatusBar("Deleting shipment...");

		if (leadSinger.deleteLeadSinger(upc, name))
		{
		    mvb.updateStatusBar("Operation successful.");
		    showAllLeadSingers();
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
