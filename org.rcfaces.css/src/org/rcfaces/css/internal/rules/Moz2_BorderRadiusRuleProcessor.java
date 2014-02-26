/*
 * $Id: Moz2_BorderRadiusRuleProcessor.java,v 1.1.2.1 2012/11/28 10:53:22 oeuillot Exp $
 */
package org.rcfaces.css.internal.rules;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1.2.1 $ $Date: 2012/11/28 10:53:22 $
 */
public class Moz2_BorderRadiusRuleProcessor extends AbstractMapRuleProcessor {

    public Moz2_BorderRadiusRuleProcessor() {
        mapPropertyName.put("border-radius", "-moz-border-radius");

        mapPropertyName.put("border-top-left-radius",
                "-moz-border-radius-topleft");

        mapPropertyName.put("border-top-right-radius",
                "-moz-border-radius-topright");

        mapPropertyName.put("border-bottom-left-radius",
                "-moz-border-radius-bottomleft");

        mapPropertyName.put("border-bottom-right-radius",
                "-moz-border-radius-bottomright");
    }

}
