/*
 * $Id: JsMetaProperties.java,v 1.1 2009/01/15 15:16:52 oeuillot Exp $
 */
package com.vedana.camelia.generator.js.parser;

import java.util.HashMap;
import java.util.Map;

public class JsMetaProperties implements IJsMetaProperties {

    private final Map<String, Object> metaProperties = new HashMap<String, Object>();

    public Object getMetaProperty(String name) {
        return metaProperties.get(name);
    }

    public void putMetaProperty(String name, Object value) {
        metaProperties.put(name, value);
    }

}
