/*
 * $Id: ISVGAccessors.java,v 1.1 2013/11/13 15:52:40 jbmeslin Exp $
 */
package org.rcfaces.renderkit.svg.internal.component;

import org.rcfaces.core.component.familly.IContentAccessors;
import org.rcfaces.core.internal.contentAccessor.IContentAccessor;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.1 $ $Date: 2013/11/13 15:52:40 $
 */
public interface ISVGAccessors extends IContentAccessors {
    IContentAccessor getSVGAccessor();
}
