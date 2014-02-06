/*
 * $Id: IFactory.java,v 1.4 2012/12/07 13:45:23 oeuillot Exp $
 */
package com.vedana.camelia.generator.components;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.util.Map;

import nu.xom.Element;

import com.vedana.camelia.generator.components_1_1.Action1_1;
import com.vedana.camelia.generator.components_1_1.Attribute1_1;
import com.vedana.camelia.generator.components_1_1.Capability1_1;
import com.vedana.camelia.generator.components_1_1.Component1_1;
import com.vedana.camelia.generator.components_1_1.ComponentDoc;
import com.vedana.camelia.generator.components_1_1.FacesComponent;
import com.vedana.camelia.generator.components_1_1.FacesField;
import com.vedana.camelia.generator.components_1_1.FacesTagAttribute;
import com.vedana.camelia.generator.components_1_1.Method1_1;
import com.vedana.camelia.generator.components_1_1.Tag;
import com.vedana.camelia.generator.components_1_1.TagLibDoc;
import com.vedana.camelia.generator.components_1_1.CameliaGenerator.RenderKit;

public interface IFactory {

    Capability1_1 newCapability(String id, BeanInfo beanInfo,
            boolean clearCachedValue, boolean forceComponent, boolean required,
            boolean onlyValueBinding, String defaultValue) throws IntrospectionException;

    Action1_1 newAction(String id, String type, boolean defaultAction);

    Attribute1_1 newAttribute(Element attributeXml);

    Component1_1 newComponent(CapabilitiesRepository cr, Element componentXml,
            Map<String, RenderKit> renderKits, boolean bootStrap,
            Map<Identifier, Component1_1> components,
            Map<Identifier, FacesComponent> facesComponents)
            throws IntrospectionException;

    ComponentDoc newComponentDoc(Element tagXml);

    FacesComponent newFacesComponent(Element componentXml,
            Map<String, RenderKit> renderKits);

    FacesField newFacesField(int modifiers, String attId, String type,
            String value);

    FacesTagAttribute newFacesTagAttribute(String attId, String type,
            boolean required, boolean generate);

    Method1_1 newMethod(Element methodXml);

    Tag newTag(Element tagXml, Map<String, RenderKit> renderKits);

    TagLibDoc newTagLibDoc(Element tagXml);

}
