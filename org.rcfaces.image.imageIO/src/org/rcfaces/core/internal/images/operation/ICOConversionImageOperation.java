/*
 * $Id: ICOConversionImageOperation.java,v 1.2 2013/11/14 10:55:22 jbmeslin Exp $
 */
package org.rcfaces.core.internal.images.operation;

import java.util.Map;

import org.rcfaces.core.image.AbstractConversionImageOperation;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/11/14 10:55:22 $
 */
public class ICOConversionImageOperation extends
        AbstractConversionImageOperation {
    private static final String REVISION = "$Revision: 1.2 $";

    public static final String MIME_TYPES[] = { "image/x-icon",
            "image/vnd.microsoft.icon", "image/ico", "image/icon",
            "application/ico" };

    public static final String SUFFIX = "ico";

    public ICOConversionImageOperation() {
        super(MIME_TYPES[0], SUFFIX);
    }

    public static void fillSuffixByMimeType(Map suffixByMimeType) {
        for (int i = 0; i < MIME_TYPES.length; i++) {
            suffixByMimeType.put(MIME_TYPES[i], SUFFIX);
        }
    }
}
