package ca.ubc.cs304.main;

// File: CustomTable.java

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*; 
import java.text.*; 
import javax.swing.event.EventListenerList;


/*
 * CustomTable is a table that accepts a CustomTableModel as its data.
 * CustomTableModel is defined in CustomTableModel.java. CustomTable
 * extends JTable to add good column widths, mouse over display of a 
 * cell's data when the cell is selected, and sorting capabilities 
 * if a non-updatable result set was passed into CustomTableModel. 
 */
public class CustomTable extends JTable
{
    protected CustomTableModel model = null;
    protected EventListenerList listenerList = new EventListenerList();


    /*
     * Parameterized constructor. Accepts a
     * non-null CustomTableModel.
     */ 
    public CustomTable(CustomTableModel m)
    {
	super(m);

	// dataModel is a protected member of JTable
        model = (CustomTableModel)dataModel;

	initColumnSizes();	

	// this prevents column widths from shrinking when
	// there are many columns
	setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

	// The CustomTableModel inner class Sorter sorts a column in
	// ascending order when the column's header is double clicked. 
	// If the shift key is held down during the clicks, the 
	// column will be sorted in descending order. 
	(model.new Sorter(this)).addMouseListenerToHeader();

	// Inform user that the table columns can be sorted
	// if the table model is not updatable
	if (!model.isCellEditable(0,0))
	{
	    getTableHeader().setToolTipText("Double click to sort");
	}

	CellMouseListener cellListener = new CellMouseListener(this);
	addMouseMotionListener(cellListener);

	// There are no default renderers and editors for Date, Timestamp, and Time
	// so we need to create them. I will only create the renderer and editor for
	// Date. You need to create the rest.
	// You can ignore this if your database table doesn't deal with these 
	// types.
	setDefaultRenderer(java.sql.Date.class, new DefaultTableCellRenderer());
	setDefaultEditor(java.sql.Date.class, new DateCellEditor());
    }


    /*
     * This method picks good column widths. It is a modified
     * version of initColumnSizes() in TableRenderDemo.java in
     * Sun's Swing tutorial. It works only with Java 2 SDK versions
     * 1.3 or greater.
     */
    private void initColumnSizes() 
    {
        TableColumn column = null;

	// comp is the component used to render a cell or cell header.
	// We will compare the preferred width of the widest cell
	// in each column to the preferred width of the column's
	// header. The column width will be the maximum of the two.
        Component comp = null;

	int numColumns = getColumnCount();
	int numRows = getRowCount();
	int headerWidth = 0;

	// cellWidth will store the width of the widest cell 
	// in each column
	int cellWidth = 0;

	int temp = 0;

	// this is not the most efficient way of finding the widest 
	// cell in each column
        for (int i = 0; i < numColumns; i++) 
	{
            column = getColumnModel().getColumn(i);

	    comp = getTableHeader().getDefaultRenderer().
		getTableCellRendererComponent(this, column.getHeaderValue(), 
					      false, false, 0, i);
                           
	    headerWidth = comp.getPreferredSize().width; 
         
	    for (int j = 0; j < numRows; j++)
	    {
		comp = getDefaultRenderer(model.getColumnClass(i)).
		    getTableCellRendererComponent(this, getValueAt(j,i),
						  false, false, j, i);

		temp = comp.getPreferredSize().width;

		if (temp > cellWidth)
		{
		    cellWidth = temp;
		}
	    }

	    // the 20 is for extra padding
            column.setPreferredWidth(Math.max(headerWidth+20, cellWidth+20));
	    cellWidth = 0;
        }
    }


    /******************************************************************************
     * Below are the methods to add and remove ExceptionListeners.
     * 
     * Whenever an exception occurs in CustomTable, an exception event
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

    
    /*
     * This class allows a cell's data to be displayed over the mouse cursor 
     * when the mouse cursor is over the selected cell. Note: to actually have
     * the data displayed, the mouse cursor must first move a little bit in the
     * selected cell.
     */ 
    class CellMouseListener extends MouseMotionAdapter
    {
	private int row = -1;
	private int col = -1; 
	private Object oldValue = null; 
	private JTable table = null;
	

	public CellMouseListener(JTable table)
	{
	    this.table = table; 
	}


	public void mouseMoved(MouseEvent event)
	{	   	
	    Point p = event.getPoint();
	    int tempRow = table.rowAtPoint(p);
	    int tempCol = table.columnAtPoint(p);	   	   

	    if (tempRow == -1 || tempCol == -1)
	    {
		return;
	    } 
	
	    // return if the cell is not selected 
	    if (!table.getSelectionModel().isSelectedIndex(tempRow) ||
		!table.getColumnModel().getSelectionModel().isSelectedIndex(tempCol))
	    {
		// Remove the old tool tip from the previous cell selection (if there is one).
		// Don't remove if the mouse pointer is still in the same cell.
		if ( (row != -1 && col != -1) && (row != tempRow || col != tempCol) )
		{
		    ((JComponent)(table.getCellRenderer(row, col).getTableCellRendererComponent
				  (table, null, false, false, row, col))).setToolTipText(null);

		    // set row and col to -1 so we don't end up later removing the tool tip again
		    row = -1;
		    col = -1; 
		}

		return; 
	    }

	    Object obj = table.getValueAt(tempRow, tempCol);

	    // return if the mouse pointer is in the same cell and the value in the cell 
	    // has not changed
	    if (row == tempRow && col == tempCol)
	    {
		if ((obj == null && oldValue == null) || 
		    (obj != null && obj.equals(oldValue)))		  
		    return; 
	    }	    

	    // only when tempRow, tempCol, obj represent a valid cell whose value is 
	    // different from its old value can we set the values for row, col, and oldValue
	    row = tempRow;
	    col = tempCol;
	    oldValue = obj; 

	    // if obj is null or the string value of obj is the empty string, 
	    // we will not display any tool tip text
	    if (obj != null)
	    {		
		String value = obj.toString();
		
		if (value.length() != 0)
		{
		    ((JComponent)(table.getCellRenderer(row,col).getTableCellRendererComponent
				  (table, null, false, false, row, col))).setToolTipText(value);
		}
		else
		{		
		    ((JComponent)(table.getCellRenderer(row,col).getTableCellRendererComponent
				  (table, null, false, false, row, col))).setToolTipText(null);
		}
	    }
	    else
	    {	
		((JComponent)(table.getCellRenderer(row,col).getTableCellRendererComponent
			      (table, null, false, false, row, col))).setToolTipText(null);
	    }
	}
    }


    /*
     * Cell editor for java.sql.Date
     * Limited error checking is performed.
     * Dates must be in JDBC format.
     */ 
    class DateCellEditor extends DefaultCellEditor
    {
	JTextField textField;

	public DateCellEditor()
	{
	    super(new JTextField());

	    // editorComponent is a protected member of DefaultCellEditor
	    textField = (JTextField)editorComponent; 
	}


	// the editor must return a Date object
	public Object getCellEditorValue()
	{
	    try
	    {
		if (textField.getText().trim().length() == 0)
		    return null; 

		SimpleDateFormat fm = new SimpleDateFormat("yyyy-MM-dd");
		java.util.Date utilDate = fm.parse(textField.getText().trim());
		java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
		
		return sqlDate; 
	    }
	    catch (ParseException e)
	    {
		ExceptionEvent event = new ExceptionEvent(this, "Invalid Date Format. Date must be in form yyyy-MM-dd");
		fireExceptionGenerated(event);

		// this line will automatically generate an exception in CustomTableModel
		return textField.getText(); 
	    }
	}
    }
}
