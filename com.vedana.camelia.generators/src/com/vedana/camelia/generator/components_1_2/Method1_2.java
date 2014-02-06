/*
 * $Id: Method1_2.java,v 1.3 2011/10/12 15:54:00 oeuillot Exp $
 */
package com.vedana.camelia.generator.components_1_2;

import nu.xom.Element;

import com.vedana.camelia.generator.components_1_1.Method1_1;

public class Method1_2 extends Method1_1 {

    public Method1_2(Element xml) {
        super(xml);
    }

    @Override
    protected String convertType(String type) {
        return CameliaGenerator1_2.convertType(type);
    }

    @Override
    protected String transformCode(String code) {
        return CameliaGenerator1_2.transformCode(code);
    }

}
