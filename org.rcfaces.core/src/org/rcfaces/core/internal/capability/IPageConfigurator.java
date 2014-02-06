/*
 * $Id: IPageConfigurator.java,v 1.1 2011/04/12 09:25:38 oeuillot Exp $
 */
package org.rcfaces.core.internal.capability;

import org.rcfaces.core.component.capability.ILiteralLocaleCapability;
import org.rcfaces.core.component.capability.ILiteralTimeZoneCapability;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:38 $
 */
public interface IPageConfigurator extends ILiteralLocaleCapability,
        ILiteralTimeZoneCapability {
    String getPageScriptType();
}
