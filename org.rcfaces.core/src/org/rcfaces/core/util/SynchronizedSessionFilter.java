/*
 * $Id: SynchronizedSessionFilter.java,v 1.1 2013/01/11 15:47:01 jbmeslin Exp $
 */
package org.rcfaces.core.util;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.1 $ $Date: 2013/01/11 15:47:01 $
 */
public class SynchronizedSessionFilter implements Filter {

    private static final Log LOG = LogFactory
            .getLog(SynchronizedSessionFilter.class);

    private static final boolean DEBUG = LOG.isDebugEnabled();

    private static final String SESSION_LOCK_ATTRIBUTE_NAME = "org.rcfaces.SESSION_LOCK";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        LOG.info("Synchronized session filter installed !");
    }

    @Override
    public void doFilter(ServletRequest servletRequest,
            ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {

        if (servletRequest instanceof HttpServletRequest) {
            HttpSession session = ((HttpServletRequest) servletRequest)
                    .getSession(false);

            if (session != null) {
                Object lock;

                synchronized (this) {
                    lock = session.getAttribute(SESSION_LOCK_ATTRIBUTE_NAME);

                    if (lock == null) {
                        lock = createLock(session);

                        session.setAttribute(SESSION_LOCK_ATTRIBUTE_NAME, lock);

                        if (DEBUG) {
                            LOG.debug("Create session lock '" + lock
                                    + "' for request '" + servletRequest + "'.");
                        }

                    } else {
                        if (DEBUG) {
                            LOG.debug("Use session lock '" + lock
                                    + "' for request '" + servletRequest + "'.");
                        }
                    }
                }

                if (lock instanceof String) {
                    lock = ((String) lock).intern();
                }

                synchronized (lock) {
                    filterChain.doFilter(servletRequest, servletResponse);
                }

                return;

            }

            if (DEBUG) {
                LOG.debug("No session for request '" + servletRequest + "'.");
            }
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    private Object createLock(HttpSession session) {
        Object lock = session.getId();

        return lock;
    }

    @Override
    public void destroy() {
        LOG.info("Synchronized session filter destroyed !");
    }

}