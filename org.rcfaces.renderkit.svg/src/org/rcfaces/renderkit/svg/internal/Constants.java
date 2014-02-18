/*
 * $Id: Constants.java,v 1.2 2013/11/13 15:52:40 jbmeslin Exp $
 * 
 */
package org.rcfaces.renderkit.svg.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/11/13 15:52:40 $
 */
public class Constants {
    private static final String REVISION = "$Revision: 1.2 $";

    private static final Log LOG = LogFactory.getLog(Constants.class);

    private static final String CONSTANT_PREFIX;
    static {
        String name = Constants.class.getPackage().getName();
        if (name.endsWith(".internal")) {
            name = name.substring(0, name.lastIndexOf('.'));
        }

        CONSTANT_PREFIX = name;
    }

    public static final String getPackagePrefix() {
        return CONSTANT_PREFIX;
    }

    private static final String BUILDER_VERSION = null;

    private static final String RCFACES_SVG_VERSION_SYSTEM_PARAMETER = "rcfaces.svg.version";

    private static final String version;

    static {
        if (BUILDER_VERSION != null) {
            version = BUILDER_VERSION;

        } else {
            version = org.rcfaces.core.internal.Constants.searchVersion(
                    Constants.class, RCFACES_SVG_VERSION_SYSTEM_PARAMETER,
                    "RCFaces HTML");
        }

        LOG.info("RCFaces SVG version='" + version + "'");
    }

    public static final String getVersion() {
        return version;
    }
}
