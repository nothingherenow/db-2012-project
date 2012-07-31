package ca.ubc.cs304.tables;

import java.math.BigDecimal;
import java.sql.*;
import java.util.*;
import java.util.Date;

import javax.swing.event.EventListenerList;

import ca.ubc.cs304.main.ExceptionEvent;
import ca.ubc.cs304.main.ExceptionListener;
import ca.ubc.cs304.main.LoginWindow;
import ca.ubc.cs304.main.MvbOracleConnection;

public class ClerkTransactions {

	protected static int MAXIMUM_DAILY_TRANSACTIONS = 50;

	protected PreparedStatement ps = null;
	protected EventListenerList listenerList = new EventListenerList();
	protected Connection con = null;
	protected boolean commit = true;

	/*
	 * Default constructor Precondition: The Connection object in
	 * MvbOracleConnection must be a valid database connection.
	 */
	public ClerkTransactions() {
		con = MvbOracleConnection.getInstance().getConnection();
	}
	
	/*
	 * Execute all parts of a purchase transaction. 
	 */
	public void instorePurchase(String cardno, Date cexpire){
		try
		{
			ps = con.prepareStatement("INSERT into Purchase VALUES(receipt_counter.nextval, sysdate, null, ?, ?, null, null ");
			
			if (cardno != null) {
				ps.setString(1, cardno);
			} else {
				ps.setString(1, null);
			}
			
			if (cexpire != null) {
				ps.setDate(2, (java.sql.Date) cexpire);
			} else {
				ps.setDate(2, null);
			}			
			ps.executeUpdate();
			
			//while loop for 
		}
		catch (SQLException ex)
		{
			
		}
	}
	
	/* Returns a ResultSet which contains items on a receipt
	 * 
	 */
	public ResultSet receiptItems(Integer rid){
		try
		{
			ps = con.prepareStatement("SELECT p.upc, p.quantity, i.price FROM PurchaseItem p, Item i" +
					"WHERE p.upc = i.upc AND p.receiptID = ?");
			
			ps.setInt(1, rid);
		
			ResultSet rs = ps.executeQuery();
			return rs;
			
		}
		catch (SQLException ex)
		{
			ExceptionEvent event = new ExceptionEvent(this, ex.getMessage());
			fireExceptionGenerated(event);
			// no need to commit or rollback since it is only a query

			return null; 
		}
	}
	
	/*
	 * Returns a ResultSet which contains a purchase's total.
	 */
	
	public BigDecimal receiptTotal(Integer rid){
		try
		{
			ps = con.prepareStatement("SELECT SUM(p.quantity * i.price) FROM PurchaseItem p, Item i" +
					"WHERE p.upc = i.upc AND p.receiptID = ?");
			
			ps.setInt(1,  rid);
			ResultSet rs = ps.executeQuery();
			
			
			rs.next();

			return rs.getBigDecimal(1);
			
		}
		catch (SQLException ex)
		{
			ExceptionEvent event = new ExceptionEvent(this, ex.getMessage());
			fireExceptionGenerated(event);
			// no need to commit or rollback since it is only a query

			return null; 
		}
	}	
		
	/*
	 * Processes a return of an item. First validate the receiptID, then check if the return is being made 
	 * within the allotted 15 days.  
	 */
	
	public boolean checkReturn(Integer rid){
		try 
		{
			ps = con.prepareStatement("SELECT * FROM Purchase p WHERE p.receiptID = ?", 
					ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.FETCH_UNKNOWN);
			ps.setInt(1,  rid);
			ResultSet rs = ps.executeQuery();
			rs.beforeFirst();
			
			if(!rs.next()){
				ExceptionEvent ev = new ExceptionEvent(this, "Invalid receipt");
				fireExceptionGenerated(ev);
				return false;
			}
			
			java.sql.Date sqldate = rs.getDate("retdate");
			GregorianCalendar gCal = new GregorianCalendar();
			gCal.add(gCal.DATE, -15);
			java.util.Date jdate = new java.util.Date(gCal.getTime().getTime());
			
			if (jdate.after(sqldate)) {
				ExceptionEvent ev = new ExceptionEvent(this, "Invalid return, 15 days have elapsed" + 
						"since purchase");
				fireExceptionGenerated(ev);
			return false;
			}
		
			return true;
					
		}
		catch (SQLException ex)
		{
			ExceptionEvent event = new ExceptionEvent(this, ex.getMessage());
			fireExceptionGenerated(event);
			return false;
		}
	}
	
	
	public int processReturn(Integer rid, Integer upc, Integer quantity){
		try
		{
			ps = con.prepareStatement("INSERT into Return VALUES(return_counter.nextval, sysdate, ?");
			ps.setInt(1, rid);
			ps.executeUpdate();
			
			ps = con.prepareStatement("INSERT into ReturnItem VALUES(return_counter.currval, ?, ?");
			ps.setInt(1, upc);
			ps.setInt(2,  quantity);
			ps.executeUpdate();
			
			con.commit();
			return 1;
		}
		catch (SQLException ex)
		{
			ExceptionEvent event = new ExceptionEvent(this, ex.getMessage());
			fireExceptionGenerated(event);

			try
			{
				con.rollback();
				return -1; 
			}
			catch (SQLException ex2)
			{
				event = new ExceptionEvent(this, ex2.getMessage());
				fireExceptionGenerated(event);
				return -1; 
			}
		}		
	}
	
	/*
	 * Returns the database connection used by this customer model
	 */
	public Connection getConnection()
	{
		return con; 
	}

	/*
	 * This method allows members of this class to clean up after itself 
	 * before it is garbage collected. It is called by the garbage collector.
	 */ 
	protected void finalize() throws Throwable
	{
		if (ps != null)
		{
			ps.close();
		}

		// finalize() must call super.finalize() as the last thing it does
		super.finalize();	
	}


	/******************************************************************************
	 * Below are the methods to add and remove ExceptionListeners.
	 * 
	 * Whenever an exception occurs in BranchModel, an exception event
	 * is sent to all registered ExceptionListeners.
	 ******************************************************************************/ 

	public void addExceptionListener(ExceptionListener l) 
	{
		listenerList.add(ExceptionListener.class, l);
	}


	public void removeExceptionListener(ExceptionListener l) 
	{
		listenerList.remove(ExceptionListener.class, l);
	}

	/*
	 * This method notifies all registered ExceptionListeners. The code below is
	 * similar to the example in the Java 2 API documentation for the
	 * EventListenerList class.
	 */
	public void fireExceptionGenerated(ExceptionEvent ex) {
		// Guaranteed to return a non-null array
		Object[] listeners = listenerList.getListenerList();

		// Process the listeners last to first, notifying
		// those that are interested in this event.
		// I have no idea why the for loop counts backwards by 2
		// and the array indices are the way they are.
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == ExceptionListener.class) {
				((ExceptionListener) listeners[i + 1]).exceptionGenerated(ex);
			}
		}
	}

	public void setCommit(boolean commit) {
		this.commit = commit;
	}

}

