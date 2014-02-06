/*
 * $Id: ChartContentModel.java,v 1.1 2011/04/12 09:26:25 oeuillot Exp $
 */
package org.rcfaces.jfreechart.renderer;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import javax.faces.FacesException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jfree.ui.Drawable;
import org.rcfaces.core.image.ImageContentModel;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:26:25 $
 */
public class ChartContentModel extends ImageContentModel {

    private static final Log LOG = LogFactory.getLog(ChartContentModel.class);

    @Override
    protected BufferedImage getBufferedImage() {

        GenerationChartInformation chartInformation = (GenerationChartInformation) generationInformation;

        BufferedImage bufferedImage = new BufferedImage(chartInformation
                .getImageWidth(), chartInformation.getImageHeight(),
                BufferedImage.TYPE_INT_RGB);

        Drawable drawable = chartInformation.getDrawable();
        if (drawable == null) {
            throw new FacesException("Drawable is NULL !");
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("Start generating jfreechart '" + drawable + "'");
        }

        long t = System.currentTimeMillis();

        Graphics2D g = bufferedImage.createGraphics();

        try {
            drawable.draw(g, new Rectangle(0, 0, bufferedImage.getWidth(),
                    bufferedImage.getHeight()));

        } catch (Throwable th) {
            LOG.error("Can not draw chart", th);

            throw new FacesException("Can not draw chart", th);

        } finally {
            g.dispose();
        }

        t = System.currentTimeMillis() - t;
        if (LOG.isDebugEnabled()) {
            LOG.debug("Jfreechart generated '" + drawable + "' (" + t + " ms)");
        }

        return bufferedImage;
    }
}
