/*
 * $Id: FacesComponent1_2.java,v 1.3 2009/01/15 15:16:52 oeuillot Exp $
 */
package com.vedana.camelia.generator.components_1_2;

import java.util.Map;
import java.util.Set;

import nu.xom.Element;

import com.vedana.camelia.generator.components.IFactory;
import com.vedana.camelia.generator.components_1_1.FacesComponent;
import com.vedana.camelia.generator.components_1_1.CameliaGenerator.RenderKit;

public class FacesComponent1_2 extends FacesComponent {

    public FacesComponent1_2(IFactory factory, Element xml,
            Map<String, RenderKit> renderKits) {
        super(factory, xml, renderKits);
    }

    @Override
    protected void removeImports(Set<String> imports) {
        imports.remove("javax.faces.el.ValueExpression");
    }

    @Override
    protected String getCameliaBaseComponentTemplate() {
        return "jsf_1_2/CameliaBaseComponent.template";
    }
}
