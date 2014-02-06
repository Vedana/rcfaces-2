/*
 * $Id: HtmlRequestContext.java,v 1.4 2013/07/03 12:25:09 jbmeslin Exp $
 */
package org.rcfaces.renderkit.html.internal;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.render.Renderer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.component.capability.IUnlockedClientAttributesCapability;
import org.rcfaces.core.internal.renderkit.AbstractRequestContext;
import org.rcfaces.core.internal.renderkit.IComponentData;
import org.rcfaces.core.internal.renderkit.IDecoderContext;
import org.rcfaces.core.internal.renderkit.IDefaultUnlockedPropertiesRenderer;
import org.rcfaces.core.internal.renderkit.IProcessContext;
import org.rcfaces.core.internal.renderkit.IRequestContext;

/**
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.4 $ $Date: 2013/07/03 12:25:09 $
 */
public class HtmlRequestContext extends AbstractRequestContext implements
        IHtmlRequestContext {

    private static final Log LOG = LogFactory.getLog(HtmlRequestContext.class);

    public static final String EVENT_SERIAL = "VFC_SERIAL";

    private static final String EVENT_NAME = "VFC_EVENT";

    private static final String EVENT_COMPONENT_ID = "VFC_COMPONENT";

    private static final String EVENT_VALUE = "VFC_VALUE";

    private static final String EVENT_OBJECT_VALUE = "VFC_OVALUE";

    private static final String EVENT_ITEM = "VFC_ITEM";

    private static final String EVENT_DETAIL = "VFC_DETAIL";

    private static final String REQUEST_CONTEXT = "camelia.request.context";

    private static final String PROPERTY_SEPARATORS = ",}";

    private static final char PROPERTY_START = '{';

    private static final char PROPERTY_END = '}';

    private Map<String, String[]> parameters;

    private Map<String, Object> properties;

    private String eventComponentId;

    private IHtmlProcessContext processContext;

    public void setFacesContext(FacesContext facesContext) {
        super.setFacesContext(facesContext);

        processContext = HtmlProcessContextImpl
                .getHtmlProcessContext(facesContext);

        parameters = facesContext.getExternalContext()
                .getRequestParameterValuesMap();

        properties = parseProperties(parameters);
        if (LOG.isTraceEnabled()) {
            LOG.trace("Parsed camelia properties => " + properties);
        }

        Set<String> keys = new HashSet<String>(parameters.size()
                + properties.size());
        keys.addAll(parameters.keySet());
        keys.addAll(properties.keySet());

        for (String key : keys) {
            putComponentData(key, Boolean.FALSE);
        }

        eventComponentId = getStringParameter(parameters, EVENT_COMPONENT_ID);
        if (eventComponentId != null) {
            putComponentData(eventComponentId, Boolean.FALSE);

            if (LOG.isDebugEnabled()) {
                LOG.debug("Event component id detected: " + eventComponentId);
            }

        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("No event component id detected");
            }
        }
    }

    protected Map<String, Object> parseProperties(Map parameters) {
        Object facesDatas = parameters.get(EVENT_SERIAL);
        if (facesDatas == null) {
            return Collections.emptyMap();
        }

        return parseCameliaData(facesDatas);
    }

    public String getComponentId(FacesContext facesContext,
            UIComponent component) {
        if (processContext.isFlatIdentifierEnabled() == false) {
            return component.getClientId(facesContext);
        }

        String id = component.getId();

        if (id == null || id.startsWith(UIViewRoot.UNIQUE_ID_PREFIX)) {
            return component.getClientId(facesContext);
        }

        return id;
    }

    protected IComponentData getComponentData(UIComponent component,
            String componentId, Object data, Renderer renderer) {

        Map<String, Object> properties = Collections.emptyMap();
        Map<String, String[]> parameters = this.parameters;

        Object values = this.properties.get(componentId);
        if (values instanceof String) {
            // Il faut transformer la valeur serialisée en Map
            properties = HtmlTools.decodeParametersToMap(getProcessContext(),
                    component, renderer, (String) values, PROPERTY_SEPARATORS,
                    "");

            if (LOG.isDebugEnabled()) {
                LOG.debug("Decode component data of '" + componentId + "' to "
                        + properties);
            }
        }

        Object parameterValues = null;
        if (parameters != null) {
            parameterValues = parameters.get(componentId);

            if (parameterValues != null) {
                if (parameterValues.getClass().isArray()) {
                    if (Array.getLength(parameterValues) == 0) {
                        parameterValues = null;
                    }
                }
            }

            if (LOG.isDebugEnabled()) {
                LOG.debug("Parameters values of component '" + componentId
                        + "'=" + parameterValues);
            }
        }

        boolean eventComponent = false;

        if (eventComponentId != null && eventComponentId.equals(componentId)) {
            eventComponent = true;

            if (LOG.isDebugEnabled()) {
                LOG.debug("Event detected for component '" + componentId + "'.");
            }

        } else if ((properties == null || properties.isEmpty())
                && (parameterValues == null)) {

            if (LOG.isDebugEnabled()) {
                LOG.debug("No properites, no parameters for component '"
                        + componentId + "'.");
            }

            return emptyComponentData();
        }

        Set<Serializable> unlockedProperties = null;

        if (isLockedClientAttributes()) {
            if (component instanceof IUnlockedClientAttributesCapability) {
                unlockedProperties = filterProperties(
                        (IUnlockedClientAttributesCapability) component, true);

                if (LOG.isDebugEnabled()) {
                    LOG.debug("Filtred properties => '" + properties + "'.");
                }

            } else {
                unlockedProperties = Collections.emptySet();
            }

        } else if (component instanceof IUnlockedClientAttributesCapability) {
            unlockedProperties = filterProperties(
                    (IUnlockedClientAttributesCapability) component, false);

        }

        if (unlockedProperties != null
                && (renderer instanceof IDefaultUnlockedPropertiesRenderer)) {
            Serializable defaultUnlockedProperties[] = ((IDefaultUnlockedPropertiesRenderer) renderer)
                    .getDefaultUnlockedProperties(getFacesContext(), component);
            if (defaultUnlockedProperties != null
                    && defaultUnlockedProperties.length > 0) {
                unlockedProperties = new HashSet(unlockedProperties);
                unlockedProperties.addAll(Arrays
                        .asList(defaultUnlockedProperties));
            }
        }

        HtmlComponentData hcd = new HtmlComponentData();
        hcd.set(parameters, component, componentId, eventComponent, properties,
                unlockedProperties);

        return hcd;
    }

    private Set<Serializable> filterProperties(
            IUnlockedClientAttributesCapability component, boolean defaultLock) {
        String unlockedAttributes = component.getUnlockedClientAttributeNames();
        if (unlockedAttributes == null) {
            if (defaultLock) {
                return Collections.emptySet();
            }

            return null;
        }

        if (defaultLock == false && unlockedAttributes.length() == 0) {
            return Collections.emptySet();
        }

        unlockedAttributes = unlockedAttributes.trim();
        if ("*".equals(unlockedAttributes)) {
            return null;
        }

        Set<Serializable> ret = null;

        StringTokenizer st = new StringTokenizer(unlockedAttributes,
                ",; \t\r\n");
        for (; st.hasMoreTokens();) {
            String attributeName = st.nextToken();

            if (ret == null) {
                ret = new HashSet<Serializable>(unlockedAttributes.length() / 8);
            }
            ret.add(attributeName);
        }

        if (ret == null) {
            return Collections.emptySet();
        }

        return ret;
    }

    private static final String getStringParameter(Map parameters,
            Serializable name) {
        Object value = parameters.get(name.toString());
        if (value == null) {

            if (LOG.isDebugEnabled()) {
                LOG.debug("getStringParameter('" + name
                        + "')=null   parameters=" + parameters);
            }

            return null;
        }

        if (value instanceof String[]) {
            String array[] = (String[]) value;

            if (LOG.isDebugEnabled()) {
                LOG.debug("getStringParameter('" + name + "')="
                        + Arrays.asList(array) + "   parameters=" + parameters);
            }

            if (array.length < 1) {
                return null;
            }

            return array[0];
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("getStringParameter('" + name + "')='" + value
                    + "'   parameters=" + parameters);
        }

        return value.toString();
    }

    private Map<String, Object> parseCameliaData(Object object) {
        String datas;
        if (object instanceof String) {
            datas = (String) object;

        } else if (object.getClass().isArray()) {
            if (Array.getLength(object) == 0) {

                if (LOG.isDebugEnabled()) {
                    LOG.debug("parseCameliaData('" + object
                            + "')=*Empty array*");
                }

                return Collections.emptyMap();
            }

            Map<String, Object> ret = parseCameliaData(Array.get(object, 0));

            if (LOG.isDebugEnabled()) {
                LOG.debug("parseCameliaData('" + object + "')[0]='" + ret + "'");
            }

            return ret;

        } else {

            if (LOG.isDebugEnabled()) {
                LOG.debug("parseCameliaData('" + object + "')=*UNKNOWN*");
            }

            return Collections.emptyMap();
        }

        char cs[] = datas.toCharArray();
        Map<String, Object> properties = new HashMap<String, Object>(
                (cs.length / 16) + 1);

        for (int i = 0; i < cs.length;) {
            int nameStart = i;
            int nameEnd = 0;
            char c;

            for (; i < cs.length; i++) {
                c = cs[i];

                if (c != '=') {
                    continue;
                }

                nameEnd = i;
                break;
            }
            if (i == cs.length) {
                throwFormatException("EOF", i, datas);
            }

            i++;
            if (i == cs.length) {
                throwFormatException("EOF", i, datas);
            }

            if (cs[i++] != PROPERTY_START) {
                throwFormatException("Bad Char ", i, datas);
            }

            int valueStart = i;
            int valueEnd = 0;
            for (; i < cs.length; i++) {
                c = cs[i];

                if (c != PROPERTY_END) {
                    continue;
                }

                valueEnd = i;
                break;

            }

            if (i == cs.length) {
                throwFormatException("EOF", i, datas);
            }

            String componentId = datas.substring(nameStart, nameEnd);

            properties.put(componentId, datas.substring(valueStart, valueEnd));

            i++;
            if (i == cs.length) {
                break;
            }

            if (PROPERTY_SEPARATORS.indexOf(cs[i]) < 0) {
                throwFormatException("Bad Char ", i, datas);
            }
            i++;
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("parseCameliaData('" + object + "')='" + properties + "'");
        }

        if (properties.size() < 1) {
            return Collections.emptyMap();
        }

        return properties;
    }

    private static void throwFormatException(String message, int i, String datas) {
        throw new FacesException("Bad format of rcfaces serialized datas ! ("
                + message + ": pos=" + i + " data='" + datas + "')");
    }

    protected IComponentData createEmptyComponentData() {
        AbstractHtmlComponentData componentData = new AbstractHtmlComponentData() {

            public Object getProperty(Serializable name) {
                return null;
            }

            public String getComponentParameter() {
                return null;
            }

            public String[] getComponentParameters() {
                return null;
            }

            public boolean isEventComponent() {
                return false;
            }

            public boolean containsKey(Serializable name) {
                return false;
            }

        };

        componentData.set(parameters);

        return componentData;
    }

    public IProcessContext getProcessContext() {
        return processContext;
    }

    public IHtmlProcessContext getHtmlProcessContext() {
        return processContext;
    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.4 $ $Date: 2013/07/03 12:25:09 $
     */
    protected static abstract class AbstractHtmlComponentData extends
            AbstractComponentData {

        private Map parameters;

        private boolean detailInitialized;

        private int detail;

        private boolean valueDeserialized;

        private Object eventValue;

        public final void set(Map parameters) {
            this.parameters = parameters;
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * org.rcfaces.core.internal.renderkit.IComponentData#getParameter(java
         * .lang.String)
         */
        public final String getParameter(Serializable parameterName) {
            return getStringParameter(parameters, parameterName);
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * org.rcfaces.core.internal.renderkit.IComponentData#getParameters(
         * java.lang.String)
         */
        public final String[] getParameters(Serializable parameterName) {
            Object value = parameters.get(parameterName.toString());
            if (value == null) {
                return null;
            }

            if (value instanceof String[]) {
                return (String[]) value;
            }

            return new String[] { value.toString() };
        }

        public void release() {
            parameters = null;
        }

        public final String getEventName() {
            return getParameter(EVENT_NAME);
        }

        public final String getEventValue() {
            return getParameter(EVENT_VALUE);
        }

        public final String getEventItem() {
            return getParameter(EVENT_ITEM);
        }

        public Object getEventObject(IDecoderContext decoderContext) {
            if (valueDeserialized == false) {
                valueDeserialized = true;

                String eventObject = getParameter(EVENT_OBJECT_VALUE);

                if (eventObject != null && eventObject.length() > 0) {
                    eventValue = HtmlTools.decodeObject(eventObject,
                            decoderContext, "event");
                }
            }

            return eventValue;
        }

        public final int getEventDetail() {
            if (detailInitialized == false) {
                detailInitialized = true;

                String detailString = getParameter(EVENT_DETAIL);
                if (detailString != null && detailString.length() > 0) {
                    try {
                        detail = Integer.parseInt(detailString);

                    } catch (NumberFormatException ex) {
                        throw new FacesException("Invalid event detail '"
                                + detail + "', it is not an integer !", ex);
                    }
                }
            }

            return detail;
        }

        public String toString() {
            return "[AbstractHtmlComponentData parameters='" + parameters
                    + "' detailInitialized=" + detailInitialized + " detail='"
                    + detail + "' valueDeserialized=" + valueDeserialized
                    + " eventValue='" + eventValue + "']";
        }
    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.4 $ $Date: 2013/07/03 12:25:09 $
     */
    protected static class HtmlComponentData extends AbstractHtmlComponentData {

        private String componentId;

        private UIComponent component;

        private Map properties;

        private boolean eventComponent;

        private Set unlockedProperties;

        public void set(Map parameters, UIComponent component,
                String componentId, boolean eventComponent, Map properties,
                Set unlockedProperties) {
            super.set(parameters);

            this.component = component;
            this.componentId = componentId;
            this.properties = properties;
            this.eventComponent = eventComponent;
            this.unlockedProperties = unlockedProperties;

            if (LOG.isDebugEnabled()) {
                LOG.debug("Init componentData");
                LOG.debug("-        componentId='" + componentId + "'");
                LOG.debug("-          component='" + component + "'");
                LOG.debug("-     eventComponent='" + eventComponent + "'");
                LOG.debug("-         properties='" + properties + "'");
                LOG.debug("- unlockedProperties='" + unlockedProperties + "'");

                LOG.debug("-         parameters=");
                if (parameters != null) {
                    for (Iterator it = parameters.entrySet().iterator(); it
                            .hasNext();) {
                        Map.Entry entry = (Map.Entry) it.next();

                        String key = (String) entry.getKey();
                        Object vs = entry.getValue();

                        if (vs instanceof String[]) {
                            LOG.debug(" # '" + key + "'='"
                                    + Arrays.asList((String[]) vs) + "'");
                            continue;
                        }

                        LOG.debug("  '" + key + "'='" + vs + "'");
                    }
                }
            }
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * org.rcfaces.core.internal.renderkit.IComponentData#getProperty(java
         * .lang.String)
         */
        public Object getProperty(Serializable propertyName) {
            String name = propertyName.toString();

            if (unlockedProperties != null
                    && unlockedProperties.contains(name) == false) {
                return null;
            }

            return properties.get(name);
        }

        public boolean containsKey(Serializable propertyName) {
            String name = propertyName.toString();

            if (unlockedProperties != null
                    && unlockedProperties.contains(name) == false) {
                return false;
            }

            return properties.containsKey(name);
        }

        /*
         * public void release() { properties = null; component = null;
         * componentId = null;
         * 
         * super.release(); }
         */

        public final String getComponentParameter() {
            String key = getComponentId();
            if (key == null) {
                return null;
            }
            return getParameter(key);
        }

        public final String[] getComponentParameters() {
            String key = getComponentId();
            if (key == null) {
                return null;
            }
            return getParameters(key);
        }

        protected String getComponentId() {
            return componentId;
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * org.rcfaces.core.internal.renderkit.IComponentData#isEventComponent()
         */
        public boolean isEventComponent() {
            return eventComponent;
        }

        public String toString() {
            return "[HtmlComponentData super='" + super.toString()
                    + "' componentId='" + componentId + "' component='"
                    + component + "' properties='" + properties
                    + "' eventComponent=" + eventComponent
                    + " unlockedProperties='" + unlockedProperties + "']";
        }
    }

    static IRequestContext getRequestContext(FacesContext context) {
        Map<String, Object> requestMap = context.getExternalContext()
                .getRequestMap();

        IRequestContext requestContext = (IRequestContext) requestMap
                .get(REQUEST_CONTEXT);
        if (requestContext != null) {
            return requestContext;
        }

        requestContext = createRequestContext(context);
        requestMap.put(REQUEST_CONTEXT, requestContext);

        return requestContext;
    }

    static IRequestContext createRequestContext(FacesContext context) {
        HtmlRequestContext hrc = new HtmlRequestContext();
        hrc.setFacesContext(context);

        return hrc;
    }

    public String getEventComponentId() {
        return eventComponentId;
    }
}
