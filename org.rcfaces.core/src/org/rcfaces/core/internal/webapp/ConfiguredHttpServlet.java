/*
 * $Id: ConfiguredHttpServlet.java,v 1.3 2013/11/13 12:53:25 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.internal.webapp;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.internal.Constants;
import org.rcfaces.core.internal.repository.LocaleCriteria;
import org.rcfaces.core.internal.util.Delay;
import org.rcfaces.core.internal.util.ServletTools;

/**
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:25 $
 */
public class ConfiguredHttpServlet extends ExtendedHttpServlet {

    private static final long serialVersionUID = 8336388990583712944L;

    private static final Log LOG = LogFactory
            .getLog(ConfiguredHttpServlet.class);

    public static final String USE_GZIP_PARAMETER = Constants
            .getPackagePrefix() + ".GZIP_SUPPORT";

    private static final String EXPIRE_PARAMETER = Constants.getPackagePrefix()
            + ".EXPIRES";

    private static final String VERSIONED_EXPIRE_PARAMETER = Constants
            .getPackagePrefix() + ".VERSIONED_EXPIRES";

    private static final String ETAG_SUPPORT_PARAMETER = Constants
            .getPackagePrefix() + ".ETAG_SUPPORT";

    private static final String HASH_SUPPORT_PARAMETER = Constants
            .getPackagePrefix() + ".HASH_SUPPORT";

    private static final String FILTERED_LOCALES_PARAMETER = Constants
            .getPackagePrefix() + ".FILTERED_LOCALES";

    private static final String DEFAULT_LOCALE_PARAMETER = Constants
            .getPackagePrefix() + ".DEFAULT_LOCALE";

    private static final String LOCALE_SUPPORT_PARAMETER = Constants
            .getPackagePrefix() + ".LOCALE_SUPPORT";

    private static final String NONE_EXPIRATION_KEYWORD = "none";

    private final Map<String, Locale> convertedLocales = new HashMap<String, Locale>(
            256);

    private boolean gZipSupport;

    private boolean etagSupport;

    private boolean hashSupport;

    private ExpirationDate expirationDate;

    private ExpirationDate versionedExpirationDate;

    private Set<Locale> filtredLocales;

    protected boolean localeSupport;

    private Locale defaultLocale;

    @Override
    public void init(ServletConfig config) throws ServletException {

        super.init(config);

        String useGZIP = getParameter(USE_GZIP_PARAMETER);
        if ("true".equalsIgnoreCase(useGZIP)) {
            gZipSupport = true;

            LOG.info("Enable gzip support for sevlet '" + getServletName()
                    + "'");
        } else if ("false".equalsIgnoreCase(useGZIP)) {
            gZipSupport = false;

            LOG.info("Disable gzip support for sevlet '" + getServletName()
                    + "'");
        } else {
            gZipSupport = getDefaultGZipSupport();

            LOG.info("Use default gzip support configuration (value="
                    + this.gZipSupport + ") for sevlet '" + getServletName()
                    + "'");
        }

        String etagSupport = getParameter(ETAG_SUPPORT_PARAMETER);
        if ("true".equalsIgnoreCase(etagSupport)) {
            this.etagSupport = true;

            LOG.info("Enable etag support for sevlet '" + getServletName()
                    + "'");

        } else if ("false".equalsIgnoreCase(etagSupport)) {
            this.etagSupport = false;

            LOG.info("Disable etag support for sevlet '" + getServletName()
                    + "'");

        } else {
            this.etagSupport = getDefaultETagSupport();

            LOG.info("Use default ETag support configuration (value="
                    + this.etagSupport + ") for sevlet '" + getServletName()
                    + "'");
        }

        String hashSupport = getParameter(HASH_SUPPORT_PARAMETER);
        if ("true".equalsIgnoreCase(hashSupport)) {
            this.hashSupport = true;

            LOG.info("Enable hash support for sevlet '" + getServletName()
                    + "'");

        } else if ("false".equalsIgnoreCase(hashSupport)) {
            this.hashSupport = false;

            LOG.info("Disable hash support for sevlet '" + getServletName()
                    + "'");

        } else {
            this.hashSupport = getDefaultHashSupport();

            LOG.info("Use default Hash support configuration (value="
                    + this.hashSupport + ") for sevlet '" + getServletName()
                    + "'");
        }

        String expires = getParameter(EXPIRE_PARAMETER);
        if (expires != null) {
            expirationDate = ExpirationDate.parse(getServletName(),
                    EXPIRE_PARAMETER, expires);
        }

        if (expirationDate == null
                && NONE_EXPIRATION_KEYWORD.equalsIgnoreCase(expires) == false) {
            expirationDate = ExpirationDate
                    .fromDelay(getDefaultExpirationDelay());
        }

        if (LOG.isInfoEnabled() && expirationDate != null) {
            if (expirationDate.getExpiresDate() >= 0) {
                LOG.info("Expire date setted to "
                        + expirationDate.getExpiresDate() + "  for sevlet '"
                        + getServletName() + "'.");
            }

            if (expirationDate.getExpiresDelay() >= 0) {
                LOG.info("Expire delay setted to "
                        + Delay.format(expirationDate.getExpiresDelay())
                        + " for sevlet '" + getServletName() + "'.");

            }
        }

        String versionedExpires = getParameter(VERSIONED_EXPIRE_PARAMETER);
        if (versionedExpires != null) {
            versionedExpirationDate = ExpirationDate.parse(getServletName(),
                    VERSIONED_EXPIRE_PARAMETER, versionedExpires);
        }

        if (versionedExpirationDate == null
                && NONE_EXPIRATION_KEYWORD.equalsIgnoreCase(versionedExpires) == false) {
            versionedExpirationDate = ExpirationDate
                    .fromDelay(getDefaultVersionedExpirationDelay());
        }

        if (LOG.isInfoEnabled() && versionedExpirationDate != null) {
            if (versionedExpirationDate.getExpiresDate() >= 0) {
                LOG.info("Versioned resources expire date detected: "
                        + versionedExpirationDate.getExpiresDate()
                        + "  for sevlet '" + getServletName() + "'");
            }

            if (versionedExpirationDate.getExpiresDelay() >= 0) {
                LOG.info("Versioned resources expire delay setted to "
                        + Delay.format(versionedExpirationDate
                                .getExpiresDelay()) + " for sevlet '"
                        + getServletName() + "'");
            }
        }

        if (hasLocaleSupport()) {
            String localeSupportProperty = getParameter(LOCALE_SUPPORT_PARAMETER);
            if ("false".equalsIgnoreCase(localeSupportProperty)) {
                localeSupport = false;

                LOG.info("LOCALE_SUPPORT is disabled for servlet '"
                        + getServletName() + "'.");
            } else if ("true".equalsIgnoreCase(localeSupportProperty)) {
                localeSupport = true;

                LOG.info("LOCALE_SUPPORT is enabled for servlet '"
                        + getServletName() + "'.");
            } else {
                localeSupport = getDefaultLocaleSupport();

                LOG.info("Use default value (" + localeSupport
                        + ") for LOCALE_SUPPORT for servlet '"
                        + getServletName() + "'.");
            }

            if (localeSupport) {
                String acceptedLocaleNames = getParameter(FILTERED_LOCALES_PARAMETER);
                if (acceptedLocaleNames != null
                        && acceptedLocaleNames.trim().length() > 0) {

                    StringTokenizer st = new StringTokenizer(
                            acceptedLocaleNames, ", ");
                    filtredLocales = new HashSet<Locale>(st.countTokens());

                    for (; st.hasMoreTokens();) {
                        String localeName = st.nextToken();

                        Locale locale = convertLocaleName(localeName, false);
                        if (locale == null) {
                            LOG.error("Rejected locale '" + localeName + "'.");
                            continue;
                        }

                        filtredLocales.add(locale);
                    }

                    LOG.info("Accepted locale: " + filtredLocales
                            + " for servlet '" + getServletName() + "'.");
                }

                String localeName = getParameter(DEFAULT_LOCALE_PARAMETER);
                if (localeName != null) {
                    defaultLocale = convertLocaleName(localeName, true);

                    if (defaultLocale != null) {
                        LOG.info("DEFAULT_LOCALE specify default locale to '"
                                + defaultLocale + "'.");
                    } else {
                        LOG.info("DEFAULT_LOCALE value '" + localeName
                                + "' is not valid !");
                    }
                }
            }
        }
    }

    protected boolean hasLocaleSupport() {
        return true;
    }

    private long getDefaultVersionedExpirationDelay() {
        return Constants.DEFAULT_VERSIONED_EXPIRATION_DELAY;
    }

    protected boolean getDefaultLocaleSupport() {
        return Constants.CLIENT_LOCALE_SUPPORT_DEFAULT_VALUE;
    }

    protected long getDefaultExpirationDelay() {
        return Constants.DEFAULT_EXPIRATION_DELAY;
    }

    protected boolean getDefaultHashSupport() {
        return Constants.HASH_SUPPORT;
    }

    protected boolean getDefaultETagSupport() {
        return Constants.ETAG_SUPPORT;
    }

    protected boolean getDefaultGZipSupport() {
        return Constants.GZIP_SUPPORT_DEFAULT_VALUE;
    }

    protected ExpirationDate getDefaultExpirationDate(boolean versioned) {
        if (versioned && versionedExpirationDate != null) {
            return versionedExpirationDate;
        }

        return expirationDate;
    }

    protected boolean hasEtagSupport() {
        return etagSupport;
    }

    protected boolean hasHashSupport() {
        return hashSupport;
    }

    protected final boolean hasGZipSupport() {
        return gZipSupport;
    }

    protected final Locale getDefaultLocale(HttpServletRequest request,
            HttpServletResponse response) {
        synchronized (this) {
            if (defaultLocale == null) {
                defaultLocale = ServletTools.getDefaultLocale(
                        getServletContext(), request, response);
            }
        }

        return defaultLocale;
    }

    protected final Locale convertLocaleName(String localeName, boolean accept) {
        if (accept && filtredLocales != null) {
            Locale locale = LocaleCriteria.convertLocaleName(localeName,
                    convertedLocales, filtredLocales);

            return locale;
        }

        Locale locale = LocaleCriteria.convertLocaleName(localeName);

        return locale;
    }
}
