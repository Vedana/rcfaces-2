/*
 * $Id: ISVGContentFamily.java,v 1.1 2013/11/13 15:52:40 jbmeslin Exp $
 */
package org.rcfaces.renderkit.svg.internal.util;

import org.rcfaces.core.internal.contentAccessor.ContentFamilies.ContentTypeImpl;
import org.rcfaces.core.lang.IContentFamily;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.1 $ $Date: 2013/11/13 15:52:40 $
 */
public interface ISVGContentFamily extends IContentFamily {

    public static final IContentFamily SVG = new ContentTypeImpl("svg");
}
