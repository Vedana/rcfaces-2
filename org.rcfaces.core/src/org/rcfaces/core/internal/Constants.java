/*
 * $Id: Constants.java,v 1.4 2013/11/13 12:53:26 jbmeslin Exp $
 */
package org.rcfaces.core.internal;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.component.capability.IAsyncRenderModeCapability;
import org.rcfaces.core.internal.component.IFactory;
import org.rcfaces.core.internal.resource.IResourceLoaderFactory;
import org.rcfaces.core.internal.util.Delay;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.4 $ $Date: 2013/11/13 12:53:26 $
 */
@SuppressWarnings("unused")
public class Constants {
    private static final Log LOG = LogFactory.getLog(Constants.class);

    private static final String CONSTANT_PREFIX;
    static {
        Package _package = Constants.class.getPackage();

        String name = _package.getName();
        if (name.endsWith(".internal")) {
            name = name.substring(0, name.lastIndexOf('.'));
        }

        CONSTANT_PREFIX = name;
    }

    private static final String BUILDER_VERSION = null;

    public static final boolean READ_ONLY_COLLECTION_LOCK_ENABLED = true;

    public static final boolean TEMPLATE_ENGINE_SUPPORT = true;

    public static final boolean COMPONENT_DEFAULT_PROPERTIES_SUPPORT = true;

    public static final boolean RESOURCE_CONTENT_VERSION_SUPPORT = true;

    public static final boolean RESOURCE_CONTENT_PROXY_SUPPORT = true;

    public static final boolean CACHED_COMPONENT_ITERATOR = false;

    public static final boolean STATED_COMPONENT_CHILDREN_LIST = false;

    public static final boolean ENCODE_URI = false;

    public static final boolean HASH_SUPPORT = true;

    public static final boolean ETAG_SUPPORT = true;

    public static final boolean GZIP_SUPPORT_DEFAULT_VALUE = true;

    public static final boolean MYFACES_INPUT_BROADCAST_BUG = true;

    /**
     * Collection can be converted to DataModel (JSF spec extension)
     */
    public static final boolean COLLECTION_DATAMODEL_SUPPORT = true;

    /**
     * Adaptable object can be converted to DataModel (JSF spec extension)
     */
    public static final boolean ADAPTABLE_DATAMODEL_SUPPORT = true;

    public static final long DEFAULT_EXPIRATION_DELAY = Delay.WEEK;

    public static final long DEFAULT_VERSIONED_EXPIRATION_DELAY = Delay.YEAR;

    public static final int VERSIONED_URI_HASHCODE_MAX_SIZE = 64;

    private static final String RCFACES_VERSION_SYSTEM_PARAMETER = "rcfaces.core.version";

    public static final String SAVE_STATE_FIELD_MARKER_SYSTEM_PARAMETER = "rcfaces.core.stateFieldMarker";

    public static final String SAVE_STATE_FIELD_MARKER_PARAMETER = getPackagePrefix()
            + ".SAVE_STATE_FIELD_MARKER";

    public static final boolean CLIENT_LOCALE_SUPPORT_DEFAULT_VALUE = true;

    public static final String[] RESOURCE_VERSION_DIGEST_ALGORITHMS = {
            "SHA-512", "SHA-256", "SHA-1" };

    public static final String[] ETAG_DIGEST_ALGORITHMS = { "SHA-512",
            "SHA-256", "SHA-1" };

    public static final String[] SERIALISATION_HASH_ALGORITHMS = { "SHA-512",
            "SHA-256", "SHA-1" };

    public static final String[] HASH_DIGEST_ALGORITHMS = { "MD5" };

    public static final boolean BASIC_CONTENT_WEAK_CACHE_ENABLED = true;

    public static final int LIMITED_MAP_WEAK_CACHE_SIZE = 256;

    public static final int BASIC_CONTENT_CACHE_SIZE = 256;

    public static final boolean BASIC_CONTENT_CACHE_SOFT_REFERENCES = true;

    private static final String version;
    static {
        if (BUILDER_VERSION != null) {
            version = BUILDER_VERSION;

        } else {
            version = searchVersion(Constants.class,
                    RCFACES_VERSION_SYSTEM_PARAMETER, "RCFaces Core");
        }

        LOG.info("RCFaces CORE version '" + version + "'");
    }


    public static final IResourceLoaderFactory IMAGE_LOADER_FACTORY = null;

    public static final boolean CACHED_LOCALE_FORMATS = true;

    /**
     * <code>false</code> Optimize for CPU <code>true</code>Optimize for Memory
     */
    public static final boolean COMPACTED_PROPERTY_NAME = false;

    public static final boolean LOCKED_CLIENT_ATTRIBUTES_DEFAULT_VALUE = false;

    private static final String FACELET_CLASSNAME = "com.sun.facelets.Facelet";

    public static final int DEFAULT_ASYNC_MODE = IAsyncRenderModeCapability.NONE_ASYNC_RENDER_MODE;

    public static final int ENABLE_ASYNC_MODE_VALUE = IAsyncRenderModeCapability.BUFFER_ASYNC_RENDER_MODE;

    public static final boolean FACELETS_SUPPORT = true;

    public static final boolean ADAPT_SELECT_ITEMS = true;

    public static final Locale REPOSITORY_DEFAULT_LOCALE = Locale.ENGLISH;

    public static final boolean VERIFY_VARIABLE_SYNTAX = false;

    private static final boolean VERIFY_IMPLEMENTATION = true;

    public static final int MINIMUM_GZIP_BUFFER_SIZE = 128;

    public static final int CONTENT_STORAGE_CACHE_SIZE = 512;

    public static final boolean CONTENT_STORAGE_CACHE_SOFT_REFERENCES = true;

    public static final boolean LOCALE_CRITERIA_LANGUAGE_ONLY = true;

    static {
        LOG.info("READ_ONLY_COLLECTION_LOCK_ENABLED="
                + READ_ONLY_COLLECTION_LOCK_ENABLED);

        LOG.info("LOCKED_CLIENT_ATTRIBUTES_DEFAULT_VALUE="
                + LOCKED_CLIENT_ATTRIBUTES_DEFAULT_VALUE);

        LOG.info("TEMPLATE_ENGINE_SUPPORT=" + TEMPLATE_ENGINE_SUPPORT);

        LOG.info("RESOURCE_CONTENT_VERSION_SUPPORT="
                + RESOURCE_CONTENT_VERSION_SUPPORT);

        LOG.info("STATED_COMPONENT_CHILDREN_LIST="
                + STATED_COMPONENT_CHILDREN_LIST);

        LOG.info("CACHED_COMPONENT_ITERATOR=" + CACHED_COMPONENT_ITERATOR);

        LOG.info("CACHED_LOCALE_FORMATS=" + CACHED_LOCALE_FORMATS);

        LOG.info("COMPACTED_PROPERTY_NAME=" + COMPACTED_PROPERTY_NAME);

        LOG.info("ETAG_SUPPORT=" + ETAG_SUPPORT + " (algorithms: "
                + Arrays.asList(ETAG_DIGEST_ALGORITHMS) + ")");

        LOG.info("HASH_SUPPORT=" + HASH_SUPPORT + " (algorithms: "
                + Arrays.asList(HASH_DIGEST_ALGORITHMS) + ")");

        LOG.info("SERIALISATION_HASH_ALGORITHMS="
                + Arrays.asList(SERIALISATION_HASH_ALGORITHMS));

        LOG.info("GZIP_SUPPORT_DEFAULT_VALUE=" + GZIP_SUPPORT_DEFAULT_VALUE);

        LOG.info("DEFAULT_EXPIRATION_DELAY=" + DEFAULT_EXPIRATION_DELAY + " ("
                + Delay.format(DEFAULT_EXPIRATION_DELAY) + ")");

        LOG.info("DEFAULT_VERSIONED_EXPIRATION_DELAY="
                + DEFAULT_VERSIONED_EXPIRATION_DELAY + " ("
                + Delay.format(DEFAULT_VERSIONED_EXPIRATION_DELAY) + ")");

        LOG.info("CLIENT_LOCALE_SUPPORT_DEFAULT_VALUE="
                + CLIENT_LOCALE_SUPPORT_DEFAULT_VALUE);

        LOG.info("VERSIONED_URI_HASHCODE_MAX_SIZE="
                + VERSIONED_URI_HASHCODE_MAX_SIZE);

        LOG.info("REPOSITORY_DEFAULT_LOCALE=" + REPOSITORY_DEFAULT_LOCALE);

        LOG.info("CONTENT_STORAGE_CACHE_SIZE=" + CONTENT_STORAGE_CACHE_SIZE
                + "  (soft references=" + CONTENT_STORAGE_CACHE_SOFT_REFERENCES
                + ")");

        LOG.info("FACELETS_SUPPORT=" + FACELETS_SUPPORT);

        switch (DEFAULT_ASYNC_MODE) {
        case IAsyncRenderModeCapability.TREE_ASYNC_RENDER_MODE:
            LOG.info("DEFAULT_ASYNC_MODE=tree");
            break;

        case IAsyncRenderModeCapability.BUFFER_ASYNC_RENDER_MODE:
            LOG.info("DEFAULT_ASYNC_MODE=buffer");
            break;

        default:
            LOG.info("DEFAULT_ASYNC_MODE=none");
            break;
        }

        switch (ENABLE_ASYNC_MODE_VALUE) {
        case IAsyncRenderModeCapability.TREE_ASYNC_RENDER_MODE:
            LOG.info("ENABLE_ASYNC_MODE_VALUE=tree");
            break;

        case IAsyncRenderModeCapability.BUFFER_ASYNC_RENDER_MODE:
            LOG.info("ENABLE_ASYNC_MODE_VALUE=buffer");
            break;

        default:
            LOG.info("ENABLE_ASYNC_MODE_VALUE=unknown");
            break;
        }

    }

    public static final String getPackagePrefix() {
        return CONSTANT_PREFIX;
    }


    public static String getVersion() {
        return version;
    }

    public static final String searchVersion(Class< ? > clazz,
            String systemParameter, String versionName) {

        try {
            String version = System.getProperty(systemParameter);
            if (version != null) {
                if ("now".equalsIgnoreCase(version)) {
                    version = "0." + String.valueOf(System.currentTimeMillis());

                    try {
                        System.setProperty(systemParameter, version);

                    } catch (Throwable th2) {
                        LOG.debug(th2);
                    }

                    LOG.info(versionName
                            + " version: DEVELOPMENT MODE (Use fake version: "
                            + version + ")");

                } else {
                    LOG.info(versionName + " version setted by property: "
                            + version);
                }

                return version;
            }

        } catch (Throwable th) {
            // Un probleme de sécurité peut-etre !
            LOG.debug(th);
        }

        Package clazzPackage = clazz.getPackage();
        if (clazzPackage != null) {
            try {
                String implementationTitle = clazzPackage
                        .getImplementationTitle();
                if (implementationTitle != null) {
                    verifyImplementationTitle(implementationTitle);
                }

                String version = clazzPackage.getImplementationVersion();
                if (version != null) {
                    LOG.info(versionName + " version: " + version);

                    return version;
                }

            } catch (Throwable th) {
                LOG.error(
                        "Can not get " + versionName
                                + " version by package API ! ("
                                + clazzPackage.getName() + ")", th);
            }
        }

        String version = "0." + String.valueOf(System.currentTimeMillis());
        LOG.error("Version of " + versionName
                + " can not be determined ! (Use the fake version: " + version
                + ")");

        return version;
    }

    private static void verifyImplementationTitle(String implementationTitle) {
        if (Constants.VERIFY_IMPLEMENTATION == false) {
            return;
        }

        LOG.debug("Check for implementation title '" + implementationTitle
                + "' uniqueness.");

        try {
            Enumeration<URL> enumeration = Constants.class.getClassLoader()
                    .getResources("META-INF/MANIFEST.MF");

            Set<URL> resources = new HashSet<URL>(2);

            for (; enumeration.hasMoreElements();) {
                URL url = enumeration.nextElement();

                InputStream inputStream = url.openStream();
                if (inputStream == null) {
                    LOG.debug("No input stream for url '" + url + "'.");
                    continue;
                }

                try {
                    Manifest manifest = new Manifest(inputStream);

                    String jarImplementationTitle = (String) manifest
                            .getMainAttributes().get(
                                    Attributes.Name.IMPLEMENTATION_TITLE);

                    if (implementationTitle.equals(jarImplementationTitle) == false) {
                        continue;
                    }

                    resources.add(url);

                    LOG.debug("Implementation title detected '"
                            + implementationTitle + "' => " + url);

                } catch (IOException ex) {
                    LOG.error("Can not load manifest '" + url + "'", ex);

                } finally {
                    try {
                        inputStream.close();

                    } catch (IOException ex) {
                        LOG.error(ex);
                    }
                }
            }

            LOG.debug("Count of implementation title '" + implementationTitle
                    + "' = " + resources.size());

            if (resources.size() > 1) {
                throw new IllegalStateException(
                        "More than one definition of implementation title '"
                                + implementationTitle + "' !");
            }

        } catch (IOException e) {
            LOG.error("Can not search manifest for implementation title '"
                    + implementationTitle + "'.", e);
        }
    }

    public static final String getBuildId(String version) {
        StringTokenizer st = new StringTokenizer(version, ".");
        if (st.countTokens() < 4) {
            return version;
        }

        st.nextToken();
        st.nextToken();
        st.nextToken();

        return st.nextToken("\n\r\t ");
    }
}
