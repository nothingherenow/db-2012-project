package ca.ubc.cs304.tables;

import java.math.BigDecimal;
import java.sql.*;

import javax.swing.event.EventListenerList;

import ca.ubc.cs304.main.ExceptionEvent;
import ca.ubc.cs304.main.ExceptionListener;
import ca.ubc.cs304.main.MvbOracleConnection;

public class ItemModel {

	protected PreparedStatement ps = null;
	protected EventListenerList listenerList = new EventListenerList();
	protected Connection con = null;

	/*
	 * Default constructor Precondition: The Connection object in
	 * MvbOracleConnection must be a valid database connection.
	 */
	public ItemModel() {
		con = MvbOracleConnection.getInstance().getConnection();
	}

	/*
	 * Insert a Item Returns true if the insert is successful; false otherwise.
	 * year,company, and quantity can be null
	 */
	public boolean insertItem(int upc, String ititle, String itype, String icat,
			int istock, String icomp, int iyear, BigDecimal isellp) {
		try {
			ps = con.prepareStatement("INSERT INTO item VALUES (?,?,?,?,?,?,?,?)");

			ps.setInt(1, upc);

			ps.setString(2, ititle);
			
			ps.setString(3, itype);

			ps.setString(4, icat);
			
			ps.setInt(5, istock);

			if (icomp != null) {
				ps.setString(6, icomp);
			} else {
				ps.setString(6, null);
			}

			if (iyear != -1) {
				ps.setInt(7, iyear);
			} else {
				ps.setNull(7, Types.INTEGER);
			}

			ps.setBigDecimal(8, isellp);

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
     * Updates an item.
     * Returns true if the update is successful; false otherwise.
     *
     * ititle, itype, icat and isellp cannot be null.
     */
    public boolean updateItem(int upc, String ititle, String itype, String icat,
			int istock, String icomp, int iyear, BigDecimal isellp)
    {
	try
	{	
	    ps = con.prepareStatement("UPDATE item SET title = ?, type = ?, category = ?," +
	    		"stock = ?, company = ?, year = ?, sellPrice = ? WHERE upc = ?");

	    ps.setString(1, ititle);

	    ps.setString(2, itype);

	    ps.setString(3, icat);
	    
	    ps.setInt(4, istock);
	    
	    if(icomp != null) {
	    	ps.setString(5, icomp);
	    } else {
	    	ps.setString(5, null);
	    }
	    
	    if(iyear != -1) {
	    	ps.setInt(6, iyear);
	    } else {
	    	ps.setNull(6, java.sql.Types.NULL);
	    }
	    
	    ps.setBigDecimal(7,isellp);
	    
	    ps.setInt(8, upc);
	    
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
	 * Deletes an item. Returns true if the delete is successful; false
	 * otherwise.
	 */
	public boolean deleteItem(Integer upc) {
		try {
			ps = con.prepareStatement("DELETE FROM item WHERE upc = ?");

			ps.setInt(1, upc.intValue());

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
     * Returns a ResultSet containing all items. The ResultSet is
     * scroll insensitive and read only. If there is an error, null
     * is returned.
     */ 
    public ResultSet showItem()
    {
	try
	{	 
	    ps = con.prepareStatement("SELECT i.* FROM item i", 
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
	 * Returns an updatable result set for Item
	 */
	public ResultSet editItem() {
		try {
			ps = con.prepareStatement("SELECT i.* FROM item i",
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
     * Returns true if the item exists; false
     * otherwise.
     */ 
    public boolean findItem(int upc)
    {
	try
	{	
	    ps = con.prepareStatement("SELECT upc FROM item where upc = ?");

	    ps.setInt(1, upc);

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
