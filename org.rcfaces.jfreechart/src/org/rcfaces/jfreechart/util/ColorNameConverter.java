/*
 * $Id: ColorNameConverter.java,v 1.1 2011/04/12 09:26:25 oeuillot Exp $
 */
package org.rcfaces.jfreechart.util;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:26:25 $
 */
public class ColorNameConverter implements Converter {

    public static final Converter SINGLETON = new ColorNameConverter();

    public Object getAsObject(FacesContext facesContext, UIComponent component,
            String colorName) {

        if (colorName == null || colorName.length() < 1) {
            return null;
        }

        colorName = colorName.trim().toLowerCase();

        char c = colorName.charAt(0);
        if (Character.isLetter(c)) {
            return namedColors.get(colorName);
        }

        if (colorName.indexOf(',') > 0) {
            StringTokenizer st = new StringTokenizer(colorName, ",");
            if (st.countTokens() > 2) {
                int r = Integer.parseInt(st.nextToken().trim());
                int g = Integer.parseInt(st.nextToken().trim());
                int b = Integer.parseInt(st.nextToken().trim());

                if (st.hasMoreTokens() == false) {
                    return new Color(r, g, b);
                }

                int a = Integer.parseInt(st.nextToken().trim());

                return new Color(r, g, b, a);
            }
        }

        try {
            return Color.decode(colorName);

        } catch (NumberFormatException ex) {
            throw new FacesException("Can not decode color '" + colorName
                    + "'.", ex);
        }
    }

    public String getAsString(FacesContext arg0, UIComponent arg1, Object _color) {
        Color color = (Color) _color;

        return "#" + color.getRed() + "," + color.getGreen() + ","
                + color.getBlue();
    }

    private static final Map namedColors = new HashMap(64);
    static {
        namedColors.put("aliceblue", Color.decode("#f0f8ff"));
        namedColors.put("antiquewhite", Color.decode("#faebd7"));
        namedColors.put("aqua", Color.decode("#00ffff"));
        namedColors.put("aquamarine", Color.decode("#7fffd4"));
        namedColors.put("azure", Color.decode("#f0ffff"));
        namedColors.put("beige", Color.decode("#f5f5dc"));
        namedColors.put("bisque", Color.decode("#ffe4c4"));
        namedColors.put("black", Color.decode("#000000"));
        namedColors.put("blanchedalmond", Color.decode("#ffebcd"));
        namedColors.put("blue", Color.decode("#0000ff"));
        namedColors.put("blueviolet", Color.decode("#8a2be2"));
        namedColors.put("brown", Color.decode("#a52a2a"));
        namedColors.put("burlywood", Color.decode("#deb887"));
        namedColors.put("cadetblue", Color.decode("#5f9ea0"));
        namedColors.put("chartreuse", Color.decode("#7fff00"));
        namedColors.put("chocolate", Color.decode("#d2691e"));
        namedColors.put("coral", Color.decode("#ff7f50"));
        namedColors.put("cornflowerblue", Color.decode("#6495ed"));
        namedColors.put("cornsilk", Color.decode("#fff8dc"));
        namedColors.put("crimson", Color.decode("#dc143c"));
        namedColors.put("cyan", Color.decode("#00ffff"));
        namedColors.put("darkblue", Color.decode("#00008b"));
        namedColors.put("darkcyan", Color.decode("#008b8b"));
        namedColors.put("darkgoldenrod", Color.decode("#b8860b"));
        namedColors.put("darkgray", Color.decode("#a9a9a9"));
        namedColors.put("darkgreen", Color.decode("#006400"));
        namedColors.put("darkkhaki", Color.decode("#bdb76b"));
        namedColors.put("darkmagenta", Color.decode("#8b008b"));
        namedColors.put("darkolivegreen", Color.decode("#556b2f"));
        namedColors.put("darkorange", Color.decode("#ff8c00"));
        namedColors.put("darkorchid", Color.decode("#9932cc"));
        namedColors.put("darkred", Color.decode("#8b0000"));
        namedColors.put("darksalmon", Color.decode("#e9967a"));
        namedColors.put("darkseagreen", Color.decode("#8fbc8f"));
        namedColors.put("darkslateblue", Color.decode("#483d8b"));
        namedColors.put("darkslategray", Color.decode("#2f4f4f"));
        namedColors.put("darkturquoise", Color.decode("#00ced1"));
        namedColors.put("darkviolet", Color.decode("#9400d3"));
        namedColors.put("deeppink", Color.decode("#ff1493"));
        namedColors.put("deepskyblue", Color.decode("#00bfff"));
        namedColors.put("dimgray", Color.decode("#696969"));
        namedColors.put("dodgerblue", Color.decode("#1e90ff"));
        namedColors.put("firebrick", Color.decode("#b22222"));
        namedColors.put("floralwhite", Color.decode("#fffaf0"));
        namedColors.put("forestgreen", Color.decode("#228b22"));
        namedColors.put("fuchsia", Color.decode("#ff00ff"));
        namedColors.put("gainsboro", Color.decode("#dcdcdc"));
        namedColors.put("ghostwhite", Color.decode("#f8f8ff"));
        namedColors.put("gold", Color.decode("#ffd700"));
        namedColors.put("goldenrod", Color.decode("#daa520"));
        namedColors.put("gray", Color.decode("#808080"));
        namedColors.put("green", Color.decode("#008000"));
        namedColors.put("greenyellow", Color.decode("#adff2f"));
        namedColors.put("honeydew", Color.decode("#f0fff0"));
        namedColors.put("hotpink", Color.decode("#ff69b4"));
        namedColors.put("indianred", Color.decode("#cd5c5c"));
        namedColors.put("indigo", Color.decode("#4b0082"));
        namedColors.put("ivory", Color.decode("#fffff0"));
        namedColors.put("khaki", Color.decode("#f0e68c"));
        namedColors.put("lavender", Color.decode("#e6e6fa"));
        namedColors.put("lavenderblush", Color.decode("#fff0f5"));
        namedColors.put("lawngreen", Color.decode("#7cfc00"));
        namedColors.put("lemonchiffon", Color.decode("#fffacd"));
        namedColors.put("lightblue", Color.decode("#add8e6"));
        namedColors.put("lightcoral", Color.decode("#f08080"));
        namedColors.put("lightcyan", Color.decode("#e0ffff"));
        namedColors.put("lightgoldenrodyellow", Color.decode("#fafad2"));
        namedColors.put("lightgreen", Color.decode("#90ee90"));
        namedColors.put("lightgrey", Color.decode("#d3d3d3"));
        namedColors.put("lightpink", Color.decode("#ffb6c1"));
        namedColors.put("lightsalmon", Color.decode("#ffa07a"));
        namedColors.put("lightseagreen", Color.decode("#20b2aa"));
        namedColors.put("lightskyblue", Color.decode("#87cefa"));
        namedColors.put("lightslategray", Color.decode("#778899"));
        namedColors.put("lightsteelblue", Color.decode("#b0c4de"));
        namedColors.put("lightyellow", Color.decode("#ffffe0"));
        namedColors.put("lime", Color.decode("#00ff00"));
        namedColors.put("limegreen", Color.decode("#32cd32"));
        namedColors.put("linen", Color.decode("#faf0e6"));
        namedColors.put("magenta", Color.decode("#ff00ff"));
        namedColors.put("maroon", Color.decode("#800000"));
        namedColors.put("mediumaquamarine", Color.decode("#66cdaa"));
        namedColors.put("mediumblue", Color.decode("#0000cd"));
        namedColors.put("mediumorchid", Color.decode("#ba55d3"));
        namedColors.put("mediumpurple", Color.decode("#9370db"));
        namedColors.put("mediumseagreen", Color.decode("#3cb371"));
        namedColors.put("mediumslateblue", Color.decode("#7b68ee"));
        namedColors.put("mediumspringgreen", Color.decode("#00fa9a"));
        namedColors.put("mediumturquoise", Color.decode("#48d1cc"));
        namedColors.put("mediumvioletred", Color.decode("#c71585"));
        namedColors.put("midnightblue", Color.decode("#191970"));
        namedColors.put("mintcream", Color.decode("#f5fffa"));
        namedColors.put("mistyrose", Color.decode("#ffe4e1"));
        namedColors.put("moccasin", Color.decode("#ffe4b5"));
        namedColors.put("navajowhite", Color.decode("#ffdead"));
        namedColors.put("navy", Color.decode("#000080"));
        namedColors.put("oldlace", Color.decode("#fdf5e6"));
        namedColors.put("olive", Color.decode("#808000"));
        namedColors.put("olivedrab", Color.decode("#6b8e23"));
        namedColors.put("orange", Color.decode("#ffa500"));
        namedColors.put("orangered", Color.decode("#ff4500"));
        namedColors.put("orchid", Color.decode("#da70d6"));
        namedColors.put("palegoldenrod", Color.decode("#eee8aa"));
        namedColors.put("palegreen", Color.decode("#98fb98"));
        namedColors.put("paleturquoise", Color.decode("#afeeee"));
        namedColors.put("palevioletred", Color.decode("#db7093"));
        namedColors.put("papayawhip", Color.decode("#ffefd5"));
        namedColors.put("peachpuff", Color.decode("#ffdab9"));
        namedColors.put("peru", Color.decode("#cd853f"));
        namedColors.put("pink", Color.decode("#ffc0cb"));
        namedColors.put("plum", Color.decode("#dda0dd"));
        namedColors.put("powderblue", Color.decode("#b0e0e6"));
        namedColors.put("purple", Color.decode("#800080"));
        namedColors.put("red", Color.decode("#ff0000"));
        namedColors.put("rosybrown", Color.decode("#bc8f8f"));
        namedColors.put("royalblue", Color.decode("#4169e1"));
        namedColors.put("saddlebrown", Color.decode("#8b4513"));
        namedColors.put("salmon", Color.decode("#fa8072"));
        namedColors.put("sandybrown", Color.decode("#f4a460"));
        namedColors.put("seagreen", Color.decode("#2e8b57"));
        namedColors.put("seashell", Color.decode("#fff5ee"));
        namedColors.put("sienna", Color.decode("#a0522d"));
        namedColors.put("silver", Color.decode("#c0c0c0"));
        namedColors.put("skyblue", Color.decode("#87ceeb"));
        namedColors.put("slateblue", Color.decode("#6a5acd"));
        namedColors.put("slategray", Color.decode("#708090"));
        namedColors.put("snow", Color.decode("#fffafa"));
        namedColors.put("springgreen", Color.decode("#00ff7f"));
        namedColors.put("steelblue", Color.decode("#4682b4"));
        namedColors.put("tan", Color.decode("#d2b48c"));
        namedColors.put("teal", Color.decode("#008080"));
        namedColors.put("thistle", Color.decode("#d8bfd8"));
        namedColors.put("tomato", Color.decode("#ff6347"));
        namedColors.put("turquoise", Color.decode("#40e0d0"));
        namedColors.put("violet", Color.decode("#ee82ee"));
        namedColors.put("wheat", Color.decode("#f5deb3"));
        namedColors.put("white", Color.decode("#ffffff"));
        namedColors.put("whitesmoke", Color.decode("#f5f5f5"));
        namedColors.put("yellow", Color.decode("#ffff00"));
        namedColors.put("yellowgreen", Color.decode("#9acd32"));

    }

}
