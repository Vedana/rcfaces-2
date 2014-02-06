/*
 * $Id: URLContentRef.java,v 1.1 2013/11/13 12:53:25 jbmeslin Exp $
 */
package org.rcfaces.core.internal.repository;

import java.net.URL;

import org.rcfaces.core.internal.repository.IRepository.ICriteria;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.1 $ $Date: 2013/11/13 12:53:25 $
 */
public class URLContentRef extends AbstractContentRef {

    private final URL url;

    public URLContentRef(ICriteria criteria, URL url) {
        super(criteria);

        this.url = url;
    }

    public final URL getURL() {
        return url;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((url == null) ? 0 : url.hashCode());
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
        URLContentRef other = (URLContentRef) obj;
        if (url == null) {
            if (other.url != null)
                return false;
        } else if (!url.equals(other.url))
            return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(256);
        builder.append("[URLContentRef url='").append(url)
                .append("' criteria='").append(getCriteria()).append("']");
        return builder.toString();
    }

}
