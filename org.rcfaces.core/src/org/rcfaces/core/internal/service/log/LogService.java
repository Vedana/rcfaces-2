/*
 * $Id: LogService.java,v 1.2 2013/07/03 12:25:08 jbmeslin Exp $
 */
package org.rcfaces.core.internal.service.log;

import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.impl.Jdk14Logger;
import org.apache.commons.logging.impl.Log4JLogger;
import org.rcfaces.core.internal.service.AbstractService;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:08 $
 */
public abstract class LogService extends AbstractService {

    

    private static final Log LOG = LogFactory.getLog(LogService.class);

    public static final String PREFIX_LOGGER_NAME = "org.rcfaces.client";

    protected static final IFilter[] EMPTY_FILTERS = new IFilter[0];

    private final Object LOGGER_LOCK = new Object();

    private ILogger logger;

    protected ILogger getLogger() {
        synchronized (LOGGER_LOCK) {
            if (logger != null) {
                return logger;
            }

            try {
                if (LOG instanceof Log4JLogger) {
                    logger = new Log4jLogger();
                }

            } catch (Throwable th) {
                // en cas de debuggage, on peut avoir un ClassCastException !
            }

            try {
                if (LOG instanceof Jdk14Logger) {
                    logger = new Java14Logger();
                }

            } catch (Throwable th) {
                // en cas de debuggage, on peut avoir un ClassCastException !
            }

            if (logger == null) {
                logger = new CommonsLogger();
            }
        }
        return logger;
    }

    public interface IFilter {
        String getName();

        int getLevel();
    }

    public interface ILogger {
        void logException(FacesContext facesContext, UIViewRoot viewRoot,
                String name, long date, String message, int level,
                Throwable exception);

        IFilter[] listFilters(FacesContext facesContext);
    }

    public final IFilter[] listFilters(FacesContext facesContext) {
        return getLogger().listFilters(facesContext);
    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:08 $
     */
    public static class Filter implements IFilter {
        

        private final int level;

        private final String name;

        public Filter(int level, String name) {
            this.level = level;
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public int getLevel() {
            return level;
        }
    }
}
