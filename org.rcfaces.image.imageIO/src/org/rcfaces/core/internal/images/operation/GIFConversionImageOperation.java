/*
 * $Id: GIFConversionImageOperation.java,v 1.2 2013/11/14 10:55:22 jbmeslin Exp $
 */
package org.rcfaces.core.internal.images.operation;

import java.util.Map;

import org.rcfaces.core.image.AbstractConversionImageOperation;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/11/14 10:55:22 $
 */
public class GIFConversionImageOperation extends
        AbstractConversionImageOperation {
    private static final String REVISION = "$Revision: 1.2 $";

    public static final String MIME_TYPE = "image/gif";

    public static final String SUFFIX = "gif";

    public GIFConversionImageOperation() {
        super(MIME_TYPE, SUFFIX);
    }

    public static void fillSuffixByMimeType(Map suffixByMimeType) {
        suffixByMimeType.put(MIME_TYPE, SUFFIX);
    }
}
