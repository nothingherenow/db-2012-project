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
	public int getReceiptID() {
		try
		{
			ps = con.prepareStatement("");
			ps.executeQuery();
			
		}
	}
	*/
	
	public ResultSet showItem(Integer itupc){
		try
		{
			ps = con.prepareStatement("SELECT upc, title, category FROM Item i where i.upc = ?");
			ps.setInt(1, itupc);
			ResultSet rs = ps.executeQuery();
			return rs;
		}
		catch (SQLException ex)
		{
			ExceptionEvent event = new ExceptionEvent(this, ex.getMessage());
			fireExceptionGenerated(event);
			return null;
		}
		
	}
	
	/*
	 * Creates new purchase record corresponding to an instore purchase. Returns the receipt number if successful,
	 * returns 0 if unsuccessful.
	 */
	public Integer instorePurchase(){
		try
		{
			//ps = con.prepareStatement("INSERT into Purchase VALUES(receipt_counter.nextval, sysdate, null," +
		//" null, null, null, null)");

		
			//ps.executeUpdate();
		
			//ps = con.prepareStatement("SELECT last_number from user_sequences where sequence_name = \'RECEIPT_COUNTER\'");
			
			ps = con.prepareStatement("SELECT receipt_counter.NEXTVAL FROM Purchase");
			ResultSet rs = ps.executeQuery();
			rs.next();
			Integer receiptid = new Integer(rs.getInt(1));
			
			ps = con.prepareStatement("INSERT into Purchase VALUES(?, sysdate, null, " +
			"null, null, null, null)");
			
			ps.setInt(1, receiptid);
			ResultSet rss = ps.executeQuery();
	
			
			
			con.commit();
			
			return receiptid;
					
		}
		catch (SQLException ex)
		{
			ExceptionEvent event = new ExceptionEvent(this, ex.getMessage());
			fireExceptionGenerated(event);

			try
			{
				con.rollback();
				return 0; 
			}
			catch (SQLException ex2)
			{
				event = new ExceptionEvent(this, ex2.getMessage());
				fireExceptionGenerated(event);
				return 0; 
			}
		}
	}
	
	/*
	 * Updates purchase record to include a credit card number and its expiry date, indicating that the
	 * purchase was made via credit card. Returns 1 if successful and 0 if not.
	 */
	
	public int updateCreditCard(Integer rid, String cardno, Date cexpire) {
		try {
			ps = con.prepareStatement("UPDATE Purchase SET cardno = ?, expire = ? WHERE receiptID = ?");
			ps.setString(1, cardno);
			ps.setDate(2, (java.sql.Date) cexpire);
			ps.setInt(3, rid);
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
				return 0; 
			}
			catch (SQLException ex2)
			{
				event = new ExceptionEvent(this, ex2.getMessage());
				fireExceptionGenerated(event);
				return 0; 
			}
		}
	}
	
	/*
	 * 
	 */
	public ResultSet receiptDate(Integer rid){
		try
		{
			ps = con.prepareStatement("SELECT p.purdate FROM Purchase p WHERE p.receiptID = ?");
			ps.setInt(1,  rid);
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
	
	/* Returns a ResultSet which contains items on a receipt
	 * 
	 */
	public ResultSet receiptItems(Integer rid){
		try
		{
			ps = con.prepareStatement("SELECT p.upc, p.quantity, i.price FROM PurchaseItem p, Item i " +
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
			ps = con.prepareStatement("SELECT p.receiptID, p.purdate FROM Purchase p WHERE p.receiptID = ?", 
					ResultSet.FETCH_UNKNOWN);
			ps.setInt(1,  rid);
			ResultSet rs = ps.executeQuery();
			//rs.beforeFirst();
			
			if(rs.next() == false){
				ExceptionEvent ev = new ExceptionEvent(this, "Invalid receipt");
				fireExceptionGenerated(ev);
				return false;
			}
			
			java.sql.Date purchdate = rs.getDate("purdate");
			GregorianCalendar gCal = new GregorianCalendar();
			gCal.add(gCal.DATE, -15);
			java.sql.Date jdate = new java.sql.Date(gCal.getTime().getTime());
			
			if (jdate.after(purchdate)) {
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
	
	/*
	 * Updates the database to reflect an in-store return, given the receipt number, UPC of the item to be returned,
	 * and the quantity of that item to be returned. Method returns a 1 if successful, 0 if unable to complete process.
	 */
	public int processReturn(Integer rid, Integer upc, Integer quantity){
		try
		{
			ps = con.prepareStatement("INSERT into Return VALUES(return_counter.nextval, sysdate, ?)");
			ps.setInt(1, rid);
			ps.executeUpdate();
			
			ps = con.prepareStatement("INSERT into ReturnItem VALUES(return_counter.currval, ?, ?)");
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
				return 0; 
			}
			catch (SQLException ex2)
			{
				event = new ExceptionEvent(this, ex2.getMessage());
				fireExceptionGenerated(event);
				return 0; 
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

