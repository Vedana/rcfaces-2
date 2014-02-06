/*
 * $Id: DisableOperation.java,v 1.2 2013/11/14 10:55:22 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.internal.images.operation;

import java.awt.image.BufferedImage;
import java.awt.image.ByteLookupTable;
import java.awt.image.DataBuffer;
import java.awt.image.IndexColorModel;
import java.awt.image.LookupOp;
import java.awt.image.LookupTable;
import java.util.Map;

import org.rcfaces.core.image.operation.IDisableOperation;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/11/14 10:55:22 $
 */
public class DisableOperation extends GrayOperation implements
        IDisableOperation {
    private static final String REVISION = "$Revision: 1.2 $";

    private static byte[] disableTable = new byte[256];
    static { // Initialize the arrays
        for (int i = 0; i < 256; i++) {
            double v = Math.sqrt(i / 255.0) * 210;

            v += 45;

            if (v > 255) {
                v = 255;
            }
            disableTable[i] = (byte) v;
        }
    }

    private LookupOp disableOperation;

    protected LookupTable getLookupTable() {
        return new ByteLookupTable(0, disableTable);
    }

    public void configure(Map configuration) {
        super.configure(configuration);

        disableOperation = new LookupOp(getLookupTable(), null);
    }

    public BufferedImage filter(Map requestParameter, BufferedImage source,
            BufferedImage destination) {

        BufferedImage dest = super
                .filter(requestParameter, source, destination);

        return disableOperation.filter(dest, destination);
    }

    public IndexColorModel filter(Map<String, Object> requestParameter, IndexColorModel source,
            BufferedImage sourceImage) {
        IndexColorModel dest = super.filter(requestParameter, source,
                sourceImage);

        int mapSize = dest.getMapSize();

        int colorMap[] = new int[mapSize];
        dest.getRGBs(colorMap);

        int transparentPixel = source.getTransparentPixel();

        for (int i = 0; i < colorMap.length; i++) {
            if (i == transparentPixel) {
                continue;
            }

            int c = colorMap[i];

            int r = disableTable[(c >> 16) & 255] & 255;
            int g = disableTable[(c >> 8) & 255] & 255;
            int b = disableTable[c & 255] & 255;

            colorMap[i] = (r << 16) | (g << 8) | b | (c & 0xff000000);
        }

        return new IndexColorModel(8, colorMap.length, colorMap, 0, source
                .hasAlpha(), source.getTransparentPixel(), DataBuffer.TYPE_BYTE);
    }
}
