/*
 * $Id: SelectedOperation.java,v 1.2 2013/11/14 10:55:22 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.internal.images.operation;

import org.rcfaces.core.image.operation.ISelectedOperation;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/11/14 10:55:22 $
 */
public class SelectedOperation extends ContrastBrightnessOperation implements
        ISelectedOperation {
    private static final String REVISION = "$Revision: 1.2 $";

    public static final String ID = "selected";

    protected float getDefaultOffset() {
        return -0.3f;
    }

    protected float getDefaultScale() {
        return 1.3f;
    }
}
