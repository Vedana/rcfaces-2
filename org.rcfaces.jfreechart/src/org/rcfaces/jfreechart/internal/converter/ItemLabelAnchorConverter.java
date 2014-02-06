package org.rcfaces.jfreechart.internal.converter;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.jfree.chart.labels.ItemLabelAnchor;
import org.rcfaces.core.model.AbstractConverter;

/**
 * @author jbmeslin@vedana.com
 * @version 
 */
public class ItemLabelAnchorConverter extends AbstractConverter {
    private static final String REVISION = "$Revision: 1.1 $";

    public static final Converter SINGLETON = new ItemLabelAnchorConverter();

    protected static final String CENTER_POSITION_NAME = "center";
    
    protected static final String INSIDE1_POSITION_NAME = "inside1";
    protected static final String INSIDE2_POSITION_NAME = "inside2";
    protected static final String INSIDE3_POSITION_NAME = "inside3";
    protected static final String INSIDE4_POSITION_NAME = "inside4";
    protected static final String INSIDE5_POSITION_NAME = "inside5";
    protected static final String INSIDE6_POSITION_NAME = "inside6";
    protected static final String INSIDE7_POSITION_NAME = "inside7";
    protected static final String INSIDE8_POSITION_NAME = "inside8";
    protected static final String INSIDE9_POSITION_NAME = "inside9";
    protected static final String INSIDE10_POSITION_NAME = "inside10";
    protected static final String INSIDE11_POSITION_NAME = "inside11";
    protected static final String INSIDE12_POSITION_NAME = "inside12";

    protected static final String OUTSIDE1_POSITION_NAME = "outside1";
    protected static final String OUTSIDE2_POSITION_NAME = "outside2";
    protected static final String OUTSIDE3_POSITION_NAME = "outside3";
    protected static final String OUTSIDE4_POSITION_NAME = "outside4";
    protected static final String OUTSIDE5_POSITION_NAME = "outside5";
    protected static final String OUTSIDE6_POSITION_NAME = "outside6";
    protected static final String OUTSIDE7_POSITION_NAME = "outside7";
    protected static final String OUTSIDE8_POSITION_NAME = "outside8";
    protected static final String OUTSIDE9_POSITION_NAME = "outside9";
    protected static final String OUTSIDE10_POSITION_NAME = "outside10";
    protected static final String OUTSIDE11_POSITION_NAME = "outside11";
    protected static final String OUTSIDE12_POSITION_NAME = "outside12";
    

    private static final String DEFAULT_POSITION_NAME = "outside12";

    private static final ItemLabelAnchor DEFAULT_POSITION = ItemLabelAnchor.OUTSIDE12;

    protected static Map LABEL_ANCHOR_POSITIONS = new HashMap(5);
    static {
        ItemLabelAnchor ila = ItemLabelAnchor.INSIDE1;
        LABEL_ANCHOR_POSITIONS.put(INSIDE1_POSITION_NAME, ila);
        ila = ItemLabelAnchor.INSIDE2;
        LABEL_ANCHOR_POSITIONS.put(INSIDE2_POSITION_NAME, ila);
        ila = ItemLabelAnchor.INSIDE3;
        LABEL_ANCHOR_POSITIONS.put(INSIDE3_POSITION_NAME, ila);
        ila = ItemLabelAnchor.INSIDE4;
        LABEL_ANCHOR_POSITIONS.put(INSIDE4_POSITION_NAME, ila);
        ila = ItemLabelAnchor.INSIDE5;
        LABEL_ANCHOR_POSITIONS.put(INSIDE5_POSITION_NAME, ila);
        ila = ItemLabelAnchor.INSIDE6;
        LABEL_ANCHOR_POSITIONS.put(INSIDE6_POSITION_NAME, ila);
        ila = ItemLabelAnchor.INSIDE7;
        LABEL_ANCHOR_POSITIONS.put(INSIDE7_POSITION_NAME, ila);
        ila = ItemLabelAnchor.INSIDE8;
        LABEL_ANCHOR_POSITIONS.put(INSIDE8_POSITION_NAME, ila);
        ila = ItemLabelAnchor.INSIDE9;
        LABEL_ANCHOR_POSITIONS.put(INSIDE9_POSITION_NAME, ila);
        ila = ItemLabelAnchor.INSIDE10;
        LABEL_ANCHOR_POSITIONS.put(INSIDE10_POSITION_NAME, ila);
        ila = ItemLabelAnchor.INSIDE11;
        LABEL_ANCHOR_POSITIONS.put(INSIDE11_POSITION_NAME, ila);
        ila = ItemLabelAnchor.INSIDE12;
        LABEL_ANCHOR_POSITIONS.put(INSIDE12_POSITION_NAME, ila);
        

        ila = ItemLabelAnchor.OUTSIDE1;
        LABEL_ANCHOR_POSITIONS.put(OUTSIDE1_POSITION_NAME, ila);
        ila = ItemLabelAnchor.OUTSIDE2;
        LABEL_ANCHOR_POSITIONS.put(OUTSIDE2_POSITION_NAME, ila);
        ila = ItemLabelAnchor.OUTSIDE3;
        LABEL_ANCHOR_POSITIONS.put(OUTSIDE3_POSITION_NAME, ila);
        ila = ItemLabelAnchor.OUTSIDE4;
        LABEL_ANCHOR_POSITIONS.put(OUTSIDE4_POSITION_NAME, ila);
        ila = ItemLabelAnchor.OUTSIDE5;
        LABEL_ANCHOR_POSITIONS.put(OUTSIDE5_POSITION_NAME, ila);
        ila = ItemLabelAnchor.OUTSIDE6;
        LABEL_ANCHOR_POSITIONS.put(OUTSIDE6_POSITION_NAME, ila);
        ila = ItemLabelAnchor.OUTSIDE7;
        LABEL_ANCHOR_POSITIONS.put(OUTSIDE7_POSITION_NAME, ila);
        ila = ItemLabelAnchor.OUTSIDE8;
        LABEL_ANCHOR_POSITIONS.put(OUTSIDE8_POSITION_NAME, ila);
        ila = ItemLabelAnchor.OUTSIDE9;
        LABEL_ANCHOR_POSITIONS.put(OUTSIDE9_POSITION_NAME, ila);
        ila = ItemLabelAnchor.OUTSIDE10;
        LABEL_ANCHOR_POSITIONS.put(OUTSIDE10_POSITION_NAME, ila);
        ila = ItemLabelAnchor.OUTSIDE11;
        LABEL_ANCHOR_POSITIONS.put(OUTSIDE11_POSITION_NAME, ila);
        ila = ItemLabelAnchor.OUTSIDE12;
        LABEL_ANCHOR_POSITIONS.put(OUTSIDE12_POSITION_NAME, ila);
        
        ila = ItemLabelAnchor.CENTER;
        LABEL_ANCHOR_POSITIONS.put(CENTER_POSITION_NAME, ila);
    }

    protected Map getTextPositions() {
        return LABEL_ANCHOR_POSITIONS;
    }

    protected ItemLabelAnchor getDefaultPosition() {
        return DEFAULT_POSITION;
    }

    public Object getAsObject(FacesContext context, UIComponent component,
            String value) {

        if (value == null || value.length() < 1
                || "default".equalsIgnoreCase(value)) {
            return getDefaultPosition();
        }

        value = value.toLowerCase();

        ItemLabelAnchor i =  (ItemLabelAnchor) getTextPositions().get(value);
        if (i != null) {
            return i;
        }

        throw new IllegalArgumentException("Keyword '" + value
                + "' is not supported for a itemLabelAnchor type !");
    }

    public String getAsString(FacesContext context, UIComponent component,
            Object value) {

        if (value == null) {
            return (String) getTextPositions().get(getDefaultPosition());
        }

        if ((value instanceof ItemLabelAnchor) == false) {
            throw new IllegalArgumentException("Value must be an ItemLabelAnchor !");
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
