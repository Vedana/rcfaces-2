/*
 * $Id: Constants.java,v 1.2 2013/07/03 12:25:07 jbmeslin Exp $
 */
package org.rcfaces.core.internal.content;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.internal.resource.ClassLoaderResourceLoaderFactory;
import org.rcfaces.core.internal.resource.IResourceLoaderFactory;
import org.rcfaces.core.internal.resource.IncludeResourceLoaderFactory;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:07 $
 */
public class Constants {
    

    private static final Log LOG = LogFactory.getLog(Constants.class);

    private static final boolean USE_INCLUDE_IMAGE_LOADER_FACTORY = true;

    private static final String CONSTANT_PREFIX;
    static {
        String name = Constants.class.getPackage().getName();
        if (name.endsWith(".internal")) {
            name = name.substring(0, name.lastIndexOf('.'));
        }

        CONSTANT_PREFIX = name;
    }

    private static final IResourceLoaderFactory DESIGNER_IMAGE_LOADER_FACTORY = new ClassLoaderResourceLoaderFactory();

    private static final IResourceLoaderFactory IMAGE_LOADER_FACTORY;

    static {

        if (USE_INCLUDE_IMAGE_LOADER_FACTORY) {
            IMAGE_LOADER_FACTORY = new IncludeResourceLoaderFactory();

        } else {
            IMAGE_LOADER_FACTORY = DESIGNER_IMAGE_LOADER_FACTORY;
        }

        LOG.info("USE_INCLUDE_IMAGE_LOADER_FACTORY="
                + USE_INCLUDE_IMAGE_LOADER_FACTORY + " ("
                + IMAGE_LOADER_FACTORY.getName() + ")");
    }

    public static final String getPackagePrefix() {
        return CONSTANT_PREFIX;
    }

    public static final IResourceLoaderFactory getImageLoaderFactory() {
        return IMAGE_LOADER_FACTORY;
    }

    public static final IResourceLoaderFactory getDesignerImageLoaderFactory() {
        return DESIGNER_IMAGE_LOADER_FACTORY;
    }
}
