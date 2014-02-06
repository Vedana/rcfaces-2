/*
 * $Id: BrightnessOperation.java,v 1.2 2013/11/14 10:55:22 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.internal.images.operation;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/11/14 10:55:22 $
 */
public class BrightnessOperation extends ContrastBrightnessOperation {
    private static final String REVISION = "$Revision: 1.2 $";

    protected String getDefaultPropertyName() {
        return getOffsetPropertyName();
    }
}
