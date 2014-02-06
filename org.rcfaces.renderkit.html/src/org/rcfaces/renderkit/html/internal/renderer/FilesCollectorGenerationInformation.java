/*
 * $Id: FilesCollectorGenerationInformation.java,v 1.2 2013/01/11 15:45:02 jbmeslin Exp $
 */
package org.rcfaces.renderkit.html.internal.renderer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.internal.contentAccessor.BasicGenerationResourceInformation;
import org.rcfaces.renderkit.html.internal.util.FileItemSource;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/01/11 15:45:02 $
 */
public class FilesCollectorGenerationInformation extends
        BasicGenerationResourceInformation {

    private static final Log LOG = LogFactory
            .getLog(FilesCollectorGenerationInformation.class);

    private static final String FILE_ITEM_SOURCES = "org.rcfaces.renderkit.html.FILE_ITEM_SOURCES";

    public FilesCollectorGenerationInformation(FileItemSource sources[]) {
        setAttribute(FILE_ITEM_SOURCES, sources);
    }

    public FileItemSource[] listSources() {
        return (FileItemSource[]) getAttribute(FILE_ITEM_SOURCES);
    }
}
