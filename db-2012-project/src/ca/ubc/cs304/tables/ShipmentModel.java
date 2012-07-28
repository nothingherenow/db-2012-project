package ca.ubc.cs304.tables;

import java.sql.*;

import javax.swing.event.EventListenerList;

import ca.ubc.cs304.main.ExceptionEvent;
import ca.ubc.cs304.main.ExceptionListener;
import ca.ubc.cs304.main.MvbOracleConnection;

public class ShipmentModel {

	protected PreparedStatement ps = null;
	protected EventListenerList listenerList = new EventListenerList();
	protected Connection con = null;

	/*
	 * Default constructor Precondition: The Connection object in
	 * MvbOracleConnection must be a valid database connection.
	 */
	public ShipmentModel() {
		con = MvbOracleConnection.getInstance().getConnection();
	}
	
	/*
	 * Insert a Shipment  Returns true if the insert
	 * is successful; false otherwise.
	 */
	public boolean insertShipment(Integer sid, String supname, Date sdate) {
		try {
			ps = con.prepareStatement("INSERT INTO shipment VALUES (?,?,?)");

			ps.setInt(1, sid.intValue());

			ps.setString(2, supname);

			ps.setDate(3, sdate);

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
     * Updates a shipment
     * Returns true if the update is successful; false otherwise.
     */
    public boolean updateShipment(int sid, String shipSupName, Date shipDate)
    {
	try
	{	
	    ps = con.prepareStatement("UPDATE shipment SET supName = ?, shipDate = ? WHERE sid = ?");

	    ps.setString(1, shipSupName);
	    
	    ps.setDate(2, shipDate);
	    
	    ps.setInt(3, sid);
	    
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
	 * Deletes a shipment. Returns true if the delete is successful; false
	 * otherwise.
	 */
	public boolean deleteShipment(Integer sid) {
		try {
			ps = con.prepareStatement("DELETE FROM shipment WHERE sid = ?");

			ps.setInt(1, sid.intValue());

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
	 * Returns an updatable result set for Shipment
	 */
	public ResultSet editShipment() {
		try {
			ps = con.prepareStatement("SELECT s.* FROM shipment s",
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
     * Returns true if the shipment exists; false
     * otherwise.
     */ 
    public boolean findShipment(int sid)
    {
	try
	{	
	    ps = con.prepareStatement("SELECT sid FROM shipment where sid = ?");

	    ps.setInt(1, sid);

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
    
	/*
     * Returns the database connection used by this branch model
     */
    public Connection getConnection()
    {
	return con; 
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
