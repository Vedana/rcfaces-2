package org.rcfaces.jfreechart.internal.converter;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.ui.TextAnchor;
import org.rcfaces.core.model.AbstractConverter;

/**
 * @author jbmeslin@vedana.com
 * @version 
 */
public class ItemLabelTextAnchorConverter extends AbstractConverter {
    private static final String REVISION = "$Revision: 1.1 $";

    public static final Converter SINGLETON = new ItemLabelTextAnchorConverter();
    
    protected static final String BASELINE_CENTER_POSITION_NAME = "baseline-center";
    protected static final String BASELINE_LEFT_POSITION_NAME = "baseline-left";
    protected static final String BASELINE_RIGHT_POSITION_NAME = "baseline-right";
    
    protected static final String BOTTOM_CENTER_POSITION_NAME = "bottom-center";
    protected static final String BOTTOM_LEFT_POSITION_NAME = "bottom-left";
    protected static final String BOTTOM_RIGHT_POSITION_NAME = "bottom-right";
    
    protected static final String CENTER_POSITION_NAME = "center";
    protected static final String CENTER_LEFT_POSITION_NAME = "center-left";
    protected static final String CENTER_RIGHT_POSITION_NAME = "center-right";
    
    protected static final String HALF_ASCENT_CENTER_POSITION_NAME = "half-ascent-center";
    protected static final String HALF_ASCENT_LEFT_POSITION_NAME = "half-ascent-left";
    protected static final String HALF_ASCENT_RIGHT_POSITION_NAME = "half-ascent-right";
    
    protected static final String TOP_CENTER_POSITION_NAME = "top-center";
    protected static final String TOP_LEFT_POSITION_NAME = "top-left";
    protected static final String TOP_RIGHT_POSITION_NAME = "top-right";
    

    private static final String DEFAULT_POSITION_NAME = "bottom-center";

    private static final TextAnchor DEFAULT_POSITION = TextAnchor.BOTTOM_CENTER ;

    protected static Map LABEL_TEXT_ANCHOR_POSITIONS = new HashMap(5);
    static {
        TextAnchor ta = TextAnchor.BASELINE_CENTER;
        LABEL_TEXT_ANCHOR_POSITIONS.put(BASELINE_CENTER_POSITION_NAME, ta);
        ta = TextAnchor.BASELINE_LEFT;
        LABEL_TEXT_ANCHOR_POSITIONS.put(BASELINE_LEFT_POSITION_NAME, ta);
        ta = TextAnchor.BASELINE_RIGHT;
        LABEL_TEXT_ANCHOR_POSITIONS.put(BASELINE_RIGHT_POSITION_NAME, ta);
        
        ta = TextAnchor.BOTTOM_CENTER;
        LABEL_TEXT_ANCHOR_POSITIONS.put(BOTTOM_CENTER_POSITION_NAME, ta);
        ta = TextAnchor.BOTTOM_LEFT;
        LABEL_TEXT_ANCHOR_POSITIONS.put(BOTTOM_LEFT_POSITION_NAME, ta);
        ta = TextAnchor.BOTTOM_RIGHT;
        LABEL_TEXT_ANCHOR_POSITIONS.put(BOTTOM_RIGHT_POSITION_NAME, ta);
        
        ta = TextAnchor.CENTER;
        LABEL_TEXT_ANCHOR_POSITIONS.put(CENTER_POSITION_NAME, ta);
        ta = TextAnchor.CENTER_LEFT;
        LABEL_TEXT_ANCHOR_POSITIONS.put(CENTER_LEFT_POSITION_NAME, ta);
        ta = TextAnchor.CENTER_RIGHT;
        LABEL_TEXT_ANCHOR_POSITIONS.put(CENTER_RIGHT_POSITION_NAME, ta);
        
        ta = TextAnchor.HALF_ASCENT_CENTER;
        LABEL_TEXT_ANCHOR_POSITIONS.put(HALF_ASCENT_CENTER_POSITION_NAME, ta);
        ta = TextAnchor.HALF_ASCENT_LEFT;
        LABEL_TEXT_ANCHOR_POSITIONS.put(HALF_ASCENT_LEFT_POSITION_NAME, ta);
        ta = TextAnchor.HALF_ASCENT_RIGHT;
        LABEL_TEXT_ANCHOR_POSITIONS.put(HALF_ASCENT_RIGHT_POSITION_NAME, ta);
        
        ta = TextAnchor.TOP_CENTER;
        LABEL_TEXT_ANCHOR_POSITIONS.put(TOP_CENTER_POSITION_NAME, ta);
        ta = TextAnchor.TOP_LEFT;
        LABEL_TEXT_ANCHOR_POSITIONS.put(TOP_LEFT_POSITION_NAME, ta);
        ta = TextAnchor.TOP_RIGHT;
        LABEL_TEXT_ANCHOR_POSITIONS.put(TOP_RIGHT_POSITION_NAME, ta);
        
    }

    protected Map getTextPositions() {
        return LABEL_TEXT_ANCHOR_POSITIONS;
    }

    protected TextAnchor getDefaultPosition() {
        return DEFAULT_POSITION;
    }

    public Object getAsObject(FacesContext context, UIComponent component,
            String value) {

        if (value == null || value.length() < 1
                || "default".equalsIgnoreCase(value)) {
            return getDefaultPosition();
        }

        value = value.toLowerCase();

        TextAnchor i =  (TextAnchor) getTextPositions().get(value);
        if (i != null) {
            return i;
        }

        throw new IllegalArgumentException("Keyword '" + value
                + "' is not supported for a TextAnchor type !");
    }

    public String getAsString(FacesContext context, UIComponent component,
            Object value) {

        if (value == null) {
            return (String) getTextPositions().get(getDefaultPosition());
        }

        if ((value instanceof TextAnchor) == false) {
            throw new IllegalArgumentException("Value must be an TextAnchor !");
        }

        for (Iterator it = getTextPositions().entrySet().iterator(); it
                .hasNext();) {
            Map.Entry entry = (Map.Entry) it.next();

            if (value.equals(entry.getValue())) {
                return (String) entry.getKey();
            }
        }

        throw new IllegalArgumentException("Value '" + value
                + "' is not supported for a text-position type !");
    }

}
