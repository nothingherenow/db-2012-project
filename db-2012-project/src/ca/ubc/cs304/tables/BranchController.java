package ca.ubc.cs304.tables;

// File: BranchController.java

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
 * BranchController is a control class that handles action events 
 * on the Branch Admin menu. It also updates the GUI based on 
 * which menu item the user selected. This class contains the following 
 * inner classes: BranchInsertDialog, BranchUpdateDialog, and 
 * BranchDeleteDialog. BranchInsertDialog is a dialog box that allows a 
 * user to insert a branch. BranchUpdateDialog is a dialog box that allows 
 * a user to update the name of a branch. BranchDeleteDialog is a dialog box 
 * that allows a user to delete a branch.
 *
 * BranchController implements the ExceptionListener interface which
 * allows it to be notified of any Exceptions that occur in BranchModel
 * (BranchModel contains the database transaction functions). It is defined
 * in BranchModel.java. The ExceptionListener interface is defined in 
 * ExceptionListener.java. When an Exception occurs in BranchModel, 
 * BranchController will update the status text area of MvbView. 
 */
public class BranchController implements ActionListener, ExceptionListener
{
    private MvbView mvb = null;
    private BranchModel branch = null; 

    // constants used for describing the outcome of an operation
    public static final int OPERATIONSUCCESS = 0;
    public static final int OPERATIONFAILED = 1;
    public static final int VALIDATIONERROR = 2; 
    
    public BranchController(MvbView mvb)
    {
	this.mvb = mvb;
	branch = new BranchModel();

	// register to receive exception events from branch
	branch.addExceptionListener(this);
    }


    /*
     * This event handler gets called when the user makes a menu
     * item selection.
     */ 
    public void actionPerformed(ActionEvent e)
    {
	String actionCommand = e.getActionCommand();

	// you cannot use == for string comparisons
	if (actionCommand.equals("Insert Branch"))
	{
	    BranchInsertDialog iDialog = new BranchInsertDialog(mvb);
	    iDialog.pack();
	    mvb.centerWindow(iDialog);
	    iDialog.setVisible(true);
	    return; 
	}

	if (actionCommand.equals("Update Branch"))
	{
	    BranchUpdateDialog uDialog = new BranchUpdateDialog(mvb);
	    uDialog.pack();
	    mvb.centerWindow(uDialog);
	    uDialog.setVisible(true);
	    return; 
	}

	if (actionCommand.equals("Delete Branch"))
	{
	    BranchDeleteDialog dDialog = new BranchDeleteDialog(mvb);
	    dDialog.pack();
	    mvb.centerWindow(dDialog);
	    dDialog.setVisible(true);
	    return; 
	}

	if (actionCommand.equals("Show Branch"))
	{
	    showAllBranches();
	    return; 
	}

	if (actionCommand.equals("Edit Branch"))
	{
	    editAllBranches();
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
     * This method displays all branches in a non-editable JTable
     */
    private void showAllBranches()
    {
	ResultSet rs = branch.showBranch();
	
	// CustomTableModel maintains the result set's data, e.g., if  
	// the result set is updatable, it will update the database
	// when the table's data is modified.  
	CustomTableModel model = new CustomTableModel(branch.getConnection(), rs);
	CustomTable data = new CustomTable(model);

	// register to be notified of any exceptions that occur in the model and table
	model.addExceptionListener(this);
	data.addExceptionListener(this);
	    
	// Adds the table to the scrollpane.
	// By default, a JTable does not have scroll bars.
	mvb.addTable(data);
    }


    /*
     * This method displays all branches in an editable JTable
     */
    private void editAllBranches()
    {
	ResultSet rs = branch.editBranch();
	
	CustomTableModel model = new CustomTableModel(branch.getConnection(), rs);
	CustomTable data = new CustomTable(model);

	model.addExceptionListener(this);
	data.addExceptionListener(this);
	    
	mvb.addTable(data);
    }


    /*
     * This class creates a dialog box for inserting a branch.
     */
    class BranchInsertDialog extends JDialog implements ActionListener
    {
	private JTextField branchID = new JTextField(4);
	private JTextField branchName = new JTextField(10);
	private JTextField branchAddr = new JTextField(15);
	private JTextField branchCity = new JTextField(10);
	private JTextField branchPhone = new JTextField(10);

	
	/*
	 * Constructor. Creates the dialog's GUI.
	 */
	public BranchInsertDialog(JFrame parent)
	{
	    super(parent, "Insert Branch", true);
	    setResizable(false);

	    JPanel contentPane = new JPanel(new BorderLayout());
	    setContentPane(contentPane);
	    contentPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

	    // this panel will contain the text field labels and the text fields.
	    JPanel inputPane = new JPanel();
	    inputPane.setBorder(BorderFactory.createCompoundBorder(
			 new TitledBorder(new EtchedBorder(), "Branch Fields"), 
			 new EmptyBorder(5, 5, 5, 5)));
	 
	    // add the text field labels and text fields to inputPane
	    // using the GridBag layout manager

	    GridBagLayout gb = new GridBagLayout();
	    GridBagConstraints c = new GridBagConstraints();
	    inputPane.setLayout(gb);

	    // create and place branch id label
	    JLabel label= new JLabel("Branch ID: ", SwingConstants.RIGHT);	    
	    c.gridwidth = GridBagConstraints.RELATIVE;
	    c.insets = new Insets(0, 0, 0, 5);
	    c.anchor = GridBagConstraints.EAST;
	    gb.setConstraints(label, c);
	    inputPane.add(label);

	    // place branch id field
	    c.gridwidth = GridBagConstraints.REMAINDER;
	    c.insets = new Insets(0, 0, 0, 0);
	    c.anchor = GridBagConstraints.WEST;
	    gb.setConstraints(branchID, c);
	    inputPane.add(branchID);

	    // create and place branch name label
	    label = new JLabel("Branch Name: ", SwingConstants.RIGHT);
	    c.gridwidth = GridBagConstraints.RELATIVE;
	    c.insets = new Insets(5, 0, 0, 5);
	    c.anchor = GridBagConstraints.EAST;
	    gb.setConstraints(label, c);
	    inputPane.add(label);

	    // place branch name field
	    c.gridwidth = GridBagConstraints.REMAINDER;
	    c.insets = new Insets(5, 0, 0, 0);
	    c.anchor = GridBagConstraints.WEST;
	    gb.setConstraints(branchName, c);
	    inputPane.add(branchName);

	    // create and place branch address label
	    label = new JLabel("Branch Address: ", SwingConstants.RIGHT);
	    c.gridwidth = GridBagConstraints.RELATIVE;
	    c.insets = new Insets(5, 0, 0, 5);
	    c.anchor = GridBagConstraints.EAST;
	    gb.setConstraints(label, c);
	    inputPane.add(label);

	    // place branch address field
	    c.gridwidth = GridBagConstraints.REMAINDER;
	    c.insets = new Insets(5, 0, 0, 0);
	    c.anchor = GridBagConstraints.WEST;
	    gb.setConstraints(branchAddr, c);
	    inputPane.add(branchAddr);

	    // create and place branch city label
	    label = new JLabel("Branch City: ", SwingConstants.RIGHT);
	    c.gridwidth = GridBagConstraints.RELATIVE;
	    c.insets = new Insets(5, 0, 0, 5);
	    c.anchor = GridBagConstraints.EAST;
	    gb.setConstraints(label, c);
	    inputPane.add(label);

	    // place branch city field
	    c.gridwidth = GridBagConstraints.REMAINDER;
	    c.insets = new Insets(5, 0, 0, 0);
	    c.anchor = GridBagConstraints.WEST;
	    gb.setConstraints(branchCity, c);
	    inputPane.add(branchCity);

	    // create and place branch phone label
	    label = new JLabel("Branch Phone: ", SwingConstants.RIGHT);
	    c.gridwidth = GridBagConstraints.RELATIVE;
	    c.insets = new Insets(5, 0, 0, 5);
	    c.anchor = GridBagConstraints.EAST;
	    gb.setConstraints(label, c);
	    inputPane.add(label);

	    // place branch phone field
	    c.gridwidth = GridBagConstraints.REMAINDER;
	    c.insets = new Insets(5, 0, 0, 0);
	    c.anchor = GridBagConstraints.WEST;
	    gb.setConstraints(branchPhone, c);
	    inputPane.add(branchPhone);

	    // when the return key is pressed in the last field
	    // of this form, the action performed by the ok button
	    // is executed
	    branchPhone.addActionListener(this);
	    branchPhone.setActionCommand("OK");

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
	 * Event handler for the OK button in BranchInsertDialog
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
	 * Validates the text fields in BranchInsertDialog and then
	 * calls branch.insertBranch() if the fields are valid.
	 * Returns the operation status, which is one of OPERATIONSUCCESS, 
	 * OPERATIONFAILED, VALIDATIONERROR.
	 */ 
	private int validateInsert()
	{
	    try
	    {
		Integer bid;
		String bname;
		String baddr;
		String bcity;
		Integer bphone;

		if (branchID.getText().trim().length() != 0)
		{
		    bid = Integer.valueOf(branchID.getText().trim());

		    // check for duplicates
		    if (branch.findBranch(bid.intValue()))
		    {
			Toolkit.getDefaultToolkit().beep();
			mvb.updateStatusBar("Branch " + bid.toString() + " already exists!");
			return OPERATIONFAILED; 
		    }
		}
		else
		{
		    return VALIDATIONERROR; 
		}

		if (branchName.getText().trim().length() != 0)
		{
		    bname = branchName.getText().trim();
		}
		else
		{
		    return VALIDATIONERROR; 
		}

		if (branchAddr.getText().trim().length() != 0)
		{
		    baddr = branchAddr.getText().trim();
		}
		else
		{
		    baddr = null; 
		}

		if (branchCity.getText().trim().length() != 0)
		{
		    bcity = branchCity.getText().trim();
		}
		else
		{
		    return VALIDATIONERROR; 
		}

		if (branchPhone.getText().trim().length() != 0)
		{
		    bphone = Integer.valueOf(branchPhone.getText().trim());
		}
		else
		{
		    bphone = null; 
		}

		mvb.updateStatusBar("Inserting branch...");

		if (branch.insertBranch(bid, bname, baddr, bcity, bphone))
		{
		    mvb.updateStatusBar("Operation successful.");
		    showAllBranches();
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
    }


    /*
     * This class creates a dialog box for updating a branch.
     */
    class BranchUpdateDialog extends JDialog implements ActionListener
    {
	private JTextField branchID = new JTextField(4);
	private JTextField branchName = new JTextField(10);

	
	/*
	 * Constructor. Creates the dialog's GUI.
	 */
	public BranchUpdateDialog(JFrame parent)
	{
	    super(parent, "Update Branch", true);
	    setResizable(false);

	    JPanel contentPane = new JPanel(new BorderLayout());
	    setContentPane(contentPane);
	    contentPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

	    // this panel contains the text field labels and 
	    // the text fields.
	    JPanel inputPane = new JPanel();
	    inputPane.setBorder(BorderFactory.createCompoundBorder(
			 new TitledBorder(new EtchedBorder(), "Branch Fields"), 
			 new EmptyBorder(5, 5, 5, 5)));
	 
	    // add the text field labels and text fields to inputPane
	    // using the GridBag layout manager

	    GridBagLayout gb = new GridBagLayout();
	    GridBagConstraints c = new GridBagConstraints();
	    inputPane.setLayout(gb);

	    // create and place branch id label
	    JLabel label= new JLabel("Branch ID: ", SwingConstants.RIGHT);	    
	    c.gridwidth = GridBagConstraints.RELATIVE;
	    c.insets = new Insets(0, 0, 0, 5);
	    c.anchor = GridBagConstraints.EAST;
	    gb.setConstraints(label, c);
	    inputPane.add(label);

	    // place branch id field
	    c.gridwidth = GridBagConstraints.REMAINDER;
	    c.insets = new Insets(0, 0, 0, 0);
	    c.anchor = GridBagConstraints.WEST;
	    gb.setConstraints(branchID, c);
	    inputPane.add(branchID);

	    // create and place branch name label
	    label = new JLabel("New Branch Name: ", SwingConstants.RIGHT);
	    c.gridwidth = GridBagConstraints.RELATIVE;
	    c.insets = new Insets(5, 0, 0, 5);
	    c.anchor = GridBagConstraints.EAST;
	    gb.setConstraints(label, c);
	    inputPane.add(label);

	    // place branch name field
	    c.gridwidth = GridBagConstraints.REMAINDER;
	    c.insets = new Insets(5, 0, 0, 0);
	    c.anchor = GridBagConstraints.WEST;
	    gb.setConstraints(branchName, c);
	    inputPane.add(branchName);

	    // when the return key is pressed in the last field
	    // of this form, the action performed by the ok button
	    // is executed
	    branchName.addActionListener(this);
	    branchName.setActionCommand("OK");

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
	 * Event handler for the OK button in BranchUpdateDialog
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
	 * Validates the text fields in BranchUpdateDialog and then
	 * calls branch.branchUpdate() if the fields are valid.
	 * Returns the operation status.
	 */ 
	private int validateUpdate()
	{
	    try
	    {
		int bid;
		String bname;

		if (branchID.getText().trim().length() != 0)
		{
		    bid = Integer.valueOf(branchID.getText().trim()).intValue();

		    // check if branch exists
		    if (!branch.findBranch(bid))
		    {
			Toolkit.getDefaultToolkit().beep();
			mvb.updateStatusBar("Branch " + bid + " does not exist!");
			return OPERATIONFAILED; 
		    }
		}
		else
		{
		    return VALIDATIONERROR; 
		}

		if (branchName.getText().trim().length() != 0)
		{
		    bname = branchName.getText().trim();
		}
		else
		{
		    return VALIDATIONERROR; 
		}

		mvb.updateStatusBar("Updating branch...");

		if (branch.updateBranch(bid, bname))
		{
		    mvb.updateStatusBar("Operation successful.");
		    showAllBranches();
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


    /*
     * This class creates a dialog box for deleting a branch.
     */
    class BranchDeleteDialog extends JDialog implements ActionListener
    {
	private JTextField branchID = new JTextField(4);
	

	/*
	 * Constructor. Creates the dialog's GUI.
	 */
	public BranchDeleteDialog(JFrame parent)
	{
	    super(parent, "Delete Branch", true);
	    setResizable(false);

	    JPanel contentPane = new JPanel(new BorderLayout());
	    setContentPane(contentPane);
	    contentPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

	    // this panel contains the text field labels and the text fields.
	    JPanel inputPane = new JPanel();
	    inputPane.setBorder(BorderFactory.createCompoundBorder(
			 new TitledBorder(new EtchedBorder(), "Branch Fields"), 
			 new EmptyBorder(5, 5, 5, 5)));

	    // add the text field labels and text fields to inputPane
	    // using the GridBag layout manager

	    GridBagLayout gb = new GridBagLayout();
	    GridBagConstraints c = new GridBagConstraints();
	    inputPane.setLayout(gb);

	    // create and place branch id label
	    JLabel label= new JLabel("Branch ID: ", SwingConstants.RIGHT);	    
	    c.gridwidth = GridBagConstraints.RELATIVE;
	    c.insets = new Insets(0, 0, 0, 5);
	    c.anchor = GridBagConstraints.EAST;
	    gb.setConstraints(label, c);
	    inputPane.add(label);

	    // place branch id field
	    c.gridwidth = GridBagConstraints.REMAINDER;
	    c.insets = new Insets(0, 0, 0, 0);
	    c.anchor = GridBagConstraints.WEST;
	    gb.setConstraints(branchID, c);
	    inputPane.add(branchID);

	    // when the return key is pressed while in the
	    // branchID field, the action performed by the ok button
	    // is executed
	    branchID.addActionListener(this);
	    branchID.setActionCommand("OK");

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
	 * Event handler for the OK button in BranchDeleteDialog
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
	 * Validates the text fields in BranchDeleteDialog and then
	 * calls branch.branchDelete() if the fields are valid.
	 * Returns the operation status.
	 */ 
	private int validateDelete()
	{
	    try
	    {
		int bid;

		if (branchID.getText().trim().length() != 0)
		{
		    bid = Integer.valueOf(branchID.getText().trim()).intValue();

		    // check if branch exists
		    if (!branch.findBranch(bid))
		    {
			Toolkit.getDefaultToolkit().beep();
			mvb.updateStatusBar("Branch " + bid + " does not exist!");
			return OPERATIONFAILED; 
		    }
		}
		else
		{
		    return VALIDATIONERROR; 
		}
	       
		mvb.updateStatusBar("Deleting branch...");

		if (branch.deleteBranch(bid))
		{
		    mvb.updateStatusBar("Operation successful.");
		    showAllBranches();
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
