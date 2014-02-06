/*
 * $Id: FacesComponent2_2.java,v 1.1 2014/02/05 16:03:40 jbmeslin Exp $
 */
package com.vedana.camelia.generator.components_2_2;

import java.util.Map;

import nu.xom.Element;

import com.vedana.camelia.generator.components.IFactory;
import com.vedana.camelia.generator.components_1_1.CameliaGenerator.RenderKit;
import com.vedana.camelia.generator.components_1_2.FacesComponent1_2;

public class FacesComponent2_2 extends FacesComponent1_2 {

    public FacesComponent2_2(IFactory factory, Element xml,
            Map<String, RenderKit> renderKits) {
        super(factory, xml, renderKits);
    }

    @Override
    protected String getCameliaBaseComponentTemplate() {
        return "jsf_2_2/CameliaBaseComponent.template";
    }
}
