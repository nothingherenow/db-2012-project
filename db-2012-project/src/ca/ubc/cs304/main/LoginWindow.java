package ca.ubc.cs304.main;

// File: LoginWindow.java

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import ca.ubc.cs304.tables.CustomerModel;

import java.awt.*;
import java.awt.event.*;


/*
 * The login window
 */ 
public class LoginWindow extends JDialog implements ActionListener, ExceptionListener
{
    // MvbOracleConnection represents a connection to an Oracle database
    private MvbOracleConnection mvb = MvbOracleConnection.getInstance();

    // Customer database connection for verifying customer logins
    private CustomerModel customer;
    
    // user is allowed 3 login attempts
    private int loginAttempts = 0;
    
    // components of the login window
    private JTextField usernameField = new JTextField(10);
    private JPasswordField passwordField = new JPasswordField(10);	   
    private JLabel usernameLabel = new JLabel("Enter username:  ");
    private JLabel passwordLabel = new JLabel("Enter password:  ");
    private JPanel buttonPanel = new JPanel();
    private JButton loginButton = new JButton("Log In");
    private JButton registerButton = new JButton("Register");
    
    // login verifier
    private LoginAccounts logins;


    /*
     * Default constructor. The login window is constructed here.
     */
    public LoginWindow(final JFrame parent)
    {
	super(parent, "User Login", true);
	setResizable(false);
	
	// Establish connection to database
	mvb.connect();
	
	// Initialize database interface
	customer = new CustomerModel();
	
	// Register to accept any exception generated in registering a customer.
	customer.addExceptionListener(this);
	
	logins = new LoginAccounts();
	
	passwordField.setEchoChar('*');
	
	// content pane for the login window
	JPanel loginPane = new JPanel();
	setContentPane(loginPane);


	/*
	 * layout components using the GridBag layout manager
	 */ 
	
	GridBagLayout gb = new GridBagLayout();
	GridBagConstraints c = new GridBagConstraints();
	
	loginPane.setLayout(gb);
	loginPane.setBorder(BorderFactory.createEmptyBorder(12, 12, 11, 11));
	
	// place the username label 
	c.gridwidth = GridBagConstraints.RELATIVE;
	c.insets = new Insets(10, 10, 5, 0);
	gb.setConstraints(usernameLabel, c);
	loginPane.add(usernameLabel);
	
	// place the text field for the username 
	c.gridwidth = GridBagConstraints.REMAINDER;
	c.insets = new Insets(10, 0, 5, 10);
	gb.setConstraints(usernameField, c);
	loginPane.add(usernameField);
	
	// place password label
	c.gridwidth = GridBagConstraints.RELATIVE;
	c.insets = new Insets(0, 10, 10, 0);
	gb.setConstraints(passwordLabel, c);
	loginPane.add(passwordLabel);
	
	// place the password field 
	c.gridwidth = GridBagConstraints.REMAINDER;
	c.insets = new Insets(0, 0, 10, 10);
	gb.setConstraints(passwordField, c);
	loginPane.add(passwordField);
	
	// set up the button panel
	buttonPanel.add(loginButton);
	buttonPanel.add(registerButton);
	
	// place the login button
	c.gridwidth = GridBagConstraints.REMAINDER;
	c.insets = new Insets(10, 10, 5, 10);
	c.anchor = GridBagConstraints.CENTER;
	gb.setConstraints(buttonPanel, c);
	loginPane.add(buttonPanel);
	
	// end of layout
	
	// Register password field and OK button with action event handler.
	// An action event is generated when the return key is pressed while 
	// the cursor is in the password field or when the OK button is pressed.
	passwordField.addActionListener(this);
	loginButton.addActionListener(this);
	
	// Register "Register" button with anonymous inner action event handler.
	// Creates a form for customer to register.
	registerButton.addActionListener(new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			CustomerRegisterDialog crd = new CustomerRegisterDialog(parent);
			crd.pack();
			crd.setVisible(true);
		}
	});
	    
	// anonymous inner class for closing the window
	addWindowListener(new WindowAdapter() 
	{
	    public void windowClosing(WindowEvent e) 
	    { 
		System.exit(0); 
	    }
	 });

	// initially, place the cursor in the username text field
	usernameField.requestFocus();	  
	}

       
    /*
     * event handler for password field and OK button
     */ 	    
    public void actionPerformed(ActionEvent e) 
    {
	if (logins.verifyLogin(usernameField.getText(), String.valueOf(passwordField.getPassword())))
	{
	    // if the username and password are valid, 
	    // get rid of the login window
	    dispose();
	}
	else
	{
	    loginAttempts++;
		
	    if (loginAttempts >= 3)
	    {
		dispose();
		System.exit(0);
	    }
	    else
	    {
		// clear the password
		passwordField.setText("");
	    }
	}  
    }
    
    @Override
    public void exceptionGenerated(ExceptionEvent ex) {
    	String message = ex.getMessage();
    	if(message != null) {
    		JOptionPane.showMessageDialog(this, message);
    	} else {
    		JOptionPane.showMessageDialog(this, "An error occurred! Try again.");
    	}
    }
    
    private class LoginAccounts{
    	private final String[] loginNames = { "customer", "clerk", "manager" };
    	
    	public LoginAccounts() {}
    	
    	public boolean verifyLogin(String user, String password) {
    		for(String login: loginNames) {
    			if(user.equals(login) && password.equals(login)) return true;
    		}
    		if(customer.validateCustomer(user, password)) return true;
    		return false;
    	}
    }
    /*
     * This class creates a dialog box for inserting a customer.
     */
    private class CustomerRegisterDialog extends JDialog implements ActionListener
    {
    private JTextField custID = new JTextField(12);
	private JPasswordField password = new JPasswordField(20);
	private JTextField custName = new JTextField(40);
	private JTextField custAddr = new JTextField(60);
	private JTextField custPhone = new JTextField(10);

	// constants used for describing the outcome of an operation
    public static final int OPERATIONSUCCESS = 0;
    public static final int OPERATIONFAILED = 1;
    public static final int VALIDATIONERROR = 2;
	
	/*
	 * Constructor. Creates the dialog's GUI.
	 */
	public CustomerRegisterDialog(JFrame parent)
	{
	    super(parent, "Register", true);
	    setResizable(false);

	    JPanel contentPane = new JPanel(new BorderLayout());
	    setContentPane(contentPane);
	    contentPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

	    // this panel will contain the text field labels and the text fields.
	    JPanel inputPane = new JPanel();
	    inputPane.setBorder(BorderFactory.createCompoundBorder(
			 new TitledBorder(new EtchedBorder(), "Customer Fields"), 
			 new EmptyBorder(5, 5, 5, 5)));
	 
	    // add the text field labels and text fields to inputPane
	    // using the GridBag layout manager

	    GridBagLayout gb = new GridBagLayout();
	    GridBagConstraints c = new GridBagConstraints();
	    inputPane.setLayout(gb);

	    // create and place customer ID label
	    JLabel label = new JLabel("Customer ID: ", SwingConstants.RIGHT);
	    c.gridwidth = GridBagConstraints.RELATIVE;
	    c.insets = new Insets(5, 0, 0, 5);
	    c.anchor = GridBagConstraints.EAST;
	    gb.setConstraints(label, c);
	    inputPane.add(label);

	    // place customer password field
	    c.gridwidth = GridBagConstraints.REMAINDER;
	    c.insets = new Insets(5, 0, 0, 0);
	    c.anchor = GridBagConstraints.WEST;
	    gb.setConstraints(custID, c);
	    inputPane.add(custID);
	    
	    // create and place customer password label
	    label = new JLabel("Password: ", SwingConstants.RIGHT);
	    c.gridwidth = GridBagConstraints.RELATIVE;
	    c.insets = new Insets(5, 0, 0, 5);
	    c.anchor = GridBagConstraints.EAST;
	    gb.setConstraints(label, c);
	    inputPane.add(label);

	    // place customer password field
	    c.gridwidth = GridBagConstraints.REMAINDER;
	    c.insets = new Insets(5, 0, 0, 0);
	    c.anchor = GridBagConstraints.WEST;
	    gb.setConstraints(password, c);
	    inputPane.add(password);

	    // create and place customer name label
	    label = new JLabel("Name: ", SwingConstants.RIGHT);
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
	    label = new JLabel("Address: ", SwingConstants.RIGHT);
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
	    label = new JLabel("Phone: ", SwingConstants.RIGHT);
	    c.gridwidth = GridBagConstraints.RELATIVE;
	    c.insets = new Insets(5, 0, 0, 5);
	    c.anchor = GridBagConstraints.EAST;
	    gb.setConstraints(label, c);
	    inputPane.add(label);

	    // place customer phone field
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
	 * Event handler for the OK button in CustomerRegisterDialog
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
	 * Validates the text fields in CustomerInsertDialog and then
	 * calls customer.insertCustomer() if the fields are valid.
	 * Returns the operation status, which is one of OPERATIONSUCCESS, 
	 * OPERATIONFAILED, VALIDATIONERROR.
	 */ 
	private int validateInsert()
	{
	    try
	    {
	    String cid;
	    String cpassword;
		String cname;
		String caddr;
		String cphone;

		if (custID.getText().trim().length() != 0)
		{
		    cid = custID.getText().trim();
		}
		else
		{
		    return VALIDATIONERROR;
		}
		
		if (String.valueOf(password.getPassword()).trim().length() != 0)
		{
		    cpassword = String.valueOf(password.getPassword()).trim();
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

		if (customer.findCustomer(cid))
		{
			JOptionPane.showMessageDialog(this, "Customer ID \"" + cid + "\" already exists. Try another one.", "Error", JOptionPane.ERROR_MESSAGE);
			return OPERATIONFAILED;
		} 
		else if (customer.insertCustomer(cid, cname, cpassword, caddr, cphone))
		{
		    JOptionPane.showMessageDialog(this, "Registration of \"" + cid + "\" complete! Login now.");
		    return OPERATIONSUCCESS;
		}
		else
		{
		    Toolkit.getDefaultToolkit().beep();
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
}
    
