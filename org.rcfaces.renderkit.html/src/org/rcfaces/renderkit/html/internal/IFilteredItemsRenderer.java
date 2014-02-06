/*
 * $Id: IFilteredItemsRenderer.java,v 1.1 2011/04/12 09:28:11 oeuillot Exp $
 * 
 */
package org.rcfaces.renderkit.html.internal;

import org.rcfaces.core.component.capability.IFilterCapability;
import org.rcfaces.core.internal.renderkit.WriterException;
import org.rcfaces.core.model.IFilterProperties;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:28:11 $
 */
public interface IFilteredItemsRenderer {
    void encodeFilteredItems(IJavaScriptWriter writer,
            IFilterCapability component, IFilterProperties filterProperties,
            int maxResultNumber) throws WriterException;

}
