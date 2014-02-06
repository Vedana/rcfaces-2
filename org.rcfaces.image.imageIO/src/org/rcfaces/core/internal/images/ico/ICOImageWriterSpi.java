/*
 * $Id: ICOImageWriterSpi.java,v 1.2 2013/11/14 10:55:22 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.internal.images.ico;

import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.SampleModel;
import java.util.Locale;

import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriter;
import javax.imageio.spi.ImageWriterSpi;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/11/14 10:55:22 $
 */
public class ICOImageWriterSpi extends ImageWriterSpi {
    private static final String REVISION = "$Revision: 1.2 $";

    private static final String vendorName = "RcFaces - Olivier Oeuillot";

    private static final String version = "1.0";

    private static final String[] formatNames = { "ico", "ICO" };

    private static final String[] entensions = { "ico", "ICO" };

    private static final String[] mimeTypes = { "image/x-icon",
            "image/vnd.microsoft.icon", "image/ico", "image/icon",
            "application/ico" };

    private static final String writerClassName = "org.rcfaces.core.internal.images.ico.ICOImageWriter";

    public ICOImageWriterSpi() {
        super(vendorName, version, formatNames, entensions, mimeTypes,
                "org.rcfaces.core.internal.images.ico.ICOImageWriter",
                STANDARD_OUTPUT_TYPE, null, true, null, null, null, null, true,
                ICOMetadata.nativeMetadataFormatName,
                "org.rcfaces.core.internal.images.ico.ICOMetadataFormat", null,
                null);
    }

    public String getDescription(Locale locale) {
        return "ICO Image Writer";
    }

    public boolean canEncodeImage(ImageTypeSpecifier type) {
        SampleModel sm = type.getSampleModel();
        if (!(sm instanceof MultiPixelPackedSampleModel)) {
            return false;
        }

        if (sm.getSampleSize(0) != 8) {
            return false;
        }

        return true;
    }

    public ImageWriter createWriterInstance(Object extension) {
        return new ICOImageWriter(this);
    }
}
