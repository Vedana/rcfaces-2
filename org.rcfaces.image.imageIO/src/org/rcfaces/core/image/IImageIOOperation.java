/*
 * $Id: IImageIOOperation.java,v 1.2 2013/11/14 10:55:22 jbmeslin Exp $
 */
package org.rcfaces.core.image;

import java.awt.image.BufferedImage;
import java.util.Map;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/11/14 10:55:22 $
 */
public interface IImageIOOperation extends IImageOperation {

    BufferedImage filter(Map filterParameters, BufferedImage source,
            BufferedImage destination);

}
