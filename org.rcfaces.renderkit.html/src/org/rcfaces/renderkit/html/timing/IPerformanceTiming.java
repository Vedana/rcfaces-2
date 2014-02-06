/*
 * $Id: IPerformanceTiming.java,v 1.2 2013/11/13 12:53:32 jbmeslin Exp $
 */
package org.rcfaces.renderkit.html.timing;

import java.util.Date;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/11/13 12:53:32 $
 */
public interface IPerformanceTiming {

    Date getClientDate();

    Date getServerDate();

    String getAgent();

    String getRemoteAddress();

    String getPageType();

    String getSourceURL();

    String getSourceTarget();

    String getDestinationURL();

    String getDestinationTarget();

    long getEventStart();

    long getNavigationStart();

    long getUnloadEventStart();

    long getUnloadEventEnd();

    long getRedirectStart();

    long getRedirectEnd();

    long getFetchStart();

    long getDomainLookupStart();

    long getDomainLookupEnd();

    long getConnectStart();

    long getConnectEnd();

    long getSecureConnectionStart();

    long getRequestStart();

    long getResponseStart();

    long getResponseEnd();

    long getDomLoading();

    long getDomInteractive();

    long getDomContentLoadedEventStart();

    long getDomContentLoadedEventEnd();

    long getDomComplete();

    long getLoadEventStart();

    long getLoadEventEnd();

}