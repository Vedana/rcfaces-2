/*
 * $Id: DragDropEffectsConverter.java,v 1.2 2013/07/03 12:25:03 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.internal.converter;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.rcfaces.core.component.capability.IDragAndDropEffects;
import org.rcfaces.core.model.AbstractConverter;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:03 $
 */
public class DragDropEffectsConverter extends AbstractConverter {
    

    public static final Converter SINGLETON = new DragDropEffectsConverter();

    private static final Integer NO_EFFECTS = new Integer(0);

    private static final Map EFFECTS = new HashMap(8);
    static {
        EFFECTS.put("NONE", new Integer(IDragAndDropEffects.NONE_DND_EFFECT));
        EFFECTS.put("DEFAULT", new Integer(
                IDragAndDropEffects.DEFAULT_DND_EFFECT));
        EFFECTS.put("COPY", new Integer(IDragAndDropEffects.COPY_DND_EFFECT));
        EFFECTS.put("LINK", new Integer(IDragAndDropEffects.LINK_DND_EFFECT));
        EFFECTS.put("MOVE", new Integer(IDragAndDropEffects.MOVE_DND_EFFECT));
        EFFECTS.put("ANY", new Integer(IDragAndDropEffects.ANY_DND_EFFECT));
        EFFECTS.put("UNKNOWN", new Integer(
                IDragAndDropEffects.UNKNOWN_DND_EFFECT));
    }

    public Object getAsObject(FacesContext context, UIComponent component,
            String value) {
        if (value == null) {
            return NO_EFFECTS;
        }

        StringTokenizer st = new StringTokenizer(value, ",; ");

        int mask = 0;

        for (; st.hasMoreTokens();) {
            String token = st.nextToken().toUpperCase();

            Integer flags = (Integer) EFFECTS.get(token);
            if (flags == null) {
                continue;
            }

            mask |= flags.intValue();
        }

        if (mask == 0) {
            return NO_EFFECTS;
        }

        return new Integer(mask);
    }

    public String getAsString(FacesContext context, UIComponent component,
            Object value) {
        throw new FacesException("Not implemented !");
    }

    public static Integer convertUpperCase(String key) {
        return (Integer) EFFECTS.get(key);
    }
}
