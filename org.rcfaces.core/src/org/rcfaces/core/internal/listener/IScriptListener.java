/*
 * $Id: IScriptListener.java,v 1.1 2011/04/12 09:25:39 oeuillot Exp $
 * 
 */
package org.rcfaces.core.internal.listener;

import javax.faces.event.FacesListener;

import org.rcfaces.core.internal.renderkit.IProcessContext;

/**
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:39 $
 */
public interface IScriptListener extends FacesListener {
    String getScriptType(IProcessContext processContext);

    String getCommand();
}
