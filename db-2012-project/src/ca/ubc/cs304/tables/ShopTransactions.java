package ca.ubc.cs304.tables;

import java.math.BigDecimal;
import java.sql.*;

import javax.swing.event.EventListenerList;

import ca.ubc.cs304.main.ExceptionEvent;
import ca.ubc.cs304.main.ExceptionListener;
import ca.ubc.cs304.main.LoginWindow;
import ca.ubc.cs304.main.MvbOracleConnection;

public class ShopTransactions {

	protected PreparedStatement ps = null;
	protected EventListenerList listenerList = new EventListenerList();
	protected Connection con = null;
	protected boolean commit = true;

	/*
	 * Default constructor Precondition: The Connection object in
	 * MvbOracleConnection must be a valid database connection.
	 */
	public ShopTransactions() {
		con = MvbOracleConnection.getInstance().getConnection();
	}

	/*
	 * Returns a ResultSet containing items matching input parameters. The ResultSet is
	 * scroll insensitive and read only. If there is an error, null is returned.
	 */ 
	public ResultSet searchItems(String title, String category, String lead)
	{
		try
		{	 
			// count non-null args
			int argCount = 0;
			int currentArg = 1;

			if(title != null) argCount++;
			if(category != null) argCount++;
			if(lead != null) argCount++;

			int argsLeft = argCount;
			// Customize query depending on given parameters
			StringBuffer statement = new StringBuffer("SELECT item.upc, title, category, " +
					"name AS leadingsinger, stock FROM item, leadsinger WHERE item.upc = leadsinger.upc AND ");
			if(title != null) {
				statement.append("title = ?");
				argsLeft--;
				if(argsLeft > 0) statement.append(" AND ");
			}
			if(category != null) {
				statement.append("category = ?");
				argsLeft--;
				if(argsLeft > 0) statement.append(" AND ");
			}
			if(lead != null) {
				statement.append("name = ?");
			}

			ps = con.prepareStatement(statement.toString(), 
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);

			if(title != null) {
				ps.setString(1, title);
				currentArg++;
			}
			if(category != null) {
				ps.setString(currentArg, category);
				currentArg++;
			}
			if(lead != null) {
				ps.setString(currentArg, lead);
			}

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

	public boolean claimItems(int upc, int quantity) {
		String login = LoginWindow.getLogin();
		
		try
		{
			ps = con.prepareStatement("INSERT INTO shoppingcart " +
					"VALUES(?,?,?)");
			
			ps.setString(1, login);
			
			ps.setInt(2, upc);
			
			ps.setInt(3, quantity);
			
			ps.executeUpdate();
			
			ps = con.prepareStatement("UPDATE Item SET stock = (stock - ?) WHERE upc = ?");

			ps.setInt(1, quantity);

			ps.setInt(2, upc);

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
     * Returns a ResultSet containing customer's shopping cart. The ResultSet is
     * scroll insensitive and read only. If there is an error, null
     * is returned.
     */ 
    public ResultSet showShoppingCart()
    {
    	String login = LoginWindow.getLogin();
    	
	try
	{	 
	    ps = con.prepareStatement("SELECT s.upc, i.title, s.quantity " +
	    		"FROM shoppingcart s, item i " +
	    		"WHERE s.upc = i.upc AND s.cid = ?", 
				      ResultSet.TYPE_SCROLL_INSENSITIVE,
				      ResultSet.CONCUR_READ_ONLY);
	    
	    ps.setString(1, login);

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
