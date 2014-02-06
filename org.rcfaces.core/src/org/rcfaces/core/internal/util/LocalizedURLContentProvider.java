/*
 * $Id: LocalizedURLContentProvider.java,v 1.1 2013/11/13 12:53:22 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.internal.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.internal.repository.IContentRef;
import org.rcfaces.core.internal.repository.IRepository.IContentProvider;
import org.rcfaces.core.internal.repository.IRepository.ICriteria;
import org.rcfaces.core.internal.repository.LocaleCriteria;
import org.rcfaces.core.internal.repository.URLContentRef;
import org.rcfaces.core.internal.webapp.URIParameters;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.1 $ $Date: 2013/11/13 12:53:22 $
 */
public class LocalizedURLContentProvider extends URLContentProvider {

    private static final Log LOG = LogFactory
            .getLog(LocalizedURLContentProvider.class);

    public static final IContentProvider SINGLETON = new LocalizedURLContentProvider();

    protected LocalizedURLContentProvider() {
    }

    @Override
    public IContentRef[] searchCriteriaContentReference(
            IContentRef contentReference, ICriteria criteria) {

        URLContentRef urlContentRef = (URLContentRef) contentReference;

        Locale locale = LocaleCriteria.getLocale(criteria);
        if (locale == null) {
            LOG.error("Criteria '" + criteria + "' has no locale !");
            return null;
        }

        String baseURL = urlContentRef.getURL().toString();

        String variant = locale.getVariant();
        String country = locale.getCountry();
        String language = locale.getLanguage();

        if (LOG.isTraceEnabled()) {
            LOG.trace("searchLocalizedContentReference ref='"
                    + contentReference + "' locale='" + criteria
                    + "' => baseURL=" + baseURL);
        }

        try {
            if (variant != null && variant.length() > 0) {
                URIParameters up = URIParameters.parseURI(baseURL);
                up.appendLocale(locale);

                URL l = new URL(up.computeParametredURI());

                ICriteria c = LocaleCriteria.get(locale);
                URLContentRef ucr = new URLContentRef(c, l);
                if (testURL(ucr)) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Localized version '" + criteria
                                + "' found for url '" + l + "' => " + locale);
                    }

                    return new IContentRef[] { ucr };
                }

                locale = new Locale(language, country);
            }

            if (country != null && country.length() > 0) {
                URIParameters up = URIParameters.parseURI(baseURL);
                up.appendLocale(locale);

                URL l = new URL(up.computeParametredURI());
                ICriteria c = LocaleCriteria.get(locale);
                URLContentRef ucr = new URLContentRef(c, l);
                if (testURL(ucr)) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Localized version '" + criteria
                                + "' found for url '" + l + "' => " + ucr);
                    }

                    return new IContentRef[] { ucr };
                }

                locale = new Locale(language);
            }

            if (language != null && language.length() > 0) {
                URIParameters up = URIParameters.parseURI(baseURL);
                up.appendLocale(locale);

                URL l = new URL(up.computeParametredURI());

                ICriteria c = LocaleCriteria.get(locale);
                URLContentRef ucr = new URLContentRef(c, l);
                if (testURL(ucr)) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Localized version '" + criteria
                                + "' found for url '" + l + "' => " + ucr);
                    }

                    return new IContentRef[] { ucr };
                }
            }
        } catch (MalformedURLException ex) {
            LOG.error("Can not search localized url of uri '" + baseURL
                    + "' for criteria '" + criteria + "'.", ex);

            return null;
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("Localized version '" + criteria
                    + "' not found for url '" + baseURL + "'.");
        }

        return null;
    }
}
