package org.rcfaces.core.lang;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.faces.FacesException;
import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.internal.renderkit.AbstractProperties;
import org.rcfaces.core.model.IFilterProperties;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.4 $ $Date: 2013/12/11 10:17:38 $
 */
public final class FilterPropertiesMap extends AbstractProperties implements
        IFilterProperties {

    private static final Log LOG = LogFactory.getLog(FilterPropertiesMap.class);

    static final boolean TEST_MAP_CONTENT = false;

    private static final long serialVersionUID = -6566852140453141630L;

    private final Map<Serializable, Object> map;

    public FilterPropertiesMap(Map<Serializable, Object> map) {
        this.map = new HashMap<Serializable, Object>(map);
    }

    public FilterPropertiesMap(IFilterProperties filterProperties) {
        this.map = copyMap(filterProperties);
    }

    public FilterPropertiesMap() {
        this.map = new HashMap<Serializable, Object>();
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public Object put(Serializable key, Object value) {
        return map.put(key.toString(), value);
    }

    @Override
    public void putAll(Map<Serializable, Object> t) {
        if (TEST_MAP_CONTENT) {
            for (Iterator it = t.entrySet().iterator(); it.hasNext();) {
                Map.Entry entry = (Map.Entry) it.next();

                Object key = entry.getKey();
                if (key != null && (key instanceof String) == false) {
                    throw new FacesException("Key is not a String !");
                }

                Object value = entry.getValue();
                if (value != null && (value instanceof String) == false) {
                    throw new FacesException("Value is not a String !");
                }
            }
        }

        map.putAll(t);
    }

    @Override
    public Object remove(Serializable key) {
        return map.remove(key.toString());
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean containsKey(Serializable propertyName) {
        return map.containsKey(propertyName.toString());
    }

    @Override
    public String[] listNames() {
        Collection<Serializable> c = map.keySet();

        return c.toArray(new String[c.size()]);
    }

    @Override
    public Map<Serializable, Object> toMap() {
        return new HashMap<Serializable, Object>(map);
    }

    @Override
    public Object saveState(FacesContext context) {
        if (map.isEmpty()) {
            return null;
        }

        Object ret[] = new Object[map.size() * 2];

        int idx = 0;
        for (Iterator it = map.entrySet().iterator(); it.hasNext();) {
            Map.Entry entry = (Entry) it.next();

            ret[idx++] = entry.getKey();
            ret[idx++] = entry.getValue();
        }

        return ret;
    }

    @Override
    public void restoreState(FacesContext context, Object state) {
        if (state == null) {
            return;
        }

        Object p[] = (Object[]) state;

        for (int i = 0; i < p.length;) {
            map.put((Serializable) p[i++], p[i++]);
        }
    }

    @Override
    public boolean isTransient() {
        return false;
    }

    @Override
    public void setTransient(boolean newTransientValue) {
    }

    @Override
    public Object getProperty(Serializable name) {
        return map.get(name.toString());
    }

    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ((map == null) ? 0 : map.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }

        final FilterPropertiesMap other = (FilterPropertiesMap) obj;
        if (map == null) {
            if (other.map != null) {
                return false;
            }

        } else if (!map.equals(other.map)) {
            return false;
        }

        return true;
    }

    private Map<Serializable, Object> copyMap(IFilterProperties filterProperties) {
        if (filterProperties instanceof FilterPropertiesMap) {
            // Pas besoin de copie !
            return filterProperties.toMap();
        }

        return new HashMap<Serializable, Object>(filterProperties.toMap());
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("[FilterPropertiesMap map='").append(map).append("']");
        return builder.toString();
    }

    static {
        if (TEST_MAP_CONTENT) {
            LOG.info("TEST_MAP_CONTENT=" + TEST_MAP_CONTENT);
        }
    }

}