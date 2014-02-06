/*
 * $Id: URLFormCodec.java,v 1.4 2013/11/13 12:53:26 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.internal.codec;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.BitSet;

import javax.faces.FacesException;
import javax.servlet.jsp.JspWriter;

import org.rcfaces.core.internal.lang.StringAppender;

/**
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.4 $ $Date: 2013/11/13 12:53:26 $
 */
public final class URLFormCodec {

    private static final String FORM_DECODER_INPUT_CHARSET = "UTF8";

    private static final String FORM_DECODER_UTF8_CHARSET = "UTF8";

    private static final int CASE_DIFF = 'a' - 'A';

    private static final BitSet dontNeedEncoding;
    static {
        dontNeedEncoding = new BitSet(127);
        int i;
        for (i = 'a'; i <= 'z'; i++) {
            dontNeedEncoding.set(i);
        }
        for (i = 'A'; i <= 'Z'; i++) {
            dontNeedEncoding.set(i);
        }
        for (i = '0'; i <= '9'; i++) {
            dontNeedEncoding.set(i);
        }
        dontNeedEncoding.set('-');
        dontNeedEncoding.set('_');
        dontNeedEncoding.set('.');
        dontNeedEncoding.set('*');
    }

    private static final BitSet urlDontNeedEncoding;
    static {
        urlDontNeedEncoding = new BitSet(127);
        urlDontNeedEncoding.or(dontNeedEncoding);

        // Pour transformer l'URL sans transformer le sens :-)
        urlDontNeedEncoding.set('/');
        urlDontNeedEncoding.set(':');
        urlDontNeedEncoding.set('?');
        urlDontNeedEncoding.set('#');
    }

    public static String decodeUTF8(String source) {
        if (source == null) {
            return null;
        }

        int length = source.length();
        if (length < 3) {
            return source;
        }

        try {
            return URLDecoder.decode(source, FORM_DECODER_INPUT_CHARSET);

        } catch (UnsupportedEncodingException e) {
            throw new FacesException("Can not decode '" + source + "'.", e);
        }

        /*
         * byte buf[]; try { buf = source.getBytes(FORM_DECODER_INPUT_CHARSET);
         * } catch (UnsupportedEncodingException e) { throw new
         * FacesException("Unknown charset '" + FORM_DECODER_INPUT_CHARSET +
         * "'.", e); }
         * 
         * int pos = 0;
         * 
         * for (int i = 0; i < length; i++) { byte c = buf[i];
         * 
         * if (c != '%') { buf[pos++] = c; continue; }
         * 
         * if (i + 2 >= length) { // Bad format break; }
         * 
         * buf[pos++] = (byte) Integer.parseInt( source.substring(i + 1, i + 3),
         * 16); i += 2; }
         * 
         * try { return new String(buf, 0, pos, FORM_DECODER_UTF8_CHARSET); }
         * catch (UnsupportedEncodingException e) { throw new
         * FacesException("Unknown charset '" + FORM_DECODER_UTF8_CHARSET +
         * "'.", e); }
         */
    }

    private static void append(StringAppender sb, String url, BitSet bitSet,
            boolean convertSpace) {
        char chs[] = url.toCharArray();

        for (int i = 0; i < chs.length; i++) {
            int c = chs[i];

            if (c < 256 && bitSet.get(c)) {
                sb.append((char) c);
                continue;
            }
            if (c == 32 && convertSpace) {
                sb.append('+');
                continue;
            }

            // L'espace est transformé ... comme les autres !

            if (c < 128) {
                sb.append('%');
                char ch = Character.forDigit((c >> 4) & 0xF, 16);
                if (Character.isLetter(ch)) {
                    ch -= CASE_DIFF;
                }
                sb.append(ch);
                ch = Character.forDigit(c & 0xF, 16);
                if (Character.isLetter(ch)) {
                    ch -= CASE_DIFF;
                }
                sb.append(ch);

                continue;
            }

            String form;
            try {
                form = URLEncoder.encode(url.substring(i, i + 1),
                        FORM_DECODER_UTF8_CHARSET);

            } catch (UnsupportedEncodingException e) {
                throw new FacesException("Can not encode '" + url + "'.", e);
            }
            sb.append(form);

        }
    }

    public static void main(String args[]) {
        System.out.println("DecodeUTF8: '" + args[0] + "' => '"
                + decodeUTF8(args[0]) + "'");
    }

    public static void writeURL(JspWriter writer, String url)
            throws IOException {
        int length = url.length();
        StringAppender sa = new StringAppender(length * 3 / 2);

        appendURL(sa, url);

        sa.copyInto(writer);
    }

    public static String decodeURL(String url) {
        char chs[] = url.toCharArray();

        return decodeURL(chs, 0, chs.length);
    }

    @SuppressWarnings("unused")
    public static String decodeURL(char chs[], int offset, int end) {
        if (false) {
            StringAppender sa = new StringAppender(end - offset);

            next_char: for (; offset < end; offset++) {
                int c = chs[offset];
                if (c != '%') {
                    sa.append((char) c);
                    continue;
                }

                int v = 0;
                for (int j = 0; j < 2; j++) {
                    offset++;
                    if (offset >= end) {
                        break next_char;
                    }

                    v <<= 4;
                    c = chs[offset];
                    if (c >= '0' && c <= '9') {
                        v |= (c - '0');

                    } else if (c >= 'A' && c <= 'F') {
                        v |= (c - 'A' + 10);

                    } else if (c >= 'a' && c <= 'f') {
                        v |= (c - 'a' + 10);
                    } else {
                        throw new IllegalStateException(
                                "Invalid hexadecimal character " + v);
                    }
                }

                sa.append((char) v);
            }

            return sa.toString();
        }

        return decodeUTF8(new String(chs, offset, end - offset));
    }

    public static void appendURL(StringAppender sa, String url) {
        append(sa, url, urlDontNeedEncoding, false);
    }

    public static void encode(StringAppender sa, String value) {
        append(sa, value, dontNeedEncoding, true);
    }
}
