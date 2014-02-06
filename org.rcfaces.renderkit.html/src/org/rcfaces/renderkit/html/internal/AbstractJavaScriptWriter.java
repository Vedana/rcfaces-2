/*
 * $Id: AbstractJavaScriptWriter.java,v 1.2 2013/07/03 12:25:09 jbmeslin Exp $
 */
package org.rcfaces.renderkit.html.internal;

import javax.faces.FacesException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.internal.renderkit.WriterException;
import org.rcfaces.renderkit.html.internal.codec.JavascriptCodec;
import org.rcfaces.renderkit.html.internal.util.JavaScriptObjectLiteralWriter;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:09 $
 */
public abstract class AbstractJavaScriptWriter implements IJavaScriptWriter {
    

    private static final Log LOG = LogFactory
            .getLog(AbstractJavaScriptWriter.class);

    private static final String LF = "\n";

    private boolean ignoreComponentInitialization;

    public IJavaScriptWriter writeSymbol(String symbol) throws WriterException {
        int idx = symbol.indexOf('.');

        String className = null;

        if (idx >= 0) {
            className = symbol.substring(0, idx);

            write(convertSymbol(null, className)).write('.');

            symbol = symbol.substring(idx + 1);
        }

        write(convertSymbol(className, symbol));

        return this;
    }

    public IJavaScriptWriter writeCall(String object, String symbol)
            throws WriterException {
        if (object == null) {
            throw new FacesException("Can not call a method without object !");
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("Write call object='" + object + "' symbol='" + symbol
                    + "'.");
        }

        String converted = convertSymbol(null, object);
        write(converted).write('.');

        write(convertSymbol(converted, symbol)).write('(');

        return this;
    }

    public IJavaScriptWriter writeMethodCall(String symbol)
            throws WriterException {

        if (LOG.isDebugEnabled()) {
            LOG.debug("Write method call symbol='" + symbol + "'.");
        }

        isInitialized();

        if (LOG.isDebugEnabled()) {
            LOG.debug("  STEP2: Write method call symbol='" + symbol
                    + "' (componentVarName='" + getComponentVarName() + "')");
        }

        String componentVarName = getComponentVarName();
        if (componentVarName == null) {
            throw new FacesException("Component var name is not defined !");
        }
        write(componentVarName); // Pas de convertion !
        write('.').write(convertSymbol(null, symbol)).write('(');

        return this;
    }

    protected abstract void isInitialized() throws WriterException;

    public IJavaScriptWriter writeConstructor(String symbol)
            throws WriterException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Write constructor symbol='" + symbol + "'.");
        }

        write("new ").write(convertSymbol(null, symbol)).write('(');

        return this;
    }

    public IJavaScriptWriter writeBoolean(boolean value) throws WriterException {
        if (value) {
            return write("true");
        }

        return write("false");
    }

    public IJavaScriptWriter writeNull() throws WriterException {
        return write("null");
    }

    public IJavaScriptWriter writeln(String string) throws WriterException {
        return write(string).writeln();
    }

    public IJavaScriptWriter writeln() throws WriterException {
        return write(LF);
    }

    public IJavaScriptWriter writeInt(long value) throws WriterException {
        return write(String.valueOf(value));
    }

    public IJavaScriptWriter writeDouble(double value) throws WriterException {
        return write(String.valueOf(value));
    }

    public IJavaScriptWriter writeNumber(Number value) throws WriterException {
        return write(String.valueOf(value));
    }

    public IJavaScriptWriter writeString(String s) throws WriterException {
        if (s == null) {
            return writeNull();
        }

        int l = s.length();
        if (l < 1) {
            return write("\"\"");
        }

        char sep = '\"';
        if (s.indexOf(sep) >= 0 && s.indexOf('\'') < 0) {
            sep = '\'';
        }

        write(sep);
        return JavascriptCodec.writeJavaScript(this, s, sep).write(sep);
    }

    public IHtmlRenderContext getHtmlRenderContext() {
        return getHtmlComponentRenderContext().getHtmlRenderContext();
    }

    public IObjectLiteralWriter writeObjectLiteral(boolean writeNullIfEmpty) {
        return new JavaScriptObjectLiteralWriter(this, writeNullIfEmpty);
    }

    protected abstract String convertSymbol(String className, String memberName);

    public void setIgnoreComponentInitialization() {
        ignoreComponentInitialization = true;
    }

    public boolean isIgnoreComponentInitialization() {
        return ignoreComponentInitialization;
    }

}
