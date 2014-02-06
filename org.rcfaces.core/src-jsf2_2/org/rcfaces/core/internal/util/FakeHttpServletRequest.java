/*
 * $Id: FakeHttpServletRequest.java,v 1.1 2014/02/05 16:05:53 jbmeslin Exp $
 */
package org.rcfaces.core.internal.util;

import java.io.IOException;
import java.security.Principal;
import java.util.Collection;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.1 $ $Date: 2014/02/05 16:05:53 $
 */
public class FakeHttpServletRequest extends FakeServletRequest implements
        HttpServletRequest {
    

    public static final HttpServletRequest SINGLETON = new FakeHttpServletRequest();

    public String getAuthType() {

        return null;
    }

    public String getContextPath() {

        return null;
    }

    public Cookie[] getCookies() {

        return null;
    }

    public long getDateHeader(String arg0) {

        return 0;
    }

    public String getHeader(String arg0) {

        return null;
    }

    public Enumeration getHeaderNames() {

        return null;
    }

    public Enumeration getHeaders(String arg0) {

        return null;
    }

    public int getIntHeader(String arg0) {

        return 0;
    }

    public String getMethod() {

        return null;
    }

    public String getPathInfo() {

        return null;
    }

    public String getPathTranslated() {

        return null;
    }

    public String getQueryString() {

        return null;
    }

    public String getRemoteUser() {

        return null;
    }

    public String getRequestURI() {

        return null;
    }

    public StringBuffer getRequestURL() {

        return null;
    }

    public String getRequestedSessionId() {

        return null;
    }

    public String getServletPath() {

        return null;
    }

    public HttpSession getSession() {

        return null;
    }

    public HttpSession getSession(boolean arg0) {

        return null;
    }

    public Principal getUserPrincipal() {

        return null;
    }

    public boolean isRequestedSessionIdFromCookie() {

        return false;
    }

    public boolean isRequestedSessionIdFromURL() {

        return false;
    }

    public boolean isRequestedSessionIdFromUrl() {

        return false;
    }

    public boolean isRequestedSessionIdValid() {

        return false;
    }

    public boolean isUserInRole(String arg0) {

        return false;
    }

    public boolean authenticate(HttpServletResponse arg0) throws IOException,
            ServletException {
        // TODO Auto-generated method stub
        return false;
    }

    public Part getPart(String arg0) throws IOException, IllegalStateException,
            ServletException {
        // TODO Auto-generated method stub
        return null;
    }

    public Collection<Part> getParts() throws IOException,
            IllegalStateException, ServletException {
        // TODO Auto-generated method stub
        return null;
    }

    public void login(String arg0, String arg1) throws ServletException {
        // TODO Auto-generated method stub
        
    }

    public void logout() throws ServletException {
        // TODO Auto-generated method stub
        
    }

}
