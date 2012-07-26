package ca.ubc.cs304.tables;

// File: BranchModel.java

import java.sql.*; 
import javax.swing.event.EventListenerList;

import ca.ubc.cs304.main.ExceptionEvent;
import ca.ubc.cs304.main.ExceptionListener;
import ca.ubc.cs304.main.MvbOracleConnection;


/*
 * BranchModel is a database interface class that provides methods to 
 * insert a branch, update the name of a branch, delete a branch, return 
 * a ResultSet containing all branches, determine the existence of a particular 
 * branch, and return the database connection used by this class. 
 *
 * Each insert, update, and delete method is treated as a transaction, i.e., they 
 * commit the change. If you want to use those methods as helper functions for a 
 * larger transaction, you would have to write non commit() versions of those methods, 
 * so that you can treat the larger transaction as a single transaction. 
 * 
 * The database connection in MvbOracleConnection must be set before any database
 * access methods can be used. (This was done when connect() was called by the 
 * LoginWindow). This class is defined in MvbOracleConnection.java. 
 *
 * BranchModel allows components to register for exception events when 
 * an exception occurs in the model.  Exceptions are part of the 
 * Java 2 API, but exception events aren't.  We are creating our own 
 * event/listener. The BranchController class registers itself as
 * a listener to BranchModel so that it can display the exception 
 * messages.  The file ExceptionEvent.java contains the class 
 * definition of ExceptionEvent.  The file ExceptionListener.java
 * contains the interface that components must implement in order 
 * to receive exception events.   
 */ 
public class BranchModel
{
    protected PreparedStatement ps = null;
    protected EventListenerList listenerList = new EventListenerList();
    protected Connection con = null; 


    /*
     * Default constructor
     * Precondition: The Connection object in MvbOracleConnection must be
     * a valid database connection.
     */ 
    public BranchModel()
    {
	con = MvbOracleConnection.getInstance().getConnection();
    }
 

    /*
     * Inserts a tuple into the branch table. The object wrapper for the int datatype 
     * is used so that null can be inserted, e.g., to insert null
     * into the branch_phone column, set the bphone argument to null. If a 10 digit phone 
     * number is used, change the data type for the phone number from Integer to Long.
     * Returns true if the insert is successful; false otherwise.
     *
     * Only baddr and bphone can be null.
     */ 
    public boolean insertBranch(Integer bid, String bname, String baddr, String bcity,
				       Integer bphone)
    {
	try
	{	   
	    ps = con.prepareStatement("INSERT INTO branch VALUES (?,?,?,?,?)");

	    ps.setInt(1, bid.intValue());

	    ps.setString(2, bname);

	    if (baddr != null)
	    {
		ps.setString(3, baddr);
	    }
	    else
	    {
		ps.setString(3, null);
	    }

	    ps.setString(4, bcity);

	    if (bphone != null)
	    {
		ps.setInt(5, bphone.intValue());
	    }
	    else
	    {
		ps.setNull(5, Types.INTEGER);
	    }

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
     * Updates the name of a branch
     * Returns true if the update is successful; false otherwise.
     *
     * bname cannot be null.
     */
    public boolean updateBranch(int bid, String bname)
    {
	try
	{	
	    ps = con.prepareStatement("UPDATE branch SET branch_name = ? WHERE branch_id = ?");

	    if (bname != null)
	    {
		ps.setString(1, bname);
	    }
	    else
	    {
		return false; 
	    }

	    ps.setInt(2, bid);

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
     * Deletes a branch.
     * Returns true if the delete is successful; false otherwise.
     */
    public boolean deleteBranch(int bid)
    {
	try
	{	  
	    ps = con.prepareStatement("DELETE FROM branch WHERE branch_id = ?");

	    ps.setInt(1, bid);

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
     * Returns a ResultSet containing all branches. The ResultSet is
     * scroll insensitive and read only. If there is an error, null
     * is returned.
     */ 
    public ResultSet showBranch()
    {
	try
	{	 
	    ps = con.prepareStatement("SELECT b.* FROM branch b", 
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
     * Same as showBranch() except that an updatable result set
     * is returned.
     */ 
    public ResultSet editBranch()
    {
	try
	{	 
	    ps = con.prepareStatement("SELECT b.* FROM branch b", 
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
     * Returns true if the branch exists; false
     * otherwise.
     */ 
    public boolean findBranch(int bid)
    {
	try
	{	
	    ps = con.prepareStatement("SELECT branch_id FROM branch where branch_id = ?");

	    ps.setInt(1, bid);

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
     * This method notifies all registered ExceptionListeners.
     * The code below is similar to the example in the Java 2 API
     * documentation for the EventListenerList class.
     */ 
    public void fireExceptionGenerated(ExceptionEvent ex) 
    {
	// Guaranteed to return a non-null array
	Object[] listeners = listenerList.getListenerList();

	// Process the listeners last to first, notifying
	// those that are interested in this event.
	// I have no idea why the for loop counts backwards by 2
	// and the array indices are the way they are.
	for (int i = listeners.length-2; i>=0; i-=2) 
	{
	    if (listeners[i]==ExceptionListener.class) 
	    {
		((ExceptionListener)listeners[i+1]).exceptionGenerated(ex);
	    }
         }
     }
}
