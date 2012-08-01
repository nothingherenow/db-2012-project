package ca.ubc.cs304.tables;

import java.math.BigDecimal;
import java.sql.*;

import javax.swing.event.EventListenerList;

import ca.ubc.cs304.main.ExceptionEvent;
import ca.ubc.cs304.main.ExceptionListener;
import ca.ubc.cs304.main.LoginWindow;
import ca.ubc.cs304.main.MvbOracleConnection;

public class ManagerTransactions {

	protected PreparedStatement ps = null;
	protected EventListenerList listenerList = new EventListenerList();
	protected Connection con = null;
	protected boolean commit = true;

	/*
	 * Default constructor Precondition: The Connection object in
	 * MvbOracleConnection must be a valid database connection.
	 */
	public ManagerTransactions() {
		con = MvbOracleConnection.getInstance().getConnection();
	}
	
	  /*
     * Returns ResultSet containing n number of top selling items. If there
     * is an error, null is returned.
     */ 
    public ResultSet topSellingItems(Date sdate, int n)
    {
    	try
    	{	
    		ps = con.prepareStatement(
    				"SELECT title, company, stock, NumCopiesSold " +
    				"FROM item " +
    				"WHERE upc IN ( " +
    					"SELECT pi.upc, SUM (pi.quantity) AS NumCopiesSold " + 
    					"FROM purchaseitem pi, purchase p " +
    					"WHERE p.date = ? AND pi.receiptID = p.receiptID " +
    					"GROUP BY pi.upc " +
   						"LIMIT 0, ?) " + 
    				"ORDER BY NumCopiesSold DESC");

	    ps.setDate(1, sdate);
	    
	    ps.setInt(2, n);

	    ResultSet rs = ps.executeQuery();
	    
	    return rs;
    	}

    	catch (SQLException ex) {
    		ExceptionEvent event = new ExceptionEvent(this, ex.getMessage());
    		fireExceptionGenerated(event);
    		// no need to commit or rollback since it is only a query

    		return null;
    	}
	
	}

	
public ResultSet processShipment(int sid)
	{
		try
		{
			ps = con.prepareStatement(
				"SELECT upc, quantity, supPrice " + 
				"FROM ShipItem " + 
				"WHERE sid = ?");

			ps.setInt(1, sid);

			ResultSet rs = ps.executeQuery();

			int upc;
			int quantity;
			float supPrice;

			while (rs.next()){
				upc = rs.getInt("upc");
				quantity = rs.getInt("quantity");
				supPrice = rs.getFloat("supPrice");

				ps = con.prepareStatement(
					"UPDATE Item " +
					"SET quantity = quantity + ?, sellPrice = (? * 1.2) " + 
					"WHERE upc = ?");
				
				ps.setInt(1, quantity);
				ps.setFloat(2, supPrice);
				ps.setInt(3, upc);

				ps.executeUpdate();

			}
			
			con.commit();

			return rs;
		}

			catch (SQLException ex) {
				ExceptionEvent event = new ExceptionEvent(this, ex.getMessage());
				fireExceptionGenerated(event);

				return null;
			}
		}

	
	/*
	 * Manager sets the delivered date for an order. 
	 * Returns true if the change has been successfully made, returns false otherwise.
	 */
	public boolean setDeliveredDate(int receiptID, Date deliveredDate){
		try {
			ps = con.prepareStatement(
					"UPDATE Purchase " +
					"SET deliveredDate = ? " +
					"WHERE receiptID = ?");
			
			ps.setDate(1, deliveredDate);
			ps.setInt(2, receiptID);
			
			ps.executeUpdate();
			
			con.commit();
			
			return true;
		}
		
		catch (SQLException ex)
		{
			ExceptionEvent event = new ExceptionEvent(this, ex.getMessage());
			fireExceptionGenerated(event);

			try
			{
				con.rollback();
				return false; 
			}
			catch (SQLException ex2)
			{
				event = new ExceptionEvent(this, ex2.getMessage());
				fireExceptionGenerated(event);
				return false; 
			}
		}
		
	}
	
	public ResultSet showDailyReportAllItems(Date date){
		try {
			ps = con.prepareStatement(
					"SELECT i.upc, i.category, i.price, SUM(pi.quantity) AS totalUnits, SUM(i.sellPrice) AS totalCost " +
					"FROM Purchase p, PurchaseItem pi, Item i " +
					"WHERE pi.receiptID = i.receiptID AND pi.upc = i.upc AND p.date = ?" +
					"GROUP BY pi.upc, i.category");
			
			ps.setDate(1, date);
			
			ps.executeUpdate();
			
			con.commit();
			
			ResultSet rs = ps.executeQuery();
			
			return rs; 
		}
			
			catch (SQLException ex) {
	    		ExceptionEvent event = new ExceptionEvent(this, ex.getMessage());
	    		fireExceptionGenerated(event);
	    		// no need to commit or rollback since it is only a query

	    		return null;
	    	}
		}
	
	public ResultSet showDailyReportCategorialTotal(Date date){
		try {
			ps = con.prepareStatement(
					"SELECT i.category, SUM(pi.quantity) AS totalSaleCategory, SUM(i.sellPrice) AS totalCategoryCost "  +
					"FROM Purchase p, PurchaseItem pi, Item i " +
					"WHERE pi.receiptID = i.receiptID AND pi.upc = i.upc AND p.date = ?" +
					"GROUP BY i.category");
			
			ps.setDate(1, date);
			
			ps.executeUpdate();
			
			con.commit();
			
			ResultSet rs = ps.executeQuery();
			
			return rs; 
		}
			
			catch (SQLException ex) {
	    		ExceptionEvent event = new ExceptionEvent(this, ex.getMessage());
	    		fireExceptionGenerated(event);
	    		// no need to commit or rollback since it is only a query

	    		return null;
	    	}
		}
	
	public ResultSet showDailyReportTotal(Date date){
		try {
			ps = con.prepareStatement(
					"SELECT SUM(pi.quantity) AS totalSalesOverall, SUM(i.sellPrice) AS totalMoneyMade "  +
					"FROM Purchase p, PurchaseItem pi, Item i " +
					"WHERE p.receiptID = pi.receiptID AND pi.upc = i.upc AND p.date = ?");
			
			ps.setDate(1, date);
			
			ps.executeUpdate();
			
			con.commit();
			
			ResultSet rs = ps.executeQuery();
			
			return rs; 
		}
			
			catch (SQLException ex) {
	    		ExceptionEvent event = new ExceptionEvent(this, ex.getMessage());
	    		fireExceptionGenerated(event);
	    		// no need to commit or rollback since it is only a query

	    		return null;
	    	}
		}

	/*
	 * Returns an non-updatable result set for Shipment
	 */
	public ResultSet showShipment() {
		try {
			ps = con.prepareStatement("SELECT s.* FROM shipment s",
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);

			ResultSet rs = ps.executeQuery();

			return rs;
		} catch (SQLException ex) {
			ExceptionEvent event = new ExceptionEvent(this, ex.getMessage());
			fireExceptionGenerated(event);
			// no need to commit or rollback since it is only a query

			return null;
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
	

	
