/*
 * $Id: CardRenderer.java,v 1.3 2013/07/03 12:25:10 jbmeslin Exp $
 * 
 */
package org.rcfaces.renderkit.html.internal.renderer;

import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.component.CardBoxComponent;
import org.rcfaces.core.component.CardComponent;
import org.rcfaces.core.component.capability.IAsyncDecodeModeCapability;
import org.rcfaces.core.component.capability.IAsyncRenderModeCapability;
import org.rcfaces.core.component.iterator.ICardIterator;
import org.rcfaces.core.internal.renderkit.IAsyncRenderer;
import org.rcfaces.core.internal.renderkit.IComponentRenderContext;
import org.rcfaces.core.internal.renderkit.IComponentWriter;
import org.rcfaces.core.internal.renderkit.WriterException;
import org.rcfaces.core.internal.renderkit.designer.IDesignerEngine;
import org.rcfaces.renderkit.html.internal.AbstractCssRenderer;
import org.rcfaces.renderkit.html.internal.IAccessibilityRoles;
import org.rcfaces.renderkit.html.internal.ICssWriter;
import org.rcfaces.renderkit.html.internal.IHtmlComponentRenderContext;
import org.rcfaces.renderkit.html.internal.IHtmlRenderContext;
import org.rcfaces.renderkit.html.internal.IHtmlWriter;
import org.rcfaces.renderkit.html.internal.IJavaScriptComponentRenderer;
import org.rcfaces.renderkit.html.internal.IJavaScriptRenderContext;
import org.rcfaces.renderkit.html.internal.IJavaScriptWriter;
import org.rcfaces.renderkit.html.internal.IObjectLiteralWriter;
import org.rcfaces.renderkit.html.internal.JavaScriptClasses;
import org.rcfaces.renderkit.html.internal.ns.XhtmlNSAttributes;

/**
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/07/03 12:25:10 $
 */
@XhtmlNSAttributes({ "asyncRender", "asyncDecode" })
public class CardRenderer extends AbstractCssRenderer implements IAsyncRenderer {

    private static final Log LOG = LogFactory.getLog(CardRenderer.class);

    private static final String CARD_SUFFIX = "_card";

    private static final IJavaScriptComponentRenderer CARD_JAVASCRIPT_COMPONENT = new IJavaScriptComponentRenderer() {
        

        public void initializeJavaScript(IJavaScriptWriter javaScriptWriter)
                throws WriterException {
            javaScriptWriter.getJavaScriptRenderContext()
                    .initializeJavaScriptDocument(javaScriptWriter);
        }

        public void initializeJavaScriptComponent(
                IJavaScriptWriter javaScriptWriter) {
        }

        public void releaseJavaScript(IJavaScriptWriter javaScriptWriter) {
        }

        public void initializePendingComponents(IJavaScriptWriter writer) {
        }

        public void addRequiredJavaScriptClassNames(IHtmlWriter writer,
                IJavaScriptRenderContext javaScriptRenderContext) {
        }
    };

    protected final void encodeBegin(IComponentWriter writer)
            throws WriterException {
        // Final a cause du designer

        // super.encodeBegin(writer); // ???

        FacesContext facesContext = writer.getComponentRenderContext()
                .getFacesContext();

        IComponentRenderContext componentRenderContext = writer
                .getComponentRenderContext();
        CardComponent cardComponent = (CardComponent) componentRenderContext
                .getComponent();
        CardBoxComponent cardBoxComponent = cardComponent.getCardBox();

        String cardClassName = getCardStyleClass(facesContext, cardComponent);

        boolean selected = isCardSelected(cardComponent);

        IHtmlWriter htmlWriter = (IHtmlWriter) writer;

        IHtmlRenderContext htmlRenderContext = htmlWriter
                .getHtmlComponentRenderContext().getHtmlRenderContext();

        IJavaScriptRenderContext javascriptRenderContext = htmlRenderContext
                .getJavaScriptRenderContext();

        if (CardBoxRenderer.isCardBoxJSF12_Generation(htmlRenderContext) == false) {

            javascriptRenderContext.removeJavaScriptWriter(htmlWriter);

            // Il faut calculer les dependances
            javascriptRenderContext.computeRequires(htmlWriter, this);

            IJavaScriptWriter jsWriter = javascriptRenderContext
                    .getJavaScriptWriter(htmlWriter, CARD_JAVASCRIPT_COMPONENT);

            initializePendingComponents(jsWriter);

            String cardBoxVarId = htmlRenderContext
                    .getComponentClientId(cardBoxComponent);

            declareCard(jsWriter, cardComponent, cardBoxVarId, selected);

            jsWriter.end();

        } else {
            htmlWriter.getJavaScriptEnableMode().enableOnInit();
        }

        int asyncRender = IAsyncRenderModeCapability.NONE_ASYNC_RENDER_MODE;
        if (selected == false) {
            if (htmlRenderContext.isAsyncRenderEnable()) {
                asyncRender = htmlRenderContext
                        .getAsyncRenderMode(cardBoxComponent);

                if (asyncRender != IAsyncRenderModeCapability.NONE_ASYNC_RENDER_MODE) {
                    htmlWriter.getHtmlComponentRenderContext()
                            .getHtmlRenderContext()
                            .pushInteractiveRenderComponent(htmlWriter, null);
                }
            }

            setAsyncRenderer(htmlWriter, cardComponent, asyncRender);
        }

        htmlWriter.writeln();

        htmlWriter.startElement(IHtmlWriter.DIV);

        /*
         * String w = tabbedPane.getWidth(); if (w != null) {
         * writer.writeAttribute("width", w); }
         */

        if (asyncRender != IAsyncRenderModeCapability.NONE_ASYNC_RENDER_MODE) {
            htmlWriter.writeAttributeNS("asyncRender", true);

            if (cardComponent.getAsyncDecodeMode(componentRenderContext
                    .getFacesContext()) == IAsyncDecodeModeCapability.PARTIAL_ASYNC_DECODE_MODE) {
                htmlWriter.writeAttributeNS("asyncDecode", true);
            }
        }

        writeHtmlAttributes(htmlWriter);
        writeJavaScriptAttributes(htmlWriter);
        htmlWriter.writeClass(cardClassName);
        writeCardAttributes(htmlWriter);

        ICssWriter cssWriter = htmlWriter.writeStyle(128);
        if (selected == false) {
            cssWriter.writeDisplay(ICssWriter.NONE);
        }

        if (selected) {
            htmlWriter.writeAriaSelected(selected);
        }
        writeCardSizes(htmlWriter, cardComponent);

        String textAlignement = cardComponent.getTextAlignment(facesContext);
        if (textAlignement != null) {
            cssWriter.writeTextAlign(textAlignement);
        }

        String verticalAlignement = cardComponent
                .getVerticalAlignment(facesContext);
        if (verticalAlignement != null) {
            cssWriter.writeVerticalAlign(verticalAlignement);
        }

        encodeSummaryTitleCard(htmlWriter);

        designerBeginChildren(htmlWriter, IDesignerEngine.MAIN_BODY);
    }

    protected void encodeSummaryTitleCard(IHtmlWriter htmlWriter)
            throws WriterException {
    }

    protected void writeCardSizes(IHtmlWriter htmlWriter,
            CardComponent cardComponent) {

        CardBoxComponent cardBoxComponent = cardComponent.getCardBox();
        FacesContext facesContext = htmlWriter.getComponentRenderContext()
                .getFacesContext();

        if (cardBoxComponent.getWidth(facesContext) != null) {
            htmlWriter.writeStyle().writeWidth("100%");
        }

        if (cardBoxComponent.getHeight(facesContext) != null) {
            htmlWriter.writeStyle().writeHeight("100%");
        }
    }

    protected void writeCardAttributes(IHtmlWriter htmlWriter)
            throws WriterException {
    }

    protected String getWAIRole() {
        return IAccessibilityRoles.TAB_CONTENT;
    }

    protected String getCardStyleClass(FacesContext facesContext,
            CardComponent cardComponent) {

        String className = cardComponent.getStyleClass(facesContext);
        if (className != null) {
            return className;
        }

        // CardBoxComponent cardBoxComponent = cardComponent.getCardBox();
        className = getDefaultCardStyleClassPrefix();

        String suffix = getCardStyleClassSuffix();
        if (suffix == null || suffix.length() < 1) {
            return className;
        }

        return className + suffix;
    }

    protected String getDefaultCardStyleClassPrefix() {
        return JavaScriptClasses.CARD_BOX;
    }

    protected String getCardStyleClassSuffix() {
        return CARD_SUFFIX;
    }

    protected void declareCard(IJavaScriptWriter js,
            CardComponent cardComponent, String cardBoxClientId,
            boolean selected) throws WriterException {

        boolean declare[] = new boolean[1];
        String var = js.getJavaScriptRenderContext().allocateComponentVarId(
                cardBoxClientId, declare);
        if (declare[0]) {
            js.write("var ").write(var).write('=')
                    .writeCall("f_core", "GetElementByClientId")
                    .writeString(cardBoxClientId).writeln(", document, true);");
        }

        // CardBoxComponent cardBoxComponent = cardComponent.getCardBox();

        IHtmlWriter writer = js.getWriter();

        IHtmlComponentRenderContext componentRenderContext = writer
                .getHtmlComponentRenderContext();

        // IHtmlRenderContext htmlRenderContext =
        // componentRenderContext.getHtmlRenderContext();
        FacesContext facesContext = js.getFacesContext();

        js.writeCall(var, "f_declareCard");

        IObjectLiteralWriter objectLiteralWriter = js.writeObjectLiteral(false);

        objectLiteralWriter.writeSymbol("_id").writeString(
                componentRenderContext.getComponentClientId());

        Object value = cardComponent.getValue();
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

        objectLiteralWriter.end();

        js.writeln(");");
    }

    protected void encodeEnd(IComponentWriter writer) throws WriterException {
        // Final a cause du desinger
        designerEndChildren(writer, IDesignerEngine.MAIN_BODY);

        IHtmlWriter htmlWriter = (IHtmlWriter) writer;

        htmlWriter.endElement(IHtmlWriter.DIV);

        super.encodeEnd(writer);
    }

    protected void encodeJavaScript(IJavaScriptWriter jsWriter)
            throws WriterException {
        // Pas de JAVASCRIPT !
        super.encodeJavaScript(jsWriter);

        /*
         * C'est fait dans les attributs du DIV IHtmlComponentRenderContext
         * htmlComponentRenderContext = jsWriter
         * .getHtmlComponentRenderContext();
         * 
         * if (htmlComponentRenderContext.getRenderContext().containsAttribute(
         * TabbedPaneRenderer.TABBED_PANE_JSF12_PROPERTY)) {
         * 
         * CardComponent cardComponent = (CardComponent)
         * htmlComponentRenderContext .getComponent();
         * 
         * boolean selected = isCardSelected(cardComponent);
         * 
         * CardBoxComponent cardBoxComponent = cardComponent.getCardBox();
         * 
         * String cardBoxVarId = htmlComponentRenderContext
         * .getHtmlRenderContext().getComponentClientId( cardBoxComponent);
         * 
         * declareCard(jsWriter, cardComponent, cardBoxVarId, selected); }
         */
    }

    protected String getJavaScriptClassName() {
        return JavaScriptClasses.CARD;
    }

    protected boolean isCardSelected(CardComponent cardComponent) {
        CardBoxComponent cardBoxComponent = cardComponent.getCardBox();

        CardComponent selectedCard = cardBoxComponent.getSelectedCard();

        if (selectedCard == null && cardBoxComponent.getValue() == null) {
            // Aucun selectionné par défaut, on prend le premier visible

            FacesContext facesContext = FacesContext.getCurrentInstance();

            ICardIterator cardIterator = cardBoxComponent.listCards();
            for (; cardIterator.hasNext();) {
                CardComponent cc = cardIterator.next();

                Boolean visibleState = cc.getVisibleState(facesContext);

                if (Boolean.FALSE.equals(visibleState)) {
                    continue;
                }

                return cc == cardComponent;
            }

            return true;
        }

        if (selectedCard == cardComponent) {
            return true;
        }

        return false;
    }
}
