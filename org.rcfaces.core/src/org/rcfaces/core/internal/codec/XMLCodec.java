/*
 * $Id: XMLCodec.java,v 1.2 2013/07/03 12:25:07 jbmeslin Exp $
 *
 */

package org.rcfaces.core.internal.codec;

import java.io.IOException;
import java.io.Writer;

import javax.faces.FacesException;

import org.rcfaces.core.internal.lang.StringAppender;

/**
 * Conversion UTF8-UNICODE / HTML
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:07 $
 */
public class XMLCodec {
    

    protected static String convertUTF8ToXML(String text, ACharset charset,
            String br) {
        if (text == null) {
            throw new FacesException("Try to convert to HTML a NULL text !",
                    null);
        }

        int len = text.length();

        if (len < 1) {
            return text;
        }

        char sb[] = new char[len];

        text.getChars(0, len, sb, 0);

        StringAppender ret = new StringAppender(len);

        char c;
        String html;

        for (int i = 0; i < len; i++) {
            c = sb[i];

            if (c == '\n') {
                if (br != null) {
                    ret.append(br);
                } else {
                    ret.append(c);
                }
                continue;
            }

            html = charset.convertFromUTF8(c);

            if (html == null) {
                ret.append(c);

            } else {
                ret.append(html);
            }
        }

        return ret.toString();
    }

    protected static void convertUTF8ToXML(Writer writer, String text,
            ACharset charset, String br) throws IOException {
        if (text == null) {
            return;
        }

        int len = text.length();

        if (len < 1) {
            return;
        }

        char sb[] = new char[len];

        text.getChars(0, len, sb, 0);

        char c;
        String html;

        for (int i = 0; i < len; i++) {
            c = sb[i];

            if (c == '\n') {
                if (br != null) {
                    writer.write(br);
                } else {
                    writer.write(c);
                }
                continue;
            }

            html = charset.convertFromUTF8(c);

            if (html == null) {
                writer.write(c);

            } else {
                writer.write(html);
            }
        }
    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:07 $
     */
    protected static interface ACharset {
        String convertFromUTF8(char c);
    }
}
