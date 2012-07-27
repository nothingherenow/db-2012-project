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
	public boolean insertPurchase(Integer prid, Date pdate, String pcid,
			String pcardno, Date pexpire, Date pexpect, Date pdeliv) {
		try {
			ps = con.prepareStatement("INSERT INTO purchase VALUES (?,?,?,?,?,?,?)");

			ps.setInt(1, prid.intValue());

			ps.setDate(2, pdate);

			// set pcid
			if (pcid != null) {
				ps.setString(3, pcid);
			} else {
				ps.setString(3, null);
			}
			// set pcard no 16 digit cc#
			if (pcardno != null) {
				ps.setString(4, pcardno);
			} else {
				ps.setString(4, null);
			}
			// set pexpire
			if (pexpire != null) {
				ps.setDate(5, pexpire);
			} else {
				ps.setNull(5, Types.DATE);
			}
			// set pexpect
			if (pexpect != null) {
				ps.setDate(6, pexpect);
			} else {
				ps.setNull(6, Types.DATE);
			}
			// set pdeliv
			if (pdeliv != null) {
				ps.setDate(7, pdeliv);
			} else {
				ps.setNull(7, Types.DATE);
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
	 * Deletes a Purchase tuple. Returns true if the delete is successful; false
	 * otherwise.
	 */
	public boolean deletePurchase(Integer prid) {
		try {
			ps = con.prepareStatement("DELETE FROM purchase WHERE receipt_id = ?");

			ps.setInt(1, prid.intValue());

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
