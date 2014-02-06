/*
 * $Id: IncludeHttpServletRequest.java,v 1.3 2013/01/11 15:46:59 jbmeslin Exp $
 */
package org.rcfaces.core.internal.util;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.rcfaces.core.internal.webapp.ExtendedHttpServlet;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/01/11 15:46:59 $
 */
public class IncludeHttpServletRequest extends HttpServletRequestWrapper {

    private static final Set<String> IGNORED_HEADER = new HashSet<String>(8);
    static {
        IGNORED_HEADER.add(ExtendedHttpServlet.HTTP_IF_MODIFIED_SINCE);
        IGNORED_HEADER.add(ExtendedHttpServlet.HTTP_IF_NONE_MATCH);
        IGNORED_HEADER.add(ExtendedHttpServlet.HTTP_LAST_MODIFIED);
        IGNORED_HEADER.add(ExtendedHttpServlet.HTTP_IF_NOT_HASH);
        IGNORED_HEADER.add(ExtendedHttpServlet.HTTP_ACCEPT_ENCODING); // Desactive le GZIP !
    }

    public IncludeHttpServletRequest(HttpServletRequest servletRequest) {
        super(servletRequest);
    }

    @Override
    public long getDateHeader(String headerName) {
        if (IGNORED_HEADER.contains(headerName)) {
            return -1;
        }
        return super.getDateHeader(headerName);
    }

    @Override
    public String getHeader(String headerName) {
        if (IGNORED_HEADER.contains(headerName)) {
            return null;
        }
        return super.getHeader(headerName);
    }

    @Override
    public int getIntHeader(String headerName) {
        if (IGNORED_HEADER.contains(headerName)) {
            return -1;
        }

        return super.getIntHeader(headerName);
    }

    @Override
    public String getParameter(String parameterName) {
        if (IGNORED_HEADER.contains(parameterName)) {
            return null;
        }

        return super.getParameter(parameterName);
    }

    @Override
    public Enumeration getHeaders(String parameterName) {
        if (IGNORED_HEADER.contains(parameterName)) {
            return new Vector().elements();
        }
        return super.getHeaders(parameterName);
    }

    @Override
    public String[] getParameterValues(String parameterName) {
        if (IGNORED_HEADER.contains(parameterName)) {
            return null;
        }

        return super.getParameterValues(parameterName);
    }

    @Override
    public String getMethod() {
        return "GET";
    }

    @Override
    public String getScheme() {
        return "http";
    }

}
