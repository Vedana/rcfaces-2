/*
 * $Id: IImageAccessors.java,v 1.1 2011/04/12 09:25:48 oeuillot Exp $
 */
package org.rcfaces.core.internal.component;

import org.rcfaces.core.component.familly.IContentAccessors;
import org.rcfaces.core.internal.contentAccessor.IContentAccessor;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:48 $
 */
public interface IImageAccessors extends IContentAccessors {
    IContentAccessor getImageAccessor();
}
