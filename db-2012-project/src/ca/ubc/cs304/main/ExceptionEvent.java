package ca.ubc.cs304.main;

// File: ExceptionEvent.java

import java.util.EventObject;


/*
 * A subject (component) "fires" (creates) an ExceptionEvent object
 * whenever an exception occurs in the subject. A component can register to 
 * receive exception events from the subject by calling the subject's 
 * addExceptionListener() method.  This component is now an ExceptionListener. 
 * ExceptionEvent allows listeners to obtain the exception message 
 * that was generated.
 *
 * All events in Swing should extend EventObject
 */
public class ExceptionEvent extends EventObject
{
    String ExceptionMessage;

    // source cannot be null
    public ExceptionEvent(Object source, String exceptionMessage)
    {
	super(source);
	ExceptionMessage = exceptionMessage;
    }

    public String getMessage()
    {
	return ExceptionMessage;
    }

    /*
     * Override the super class's toString() method
     */
    public String toString()
    {
	return ExceptionMessage;
    }
}
