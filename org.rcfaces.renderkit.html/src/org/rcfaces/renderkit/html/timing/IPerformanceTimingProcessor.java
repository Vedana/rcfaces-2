/*
 * $Id: IPerformanceTimingProcessor.java,v 1.1 2013/01/11 15:45:05 jbmeslin Exp $
 */
package org.rcfaces.renderkit.html.timing;

import javax.faces.context.FacesContext;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.1 $ $Date: 2013/01/11 15:45:05 $
 */
public interface IPerformanceTimingProcessor {
    void process(FacesContext facesContext, IPerformanceTiming timing);
}
