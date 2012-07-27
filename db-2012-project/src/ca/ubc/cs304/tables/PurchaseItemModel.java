package ca.ubc.cs304.tables;
import java.sql.*;

import javax.swing.event.EventListenerList;

import ca.ubc.cs304.main.ExceptionEvent;
import ca.ubc.cs304.main.ExceptionListener;
import ca.ubc.cs304.main.MvbOracleConnection;


public class PurchaseItemModel {
	
	protected PreparedStatement ps = null;
	protected EventListenerList listenerList = new EventListenerList();
	protected Connection con = null;
	
	/*
	 * Default constructor Precondition: The Connection object in
	 * MvbOracleConnection must be a valid database connection.
	 */
	public PurchaseItemModel() {
		con = MvbOracleConnection.getInstance().getConnection();
	}
	/*
	 * Insert a PurchaseItem Returns true if the insert is successful; false
	 * otherwise.
	 */
	public boolean insertPurchaseItem(Integer pirid, Integer piupc, Integer piquantity) {
		try {
			ps = con.prepareStatement("INSERT INTO purchaseitem VALUES (?,?,?)");

			ps.setInt(1, pirid.intValue());

			ps.setInt(2, piupc.intValue());

			ps.setInt(3, piquantity.intValue());

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
	 * Deletes a PurchaseItem tuple. Returns true if the delete is successful; false
	 * otherwise.
	 */
	public boolean deletePurchaseItem(Integer rid, Integer upc) {
		try {
			ps = con.prepareStatement("DELETE FROM purchaseitem WHERE receipt_id = ? AND upc = ?");

			ps.setInt(1, rid.intValue());

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
	 * Returns an updatable result set for PurchaseItem
	 */
	public ResultSet editPurchaseItem() {
		try {
			ps = con.prepareStatement("SELECT pi.* FROM purchaseitem pi",
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
