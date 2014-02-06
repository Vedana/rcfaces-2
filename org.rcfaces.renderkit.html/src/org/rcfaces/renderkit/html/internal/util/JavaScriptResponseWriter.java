/*
 * $Id: JavaScriptResponseWriter.java,v 1.4 2013/11/13 12:53:32 jbmeslin Exp $
 * 
 */
package org.rcfaces.renderkit.html.internal.util;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

import org.rcfaces.core.internal.renderkit.IComponentRenderContext;
import org.rcfaces.core.internal.renderkit.IRenderContext;
import org.rcfaces.core.internal.renderkit.WriterException;
import org.rcfaces.core.internal.repository.IRepository.IFile;
import org.rcfaces.renderkit.html.internal.AbstractHtmlComponentRenderContext;
import org.rcfaces.renderkit.html.internal.IHtmlComponentRenderContext;
import org.rcfaces.renderkit.html.internal.IHtmlWriter;
import org.rcfaces.renderkit.html.internal.IJavaScriptRenderContext;
import org.rcfaces.renderkit.html.internal.IJavaScriptWriter;
import org.rcfaces.renderkit.html.internal.IObjectLiteralWriter;
import org.rcfaces.renderkit.html.internal.JavaScriptRenderContext;
import org.rcfaces.renderkit.html.internal.codec.JavascriptCodec;
import org.rcfaces.renderkit.html.internal.javascript.JavaScriptRepositoryServlet;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.4 $ $Date: 2013/11/13 12:53:32 $
 */
public class JavaScriptResponseWriter extends
        AbstractHtmlComponentRenderContext implements IJavaScriptWriter {
    

    private final PrintWriter out;

    private final String characterEncoding;

    private String componentVarName;

    private Map<String, String> strings;

    private boolean symbolsInitialized;

    private Map symbols;

    private IJavaScriptRenderContext javaScriptRenderContext;

    private ServletContext servletContext;

    private IRenderContext renderContext;

    private IHtmlComponentRenderContext componentRenderContext;

    private boolean ignoreComponentInitialization;

    public JavaScriptResponseWriter(FacesContext facesContext, PrintWriter out,
            String characterEncoding, UIComponent component, String componentId) {
        super(facesContext, component, componentId);

        this.out = out;
        this.characterEncoding = characterEncoding;
    }

    public JavaScriptResponseWriter(ServletContext servletContext,
            PrintWriter out, String characterEncoding) {
        super(null, null, null);

        this.out = out;
        this.servletContext = servletContext;
        this.characterEncoding = characterEncoding;
    }

    public final String getResponseCharacterEncoding() {
        return characterEncoding;
    }

    public IJavaScriptRenderContext getJavaScriptRenderContext() {
        if (javaScriptRenderContext == null) {
            javaScriptRenderContext = new JavaScriptRenderContext(
                    getFacesContext());
        }
        return javaScriptRenderContext;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.rcfaces.core.internal.renderkit.html.IJavaScriptWriter#getComponentVarName()
     */
    public String getComponentVarName() {
        if (componentVarName != null) {
            return componentVarName;
        }

        componentVarName = getJavaScriptRenderContext().allocateVarName();

        return componentVarName;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.rcfaces.core.internal.renderkit.html.IJavaScriptWriter#write(java.lang.String)
     */
    public IJavaScriptWriter write(String string) {
        out.print(string);

        return this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.rcfaces.core.internal.renderkit.html.IJavaScriptWriter#writeln(java.lang.String)
     */
    public IJavaScriptWriter writeln(String string) {
        out.println(string);

        return this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.rcfaces.core.internal.renderkit.html.IJavaScriptWriter#writeln()
     */
    public IJavaScriptWriter writeln() {
        out.println();

        return this;
    }

    public IJavaScriptWriter ensureInitialization() {
        return this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.rcfaces.core.internal.renderkit.html.IJavaScriptWriter#write(char)
     */
    public IJavaScriptWriter write(char c) {
        out.write(c);

        return this;
    }

    public IJavaScriptWriter writeInt(long value) {
        write(String.valueOf(value));

        return this;
    }

    public IJavaScriptWriter writeDouble(double value) {
        write(String.valueOf(value));

        return this;
    }

    public IJavaScriptWriter writeNumber(Number value) {
        write(value.toString());

        return this;
    }

    public void end() {
    }

    public boolean isOpened() {
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.rcfaces.core.internal.IReleasable#release()
     */
    public void release() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.rcfaces.core.internal.renderkit.html.IJavaScriptWriter#writeRaw(char[],
     *      int, int)
     */
    public IJavaScriptWriter writeRaw(char[] dst, int pos, int length) {
        out.write(dst, pos, length);

        return this;
    }

    public String allocateString(String string) throws WriterException {
        if (strings == null) {
            strings = new HashMap<String, String>(8);

        } else {
            String varId = strings.get(string);
            if (varId != null) {
                return varId;
            }
        }

        String varId = getJavaScriptRenderContext().allocateVarName();
        strings.put(string, varId);

        write("var ").write(varId).write('=').writeString(string).writeln(";");

        return varId;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.rcfaces.core.internal.renderkit.html.IJavaScriptWriter#getComponentRenderContext()
     */
    public final IHtmlComponentRenderContext getHtmlComponentRenderContext() {
        if (componentRenderContext == null) {
            componentRenderContext = this;
        }
        return componentRenderContext;
    }

    public IComponentRenderContext getComponentRenderContext() {
        return getHtmlComponentRenderContext();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.rcfaces.core.internal.renderkit.IComponentRenderContext#getRenderContext()
     */
    public IRenderContext getRenderContext() {
        // Y en a pas !
        // Par contre, il ne faut pas envoyer d'exception,
        // car le RewritingURL l'appelle !

        if (renderContext == null) {
            renderContext = getRenderContext(getFacesContext());
        }
        return renderContext;
        // throw new FacesException("Not supported !");
    }

    public IHtmlWriter getWriter() {
        throw new FacesException("Not supported !");
    }

    public IJavaScriptWriter writeSymbol(String symbol) {
        int idx = symbol.indexOf('.');
        String className = null;
        if (idx >= 0) {
            className = symbol.substring(0, idx);

            className = convertSymbol(null, className);
            write(className);
            write('.');

            symbol = symbol.substring(idx + 1);
        }

        write(convertSymbol(className, symbol));

        return this;
    }

    public IJavaScriptWriter writeCall(String object, String symbol) {
        if (object != null) {
            object = convertSymbol(null, object);
            write(object);
            write('.');
        }
        write(convertSymbol(object, symbol));
        write('(');

        return this;
    }

    public IJavaScriptWriter writeMethodCall(String symbol) {
        write(convertSymbol(null, getComponentVarName()));
        write('.');
        write(convertSymbol(null, symbol));
        write('(');

        return this;
    }

    public IJavaScriptWriter writeConstructor(String symbol) {
        write("new ");
        write(convertSymbol(null, symbol));
        write('(');

        return this;
    }

    public IJavaScriptWriter writeBoolean(boolean value) {
        if (value) {
            return write("true");
        }

        return write("false");
    }

    public IJavaScriptWriter writeNull() {
        return write("null");
    }

    private String convertSymbol(String className, String memberName) {
        if (symbolsInitialized == false) {
            symbolsInitialized = true;
            symbols = getSymbolsMap();
        }

        if (symbols == null) {
            return memberName;
        }

        if (className != null && className.startsWith("f")) {
            String s = (String) symbols.get(className + "." + memberName);
            if (s != null) {
                return s;
            }
        }

        String s = (String) symbols.get(memberName);
        if (s != null) {
            return s;
        }

        return memberName;
    }

    protected Map getSymbolsMap() {
        if (servletContext != null) {
            return JavaScriptRepositoryServlet.getSymbols(servletContext);
        }
        return JavaScriptRepositoryServlet.getSymbols(getFacesContext());
    }

    public IJavaScriptWriter writeString(String s) throws WriterException {
        if (s == null) {
            writeNull();
            return this;
        }

        int l = s.length();
        if (l < 1) {
            write("\"\"");
            return this;
        }

        char sep = '\"';
        if (s.indexOf(sep) >= 0 && s.indexOf('\'') < 0) {
            sep = '\'';
        }

        write(sep);
        return JavascriptCodec.writeJavaScript(this, s, sep).write(sep);
    }

    public IFile[] popRequiredFiles() {
        return null;
    }

    public void setComponentVarName(String varName) {
        this.componentVarName = varName;
    }

    public IObjectLiteralWriter writeObjectLiteral(boolean writeNullIfEmpty) {
        return new JavaScriptObjectLiteralWriter(this, writeNullIfEmpty);
    }

    public final void setRenderContext(IRenderContext renderContext) {
        this.renderContext = renderContext;
    }

    public final void setComponentRenderContext(
            IHtmlComponentRenderContext componentRenderContext) {
        this.componentRenderContext = componentRenderContext;
    }

    public boolean isIgnoreComponentInitialization() {
        return ignoreComponentInitialization;
    }

    public void setIgnoreComponentInitialization() {
        ignoreComponentInitialization = true;
    }
}
