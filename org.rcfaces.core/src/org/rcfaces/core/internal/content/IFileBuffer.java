/*
 * $Id: IFileBuffer.java,v 1.1 2011/04/12 09:25:46 oeuillot Exp $
 */
package org.rcfaces.core.internal.content;

import java.io.IOException;
import java.io.InputStream;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:46 $
 */
public interface IFileBuffer {

    String getName();

    void initializeRedirection(String url) throws IOException;

    String getRedirection();

    int getSize();

    boolean isErrored();

    void setErrored();

    boolean isInitialized();

    InputStream getContent() throws IOException;

    String getContentType();

    long getModificationDate();

    String getHash();

    String getETag();
}
