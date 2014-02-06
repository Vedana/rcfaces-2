/*
 * $Id: SessionCookieViewSizeDetector.java,v 1.1 2013/01/11 15:45:05 jbmeslin Exp $
 */
package org.rcfaces.renderkit.html.internal.layout;

import java.util.Map;
import java.util.StringTokenizer;

import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SessionCookieViewSizeDetector implements IViewSizeDetector {

    private static final String COOKIE_NAME = "org.rcfaces.html.ViewSize";

    public Size getViewSize(UIViewRoot viewRoot) {

        FacesContext facesContext = FacesContext.getCurrentInstance();

        Map<String, Object> cookieMap = facesContext.getExternalContext()
                .getRequestCookieMap();

        Cookie cookie = (Cookie) cookieMap.get(COOKIE_NAME);
        if (cookie == null) {

            installCookie(facesContext);
            return null;
        }

        String cookieValue = cookie.getValue();
        if (cookieValue == null || cookieValue.length() == 0) {
            return null;
        }

        StringTokenizer st = new StringTokenizer(cookieValue, ",");
        if (st.hasMoreTokens() == false) {
            return null;
        }
        String w = st.nextToken();
        if (st.hasMoreTokens() == false) {
            return null;
        }
        String h = st.nextToken();

        Size size = new Size(Double.parseDouble(w.trim()), Double.parseDouble(h
                .trim()));

        return size;
    }

    private void installCookie(FacesContext facesContext) {

        ExternalContext externalContext = facesContext.getExternalContext();

        HttpServletRequest request = (HttpServletRequest) externalContext
                .getRequest();
        HttpServletResponse response = (HttpServletResponse) externalContext
                .getResponse();

        String requestURI = request.getRequestURI();
        int idx = requestURI.indexOf("//");
        if (idx >= 0) {
            requestURI = requestURI.substring(idx + 2);
        }

        idx = requestURI.indexOf(':');
        if (idx >= 0) {
            requestURI = requestURI.substring(0, idx);
        }

        idx = requestURI.indexOf('/');
        if (idx >= 0) {
            requestURI = requestURI.substring(0, idx);
        }

        idx = requestURI.lastIndexOf('.');
        if (idx > 0) {
            int idx1 = requestURI.lastIndexOf('.', idx - 1);

            if (idx1 > 0) {
                requestURI = requestURI.substring(idx1 + 1);
            }
        }

        Cookie cookie = new Cookie(COOKIE_NAME, "");
        cookie.setDomain(requestURI);
        cookie.setPath("/");

        response.addCookie(cookie);
    }
}
