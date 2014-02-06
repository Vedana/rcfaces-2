/*
 * $Id: IPageLoadedListener.java,v 1.1 2013/11/13 12:53:20 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.event;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.FacesListener;

/**
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.1 $ $Date: 2013/11/13 12:53:20 $
 */
public interface IPageLoadedListener extends FacesListener {

    void processLoad(LoadEvent event) throws AbortProcessingException;
}
