/*
 * $Id: IJavaScriptEnableMode.java,v 1.2 2013/01/11 15:45:00 jbmeslin Exp $
 */
package org.rcfaces.renderkit.html.internal;

/**
 * 
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/01/11 15:45:00 $
 */
public interface IJavaScriptEnableMode {

    void enableOnInit();

    void enableOnAccessKey();

    void enableOnFocus();

    void enableOnSubmit();

    void enableOnMessage();

    void enableOnOver();

    void enableOnLayout();

    boolean isOnInitEnabled();

}
