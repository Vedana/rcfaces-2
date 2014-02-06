/*
 * $Id: PerformanceTimingLogger.java,v 1.1 2013/01/11 15:45:05 jbmeslin Exp $
 */
package org.rcfaces.renderkit.html.timing;

import java.net.InetAddress;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.1 $ $Date: 2013/01/11 15:45:05 $
 */
public class PerformanceTimingLogger implements IPerformanceTimingProcessor {

    private static final Log PERFORMANCE_TIMING = LogFactory
            .getLog("rcfaces.performanceTiming");

    private static final DateFormat dateFormat = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss.SSS");

    public PerformanceTimingLogger() {
        PERFORMANCE_TIMING
                .info("clientDate;serverDate;remoteAddress;type;sourceURL;sourceTarget;destinationURL;destinationTarget;navigationStart;unloadEventStart;unloadEventEnd;redirectStart;redirectEnd;fetchStart;domainLookupStart;domainLookupEnd;connectStart;connectEnd;secureConnectionStart;requestStart;responseStart;responseEnd;domLoading;domInteractive;domContentLoadedEventStart;domContentLoadedEventEnd;domComplete;loadEventStart;loadEventEnd;agent\n");

    }

    public void process(FacesContext facesContext, IPerformanceTiming timing) {
        if (timing == null) {
            return;
        }

        StringBuilder sb = new StringBuilder(1024);

        add(sb, timing.getClientDate());

        add(sb, timing.getServerDate());

        add(sb, timing.getRemoteAddress());

        add(sb, timing.getPageType());

        add(sb, timing.getSourceURL());
        add(sb, timing.getSourceTarget());
        add(sb, timing.getDestinationURL());
        add(sb, timing.getDestinationTarget());

        long t = timing.getNavigationStart();
        add(sb, new Date(t));

        add(sb, t, timing.getUnloadEventStart());

        add(sb, t, timing.getUnloadEventEnd());

        add(sb, t, timing.getRedirectStart());

        add(sb, t, timing.getRedirectEnd());

        add(sb, t, timing.getFetchStart());

        add(sb, t, timing.getDomainLookupStart());

        add(sb, t, timing.getDomainLookupEnd());

        add(sb, t, timing.getConnectStart());

        add(sb, t, timing.getConnectEnd());

        add(sb, t, timing.getSecureConnectionStart());

        add(sb, t, timing.getRequestStart());

        add(sb, t, timing.getResponseStart());

        add(sb, t, timing.getResponseEnd());

        add(sb, t, timing.getDomLoading());

        add(sb, t, timing.getDomInteractive());

        add(sb, t, timing.getDomContentLoadedEventStart());

        add(sb, t, timing.getDomContentLoadedEventEnd());

        add(sb, t, timing.getDomComplete());

        add(sb, t, timing.getLoadEventStart());

        add(sb, t, timing.getLoadEventEnd());

        add(sb, timing.getAgent());

        sb.append('\n');

        PERFORMANCE_TIMING.info(sb.toString());
    }

    private void add(StringBuilder sb, InetAddress inetAddress) {
        if (inetAddress != null) {
            sb.append(inetAddress.getHostAddress());
        }

        sb.append(';');
    }

    private void add(StringBuilder sb, long base, long time) {
        if (time > 0 && base > 0) {
            sb.append(Long.valueOf(time - base));
        }

        sb.append(';');
    }

    private void add(StringBuilder sb, String text) {
        if (text != null) {
            text = text.replace("\"", "\"\"");

            sb.append('\"').append(text).append('\"');
        }

        sb.append(';');
    }

    private void add(StringBuilder sb, Date date) {
        if (date != null) {
            synchronized (dateFormat) {
                sb.append(dateFormat.format(date));
            }
        }
        sb.append(';');
    }

}
