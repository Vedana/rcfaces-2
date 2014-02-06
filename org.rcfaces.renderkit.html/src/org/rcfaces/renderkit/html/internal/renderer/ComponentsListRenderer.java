/*
 * $Id: ComponentsListRenderer.java,v 1.3 2013/11/13 12:53:29 jbmeslin Exp $
 */
package org.rcfaces.renderkit.html.internal.renderer;


import javax.faces.context.FacesContext;



import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.component.ComponentsListComponent;
import org.rcfaces.core.internal.renderkit.WriterException;
import org.rcfaces.renderkit.html.internal.IAccessibilityRoles;
import org.rcfaces.renderkit.html.internal.IHtmlWriter;
import org.rcfaces.renderkit.html.internal.ns.XhtmlNSAttributes;


/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:29 $
 */
@XhtmlNSAttributes({ "asyncRender", "interactiveShow", "filtred",
        "filterExpression", "rows", "rowCount", "first", "nc" })
public class ComponentsListRenderer extends AbstractComponentsListRenderer {

    private static final Log LOG = LogFactory
            .getLog(ComponentsListRenderer.class);

    protected void encodeListBegin(IHtmlWriter htmlWriter,
            ComponentsListComponent componentsListComponent,
            ListContext listContext) throws WriterException {

        FacesContext facesContext = htmlWriter.getComponentRenderContext()
                .getFacesContext();

        htmlWriter.startElement(IHtmlWriter.TABLE);

        htmlWriter.writeId(htmlWriter.getComponentRenderContext()
                .getComponentClientId() + "::table");

        htmlWriter.writeRole(IAccessibilityRoles.PRESENTATION);

        String w = componentsListComponent.getWidth(facesContext);
        if (w != null) {
            htmlWriter.writeWidth("100%");
        }

        String ccls = componentsListComponent.getColumnStyleClass(facesContext);
        String columnClasses[] = parseClasses(ccls);

        int columnNumber = listContext.getColumnNumber();
        if (columnNumber < 1) {
            columnNumber = 1;
        }

        if (columnNumber > 0) {
            for (int i = 0; i < columnNumber; i++) {
                htmlWriter.startElement(IHtmlWriter.COL);
                htmlWriter.writeWidth("1*");

                if (columnClasses == null || columnClasses.length < 1) {
                    htmlWriter.endElement(IHtmlWriter.COL);
                    continue;
                }
                int rs = i % columnClasses.length;

                htmlWriter.writeClass(columnClasses[rs]);
                htmlWriter.endElement(IHtmlWriter.COL);
            }
        }

        htmlWriter.startElement(IHtmlWriter.TBODY);
        htmlWriter.writeClass(getTBodyClassName(htmlWriter));
    }

    protected void encodeComponentBegin(IHtmlWriter htmlWriter,
            ComponentsListComponent componentsListComponent, int processed,
            int columnNumber, String[] rowClasses, String tdClass)
            throws WriterException {
       
                if ((processed % columnNumber) == 0) {
                    // Render the beginning of this row
                    htmlWriter.startElement(IHtmlWriter.TR);

            String rowId = htmlWriter.getComponentRenderContext()
                    .getRenderContext()
                            .getComponentClientId(componentsListComponent);
                    if (rowId != null) {
                        htmlWriter.writeId(rowId);
                    }

                    htmlWriter.writeAttributeNS("nc", true);

                    if (rowClasses.length > 0) {
                        int rs = (processed / columnNumber) % rowClasses.length;

                        htmlWriter.writeClass(rowClasses[rs]);
                    }
                }

                htmlWriter.startElement(IHtmlWriter.TD);
                htmlWriter.writeClass(tdClass);

                htmlWriter.writeln();

    }

    protected void encodeComponentEnd(IHtmlWriter htmlWriter,
            ComponentsListComponent componentsListComponent, int processed,
            int columnNumber, String[] rowClasses, String tdClass)
            throws WriterException {

                htmlWriter.endElement(IHtmlWriter.TD);

                if (((processed + 1) % columnNumber) == 0) {
                    htmlWriter.endElement(IHtmlWriter.TR);
                }
            }

    protected void encodeChildrenComponentEnd(IHtmlWriter htmlWriter,
            ComponentsListComponent componentsListComponent, int processed,
            int columnNumber, String[] rowClasses, String tdClass)
            throws WriterException {

            if ((processed % columnNumber) > 0) {
                for (; (processed % columnNumber) > 0; processed++) {
                    htmlWriter.startElement(IHtmlWriter.TD);
                    htmlWriter.endElement(IHtmlWriter.TD);
                }

                htmlWriter.endElement(IHtmlWriter.TR);
            }

     
    }

    protected void encodeListEnd(IHtmlWriter htmlWriter,
            ComponentsListComponent componentsListComponent)
            throws WriterException {
        htmlWriter.endElement(IHtmlWriter.TBODY);

        htmlWriter.endElement(IHtmlWriter.TABLE);
    }

}
