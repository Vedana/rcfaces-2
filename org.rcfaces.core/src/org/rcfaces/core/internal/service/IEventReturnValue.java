/*
 * $Id: IEventReturnValue.java,v 1.1 2011/04/12 09:25:45 oeuillot Exp $
 */
package org.rcfaces.core.internal.service;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:45 $
 */
public interface IEventReturnValue {

    void setReturnValue(Object ret);

    Object getReturnValue();
}
