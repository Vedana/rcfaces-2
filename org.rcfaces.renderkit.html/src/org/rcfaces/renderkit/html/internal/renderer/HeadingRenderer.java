/*
 * $Id: HeadingRenderer.java,v 1.1 2013/01/11 15:45:01 jbmeslin Exp $
 */
package org.rcfaces.renderkit.html.internal.renderer;

import org.rcfaces.core.component.HeadingComponent;
import org.rcfaces.core.component.TextComponent;
import org.rcfaces.core.internal.renderkit.IComponentRenderContext;
import org.rcfaces.core.internal.renderkit.WriterException;
import org.rcfaces.renderkit.html.internal.IHtmlElements;
import org.rcfaces.renderkit.html.internal.IHtmlWriter;
import org.rcfaces.renderkit.html.internal.JavaScriptClasses;
import org.rcfaces.renderkit.html.internal.util.HeadingTools;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.1 $ $Date: 2013/01/11 15:45:01 $
 */
public class HeadingRenderer extends TextRenderer {

    @Override
    protected String getTextElement(IHtmlWriter htmlWriter) {
        IComponentRenderContext componentRenderContext = htmlWriter
                .getComponentRenderContext();

        HeadingComponent textComponent = (HeadingComponent) componentRenderContext
                .getComponent();

        int ariaLevel = HeadingTools.computeHeadingLevel(textComponent);

        if (ariaLevel > 0) {
            if (ariaLevel > IHtmlElements.MAX_HEADING_LEVEL) {
                ariaLevel = IHtmlElements.MAX_HEADING_LEVEL;
            }
            return IHtmlElements.H_BASE + ariaLevel;
        }

        return super.getTextElement(htmlWriter);
    }

    protected String getJavaScriptClassName() {
        return JavaScriptClasses.HEADING;
    }

    @Override
    protected ICssStyleClasses createStyleClasses(IHtmlWriter htmlWriter) {
        ICssStyleClasses cssStyleClasses = super.createStyleClasses(htmlWriter);

        HeadingComponent headingComponent = (HeadingComponent) htmlWriter
                .getComponentRenderContext().getComponent();
        int ariaLevel = HeadingTools.computeHeadingLevel(headingComponent);

        if (ariaLevel > 0) {
            if (ariaLevel > IHtmlElements.MAX_HEADING_LEVEL) {
                ariaLevel = IHtmlElements.MAX_HEADING_LEVEL;
            }
            cssStyleClasses.addSuffix("_h" + ariaLevel);
        }

        return cssStyleClasses;
    }

    @Override
    protected void writeAriaLevelAttribute(IHtmlWriter htmlWriter,
            TextComponent textComponent) throws WriterException {
        HeadingComponent headingComponent = (HeadingComponent) textComponent;

        int ariaLevel = HeadingTools.computeHeadingLevel(headingComponent);

        if (ariaLevel > 0) {
            return;
        }

        super.writeAriaLevelAttribute(htmlWriter, textComponent);
    }

}