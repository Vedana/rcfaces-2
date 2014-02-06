/*
 * $Id: HTMLCodec.java,v 1.3 2013/07/03 12:25:10 jbmeslin Exp $
 *
 */

package org.rcfaces.renderkit.html.internal.codec;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import javax.faces.FacesException;

import org.rcfaces.core.internal.codec.XMLCodec;
import org.rcfaces.core.internal.lang.StringAppender;

/**
 * Conversion UTF8-UNICODE / HTML
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/07/03 12:25:10 $
 */
public final class HTMLCodec extends XMLCodec {
    

    private static final String[] htmlArray = new String[256];

    private static final Map<String, String> codes = new HashMap<String, String>(
            htmlArray.length);

    static {
        htmlArray[161] = "&iexcl;"; // inverted exclamation ¡ &#161; --> ¡
        // &iexcl; --> ¡
        htmlArray[162] = "&cent;"; // cent sign ¢ &#162; --> ¢ &cent; --> ¢
        htmlArray[163] = "&pound;"; // pound sterling £ &#163; --> £ &pound; -->
        // £
        htmlArray[164] = "&curren;"; // general currency sign ¤ &#164; --> ¤
        // &curren; --> ¤
        htmlArray[165] = "&yen;"; // yen sign ¥ &#165; --> ¥ &yen; --> ¥
        htmlArray[166] = "&brvbar;"; // broken vertical bar ¦ &#166; --> ¦
        // &brvbar; --> ¦
        htmlArray[167] = "&sect;"; // section sign § &#167; --> § &sect; --> §
        htmlArray[168] = "&uml;"; // umlaut (dieresis) ¨ &#168; --> ¨ &uml;
        // --> ¨
        htmlArray[169] = "&copy;"; // copyright © &#169; --> © &copy; --> ©
        htmlArray[170] = "&ordf;"; // feminine ordinal ª &#170; --> ª &ordf;
        // --> ª
        htmlArray[171] = "&laquo;"; // left angle quote, guillemotleft « &#171;
        // --> « &laquo; --> «
        htmlArray[172] = "&not;"; // not sign ¬ &#172; --> ¬ &not; --> ¬
        htmlArray[173] = "&shy;"; // soft hyphen ­ &#173; --> ­ &shy; --> ­
        htmlArray[174] = "&reg;"; // registered trademark ® &#174; --> ® &reg;
        // --> ®
        htmlArray[175] = "&macr;"; // macron accent ¯ &#175; --> ¯ &macr; --> ¯
        htmlArray[176] = "&deg;"; // degree sign ° &#176; --> ° &deg; --> °
        htmlArray[177] = "&plusmn;"; // plus or minus ± &#177; --> ± &plusmn;
        // --> ±
        htmlArray[178] = "&sup2;"; // superscript two ² &#178; --> ² &sup2; -->
        // ²
        htmlArray[179] = "&sup3;"; // superscript three ³ &#179; --> ³ &sup3;
        // --> ³
        htmlArray[180] = "&acute;"; // acute accent ´ &#180; --> ´ &acute; --> ´
        htmlArray[181] = "&micro;"; // micro sign µ &#181; --> µ &micro; --> µ
        htmlArray[182] = "&para;"; // paragraph sign ¶ &#182; --> ¶ &para; -->
        // ¶
        htmlArray[183] = "&middot;"; // middle dot · &#183; --> · &middot;
        // --> ·
        htmlArray[184] = "&cedil;"; // cedilla ¸ &#184; --> ¸ &cedil; --> ¸
        htmlArray[185] = "&sup1;"; // superscript one ¹ &#185; --> ¹ &sup1; -->
        // ¹
        htmlArray[186] = "&ordm;"; // masculine ordinal º &#186; --> º &ordm;
        // --> º
        htmlArray[187] = "&raquo;"; // right angle quote, guillemotright »
        // &#187; --> » &raquo; --> »
        htmlArray[188] = "&frac14;"; // fraction one-fourth ¼ &#188; --> ¼
        // &frac14; --> ¼
        htmlArray[189] = "&frac12;"; // fraction one-half ½ &#189; --> ½
        // &frac12; --> ½
        htmlArray[190] = "&frac34;"; // fraction three-fourths ¾ &#190; --> ¾
        // &frac34; --> ¾
        htmlArray[191] = "&iquest;"; // inverted question mark ¿ &#191; --> ¿
        // &iquest; --> ¿
        htmlArray[192] = "&Agrave;"; // capital A, grave accent À &#192; -->
        // À &Agrave; --> À
        htmlArray[193] = "&Aacute;"; // capital A, acute accent Á &#193; -->
        // Á &Aacute; --> Á
        htmlArray[194] = "&Acirc;"; // capital A, circumflex accent Â &#194; -->
        // Â &Acirc; --> Â
        htmlArray[195] = "&Atilde;"; // capital A, tilde Ã &#195; --> Ã
        // &Atilde; --> Ã
        htmlArray[196] = "&Auml;"; // capital A, dieresis or umlaut mark Ä
        // &#196; --> Ä &Auml; --> Ä
        htmlArray[197] = "&Aring;"; // capital A, ring Å &#197; --> Å &Aring;
        // --> Å
        htmlArray[198] = "&AElig;"; // capital AE diphthong (ligature) Æ &#198;
        // --> Æ &AElig; --> Æ
        htmlArray[199] = "&Ccedil;"; // capital C, cedilla Ç &#199; --> Ç
        // &Ccedil; --> Ç
        htmlArray[200] = "&Egrave;"; // capital E, grave accent È &#200; -->
        // È &Egrave; --> È
        htmlArray[201] = "&Eacute;"; // capital E, acute accent É &#201; -->
        // É &Eacute; --> É
        htmlArray[202] = "&Ecirc;"; // capital E, circumflex accent Ê &#202; -->
        // Ê &Ecirc; --> Ê
        htmlArray[203] = "&Euml;"; // capital E, dieresis or umlaut mark Ë
        // &#203; --> Ë &Euml; --> Ë
        htmlArray[204] = "&Igrave;"; // capital I, grave accent Ì &#204; -->
        // Ì &Igrave; --> Ì
        htmlArray[205] = "&Iacute;"; // capital I, acute accent Í &#205; -->
        // Í &Iacute; --> Í
        htmlArray[206] = "&Icirc;"; // capital I, circumflex accent Î &#206; -->
        // Î &Icirc; --> Î
        htmlArray[207] = "&Iuml;"; // capital I, dieresis or umlaut mark Ï
        // &#207; --> Ï &Iuml; --> Ï
        htmlArray[208] = "&ETH;"; // capital Eth, Icelandic Ð &#208; --> Ð
        // &ETH; --> Ð
        htmlArray[209] = "&Ntilde;"; // capital N, tilde Ñ &#209; --> Ñ
        // &Ntilde; --> Ñ
        htmlArray[210] = "&Ograve;"; // capital O, grave accent Ò &#210; -->
        // Ò &Ograve; --> Ò
        htmlArray[211] = "&Oacute;"; // capital O, acute accent Ó &#211; -->
        // Ó &Oacute; --> Ó
        htmlArray[212] = "&Ocirc;"; // capital O, circumflex accent Ô &#212; -->
        // Ô &Ocirc; --> Ô
        htmlArray[213] = "&Otilde;"; // capital O, tilde Õ &#213; --> Õ
        // &Otilde; --> Õ
        htmlArray[214] = "&Ouml;"; // capital O, dieresis or umlaut mark Ö
        // &#214; --> Ö &Ouml; --> Ö
        htmlArray[215] = "&times;"; // multiply sign × &#215; --> × &times; -->
        // ×
        htmlArray[216] = "&Oslash;"; // capital O, slash Ø &#216; --> Ø
        // &Oslash; --> Ø
        htmlArray[217] = "&Ugrave;"; // capital U, grave accent Ù &#217; -->
        // Ù &Ugrave; --> Ù
        htmlArray[218] = "&Uacute;"; // capital U, acute accent Ú &#218; -->
        // Ú &Uacute; --> Ú
        htmlArray[219] = "&Ucirc;"; // capital U, circumflex accent Û &#219; -->
        // Û &Ucirc; --> Û
        htmlArray[220] = "&Uuml;"; // capital U, dieresis or umlaut mark Ü
        // &#220; --> Ü &Uuml; --> Ü
        htmlArray[221] = "&Yacute;"; // capital Y, acute accent Ý &#221; -->
        // Ý &Yacute; --> Ý
        htmlArray[222] = "&THORN;"; // capital THORN, Icelandic Þ &#222; --> Þ
        // &THORN; --> Þ
        htmlArray[223] = "&szlig;"; // small sharp s, German (sz ligature) ß
        // &#223; --> ß &szlig; --> ß
        htmlArray[224] = "&agrave;"; // small a, grave accent à &#224; --> à
        // &agrave; --> à
        htmlArray[225] = "&aacute;"; // small a, acute accent á &#225; --> á
        // &aacute; --> á
        htmlArray[226] = "&acirc;"; // small a, circumflex accent â &#226; --> â
        // &acirc; --> â
        htmlArray[227] = "&atilde;"; // small a, tilde ã &#227; --> ã
        // &atilde; --> ã
        htmlArray[228] = "&auml;"; // small a, dieresis or umlaut mark ä &#228;
        // --> ä &auml; --> ä
        htmlArray[229] = "&aring;"; // small a, ring å &#229; --> å &aring; -->
        // å
        htmlArray[230] = "&aelig;"; // small ae diphthong (ligature) æ &#230;
        // --> æ &aelig; --> æ
        htmlArray[231] = "&ccedil;"; // small c, cedilla ç &#231; --> ç
        // &ccedil; --> ç
        htmlArray[232] = "&egrave;"; // small e, grave accent è &#232; --> è
        // &egrave; --> è
        htmlArray[233] = "&eacute;"; // small e, acute accent é &#233; --> é
        // &eacute; --> é
        htmlArray[234] = "&ecirc;"; // small e, circumflex accent ê &#234; --> ê
        // &ecirc; --> ê
        htmlArray[235] = "&euml;"; // small e, dieresis or umlaut mark ë &#235;
        // --> ë &euml; --> ë
        htmlArray[236] = "&igrave;"; // small i, grave accent ì &#236; --> ì
        // &igrave; --> ì
        htmlArray[237] = "&iacute;"; // small i, acute accent í &#237; --> í
        // &iacute; --> í
        htmlArray[238] = "&icirc;"; // small i, circumflex accent î &#238; --> î
        // &icirc; --> î
        htmlArray[239] = "&iuml;"; // small i, dieresis or umlaut mark ï &#239;
        // --> ï &iuml; --> ï
        htmlArray[240] = "&eth;"; // small eth, Icelandic ð &#240; --> ð &eth;
        // --> ð
        htmlArray[241] = "&ntilde;"; // small n, tilde ñ &#241; --> ñ
        // &ntilde; --> ñ
        htmlArray[242] = "&ograve;"; // small o, grave accent ò &#242; --> ò
        // &ograve; --> ò
        htmlArray[243] = "&oacute;"; // small o, acute accent ó &#243; --> ó
        // &oacute; --> ó
        htmlArray[244] = "&ocirc;"; // small o, circumflex accent ô &#244; --> ô
        // &ocirc; --> ô
        htmlArray[245] = "&otilde;"; // small o, tilde õ &#245; --> õ
        // &otilde; --> õ
        htmlArray[246] = "&ouml;"; // small o, dieresis or umlaut mark ö &#246;
        // --> ö &ouml; --> ö
        htmlArray[247] = "&divide;"; // division sign ÷ &#247; --> ÷ &divide;
        // --> ÷
        htmlArray[248] = "&oslash;"; // small o, slash ø &#248; --> ø
        // &oslash; --> ø
        htmlArray[249] = "&ugrave;"; // small u, grave accent ù &#249; --> ù
        // &ugrave; --> ù
        htmlArray[250] = "&uacute;"; // small u, acute accent ú &#250; --> ú
        // &uacute; --> ú
        htmlArray[251] = "&ucirc;"; // small u, circumflex accent û &#251; --> û
        // &ucirc; --> û
        htmlArray[252] = "&uuml;"; // small u, dieresis or umlaut mark ü &#252;
        // --> ü &uuml; --> ü
        htmlArray[253] = "&yacute;"; // small y, acute accent ý &#253; --> ý
        // &yacute; --> ý
        htmlArray[254] = "&thorn;"; // small thorn, Icelandic þ &#254; --> þ
        // &thorn; --> þ
        htmlArray[255] = "&yuml;"; // small y, dieresis or umlaut mark ÿ &#255;
        // --> ÿ &yuml; --> ÿ

        htmlArray['\"'] = "&quot;";
        htmlArray['&'] = "&amp;";
        htmlArray['<'] = "&lt;";
        htmlArray['>'] = "&gt;";

        /*
         * wmlArray[(int) '\"'] = "&quot;"; wmlArray[(int) '&'] = "&amp;";
         * wmlArray[(int) '<'] = "&lt;"; wmlArray[(int) '>'] = "&gt;";
         * wmlArray[(int) '$'] = "$$";
         * 
         * xmlArray[(int) '\"'] = "&quot;"; xmlArray[(int) '&'] = "&amp;";
         * xmlArray[(int) '<'] = "&lt;"; xmlArray[(int) '>'] = "&gt;";
         */
        for (int i = 0; i < htmlArray.length; i++) {
            if (htmlArray[i] != null) {
                String ch = new Character((char) i).toString();
                codes.put(htmlArray[i], ch);
                codes.put("&#" + i, ch);
            }
        }

        // codes.put("&#8211;", new Character((char)8211)); // "'"
        // codes.put("&#339;", new Character((char)339)); //oe minuscule
        // codes.put("&#8364;", new Character((char)8364));
        codes.put("&euro;", new Character((char) 8364).toString());

        codes.put("&nbsp;", " ");
    }

    /**
     * Converti une chaine de caracteres contenant des codes HTML en chaine de
     * caracteres JAVA. (format UNICODE-16 ou UTF8)
     * 
     * @param html
     *            La chaine en HTML.
     */
    public static final String convertHTMLtoUTF8(String html) {
        if (html == null) {
            return null;
        }

        StringAppender sb = new StringAppender(html.length());

        for (int pos = 0;;) {
            int p = html.indexOf('&', pos);

            if (p < 0) {
                sb.append(html.substring(pos));

                break;
            }

            int p1 = html.indexOf(';', p);
            int p2 = html.indexOf('&', p + 1);

            if (p1 < 0) {
                sb.append(html.substring(pos));

                break;
            }

            if ((p2 > 0) && (p2 < p1)) {
                sb.append(html.substring(pos, p2));
                pos = p2;

                continue;
            }

            sb.append(html.substring(pos, p));

            String key = html.substring(p, p1 + 1);
            String v = codes.get(key.toLowerCase());

            if (v == null) {
                try {
                    int cv = Integer.parseInt(key);
                    sb.append((char) cv);
                } catch (NumberFormatException ex) {

                    throw new FacesException(
                            "ConvertHTMLtoUTF8 can not find html code '" + key
                                    + "'", ex);
                }
            } else {
                sb.append(v);
            }

            pos = p1 + 1;
        }

        return sb.toString();
    }

    public static String convertUTF8ToHTML(String text) {
        return convertUTF8ToXML(text, HTML_CHARSET, null);
    }

    private static final ACharset HTML_CHARSET = new ACharset() {
        

        public String convertFromUTF8(char c) {

            if (c < htmlArray.length) {
                return htmlArray[c];
            }

            if (c == 8364) {
                return "&euro;";
            }

            return null;
        }

    };

    public static void encodeAttribute(Writer writer, String attributeValue)
            throws IOException {

        int len = attributeValue.length();

        int i = 0;
        for (; i < len; i++) {
            char c = attributeValue.charAt(i);
            if (c != '"' && c != '&' && c != '>') {
                continue;
            }

            break;
        }

        if (i == len) {
            writer.write(attributeValue);
            return;
        }

        char cs[] = attributeValue.toCharArray();

        int last = 0;
        for (; i < cs.length; i++) {
            char c = cs[i];

            if (c != '"' && c != '&' && c != '>') {
                continue;
            }

            if (last < i) {
                writer.write(cs, last, i - last);
            }
            last = i + 1;

            if (c == '"') {
                writer.write("&quot;");
                continue;
            }

            if (c == '>') {
                writer.write("&gt;");
                continue;
            }

            // HTML 4.0, section B.7.1: ampersands followed by an open brace
            // don't get escaped
            if ((i + 1 < cs.length) && (cs[i + 1] == '{')) {
                last = i; // On garde !
                continue;
            }

            writer.write("&amp;");
        }

        if (last < cs.length) {
            writer.write(cs, last, cs.length - last);
        }
    }
}
