/*
 * $Id: ICellImageSettings.java,v 1.1 2011/04/12 09:25:39 oeuillot Exp $
 */
package org.rcfaces.core.internal.capability;

import org.rcfaces.core.component.capability.ICellImageCapability;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:39 $
 */
public interface ICellImageSettings extends ICellImageCapability {
    boolean isDefaultCellImageURLSetted();

    boolean isCellImageURLSetted();

}
