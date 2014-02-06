/*
 * $Id: AbstractResolvedContent.java,v 1.3 2013/11/13 12:53:20 jbmeslin Exp $
 */
package org.rcfaces.core.internal.contentStorage;

import org.rcfaces.core.internal.lang.StringAppender;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:20 $
 */
public abstract class AbstractResolvedContent implements IResolvedContent {

    private static final long serialVersionUID = 8947615551118704798L;

    private boolean versioned;

    public String getETag() {
        return null;
    }

    public String getHash() {
        return null;
    }

    public int getLength() {
        return -1;
    }

    public long getModificationDate() {
        return -1;
    }

    public String getURLSuffix() {
        return null;
    }

    public boolean isErrored() {
        return false;
    }

    public boolean isProcessAtRequest() {
        return false;
    }

    public String getResourceKey() {
        return null;
    }

    public String getContentEncoding() {
        return null;
    }

    public boolean isVersioned() {
        return versioned;
    }

    public void setVersioned(boolean versioned) {
        this.versioned = versioned;
    }

    public void appendHashInformations(StringAppender sa) {
        long date = getModificationDate();
        if (date > 0) {
            sa.append(date);
        }

        int length = getLength();
        if (length > 0) {
            sa.append(length);
        }
    }

}
