package org.rcfaces.core.internal.converter;

import java.util.HashMap;
import java.util.Map;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.rcfaces.core.model.AbstractConverter;

/**
 * 
 * @author jbmeslin@vedana.com
 */
public class StrategyListenerConverter extends AbstractConverter {

    public static final Converter SINGLETON = new StrategyListenerConverter();

    private final static int NORMAL= 0x00;
    
    private final static int DEFAULT= 0x02;
    
    private final static int CLEAN_ALL= 0x02;
    
    private final static int CLEAN_BY_CLASS = 0x04;
    
    private final static int ADD_IF_NEW = 0x08;
    
    private static final Map STRATEGY = new HashMap(8);
    static {
    	STRATEGY.put("DEFAULT", new Integer(DEFAULT));
    	STRATEGY.put("NORMAL", new Integer(NORMAL));
    	STRATEGY.put("CLEAN_ALL", new Integer(CLEAN_ALL));
    	STRATEGY.put("CLEAN_BY_CLASS", new Integer(CLEAN_BY_CLASS));
    	STRATEGY.put("ADD_IF_NEW", new Integer(ADD_IF_NEW));
    }

    public Object getAsObject(FacesContext context, UIComponent component,
            String value) {
        if (value == null) {
            return DEFAULT;
        }
        
        Integer strategy = (Integer)  STRATEGY.get(value.trim().toUpperCase());
        
        return strategy;
    }

    public String getAsString(FacesContext context, UIComponent component,
            Object value) {
        throw new FacesException("Not implemented !");
    }
}
