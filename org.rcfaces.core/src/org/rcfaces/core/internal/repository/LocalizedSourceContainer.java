/*
 * $Id: LocalizedSourceContainer.java,v 1.3 2013/11/13 12:53:25 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.internal.repository;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.internal.webapp.URIParameters;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:25 $
 */
public abstract class LocalizedSourceContainer extends SourceContainer<String> {

    private static final Log LOG = LogFactory
            .getLog(LocalizedSourceContainer.class);

    private final Locale[] testedLocales;

    public LocalizedSourceContainer(ServletConfig config,
            String repositoryType, Set<String> modules, String charSet,
            boolean canUseGzip, boolean canUseETag, boolean canUseHash,
            String externalRepositoriesPropertyName, String repositoryVersion)
            throws ServletException {
        super(config, repositoryType, modules, charSet, canUseGzip, canUseETag,
                canUseHash, externalRepositoriesPropertyName, repositoryVersion);

        this.testedLocales = listTestedLocales(config);
    }

    @Override
    protected URL getURL(String path) {
        for (Locale locale : testedLocales) {
            URIParameters up = URIParameters.parseURI(path);
            if (locale != null) {
                up.appendLocale(locale);
            }

            String p = up.computeParametredURI();

            URL url = super.getURL(p);
            if (url != null) {
                return url;
            }
        }

        return null;
    }

    protected Locale[] listTestedLocales(ServletConfig config) {
        Locale locale = Locale.getDefault();

        List<Locale> l = new ArrayList<Locale>();

        if (locale != null) {
            String language = locale.getLanguage();
            if (language != null && language.length() > 0) {
                String country = locale.getCountry();
                if (country != null && country.length() > 0) {
                    String variant = locale.getVariant();
                    if (variant != null && variant.length() > 0) {
                        l.add(locale);

                        locale = new Locale(locale.getLanguage(),
                                locale.getCountry(), null);
                    }

                    l.add(locale);

                    locale = new Locale(locale.getLanguage(), null, null);
                }

                l.add(locale);
            }

            locale = null;
        }

        l.add(locale);

        return l.toArray(new Locale[l.size()]);
    }
}
