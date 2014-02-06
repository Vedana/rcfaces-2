/*
 * $Id: IHelpCapability.java,v 1.2 2013/01/11 15:46:57 jbmeslin Exp $
 * 
 */

package org.rcfaces.core.component.capability;

/**
 * 
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/01/11 15:46:57 $
 */
public interface IHelpCapability extends IToolTipTextCapability {

    /**
     * Returns a string value specifying the message to be shown by a <a
     * href="/comps/helpMessageZoneComponent.html">helpMessageZone Component</a>.
     * 
     * @return help message
     */
    String getHelpMessage();

    /**
     * Sets a string value specifying the message to be shown by a <a
     * href="/comps/helpMessageZoneComponent.html">helpMessageZone Component</a>.
     * 
     * @param message
     *            help message
     */
    void setHelpMessage(String message);

    /**
     * Returns an URL that points to a help page for the component. it is used
     * for example by a <a href="/comps/helpButtonComponent.html">helpButton
     * Component</a>.
     * 
     * @return help url
     */
    String getHelpURL();

    /**
     * Sets an URL that points to a help page for the component. it is used for
     * example by a <a href="/comps/helpButtonComponent.html">helpButton
     * Component</a>.
     * 
     * @param url
     *            help url
     */
    void setHelpURL(String url);
}
