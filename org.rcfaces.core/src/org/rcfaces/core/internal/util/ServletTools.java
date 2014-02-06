/*
 * $Id: ServletTools.java,v 1.4 2013/11/13 12:53:22 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.internal.util;

import java.util.Locale;

import javax.faces.FacesException;
import javax.faces.FactoryFinder;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextFactory;
import javax.faces.event.PhaseListener;
import javax.faces.lifecycle.Lifecycle;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.internal.util.WebXmlParser.ServletBean;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.4 $ $Date: 2013/11/13 12:53:22 $
 */
public class ServletTools {

    private static final Log LOG = LogFactory.getLog(ServletTools.class);

    public static String computeResourceURI(ServletContext servletContext,
            String defaultURI, Class< ? extends Servlet> servletClass) {

        WebXmlParser parser = new WebXmlParser(servletContext);

        ServletBean servlets[] = parser.getServletsByClassName(servletClass
                .getName());

        if (LOG.isDebugEnabled()) {
            LOG.debug("Search pattern associated to servlet class '"
                    + servletClass + "' => " + servlets.length + " response(s)");
        }

        if (servlets.length < 1) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Return default uri ('" + defaultURI
                        + "') for pattern of servlet '" + servletClass + "'.");
            }

            return defaultURI;
        }

        for (int i = 0; i < servlets.length; i++) {
            ServletBean servlet = servlets[i];

            String patterns[] = servlet.listUrlPatterns();
            if (patterns.length < 1) {
                continue;
            }

            for (int j = 0; j < patterns.length; j++) {
                String pattern = patterns[j];

                if (pattern.startsWith("/") == false
                        || pattern.endsWith("/*") == false) {

                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Refuse pattern '" + pattern + "'");
                    }
                    continue;
                }

                if (LOG.isDebugEnabled()) {
                    LOG.debug("Accept pattern '" + pattern + "'");
                }

                return pattern.substring(0, pattern.length() - 2);
            }
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("Return default uri ('" + defaultURI
                    + "') for pattern of servlet '" + servletClass + "'.");
        }

        return defaultURI;
    }

    public static Locale getDefaultLocale(ServletContext servletContext,
            HttpServletRequest request, HttpServletResponse response) {

        boolean releaseContext = false;
        FacesContext facesContext = FacesContext.getCurrentInstance();

        if (facesContext == null) {
            FacesContextFactory facesContextFactory = (FacesContextFactory) FactoryFinder
                    .getFactory(FactoryFinder.FACES_CONTEXT_FACTORY);

            Lifecycle lifeCycle = EmptyLifecycle.SINGLETON;

            facesContext = facesContextFactory.getFacesContext(servletContext,
                    request, response, lifeCycle);
            releaseContext = true;
        }

        Locale locale;
        try {
            locale = facesContext.getApplication().getDefaultLocale();

        } finally {
            if (releaseContext) {
                facesContext.release();
            }
        }

        if (locale != null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Application default locale: " + locale);
            }
            return locale;
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("No application default locale, use system default: "
                    + locale);
        }
        return Locale.getDefault();

    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.4 $ $Date: 2013/11/13 12:53:22 $
     */
    private static final class EmptyLifecycle extends Lifecycle {

        public static final Lifecycle SINGLETON = new EmptyLifecycle();

        @Override
        public void addPhaseListener(PhaseListener listener) {
            // System.out.println("Add phase listener !");
        }

        @Override
        public void execute(FacesContext context) throws FacesException {
            // System.out.println("EXECUTE !");
        }

        @Override
        public PhaseListener[] getPhaseListeners() {
            return null;
        }

        @Override
        public void removePhaseListener(PhaseListener listener) {
            // System.out.println("Remove phase listener !");
        }

        @Override
        public void render(FacesContext context) throws FacesException {
            // System.out.println("Render !");
        }
    }
}
