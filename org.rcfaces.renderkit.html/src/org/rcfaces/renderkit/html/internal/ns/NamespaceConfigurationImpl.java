/*
 * $Id: NamespaceConfigurationImpl.java,v 1.1 2013/01/11 15:45:02 jbmeslin Exp $
 */
package org.rcfaces.renderkit.html.internal.ns;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.1 $ $Date: 2013/01/11 15:45:02 $
 */
public class NamespaceConfigurationImpl implements INamespaceConfiguration {

    private static final Log LOG = LogFactory
            .getLog(NamespaceConfigurationImpl.class);

    public static final String DEFAULT_NAMESPACE = "*";

    public static final String DEFAULT_COMPONENT = "*";

    private final Map<String, NameSpace> namespacesByURI = new HashMap<String, NameSpace>();

    public void addComponent(String componentName) {
        addComponentNS(DEFAULT_NAMESPACE, componentName);
    }

    public void addAttribute(String componentName, String propertyName) {
        addAttributeNS(DEFAULT_NAMESPACE, componentName, propertyName);
    }

    public void addAttributes(String componentName, String[] propertiesName) {
        addAttributeNS(DEFAULT_NAMESPACE, componentName, propertiesName);
    }

    public void addComponentNS(String namespaceURI, String componentName) {
        getNameSpace(namespaceURI).getComponent(componentName);
    }

    public void addAttributeNS(String namespaceURI, String componentName,
            String propertyName) {
        if (componentName == null) {
            componentName = DEFAULT_COMPONENT;
        }

        getNameSpace(namespaceURI).getComponent(componentName).addAttribute(
                propertyName);
    }

    public void addAttributeNS(String namespaceURI, String componentName,
            String[] propertiesName) {
        if (componentName == null) {
            componentName = DEFAULT_COMPONENT;
        }

        getNameSpace(namespaceURI).getComponent(componentName).addAttributes(
                propertiesName);
    }

    protected NameSpace getNameSpace(String namespaceURI) {
        NameSpace nameSpace = namespacesByURI.get(namespaceURI);
        if (nameSpace != null) {
            return nameSpace;
        }

        nameSpace = new NameSpace(namespaceURI);
        namespacesByURI.put(nameSpace.getURI(), nameSpace);

        return nameSpace;
    }

    public NameSpace[] listNamespaces() {
        Collection<NameSpace> cl = namespacesByURI.values();

        return cl.toArray(new NameSpace[cl.size()]);
    }

    public static class NameSpace {

        private final String uri;

        private final Map<String, Component> componentsByName = new HashMap<String, Component>();

        public NameSpace(String namespaceURI) {
            this.uri = namespaceURI;
        }

        public Component getComponent(String componentName) {
            Component component = componentsByName.get(componentName);
            if (component != null) {
                return component;
            }

            component = new Component(componentName);
            componentsByName.put(component.getName(), component);

            return component;
        }

        public String getURI() {
            return uri;
        }

        public Component[] listComponents() {
            Collection<Component> cl = componentsByName.values();

            return cl.toArray(new Component[cl.size()]);
        }

    }

    public static class Component {

        private final String name;

        private final Set<String> attributesName = new HashSet<String>();

        public Component(String componentName) {
            this.name = componentName;
        }

        public String getName() {
            return name;
        }

        public void addAttribute(String propertyName) {
            attributesName.add(propertyName);
        }

        public void addAttributes(String[] propertiesName) {
            this.attributesName.addAll(Arrays.asList(propertiesName));
        }

        public String[] listAttributes() {
            return attributesName.toArray(new String[attributesName.size()]);
        }

    }
}
