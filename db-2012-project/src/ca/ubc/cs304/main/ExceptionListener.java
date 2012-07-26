package ca.ubc.cs304.main;

// File: ExceptionListener.java

import java.util.EventListener;

/*
 * The listener interface for receiving exception events. The object that is 
 * interested in receiving an exception event must implement this interface. 
 * To register a component to receive exception events from a particular
 * subject (another component), add the component to the subject's listenerList 
 * using the addExceptionListener() method.  When the exception event occurs, that 
 * component's exceptionGenerated() method is invoked.
 *
 * All event listeners in Swing should extend EventListener 
 */ 
public interface ExceptionListener extends EventListener
{
    public void exceptionGenerated(ExceptionEvent ex);
}
