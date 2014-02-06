/*
 * $Id: JPEGConversionImageOperation.java,v 1.2 2013/11/14 10:55:22 jbmeslin Exp $
 */
package org.rcfaces.core.internal.images.operation;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.image.AbstractConversionImageOperation;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/11/14 10:55:22 $
 */
public class JPEGConversionImageOperation extends
        AbstractConversionImageOperation {
    private static final String REVISION = "$Revision: 1.2 $";

    private static final Log LOG = LogFactory
            .getLog(JPEGConversionImageOperation.class);

    public static final String MIME_TYPES[] = { "image/jpeg", "image/jpg" };

    public static final String SUFFIX = "jpeg";

    public JPEGConversionImageOperation() {
        super(MIME_TYPES[0], SUFFIX);
    }

    public static void fillSuffixByMimeType(Map suffixByMimeType) {
        for (int i = 0; i < MIME_TYPES.length; i++) {
            suffixByMimeType.put(MIME_TYPES[i], SUFFIX);
        }
    }
}
