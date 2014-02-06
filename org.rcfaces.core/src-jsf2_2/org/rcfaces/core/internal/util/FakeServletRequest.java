/*
 * $Id: FakeServletRequest.java,v 1.1 2014/02/05 16:05:53 jbmeslin Exp $
 */
package org.rcfaces.core.internal.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.AsyncContext;
import javax.servlet.DispatcherType;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.1 $ $Date: 2014/02/05 16:05:53 $
 */
public class FakeServletRequest implements ServletRequest {
    

    public static final ServletRequest SINGLETON = new FakeServletRequest();

    private final Map attributes = new HashMap();

    public void removeAttribute(String arg0) {
        attributes.remove(arg0);
    }

    public void setAttribute(String arg0, Object arg1) {
        attributes.put(arg0, arg1);
    }

    public Object getAttribute(String arg0) {
        return attributes.get(arg0);
    }

    public Enumeration getAttributeNames() {
        return Collections.enumeration(attributes.keySet());
    }

    public String getCharacterEncoding() {

        return null;
    }

    public int getContentLength() {

        return 0;
    }

    public String getContentType() {

        return null;
    }

    public ServletInputStream getInputStream() throws IOException {

        return null;
    }

    public String getLocalAddr() {

        return null;
    }

    public String getLocalName() {

        return null;
    }

    public int getLocalPort() {

        return 0;
    }

    public Locale getLocale() {

        return null;
    }

    public Enumeration getLocales() {

        return null;
    }

    public String getParameter(String arg0) {

        return null;
    }

    public Map getParameterMap() {

        return null;
    }

    public Enumeration getParameterNames() {

        return null;
    }

    public String[] getParameterValues(String arg0) {

        return null;
    }

    public String getProtocol() {

        return null;
    }

    public BufferedReader getReader() throws IOException {

        return null;
    }

    public String getRealPath(String arg0) {

        return null;
    }

    public String getRemoteAddr() {

        return null;
    }

    public String getRemoteHost() {

        return null;
    }

    public int getRemotePort() {

        return 0;
    }

    public RequestDispatcher getRequestDispatcher(String arg0) {

        return null;
    }

    public String getScheme() {

        return null;
    }

    public String getServerName() {

        return null;
    }

    public int getServerPort() {

        return 0;
    }

    public boolean isSecure() {

        return false;
    }

    public void setCharacterEncoding(String arg0) {

    }

    public AsyncContext getAsyncContext() {
        // TODO Auto-generated method stub
        return null;
    }

    public DispatcherType getDispatcherType() {
        // TODO Auto-generated method stub
        return null;
    }

    public ServletContext getServletContext() {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean isAsyncStarted() {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isAsyncSupported() {
        // TODO Auto-generated method stub
        return false;
    }

    public AsyncContext startAsync() {
        // TODO Auto-generated method stub
        return null;
    }

    public AsyncContext startAsync(ServletRequest arg0, ServletResponse arg1) {
        // TODO Auto-generated method stub
        return null;
    }

}
