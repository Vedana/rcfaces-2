/*
 * $Id: AudioDescriptionTools.java,v 1.1 2013/11/13 12:53:32 jbmeslin Exp $
 */
package org.rcfaces.renderkit.html.internal.util;

import javax.faces.component.UIComponent;

import org.rcfaces.core.component.capability.IAudioDescriptionCapability;
import org.rcfaces.core.internal.renderkit.WriterException;
import org.rcfaces.renderkit.html.internal.IHtmlElements;
import org.rcfaces.renderkit.html.internal.IHtmlWriter;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.1 $ $Date: 2013/11/13 12:53:32 $
 */
public class AudioDescriptionTools {
    private static final String AUDIO_DESCRIPTION_ELEMENT_NAME = IHtmlElements.SPAN;

    private static final String AUDIO_DESCRIPTION_CLASS_NAME = "f_audioDescription";

    private static final String AUDIO_DESCRIPTION_TYPE_ATTRIBUTE = "audioDescriptionType";

    private static final String AUDIO_DESCRIPTION_FOR_ATTRIBUTE = "audioDescriptionFor";

    public static void writeAudioDescription(IHtmlWriter htmlWriter)
            throws WriterException {
        UIComponent component = htmlWriter.getComponentRenderContext()
                .getComponent();
        if ((component instanceof IAudioDescriptionCapability) == false) {
            return;
        }

        IAudioDescriptionCapability audioDescriptionCapability = (IAudioDescriptionCapability) component;

        String audioDescription = audioDescriptionCapability
                .getAudioDescription();
        if (audioDescription == null || audioDescription.length() < 1) {
            return;
        }

        writeAudioDescription(htmlWriter, audioDescription, null, htmlWriter
                .getComponentRenderContext().getComponentClientId());
    }

    public static void writeAudioDescription(IHtmlWriter htmlWriter,
            String message, String type, String forClientId)
            throws WriterException {

        if (message == null || message.length() == 0) {
            return;
        }

        htmlWriter.startElement(AUDIO_DESCRIPTION_ELEMENT_NAME);

        htmlWriter.writeClass(AUDIO_DESCRIPTION_CLASS_NAME);

        if (type != null) {
            htmlWriter.writeAttribute(AUDIO_DESCRIPTION_TYPE_ATTRIBUTE, type);
        }

        if (forClientId != null) {
            htmlWriter.writeAttribute(AUDIO_DESCRIPTION_FOR_ATTRIBUTE,
                    forClientId);
        }

        htmlWriter.writeText(message);

        htmlWriter.endElement(AUDIO_DESCRIPTION_ELEMENT_NAME);
    }
}
