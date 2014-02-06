/*
 * $Id: ITooltipGridRenderer.java,v 1.1 2013/12/11 10:19:48 jbmeslin Exp $
 */
package org.rcfaces.renderkit.html.internal.renderer;

import org.rcfaces.core.internal.capability.IColumnsContainer;
import org.rcfaces.core.internal.renderkit.WriterException;
import org.rcfaces.renderkit.html.internal.IHtmlWriter;

public interface ITooltipGridRenderer {

    Object[] getTooltipsRenderContextState(IColumnsContainer gridComponent);

    void renderTooltip(IHtmlWriter htmlWriter, IColumnsContainer gridComponent,
            String responseCharset, String rowValue, String rowIndex,
            String toolTipId) throws WriterException;

}
