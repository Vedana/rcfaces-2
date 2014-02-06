/*
 * $Id: IResolvedContent.java,v 1.2 2013/11/13 12:53:20 jbmeslin Exp $
 */
package org.rcfaces.core.internal.contentStorage;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import org.rcfaces.core.internal.lang.StringAppender;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/11/13 12:53:20 $
 */
public interface IResolvedContent extends IResourceKey, Serializable {

    boolean isErrored();

    boolean isProcessAtRequest();

    int getLength();

    long getModificationDate();

    String getURLSuffix();

    String getContentType();

    String getContentEncoding();

    InputStream getInputStream() throws IOException;

    String getHash();

    String getETag();

    boolean isVersioned();

    void setVersioned(boolean versioned);

    void appendHashInformations(StringAppender sa);
}