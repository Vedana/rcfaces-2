/*
 * $Id: Factory1_2.java,v 1.2 2012/12/07 13:45:23 oeuillot Exp $
 */
package com.vedana.camelia.generator.components_1_2;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.util.Map;

import nu.xom.Element;

import com.vedana.camelia.generator.components.CapabilitiesRepository;
import com.vedana.camelia.generator.components.IFactory;
import com.vedana.camelia.generator.components.Identifier;
import com.vedana.camelia.generator.components_1_1.Action1_1;
import com.vedana.camelia.generator.components_1_1.Attribute1_1;
import com.vedana.camelia.generator.components_1_1.CameliaGenerator.RenderKit;
import com.vedana.camelia.generator.components_1_1.Capability1_1;
import com.vedana.camelia.generator.components_1_1.Component1_1;
import com.vedana.camelia.generator.components_1_1.ComponentDoc;
import com.vedana.camelia.generator.components_1_1.FacesComponent;
import com.vedana.camelia.generator.components_1_1.FacesField;
import com.vedana.camelia.generator.components_1_1.FacesTagAttribute;
import com.vedana.camelia.generator.components_1_1.Method1_1;
import com.vedana.camelia.generator.components_1_1.Tag;
import com.vedana.camelia.generator.components_1_1.TagLibDoc;

public class Factory1_2 implements IFactory {

    public Capability1_1 newCapability(String id, BeanInfo beanInfo,
            boolean clearCachedValue, boolean forceComponent, boolean required,
            boolean onlyValueBinding, String defaultValue) throws IntrospectionException {
        return new Capability1_2(id, beanInfo, clearCachedValue,
                forceComponent, required, onlyValueBinding, defaultValue);
    }

    public Action1_1 newAction(String id, String type, boolean defaultAction) {
        return new Action1_2(id, type, defaultAction);
    }

    public Attribute1_1 newAttribute(Element attributeXml) {
        return new Attribute1_2(attributeXml);
    }

    public Component1_1 newComponent(CapabilitiesRepository cr,
            Element componentXml, Map<String, RenderKit> renderKits,
            boolean bootStrap, Map<Identifier, Component1_1> components,
            Map<Identifier, FacesComponent> facesComponents)
            throws IntrospectionException {
        return new Component1_2(cr, componentXml, renderKits, bootStrap,
                components, facesComponents);
    }

    public ComponentDoc newComponentDoc(Element tagXml) {
        return new ComponentDoc(tagXml);
    }

    public FacesComponent newFacesComponent(Element componentXml,
            Map<String, RenderKit> renderKits) {
        return new FacesComponent1_2(this, componentXml, renderKits);
    }

    public FacesField newFacesField(int modifiers, String attId, String type,
            String value) {
        return new FacesField(modifiers, attId, type, value);
    }

    public FacesTagAttribute newFacesTagAttribute(String attId, String type,
            boolean required, boolean generate) {
        return new FacesTagAttribute1_2(attId, type, required, generate);
    }

    public Method1_1 newMethod(Element methodXml) {
        return new Method1_2(methodXml);
    }

    public Tag newTag(Element tagXml, Map<String, RenderKit> renderKits) {
        return new Tag1_2(tagXml, renderKits);
    }

    public TagLibDoc newTagLibDoc(Element tagXml) {
        return new TagLibDoc(tagXml);
    }
}