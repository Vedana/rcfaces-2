/*
 * $Id: ITitledBorderRenderer.java,v 1.1 2011/04/12 09:25:46 oeuillot Exp $
 * 
 */
package org.rcfaces.core.internal.renderkit.border;

import org.rcfaces.core.internal.renderkit.IComponentWriter;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:46 $
 */
public interface ITitledBorderRenderer {
    void setText(IComponentWriter writer, String text, String textComponentId);
}
