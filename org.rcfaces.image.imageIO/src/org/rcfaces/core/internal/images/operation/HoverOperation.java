/*
 * $Id: HoverOperation.java,v 1.2 2013/11/14 10:55:22 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.internal.images.operation;

import org.rcfaces.core.image.operation.IHoverOperation;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/11/14 10:55:22 $
 */
public class HoverOperation extends ContrastBrightnessOperation implements
        IHoverOperation {
    private static final String REVISION = "$Revision: 1.2 $";

    protected float getDefaultOffset() {
        return -0.3f;
    }

    protected float getDefaultScale() {
        return 1.3f;
    }
}
