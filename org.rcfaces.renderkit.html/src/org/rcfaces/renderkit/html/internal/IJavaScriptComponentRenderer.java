/*
 * $Id: IJavaScriptComponentRenderer.java,v 1.1 2011/04/12 09:28:10 oeuillot Exp $
 * 
 */
package org.rcfaces.renderkit.html.internal;


import org.rcfaces.core.internal.renderkit.WriterException;

/**
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:28:10 $
 */
public interface IJavaScriptComponentRenderer {
    void initializeJavaScript(IJavaScriptWriter javaScriptWriter)
            throws WriterException;

    void initializePendingComponents(IJavaScriptWriter writer)
            throws WriterException;

    void initializeJavaScriptComponent(IJavaScriptWriter javaScriptWriter)
            throws WriterException;

    void releaseJavaScript(IJavaScriptWriter javaScriptWriter)
            throws WriterException;

    void addRequiredJavaScriptClassNames(IHtmlWriter writer,
            IJavaScriptRenderContext javaScriptRenderContext);
}
