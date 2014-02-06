/*
 * $Id: JSONException.java,v 1.1 2011/04/12 09:25:50 oeuillot Exp $
 */
package org.rcfaces.core.internal.util.json;

/**
 * The JSONException is thrown by the JSON.org classes then things are amiss.
 * 
 * @author JSON.org
 * @version 2
 */
public class JSONException extends Exception {
    private static final long serialVersionUID = 6890284447052696055L;

    private final Throwable cause;

    /**
     * Constructs a JSONException with an explanatory message.
     * 
     * @param message
     *            Detail about the reason for the exception.
     */
    public JSONException(String message) {
        super(message);
        
        this.cause = null;
    }

    public JSONException(Throwable t) {
        super(t);

        this.cause = t;
    }

    public Throwable getCause() {
        return cause;
    }
}