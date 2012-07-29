package ca.ubc.cs304.tables;
import java.math.BigDecimal;
import java.sql.*;

import javax.swing.event.EventListenerList;

import ca.ubc.cs304.main.ExceptionEvent;
import ca.ubc.cs304.main.ExceptionListener;
import ca.ubc.cs304.main.MvbOracleConnection;


public class PurchaseItemModel {
	
	protected PreparedStatement ps = null;
	protected EventListenerList listenerList = new EventListenerList();
	protected Connection con = null;
	
	/*
	 * Default constructor Precondition: The Connection object in
	 * MvbOracleConnection must be a valid database connection.
	 */
	public PurchaseItemModel() {
		con = MvbOracleConnection.getInstance().getConnection();
	}
	/*
	 * Insert a PurchaseItem Returns true if the insert is successful; false
	 * otherwise.
	 */
	public boolean insertPurchaseItem(Integer pirid, Integer piupc, Integer piquantity) {
		try {
			ps = con.prepareStatement("INSERT INTO purchaseitem VALUES (?,?,?)");

			ps.setInt(1, pirid.intValue());

			ps.setInt(2, piupc.intValue());

			ps.setInt(3, piquantity.intValue());

			ps.executeUpdate();
			con.commit();
			return true;

		} catch (SQLException ex) {
			ExceptionEvent event = new ExceptionEvent(this, ex.getMessage());
			fireExceptionGenerated(event);

			try {
				con.rollback();
				return false;
			} catch (SQLException ex2) {
				event = new ExceptionEvent(this, ex2.getMessage());
				fireExceptionGenerated(event);
				return false;
			}
		}
	}
	
	/*
     * Updates a purchaseItem tuple.
     * Returns true if the update is successful; false otherwise.
     *
     * All arguments cannot be null.
     */
    public boolean updatePurchaseItem(int receiptID, int upc, int quantity)
    {
	try
	{	
	    ps = con.prepareStatement("UPDATE purchaseitem SET quantity = ? WHERE receiptID = ? AND upc = ?");

	    ps.setInt(1, quantity);
	    
	    ps.setInt(2, receiptID);
	    
	    ps.setInt(3, upc);
	    
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
	
	/*
	 * Deletes a PurchaseItem tuple. Returns true if the delete is successful; false
	 * otherwise.
	 */
	public boolean deletePurchaseItem(Integer rid, Integer upc) {
		try {
			ps = con.prepareStatement("DELETE FROM purchaseitem WHERE receiptid = ? AND upc = ?");

			ps.setInt(1, rid.intValue());

			ps.setInt(2, upc.intValue());

			ps.executeUpdate();

			con.commit();

			return true;
		} catch (SQLException ex) {
			ExceptionEvent event = new ExceptionEvent(this, ex.getMessage());
			fireExceptionGenerated(event);

			try {
				con.rollback();
				return false;
			} catch (SQLException ex2) {
				event = new ExceptionEvent(this, ex2.getMessage());
				fireExceptionGenerated(event);
				return false;
			}
		}
	}
	
	/*
     * Returns a ResultSet containing all purchaseItem tuples. The ResultSet is
     * scroll insensitive and read only. If there is an error, null
     * is returned.
     */ 
    public ResultSet showPurchaseItem()
    {
	try
	{	 
	    ps = con.prepareStatement("SELECT pi.* FROM purchaseitem pi", 
				      ResultSet.TYPE_SCROLL_INSENSITIVE,
				      ResultSet.CONCUR_READ_ONLY);

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
	 * Returns an updatable result set for PurchaseItem
	 */
	public ResultSet editPurchaseItem() {
		try {
			ps = con.prepareStatement("SELECT pi.* FROM purchaseitem pi",
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);

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
     * Returns true if the PurchaseItem tuple exists; false
     * otherwise.
     */ 
    public boolean findPurchaseItem(int receiptID, int upc)
    {
	try
	{	
	    ps = con.prepareStatement("SELECT * FROM purchaseitem WHERE receiptID = ? AND upc = ?");

	    ps.setInt(1, receiptID);
	    
	    ps.setInt(2, upc);

	    ResultSet rs = ps.executeQuery();

	    if (rs.next())
	    {
		return true; 
	    }
	    else
	    {
		return false; 
	    }
	}
	catch (SQLException ex)
	{
	    ExceptionEvent event = new ExceptionEvent(this, ex.getMessage());
	    fireExceptionGenerated(event);

	    return false; 
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
}
