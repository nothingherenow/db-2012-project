package ca.ubc.cs304.tables;
import java.sql.*;

import javax.swing.event.EventListenerList;

import ca.ubc.cs304.main.ExceptionEvent;
import ca.ubc.cs304.main.ExceptionListener;
import ca.ubc.cs304.main.MvbOracleConnection;

public class ReturnItemModel {
	protected PreparedStatement ps = null;
	protected EventListenerList listenerList = new EventListenerList();
	protected Connection con = null;
	
	/*
	 * Default constructor Precondition: The Connection object in
	 * MvbOracleConnection must be a valid database connection.
	 */
	public ReturnItemModel() {
		con = MvbOracleConnection.getInstance().getConnection();
	}
	/*
	 * Insert a ReturnItem. Returns true if the insert is successful; false
	 * otherwise.
	 */
	public boolean insertReturnItem(Integer retid, Integer upc, Integer quantity) {
		try {
			ps = con.prepareStatement("INSERT INTO returnitem VALUES (?,?,?)");

			ps.setInt(1, retid.intValue());

			ps.setInt(2, upc.intValue());

			ps.setInt(3, quantity.intValue());

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
     * Updates a returnItem tuple.
     * Returns true if the update is successful; false otherwise.
     *
     * All arguments cannot be null.
     */
    public boolean updateReturnItem(int retID, int upc, int quantity)
    {
	try
	{	
	    ps = con.prepareStatement("UPDATE returnitem SET quantity = ? WHERE retid = ? AND upc = ?");

	    ps.setInt(1, quantity);
	    
	    ps.setInt(2, retID);
	    
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
	 * Deletes a ReturnItem tuple. Returns true if the delete is successful; false
	 * otherwise.
	 */
	public boolean deleteReturnItem(Integer retid, Integer upc) {
		try {
			ps = con.prepareStatement("DELETE FROM returnitem WHERE retid = ? AND upc=?");

			ps.setInt(1, retid.intValue());
			
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
     * Returns a ResultSet containing all returnItem tuples. The ResultSet is
     * scroll insensitive and read only. If there is an error, null
     * is returned.
     */ 
    public ResultSet showReturnItem()
    {
	try
	{	 
	    ps = con.prepareStatement("SELECT ri.* FROM returnitem ri", 
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
	 * Returns an updatable result set for ReturnItem
	 */
	public ResultSet editReturnItem() {
		try {
			ps = con.prepareStatement("SELECT ri.* FROM returnitem ri",
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
     * Returns true if the ReturnItem tuple exists; false
     * otherwise.
     */ 
    public boolean findReturnItem(int retID, int upc)
    {
	try
	{	
	    ps = con.prepareStatement("SELECT * FROM returnitem WHERE retID = ? AND upc = ?");

	    ps.setInt(1, retID);
	    
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
