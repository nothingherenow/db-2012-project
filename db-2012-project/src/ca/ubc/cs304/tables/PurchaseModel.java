package ca.ubc.cs304.tables;

import java.math.BigDecimal;
import java.sql.*;

import javax.swing.event.EventListenerList;

import ca.ubc.cs304.main.ExceptionEvent;
import ca.ubc.cs304.main.ExceptionListener;
import ca.ubc.cs304.main.MvbOracleConnection;

public class PurchaseModel {

	protected PreparedStatement ps = null;
	protected EventListenerList listenerList = new EventListenerList();
	protected Connection con = null;
	protected boolean commit = true;

	/*
	 * Default constructor Precondition: The Connection object in
	 * MvbOracleConnection must be a valid database connection.
	 */
	public PurchaseModel() {
		con = MvbOracleConnection.getInstance().getConnection();
	}

	/*
	 * Insert a Purchase Returns true if the insert is successful; false
	 * otherwise. prid and pdate cannot be null.
	 */
	public boolean insertPurchase(String pcid,
			String pcardno, Date pexpire, Date pexpect, Date pdeliv) {
		try {
			ps = con.prepareStatement("INSERT INTO purchase VALUES (receipt_counter.nextval,sysdate,?,?,?,?,?)");

			// set pcid
			if (pcid != null) {
				ps.setString(1, pcid);
			} else {
				ps.setString(1, null);
			}
			// set pcard no 16 digit cc#
			if (pcardno != null) {
				ps.setString(2, pcardno);
			} else {
				ps.setString(2, null);
			}
			// set pexpire
			if (pexpire != null) {
				ps.setDate(3, pexpire);
			} else {
				ps.setNull(3, Types.DATE);
			}
			// set pexpect
			if (pexpect != null) {
				ps.setDate(4, pexpect);
			} else {
				ps.setNull(4, Types.DATE);
			}
			// set pdeliv
			if (pdeliv != null) {
				ps.setDate(5, pdeliv);
			} else {
				ps.setNull(5, Types.DATE);
			}

			ps.executeUpdate();
			
			if(commit)
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
     * Updates a purchase.
     * Returns true if the update is successful; false otherwise.
     *
     * receiptID and cid cannot be null.
     */
    public boolean updatePurchase(int receiptID, String cid, String cardno, 
    		Date expire, Date expectedDate, Date deliveredDate)
    {
	try
	{	
	    ps = con.prepareStatement("UPDATE purchase SET cid = ?, cardno = ?," +
	    		"expire = ?, expectedDate = ?, deliveredDate = ? WHERE receiptID = ?");

	    ps.setString(1, cid);

	    if(cardno != null) {
	    	ps.setString(2, cardno);
	    } else {
	    	ps.setString(2, null);
	    }
	    
	    if(expire != null) {
	    	ps.setDate(3, expire);
	    } else {
	    	ps.setDate(3, null);
	    }
	    
	    if(expectedDate != null) {
	    	ps.setDate(4, expectedDate);
	    } else {
	    	ps.setDate(4, null);
	    }
	    
	    if(deliveredDate != null) {
	    	ps.setDate(5, deliveredDate);
	    } else {
	    	ps.setDate(5, null);
	    }
	    
	    ps.setInt(6, receiptID);
	    
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
	 * Deletes a Purchase tuple. Returns true if the delete is successful; false
	 * otherwise.
	 */
	public boolean deletePurchase(Integer prid) {
		try {
			ps = con.prepareStatement("DELETE FROM purchase WHERE receipt_id = ?");

			ps.setInt(1, prid.intValue());

			ps.executeUpdate();

			if(commit)
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
     * Returns a ResultSet containing all purchases. The ResultSet is
     * scroll insensitive and read only. If there is an error, null
     * is returned.
     */ 
    public ResultSet showPurchase()
    {
	try
	{	 
	    ps = con.prepareStatement("SELECT p.* FROM purchase p", 
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
	 * Returns an updatable result set for Purchase
	 */
	public ResultSet editPurchase() {
		try {
			ps = con.prepareStatement("SELECT p.* FROM purchase p",
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
     * Returns true if the purchase exists; false
     * otherwise.
     */ 
    public boolean findPurchase(int receiptID)
    {
	try
	{	
	    ps = con.prepareStatement("SELECT receiptID FROM purchase where receiptID = ?");

	    ps.setInt(1, receiptID);

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
	
	public void setCommit(boolean commit) {
		this.commit = commit;
	}

}
