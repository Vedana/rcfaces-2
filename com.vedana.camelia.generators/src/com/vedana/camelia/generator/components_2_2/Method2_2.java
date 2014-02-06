/*
 * $Id: Method2_2.java,v 1.1 2014/02/05 16:03:40 jbmeslin Exp $
 */
package com.vedana.camelia.generator.components_2_2;

import nu.xom.Element;

import com.vedana.camelia.generator.components_1_2.Method1_2;

public class Method2_2 extends Method1_2 {

    public Method2_2(Element xml) {
        super(xml);
    }

    @Override
    protected String transformCode(String code) {
        return CameliaGenerator2_2.transformCode(code);
    }

}
