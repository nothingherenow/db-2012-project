package ca.ubc.cs304.tables;

// File: ItemController.java

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
 * ItemController is a control class that handles action events 
 * on the Item Admin menu. It also updates the GUI based on 
 * which menu item the user selected. This class contains the following 
 * inner classes: ItemInsertDialog, ItemUpdateDialog, and 
 * ItemDeleteDialog. ItemInsertDialog is a dialog box that allows a 
 * user to insert a item. ItemUpdateDialog is a dialog box that allows 
 * a user to an item. ItemDeleteDialog is a dialog box 
 * that allows a user to delete an item.
 *
 * ItemController implements the ExceptionListener interface which
 * allows it to be notified of any Exceptions that occur in ItemModel
 * (ItemModel contains the database transaction functions). It is defined
 * in ItemModel.java. The ExceptionListener interface is defined in 
 * ExceptionListener.java. When an Exception occurs in ItemModel, 
 * ItemController will update the status text area of MvbView. 
 */
public class ItemController implements ActionListener, ExceptionListener
{
    private MvbView mvb = null;
    private ItemModel item = null; 

    // constants used for describing the outcome of an operation
    public static final int OPERATIONSUCCESS = 0;
    public static final int OPERATIONFAILED = 1;
    public static final int VALIDATIONERROR = 2; 
    
    public ItemController(MvbView mvb)
    {
	this.mvb = mvb;
	item = new ItemModel();

	// register to receive exception events from item
	item.addExceptionListener(this);
    }


    /*
     * This event handler gets called when the user makes a menu
     * item selection.
     */ 
    public void actionPerformed(ActionEvent e)
    {
	String actionCommand = e.getActionCommand();

	// you cannot use == for string comparisons
	if (actionCommand.equals("Insert Item"))
	{
	    ItemInsertDialog iDialog = new ItemInsertDialog(mvb);
	    iDialog.pack();
	    mvb.centerWindow(iDialog);
	    iDialog.setVisible(true);
	    return; 
	}

	if (actionCommand.equals("Update Item"))
	{
	    ItemUpdateDialog uDialog = new ItemUpdateDialog(mvb);
	    uDialog.pack();
	    mvb.centerWindow(uDialog);
	    uDialog.setVisible(true);
	    return; 
	}

	if (actionCommand.equals("Delete Item"))
	{
	    ItemDeleteDialog dDialog = new ItemDeleteDialog(mvb);
	    dDialog.pack();
	    mvb.centerWindow(dDialog);
	    dDialog.setVisible(true);
	    return; 
	}

	if (actionCommand.equals("Show Item"))
	{
	    showAllItems();
	    return; 
	}

	if (actionCommand.equals("Edit Item"))
	{
	    editAllItems();
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
     * This method displays all items in a non-editable JTable
     */
    private void showAllItems()
    {
	ResultSet rs = item.showItem();
	
	// CustomTableModel maintains the result set's data, e.g., if  
	// the result set is updatable, it will update the database
	// when the table's data is modified.  
	CustomTableModel model = new CustomTableModel(item.getConnection(), rs);
	CustomTable data = new CustomTable(model);

	// register to be notified of any exceptions that occur in the model and table
	model.addExceptionListener(this);
	data.addExceptionListener(this);
	    
	// Adds the table to the scrollpane.
	// By default, a JTable does not have scroll bars.
	mvb.addTable(data);
    }


    /*
     * This method displays all items in an editable JTable
     */
    private void editAllItems()
    {
	ResultSet rs = item.editItem();
	
	CustomTableModel model = new CustomTableModel(item.getConnection(), rs);
	CustomTable data = new CustomTable(model);

	model.addExceptionListener(this);
	data.addExceptionListener(this);
	    
	mvb.addTable(data);
    }


    /*
     * This class creates a dialog box for inserting a item.
     */
    class ItemInsertDialog extends JDialog implements ActionListener
    {
    private JTextField itemUPC = new JTextField(10);
	private JTextField title = new JTextField(60);
	private JTextField type = new JTextField(3);
	private JTextField category = new JTextField(60);
	private JTextField stock = new JTextField(10);
	private JTextField company = new JTextField(30);
	private JTextField year = new JTextField(4);
	private JTextField sellPrice = new JTextField(13);
	
	/*
	 * Constructor. Creates the dialog's GUI.
	 */
	public ItemInsertDialog(JFrame parent)
	{
	    super(parent, "Insert Item", true);
	    setResizable(false);

	    JPanel contentPane = new JPanel(new BorderLayout());
	    setContentPane(contentPane);
	    contentPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

	    // this panel will contain the text field labels and the text fields.
	    JPanel inputPane = new JPanel();
	    inputPane.setBorder(BorderFactory.createCompoundBorder(
			 new TitledBorder(new EtchedBorder(), "Item Fields"), 
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

	    // place item UPC field
	    c.gridwidth = GridBagConstraints.REMAINDER;
	    c.insets = new Insets(5, 0, 0, 0);
	    c.anchor = GridBagConstraints.WEST;
	    gb.setConstraints(itemUPC, c);
	    inputPane.add(itemUPC);
	    
	    // create and place item title label
	    label = new JLabel("Title: ", SwingConstants.RIGHT);
	    c.gridwidth = GridBagConstraints.RELATIVE;
	    c.insets = new Insets(5, 0, 0, 5);
	    c.anchor = GridBagConstraints.EAST;
	    gb.setConstraints(label, c);
	    inputPane.add(label);

	    // place item password field
	    c.gridwidth = GridBagConstraints.REMAINDER;
	    c.insets = new Insets(5, 0, 0, 0);
	    c.anchor = GridBagConstraints.WEST;
	    gb.setConstraints(title, c);
	    inputPane.add(title);

	    // create and place item type label
	    label = new JLabel("Type: ", SwingConstants.RIGHT);
	    c.gridwidth = GridBagConstraints.RELATIVE;
	    c.insets = new Insets(5, 0, 0, 5);
	    c.anchor = GridBagConstraints.EAST;
	    gb.setConstraints(label, c);
	    inputPane.add(label);

	    // place item type field
	    c.gridwidth = GridBagConstraints.REMAINDER;
	    c.insets = new Insets(5, 0, 0, 0);
	    c.anchor = GridBagConstraints.WEST;
	    gb.setConstraints(type, c);
	    inputPane.add(type);

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
	    gb.setConstraints(category, c);
	    inputPane.add(category);

	    // create and place item stock label
	    label = new JLabel("Stock: ", SwingConstants.RIGHT);
	    c.gridwidth = GridBagConstraints.RELATIVE;
	    c.insets = new Insets(5, 0, 0, 5);
	    c.anchor = GridBagConstraints.EAST;
	    gb.setConstraints(label, c);
	    inputPane.add(label);

	    // place item stock field
	    c.gridwidth = GridBagConstraints.REMAINDER;
	    c.insets = new Insets(5, 0, 0, 0);
	    c.anchor = GridBagConstraints.WEST;
	    gb.setConstraints(stock, c);
	    inputPane.add(stock);

	    // create and place item company label
	    label = new JLabel("Company: ", SwingConstants.RIGHT);
	    c.gridwidth = GridBagConstraints.RELATIVE;
	    c.insets = new Insets(5, 0, 0, 5);
	    c.anchor = GridBagConstraints.EAST;
	    gb.setConstraints(label, c);
	    inputPane.add(label);

	    // place item stock field
	    c.gridwidth = GridBagConstraints.REMAINDER;
	    c.insets = new Insets(5, 0, 0, 0);
	    c.anchor = GridBagConstraints.WEST;
	    gb.setConstraints(company, c);
	    inputPane.add(company);
	    
	    // create and place item year label
	    label = new JLabel("Year: ", SwingConstants.RIGHT);
	    c.gridwidth = GridBagConstraints.RELATIVE;
	    c.insets = new Insets(5, 0, 0, 5);
	    c.anchor = GridBagConstraints.EAST;
	    gb.setConstraints(label, c);
	    inputPane.add(label);

	    // place item year field
	    c.gridwidth = GridBagConstraints.REMAINDER;
	    c.insets = new Insets(5, 0, 0, 0);
	    c.anchor = GridBagConstraints.WEST;
	    gb.setConstraints(year, c);
	    inputPane.add(year);
	    
	    // create and place item sellPrice label
	    label = new JLabel("Sell price: ", SwingConstants.RIGHT);
	    c.gridwidth = GridBagConstraints.RELATIVE;
	    c.insets = new Insets(5, 0, 0, 5);
	    c.anchor = GridBagConstraints.EAST;
	    gb.setConstraints(label, c);
	    inputPane.add(label);

	    // place item sellPrice field
	    c.gridwidth = GridBagConstraints.REMAINDER;
	    c.insets = new Insets(5, 0, 0, 0);
	    c.anchor = GridBagConstraints.WEST;
	    gb.setConstraints(sellPrice, c);
	    inputPane.add(sellPrice);
	    
	    // when the return key is pressed in the last field
	    // of this form, the action performed by the ok button
	    // is executed
	    sellPrice.addActionListener(this);
	    sellPrice.setActionCommand("OK");

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
	 * Event handler for the OK button in ItemInsertDialog
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
	 * Validates the text fields in ItemInsertDialog and then
	 * calls item.insertItem() if the fields are valid.
	 * Returns the operation status, which is one of OPERATIONSUCCESS, 
	 * OPERATIONFAILED, VALIDATIONERROR.
	 */ 
	private int validateInsert()
	{
	    try
	    {
	    int upc;
	    String tit;
		String typ;
		String cat;
		int stk;
		String comp;
		int yr;
		BigDecimal price;

		if (itemUPC.getText().trim().length() != 0)
		{
		    upc = Integer.valueOf(itemUPC.getText().trim()).intValue();
		    
		    // check for duplicates
		    if (item.findItem(upc))
		    {
			Toolkit.getDefaultToolkit().beep();
			mvb.updateStatusBar("Item with UPC " + upc + " already exists!");
			return OPERATIONFAILED; 
		    }
		} else {
			return VALIDATIONERROR;
		}

		if (title.getText().trim().length() != 0)
		{
		    tit = title.getText().trim();
		}
		else
		{
		    return VALIDATIONERROR; 
		}

		if (type.getText().trim().length() != 0)
		{
		    typ = type.getText().trim();
		    if(!typ.equals("cd") && !typ.equals("dvd"))
		    	return VALIDATIONERROR;
		}
		else
		{
		    return VALIDATIONERROR; 
		}

		if (category.getText().trim().length() != 0)
		{
		    cat = category.getText().trim();
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
		    return VALIDATIONERROR;
		}
		
		if (stock.getText().trim().length() != 0 && isNumeric(stock.getText().trim()))
		{
		    stk = Integer.valueOf(stock.getText().trim()).intValue();
		} else {
			return VALIDATIONERROR;
		}
		
		if (company.getText().trim().length() != 0)
		{
		    comp = company.getText().trim();
		}
		else
		{
		    comp = null; 
		}
		
		if (year.getText().trim().length() == 4 && isNumeric(year.getText().trim()))
		{
		    yr = Integer.valueOf(stock.getText().trim()).intValue();
		} else {
			yr = -1;
		}

		if (sellPrice.getText().trim().length() != 0 && isPrice(sellPrice.getText().trim()))
		{
		    price = BigDecimal.valueOf(Double.valueOf(sellPrice.getText().trim()));
		} else {
			return VALIDATIONERROR;
		}
		
		mvb.updateStatusBar("Inserting item...");

		if (item.insertItem(upc, tit, typ, cat, stk, comp, yr, price))
		{
		    mvb.updateStatusBar("Operation successful.");
		    showAllItems();
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
     * This class creates a dialog box for updating a item.
     */
    class ItemUpdateDialog extends JDialog implements ActionListener
    {
    	private JTextField itemUPC = new JTextField(10);
    	private JTextField title = new JTextField(60);
    	private JTextField type = new JTextField(3);
    	private JTextField category = new JTextField(60);
    	private JTextField stock = new JTextField(10);
    	private JTextField company = new JTextField(30);
    	private JTextField year = new JTextField(4);
    	private JTextField sellPrice = new JTextField(13);
	
	/*
	 * Constructor. Creates the dialog's GUI.
	 */
	public ItemUpdateDialog(JFrame parent)
	{
	    super(parent, "Update Item", true);
	    setResizable(false);

	    JPanel contentPane = new JPanel(new BorderLayout());
	    setContentPane(contentPane);
	    contentPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

	    // this panel contains the text field labels and 
	    // the text fields.
	    JPanel inputPane = new JPanel();
	    inputPane.setBorder(BorderFactory.createCompoundBorder(
			 new TitledBorder(new EtchedBorder(), "Item Fields"), 
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

	    // place item UPC field
	    c.gridwidth = GridBagConstraints.REMAINDER;
	    c.insets = new Insets(5, 0, 0, 0);
	    c.anchor = GridBagConstraints.WEST;
	    gb.setConstraints(itemUPC, c);
	    inputPane.add(itemUPC);
	    
	    // create and place item title label
	    label = new JLabel("New title: ", SwingConstants.RIGHT);
	    c.gridwidth = GridBagConstraints.RELATIVE;
	    c.insets = new Insets(5, 0, 0, 5);
	    c.anchor = GridBagConstraints.EAST;
	    gb.setConstraints(label, c);
	    inputPane.add(label);

	    // place item password field
	    c.gridwidth = GridBagConstraints.REMAINDER;
	    c.insets = new Insets(5, 0, 0, 0);
	    c.anchor = GridBagConstraints.WEST;
	    gb.setConstraints(title, c);
	    inputPane.add(title);

	    // create and place item type label
	    label = new JLabel("New type: ", SwingConstants.RIGHT);
	    c.gridwidth = GridBagConstraints.RELATIVE;
	    c.insets = new Insets(5, 0, 0, 5);
	    c.anchor = GridBagConstraints.EAST;
	    gb.setConstraints(label, c);
	    inputPane.add(label);

	    // place item type field
	    c.gridwidth = GridBagConstraints.REMAINDER;
	    c.insets = new Insets(5, 0, 0, 0);
	    c.anchor = GridBagConstraints.WEST;
	    gb.setConstraints(type, c);
	    inputPane.add(type);

	    // create and place item category label
	    label = new JLabel("New category: ", SwingConstants.RIGHT);
	    c.gridwidth = GridBagConstraints.RELATIVE;
	    c.insets = new Insets(5, 0, 0, 5);
	    c.anchor = GridBagConstraints.EAST;
	    gb.setConstraints(label, c);
	    inputPane.add(label);

	    // place item category field
	    c.gridwidth = GridBagConstraints.REMAINDER;
	    c.insets = new Insets(5, 0, 0, 0);
	    c.anchor = GridBagConstraints.WEST;
	    gb.setConstraints(category, c);
	    inputPane.add(category);

	    // create and place item stock label
	    label = new JLabel("New stock: ", SwingConstants.RIGHT);
	    c.gridwidth = GridBagConstraints.RELATIVE;
	    c.insets = new Insets(5, 0, 0, 5);
	    c.anchor = GridBagConstraints.EAST;
	    gb.setConstraints(label, c);
	    inputPane.add(label);

	    // place item stock field
	    c.gridwidth = GridBagConstraints.REMAINDER;
	    c.insets = new Insets(5, 0, 0, 0);
	    c.anchor = GridBagConstraints.WEST;
	    gb.setConstraints(stock, c);
	    inputPane.add(stock);

	    // create and place item company label
	    label = new JLabel("New company: ", SwingConstants.RIGHT);
	    c.gridwidth = GridBagConstraints.RELATIVE;
	    c.insets = new Insets(5, 0, 0, 5);
	    c.anchor = GridBagConstraints.EAST;
	    gb.setConstraints(label, c);
	    inputPane.add(label);

	    // place item stock field
	    c.gridwidth = GridBagConstraints.REMAINDER;
	    c.insets = new Insets(5, 0, 0, 0);
	    c.anchor = GridBagConstraints.WEST;
	    gb.setConstraints(company, c);
	    inputPane.add(company);
	    
	    // create and place item year label
	    label = new JLabel("New year: ", SwingConstants.RIGHT);
	    c.gridwidth = GridBagConstraints.RELATIVE;
	    c.insets = new Insets(5, 0, 0, 5);
	    c.anchor = GridBagConstraints.EAST;
	    gb.setConstraints(label, c);
	    inputPane.add(label);

	    // place item year field
	    c.gridwidth = GridBagConstraints.REMAINDER;
	    c.insets = new Insets(5, 0, 0, 0);
	    c.anchor = GridBagConstraints.WEST;
	    gb.setConstraints(year, c);
	    inputPane.add(year);
	    
	    // create and place item sellPrice label
	    label = new JLabel("New sell price: ", SwingConstants.RIGHT);
	    c.gridwidth = GridBagConstraints.RELATIVE;
	    c.insets = new Insets(5, 0, 0, 5);
	    c.anchor = GridBagConstraints.EAST;
	    gb.setConstraints(label, c);
	    inputPane.add(label);

	    // place item sellPrice field
	    c.gridwidth = GridBagConstraints.REMAINDER;
	    c.insets = new Insets(5, 0, 0, 0);
	    c.anchor = GridBagConstraints.WEST;
	    gb.setConstraints(sellPrice, c);
	    inputPane.add(sellPrice);
	    
	    // when the return key is pressed in the last field
	    // of this form, the action performed by the ok button
	    // is executed
	    sellPrice.addActionListener(this);
	    sellPrice.setActionCommand("OK");

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
	 * Event handler for the OK button in ItemUpdateDialog
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
	 * Validates the text fields in ItemUpdateDialog and then
	 * calls item.updateItem() if the fields are valid.
	 * Returns the operation status.
	 */ 
	private int validateUpdate()
	{
		try
	    {
	    int upc;
	    String tit;
		String typ;
		String cat;
		int stk;
		String comp;
		int yr;
		BigDecimal price;

		if (itemUPC.getText().trim().length() != 0)
		{
		    upc = Integer.valueOf(itemUPC.getText().trim()).intValue();
		    
		    // check if item exists
		    if (!item.findItem(upc))
		    {
		    	Toolkit.getDefaultToolkit().beep();
		    	mvb.updateStatusBar("Item with UPC " + upc + " does not exist!");
		    	return OPERATIONFAILED; 
		    }
		} else {
			return VALIDATIONERROR;
		}

		if (title.getText().trim().length() != 0)
		{
		    tit = title.getText().trim();
		}
		else
		{
		    return VALIDATIONERROR; 
		}

		if (type.getText().trim().length() != 0)
		{
		    typ = type.getText().trim();
		    if(!typ.equals("cd") && !typ.equals("dvd"))
		    	return VALIDATIONERROR;
		}
		else
		{
		    return VALIDATIONERROR; 
		}

		if (category.getText().trim().length() != 0)
		{
		    cat = category.getText().trim();
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
		    return VALIDATIONERROR;
		}
		
		if (stock.getText().trim().length() != 0 && isNumeric(stock.getText().trim()))
		{
		    stk = Integer.valueOf(stock.getText().trim()).intValue();
		} else {
			return VALIDATIONERROR;
		}
		
		if (company.getText().trim().length() != 0)
		{
		    comp = company.getText().trim();
		}
		else
		{
		    comp = null; 
		}
		
		if (year.getText().trim().length() == 4 && isNumeric(year.getText().trim()))
		{
		    yr = Integer.valueOf(stock.getText().trim()).intValue();
		} else {
			yr = -1;
		}

		if (sellPrice.getText().trim().length() != 0 && isPrice(sellPrice.getText().trim()))
		{
		    price = BigDecimal.valueOf(Double.valueOf(sellPrice.getText().trim()));
		} else {
			return VALIDATIONERROR;
		}
		
		mvb.updateStatusBar("Updating item...");

		if (item.updateItem(upc, tit, typ, cat, stk, comp, yr, price))
		{
		    mvb.updateStatusBar("Operation successful.");
		    showAllItems();
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
     * This class creates a dialog box for deleting a item.
     */
    class ItemDeleteDialog extends JDialog implements ActionListener
    {
	private JTextField itemUPC = new JTextField(10);
	

	/*
	 * Constructor. Creates the dialog's GUI.
	 */
	public ItemDeleteDialog(JFrame parent)
	{
	    super(parent, "Delete Item", true);
	    setResizable(false);

	    JPanel contentPane = new JPanel(new BorderLayout());
	    setContentPane(contentPane);
	    contentPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

	    // this panel contains the text field labels and the text fields.
	    JPanel inputPane = new JPanel();
	    inputPane.setBorder(BorderFactory.createCompoundBorder(
			 new TitledBorder(new EtchedBorder(), "Item Fields"), 
			 new EmptyBorder(5, 5, 5, 5)));

	    // add the text field labels and text fields to inputPane
	    // using the GridBag layout manager

	    GridBagLayout gb = new GridBagLayout();
	    GridBagConstraints c = new GridBagConstraints();
	    inputPane.setLayout(gb);

	    // create and place item id label
	    JLabel label= new JLabel("UPC: ", SwingConstants.RIGHT);	    
	    c.gridwidth = GridBagConstraints.RELATIVE;
	    c.insets = new Insets(0, 0, 0, 5);
	    c.anchor = GridBagConstraints.EAST;
	    gb.setConstraints(label, c);
	    inputPane.add(label);

	    // place item id field
	    c.gridwidth = GridBagConstraints.REMAINDER;
	    c.insets = new Insets(0, 0, 0, 0);
	    c.anchor = GridBagConstraints.WEST;
	    gb.setConstraints(itemUPC, c);
	    inputPane.add(itemUPC);

	    // when the return key is pressed while in the
	    // itemID field, the action performed by the ok button
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
	 * Event handler for the OK button in ItemDeleteDialog
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
	 * Validates the text fields in ItemDeleteDialog and then
	 * calls item.deleteItem() if the fields are valid.
	 * Returns the operation status.
	 */ 
	private int validateDelete()
	{
	    try
	    {
		int upc;

		if (itemUPC.getText().trim().length() != 0)
		{
		    upc = Integer.valueOf(itemUPC.getText().trim()).intValue();

		    // check if item exists
		    if (!item.findItem(upc))
		    {
			Toolkit.getDefaultToolkit().beep();
			mvb.updateStatusBar("Item with UPC " + upc + " does not exist!");
			return OPERATIONFAILED; 
		    }
		}
		else
		{
		    return VALIDATIONERROR; 
		}
	       
		mvb.updateStatusBar("Deleting item...");

		if (item.deleteItem(upc))
		{
		    mvb.updateStatusBar("Operation successful.");
		    showAllItems();
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
