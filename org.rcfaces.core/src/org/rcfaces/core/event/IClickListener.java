/*
 * $Id: IClickListener.java,v 1.1 2013/01/11 15:47:01 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.event;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.FacesListener;

/**
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.1 $ $Date: 2013/01/11 15:47:01 $
 */
public interface IClickListener extends FacesListener {

    void processClick(ClickEvent event) throws AbortProcessingException;
}
