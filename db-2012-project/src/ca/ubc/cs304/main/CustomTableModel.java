package ca.ubc.cs304.main;

// File: CustomTableModel.java


import java.util.*;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import java.sql.*;
import java.math.BigDecimal;
import java.awt.event.*;    
import javax.swing.table.*;
import javax.swing.event.TableModelEvent;


/*
 * CustomTableModel is a table model that stores and maintains the data   
 * in a result set. The data in the result set is cached in a Vector 
 * object to improve performance. This model extends the AbstractTableModel 
 * class because this class provides methods to dispatch TableModelEvents to 
 * event handlers that listen for changes in the table's data.
 *
 * This class allows components to register to receive exception events 
 * when an exception occurs in the model.  Exceptions are part of the 
 * Java 2 API, but exception events aren't.  We are creating our own 
 * event/listener. The file ExceptionEvent.java contains the 
 * class definition of ExceptionEvent. The file ExceptionListener.java
 * contains the interface that components need to implement in order 
 * to receive exception events. 
 *
 * Issues/Constraints:
 * 
 * 1) The ResultSetMetaData's getColumnType() returns Types.TIMESTAMP 
 * for an Oracle date column because the Oracle date datatype has both 
 * a date and time component. This table model allows you to specify 
 * whether you want to deal with dates or times or both by setting the 
 * dateTime member variable.  dateTime is set to Types.DATE by default.
 * All dates, times, and timestamps must be in JDBC format.
 *
 * 2) If a change to a cell results in a tuple no longer
 * satisfying the query, the tuple will remain in the JTable.
 * However, once the change is made, if you try to make another
 * change to the same tuple, the Oracle JDBC driver will not  
 * allow you to do so.
 *
 * 3) This class is bias towards Oracle because it deals with the
 * mappings between Java and Oracle datatypes. 
 */
public class CustomTableModel extends AbstractTableModel
{
    // possible values for dateTime are
    // Types.DATE, Types.TIMESTAMP, and Types.TIME 
    int                  dateTime = Types.DATE;

    Connection                con = null; 
    ResultSet                  rs = null;
    ResultSetMetaData  rsMetaData = null;
    int                numColumns; 
    String[]          columnNames = null;

    // whether or not the result set is updatable
    boolean           isUpdatable; 

    // a vector of rows; each row is a vector of columns
    Vector                   rows = new Vector();


    /*
     * Parameterized constructor.
     * Accepts a non-null database connection and a non-null 
     * scrollable ResultSet argument. The data in the ResultSet 
     * is copied to the rows vector. If changes in the table's 
     * data are to be propagated to the database, the result set
     * must be updatable and the data represented by the result 
     * set must be stored in the database represented by the 
     * connection object.
     */
    public CustomTableModel(Connection connect, ResultSet rset)
    {
	try
        {
	    con = connect;
	    rs = rset;
	    rsMetaData = rs.getMetaData();
	    numColumns = rsMetaData.getColumnCount();
	    columnNames = new String[numColumns];
     
	    // Determine whether the result set is updatable.
	    // Sometimes getConcurrency() does not return the actual
	    // concurrency type of the ResultSet (e.g. when the query 
	    // has an order by clause); nevertheless, the
	    // ResultSet will still not be updatable if it does
	    // not meet the criteria listed in Tutorial 2.
	    if (rs.getConcurrency() == ResultSet.CONCUR_UPDATABLE)
	    {
		isUpdatable = true;
	    }
	    else
	    {
		isUpdatable = false; 
	    }

	    for (int i = 0; i < numColumns; i++)
	    {
		// ResultSet columns start at 1 but array indices start at 0
		columnNames[i] = rsMetaData.getColumnLabel(i+1);
	    }
 
	    while(rs.next())
	    {
		Vector tempRow = new Vector(numColumns);

		for (int i = 0; i < numColumns; i++) 
		{
		    switch(rsMetaData.getColumnType(i+1))
		    {
			// mappings are based on Table 3 in JDBC/Oracle
			// Tutorial 1		

	               case Types.CHAR:
	               case Types.VARCHAR:
	               case Types.LONGVARCHAR:
			   String s = rs.getString(i+1);
			   tempRow.add(s);
			   break;
	    				   
		       case Types.INTEGER:
			   int in = rs.getInt(i+1);
			   if (rs.wasNull())
			       tempRow.add(null);
			   else
			       tempRow.add(new Integer(in));
			   break;

	               case Types.SMALLINT:
			   short sh = rs.getShort(i+1);
			   if (rs.wasNull())
			       tempRow.add(null);
			   else
			       tempRow.add(new Short(sh));
			   break;

		       case Types.TINYINT:
			   byte b = rs.getByte(i+1);
			   if (rs.wasNull())
			       tempRow.add(null);
			   else
			       tempRow.add(new Byte(b));
			   break;

		      case Types.FLOAT:
		      case Types.DOUBLE:
			  double d = rs.getDouble(i+1);
			  if (rs.wasNull())
			      tempRow.add(null);
			  else
			      tempRow.add(new Double(d));
			  break;

		      case Types.REAL:
			  float f = rs.getFloat(i+1);
			  if (rs.wasNull())
			      tempRow.add(null);
			  else
			      tempRow.add(new Float(f));
			  break;

		      case Types.BIT:
			  boolean bo = rs.getBoolean(i+1);
			  if (rs.wasNull())
			      tempRow.add(null);
			  else
			      tempRow.add(new Boolean(bo));
			  break;

		      case Types.BIGINT:
			  long l = rs.getLong(i+1);
			  if (rs.wasNull())
			      tempRow.add(null);
			  else
			      tempRow.add(new Long(l));
			  break;

		      case Types.NUMERIC:
		      case Types.DECIMAL:
			  BigDecimal bd = rs.getBigDecimal(i+1);
			  tempRow.add(bd); 
			  break;

		      case Types.DATE:
			  java.sql.Date date = rs.getDate(i+1);
			  tempRow.add(date);
			  break;

		      case Types.TIMESTAMP:
			  if (dateTime == Types.TIMESTAMP)
			  {
			      Timestamp ts = rs.getTimestamp(i+1);
			      tempRow.add(ts);
			      break; 
			  }
			  if (dateTime == Types.TIME)
			  {
			      Time t = rs.getTime(i+1);
			      tempRow.add(t);
			      break; 
			  }
			  if (dateTime == Types.DATE)
			  {
			      java.sql.Date date2 = rs.getDate(i+1);
			      tempRow.add(date2);
			      break; 
			  } 

		      case Types.TIME:	       
			  Time t2 = rs.getTime(i+1);
			  tempRow.add(t2);
			  break;

		      default:
			  Object o = rs.getObject(i+1);
			  tempRow.add(o); 
			  break;
		    } 				 
		} // end for loop
		
		rows.add(tempRow);
	    } // end while loop
	}
	catch (SQLException ex)
	{
	    ExceptionEvent event = new ExceptionEvent(this, ex.getMessage());
	    
	    // notify all registered listeners
	    fireExceptionGenerated(event);
	}
    }

    
    /******************************************************************************
     * Below is the TableModel Interface Implementation
     * 
     * The methods in this interface are used by JTable
     ******************************************************************************/ 


    /*
     * Returns the most specific superclass for all the cell values in the 
     * given column. This method is used by JTable to set up a default renderer 
     * and editor for the column. If you don't like the default renderer and 
     * editor, you can always create your own. Note: in Java 2 SDK version 1.2x,
     * some column classes don't have a default renderer and editor, e.g., any
     * class derived from Number.
     */
    public Class getColumnClass(int columnIndex)
    {
	try
	{ 
	    switch(rsMetaData.getColumnType(columnIndex+1))
	    {
		// mappings are based on Table 3 in JDBC/Oracle
		// Tutorial 1		

		// In Java 2 SDK v1.2x and 1.3x, there are no default 
		// renderers and editors for date, time, and timestamp, 
		// so you need to create your own for those types 

	        case Types.CHAR:
	        case Types.VARCHAR:
	        case Types.LONGVARCHAR:
		    return String.class;
	    	
	        case Types.INTEGER:
		    return Integer.class;

		case Types.SMALLINT:
		    return Short.class;

	        case Types.TINYINT:
		    return Byte.class;

	        case Types.FLOAT:
	        case Types.DOUBLE:
		    return Double.class;

	        case Types.REAL:
		    return Float.class;

		// There is no boolean datatype in Oracle
		case Types.BIT:
		    return Boolean.class;

	        case Types.BIGINT:
		    return Long.class;

	        case Types.NUMERIC:
	        case Types.DECIMAL:
		    return BigDecimal.class;

	        case Types.DATE:
		    return java.sql.Date.class;

	        case Types.TIMESTAMP:
		    return (dateTime == Types.DATE ? java.sql.Date.class :
			    (dateTime == Types.TIMESTAMP ? Timestamp.class : Time.class)); 

	        case Types.TIME:
		    return Time.class; 

	        default:
		    return Object.class;
	    } 
	}
	catch (SQLException ex)
	{
	    ExceptionEvent event = new ExceptionEvent(this, ex.getMessage());
	    fireExceptionGenerated(event);
	    return Object.class;
	}
    }


    /*
     * JTable uses this method to determine how many columns to display
     */ 
    public int getColumnCount()
    {
	return numColumns;
    }

    
    /*
     * Returns the column name.
     * This is used to initialize the given column's header name
     */ 
    public String getColumnName(int columnIndex)
    {
	return columnNames[columnIndex];
    }
     

    /*
     * JTable uses this method to determine how many rows to display
     */ 
    public int getRowCount()
    {
	return rows.size();
    }


    /*
     * Returns the value at the given cell
     */ 
    public Object getValueAt(int rowIndex, int columnIndex)
    {
	Vector aRow = (Vector)rows.get(rowIndex);

	return aRow.get(columnIndex);
    }


    /*
     * This method determines whether a setValueAt() on the given cell
     * will change its value
     * 
     */ 
    public boolean isCellEditable(int row, int column)
    {
	try
	{
	    // can edit cell if the result set is updatable and 
	    // the column is not read only
	    if (isUpdatable && rsMetaData.isWritable(column+1))
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
     * Sets value at the given cell to aValue.
     * If the ResultSet is updatable then the ResultSet and database is updated.
     */ 
    public void setValueAt(Object aValue, int rowIndex, int columnIndex)
    {
	try
	{
	    Object oldValue = getValueAt(rowIndex,columnIndex);

	    // only update the data if the new value is different
	    // from the old one

	    if (oldValue == null && aValue == null)
	    {
		return; 
	    }

	    // can use equals() only if oldValue is not null
	    if (oldValue != null)
	    {
		if (oldValue.equals(aValue))
		{
		    return;
		} 
	    }
	    
	    // JTable is 0 based while ResultSet is 1 based
	    rs.absolute(rowIndex+1);

	    updateResultSet(rs, aValue, rowIndex, columnIndex);
	    
	    rs.updateRow();

	    // save the change
	    con.commit();
	    
	    Vector aRow = (Vector)rows.get(rowIndex);
	    
	    aRow.set(columnIndex, aValue);	

	    // notify all TableModelListeners about the change
	    fireTableCellUpdated(rowIndex, columnIndex);
	}
	catch (SQLException ex)
	{
	    ExceptionEvent event = new ExceptionEvent(this, ex.getMessage());
	    fireExceptionGenerated(event);

	    try
	    {
		// undo the change
		con.rollback();
	    }
	    catch (SQLException ex2)
	    {
		event = new ExceptionEvent(this, ex2.getMessage());
		fireExceptionGenerated(event);
	    }
	}
	catch (Exception ex)
	{
	    ExceptionEvent event = new ExceptionEvent(this, ex.getMessage());
	    fireExceptionGenerated(event);
	}
    }


    /*
     * Helper for setValueAt().  This method is not part of the TableModel
     * interface.  This function performs the ResultSet updateXXX().
     */ 
    private void updateResultSet(ResultSet rs, Object aValue, int rowIndex, int columnIndex) throws Exception
    {
	// if cell is empty insert null
	if ( aValue == null || aValue.toString().trim().length() == 0 )
	{
	    rs.updateNull(columnIndex+1);
	    return; 
	}
	
	switch(rsMetaData.getColumnType(columnIndex+1))
	{
	  // mappings are based on Table 3 in JDBC/Oracle
	  // Tutorial 1		

	  case Types.CHAR:
	  case Types.VARCHAR:
	  case Types.LONGVARCHAR:
	    rs.updateString(columnIndex+1, (String)aValue);
	    return;
	    	
	  case Types.INTEGER:
	      rs.updateInt(columnIndex+1, ((Integer)aValue).intValue());
	      return;

	  case Types.SMALLINT:
	      rs.updateShort(columnIndex+1, ((Short)aValue).shortValue());
	      return;

	  case Types.TINYINT:
	      rs.updateByte(columnIndex+1, ((Byte)aValue).byteValue());
	      return;

	  case Types.FLOAT:
	  case Types.DOUBLE:
	      rs.updateDouble(columnIndex+1, ((Double)aValue).doubleValue());
	      return;

	  case Types.REAL:
	      rs.updateFloat(columnIndex+1, ((Float)aValue).floatValue());
	      return;

	  case Types.BIT:
	      rs.updateBoolean(columnIndex+1, ((Boolean)aValue).booleanValue());
	      return;

	  case Types.BIGINT:
	      rs.updateLong(columnIndex+1, ((Long)aValue).longValue());
	      return;

	  case Types.NUMERIC:
	  case Types.DECIMAL:
	      rs.updateBigDecimal(columnIndex+1, (BigDecimal)aValue);
	      return;

	  case Types.DATE:
	      rs.updateDate(columnIndex+1, (java.sql.Date)aValue);
	      return;

	  case Types.TIMESTAMP:
	      if (dateTime == Types.TIMESTAMP)
	      {
		  rs.updateTimestamp(columnIndex+1, (Timestamp)aValue);
	      }
	      if (dateTime == Types.TIME)
	      {
		  rs.updateTime(columnIndex+1, (Time)aValue);
	      }
	      if (dateTime == Types.DATE)
	      {
		  rs.updateDate(columnIndex+1, (java.sql.Date)aValue);
	      }
	      return; 

	  case Types.TIME:	       
	      rs.updateTime(columnIndex+1, (Time)aValue);
	      return;

	  default:
	      rs.updateObject(columnIndex+1, aValue);
	      return;
	} 		
    }


    /******************************************************************************
     * Below are the methods to add and remove ExceptionListeners.
     * 
     * Whenever an exception occurs in CustomTableModel, an exception event
     * is sent to all registered ExceptionListeners.
     ******************************************************************************/ 


    public void addExceptionListener(ExceptionListener l) 
    {
	// listenerList is an EventListenerList object.
	// It is a protected member of AbstractTableModel.
	listenerList.add(ExceptionListener.class, l);
    }


    public void removeExceptionListener(ExceptionListener l) 
    {
	listenerList.remove(ExceptionListener.class, l);
    }

    
    /*
     * This method notifies all registered ExceptionListeners.
     * The code below is similar to the sample in the Java 2 API
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


    /*
     * This class sorts the data in a given column.
     * This class will not sort if the result set is
     * updatable because after the table is sorted,
     * a cell's row index in the table won't match 
     * the cell's row index in the result set. This 
     * means that updates to the table won't be
     * properly propagated to the database.
     */ 
    class Sorter extends MouseAdapter implements Comparator
    {
	JTable table = null; 
	int sortIndex = -1;  // the column index to sort on
	boolean ascendingOrder = true; 

	/*
	 * Parameterized Constructor
	 */ 
	public Sorter(JTable t)
	{
	    table = t; 
	}


	/*
	 * Registers the current object to receive
	 * mouse events from the table header
	 */ 
	public void addMouseListenerToHeader()
	{
	    JTableHeader header = table.getTableHeader(); 
	    header.addMouseListener(this); 
	}


	/*
	 * When the user double clicks the header, the column 
	 * will be sorted in ascending order. If the shift key
	 * was held down as well then the column will be 
	 * sorted in descending order.
	 */ 
	public void mouseClicked(MouseEvent e)
	{
	    if (isUpdatable)
	    {
		return; 
	    }

	    if (e.getClickCount() == 2)
	    {
		ascendingOrder = ((e.getModifiers()&InputEvent.SHIFT_MASK) == 0);

		TableColumnModel colModel = table.getColumnModel();

		int viewIndex = colModel.getColumnIndexAtX(e.getX());

		if (viewIndex < 0)
		{
		    return; 
		}

		// we need to convert the column index from the table view's
		// coordinate system to the table model's coordinate system
		int modelIndex = colModel.getColumn(viewIndex).getModelIndex(); 
		
		// Remove the sort indicator from the previously sorted column.
		if (sortIndex >= 0)
		{
		    // sortIndex is in the model's coordinate system but
		    // getColumn() requires an index in the view's coordinate system,
		    // so we need to convert
		    TableColumn oldColumn = colModel.getColumn(table.convertColumnIndexToView(sortIndex));
		    String oldHeaderValue = oldColumn.getHeaderValue().toString(); 
		    int lastIndex = oldHeaderValue.lastIndexOf('<'); 
		    oldColumn.setHeaderValue(oldHeaderValue.substring(0,lastIndex-1));
		}

		sortIndex = modelIndex; 
		
		/// add sort indicator to column header
		TableColumn newColumn = colModel.getColumn(table.convertColumnIndexToView(sortIndex));
		String newHeaderValue = newColumn.getHeaderValue().toString(); 
		newColumn.setHeaderValue(newHeaderValue + " <");

		Collections.sort(rows, this);
	    }
	}


	/**********************************************
	 * Begin Comparator interface implementation.
	 * By implementing this interface, we can sort 
	 * the objects in the table.
	 **********************************************/


	/*
	 * This compares the column value of one row to 
	 * the value of another.
	 */ 
	public int compare(Object obj1, Object obj2)
	{
	    int result = -99; 

	    Object o1 = ((Vector)obj1).get(sortIndex);
	    Object o2 = ((Vector)obj2).get(sortIndex);

	    if (o1 == null)
	    {
		if (o2 == null)
		{
		    result = 0; 
		}
		else
		{
		    result = -1; 
		}
	    }
	    else
	    {
	      if (o2 == null)
	      {
		result = 1; 
	      }
	    }

	    if (result != -99)
	    {
		return (ascendingOrder ? result : -result); 
	    }

	    try
	    {
		switch(rsMetaData.getColumnType(sortIndex+1))
		{
		  case Types.CHAR:
		  case Types.VARCHAR:
		  case Types.LONGVARCHAR:
		      result = ((String)o1).compareTo((String)o2);
		      break; 

		  case Types.INTEGER:
		      result = ((Integer)o1).compareTo((Integer)o2);
		      break; 

		  case Types.SMALLINT:
		      result = ((Short)o1).compareTo((Short)o2);
		      break; 

		  case Types.TINYINT:
		      result = ((Byte)o1).compareTo((Byte)o2);
		      break; 

		  case Types.FLOAT:
		  case Types.DOUBLE:
		      result = ((Double)o1).compareTo((Double)o2);
		      break; 

		  case Types.REAL:
		      result = ((Float)o1).compareTo((Float)o2);
		      break; 

		  case Types.BIT:
		      boolean b1 = ((Boolean)o1).booleanValue();
		      boolean b2 = ((Boolean)o2).booleanValue();
		      if (b1 == true)
		      {
			  if (b2 == true)
			  {
			      result = 0; 
			  }
			  else
			  {
			      result = 1; 
			  }
		      }
		      else
		      {
			  if (b2 == true)
			  {
			      result = -1; 
			  }
			  else
			  {
			      result = 0; 
			  }
		      }
		      break;

		  case Types.BIGINT:
		      result = ((Long)o1).compareTo((Long)o2);
		      break;

		  case Types.NUMERIC:
		  case Types.DECIMAL:
		      result = ((BigDecimal)o1).compareTo((BigDecimal)o2);
		      break;

		  case Types.DATE:
		      result = ((java.sql.Date)o1).compareTo((java.sql.Date)o2);
		      break;

		  case Types.TIMESTAMP:
		      if (dateTime == Types.TIMESTAMP)
		      {
			  result = ((Timestamp)o1).compareTo((Timestamp)o2);
		      }
		      if (dateTime == Types.TIME)
		      {
			  result = ((Time)o1).compareTo((Time)o2);
		      }
		      if (dateTime == Types.DATE)
		      {
			  result = ((java.sql.Date)o1).compareTo((java.sql.Date)o2);
		      }
		      break; 

		  case Types.TIME:	       
		      result = ((Time)o1).compareTo((Time)o2);
		      break; 
		}
	    }
	    catch (SQLException ex)
	    {
		ExceptionEvent event = new ExceptionEvent(this, ex.getMessage());
		fireExceptionGenerated(event);
	    }

	    if (ascendingOrder)
	    {
		return result; 
	    }
	    else
	    {
		return -result; 
	    }
	}

	/*
	 * Determines whether some other object is equal to this Comparator.
	 */ 
	public boolean equals(Object obj)
	{
	    if (obj instanceof Sorter)
	    {
		Sorter s = (Sorter)obj;

		if (ascendingOrder == s.ascendingOrder && sortIndex == s.sortIndex)
		{
		    return true; 
		}		
	    }
	    return false; 
	}
	// end Comparator interface implementation
    }
}

