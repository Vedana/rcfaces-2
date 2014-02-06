/*
 * $Id: TextPositionConverter.java,v 1.2 2013/07/03 12:25:03 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.internal.converter;

import java.util.HashMap;
import java.util.Map;

import javax.faces.convert.Converter;

import org.rcfaces.core.component.capability.IHorizontalTextPositionCapability;
import org.rcfaces.core.component.capability.ITextPositionCapability;

/**
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:03 $
 */
public class TextPositionConverter extends HorizontalTextPositionConverter {
    

    public static final Converter SINGLETON = new TextPositionConverter();

    private static final String TOP_POSITION_NAME = "top";

    private static final String BOTTOM_POSITION_NAME = "bottom";

    private static final Integer DEFAULT_POSITION = new Integer(
            IHorizontalTextPositionCapability.DEFAULT_POSITION);

    private static Map TEXT_POSITIONS = new HashMap(HORIZONTAL_TEXT_POSITIONS);
    static {
        Integer i = new Integer(ITextPositionCapability.BOTTOM_POSITION);
        TEXT_POSITIONS.put(BOTTOM_POSITION_NAME, i);

        i = new Integer(ITextPositionCapability.TOP_POSITION);
        TEXT_POSITIONS.put(TOP_POSITION_NAME, i);
    }

    protected Map getTextPositions() {
        return TEXT_POSITIONS;
    }

    protected Integer getDefaultPosition() {
        return DEFAULT_POSITION;
    }
}
