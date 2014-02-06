/*
 * $Id: ResourcesBundleContentProvider.java,v 1.3 2013/11/13 12:53:32 jbmeslin Exp $
 * 
 */
package org.rcfaces.renderkit.html.internal.javascript;

import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.internal.lang.StringAppender;
import org.rcfaces.core.internal.repository.IRepository.ICriteria;
import org.rcfaces.core.internal.repository.LocaleCriteria;
import org.rcfaces.core.internal.util.FilteredContentProvider;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:32 $
 */
public class ResourcesBundleContentProvider extends FilteredContentProvider {

    private static final Log LOG = LogFactory
            .getLog(ResourcesBundleContentProvider.class);

    private static final String JAVASCRIPT_CHARSET = "UTF-8";

    private final String resourceBundleBaseName;

    public ResourcesBundleContentProvider(String resourceBundleBaseName) {
        this.resourceBundleBaseName = resourceBundleBaseName;
    }

    protected String getCharset() {
        return JAVASCRIPT_CHARSET;
    }

    protected String updateBuffer(String buffer, URL url, ICriteria criteria) {

        if (criteria == null) {
            return super.updateBuffer(buffer, url, criteria);
        }

        // XXX ??? pourquoi ???
        Locale locale = LocaleCriteria.getLocale(criteria);
        ResourceBundle resourceBundle = ResourceBundle.getBundle(
                resourceBundleBaseName, locale);

        StringAppender sa = new StringAppender(buffer, 8000);

        return super.updateBuffer(sa.toString(), url, criteria);
    }

    protected static ClassLoader getCurrentLoader(Object fallbackClass) {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        if (loader != null) {
            return loader;
        }

        return fallbackClass.getClass().getClassLoader();
    }
}
