/*
 * $Id: ICriteriaListener.java,v 1.1 2013/01/11 15:47:01 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.event;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.FacesListener;

/**
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.1 $ $Date: 2013/01/11 15:47:01 $
 */
public interface ICriteriaListener extends FacesListener {

	void processCriteriaChanged(CriteriaEvent event) throws AbortProcessingException;
}
