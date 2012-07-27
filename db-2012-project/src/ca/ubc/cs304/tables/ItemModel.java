package ca.ubc.cs304.tables;

import java.math.BigDecimal;
import java.sql.*;

import javax.swing.event.EventListenerList;

import ca.ubc.cs304.main.ExceptionEvent;
import ca.ubc.cs304.main.ExceptionListener;
import ca.ubc.cs304.main.MvbOracleConnection;

public class ItemModel {

	protected PreparedStatement ps = null;
	protected EventListenerList listenerList = new EventListenerList();
	protected Connection con = null;

	/*
	 * Default constructor Precondition: The Connection object in
	 * MvbOracleConnection must be a valid database connection.
	 */
	public ItemModel() {
		con = MvbOracleConnection.getInstance().getConnection();
	}

	/*
	 * Insert a Item Returns true if the insert is successful; false otherwise.
	 * year,company, and quantity can be null
	 */
	public boolean insertItem(Integer upc, String ititle, String icat,
			String icomp, Integer iyear, Integer iquantity, BigDecimal isellp) {
		try {
			ps = con.prepareStatement("INSERT INTO item VALUES (?,?,?,?,?,?,?)");

			ps.setInt(1, upc.intValue());

			ps.setString(2, ititle);

			ps.setString(3, icat);

			if (icomp != null) {
				ps.setString(4, icomp);
			} else {
				ps.setString(4, null);
			}

			if (iyear != null) {
				ps.setInt(5, iyear.intValue());
			} else {
				ps.setNull(5, Types.INTEGER);
			}

			if (iquantity != null) {
				ps.setInt(6, iquantity.intValue());
			} else {
				ps.setNull(6, Types.INTEGER);
			}

			ps.setBigDecimal(7, isellp);

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
	 * Deletes an item. Returns true if the delete is successful; false
	 * otherwise.
	 */
	public boolean deleteItem(Integer upc) {
		try {
			ps = con.prepareStatement("DELETE FROM item WHERE upc = ?");

			ps.setInt(1, upc.intValue());

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
	 * Returns an updatable result set for Item
	 */
	public ResultSet editItem() {
		try {
			ps = con.prepareStatement("SELECT i.* FROM item i",
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
