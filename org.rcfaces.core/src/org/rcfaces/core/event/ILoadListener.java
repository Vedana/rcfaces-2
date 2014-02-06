/*
 * $Id: ILoadListener.java,v 1.1 2011/04/12 09:25:15 oeuillot Exp $
 * 
 */
package org.rcfaces.core.event;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.FacesListener;

/**
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:15 $
 */
public interface ILoadListener extends FacesListener {

    void processLoad(LoadEvent event) throws AbortProcessingException;
}
