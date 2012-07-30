package ca.ubc.cs304.tables;

// File: ShopController.java

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
 * ShopController is a control class that handles action events 
 * on the Customer menu. It also updates the GUI based on 
 * which menu item the user selected. This class contains the following 
 * inner class: ItemSearchDialog. ItemSearchDialog is a dialog box that allows a 
 * user to search for an item.
 *
 * ShopController implements the ExceptionListener interface which
 * allows it to be notified of any Exceptions that occur in ShopTransactions.
 * The ExceptionListener interface is defined in 
 * ExceptionListener.java. When an Exception occurs in ShopTransactions, 
 * ShopController will update the status text area of MvbView. 
 */
public class ShopController implements ActionListener, ExceptionListener
{
    private MvbView mvb;
    private ShopTransactions shop = null;
    private JTable table = null;
    private ResultSet rs = null;

    // constants used for describing the outcome of an operation
    public static final int OPERATIONSUCCESS = 0;
    public static final int OPERATIONFAILED = 1;
    public static final int VALIDATIONERROR = 2; 
    
    public ShopController(MvbView mvb)
    {
	this.mvb = mvb;
	shop = new ShopTransactions();

	// register to receive exception events from customer
	shop.addExceptionListener(this);
    }


    /*
     * This event handler gets called when the user makes a menu
     * item selection.
     */ 
    public void actionPerformed(ActionEvent e)
    {
	String actionCommand = e.getActionCommand();

	// you cannot use == for string comparisons
	if (actionCommand.equals("Item Search"))
	{
	    ItemSearchDialog iDialog = new ItemSearchDialog(mvb);
	    iDialog.pack();
	    mvb.centerWindow(iDialog);
	    iDialog.setVisible(true);
	    return; 
	}

	if (actionCommand.equals("Item Add"))
	{
		QuantityDialog qDialog= new QuantityDialog(mvb, this);
		qDialog.pack();
		mvb.centerWindow(qDialog);
		qDialog.setVisible(true);
		return;
	}
	
	if (actionCommand.equals("Show Cart"))
	{
	    showShoppingCart();
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
     * This method displays all search results in a non-editable JTable
     */
    private void showResults(ResultSet rs)
    {
	// CustomTableModel maintains the result set's data, e.g., if  
	// the result set is updatable, it will update the database
	// when the table's data is modified.  
	CustomTableModel model = new CustomTableModel(shop.getConnection(), rs);
	CustomTable data = new CustomTable(model);
	table = data;

	// register to be notified of any exceptions that occur in the model and table
	model.addExceptionListener(this);
	data.addExceptionListener(this);
	    
	// Adds the table to the scrollpane.
	// By default, a JTable does not have scroll bars.
	mvb.addShoppingTable(data);
    }
    
    /*
     * This method displays the customer's shopping cart in a non-editable JTable
     */
    private void showShoppingCart()
    {
	ResultSet rs = shop.showShoppingCart();
	
	// CustomTableModel maintains the result set's data, e.g., if  
	// the result set is updatable, it will update the database
	// when the table's data is modified.  
	CustomTableModel model = new CustomTableModel(shop.getConnection(), rs);
	CustomTable data = new CustomTable(model);

	// register to be notified of any exceptions that occur in the model and table
	model.addExceptionListener(this);
	data.addExceptionListener(this);
	    
	// Adds the table to the scrollpane.
	// By default, a JTable does not have scroll bars.
	mvb.addTable(data);
    }

    /*
     * This class creates a dialog box for searching an item.
     */
    class ItemSearchDialog extends JDialog implements ActionListener
    {
    private JTextField ititle = new JTextField(60);
	private JTextField icat = new JTextField(12);
	private JTextField ilead = new JTextField(30);
	
	
	/*
	 * Constructor. Creates the dialog's GUI.
	 */
	public ItemSearchDialog(JFrame parent)
	{
	    super(parent, "Search for item", true);
	    setResizable(false);

	    JPanel contentPane = new JPanel(new BorderLayout());
	    setContentPane(contentPane);
	    contentPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

	    // this panel will contain the text field labels and the text fields.
	    JPanel inputPane = new JPanel();
	    inputPane.setBorder(BorderFactory.createCompoundBorder(
			 new TitledBorder(new EtchedBorder(), "Item fields"), 
			 new EmptyBorder(5, 5, 5, 5)));
	 
	    // add the text field labels and text fields to inputPane
	    // using the GridBag layout manager

	    GridBagLayout gb = new GridBagLayout();
	    GridBagConstraints c = new GridBagConstraints();
	    inputPane.setLayout(gb);

	    // create and place item title label
	    JLabel label = new JLabel("Title: ", SwingConstants.RIGHT);
	    c.gridwidth = GridBagConstraints.RELATIVE;
	    c.insets = new Insets(5, 0, 0, 5);
	    c.anchor = GridBagConstraints.EAST;
	    gb.setConstraints(label, c);
	    inputPane.add(label);

	    // place item title field
	    c.gridwidth = GridBagConstraints.REMAINDER;
	    c.insets = new Insets(5, 0, 0, 0);
	    c.anchor = GridBagConstraints.WEST;
	    gb.setConstraints(ititle, c);
	    inputPane.add(ititle);
	    
	    // create and place item category label
	    label = new JLabel("Category: ", SwingConstants.RIGHT);
	    c.gridwidth = GridBagConstraints.RELATIVE;
	    c.insets = new Insets(5, 0, 0, 5);
	    c.anchor = GridBagConstraints.EAST;
	    gb.setConstraints(label, c);
	    inputPane.add(label);

	    // place item category field
	    c.gridwidth = GridBagConstraints.REMAINDER;
	    c.insets = new Insets(5, 0, 0, 0);
	    c.anchor = GridBagConstraints.WEST;
	    gb.setConstraints(icat, c);
	    inputPane.add(icat);

	    // create and place lead singer name label
	    label = new JLabel("Lead singer's name: ", SwingConstants.RIGHT);
	    c.gridwidth = GridBagConstraints.RELATIVE;
	    c.insets = new Insets(5, 0, 0, 5);
	    c.anchor = GridBagConstraints.EAST;
	    gb.setConstraints(label, c);
	    inputPane.add(label);

	    // place lead singer name field
	    c.gridwidth = GridBagConstraints.REMAINDER;
	    c.insets = new Insets(5, 0, 0, 0);
	    c.anchor = GridBagConstraints.WEST;
	    gb.setConstraints(ilead, c);
	    inputPane.add(ilead);

	    // when the return key is pressed in the last field
	    // of this form, the action performed by the ok button
	    // is executed
	    ititle.addActionListener(this);
	    ititle.setActionCommand("OK");
	    icat.addActionListener(this);
	    icat.setActionCommand("OK");
	    ilead.addActionListener(this);
	    ilead.setActionCommand("OK");

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
		if (validateSearch() != VALIDATIONERROR)
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
	 * Validates the text fields in ItemSearchDialog and then
	 * searches for items if the fields are valid.
	 * Returns the operation status, which is one of OPERATIONSUCCESS, 
	 * OPERATIONFAILED, VALIDATIONERROR.
	 */ 
	private int validateSearch()
	{
	    try
	    {
	    String title;
	    String cat;
		String lead;

		if (ititle.getText().trim().length() != 0)
		{
		    title = ititle.getText().trim();
		}
		else
		{
		    title = null;
		}

		if (icat.getText().trim().length() != 0)
		{
			cat = icat.getText().trim();
		    boolean validCat = false;
		    String[] categories = {"rock", "pop", "rap", "country", "classical", "new age" ,"instrumental"};
		    for(String valid: categories) {
		    	if(cat.equals(valid)) {
		    		validCat = true;
		    		break;
		    	}
		    }
		    if(!validCat) return VALIDATIONERROR;
		}
		else
		{
		    cat = null; 
		}

		if (ilead.getText().trim().length() != 0)
		{
		    lead = ilead.getText().trim();
		}
		else
		{
		    lead = null; 
		}

		// Disallow blank searches
		if(title == null && cat == null && lead == null) return VALIDATIONERROR;

		mvb.updateStatusBar("Searching for item...");

		rs = shop.searchItems(title, cat, lead);
		
		mvb.updateStatusBar("Search complete.");
		
		showResults(rs);
		
		return OPERATIONSUCCESS;
		
	    }
	    catch (NumberFormatException ex)
	    {
		// this exception is thrown when a string 
		// cannot be converted to a number
		return VALIDATIONERROR; 
	    }
	}
    }
    /*
     * This class creates a dialog box for adding an item.
     */
    class QuantityDialog extends JDialog implements ActionListener
    {
    private JTextField iquant = new JTextField(6);
	
	
	/*
	 * Constructor. Creates the dialog's GUI.
	 */
	public QuantityDialog(JFrame parent, ShopController control)
	{
	    super(parent, "Add item to shopping cart", true);
	    setResizable(false);
	    
	    JPanel contentPane = new JPanel(new BorderLayout());
	    setContentPane(contentPane);
	    contentPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

	    // this panel will contain the text field labels and the text fields.
	    JPanel inputPane = new JPanel();
	    inputPane.setBorder(BorderFactory.createCompoundBorder(
			 new TitledBorder(new EtchedBorder(), "Item fields"), 
			 new EmptyBorder(5, 5, 5, 5)));
	 
	    // add the text field labels and text fields to inputPane
	    // using the GridBag layout manager

	    GridBagLayout gb = new GridBagLayout();
	    GridBagConstraints c = new GridBagConstraints();
	    inputPane.setLayout(gb);

	    // create and place item quantity label
	    JLabel label = new JLabel("Quantity: ", SwingConstants.RIGHT);
	    c.gridwidth = GridBagConstraints.RELATIVE;
	    c.insets = new Insets(5, 0, 0, 5);
	    c.anchor = GridBagConstraints.EAST;
	    gb.setConstraints(label, c);
	    inputPane.add(label);

	    // place item quantity field
	    c.gridwidth = GridBagConstraints.REMAINDER;
	    c.insets = new Insets(5, 0, 0, 0);
	    c.anchor = GridBagConstraints.WEST;
	    gb.setConstraints(iquant, c);
	    inputPane.add(iquant);

	    // when the return key is pressed in the last field
	    // of this form, the action performed by the ok button
	    // is executed
	    iquant.addActionListener(this);
	    iquant.setActionCommand("OK");

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
		if (validateQuant() != VALIDATIONERROR)
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
	 * Validates the text fields in ItemSearchDialog and then
	 * searches for items if the fields are valid.
	 * Returns the operation status, which is one of OPERATIONSUCCESS, 
	 * OPERATIONFAILED, VALIDATIONERROR.
	 */ 
	private int validateQuant()
	{
	    try
	    {
		int quantity;
		
		if (iquant.getText().trim().length() != 0)
		{
			if(isNumeric(iquant.getText().trim()))
				quantity = Integer.valueOf(iquant.getText().trim()).intValue();
			else return VALIDATIONERROR;
		    if(quantity <= 0)
		    	return VALIDATIONERROR;
		}
		else
		{
		    return VALIDATIONERROR;
		}

		int stock = -1;
		
		try {
			rs.absolute(table.getSelectedRow()+1);
			stock = rs.getInt(5);
		} catch (SQLException e) {
			mvb.updateStatusBar(e.getMessage());
			return VALIDATIONERROR;
		}
		
		int upc = -1;
		
		try {
			rs.absolute(table.getSelectedRow()+1);
			upc = rs.getInt(1);
		} catch (SQLException e) {
			mvb.updateStatusBar(e.getMessage());
			return VALIDATIONERROR;
		}
		
		if(quantity <= stock){
			if(shop.claimItems(upc, quantity))
				return OPERATIONSUCCESS;
			else return OPERATIONFAILED;
		} else {
			int result = JOptionPane.showConfirmDialog(this, "Not enough stock! Buy " + stock + " instead?" ,
					"Not enough stock!", JOptionPane.YES_NO_OPTION);
			if(result == JOptionPane.YES_OPTION) {
				if(shop.claimItems(upc, stock))
					return OPERATIONSUCCESS;
				else return OPERATIONFAILED;
			} else {
				return OPERATIONFAILED;
			}
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
    
}


