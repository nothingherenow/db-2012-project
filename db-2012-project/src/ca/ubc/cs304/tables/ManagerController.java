package ca.ubc.cs304.tables;

//File: ManagerController.java

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import ca.ubc.cs304.main.CustomTable;
import ca.ubc.cs304.main.CustomTableModel;
import ca.ubc.cs304.main.ExceptionEvent;
import ca.ubc.cs304.main.ExceptionListener;
import ca.ubc.cs304.main.LoginWindow;
import ca.ubc.cs304.main.MvbView;
import ca.ubc.cs304.tables.ClerkController.CheckoutStoreDialog;
import ca.ubc.cs304.tables.ClerkController.ReturnDialog;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class ManagerController implements ActionListener, ExceptionListener {
	private MvbView mvb;
	private ManagerTransactions manage = null;
	private JTable table = null;
	private ResultSet rs = null;

	// constants used for describing the outcome of an operation
	public static final int OPERATIONSUCCESS = 0;
	public static final int OPERATIONFAILED = 1;
	public static final int VALIDATIONERROR = 2;

	public ManagerController(MvbView mvb) {
		this.mvb = mvb;
		manage = new ManagerTransactions();

		//register to receive exception events from manager
		manage.addExceptionListener(this);
	}
	
	/*
	 * This event handler gets called when the user makes a menu item selection.
	 */
	public void actionPerformed(ActionEvent e) {
		String actionCommand = e.getActionCommand();
		
		
		if (actionCommand.equals("Process Shipment"))
		{
			ProcessShipmentDialog iDialog = new ProcessShipmentDialog(mvb);
			iDialog.pack();
			mvb.centerWindow(iDialog);
			iDialog.setVisible(true);
			return;
		}
		
		if (actionCommand.equals("Set Delivery"))
		{
			SetDeliveredDialog iDialog = new SetDeliveredDialog(mvb);
			iDialog.pack();
			mvb.centerWindow(iDialog);
			iDialog.setVisible(true);
			return;
		}
		
		if (actionCommand.equals("Show Shipments"))
		{
			mvb.updateStatusBar("Showing Shipments...");
			showAllShipments();
			return; 
		}
		
		if (actionCommand.equals("Sales"))
		{
			DisplaySalesDialog iDialog = new DisplaySalesDialog(mvb);
			iDialog.pack();
			mvb.centerWindow(iDialog);
			iDialog.setVisible(true);
			return;
		}
		
		if (actionCommand.equals("Top Selling"))
		{
			TopSellingDialog iDialog = new TopSellingDialog(mvb);
			iDialog.pack();
			mvb.centerWindow(iDialog);
			iDialog.setVisible(true);
			return;
		}
		
	}
	
	
	/*
	 * Returns an non-updatable result set for Shipment
	 */
	 private void showAllShipments()
	    {
		ResultSet rs = manage.showShipment();
		
		// CustomTableModel maintains the result set's data, e.g., if  
		// the result set is updatable, it will update the database
		// when the table's data is modified.  
		CustomTableModel model = new CustomTableModel(manage.getConnection(), rs);
		CustomTable data = new CustomTable(model);

		// register to be notified of any exceptions that occur in the model and table
		model.addExceptionListener(this);
		data.addExceptionListener(this);
		    
		// Adds the table to the scrollpane.
		// By default, a JTable does not have scroll bars.
		mvb.addTable(data);
	    }
	 
	 /*
	 * This class creates a dialog box for displaying the daily sales report.
	 */
	class DisplaySalesDialog extends JDialog implements ActionListener
	{
		private JTextField idate = new JTextField(10);

		/*
		 * Constructor. Creates the dialog's GUI.
		 */
		public DisplaySalesDialog(JFrame parent)
		{
			super(parent, "Set a date to generate a daily sales report", true);
			setResizable(false);

			JPanel contentPane = new JPanel(new BorderLayout());
			setContentPane(contentPane);
			contentPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

			// this panel will contain the text field labels and the text fields.
			JPanel inputPane = new JPanel();
			inputPane.setBorder(BorderFactory.createCompoundBorder(
					new TitledBorder(new EtchedBorder(), "Daily Sales Fields"), 
					new EmptyBorder(5, 5, 5, 5)));

			// add the text field labels and text fields to inputPane
			// using the GridBag layout manager

			GridBagLayout gb = new GridBagLayout();
			GridBagConstraints c = new GridBagConstraints();
			inputPane.setLayout(gb);
		    
			// create and place Daily Sales date label
		    JLabel label = new JLabel("Daily Sales date (dd-MM-yyyy): ", SwingConstants.RIGHT);
		    c.gridwidth = GridBagConstraints.RELATIVE;
		    c.insets = new Insets(5, 0, 0, 5);
		    c.anchor = GridBagConstraints.EAST;
		    gb.setConstraints(label, c);
		    inputPane.add(label);

		    // place date field
		    c.gridwidth = GridBagConstraints.REMAINDER;
		    c.insets = new Insets(5, 0, 0, 0);
		    c.anchor = GridBagConstraints.WEST;
		    gb.setConstraints(idate, c);
		    inputPane.add(idate);

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
		 * Event handler for the OK button in ItemSearchDialog
		 */ 
		public void actionPerformed(ActionEvent e)
		{
			String actionCommand = e.getActionCommand();

			if (actionCommand.equals("OK"))
			{
				if (validateDate() != VALIDATIONERROR)
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
		private int validateDate()
		{
			try
			{
				Date date;
				String stringDate;

				stringDate = idate.getText().trim();
				
				if (stringDate.length() != 0)
				{
					if(stringDate.length() != 10) return VALIDATIONERROR;
					SimpleDateFormat fm = new SimpleDateFormat("dd-MM-yyyy");
					java.util.Date utilDate;
					try {
						utilDate = fm.parse(stringDate);
					} catch (ParseException ex) {
						return VALIDATIONERROR;
					}
					
					date = new java.sql.Date(utilDate.getTime());
				}
				else
				{
				    date = null;
				}

				mvb.updateStatusBar("Generating Daily Sales Report...");

				rs = manage.showDailyReportAllItems(date);
				showResults(rs);
				rs = manage.showDailyReportCategorialTotal(date);
				showResults(rs);
				rs = manage.showDailyReportTotal(date);
				showResults(rs);

				mvb.updateStatusBar("Processing complete, tables show reports for all items, per category, and in total in sequential order.");

				

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
	 * This class creates a dialog box for processing a shipment.
	 */
	class TopSellingDialog extends JDialog implements ActionListener
	{
		private JTextField idate = new JTextField(10);
		private JTextField inumber = new JTextField(5);

		/*
		 * Constructor. Creates the dialog's GUI.
		 */
		public TopSellingDialog(JFrame parent)
		{
			super(parent, "Find the top sellers of a particular day", true);
			setResizable(false);

			JPanel contentPane = new JPanel(new BorderLayout());
			setContentPane(contentPane);
			contentPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

			// this panel will contain the text field labels and the text fields.
			JPanel inputPane = new JPanel();
			inputPane.setBorder(BorderFactory.createCompoundBorder(
					new TitledBorder(new EtchedBorder(), "Top Selling Items field"), 
					new EmptyBorder(5, 5, 5, 5)));

			// add the text field labels and text fields to inputPane
			// using the GridBag layout manager

			GridBagLayout gb = new GridBagLayout();
			GridBagConstraints c = new GridBagConstraints();
			inputPane.setLayout(gb);
			
			// create and place number label
		    JLabel label = new JLabel("Top # of items: ", SwingConstants.RIGHT);
		    c.gridwidth = GridBagConstraints.RELATIVE;
		    c.insets = new Insets(5, 0, 0, 5);
		    c.anchor = GridBagConstraints.EAST;
		    gb.setConstraints(label, c);
		    inputPane.add(label);

		    // place item number field
		    c.gridwidth = GridBagConstraints.REMAINDER;
		    c.insets = new Insets(5, 0, 0, 0);
		    c.anchor = GridBagConstraints.WEST;
		    gb.setConstraints(inumber, c);
		    inputPane.add(inumber);
		    
			// create and place delivered date label
		    label = new JLabel("Date (dd-MM-yyyy): ", SwingConstants.RIGHT);
		    c.gridwidth = GridBagConstraints.RELATIVE;
		    c.insets = new Insets(5, 0, 0, 5);
		    c.anchor = GridBagConstraints.EAST;
		    gb.setConstraints(label, c);
		    inputPane.add(label);

		    // place delivered date field
		    c.gridwidth = GridBagConstraints.REMAINDER;
		    c.insets = new Insets(5, 0, 0, 0);
		    c.anchor = GridBagConstraints.WEST;
		    gb.setConstraints(idate, c);
		    inputPane.add(idate);

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
		 * Event handler for the OK button in ItemSearchDialog
		 */ 
		public void actionPerformed(ActionEvent e)
		{
			String actionCommand = e.getActionCommand();

			if (actionCommand.equals("OK"))
			{
				if (validateDateAndNumber() != VALIDATIONERROR)
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
		private int validateDateAndNumber()
		{
			try
			{
				int number;
				Date date;
				String stringDate;
				
				if (inumber.getText().trim().length() != 0)
				{
				    number = Integer.valueOf(inumber.getText().trim()).intValue();
				 
				} else {
					return VALIDATIONERROR;
				}

				stringDate = idate.getText().trim();
				
				if (stringDate.length() != 0)
				{
					if(stringDate.length() != 10) return VALIDATIONERROR;
					SimpleDateFormat fm = new SimpleDateFormat("dd-MM-yyyy");
					java.util.Date utilDate;
					try {
						utilDate = fm.parse(stringDate);
					} catch (ParseException ex) {
						return VALIDATIONERROR;
					}
					
					date = new java.sql.Date(utilDate.getTime());
				}
				else
				{
				    date = null;
				}

				mvb.updateStatusBar("Generating Top " + number + "Items...");

				rs = manage.topSellingItems(date, number);

				mvb.updateStatusBar("Generating Complete!");

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
		 * This class creates a dialog box for processing a shipment.
		 */
		class SetDeliveredDialog extends JDialog implements ActionListener
		{
			private JTextField idate = new JTextField(10);
			private JTextField ireceipt = new JTextField(20);

			/*
			 * Constructor. Creates the dialog's GUI.
			 */
			public SetDeliveredDialog(JFrame parent)
			{
				super(parent, "Set a delivered date for a purchase", true);
				setResizable(false);

				JPanel contentPane = new JPanel(new BorderLayout());
				setContentPane(contentPane);
				contentPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

				// this panel will contain the text field labels and the text fields.
				JPanel inputPane = new JPanel();
				inputPane.setBorder(BorderFactory.createCompoundBorder(
						new TitledBorder(new EtchedBorder(), "Delivered Package Fields"), 
						new EmptyBorder(5, 5, 5, 5)));

				// add the text field labels and text fields to inputPane
				// using the GridBag layout manager

				GridBagLayout gb = new GridBagLayout();
				GridBagConstraints c = new GridBagConstraints();
				inputPane.setLayout(gb);
				
				// create and place receipt number label
			    JLabel label = new JLabel("Receipt Number: ", SwingConstants.RIGHT);
			    c.gridwidth = GridBagConstraints.RELATIVE;
			    c.insets = new Insets(5, 0, 0, 5);
			    c.anchor = GridBagConstraints.EAST;
			    gb.setConstraints(label, c);
			    inputPane.add(label);

			    // place item receipt number field
			    c.gridwidth = GridBagConstraints.REMAINDER;
			    c.insets = new Insets(5, 0, 0, 0);
			    c.anchor = GridBagConstraints.WEST;
			    gb.setConstraints(ireceipt, c);
			    inputPane.add(ireceipt);
			    
				// create and place delivered date label
			    label = new JLabel("Delivered date (dd-MM-yyyy): ", SwingConstants.RIGHT);
			    c.gridwidth = GridBagConstraints.RELATIVE;
			    c.insets = new Insets(5, 0, 0, 5);
			    c.anchor = GridBagConstraints.EAST;
			    gb.setConstraints(label, c);
			    inputPane.add(label);

			    // place delivered date field
			    c.gridwidth = GridBagConstraints.REMAINDER;
			    c.insets = new Insets(5, 0, 0, 0);
			    c.anchor = GridBagConstraints.WEST;
			    gb.setConstraints(idate, c);
			    inputPane.add(idate);

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
			 * Event handler for the OK button in ItemSearchDialog
			 */ 
			public void actionPerformed(ActionEvent e)
			{
				String actionCommand = e.getActionCommand();

				if (actionCommand.equals("OK"))
				{
					if (validateDateAndReceipt() != VALIDATIONERROR)
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
			private int validateDateAndReceipt()
			{
				try
				{
					int receipt;
					Date date;
					String stringDate;
					
					if (ireceipt.getText().trim().length() != 0)
					{
					    receipt = Integer.valueOf(ireceipt.getText().trim()).intValue();
					 
					} else {
						return VALIDATIONERROR;
					}

					stringDate = idate.getText().trim();
					
					if (stringDate.length() != 0)
					{
						if(stringDate.length() != 10) return VALIDATIONERROR;
						SimpleDateFormat fm = new SimpleDateFormat("dd-MM-yyyy");
						java.util.Date utilDate;
						try {
							utilDate = fm.parse(stringDate);
						} catch (ParseException ex) {
							return VALIDATIONERROR;
						}
						
						date = new java.sql.Date(utilDate.getTime());
					}
					else
					{
					    date = null;
					}

					mvb.updateStatusBar("Inserting Delivered Date...");

					if (manage.setDeliveredDate(receipt, date))
					{
					    mvb.updateStatusBar("Operation successful.");
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
		 * This class creates a dialog box for processing a shipment.
		 */
		class ProcessShipmentDialog extends JDialog implements ActionListener
		{
			private JTextField isid = new JTextField(10);

			/*
			 * Constructor. Creates the dialog's GUI.
			 */
			public ProcessShipmentDialog(JFrame parent)
			{
				super(parent, "Process a shipment from a supplier", true);
				setResizable(false);

				JPanel contentPane = new JPanel(new BorderLayout());
				setContentPane(contentPane);
				contentPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

				// this panel will contain the text field labels and the text fields.
				JPanel inputPane = new JPanel();
				inputPane.setBorder(BorderFactory.createCompoundBorder(
						new TitledBorder(new EtchedBorder(), "Shipment fields"), 
						new EmptyBorder(5, 5, 5, 5)));

				// add the text field labels and text fields to inputPane
				// using the GridBag layout manager

				GridBagLayout gb = new GridBagLayout();
				GridBagConstraints c = new GridBagConstraints();
				inputPane.setLayout(gb);

				// create and place item title label
				JLabel label = new JLabel("Shipping ID: ", SwingConstants.RIGHT);
				c.gridwidth = GridBagConstraints.RELATIVE;
				c.insets = new Insets(5, 0, 0, 5);
				c.anchor = GridBagConstraints.EAST;
				gb.setConstraints(label, c);
				inputPane.add(label);

				// place item title field
				c.gridwidth = GridBagConstraints.REMAINDER;
				c.insets = new Insets(5, 0, 0, 0);
				c.anchor = GridBagConstraints.WEST;
				gb.setConstraints(isid, c);
				inputPane.add(isid);

				// when the return key is pressed in the last field
				// of this form, the action performed by the ok button
				// is executed
				isid.addActionListener(this);
				isid.setActionCommand("OK");

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
			 * Event handler for the OK button in ItemSearchDialog
			 */ 
			public void actionPerformed(ActionEvent e)
			{
				String actionCommand = e.getActionCommand();

				if (actionCommand.equals("OK"))
				{
					if (validateSID() != VALIDATIONERROR)
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
			private int validateSID()
			{
				try
				{
					int sid = 0;

					if (isid.getText().trim().length() != 0)
					{
					    sid = Integer.valueOf(isid.getText().trim()).intValue();
					} else {
						return VALIDATIONERROR;
					}

					// Disallow searches equal to or less than 0.
					if(sid <= 0) return VALIDATIONERROR;

					mvb.updateStatusBar("Processing shipment...");

					rs = manage.processShipment(sid);

					mvb.updateStatusBar("Processing complete, shipment added to stocks and prices updated!");

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
		CustomTableModel model = new CustomTableModel(manage.getConnection(), rs);
		final CustomTable data = new CustomTable(model);
		table = data;

		// register to be notified of any exceptions that occur in the model and table
		model.addExceptionListener(this);
		data.addExceptionListener(this);
		
		data.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		data.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if(!data.getSelectionModel().isSelectionEmpty()) {
					mvb.enableAddItem();
				}
				
			}
		});

		// Adds the table to the scrollpane.
		// By default, a JTable does not have scroll bars.
		mvb.addTable(data);
	}
	

	
	

}