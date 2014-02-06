/*
 * $Id: IComponentTreeRenderProcessor.java,v 1.1 2011/04/12 09:25:49 oeuillot Exp $
 */
package org.rcfaces.core.internal.renderkit.tools;

import java.io.IOException;
import java.io.Writer;

import javax.faces.component.UIComponent;

import org.rcfaces.core.internal.renderkit.WriterException;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:49 $
 */
public interface IComponentTreeRenderProcessor {

    boolean hasSaveStateFieldMarker(String content);

    void writeFilteredContent(Writer writer, String content) throws IOException;

    void encodeChildrenRecursive(UIComponent component, String componentId)
            throws WriterException;
}
