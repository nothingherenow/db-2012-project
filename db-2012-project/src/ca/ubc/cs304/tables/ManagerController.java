package ca.ubc.cs304.tables;

//File: ManagerController.java

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*;

import ca.ubc.cs304.main.CustomTable;
import ca.ubc.cs304.main.CustomTableModel;
import ca.ubc.cs304.main.ExceptionEvent;
import ca.ubc.cs304.main.ExceptionListener;
import ca.ubc.cs304.main.MvbView;
import ca.ubc.cs304.tables.ClerkController.CheckoutStoreDialog;
import ca.ubc.cs304.tables.ClerkController.ReturnDialog;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class ManagerController implements ActionListener, ExceptionListener {
	private MvbView mvb;
	// private ManagerTransactions manage = null;
	private JTable table = null;
	private ResultSet rs = null;

	// constants used for describing the outcome of an operation
	public static final int OPERATIONSUCCESS = 0;
	public static final int OPERATIONFAILED = 1;
	public static final int VALIDATIONERROR = 2;

	public ManagerController(MvbView mvb) {
		this.mvb = mvb;
		// manage = new ManagerTransactions();

		// register to receive exception events from manager
		// manage.addExceptionListener(this);
	}
	
	/*
	 * This event handler gets called when the user makes a menu item selection.
	 */
	public void actionPerformed(ActionEvent e) {
		String actionCommand = e.getActionCommand();

		/*
		if (actionCommand.equals("Shipment")) {
			ShipmentDialog iDialog = new ShipmentDialog(mvb);
			iDialog.pack();
			mvb.centerWindow(iDialog);
			iDialog.setVisible(true);
			return;
		}

		if (actionCommand.equals("Delivery")) {
			DeliveryDialog iDialog = new DeliveryDialog(mvb);
			iDialog.pack();
			mvb.centerWindow(iDialog);
			iDialog.setVisible(true);
			return;
		}
		*/
		
		if (actionCommand.equals("Sales"))
		{
			mvb.updateStatusBar("Generating Daily Sales Report...");
			showDailySales();
			return; 
		}
		
		if (actionCommand.equals("Top Selling"))
		{
			mvb.updateStatusBar("Showing top selling items...");
			showTopSelling();
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
	 * This method Generates a daily sales Report
	 */
	private void showDailySales()
	{
	// SQL transaction method from ManagerTransactions goes here
	}
	
	/*
	 * This method shows the top selling items
	 */
	private void showTopSelling()
	{
	// SQL transaction method from ManagerTransactions goes here
	}
	
	

}
