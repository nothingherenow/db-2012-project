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
	 * Insert a Customer caddr and cphone can be null Returns true if the insert
	 * is successful; false otherwise.
	 */
	public boolean insertCustomer(String cid, String cname, String cpass,
			String caddr, String cphone) {
		try {
			ps = con.prepareStatement("INSERT INTO customer VALUES (?,?,?,?,?)");
			
			ps.setString(1, cid);
			
			ps.setString(2, cpass);
			
			if (caddr != null) {
				ps.setString(3, cname);
			} else {
				ps.setString(3, null);
			}

			if (caddr != null) {
				ps.setString(4, caddr);
			} else {
				ps.setString(4, null);
			}

			if (cphone != null) {
				ps.setString(5, cphone);
			} else {
				ps.setNull(5, Types.VARCHAR);
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
     * Updates a customer
     * Returns true if the update is successful; false otherwise.
     *
     * cname, caddr and cphone can be null.
     */
    public boolean updateCustomer(String cid, String cname, String caddr, String cphone)
    {
	try
	{	
	    ps = con.prepareStatement("UPDATE customer SET name = ?, address = ?, phone = ? WHERE cid = ?");

	    if (cname != null)
	    {
	    	ps.setString(1, cname);
	    } else {
	    	ps.setString(1, null);
	    }

	    if (caddr != null) {
	    	ps.setString(2, caddr);
	    } else {
	    	ps.setString(2, null);
	    }
	    
	    if (cphone != null) {
	    	ps.setString(3, cphone);
	    } else {
	    	ps.setString(3, null);
	    }
	    
	    ps.setString(4, cid);

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
	 * Deletes a customer. Returns true if the delete is successful; false
	 * otherwise.
	 */
	public boolean deleteCustomer(String cid) {
		try {
			ps = con.prepareStatement("DELETE FROM customer WHERE cid = ?");

			ps.setString(1, cid);

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
     * Returns a ResultSet containing all customers. The ResultSet is
     * scroll insensitive and read only. If there is an error, null
     * is returned.
     */ 
    public ResultSet showCustomer()
    {
	try
	{	 
	    ps = con.prepareStatement("SELECT c.* FROM customer c", 
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
	 * Returns an updatable result set for Customer
	 */
	public ResultSet editCustomer() {
		try {
			ps = con.prepareStatement("SELECT c.* FROM customer c",
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
     * Returns true if the customer exists; false
     * otherwise.
     */ 
    public boolean findCustomer(String cid)
    {
	try
	{	
	    ps = con.prepareStatement("SELECT cid FROM customer where cid = ?");

	    ps.setString(1, cid);

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
     * Returns true if the customer exists and the password is correct; false
     * otherwise.
     */ 
    public boolean validateCustomer(String cid, String password)
    {
	try
	{	
	    ps = con.prepareStatement("SELECT cid FROM customer where cid = ? AND password = ?");

	    ps.setString(1, cid);

	    ps.setString(2, password);
	    
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
     * Returns the database connection used by this branch model
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
