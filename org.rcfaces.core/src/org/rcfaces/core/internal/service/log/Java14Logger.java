/*
 * $Id: Java14Logger.java,v 1.4 2013/11/13 12:53:26 jbmeslin Exp $
 */
package org.rcfaces.core.internal.service.log;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

import org.rcfaces.core.internal.service.log.LogService.IFilter;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.4 $ $Date: 2013/11/13 12:53:26 $
 */
class Java14Logger implements LogService.ILogger {

    private static LogManager logManager = LogManager.getLogManager();

    private IFilter filters[];

    public void logException(FacesContext facesContext, UIViewRoot viewRoot,
            String name, long date, String message, int level,
            Throwable exception) {

        Logger logger = logManager.getLogger(name);

        Level levelObject = convertIntToLevel(level);

        if (logger.isLoggable(levelObject) == false) {
            return;
        }

        LogRecord logRecord = new LogRecord(levelObject, message);
        if (exception != null) {
            logRecord.setThrown(exception);
        }
        if (date > 0) {
            logRecord.setMillis(date);
        }

        logger.log(logRecord);
    }

    public synchronized IFilter[] listFilters(FacesContext facesContext) {
        if (filters != null) {
            return filters;
        }

        List<LogService.Filter> l = null;

        Enumeration<String> loggers = logManager.getLoggerNames();

        for (; loggers.hasMoreElements();) {
            String loggerName = loggers.nextElement();

            Logger logger = logManager.getLogger(loggerName);

            if (logger == null) {
                continue;
            }

            String name = logger.getName();
            if (name.startsWith(LogService.PREFIX_LOGGER_NAME) == false) {
                continue;
            }

            name = name.substring(LogService.PREFIX_LOGGER_NAME.length());
            if (name.length() > 0 && name.charAt(0) == '.') {
                name = name.substring(1);
            }

            int level = convertLevelToInt(logger.getLevel());

            if (l == null) {
                l = new ArrayList<LogService.Filter>();
            }

            l.add(new LogService.Filter(level, name));
        }

        if (l == null) {
            return LogService.EMPTY_FILTERS;
        }

        return l.toArray(new LogService.Filter[l.size()]);
    }

    private static final int convertLevelToInt(Level level) {
        int l = level.intValue();
        if (l >= Level.SEVERE.intValue()) {
            return 0;
        }
        if (l >= Level.WARNING.intValue()) {
            return 2;
        }
        if (l >= Level.INFO.intValue()) {
            return 3;
        }
        if (l >= Level.CONFIG.intValue()) {
            return 4;
        }

        return 5;
    }

    private static final Level convertIntToLevel(int level) {
        switch (level) {
        case 0:
        case 1:
            return Level.SEVERE;

        case 2:
            return Level.WARNING;

        case 3:
            return Level.INFO;

        case 4:
            return Level.CONFIG;

        default:
            return Level.FINE;
        }
    }
}
