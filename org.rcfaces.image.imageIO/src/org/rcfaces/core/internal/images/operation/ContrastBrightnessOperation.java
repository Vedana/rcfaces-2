/*
 * $Id: ContrastBrightnessOperation.java,v 1.2 2013/11/14 10:55:22 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.internal.images.operation;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/11/14 10:55:22 $
 */
public class ContrastBrightnessOperation extends ColorsRescaleOperation {
    private static final String REVISION = "$Revision: 1.2 $";

    protected String getOffsetPropertyName() {
        return "brightness";
    }

    protected String getScalePropertyName() {
        return "contrast";
    }
}
