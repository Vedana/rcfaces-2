/*
 * $Id: IClientBrowser.java,v 1.2 2013/11/13 12:53:32 jbmeslin Exp $
 */
package org.rcfaces.renderkit.html.internal.agent;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/11/13 12:53:32 $
 */
public interface IClientBrowser extends IUserAgent {

    String getUserAgent();

    String getBrowserId();

    String getBrowserIdAndVersion();

    /**
     * 
     * @return Boolean if mobile version is determined, or <code>null</code> if
     *         not detected.
     */
    Boolean isMobileVersion();
}
