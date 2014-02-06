/*
 * $Id: ClientBrowserImpl.java,v 1.2 2013/11/13 12:53:32 jbmeslin Exp $
 */
package org.rcfaces.renderkit.html.internal.agent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/11/13 12:53:32 $
 */
public class ClientBrowserImpl extends BasicUserAgent implements IClientBrowser {

    private static final Log LOG = LogFactory.getLog(ClientBrowserImpl.class);

    private final String userAgent;

    private final String browserId;

    private final Boolean isMobileVersion;

    private final String browserIdVersion;

    ClientBrowserImpl(String userAgent, BrowserType browserType,
            Integer majorVersion, Integer minorVersion, Integer releaseVersion,
            String browserId, Boolean isMobileVersion) {

        this.userAgent = userAgent;
        this.type = browserType;
        this.majorVersion = majorVersion;
        this.minorVersion = minorVersion;
        this.releaseVersion = releaseVersion;
        this.browserId = browserId;
        this.isMobileVersion = isMobileVersion;

        StringBuilder sb = new StringBuilder(128);
        sb.append(browserId);
        if (majorVersion != null) {
            sb.append('.').append(majorVersion);

            if (minorVersion != null) {
                sb.append('.').append(minorVersion);

                if (releaseVersion != null) {
                    sb.append('.').append(releaseVersion);
                }
            }
        }
        browserIdVersion = sb.toString();
    }

    public String getUserAgent() {
        return userAgent;
    }

    public String getBrowserId() {
        return browserId;
    }

    public String getBrowserIdAndVersion() {
        return browserIdVersion;
    }

    public Boolean isMobileVersion() {
        return isMobileVersion;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result
                + ((browserId == null) ? 0 : browserId.hashCode());
        result = prime
                * result
                + ((browserIdVersion == null) ? 0 : browserIdVersion.hashCode());
        result = prime * result
                + ((isMobileVersion == null) ? 0 : isMobileVersion.hashCode());
        result = prime * result
                + ((userAgent == null) ? 0 : userAgent.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        ClientBrowserImpl other = (ClientBrowserImpl) obj;
        if (browserId == null) {
            if (other.browserId != null)
                return false;
        } else if (!browserId.equals(other.browserId))
            return false;
        if (browserIdVersion == null) {
            if (other.browserIdVersion != null)
                return false;
        } else if (!browserIdVersion.equals(other.browserIdVersion))
            return false;
        if (isMobileVersion == null) {
            if (other.isMobileVersion != null)
                return false;
        } else if (!isMobileVersion.equals(other.isMobileVersion))
            return false;
        if (userAgent == null) {
            if (other.userAgent != null)
                return false;
        } else if (!userAgent.equals(other.userAgent))
            return false;
        return true;
    }

    public String toString() {
        return "[ClientBrowserImpl browserId='" + browserId + "', browserType="
                + getBrowserId() + ", majorVersion=" + getMajorVersion()
                + ", minorVersion=" + getMinorVersion() + ", releaseVersion="
                + getReleaseVersion() + ", isMobileVersion=" + isMobileVersion
                + " userAgent='" + userAgent + "']";
    }

}
