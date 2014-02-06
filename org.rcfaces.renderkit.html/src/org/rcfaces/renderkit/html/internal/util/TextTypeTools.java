/*
 * $Id: TextTypeTools.java,v 1.4 2013/11/13 12:53:32 jbmeslin Exp $
 */
package org.rcfaces.renderkit.html.internal.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.faces.component.UIComponent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.component.capability.ITypedComponentCapability;
import org.rcfaces.core.internal.lang.StringAppender;
import org.rcfaces.renderkit.html.internal.IHtmlComponentRenderContext;
import org.rcfaces.renderkit.html.internal.IHtmlWriter;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.4 $ $Date: 2013/11/13 12:53:32 $
 */
public class TextTypeTools {
    

    private static final Log LOG = LogFactory.getLog(TextTypeTools.class);

    private static final Map<String, String> HTML_TYPES = new HashMap<String, String>(
            32);
    static {
        HTML_TYPES.put("label", IHtmlWriter.LABEL);
        HTML_TYPES.put("div", IHtmlWriter.DIV);
        HTML_TYPES.put("span", IHtmlWriter.SPAN);
        HTML_TYPES.put("emphasis", "em");
        HTML_TYPES.put("cite", "cite");
        HTML_TYPES.put("definition", "dfn");
        HTML_TYPES.put("code", "code");
        HTML_TYPES.put("sample", "samp");
        HTML_TYPES.put("keyboard", "kbd");
        HTML_TYPES.put("variable", "var");
        HTML_TYPES.put("abbreviated", "abbr");
        HTML_TYPES.put("acronym", "acronym");
        HTML_TYPES.put("quote", "blockquote");
        HTML_TYPES.put("q", "q");
        HTML_TYPES.put("paragraph", "p");
        HTML_TYPES.put("preformatted", "pre");
        HTML_TYPES.put("ins", "ins");
        HTML_TYPES.put("del", "del");
        HTML_TYPES.put("strong", "strong");
        HTML_TYPES.put("header", "h1");
        HTML_TYPES.put("header1", "h1");
        HTML_TYPES.put("header2", "h2");
        HTML_TYPES.put("header3", "h3");
        HTML_TYPES.put("header4", "h4");
        HTML_TYPES.put("header5", "h5");
        HTML_TYPES.put("header6", "h6");

        HTML_TYPES.put("unorderedlist", IHtmlWriter.UL);
        HTML_TYPES.put("listitem", IHtmlWriter.LI);
        HTML_TYPES.put("orderedlist", "ol");

        HTML_TYPES.put("datalist", "dl");
        HTML_TYPES.put("datatitle", "dt");
        HTML_TYPES.put("datadata", "dd");

        Collection<String> c = HTML_TYPES.values();
        String values[] = c.toArray(new String[c.size()]);

        for (int i = 0; i < values.length; i++) {
            HTML_TYPES.put(values[i].toLowerCase(), values[i]);
        }
    }

    public static String getType(IHtmlWriter htmlWriter) {
        IHtmlComponentRenderContext renderContext = htmlWriter
                .getHtmlComponentRenderContext();

        UIComponent component = renderContext.getComponent();
        if ((component instanceof ITypedComponentCapability) == false) {
            return null;
        }

        ITypedComponentCapability textComponent = (ITypedComponentCapability) component;

        String type = textComponent.getType();

        if (type == null) {
            return null;
        }

        String element = HTML_TYPES.get(type);
        if (element != null) {
            return element;
        }

        // On retire les espaces, et on passe tout en minuscule !

        char chs[] = type.toCharArray();

        StringAppender sa = new StringAppender(chs.length);
        for (int i = 0; i < chs.length; i++) {
            char ch = chs[i];

            if (Character.isWhitespace(ch)) {
                continue;
            }

            ch = Character.toLowerCase(ch);

            sa.append(ch);
        }

        type = sa.toString();

        element = HTML_TYPES.get(type);

        return element;
    }
}
