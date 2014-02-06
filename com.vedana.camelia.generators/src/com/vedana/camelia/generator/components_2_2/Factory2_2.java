/*
 * $Id: Factory2_2.java,v 1.1 2014/02/05 16:03:40 jbmeslin Exp $
 */
package com.vedana.camelia.generator.components_2_2;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.util.Map;

import nu.xom.Element;

import com.vedana.camelia.generator.components.CapabilitiesRepository;
import com.vedana.camelia.generator.components.Identifier;
import com.vedana.camelia.generator.components_1_1.Attribute1_1;
import com.vedana.camelia.generator.components_1_1.CameliaGenerator.RenderKit;
import com.vedana.camelia.generator.components_1_1.Capability1_1;
import com.vedana.camelia.generator.components_1_1.Component1_1;
import com.vedana.camelia.generator.components_1_1.FacesComponent;
import com.vedana.camelia.generator.components_1_1.Method1_1;
import com.vedana.camelia.generator.components_1_2.Factory1_2;

public class Factory2_2 extends Factory1_2 {

    @Override
    public Attribute1_1 newAttribute(Element attributeXml) {
        return new Attribute2_2(attributeXml);
    }

    @Override
    public FacesComponent newFacesComponent(Element componentXml,
            Map<String, RenderKit> renderKits) {
        return new FacesComponent2_2(this, componentXml, renderKits);
    }

    @Override
    public Capability1_1 newCapability(String id, BeanInfo beanInfo,
            boolean clearCachedValue, boolean forceComponent, boolean required,
            boolean onlyValueBinding, String defaultValue) throws IntrospectionException {
        return new Capability2_2(id, beanInfo, clearCachedValue,
                forceComponent, required, onlyValueBinding, defaultValue);
    }

    @Override
    public Component1_1 newComponent(CapabilitiesRepository cr,
            Element componentXml, Map<String, RenderKit> renderKits,
            boolean bootStrap, Map<Identifier, Component1_1> components,
            Map<Identifier, FacesComponent> facesComponents)
            throws IntrospectionException {

        return new Component2_2(cr, componentXml, renderKits, bootStrap,
                components, facesComponents);
    }

    @Override
    public Method1_1 newMethod(Element methodXml) {
        return new Method2_2(methodXml);
    }

}