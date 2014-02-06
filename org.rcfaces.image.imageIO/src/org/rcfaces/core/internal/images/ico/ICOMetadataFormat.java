/*
 * $Id: ICOMetadataFormat.java,v 1.2 2013/11/14 10:55:22 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.internal.images.ico;

import javax.imageio.ImageTypeSpecifier;
import javax.imageio.metadata.IIOMetadataFormat;
import javax.imageio.metadata.IIOMetadataFormatImpl;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/11/14 10:55:22 $
 */
public class ICOMetadataFormat extends IIOMetadataFormatImpl {
    private static final String REVISION = "$Revision: 1.2 $";

    private static IIOMetadataFormat instance = null;

    private ICOMetadataFormat() {
        super(ICOMetadata.nativeMetadataFormatName, CHILD_POLICY_SOME);

        addElement("ImageDescriptor", ICOMetadata.nativeMetadataFormatName,
                CHILD_POLICY_EMPTY);

        addAttribute("ImageDescriptor", "Width", DATATYPE_INTEGER, true, null,
                "0", "65535", true, true);
        addAttribute("ImageDescriptor", "Height", DATATYPE_INTEGER, true, null,
                "1", "65535", true, true);
    }

    public boolean canNodeAppear(String elementName,
            ImageTypeSpecifier imageType) {
        return true;
    }

    public static synchronized IIOMetadataFormat getInstance() {
        if (instance != null) {
            return instance;
        }

        instance = new ICOMetadataFormat();

        return instance;
    }
}
