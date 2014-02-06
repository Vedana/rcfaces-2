/*
 * $Id: IPagerMessageCapability.java,v 1.1 2011/04/12 09:25:24 oeuillot Exp $
 */
package org.rcfaces.core.component.capability;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:24 $
 */
public interface IPagerMessageCapability {
    String getMessage();

    void setMessage(String message);

    String getZeroResultMessage();

    void setZeroResultMessage(String message);

    String getOneResultMessage();

    void setOneResultMessage(String message);

    String getManyResultsMessage();

    void setManyResultsMessage(String message);
}
