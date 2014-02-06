/*
 * $Id: BasicUserAgent.java,v 1.1 2013/11/13 12:53:32 jbmeslin Exp $
 */
package org.rcfaces.renderkit.html.internal.agent;

import java.util.HashMap;
import java.util.Map;

import javax.faces.component.StateHolder;
import javax.faces.context.FacesContext;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.1 $ $Date: 2013/11/13 12:53:32 $
 */
public class BasicUserAgent implements IUserAgent, StateHolder {

    private static final Map<BrowserType, BasicUserAgent> cache = new HashMap<IUserAgent.BrowserType, BasicUserAgent>(
            16);
    static {
        for (BrowserType browserType : BrowserType.values()) {
            cache.put(browserType, new BasicUserAgent(browserType, null, null,
                    null));
        }
    }

    protected BrowserType type;

    protected Integer majorVersion;

    protected Integer minorVersion;

    protected Integer releaseVersion;

    private boolean transientState;

    public BasicUserAgent() {
    }

    public BasicUserAgent(BrowserType type, Integer majorVersion,
            Integer minorVersion, Integer releaseVersion) {
        this.type = type;
        this.majorVersion = majorVersion;
        this.minorVersion = minorVersion;
        this.releaseVersion = releaseVersion;
    }

    public final BrowserType getType() {
        return type;
    }

    protected final boolean versionEquals(IUserAgent proposal) {

        if (majorVersion != null) {
            if (proposal.getMajorVersion() == null) {
                return false;
            }

            if (majorVersion.intValue() != proposal.getMajorVersion()
                    .intValue()) {
                return false;
            }
        }

        if (minorVersion != null) {
            if (proposal.getMinorVersion() == null) {
                return false;
            }

            if (minorVersion.intValue() != proposal.getMinorVersion()
                    .intValue()) {
                return false;
            }
        }

        if (releaseVersion != null) {
            if (proposal.getReleaseVersion() == null) {
                return false;
            }

            if (releaseVersion.intValue() != proposal.getReleaseVersion()
                    .intValue()) {
                return false;
            }
        }

        return true;
    }

    public final boolean equalsType(IUserAgent userAgent) {
        if (userAgent == null) {
            return false;
        }

        if (type == userAgent.getBrowserType()) {
            return true;
        }

        return type != null && type.equals(userAgent.getBrowserType());
    }

    public final BrowserType getBrowserType() {
        return type;
    }

    public final Integer getMajorVersion() {
        return majorVersion;
    }

    public final Integer getMinorVersion() {
        return minorVersion;
    }

    public final Integer getReleaseVersion() {
        return releaseVersion;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("[BasicUserAgent type='").append(type)
                .append("' majorVersion='").append(majorVersion)
                .append("' minorVersion='").append(minorVersion)
                .append("' releaseVersion='").append(releaseVersion)
                .append("']");
        return builder.toString();
    }

    public Object saveState(FacesContext context) {
        Object[] ret = new Object[4];

        ret[0] = majorVersion;
        ret[1] = minorVersion;
        ret[2] = releaseVersion;
        ret[3] = type.name();

        return ret;
    }

    public void setTransient(boolean newTransientValue) {
        this.transientState = newTransientValue;
    }

    public void restoreState(FacesContext context, Object state) {
        Object[] array = (Object[]) state;

        majorVersion = (Integer) array[0];
        minorVersion = (Integer) array[1];
        releaseVersion = (Integer) array[2];
        type = BrowserType.valueOf((String) array[3]);
    }

    public boolean isTransient() {
        return transientState;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((majorVersion == null) ? 0 : majorVersion.hashCode());
        result = prime * result
                + ((minorVersion == null) ? 0 : minorVersion.hashCode());
        result = prime * result
                + ((releaseVersion == null) ? 0 : releaseVersion.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        BasicUserAgent other = (BasicUserAgent) obj;
        if (majorVersion == null) {
            if (other.majorVersion != null)
                return false;
        } else if (!majorVersion.equals(other.majorVersion))
            return false;
        if (minorVersion == null) {
            if (other.minorVersion != null)
                return false;
        } else if (!minorVersion.equals(other.minorVersion))
            return false;
        if (releaseVersion == null) {
            if (other.releaseVersion != null)
                return false;
        } else if (!releaseVersion.equals(other.releaseVersion))
            return false;
        if (type != other.type)
            return false;
        return true;
    }

    public IUserAgent reduce() {
        if (getClass() == BasicUserAgent.class) {
            return this;
        }

        return new BasicUserAgent(type, majorVersion, minorVersion,
                releaseVersion);
    }

    public IUserAgent reduceByBrowserType() {
        return cache.get(type);
    }

}
