/*
 * $Id: ICOImageWriter.java,v 1.2 2013/11/14 10:55:22 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.internal.images.ico;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.IndexColorModel;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.Arrays;

import javax.imageio.IIOImage;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.spi.ImageWriterSpi;
import javax.imageio.stream.ImageOutputStream;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/11/14 10:55:22 $
 */
public class ICOImageWriter extends ImageWriter {

    private ImageOutputStream stream = null;

    public ICOImageWriter(ImageWriterSpi originator) {
        super(originator);
    }

    @Override
    public void setOutput(Object output) {
        super.setOutput(output); // validates output
        if (output != null) {
            if ((output instanceof ImageOutputStream) == false)
                throw new IllegalArgumentException(
                        "Output is not an ImageOutputStream.");
            this.stream = (ImageOutputStream) output;
            return;
        }

        this.stream = null;
    }

    @Override
    public IIOMetadata getDefaultStreamMetadata(ImageWriteParam param) {
        return null;
    }

    @Override
    public IIOMetadata getDefaultImageMetadata(ImageTypeSpecifier imageType,
            ImageWriteParam param) {
        ICOMetadata meta = new ICOMetadata();
        return meta;
    }

    @Override
    public IIOMetadata convertStreamMetadata(IIOMetadata inData,
            ImageWriteParam param) {
        return null;
    }

    @Override
    public IIOMetadata convertImageMetadata(IIOMetadata metadata,
            ImageTypeSpecifier type, ImageWriteParam param) {
        return null;
    }

    @Override
    public boolean canWriteRasters() {
        return true;
    }

    @Override
    public void write(IIOMetadata streamMetadata, IIOImage image,
            ImageWriteParam param) throws IOException {
        if (stream == null) {
            throw new IllegalStateException("Output has not been set.");
        }

        if (image == null) {
            throw new IllegalArgumentException("Image is null!");
        }

        clearAbortRequest();
        processImageStarted(0);
        if (param == null) {
            param = getDefaultWriteParam();
        }

        RenderedImage input = null;
        Raster inputRaster = null;
        boolean writeRaster = image.hasRaster();
        Rectangle sourceRegion = param.getSourceRegion();
        SampleModel sampleModel = null;

        if (writeRaster) {
            inputRaster = image.getRaster();
            sampleModel = inputRaster.getSampleModel();
        } else {
            input = image.getRenderedImage();
            sampleModel = input.getSampleModel();

            inputRaster = input.getData();
        }

        int type = sampleModel.getDataType();
        if (type < DataBuffer.TYPE_BYTE || type > DataBuffer.TYPE_INT
                || sampleModel.getNumBands() != 1
                || sampleModel.getSampleSize(0) != 8) {
            throw new IllegalArgumentException(
                    "Only integral single-band bilevel image is supported.");
        }

        if (sourceRegion == null) {
            sourceRegion = inputRaster.getBounds();

        } else {
            sourceRegion = sourceRegion.intersection(inputRaster.getBounds());
        }

        if (sourceRegion.isEmpty()) {
            throw new RuntimeException(
                    "The image region to be encoded is empty.");
        }

        int scaleX = param.getSourceXSubsampling();
        int scaleY = param.getSourceYSubsampling();
        int xOffset = param.getSubsamplingXOffset();
        int yOffset = param.getSubsamplingYOffset();

        sourceRegion.translate(xOffset, yOffset);
        sourceRegion.width -= xOffset;
        sourceRegion.height -= yOffset;

        int minX = sourceRegion.x / scaleX;
        int minY = sourceRegion.y / scaleY;
        int w = (sourceRegion.width + scaleX - 1) / scaleX;
        int h = (sourceRegion.height + scaleY - 1) / scaleY;

        Rectangle destinationRegion = new Rectangle(minX, minY, w, h);
        sampleModel = sampleModel.createCompatibleSampleModel(w, h);

        SampleModel destSM = sampleModel;

        // If the data are not formatted nominally then reformat.
        if (sampleModel.getDataType() != DataBuffer.TYPE_BYTE
                || !(sampleModel instanceof MultiPixelPackedSampleModel)
                || ((MultiPixelPackedSampleModel) sampleModel)
                        .getDataBitOffset() != 0) {
            destSM = new MultiPixelPackedSampleModel(DataBuffer.TYPE_BYTE, w,
                    h, 8, w, 0);
        }

        if (!destinationRegion.equals(sourceRegion)) {
            if (scaleX == 1 && scaleY == 1) {
                inputRaster = inputRaster.createChild(inputRaster.getMinX(),
                        inputRaster.getMinY(), w, h, minX, minY, null);
            } else {
                WritableRaster ras = Raster.createWritableRaster(destSM,
                        new Point(minX, minY));

                // @XXX TODO
                byte[] data = ((DataBufferByte) ras.getDataBuffer()).getData();

                for (int j = minY, y = sourceRegion.y, k = 0; j < minY + h; j++, y += scaleY) {

                    for (int i = 0, x = sourceRegion.x; i < w; i++, x += scaleX) {
                        int v = inputRaster.getSample(x, y, 0);
                        data[k + (i >> 3)] |= v << (7 - (i & 7));
                    }
                    k += w + 7 >> 3;
                }
                inputRaster = ras;
            }
        }

        // If the data are not formatted nominally then reformat.
        if (!destSM.equals(inputRaster.getSampleModel())) {
            WritableRaster raster = Raster.createWritableRaster(destSM,
                    new Point(inputRaster.getMinX(), inputRaster.getMinY()));
            raster.setRect(inputRaster);
            inputRaster = raster;
        }

        IndexColorModel icm = (IndexColorModel) input.getColorModel();

        int bytesPerRow;
        if (w <= 16) {
            bytesPerRow = 16;
        } else if (w <= 32) {
            bytesPerRow = 32;
        } else {
            bytesPerRow = 64;
        }

        stream.setByteOrder(ByteOrder.LITTLE_ENDIAN);

        stream.writeShort(0);

        stream.writeShort(1);

        stream.writeShort(1); // nombre d'images

        if (true) { // Entries
            stream.writeByte(bytesPerRow);
            stream.writeByte(bytesPerRow);
            stream.writeByte(0); // 256 couleurs
            stream.writeByte(0); // reserved
            stream.writeShort(1); // reserved
            stream.writeShort(8); // 8 bits
            stream.writeInt(40 + bytesPerRow * bytesPerRow + bytesPerRow / 8
                    * bytesPerRow + 256 * 4);

            stream.writeInt(6 + 16);
        }

        if (true) { // InfoHeader
            stream.writeInt(40);
            stream.writeInt(bytesPerRow);
            stream.writeInt(bytesPerRow * 2);
            stream.writeShort(1); // nb planes
            stream.writeShort(8); // bits per pixel
            stream.writeInt(0); // Compression
            stream.writeInt(0); // size of image (0=uncompressed)
            stream.writeInt(0); // reserved 1
            stream.writeInt(0); // reserved 2
            stream.writeInt(0); // reserved 3
            stream.writeInt(0); // reserved 4
        }

        int colors[] = new int[icm.getMapSize()];
        icm.getRGBs(colors);

        int trans = icm.getTransparentPixel();

        for (int i = 0; i < 256; i++) {
            if (i >= colors.length) {
                stream.writeInt(0);
                continue;
            }

            int color = colors[i];

            stream.writeByte(color & 0xff);
            stream.writeByte((color & 0xff00) >> 8);
            stream.writeByte((color & 0xff0000) >> 16);

            if (false && i == trans) {
                stream.writeByte(255);
                continue;
            }
            stream.writeByte(0);
        }

        byte[] bdata = ((DataBufferByte) inputRaster.getDataBuffer()).getData();

        int lineStride = ((MultiPixelPackedSampleModel) destSM)
                .getScanlineStride();

        int index = bdata.length - lineStride;

        byte transparent[] = new byte[bytesPerRow];
        if (trans >= 0) {
            Arrays.fill(transparent, (byte) trans);
        }

        int storeWidth = bytesPerRow;
        if (storeWidth > w) {
            storeWidth = w;
        }

        for (int y = bytesPerRow; y > 0; y--) {
            if (y > h) {
                stream.write(transparent);
                continue;
            }

            stream.write(bdata, index, storeWidth);
            if (storeWidth < bytesPerRow) {
                stream.write(transparent, 0, bytesPerRow - storeWidth);
            }
            index -= lineStride;
        }

        processImageProgress(50);

        Arrays.fill(transparent, (byte) 255);

        int paddingSize = ((storeWidth + 31) / 32) * 32;

        if (trans >= 0) {
            index = bdata.length - lineStride;

            for (int y = bytesPerRow; y > 0; y--) {
                if (y > h) {
                    stream.write(transparent, 0, paddingSize / 8);
                    continue;
                }

                int idx = index;
                int x = 0;
                int mask = 0;
                for (x = 0; x < storeWidth; x++) {
                    if (x > 0 && (x % 8) == 0) {
                        stream.writeByte(mask);
                        mask = 0;
                    }
                    mask <<= 1;
                    if (bdata[idx++] != trans) {
                        continue;
                    }

                    mask |= 1;
                }

                if ((x % 8) > 0) {
                    int rest = 8 - (x % 8);
                    mask <<= rest;

                    mask |= (255 >> (x % 8));

                    x += rest;
                }
                stream.writeByte(mask);

                if (x < paddingSize) {
                    stream.write(transparent, 0, (paddingSize - x) / 8);
                }

                index -= lineStride;
            }
        } else {
            for (int y = 0; y < bytesPerRow; y++) {
                stream.write(transparent, 0, paddingSize / 8);
            }
        }

        if (abortRequested()) {
            processWriteAborted();
            return;
        }

        processImageComplete();
        stream.flushBefore(stream.getStreamPosition());
    }

    public void reset() {
        super.reset();
        stream = null;
    }
}
