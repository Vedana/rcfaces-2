package org.rcfaces.renderkit.svg.internal.image;

import java.awt.image.BufferedImage;

import org.apache.batik.transcoder.TranscoderOutput;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/11/13 15:52:39 $
 */
class AdapterTranscoderOuput extends TranscoderOutput {

    private BufferedImage bufferedImage;

    public AdapterTranscoderOuput() {
    }

    public final BufferedImage getBufferedImage() {
        return bufferedImage;
    }

    public final void setBufferedImage(BufferedImage bufferedImage) {
        this.bufferedImage = bufferedImage;
    }

}