/*
 * $Id: IHtmlComponentWriter.java,v 1.1 2013/01/11 15:45:01 jbmeslin Exp $
 */
package org.rcfaces.renderkit.html.internal;

import org.rcfaces.core.internal.renderkit.IComponentWriter;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.1 $ $Date: 2013/01/11 15:45:01 $
 */
public interface IHtmlComponentWriter extends IComponentWriter {

    IHtmlComponentRenderContext getHtmlComponentRenderContext();

}
