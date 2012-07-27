package ca.ubc.cs304.tables;

import java.sql.*;

import javax.swing.event.EventListenerList;

import ca.ubc.cs304.main.ExceptionEvent;
import ca.ubc.cs304.main.ExceptionListener;
import ca.ubc.cs304.main.MvbOracleConnection;

public class CustomerModel {
	protected PreparedStatement ps = null;
	protected EventListenerList listenerList = new EventListenerList();
	protected Connection con = null;

	/*
	 * Default constructor Precondition: The Connection object in
	 * MvbOracleConnection must be a valid database connection.
	 */
	public CustomerModel() {
		con = MvbOracleConnection.getInstance().getConnection();
	}

	/*
	 * Insert a Customer caddr and cphone can be null
	 */
	public boolean insertCustomer(String cid, String cname, String cpass,
			String caddr, Integer cphone) {
		try {
			ps = con.prepareStatement("INSERT INTO customer VALUES (?,?,?,?,?)");

			ps.setString(1, cid);

			ps.setString(2, cname);

			ps.setString(3, cpass);

			if (caddr != null) {
				ps.setString(3, caddr);
			} else {
				ps.setString(3, null);
			}

			if (cphone != null) {
				ps.setInt(5, cphone.intValue());
			} else {
				ps.setNull(5, Types.INTEGER);
			}

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
     * Deletes a customer.
     * Returns true if the delete is successful; false otherwise.
     */
    public boolean deleteCustomer(String cid)
    {
	try
	{	  
	    ps = con.prepareStatement("DELETE FROM customer WHERE cid = ?");

	    ps.setString(1, cid);

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
     * Returns an updatable result set for Customer 
     */ 
    public ResultSet editCustomer()
    {
	try
	{	 
	    ps = con.prepareStatement("SELECT c.* FROM customer c", 
				      ResultSet.TYPE_SCROLL_INSENSITIVE,
				      ResultSet.CONCUR_UPDATABLE);

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
