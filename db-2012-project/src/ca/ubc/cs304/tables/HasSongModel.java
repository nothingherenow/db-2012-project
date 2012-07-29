package ca.ubc.cs304.tables;

import java.sql.*;

import javax.swing.event.EventListenerList;

import ca.ubc.cs304.main.ExceptionEvent;
import ca.ubc.cs304.main.ExceptionListener;
import ca.ubc.cs304.main.MvbOracleConnection;

public class HasSongModel {
	protected PreparedStatement ps = null;
	protected EventListenerList listenerList = new EventListenerList();
	protected Connection con = null;
	protected boolean commit = true;
	
	/*
	 * Default constructor Precondition: The Connection object in
	 * MvbOracleConnection must be a valid database connection.
	 */
	public HasSongModel() {
		con = MvbOracleConnection.getInstance().getConnection();
	}

	/* Insert a HasSong tuple 
	 * (upc,stitle) is the primary key
	 *  Returns true if the insert is successful; false otherwise.
	 */
	public boolean insertHasSong(Integer upc, String stitle){
		try
		{
			ps = con.prepareStatement("INSERT INTO hassong VALUES (?,?)");
			
			ps.setInt(1, upc.intValue());
			
			ps.setString(2, stitle);
			
			ps.executeUpdate();
			
			if(commit)
			con.commit();
			return true;
		}
		catch (SQLException ex) {
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
     * Deletes a HasSong tuple.
     * Returns true if the delete is successful; false otherwise.
     */
    public boolean deleteHasSong(Integer upc, String stitle)
    {
	try
	{	  
	    ps = con.prepareStatement("DELETE FROM hassong WHERE upc = ? AND title = ?");

	    ps.setInt(1, upc.intValue());
	    
	    ps.setString(2, stitle);

	    ps.executeUpdate();

	    if(commit)
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
     * Returns a ResultSet containing all customers. The ResultSet is
     * scroll insensitive and read only. If there is an error, null
     * is returned.
     */ 
    public ResultSet showHasSong()
    {
	try
	{	 
	    ps = con.prepareStatement("SELECT h.* FROM hassong h", 
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
     * Returns an updatable result set for HasSong
     */ 
    public ResultSet editHasSong()
    {
	try
	{	 
	    ps = con.prepareStatement("SELECT hs.* FROM hassong hs", 
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
     * Returns true if the hasSong tuple exists; false
     * otherwise.
     */ 
    public boolean findHasSong(int upc, String title)
    {
	try
	{	
	    ps = con.prepareStatement("SELECT * FROM hassong WHERE upc = ? AND title = ?");

	    ps.setInt(1, upc);
	    
	    ps.setString(2, title);

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
     * Returns the database connection used by this shipment model
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
