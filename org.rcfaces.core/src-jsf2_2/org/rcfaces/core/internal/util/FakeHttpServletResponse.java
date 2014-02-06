/*
 * $Id: FakeHttpServletResponse.java,v 1.1 2014/02/05 16:05:53 jbmeslin Exp $
 */
package org.rcfaces.core.internal.util;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.1 $ $Date: 2014/02/05 16:05:53 $
 */
public class FakeHttpServletResponse extends FakeServletResponse implements
        HttpServletResponse {
    

    public static final HttpServletResponse SINGLETON = new FakeHttpServletResponse();

    private int status;

    private String statusText;

    public void addCookie(Cookie arg0) {

    }

    public void addDateHeader(String arg0, long arg1) {

    }

    public void addHeader(String arg0, String arg1) {

    }

    public void addIntHeader(String arg0, int arg1) {

    }

    public boolean containsHeader(String arg0) {

        return false;
    }

    public String encodeRedirectURL(String arg0) {

        return null;
    }

    public String encodeRedirectUrl(String arg0) {

        return null;
    }

    public String encodeURL(String arg0) {

        return null;
    }

    public String encodeUrl(String arg0) {

        return null;
    }

    public void sendError(int arg0) throws IOException {

    }

    public void sendError(int arg0, String arg1) throws IOException {

    }

    public void sendRedirect(String arg0) throws IOException {

    }

    public void setDateHeader(String arg0, long arg1) {

    }

    public void setHeader(String arg0, String arg1) {

    }

    public void setIntHeader(String arg0, int arg1) {

    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setStatus(int status, String statusText) {
        this.status = status;
        this.statusText = statusText;
    }

    public String getHeader(String arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    public Collection<String> getHeaderNames() {
        // TODO Auto-generated method stub
        return null;
    }

    public Collection<String> getHeaders(String arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    public int getStatus() {
        return status;
    }

}
