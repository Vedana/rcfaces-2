/*
 * $Id: IBufferedImage.java,v 1.2 2013/11/14 10:55:22 jbmeslin Exp $
 */
package org.rcfaces.core.internal.images;

import java.awt.image.RenderedImage;
import java.io.IOException;

import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;

import org.rcfaces.core.internal.content.IFileBuffer;
import org.rcfaces.core.internal.resource.IResourceLoaderFactory.IResourceLoader;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/11/14 10:55:22 $
 */

public interface IBufferedImage extends IFileBuffer {
    void initialize(IResourceLoader imageDownloader, String contentType,
            RenderedImage renderedImage, ImageWriter imageWriter,
            ImageWriteParam imageWriterParam, int imageType, long lastModified)
            throws IOException;
}