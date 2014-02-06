/*
 * $Id: CapabilitiesRepository.java,v 1.7 2012/12/07 13:45:23 oeuillot Exp $
 * 
 * $Log: CapabilitiesRepository.java,v $
 * Revision 1.7  2012/12/07 13:45:23  oeuillot
 * Pour JB
 *
 * Revision 1.6  2011/10/12 15:54:01  oeuillot
 * Fork JSF 2.1
 *
 * Revision 1.5  2010/05/20 12:07:25  oeuillot
 * *** empty log message ***
 *
 * Revision 1.4  2009/01/15 15:16:52  oeuillot
 * Refonte complete du compilateur pour le g�n�rateur JS->JAVA
 *
 * Revision 1.3  2008/01/02 09:40:19  oeuillot
 * *** empty log message ***
 *
 * Revision 1.2  2006/12/18 15:14:02  oeuillot
 * *** empty log message ***
 *
 * Revision 1.1  2006/11/14 17:06:10  oeuillot
 * *** empty log message ***
 *
 */
package com.vedana.camelia.generator.components;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.util.HashMap;
import java.util.Map;

import nu.xom.Element;

import com.vedana.camelia.generator.components_1_1.CameliaGenerator;
import com.vedana.camelia.generator.components_1_1.CameliaGenerator.RenderKit;
import com.vedana.camelia.generator.components_1_1.Capability1_1;

/**
 * @author Olivier Oeuillot
 * @version $Revision: 1.7 $
 */
public class CapabilitiesRepository {

    private ClassLoader classLoader;

    private Map<String, Capability1_1> capabilities = new HashMap<String, Capability1_1>();

    private IFactory factory;

    public static final String CAPABILITY_PREFIX = CameliaGenerator.CAMELIA_CORE_PACKAGE
            + ".component.capability.";

    private static final String FAMILLY_PREFIX = CameliaGenerator.CAMELIA_CORE_PACKAGE
            + ".component.familly.";

    public static final String INTERNAL_CAPABILITY_PREFIX = CameliaGenerator.CAMELIA_CORE_PACKAGE
            + ".internal.capability.";

    /**
     * @param classLoader
     */
    public CapabilitiesRepository(ClassLoader classLoader, IFactory factory) {
        this.classLoader = classLoader;
        this.factory = factory;
    }

    public Capability1_1 getCapability(String id) {
        return capabilities.get(id);
    }

    public Capability1_1 getCapability(String id, Element capabilityXml,
            RenderKit renderKit) throws IntrospectionException {
        Capability1_1 capability = capabilities.get(id);
        if (capability != null) {
            // return capability;
        }

        Class< ? > cl;

        try {
            cl = classLoader.loadClass(id);

        } catch (ClassNotFoundException ex2) {
            try {
                cl = classLoader.loadClass(CAPABILITY_PREFIX + id);

            } catch (ClassNotFoundException e) {
                try {
                    cl = classLoader.loadClass(FAMILLY_PREFIX + id);

                } catch (ClassNotFoundException e2) {
                    try {
                        cl = classLoader.loadClass(INTERNAL_CAPABILITY_PREFIX
                                + id);

                    } catch (ClassNotFoundException e3) {
                        try {
                            cl = classLoader
                                    .loadClass(renderKit.componentPackage
                                            + ".capability." + id);

                        } catch (ClassNotFoundException e4) {
                            e.printStackTrace();

                            return null;
                        }
                    }
                }
            }
        }

        boolean clearCachedValue = "true".equalsIgnoreCase(capabilityXml
                .getAttributeValue("clearCachedValue"));
        boolean forceComponent = "true".equalsIgnoreCase(capabilityXml
                .getAttributeValue("forceComponent"));
        boolean required = "true".equalsIgnoreCase(capabilityXml
                .getAttributeValue("required"));

        boolean onlyValueBinding = "true".equalsIgnoreCase(capabilityXml
                .getAttributeValue("onlyValueBinding"));

        String defaultValue = capabilityXml.getAttributeValue("default");

        BeanInfo beanInfo = Introspector.getBeanInfo(cl);

        capability = factory.newCapability(id, beanInfo, clearCachedValue,
                forceComponent, required, onlyValueBinding, defaultValue);
        capabilities.put(id, capability);

        return capability;
    }

    public Class< ? > loadClass(String interfaceName)
            throws ClassNotFoundException {
        return classLoader.loadClass(interfaceName);
    }

    public IFactory getFactory() {
        return factory;
    }
}
