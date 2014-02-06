/*
 * $Id: IContentFamily.java,v 1.1 2011/04/12 09:25:49 oeuillot Exp $
 */
package org.rcfaces.core.lang;

import org.rcfaces.core.internal.contentAccessor.ContentFamilies.ContentTypeImpl;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:49 $
 */
public interface IContentFamily {

    public static final IContentFamily IMAGE = new ContentTypeImpl("image");

    public static final IContentFamily HELP = new ContentTypeImpl("help");

    public static final IContentFamily SCRIPT = new ContentTypeImpl("script");

    public static final IContentFamily STYLE = new ContentTypeImpl("style");

    public static final IContentFamily USER = new ContentTypeImpl("user");

    public static final IContentFamily JSP = new ContentTypeImpl("jsp");

    int getOrdinal();
}
