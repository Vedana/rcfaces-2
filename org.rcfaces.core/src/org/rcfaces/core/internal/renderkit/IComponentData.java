/*
 * $Id: IComponentData.java,v 1.2 2011/10/11 09:29:09 oeuillot Exp $
 * 
 */
package org.rcfaces.core.internal.renderkit;

import java.io.Serializable;

import org.rcfaces.core.internal.IReleasable;

/**
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.2 $ $Date: 2011/10/11 09:29:09 $
 */
public interface IComponentData extends IProperties, IReleasable, IEventData {

    String getComponentParameter();

    String[] getComponentParameters();

    String getParameter(Serializable parameterName);

    String[] getParameters(Serializable parameterName);

    boolean isEventComponent();
}
