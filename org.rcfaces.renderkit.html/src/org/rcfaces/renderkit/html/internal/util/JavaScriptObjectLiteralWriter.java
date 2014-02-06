/*
 * $Id: JavaScriptObjectLiteralWriter.java,v 1.3 2013/07/03 12:25:09 jbmeslin Exp $
 */
package org.rcfaces.renderkit.html.internal.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.internal.renderkit.WriterException;
import org.rcfaces.renderkit.html.internal.IJavaScriptWriter;
import org.rcfaces.renderkit.html.internal.IObjectLiteralWriter;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/07/03 12:25:09 $
 */
public class JavaScriptObjectLiteralWriter implements IObjectLiteralWriter {
    

    private static final Log LOG = LogFactory
            .getLog(JavaScriptObjectLiteralWriter.class);

    private final IJavaScriptWriter parent;

    private final boolean writeNullIfEmpty;

    private boolean firstProperty = true;

    public JavaScriptObjectLiteralWriter(IJavaScriptWriter parent,
            boolean writeNullIfEmpty) {
        this.parent = parent;
        this.writeNullIfEmpty = writeNullIfEmpty;
    }

    public IJavaScriptWriter getParent() {
        return parent;
    }

    public IJavaScriptWriter writeProperty(String propertyName)
            throws WriterException {

        if (firstProperty) {
            firstProperty = false;
            parent.write('{');

        } else {
            parent.write(',');
        }

        boolean directWriter = true;

        if (propertyName.length() > 0) {
            char chs[] = propertyName.toCharArray();
            if (Character.isJavaIdentifierStart(chs[0]) == false) {
                directWriter = false;

            } else {
                for (int i = 1; i < chs.length; i++) {
                    if (Character.isJavaIdentifierPart(chs[i]) == false && Character.isDigit(chs[0]) == false) {
                        directWriter = false;

                        break;
                    }
                }
            }
        }

        if (directWriter == false) {
            parent.writeString(propertyName);

        } else {
            parent.write(propertyName);
        }

        parent.write(':');

        return parent;
    }

    public IJavaScriptWriter writeSymbol(String symbol) throws WriterException {

        String convertedSymbol = parent.getJavaScriptRenderContext()
                .convertSymbol(null, symbol);

        return writeProperty(convertedSymbol);
    }

    public IJavaScriptWriter end() throws WriterException {
        if (firstProperty) {
            if (writeNullIfEmpty == false) {
                return parent.write("{}");
            }

            return parent.writeNull();
        }

        return parent.write('}');
    }
}
