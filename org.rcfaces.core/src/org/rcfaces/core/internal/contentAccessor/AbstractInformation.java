/*
 * $Id: AbstractInformation.java,v 1.4 2013/11/13 12:53:23 jbmeslin Exp $
 */
package org.rcfaces.core.internal.contentAccessor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.security.MessageDigest;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.faces.component.StateHolder;
import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.internal.Constants;
import org.rcfaces.core.internal.lang.StringAppender;
import org.rcfaces.core.internal.util.Base64;
import org.rcfaces.core.internal.util.MessageDigestSelector;
import org.rcfaces.core.internal.util.StateHolderTools;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.4 $ $Date: 2013/11/13 12:53:23 $
 */
public class AbstractInformation implements StateHolder,
        IResourceKeyParticipant {
    
    private static final Log LOG = LogFactory.getLog(AbstractInformation.class);

    private static final int INITIAL_KEY_SIZE = 4096;

    private static final int INITIAL_SERIALIZATION_SIZE = 16000;

    private Map<String, Object> attributes;

    private boolean transientState;

    private String cachedParticipeKey = null;

    private long hashCode = 0;

    public final Object getAttribute(String attributeName) {
        if (attributes == null) {
            return null;
        }

        return attributes.get(attributeName);
    }

    public final Object setAttribute(String attributeName, Object attributeValue) {
        cachedParticipeKey = null;

        if (attributes == null) {
            attributes = new HashMap<String, Object>();
        }

        return attributes.put(attributeName, attributeValue);
    }

    public final Map<String, Object> getAttributes() {
        if (attributes == null) {
            return Collections.emptyMap();
        }
        return attributes;
    }

    @Override
    public int hashCode() {
        return computeCachedParticipeKey().hashCode();
    }

    private String computeCachedParticipeKey() {
        if (cachedParticipeKey != null) {
            return cachedParticipeKey;
        }

        StringAppender sa = new StringAppender(INITIAL_KEY_SIZE);
        participeKey(sa);

        cachedParticipeKey = sa.toString();

        return cachedParticipeKey;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final AbstractInformation other = (AbstractInformation) obj;

        return computeCachedParticipeKey().equals(
                other.computeCachedParticipeKey());
    }

    public void restoreState(FacesContext context, Object state) {
        cachedParticipeKey = null;

        Object states[] = (Object[]) state;

        attributes = new HashMap<String, Object>(states.length / 2);

        for (int i = 0; i < states.length;) {
            String key = (String) states[i++];

            Object value = states[i++];
            if (StateHolderTools.isPrimitive(value) == false) {
                value = UIComponentBase.restoreAttachedState(context, value);
            }

            attributes.put(key, value);
        }
    }

    public Object saveState(FacesContext context) {
        Object ret[] = new Object[attributes.size() * 2];

        int i = 0;
        for (Iterator<Map.Entry<String, Object>> it = attributes.entrySet()
                .iterator(); it.hasNext();) {
            Map.Entry<String, Object> entry = it.next();

            ret[i++] = entry.getKey();

            Object value = entry.getValue();

            if (StateHolderTools.isPrimitive(value)) {
                ret[i++] = value;
                continue;
            }

            ret[i++] = UIComponentBase.saveAttachedState(context, value);
        }

        return ret;
    }

    public boolean isTransient() {
        return transientState;
    }

    public void setTransient(boolean transientState) {
        this.transientState = transientState;
    }

    protected void appendToKey(StringAppender sa, String propertyName,
            Object value) {
        LOG.debug("Ignore key '" + propertyName + "'.");
    }

    protected boolean participeSerializableHashCode(StringAppender sa,
            String propertyName, Serializable serializable) {

        if (serializable == null) {
            participeValue(sa, propertyName, null);
            return true;
        }

        try {
            ByteArrayOutputStream byos = new ByteArrayOutputStream(
                    INITIAL_SERIALIZATION_SIZE);
            ObjectOutputStream oos = new ObjectOutputStream(byos);

            oos.writeObject(serializable);

            oos.close();

            byte result[] = byos.toByteArray();

            MessageDigest messageDigest = MessageDigestSelector
                    .getInstance(Constants.SERIALISATION_HASH_ALGORITHMS);

            byte digested[] = messageDigest.digest(result);

            String hashCode = Base64.encodeBytes(digested,
                    Base64.DONT_BREAK_LINES);

            StringAppender hc = new StringAppender(hashCode, 16);
            hc.append(':').append(result.length);

            participeValue(sa, propertyName, hc);

            return true;

        } catch (IOException ex) {
            LOG.error("Can not compute hashcode of serializable "
                    + serializable, ex);
        }

        return false;
    }

    public void participeKey(StringAppender sa) {
        for (Iterator<Map.Entry<String, Object>> it = attributes.entrySet()
                .iterator(); it.hasNext();) {
            Map.Entry<String, Object> entry = it.next();

            String key = entry.getKey();

            sa.append(IResourceKeyParticipant.RESOURCE_KEY_SEPARATOR).append(
                    key);

            Object value = entry.getValue();
            if (value == null) {
                continue;
            }

            participeValue(sa, key, value);
        }
    }

    private void participeValue(StringAppender sa, String key, Object value) {

        if ((value instanceof String) || (value instanceof Number)
                || (value instanceof Boolean)) {
            sa.append(IResourceKeyParticipant.RESOURCE_KEY_SEPARATOR).append(
                    String.valueOf(value));
            return;
        }

        if (value instanceof StringAppender) {
            sa.append(IResourceKeyParticipant.RESOURCE_KEY_SEPARATOR).append(
                    (StringAppender) value);
            return;
        }

        if (value instanceof IResourceKeyParticipant) {
            sa.append(IResourceKeyParticipant.RESOURCE_KEY_SEPARATOR);
            ((IResourceKeyParticipant) value).participeKey(sa);
            return;
        }

        if (value.getClass().isArray()) {
            int len = Array.getLength(value);
            sa.append('[');
            for (int i = 0; i < len; i++) {
                if (i > 0) {
                    sa.append(',');
                }

                Object valueItem = Array.get(value, i);
                if (valueItem == null) {
                    continue;
                }

                participeValue(sa, key, valueItem);
            }

            sa.append(']');

            return;
        }

        if (value instanceof Collection) {
            Collection< ? > cl = (Collection< ? >) value;

            sa.append('[');
            boolean first = true;
            for (Iterator< ? > it = cl.iterator(); it.hasNext();) {
                if (first) {
                    first = false;
                } else {
                    sa.append(',');
                }

                Object valueItem = it.next();
                if (valueItem == null) {
                    continue;
                }

                participeValue(sa, key, valueItem);
            }

            sa.append(']');

            return;
        }

        appendToKey(sa, key, value);

    }

}
