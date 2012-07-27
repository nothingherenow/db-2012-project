package ca.ubc.cs304.tables;

import java.sql.*;

import javax.swing.event.EventListenerList;

import ca.ubc.cs304.main.ExceptionEvent;
import ca.ubc.cs304.main.ExceptionListener;
import ca.ubc.cs304.main.MvbOracleConnection;

public class LeadSingerModel {

	protected PreparedStatement ps = null;
	protected EventListenerList listenerList = new EventListenerList();
	protected Connection con = null;

	/*
	 * Default constructor Precondition: The Connection object in
	 * MvbOracleConnection must be a valid database connection.
	 */
	public LeadSingerModel() {
		con = MvbOracleConnection.getInstance().getConnection();
	}
	
	/* Insert a LeadSinger tuple 
	 * upc and lname are PRIMARY KEYS
	 *  Returns true if the insert is successful; false otherwise.
	 */
	public boolean insertLeadSinger(Integer upc, String lname){
		try
		{
			ps = con.prepareStatement("INSERT INTO leadsinger VALUES (?,?)");
			
			ps.setInt(1, upc.intValue());
			
			ps.setString(2, lname);
			
			ps.executeUpdate();
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
     * Deletes a LeadSinger tuple.
     * Returns true if the delete is successful; false otherwise.
     */
    public boolean deleteLeadSinger(Integer upc, String lname)
    {
	try
	{	  
	    ps = con.prepareStatement("DELETE FROM leadsinger WHERE upc = ? AND name = ?");

	    ps.setInt(1, upc.intValue());
	    
	    ps.setString(2, lname);

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
     * Returns an updatable result set for LeadSinger
     */ 
    public ResultSet editLeadSinger()
    {
	try
	{	 
	    ps = con.prepareStatement("SELECT ls.* FROM leadsinger ls", 
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
