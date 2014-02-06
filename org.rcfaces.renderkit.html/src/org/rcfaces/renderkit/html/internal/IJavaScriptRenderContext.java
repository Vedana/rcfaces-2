/*
 * $Id: IJavaScriptRenderContext.java,v 1.2 2013/11/13 12:53:28 jbmeslin Exp $
 * 
 */
package org.rcfaces.renderkit.html.internal;

import org.rcfaces.core.internal.renderkit.IScriptRenderContext;
import org.rcfaces.core.internal.renderkit.WriterException;
import org.rcfaces.core.internal.repository.IRepository;
import org.rcfaces.core.internal.repository.IRepository.ICriteria;
import org.rcfaces.core.internal.repository.IRepository.IFile;
import org.rcfaces.renderkit.html.internal.javascript.IJavaScriptRepository;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/11/13 12:53:28 $
 */
public interface IJavaScriptRenderContext extends IScriptRenderContext {

    ICriteria getCriteria();

    boolean isInitialized();

    void forceJavaScriptStub();

    boolean isJavaScriptStubForced();

    void clearJavaScriptStubForced();

    void computeRequires(IHtmlWriter writer,
            IJavaScriptComponentRenderer renderer);

    IRepository.IFile[] popRequiredFiles();

    boolean isRequiresPending();

    IJavaScriptRenderContext createChild();

    void pushChild(IJavaScriptRenderContext javaScriptRenderContext,
            IHtmlWriter htmlWriter) throws WriterException;

    void popChild(IJavaScriptRenderContext javaScriptRenderContext,
            IHtmlWriter htmlWriter) throws WriterException;

    String allocateVarName();

    String allocateString(String text, boolean mustDeclare[]);

    String allocateComponentVarId(String componentId, boolean mustDeclare[]);

    void initializeJavaScriptDocument(IJavaScriptWriter writer)
            throws WriterException;

    void appendRequiredClass(String className, String requiredId);

    boolean canLazyTagUsesBrother();

    IJavaScriptWriter removeJavaScriptWriter(IHtmlWriter writer);

    IJavaScriptWriter getJavaScriptWriter(IHtmlWriter writer,
            IJavaScriptComponentRenderer javaScriptComponent)
            throws WriterException;

    void initializePendingComponents(IJavaScriptWriter writer)
            throws WriterException;

    void initializeJavaScriptComponent(IJavaScriptWriter writer)
            throws WriterException;

    void releaseComponentJavaScript(IJavaScriptWriter writer,
            boolean sendComplete, AbstractHtmlRenderer htmlComponentRenderer)
            throws WriterException;

    void declareLazyJavaScriptRenderer(IHtmlWriter writer);

    boolean isJavaScriptRendererDeclaredLazy(IHtmlWriter writer);

    boolean isCollectorMode();

    void appendRequiredFiles(IFile[] files);

    void includeJavaScript(IHtmlWriter htmlWriter, String src,
            String javaScriptSrcCharSet) throws WriterException;

    void writeRaw(IHtmlWriter htmlWriter, String text) throws WriterException;

    IJavaScriptRepository getRepository();

    String convertSymbol(String className, String memberName);
}
