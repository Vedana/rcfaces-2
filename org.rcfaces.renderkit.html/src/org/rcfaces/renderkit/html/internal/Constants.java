/*
 * $Id: Constants.java,v 1.3 2013/11/13 12:53:28 jbmeslin Exp $
 * 
 */
package org.rcfaces.renderkit.html.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:28 $
 */
public class Constants {

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

    private static final String RCFACES_HTML_VERSION_SYSTEM_PARAMETER = "rcfaces.html.version";

    public static final boolean INTERACTIVE_RENDER_DEFAULT_VALUE = true;

    public static final boolean INTERACTIVE_RENDER_GZIP_DEFAULT_VALUE = true;

    public static final boolean JAVASCRIPT_APPEND_RCFACES_HEADER = true;

    public static final boolean JAVASCRIPT_APPEND_PROFILER_DATE = true;

    public static final String JAVASCRIPT_GROUP_ALL_DEFAULT_VALUE = "all";

    public static final String JAVASCRIPT_BOOT_SET_DEFAULT_VALUE = "CORE";

    public static final String JAVASCRIPT_DEFAULT_CHARSET = "UTF-8";

    public static final String CSS_DEFAULT_CHARSET = "UTF-8";

    public static final boolean RESOURCES_MODULE_URL_SUPPORT = true;

    public static final boolean VERSIONED_FRAMEWORK_URL_SUPPORT = true;

    public static final boolean VERSIONED_CLIENT_BUNDLE_SUPPORT = true;

    public static final boolean CLIENT_NAMING_SEPARATOR_SUPPORT = true;

    public static final boolean GROUP_NAME_NAMESPACE_SUPPORT = true;

    public static final boolean NORMALIZE_STYLE_MARGINS = true;

    public static final boolean DISABLE_IE_IMAGEBAR_DEFAULT_VALUE = true;

    public static final boolean STAT_RESOURCES_HTTP_RESPONSE = false;

    public static final boolean ACCESSIBILITY_ROLE_SUPPORT = true;

    public static final boolean LAZY_TAG_USES_BROTHER_DEFAULT_VALUE = false;

    public static final boolean VERIFY_SORT_COMMAND = true;

    private static final String version;

    private static final String buildId;

    public static final boolean GENERATE_RCFACES_INIT_TIMER = true;
    
    public static final boolean DISABLE_HEADING_ZONE = false;

    public static final boolean REDUCE_USER_AGENT_TO_BROWSER_TYPE = true;

    static {
        if (BUILDER_VERSION != null) {
            version = BUILDER_VERSION;

        } else {
            version = org.rcfaces.core.internal.Constants.searchVersion(
                    Constants.class, RCFACES_HTML_VERSION_SYSTEM_PARAMETER,
                    "RCFaces HTML");
        }

        buildId = org.rcfaces.core.internal.Constants.getBuildId(version);

        LOG.info("RCFaces HTML version='" + version + "' buildId='" + buildId
                + "'");

        LOG.info("INTERACTIVE_RENDER_DEFAULT_VALUE="
                + INTERACTIVE_RENDER_DEFAULT_VALUE);

        LOG.info("INTERACTIVE_RENDER_GZIP_DEFAULT_VALUE="
                + INTERACTIVE_RENDER_GZIP_DEFAULT_VALUE);

        LOG.info("VERSIONED_FRAMEWORK_URL_SUPPORT="
                + VERSIONED_FRAMEWORK_URL_SUPPORT);

        LOG.info("VERSIONED_CLIENT_BUNDLE_SUPPORT="
                + VERSIONED_CLIENT_BUNDLE_SUPPORT);

        LOG.info("CLIENT_NAMING_SEPARATOR_SUPPORT="
                + CLIENT_NAMING_SEPARATOR_SUPPORT);

        LOG.info("GROUP_NAME_NAMESPACE_SUPPORT=" + GROUP_NAME_NAMESPACE_SUPPORT);

        LOG.info("NORMALIZE_STYLE_MARGINS=" + NORMALIZE_STYLE_MARGINS);

        LOG.debug("DISABLE_IE_IMAGEBAR_DEFAULT_VALUE="
                + DISABLE_IE_IMAGEBAR_DEFAULT_VALUE);

        LOG.debug("STAT_RESOURCES_HTTP_RESPONSE="
                + STAT_RESOURCES_HTTP_RESPONSE);

        LOG.debug("ACCESSIBILITY_ROLE_SUPPORT=" + ACCESSIBILITY_ROLE_SUPPORT);

        LOG.debug("LAZY_TAG_USES_BROTHER_DEFAULT_VALUE="
                + LAZY_TAG_USES_BROTHER_DEFAULT_VALUE);

    }

    public static final String getVersion() {
        return version;
    }

    public static final String getBuildId() {
        return buildId;
    }

}
