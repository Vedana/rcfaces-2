/*
 * $Id: ComponentsListULRenderer.java,v 1.1 2013/11/13 12:53:30 jbmeslin Exp $
 */
package org.rcfaces.renderkit.html.internal.renderer;

import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.component.ComponentsListComponent;
import org.rcfaces.core.internal.renderkit.WriterException;
import org.rcfaces.renderkit.html.internal.IHtmlWriter;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.1 $ $Date: 2013/11/13 12:53:30 $
 */
public class ComponentsListULRenderer extends AbstractComponentsListRenderer {

    private static final Log LOG = LogFactory
            .getLog(ComponentsListULRenderer.class);

    protected void encodeListBegin(IHtmlWriter htmlWriter,
            ComponentsListComponent componentsListComponent,
            ListContext listContext) throws WriterException {

        FacesContext facesContext = htmlWriter.getComponentRenderContext()
                .getFacesContext();

        htmlWriter.startElement(IHtmlWriter.UL);

        htmlWriter.writeId(htmlWriter.getComponentRenderContext()
                .getComponentClientId() + "::table");

        String w = componentsListComponent.getWidth(facesContext);
        if (w != null) {
            htmlWriter.writeStyle().writeWidth("100%");
        }

        htmlWriter.writeClass(getTBodyClassName(htmlWriter));
    }

    protected void encodeComponentBegin(IHtmlWriter htmlWriter,
            ComponentsListComponent componentsListComponent, int processed,
            int columnNumber, String[] rowClasses, String tdClass)
            throws WriterException {

        htmlWriter.startElement(IHtmlWriter.LI);
        htmlWriter.writeAttributeNS("nc", true);
        htmlWriter.writeClass(tdClass);

        htmlWriter.writeln();

    }

    protected void encodeComponentEnd(IHtmlWriter htmlWriter,
            ComponentsListComponent componentsListComponent, int processed,
            int columnNumber, String[] rowClasses, String tdClass)
            throws WriterException {

        htmlWriter.endElement(IHtmlWriter.LI);
    }

    protected void encodeChildrenComponentEnd(IHtmlWriter htmlWriter,
            ComponentsListComponent componentsListComponent, int processed,
            int columnNumber, String[] rowClasses, String tdClass)
            throws WriterException {
    }

    protected void encodeListEnd(IHtmlWriter htmlWriter,
            ComponentsListComponent componentsListComponent)
            throws WriterException {
        htmlWriter.endElement(IHtmlWriter.UL);
    }

}
