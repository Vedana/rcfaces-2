/*
 * $Id: IFontCapability.java,v 1.1 2011/04/12 09:25:24 oeuillot Exp $
 * 
 */
package org.rcfaces.core.component.capability;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:24 $
 */
public interface IFontCapability {

    /**
     * Returns a boolean object (or null) indicating the <i>bold</i> property
     * for the specified font.
     * 
     * @return True|False|null
     */
    Boolean getFontBold();

    /**
     * Returns a boolean object (or null) indicating the <i>italic</i> property
     * for the specified font.
     * 
     * @return True|False|null
     */
    Boolean getFontItalic();

    /**
     * Returns a boolean object (or null) indicating the <i>underline</i>
     * property for the specified font.
     * 
     * @return True|False|null
     */
    Boolean getFontUnderline();

    /**
     * Returns a string value indicating the name of the font used for this
     * component.
     * 
     * @return font name
     */
    String getFontName();

    /**
     * Returns a string indicating the size to use for the selected font. (or
     * any other CSS accepted value for font size).
     * 
     * @return xx-small|x-small|small|medium|large|x-large|xx-large|smaller|larger|<i>length</i>
     *         for example 10px|<i>%</i>
     */
    String getFontSize();

    /**
     * Sets a boolean object (or null) indicating the <i>bold</i> property for
     * the specified font.
     * 
     * @param bold
     *            True|False|null
     */
    void setFontBold(Boolean bold);

    /**
     * Sets a boolean object (or null) indicating the <i>italic</i> property
     * for the specified font.
     * 
     * @param italic
     *            True|False|null
     */
    void setFontItalic(Boolean italic);

    /**
     * Sets a boolean object (or null) indicating the <i>underline</i> property
     * for the specified font.
     * 
     * @param underline
     *            True|False|null
     */
    void setFontUnderline(Boolean underline);

    /**
     * Sets a string value indicating the name of the font used for this
     * component.
     * 
     * @param name
     *            font name
     */
    void setFontName(String name);

    /**
     * Sets a string indicating the size to use for the selected font. (or any
     * other CSS accepted value for font size).
     * 
     * @param size
     *            xx-small|x-small|small|medium|large|x-large|xx-large|smaller|larger|<i>length</i>
     *            for example 10px|<i>%</i>
     */
    void setFontSize(String size);
}
