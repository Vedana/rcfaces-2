/*
 * $Id: Globals.java,v 1.1 2011/04/12 09:25:41 oeuillot Exp $
 */
package org.rcfaces.core.internal;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:41 $
 */
public class Globals {

    /**
     * The request attribute under which the request URI of the included servlet
     * is stored on an included dispatcher request.
     */
    public static final String INCLUDE_REQUEST_URI_ATTR = "javax.servlet.include.request_uri";

    /**
     * The request attribute under which the context path of the included
     * servlet is stored on an included dispatcher request.
     */
    public static final String INCLUDE_CONTEXT_PATH_ATTR = "javax.servlet.include.context_path";

    /**
     * The request attribute under which the path info of the included servlet
     * is stored on an included dispatcher request.
     */
    public static final String INCLUDE_PATH_INFO_ATTR = "javax.servlet.include.path_info";

    /**
     * The request attribute under which the servlet path of the included
     * servlet is stored on an included dispatcher request.
     */
    public static final String INCLUDE_SERVLET_PATH_ATTR = "javax.servlet.include.servlet_path";

    /**
     * The request attribute under which the query string of the included
     * servlet is stored on an included dispatcher request.
     */
    public static final String INCLUDE_QUERY_STRING_ATTR = "javax.servlet.include.query_string";

    /**
     * The request attribute under which the original request URI is stored on
     * an forwarded dispatcher request.
     */
    public static final String FORWARD_REQUEST_URI_ATTR = "javax.servlet.forward.request_uri";

    /**
     * The request attribute under which the original context path is stored on
     * an forwarded dispatcher request.
     */
    public static final String FORWARD_CONTEXT_PATH_ATTR = "javax.servlet.forward.context_path";

    /**
     * The request attribute under which the original path info is stored on an
     * forwarded dispatcher request.
     */
    public static final String FORWARD_PATH_INFO_ATTR = "javax.servlet.forward.path_info";

    /**
     * The request attribute under which the original servlet path is stored on
     * an forwarded dispatcher request.
     */
    public static final String FORWARD_SERVLET_PATH_ATTR = "javax.servlet.forward.servlet_path";

    /**
     * The request attribute under which the original query string is stored on
     * an forwarded dispatcher request.
     */
    public static final String FORWARD_QUERY_STRING_ATTR = "javax.servlet.forward.query_string";

    /**
     * The request attribute under which we forward a servlet name to an error
     * page.
     */
    public static final String SERVLET_NAME_ATTR = "javax.servlet.error.servlet_name";

}
