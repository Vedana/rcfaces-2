/*
 * $Id: ApplicationVersionServlet.java,v 1.4 2013/11/13 12:53:26 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.internal.version;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.internal.util.ServletTools;
import org.rcfaces.core.internal.webapp.ConfiguredHttpServlet;
import org.rcfaces.core.internal.webapp.ExpirationDate;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.4 $ $Date: 2013/11/13 12:53:26 $
 */
public class ApplicationVersionServlet extends ConfiguredHttpServlet {

    private static final long serialVersionUID = -4209462021160100620L;

    private static final Log LOG = LogFactory
            .getLog(ApplicationVersionServlet.class);

    private static final String DEFAULT_APPLICATION_VERSION_URL = "/ap-v";

    private static final String APPLICATION_VERSION_URL_PROPERTY = "org.rcfaces.core.internal.rewriting.APPLICATION_VERSION_URL_PROPERTY";

    @Override
    public void init(ServletConfig config) throws ServletException {

        super.init(config);

        String applicationVersionURL = ServletTools.computeResourceURI(
                getServletContext(), DEFAULT_APPLICATION_VERSION_URL,
                getClass());
        if (applicationVersionURL == null) {
            return;
        }
        LOG.info("Base of application version url is '" + applicationVersionURL
                + "'.");

        getServletContext().setAttribute(APPLICATION_VERSION_URL_PROPERTY,
                applicationVersionURL);
    }

    static String getApplicationVersionURI(Map<String, Object> applicationMap) {
        return (String) applicationMap.get(APPLICATION_VERSION_URL_PROPERTY);
    }

    @Override
    protected void service(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {

        String url = request.getRequestURI();

        String contextPath = request.getContextPath();
        if (contextPath != null) {
            url = url.substring(contextPath.length());
        }

        String servletPath = request.getServletPath();
        if (servletPath != null) {
            url = url.substring(servletPath.length());
        }

        // Retire le nom de notre servlet
        int idx = url.indexOf('/');
        if (idx < 0) {
            throw new ServletException("Can not understand URI '"
                    + request.getRequestURI() + "'.");
        }

        url = url.substring(idx + 1);

        idx = url.indexOf('/');
        if (idx < 0) {
            throw new ServletException("Can not understand URI '"
                    + request.getRequestURI() + "'.");
        }
        String version = url.substring(0, idx);
        url = url.substring(idx);

        if (LOG.isDebugEnabled()) {
            LOG.debug("Forward url='" + url + "' version='" + version
                    + "'  requested url='" + request.getRequestURI() + "'.");
        }

        RequestDispatcher requestDispatcher = getServletContext()
                .getRequestDispatcher(url);
        if (requestDispatcher == null) {
            LOG.error("Can not get request dispatcher for url '" + url + "'.");

            throw new ServletException(
                    "Can not get request dispatcher for url '" + url + "'.");
        }

        HttpServletResponse wrappedResponse = new HttpServletResponseWrapper(
                response) {

            protected void updateExpiration() {
                ExpirationDate expirationDate = getDefaultExpirationDate(true);
                if (expirationDate != null) {
                    expirationDate.sendExpires(this);
                }
            }

            @Override
            public ServletOutputStream getOutputStream() throws IOException {
                updateExpiration();

                return super.getOutputStream();
            }

            @Override
            public PrintWriter getWriter() throws IOException {
                updateExpiration();

                return super.getWriter();
            }

        };

        requestDispatcher.forward(request, wrappedResponse);
    }
}
