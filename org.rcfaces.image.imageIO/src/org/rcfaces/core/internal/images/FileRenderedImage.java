/*
 * $Id: FileRenderedImage.java,v 1.2 2013/11/14 10:55:22 jbmeslin Exp $
 */
package org.rcfaces.core.internal.images;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.IndexColorModel;
import java.awt.image.PixelInterleavedSampleModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.io.IOException;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.internal.content.FileBuffer;
import org.rcfaces.core.internal.lang.ByteBufferOutputStream;
import org.rcfaces.core.internal.resource.IResourceLoaderFactory.IResourceLoader;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/11/14 10:55:22 $
 */
class FileRenderedImage extends FileBuffer implements IBufferedImage {
    private static final Log LOG = LogFactory.getLog(FileRenderedImage.class);

    private static final int INITIAL_BUFFER_SIZE = 1024 * 8;

    public FileRenderedImage(String imageName) {
        super(imageName);
    }

    public void initialize(IResourceLoader imageDownloader, String contentType,
            RenderedImage renderedImage, ImageWriter imageWriter,
            ImageWriteParam imageWriteParam, int destImageType,
            long lastModified) throws IOException {

        int size = INITIAL_BUFFER_SIZE;
        long originalSize = imageDownloader.getContentLength();
        if (originalSize > 0 && originalSize < INITIAL_BUFFER_SIZE) {
            size = (int) originalSize + 256;
        }

        ByteBufferOutputStream bous = new ByteBufferOutputStream(size);

        ImageOutputStream out = ImageIO.createImageOutputStream(bous);

        imageWriter.setOutput(out);

        RenderedImage image = renderedImage;
        if (destImageType == BufferedImage.TYPE_BYTE_INDEXED) {
            if ((renderedImage.getColorModel() instanceof IndexColorModel) == false) {

                if (LOG.isTraceEnabled()) {
                    LOG.trace("Dither imageName '" + getName() + "' ...");
                }

                image = ditherImage(image);
            }

        } else if (destImageType != ((BufferedImage) renderedImage).getType()) {
            if (LOG.isTraceEnabled()) {
                LOG.trace("Convert imageName '" + getName() + "' from type "
                        + ((BufferedImage) renderedImage).getType()
                        + " to type " + destImageType);
            }

            BufferedImage bimage = new BufferedImage(image.getWidth(),
                    image.getHeight(), destImageType);

            Graphics2D g = bimage.createGraphics();
            try {
                g.drawImage((Image) image, 0, 0, null);

            } finally {
                g.dispose();
            }

            image = bimage;
        }

        try {
            imageWriter.write(null, new IIOImage(image, null, null),
                    imageWriteParam);

        } catch (IOException ex) {
            LOG.error("Can not encode image into temp file ! (imageName="
                    + getName() + ")", ex);

            throw ex;
        }

        imageWriter.dispose();

        out.close();

        byte buffer[] = bous.toByteArray();

        super.initialize(contentType, buffer, lastModified);
    }

    private RenderedImage ditherImage(RenderedImage image) {
        return ditherImage(image, 0xff);
    }

    private RenderedImage ditherImage(RenderedImage image, int mask) {
        int w = image.getWidth();
        int h = image.getHeight();

        int colors[] = new int[256];

        int w4 = w * 4;
        int buffer[] = new int[w4];

        byte imageBuffer[] = new byte[w * h];

        int cnt;
        int trans;
        int imageBufferPos;

        for (;;) {
            SampleModel sampleModel = image.getSampleModel();
            DataBuffer dataBuffer = image.getData().getDataBuffer();

            cnt = 0;
            trans = -1;
            imageBufferPos = 0;

            for (int y = 0; y < h; y++) {
                sampleModel.getPixels(0, y, w, 1, buffer, dataBuffer);

                next_pixel: for (int x = 0; x < w4;) {
                    int r = buffer[x++];
                    int g = buffer[x++];
                    int b = buffer[x++];
                    int t = buffer[x++];

                    if (t == 0) {
                        if (trans < 0) {
                            trans = cnt++;
                            if (trans > 255) {
                                break;
                            }
                        }

                        imageBuffer[imageBufferPos++] = (byte) trans;
                        continue next_pixel;
                    }

                    if (mask < 255) {
                        r &= mask;
                        g &= mask;
                        b &= mask;
                    }

                    int c = (r << 16) | (g << 8) | b;
                    for (int i = 0; i < cnt; i++) {
                        if (i != trans && colors[i] == c) {
                            imageBuffer[imageBufferPos++] = (byte) i;
                            continue next_pixel;
                        }
                    }

                    int i = (cnt++);
                    if (i > 255) {
                        break;
                    }
                    colors[i] = c;
                    imageBuffer[imageBufferPos++] = (byte) i;
                }

                if (cnt > 256) {
                    break;
                }
            }

            if (cnt <= 256) {
                break;
            }

            float coef = imageBufferPos / (float) imageBuffer.length;

            if (LOG.isTraceEnabled()) {
                LOG.trace("Dither image: too many color=" + cnt
                        + " for mask 0x" + Integer.toHexString(mask) + ". ("
                        + ((int) (coef * 100)) + "%)");
            }

            mask = (mask << 1) & 0xff;

            if (coef < 0.50f) {
                mask = (mask << 1) & 0xff;
            }
        }

        if (LOG.isTraceEnabled()) {
            LOG.trace("Dither image: colorCount=" + cnt + " transparent="
                    + trans + " mask=0x" + Integer.toHexString(mask));
        }

        BufferedImage bufferedImage;
        if (cnt <= 2) {
            cnt = 2;
        } else if (cnt <= 4) {
            cnt = 4;
        } else if (cnt <= 16) {
            cnt = 16;
        } else {
            cnt = 256;
        }

        if (trans >= 0) {
            colors[trans] = (1 << 16) | (2 << 8) | (3 << 0);
        }

        IndexColorModel indexColorModel = new IndexColorModel(8, cnt, colors,
                0, false, trans, DataBuffer.TYPE_BYTE);

        int bandOffsets[] = new int[1];
        PixelInterleavedSampleModel destSampleModel = new PixelInterleavedSampleModel(
                DataBuffer.TYPE_BYTE, w, h, 1, w, bandOffsets);

        WritableRaster raster = Raster.createWritableRaster(destSampleModel,
                new DataBufferByte(imageBuffer, imageBufferPos), null);

        bufferedImage = new BufferedImage(indexColorModel, raster, false, null);

        return bufferedImage;
    }
}
