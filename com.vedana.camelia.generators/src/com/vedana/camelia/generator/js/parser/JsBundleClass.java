/*
 * $Id: JsBundleClass.java,v 1.2 2009/01/15 15:16:52 oeuillot Exp $
 */
package com.vedana.camelia.generator.js.parser;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.2 $ $Date: 2009/01/15 15:16:52 $
 */
public class JsBundleClass extends JsClass {

    private final IJsClass jsClass;

    private final String bundleName;

    public JsBundleClass(IJsClass jsClass, String bundleName) {
        super(jsClass.getName() + "_" + bundleName, false);

        this.jsClass = jsClass;
        this.bundleName = bundleName;
    }

    public String getBundeName() {
        return bundleName;
    }

    @Override
    public void print() {
        System.out.println("Bundle '" + jsClass.getName() + "' locale="
                + bundleName);
    }
}
