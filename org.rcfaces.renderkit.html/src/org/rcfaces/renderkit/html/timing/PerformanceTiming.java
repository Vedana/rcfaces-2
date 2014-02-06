package org.rcfaces.renderkit.html.timing;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

class PerformanceTiming implements IPerformanceTiming {

    private static final DateFormat dateFormat = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss.SSS");

    private long eventStart = -1;

    private long navigationStart = -1;

    private long unloadEventStart = -1;

    private long unloadEventEnd = -1;

    private long redirectStart = -1;

    private long redirectEnd = -1;

    private long fetchStart = -1;

    private long domainLookupStart = -1;

    private long domainLookupEnd = -1;

    private long connectStart = -1;

    private long connectEnd = -1;

    private long secureConnectionStart = -1;

    private long requestStart = -1;

    private long responseStart = -1;

    private long responseEnd = -1;

    private long domLoading = -1;

    private long domInteractive = -1;

    private long domContentLoadedEventStart = -1;

    private long domContentLoadedEventEnd = -1;

    private long domComplete = -1;

    private long loadEventStart = -1;

    private long loadEventEnd = -1;

    private String sourceURL;

    private String sourceTarget;

    private String destinationURL;

    private String destinationTarget;

    private Date serverDate = new Date();

    private Date clientDate;

    private String remoteAddress;

    private String agent;

    private String pageType;

    public String getPageType() {
        return pageType;
    }

    public void setPageType(String pageType) {
        this.pageType = pageType;
    }

    public String getAgent() {
        return agent;
    }

    public void setAgent(String agent) {
        this.agent = agent;
    }

    public String getRemoteAddress() {
        return remoteAddress;
    }

    public void setRemoteAddress(String inetAddress) {
        this.remoteAddress = inetAddress;
    }

    public Date getClientDate() {
        return clientDate;
    }

    public void setClientDate(Date clientDate) {
        this.clientDate = clientDate;
    }

    public Date getServerDate() {
        return serverDate;
    }

    public void setServerDate(Date date) {
        this.serverDate = date;
    }

    public String getSourceURL() {
        return sourceURL;
    }

    public void setSourceURL(String sourceURL) {
        this.sourceURL = sourceURL;
    }

    public String getSourceTarget() {
        return sourceTarget;
    }

    public void setSourceTarget(String sourceTarget) {
        this.sourceTarget = sourceTarget;
    }

    public String getDestinationURL() {
        return destinationURL;
    }

    public void setDestinationURL(String destinationURL) {
        this.destinationURL = destinationURL;
    }

    public String getDestinationTarget() {
        return destinationTarget;
    }

    public void setDestinationTarget(String destinationTarget) {
        this.destinationTarget = destinationTarget;
    }

    public long getNavigationStart() {
        return navigationStart;
    }

    public long getUnloadEventStart() {
        return unloadEventStart;
    }

    public long getUnloadEventEnd() {
        return unloadEventEnd;
    }

    public long getRedirectStart() {
        return redirectStart;
    }

    public long getRedirectEnd() {
        return redirectEnd;
    }

    public long getFetchStart() {
        return fetchStart;
    }

    public long getDomainLookupStart() {
        return domainLookupStart;
    }

    public long getDomainLookupEnd() {
        return domainLookupEnd;
    }

    public long getConnectStart() {
        return connectStart;
    }

    public long getConnectEnd() {
        return connectEnd;
    }

    public long getSecureConnectionStart() {
        return secureConnectionStart;
    }

    public long getRequestStart() {
        return requestStart;
    }

    public long getResponseStart() {
        return responseStart;
    }

    public long getResponseEnd() {
        return responseEnd;
    }

    public long getDomLoading() {
        return domLoading;
    }

    public long getDomInteractive() {
        return domInteractive;
    }

    public long getDomContentLoadedEventStart() {
        return domContentLoadedEventStart;
    }

    public long getDomContentLoadedEventEnd() {
        return domContentLoadedEventEnd;
    }

    public long getDomComplete() {
        return domComplete;
    }

    public long getLoadEventStart() {
        return loadEventStart;
    }

    public long getLoadEventEnd() {
        return loadEventEnd;
    }

    public void setNavigationStart(long navigationStart) {
        this.navigationStart = navigationStart;
    }

    public void setUnloadEventStart(long unloadEventStart) {
        this.unloadEventStart = unloadEventStart;
    }

    public void setUnloadEventEnd(long unloadEventEnd) {
        this.unloadEventEnd = unloadEventEnd;
    }

    public void setRedirectStart(long redirectStart) {
        this.redirectStart = redirectStart;
    }

    public void setRedirectEnd(long redirectEnd) {
        this.redirectEnd = redirectEnd;
    }

    public void setFetchStart(long fetchStart) {
        this.fetchStart = fetchStart;
    }

    public void setDomainLookupStart(long domainLookupStart) {
        this.domainLookupStart = domainLookupStart;
    }

    public void setDomainLookupEnd(long domainLookupEnd) {
        this.domainLookupEnd = domainLookupEnd;
    }

    public void setConnectStart(long connectStart) {
        this.connectStart = connectStart;
    }

    public void setConnectEnd(long connectEnd) {
        this.connectEnd = connectEnd;
    }

    public void setSecureConnectionStart(long secureConnectionStart) {
        this.secureConnectionStart = secureConnectionStart;
    }

    public void setRequestStart(long requestStart) {
        this.requestStart = requestStart;
    }

    public void setResponseStart(long responseStart) {
        this.responseStart = responseStart;
    }

    public void setResponseEnd(long responseEnd) {
        this.responseEnd = responseEnd;
    }

    public void setDomLoading(long domLoading) {
        this.domLoading = domLoading;
    }

    public void setDomInteractive(long domInteractive) {
        this.domInteractive = domInteractive;
    }

    public void setDomContentLoadedEventStart(long domContentLoadedEventStart) {
        this.domContentLoadedEventStart = domContentLoadedEventStart;
    }

    public void setDomContentLoadedEventEnd(long domContentLoadedEventEnd) {
        this.domContentLoadedEventEnd = domContentLoadedEventEnd;
    }

    public void setDomComplete(long domComplete) {
        this.domComplete = domComplete;
    }

    public void setLoadEventStart(long loadEventStart) {
        this.loadEventStart = loadEventStart;
    }

    public void setLoadEventEnd(long loadEventEnd) {
        this.loadEventEnd = loadEventEnd;
    }

    public long getEventStart() {
        return eventStart;
    }

    public void setEventStart(long eventStart) {
        this.eventStart = eventStart;
    }

    @Override
    public String toString() {
        String ct;
        String st;
        synchronized (dateFormat) {
            ct = ((clientDate != null) ? dateFormat.format(clientDate) : "NULL");
            st = ((serverDate != null) ? dateFormat.format(serverDate) : "NULL");
        }

        return "[PerformanceTiming clientDate='" + ct + "' serverDate='" + st
                + "' sourceURL='" + sourceURL + "' sourceTarget='"
                + sourceTarget + "' destinationURL='" + destinationURL
                + "' destinationTarget='" + destinationTarget + "' pageType='"
                + pageType + "' eventStart='" + format(eventStart)
                + "' navigationStart='" + navigationStart
                + "' unloadEventStart=" + format(unloadEventStart)
                + " unloadEventEnd=" + format(unloadEventEnd)
                + " redirectStart=" + format(redirectStart) + " redirectEnd="
                + format(redirectEnd) + " fetchStart=" + format(fetchStart)
                + " domainLookupStart=" + format(domainLookupStart)
                + " domainLookupEnd=" + format(domainLookupEnd)
                + " connectStart=" + format(connectStart) + " connectEnd="
                + format(connectEnd) + " secureConnectionStart="
                + format(secureConnectionStart) + " requestStart="
                + format(requestStart) + " responseStart="
                + format(responseStart) + " responseEnd=" + format(responseEnd)
                + " domLoading=" + format(domLoading) + " domInteractive="
                + format(domInteractive) + " domContentLoadedEventStart="
                + format(domContentLoadedEventStart)
                + " domContentLoadedEventEnd="
                + format(domContentLoadedEventEnd) + " domComplete="
                + format(domComplete) + " loadEventStart="
                + format(loadEventStart) + " loadEventEnd="
                + format(loadEventEnd) + " agent='" + agent + "']";
    }

    private String format(long time) {
        if (time < 0) {
            return "-";
        }
        if (navigationStart <= 0) {
            return String.valueOf(time);
        }
        time -= navigationStart;

        if (time > 0) {
            return "+" + time;
        }

        return String.valueOf(time);
    }

}
