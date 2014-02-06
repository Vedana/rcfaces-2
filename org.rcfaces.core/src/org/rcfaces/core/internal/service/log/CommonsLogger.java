/*
 * $Id: CommonsLogger.java,v 1.4 2013/11/13 12:53:26 jbmeslin Exp $
 */
package org.rcfaces.core.internal.service.log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogConfigurationException;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.internal.Constants;
import org.rcfaces.core.internal.service.log.LogService.IFilter;
import org.rcfaces.core.internal.service.log.LogService.ILogger;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.4 $ $Date: 2013/11/13 12:53:26 $
 */
public class CommonsLogger implements ILogger {
    private static final Log LOG = LogFactory.getLog(CommonsLogger.class);

    private static final String DEFAULT_LOG_LEVEL_PARAMETER = Constants
            .getPackagePrefix() + ".client.DEFAULT_LOG_LEVEL";

    private static final String LOG_LEVELS_PARAMETER = Constants
            .getPackagePrefix() + ".client.LOG_LEVELS";

    private static final Map<String, Object> LOG_LEVELS = new HashMap<String, Object>(
            8);

    static {
        LOG_LEVELS.put("FATAL", new Integer(0));
        LOG_LEVELS.put("ERROR", new Integer(1));
        LOG_LEVELS.put("WARN", new Integer(2));
        LOG_LEVELS.put("INFO", new Integer(3));
        LOG_LEVELS.put("DEBUG", new Integer(4));
        LOG_LEVELS.put("TRACE", new Integer(5));
    }

    private IFilter filters[];

    public void logException(FacesContext facesContext, UIViewRoot viewRoot,
            String name, long date, String message, int level, Throwable ex) {

        if (name != null) {
            try {
                Log log = LogFactory.getLog(LogService.PREFIX_LOGGER_NAME + "."
                        + name);
                if (log != null) {
                    if (level < 0) {
                        level = 0;

                    } else if (level > 5) {
                        level = 5;
                    }

                    switch (level) {
                    case 0:
                        log.fatal(message, ex);
                        return;

                    case 1:
                        log.error(message, ex);
                        return;

                    case 2:
                        log.warn(message, ex);
                        return;

                    case 3:
                        log.info(message, ex);
                        return;

                    case 4:
                        log.debug(message, ex);
                        return;

                    case 5:
                        log.trace(message, ex);
                        return;
                    }
                }

            } catch (LogConfigurationException logException) {
                LOG.error("Can not log '" + name + "'.", logException);
            }
        }

        if (LOG.isDebugEnabled()) {
            LOG.trace("Name=" + name);
            LOG.trace("  level=" + level);
            LOG.trace("  message=" + message);
            LOG.trace("  exception=" + ex);
        }
    }

    public synchronized IFilter[] listFilters(FacesContext facesContext) {
        if (verifyFilters(facesContext)) {
            return filters;
        }

        filters = loadFilters(facesContext);

        return filters;
    }

    protected boolean verifyFilters(FacesContext facesContext) {
        return (filters != null);
    }

    @SuppressWarnings("unchecked")
    protected IFilter[] loadFilters(FacesContext facesContext) {

        Map<String, Object> initParameters = facesContext.getExternalContext()
                .getInitParameterMap();

        List<LogService.Filter> l = new ArrayList<LogService.Filter>();

        String level = (String) initParameters.get(DEFAULT_LOG_LEVEL_PARAMETER);
        if (level != null) {
            Integer i = (Integer) LOG_LEVELS.get(level.toUpperCase());
            if (i != null) {
                l.add(new LogService.Filter(i.intValue(), ""));
            }
        }

        String levels = (String) initParameters.get(LOG_LEVELS_PARAMETER);
        if (levels != null) {
            for (StringTokenizer st = new StringTokenizer(levels, ", "); st
                    .hasMoreTokens();) {
                String token = st.nextToken();

                int idx = token.indexOf('=');
                if (idx < 0) {
                    continue;
                }

                String name = token.substring(0, idx);
                level = token.substring(idx + 1);
                Integer i = (Integer) LOG_LEVELS.get(level.toUpperCase());
                if (i == null) {
                    continue;
                }

                l.add(new LogService.Filter(i.intValue(), name));
            }
        }

        if (l.isEmpty()) {
            return LogService.EMPTY_FILTERS;
        }

        return l.toArray(new IFilter[l.size()]);
    }
}