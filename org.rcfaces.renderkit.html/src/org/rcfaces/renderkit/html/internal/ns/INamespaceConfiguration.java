/*
 * $Id: INamespaceConfiguration.java,v 1.1 2013/01/11 15:45:02 jbmeslin Exp $
 */
package org.rcfaces.renderkit.html.internal.ns;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.1 $ $Date: 2013/01/11 15:45:02 $
 */
public interface INamespaceConfiguration {
    void addComponent(String componentName);

    void addAttribute(String componentName, String propertyName);

    void addAttributes(String componentName, String[] propertiesName);

    void addComponentNS(String namespaceURI, String componentName);

    void addAttributeNS(String namespaceURI, String componentName,
            String propertyName);

    void addAttributeNS(String namespaceURI, String componentName,
            String[] propertiesName);
}
