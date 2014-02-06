/*
 * $Id: ICssRenderer.java,v 1.1 2011/04/12 09:28:10 oeuillot Exp $
 */
package org.rcfaces.renderkit.html.internal;

import org.rcfaces.renderkit.html.internal.renderer.ICssStyleClasses;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:28:10 $
 */
public interface ICssRenderer {

    ICssStyleClasses getCssStyleClasses(IHtmlWriter htmlWriter);
}
