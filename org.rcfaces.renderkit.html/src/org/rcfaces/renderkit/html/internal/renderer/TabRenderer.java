/*
 * $Id: TabRenderer.java,v 1.2 2013/01/11 15:45:01 jbmeslin Exp $
 */
package org.rcfaces.renderkit.html.internal.renderer;

import javax.faces.component.UIComponent;
import javax.faces.component.UINamingContainer;
import javax.faces.context.FacesContext;

import org.rcfaces.core.component.CardComponent;
import org.rcfaces.core.component.TabComponent;
import org.rcfaces.core.component.familly.IContentAccessors;
import org.rcfaces.core.event.PropertyChangeEvent;
import org.rcfaces.core.internal.component.IImageAccessors;
import org.rcfaces.core.internal.component.IStatesImageAccessors;
import org.rcfaces.core.internal.component.Properties;
import org.rcfaces.core.internal.contentAccessor.IContentAccessor;
import org.rcfaces.core.internal.renderkit.IComponentData;
import org.rcfaces.core.internal.renderkit.IRenderContext;
import org.rcfaces.core.internal.renderkit.IRequestContext;
import org.rcfaces.core.internal.renderkit.WriterException;
import org.rcfaces.core.internal.util.ParamUtils;
import org.rcfaces.renderkit.html.internal.IHtmlElements;
import org.rcfaces.renderkit.html.internal.IHtmlRenderContext;
import org.rcfaces.renderkit.html.internal.IHtmlWriter;
import org.rcfaces.renderkit.html.internal.IJavaScriptWriter;
import org.rcfaces.renderkit.html.internal.IObjectLiteralWriter;
import org.rcfaces.renderkit.html.internal.ISubInputClientIdRenderer;
import org.rcfaces.renderkit.html.internal.JavaScriptClasses;
import org.rcfaces.renderkit.html.internal.util.HeadingTools;
import org.rcfaces.renderkit.html.internal.ns.XhtmlNSAttributes;

/**
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/01/11 15:45:01 $
 */
@XhtmlNSAttributes({ "tabbedPaneId", "value", "selected", "text", "accessKey",
        "disabled", "imageURL", "disabledImageURL", "hoverImageURL",
        "selectedImageURL" })
public class TabRenderer extends CardRenderer implements
        ISubInputClientIdRenderer {

    private static final String TAB = "_tab";

    public static final String INPUT_ID_SUFFIX = ""
            + UINamingContainer.SEPARATOR_CHAR
            + UINamingContainer.SEPARATOR_CHAR + "input";

    protected void writeCardAttributes(IHtmlWriter htmlWriter)
            throws WriterException {
        super.writeCardAttributes(htmlWriter);

        IHtmlRenderContext htmlRenderContext = htmlWriter
                .getHtmlComponentRenderContext().getHtmlRenderContext();

        FacesContext facesContext = htmlRenderContext.getFacesContext();

        TabComponent tab = (TabComponent) htmlWriter
                .getComponentRenderContext().getComponent();

        if (CardBoxRenderer.isCardBoxJSF12_Generation(htmlRenderContext)) {

            htmlWriter.writeAttributeNS("tabbedPaneId", tab.getTabbedPane()
                    .getClientId(facesContext));

            Object value = tab.getValue();
            String clientValue = null;
            if (value instanceof String) {
                clientValue = (String) value;

            } else if (value != null) {
                clientValue = convertValue(facesContext, tab, value);
            }

            if (clientValue != null) {
                htmlWriter.writeAttributeNS("value", clientValue);
            }

            if (isCardSelected(tab)) {
                htmlWriter.writeAttributeNS("selected", true);
            }

            String text = tab.getText(facesContext);
            if (text != null) {
                text = ParamUtils.formatMessage(tab, text);

                htmlWriter.writeAttributeNS("text", text);
            }

            String accessKey = tab.getAccessKey(facesContext);
            if (accessKey != null) {
                htmlWriter.writeAttributeNS("accessKey", accessKey);
            }

            if (tab.isDisabled(facesContext)) {
                htmlWriter.writeAttributeNS("disabled", true);
            }

            IContentAccessors contentAccessors = tab
                    .getImageAccessors(facesContext);

            if (contentAccessors instanceof IImageAccessors) {
                IImageAccessors imageAccessors = (IImageAccessors) contentAccessors;

                IContentAccessor contentAccessor = imageAccessors
                        .getImageAccessor();

                if (contentAccessor != null) {
                    htmlWriter.writeURIAttributeNS("imageURL", contentAccessor
                            .resolveURL(facesContext, null, null));
                }

                if (imageAccessors instanceof IStatesImageAccessors) {
                    IStatesImageAccessors statesImageAccessors = (IStatesImageAccessors) imageAccessors;

                    contentAccessor = statesImageAccessors
                            .getDisabledImageAccessor();
                    if (contentAccessor != null) {
                        htmlWriter.writeURIAttributeNS("disabledImageURL",
                                contentAccessor.resolveURL(facesContext, null,
                                        null));
                    }

                    contentAccessor = statesImageAccessors
                            .getHoverImageAccessor();
                    if (contentAccessor != null) {
                        htmlWriter.writeURIAttributeNS("hoverImageURL",
                                contentAccessor.resolveURL(facesContext, null,
                                        null));
                    }

                    contentAccessor = statesImageAccessors
                            .getSelectedImageAccessor();
                    if (contentAccessor != null) {
                        htmlWriter.writeURIAttributeNS("selectedImageURL",
                                contentAccessor.resolveURL(facesContext, null,
                                        null));
                    }
                }
            }
        }
    }

    protected String getDefaultCardStyleClassPrefix() {
        return JavaScriptClasses.TABBED_PANE;
    }

    protected String getCardStyleClassSuffix() {
        return TAB;
    }

    protected String getJavaScriptClassName() {
        return JavaScriptClasses.TAB;
    }

    protected void declareCard(IJavaScriptWriter js,
            CardComponent cardComponent, String tabbedPaneClientId,
            boolean selected) throws WriterException {

        boolean declare[] = new boolean[1];
        String var = js.getJavaScriptRenderContext().allocateComponentVarId(
                tabbedPaneClientId, declare);
        if (declare[0]) {
            js.write("var ").write(var).write('=')
                    .writeCall("f_core", "GetElementByClientId")
                    .writeString(tabbedPaneClientId)
                    .writeln(", document, true);");
        }

        TabComponent tab = (TabComponent) cardComponent;
        // TabbedPaneComponent tabbedPane = tab.getTabbedPane();

        IHtmlWriter writer = js.getWriter();

        // IHtmlRenderContext htmlRenderContext =
        // writer.getHtmlComponentRenderContext().getHtmlRenderContext();

        FacesContext facesContext = js.getFacesContext();

        String tadComponentId = writer.getComponentRenderContext()
                .getComponentClientId();

        js.writeCall(var, "f_declareCard");

        IObjectLiteralWriter objectLiteralWriter = js.writeObjectLiteral(false);

        if (CardBoxRenderer.isCardBoxJSF12_Generation(js
                .getComponentRenderContext().getRenderContext())) {
            objectLiteralWriter.writeSymbol("_titleGenerated").writeBoolean(
                    true);
        }

        objectLiteralWriter.writeSymbol("_id").writeString(tadComponentId);

        Object value = tab.getValue();
        String clientValue = null;
        if (value instanceof String) {
            clientValue = (String) value;

        } else if (value != null) {
            clientValue = convertValue(facesContext, cardComponent, value);
        }

        if (clientValue != null) {
            objectLiteralWriter.writeSymbol("_value").writeString(clientValue);
        }

        if (selected) {
            objectLiteralWriter.writeSymbol("_selected").writeBoolean(true);
        }

        String text = tab.getText(facesContext);
        if (text != null) {
            text = ParamUtils.formatMessage(tab, text);

            objectLiteralWriter.writeSymbol("_text").writeString(text);
        }

        String accessKey = tab.getAccessKey(facesContext);
        if (accessKey != null) {
            objectLiteralWriter.writeSymbol("_accessKey")
                    .writeString(accessKey);
        }

        if (tab.isDisabled(facesContext)) {
            objectLiteralWriter.writeSymbol("_disabled").writeBoolean(true);
        }

        IContentAccessors contentAccessors = tab
                .getImageAccessors(facesContext);

        if (contentAccessors instanceof IImageAccessors) {
            IImageAccessors imageAccessors = (IImageAccessors) contentAccessors;

            IContentAccessor contentAccessor = imageAccessors
                    .getImageAccessor();

            if (contentAccessor != null) {
                objectLiteralWriter.writeSymbol("_imageURL").writeString(
                        contentAccessor.resolveURL(facesContext, null, null));
            }

            if (imageAccessors instanceof IStatesImageAccessors) {
                IStatesImageAccessors statesImageAccessors = (IStatesImageAccessors) imageAccessors;

                contentAccessor = statesImageAccessors
                        .getDisabledImageAccessor();
                if (contentAccessor != null) {
                    objectLiteralWriter.writeSymbol("_disabledImageURL")
                            .writeString(
                                    contentAccessor.resolveURL(facesContext,
                                            null, null));
                }

                contentAccessor = statesImageAccessors.getHoverImageAccessor();
                if (contentAccessor != null) {
                    objectLiteralWriter.writeSymbol("_hoverImageURL")
                            .writeString(
                                    contentAccessor.resolveURL(facesContext,
                                            null, null));
                }

                contentAccessor = statesImageAccessors
                        .getSelectedImageAccessor();
                if (contentAccessor != null) {
                    objectLiteralWriter.writeSymbol("_selectedImageURL")
                            .writeString(
                                    contentAccessor.resolveURL(facesContext,
                                            null, null));
                }
            }
        }

        objectLiteralWriter.end();

        js.writeln(");");
    }

    protected void decode(IRequestContext context, UIComponent component,
            IComponentData componentData) {
        super.decode(context, component, componentData);

        FacesContext facesContext = context.getFacesContext();

        TabComponent tabComponent = (TabComponent) component;

        String text = componentData.getStringProperty("text");
        if (text != null) {
            String old = tabComponent.getText(facesContext);
            if (text.equals(old) == false) {
                tabComponent.setText(text);

                component.queueEvent(new PropertyChangeEvent(component,
                        Properties.TEXT, old, text));
            }
        }
    }

    @Override
    protected void encodeSummaryTitleCard(IHtmlWriter writer)
            throws WriterException {

        TabComponent tab = (TabComponent) writer.getComponentRenderContext()
                .getComponent();

        String text = tab.getText();
        if (text == null || text.length() == 0) {
            return;
        }

        int level = HeadingTools.computeHeadingLevel(tab);
        if (tab.getTabbedPane().isHeadingZone()) {
            level--;
        }

        if (level < 1) {
            level = 1;

        } else if (level > IHtmlElements.MAX_HEADING_LEVEL) {
            level = IHtmlElements.MAX_HEADING_LEVEL;
        }

        String tagName = IHtmlElements.H_BASE + level;
        writer.writeId(writer.getComponentRenderContext()
                .getComponentClientId() + "::heading");

        writer.startElement(tagName);
        writer.writeClass("f_tab_heading");
        writer.writeText(text);

        writer.endElement(tagName);

    }

    public String computeSubInputClientId(IRenderContext renderContext,
            UIComponent component, String clientId) {
        return clientId + INPUT_ID_SUFFIX;
    }

}
