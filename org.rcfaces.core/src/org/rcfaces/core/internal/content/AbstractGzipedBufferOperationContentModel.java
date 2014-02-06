/*
 * $Id: AbstractGzipedBufferOperationContentModel.java,v 1.1 2013/01/11 15:47:02 jbmeslin Exp $
 */
package org.rcfaces.core.internal.content;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.internal.contentStorage.GZipedResolvedContent;
import org.rcfaces.core.internal.contentStorage.IGzipedResolvedContent;
import org.rcfaces.core.internal.contentStorage.IResolvedContent;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.1 $ $Date: 2013/01/11 15:47:02 $
 */
public abstract class AbstractGzipedBufferOperationContentModel extends
        AbstractBufferOperationContentModel implements IGzipedResolvedContent {

    private static final long serialVersionUID = 6150821990622321197L;

    private static final Log LOG = LogFactory
            .getLog(AbstractGzipedBufferOperationContentModel.class);

    private final Object gzipedLock = new Object();

    private transient volatile GZipedResolvedContent gzipedResolvedContent;

    public AbstractGzipedBufferOperationContentModel(String resourceURL,
            String versionId, String operationId,
            String filterParametersToParse, IBufferOperation styleOperation,
            String specifiedResourceKey) {
        super(resourceURL, versionId, operationId, filterParametersToParse,
                styleOperation, specifiedResourceKey);
    }

    protected boolean isGzipSupported() {
        return true;
    }

    public IResolvedContent getGzipedContent() {
        if (isGzipSupported() == false) {
            return null;
        }

        synchronized (gzipedLock) {
            if (gzipedResolvedContent == null) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Create gziped content of '" + this + "'");
                }

                gzipedResolvedContent = new GZipedResolvedContent(this);
            }

            return gzipedResolvedContent;
        }
    }

}
