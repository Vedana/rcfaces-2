/*
 * $Id: AbstractImageButtonFamillyDecorator.java,v 1.3 2013/11/13 12:53:31 jbmeslin Exp $
 */
package org.rcfaces.renderkit.html.internal.decorator;

import javax.faces.FacesException;
import javax.faces.component.UICommand;
import javax.faces.component.UIComponent;
import javax.faces.component.UINamingContainer;
import javax.faces.component.ValueHolder;
import javax.faces.context.FacesContext;
import javax.faces.render.RenderKitFactory;
import javax.faces.render.Renderer;

import org.rcfaces.core.component.capability.IAccessKeyCapability;
import org.rcfaces.core.component.capability.IAlternateTextCapability;
import org.rcfaces.core.component.capability.IFontCapability;
import org.rcfaces.core.component.capability.IForegroundBackgroundColorCapability;
import org.rcfaces.core.component.capability.IHorizontalTextPositionCapability;
import org.rcfaces.core.component.capability.ISelectedCapability;
import org.rcfaces.core.component.capability.ISizeCapability;
import org.rcfaces.core.component.capability.ITextDirectionCapability;
import org.rcfaces.core.component.capability.ITextPositionCapability;
import org.rcfaces.core.component.capability.IToolTipTextCapability;
import org.rcfaces.core.component.familly.IImageButtonFamilly;
import org.rcfaces.core.image.operation.IDisableOperation;
import org.rcfaces.core.image.operation.IHoverOperation;
import org.rcfaces.core.image.operation.ISelectedOperation;
import org.rcfaces.core.internal.RcfacesContext;
import org.rcfaces.core.internal.component.IStatesImageAccessors;
import org.rcfaces.core.internal.contentAccessor.ContentAccessorFactory;
import org.rcfaces.core.internal.contentAccessor.IContentAccessor;
import org.rcfaces.core.internal.images.ImageContentAccessorHandler;
import org.rcfaces.core.internal.renderkit.IComponentRenderContext;
import org.rcfaces.core.internal.renderkit.WriterException;
import org.rcfaces.core.internal.renderkit.border.IBorderRenderersRegistry;
import org.rcfaces.core.internal.tools.ValuesTools;
import org.rcfaces.core.internal.util.ParamUtils;
import org.rcfaces.renderkit.html.internal.AbstractCssRenderer;
import org.rcfaces.renderkit.html.internal.AbstractHtmlRenderer;
import org.rcfaces.renderkit.html.internal.HtmlTools;
import org.rcfaces.renderkit.html.internal.IAccessibilityRoles;
import org.rcfaces.renderkit.html.internal.ICssRenderer;
import org.rcfaces.renderkit.html.internal.ICssWriter;
import org.rcfaces.renderkit.html.internal.IHtmlRenderContext;
import org.rcfaces.renderkit.html.internal.IHtmlWriter;
import org.rcfaces.renderkit.html.internal.IJavaScriptWriter;
import org.rcfaces.renderkit.html.internal.border.AbstractHtmlBorderRenderer;
import org.rcfaces.renderkit.html.internal.border.IHtmlBorderRenderer;
import org.rcfaces.renderkit.html.internal.border.NoneBorderRenderer;
import org.rcfaces.renderkit.html.internal.ns.XhtmlNSAttributes;
import org.rcfaces.renderkit.html.internal.renderer.ICssStyleClasses;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:31 $
 */
@XhtmlNSAttributes({ "tabIndex", "borderType", "className", "imageURL",
        "selectedImageURL", "imageURL", "selectedImageURL", "disabledImageURL",
        "hoverImageURL" })
public abstract class AbstractImageButtonFamillyDecorator extends
        AbstractComponentDecorator {

    // private static final String SELECTED_IMAGE_RENDERED =
    // "imageButton.selected.rendered";

    // private static final String DISABLED_IMAGE_RENDERED =
    // "imageButton.disabled.rendered";

    private static final String IMAGE_ID_SUFFIX = ""
            + UINamingContainer.SEPARATOR_CHAR
            + UINamingContainer.SEPARATOR_CHAR + "image";

    private static final String TEXT_ID_SUFFIX = ""
            + UINamingContainer.SEPARATOR_CHAR
            + UINamingContainer.SEPARATOR_CHAR + "text";

    private static final String INPUT_ID_SUFFIX = ""
            + UINamingContainer.SEPARATOR_CHAR
            + UINamingContainer.SEPARATOR_CHAR + "input";

    private static final String IMAGE_CLASSNAME_SUFFIX = "_image";

    private static final String TEXT_CLASSNAME_SUFFIX = "_text";

    private static final String NONE_BORDER_ID = "none";

    protected final IImageButtonFamilly imageButtonFamilly;

    private IStatesImageAccessors imageAccessors;

    protected IHtmlWriter writer;

    protected Renderer renderer;

    private ICssStyleClasses cssStyleClasses;

    protected IJavaScriptWriter javaScriptWriter;

    protected String imageSrc;

    protected String text;

    protected IHtmlBorderRenderer htmlBorderWriter;

    private boolean alignHorizontal;

    protected int textPosition;

    protected int imageWidth;

    protected int imageHeight;

    protected String accessKey = null;

    protected Integer tabIndex = null;

    protected String alternateText;

    protected boolean disabled;

    public AbstractImageButtonFamillyDecorator(
            IImageButtonFamilly imageButtonFamilly) {
        this.imageButtonFamilly = imageButtonFamilly;
    }

    public final void encodeContainerEnd(IHtmlWriter writer, Renderer renderer)
            throws WriterException {
        this.writer = writer;
        this.renderer = renderer;
        try {
            IComponentRenderContext componentRenderContext = writer
                    .getComponentRenderContext();

            FacesContext facesContext = componentRenderContext
                    .getFacesContext();

            String borderType = null;
            if (imageButtonFamilly.isBorder(facesContext)) {
                borderType = imageButtonFamilly.getBorderType(facesContext);

                IBorderRenderersRegistry borderRendererRegistry = componentRenderContext
                        .getRenderContext().getProcessContext()
                        .getRcfacesContext().getBorderRenderersRegistry();

                UIComponent cmp = (UIComponent) imageButtonFamilly;

                htmlBorderWriter = (IHtmlBorderRenderer) borderRendererRegistry
                        .getBorderRenderer(facesContext,
                                RenderKitFactory.HTML_BASIC_RENDER_KIT,
                                cmp.getFamily(), cmp.getRendererType(),
                                borderType);

            }

            text = imageButtonFamilly.getText(facesContext);
            text = ParamUtils.formatMessage((UIComponent) imageButtonFamilly,
                    text);

            textPosition = imageButtonFamilly.getTextPosition(facesContext);
            if (textPosition == 0) {
                textPosition = IHorizontalTextPositionCapability.DEFAULT_POSITION;
            }

            if (accessKey == null) {
                // Peut être positionné par le constructeur enfant !

            if (imageButtonFamilly instanceof IAccessKeyCapability) {
                accessKey = ((IAccessKeyCapability) imageButtonFamilly)
                        .getAccessKey();
            }
            }

            if (alternateText == null) {
                // Peut être positionné par le constructeur enfant !
            if (imageButtonFamilly instanceof IAlternateTextCapability) {
                alternateText = ((IAlternateTextCapability) imageButtonFamilly)
                        .getAlternateText();

                if (alternateText == null
                        && (imageButtonFamilly instanceof IToolTipTextCapability)) {
                    alternateText = ((IToolTipTextCapability) imageButtonFamilly)
                            .getToolTipText();
                }
            }
            }
            if (tabIndex == null) {
                // Peut être positionné par le constructeur enfant !
            tabIndex = imageButtonFamilly.getTabIndex(facesContext);
            }

            if (htmlBorderWriter == null && (text != null)) {
                IBorderRenderersRegistry borderRendererRegistry = RcfacesContext
                        .getInstance(facesContext).getBorderRenderersRegistry();

                htmlBorderWriter = (IHtmlBorderRenderer) borderRendererRegistry
                        .getBorderRenderer(facesContext,
                                RenderKitFactory.HTML_BASIC_RENDER_KIT, null,
                                null, NONE_BORDER_ID);
            }

            disabled = imageButtonFamilly.isDisabled(facesContext);

            boolean selected = false;
            if (imageButtonFamilly instanceof ISelectedCapability) {
                selected = isSelected((ISelectedCapability) imageButtonFamilly);
            }

            IContentAccessor imageAccessor = getImageAccessor(writer);
            // On le met ici, car le getImageAccessor peut changer la taille des
            // boutons ...
            IContentAccessor disabledImageAccessor = getDisabledImageAccessor(writer);
            IContentAccessor selectedImageAccessor = getSelectedImageAccessor(writer);
            IContentAccessor hoverImageAccessor = getHoverImageAccessor(writer);

            imageWidth = imageButtonFamilly.getImageWidth(facesContext);
            imageHeight = imageButtonFamilly.getImageHeight(facesContext);

            String width = null;
            String height = null;
            if (imageButtonFamilly instanceof ISizeCapability) {
                ISizeCapability sizeCapability = (ISizeCapability) imageButtonFamilly;

                width = sizeCapability.getWidth();
                if (width != null && imageWidth < 1) {
                    alignHorizontal = true;
                }

                height = sizeCapability.getHeight();
            }

            int tableHorizontalSpan = computeHorizontalSpan();
            int tableVerticalSpan = computeVerticalSpan();

            boolean directComponent = false;
            String mainComponent = null;
            if (htmlBorderWriter == null) {
                boolean displayInline = true;
                if (isCompositeComponent()) {
                    if (width == null && height == null) {
                        displayInline = true;
                    }
                    mainComponent = IHtmlWriter.DIV;

                } else {
                    displayInline = false;
                    mainComponent = getInputElement();
                    directComponent = true;
                }

                writer.startElement(mainComponent);

                if (displayInline) {
                    writer.writeStyle().writeDisplay(ICssWriter.INLINE);
                }

            } else {
                mainComponent = IHtmlWriter.DIV;

                writer.startElement(mainComponent);

                htmlBorderWriter.initialize(writer, getCssStyleClasses(),
                        width, height, tableHorizontalSpan, tableVerticalSpan,
                        disabled, selected);
            }

            if (tabIndex != null) {
                writer.writeAttributeNS("tabIndex", tabIndex.intValue());
            }

            if (borderType != null) {
                writer.writeAttributeNS("borderType", borderType);
            }

            ICssStyleClasses cssStyleClasses = getCssStyleClasses();
            if (disabled) {
                cssStyleClasses.addSuffix("_disabled");

            } else if (selected) {
                cssStyleClasses.addSuffix("_selected");
            }

            writeAttributes(cssStyleClasses);

            if (cssStyleClasses != null) {
                writer.writeAttributeNS("className",
                        cssStyleClasses.listStyleClasses(), " ");
            }

            initializeJavaScript(writer);

            if (imageAccessor != null) {
                imageSrc = imageAccessor.resolveURL(facesContext, null, null);
            }

            if (disabled) {
                if (disabledImageAccessor != null) {
                    if (imageSrc != null) {
                        writer.writeAttributeNS("imageURL", imageSrc);
                    }

                    String disabledImageSrc = disabledImageAccessor.resolveURL(
                            facesContext, null, null);
                    if (disabledImageSrc != null) {
                        imageSrc = disabledImageSrc;
                    }
                }

                if (selectedImageAccessor != null) {
                    String selectedSrc = selectedImageAccessor.resolveURL(
                            facesContext, null, null);

                    if (selectedSrc != null) {
                        writer.getJavaScriptEnableMode().enableOnFocus();

                        writer.writeAttributeNS("selectedImageURL", selectedSrc);
                    }
                }
            } else {
                if (selectedImageAccessor != null) {
                    String selectedImageURL = selectedImageAccessor.resolveURL(
                            facesContext, null, null);

                    if (selectedImageURL != null) {
                        writer.getJavaScriptEnableMode().enableOnFocus();

                        if (selected) {
                            if (imageSrc != null) {
                                writer.writeAttributeNS("imageURL", imageSrc);
                            }
                            imageSrc = selectedImageURL;

                        } else {
                            writer.writeAttributeNS("selectedImageURL",
                                    selectedImageURL);
                        }
                    }
                }

                if (disabledImageAccessor != null) {
                    String disabledImageURL = disabledImageAccessor.resolveURL(
                            facesContext, null, null);

                    if (disabledImageURL != null) {
                        writer.writeAttributeNS("disabledImageURL",
                                disabledImageURL);
                    }
                }

            }

            if (hoverImageAccessor != null) {
                String hoverImageURL = hoverImageAccessor.resolveURL(
                        facesContext, null, null);

                if (hoverImageURL != null) {
                    writer.getJavaScriptEnableMode().enableOnOver();
                    writer.writeAttributeNS("hoverImageURL", hoverImageURL);
                }
            }

            if (htmlBorderWriter == null && directComponent) {
                writeImageAttributes(writer, imageButtonFamilly);

                if (mainComponent.equals(IHtmlWriter.INPUT)) {
                    writer.writeType(IHtmlWriter.IMAGE_INPUT_TYPE);
                    writer.writeName(writer.getComponentRenderContext()
                            .getComponentClientId());
                    writer.writeValue(getInputValue(true));
                    writeInputAttributes(writer, true);
                    writeImageSrc(writer, imageSrc);
                    writeImageSize(writer, imageButtonFamilly);

                } else {
                    // C'est un <a href> !
                    // On évite d'avoir href="javascript:void(0)" qui se suivent
                    // car JAWS les merge ....
                    writer.writeHRef_JavascriptVoid0();
                    writeInputAttributes(writer, false);

                    writer.startElement(IHtmlWriter.IMG);
                    writer.writeId(getImageId(writer, htmlBorderWriter));
                    writer.writeClass(getImageClassName(htmlBorderWriter));
                    writeImageSrc(writer, imageSrc);
                    writeImageSize(writer, imageButtonFamilly);

                    if (alternateText != null) {
                        writer.writeAlt(alternateText);
                    }
                    writer.endElement(IHtmlWriter.IMG);
                }

                // writer.writeAlign("baseline");
            }

            /*
             * Le javascript s'occupe de ca ! if (button == false &&
             * imageJavascript == false) { writer.writeAttribute("href",
             * IHtmlWriter.JAVASCRIPT_VOID); }
             */

            if (htmlBorderWriter != null) {
                htmlBorderWriter.startComposite(writer);

                if (textPosition == ITextPositionCapability.BOTTOM_POSITION) {
                    writeBottomPosition();

                } else if (textPosition == ITextPositionCapability.TOP_POSITION) {
                    writeTopPosition();

                } else if (textPosition == IHorizontalTextPositionCapability.RIGHT_POSITION) {
                    writeRightPosition();

                } else {
                    writeLeftPosition();
                }

                htmlBorderWriter.endComposite(writer);

            } else if (directComponent == false) {
                // htmlBorderWriter=null;

                // Nous sommes dans DIV ou SPAN
                writeImage();
            }

            if (isCompositeComponent()) {
                writeEndCompositeComponent();
            }

            if (mainComponent != null) {
                writer.endElement(mainComponent);
            }

        } finally {
            this.writer = null;
            this.renderer = null;
        }

        super.encodeContainerEnd(writer, renderer);
    }

    protected String getInputRole() {
        return IAccessibilityRoles.BUTTON;
    }

    protected String getMainStyleClassName() {
        return null;
    }

    private void writeInputAttributes(IHtmlWriter writer, boolean isInput)
            throws WriterException {

        if (isInput) {
            if (disabled) {
                writer.writeDisabled();
            }
            if (tabIndex != null) {
                writer.writeTabIndex(tabIndex.intValue());
            }
        } else {
            if (disabled) {
                writer.writeTabIndex(-1);
            } else if (tabIndex != null) {
                writer.writeTabIndex(tabIndex.intValue());
            }
        }

        if (accessKey != null) {
            writer.writeAccessKey(accessKey);
        }
        if (alternateText != null) {
            writer.writeTitle(alternateText);
        }
    }

    protected void writeImageSrc(IHtmlWriter writer, String imageSrc)
            throws WriterException {
        if (imageSrc == null) {
            imageSrc = computeBlankImageURL();

            if (imageSrc == null) {
                return;
            }
        }

        writer.writeSrc(imageSrc);
    }

    protected boolean isSelected(ISelectedCapability imageButtonFamilly) {
        return imageButtonFamilly.isSelected();
    }

    protected void writeEndCompositeComponent() throws WriterException {
    }

    protected void writeImageAttributes(IHtmlWriter writer,
            IImageButtonFamilly imageButtonFamilly) throws WriterException {

        if (imageButtonFamilly instanceof IAccessKeyCapability) {
            IAccessKeyCapability accessKeyCapability = (IAccessKeyCapability) imageButtonFamilly;

            String accessKey = accessKeyCapability.getAccessKey();
            if (accessKey != null) {
                writer.writeAccessKey(accessKey);
            }
        }
    }

    protected void writeImageSize(IHtmlWriter writer,
            IImageButtonFamilly imageButtonFamilly) throws WriterException {

        if (imageWidth > 0 && imageHeight > 0) {
            writer.writeWidth(imageWidth);
            writer.writeHeight(imageHeight);
        }
    }

    protected boolean isCompositeComponent() {
        return false;
    }

    protected IStatesImageAccessors getImageButtonAccessors(
            IHtmlWriter htmlWriter) {
        if (imageAccessors != null) {
            return imageAccessors;
        }

        imageAccessors = (IStatesImageAccessors) imageButtonFamilly
                .getImageAccessors(htmlWriter.getComponentRenderContext()
                        .getFacesContext());

        return imageAccessors;
    }

    protected boolean useImageFilterIfNecessery() {
        return false;
    }

    protected IContentAccessor getHoverImageAccessor(IHtmlWriter htmlWriter) {
        IContentAccessor contentAccessor = getImageButtonAccessors(htmlWriter)
                .getHoverImageAccessor();
        if (contentAccessor != null || useImageFilterIfNecessery() == false) {
            return contentAccessor;
        }

        IContentAccessor imageContentAccessor = getImageAccessor(htmlWriter);

        // RcfacesContext rcfacesContext =
        // RcfacesContext.getInstance(htmlWriter.getHtmlComponentRenderContext().
        // getFacesContext()).getImage();

        if (ImageContentAccessorHandler.isOperationSupported(htmlWriter
                .getComponentRenderContext().getFacesContext(),
                IHoverOperation.ID, imageContentAccessor) == false) {
            return contentAccessor;
        }

        return ContentAccessorFactory.createFromWebResource(null,
                IHoverOperation.ID + IContentAccessor.FILTER_SEPARATOR,
                imageContentAccessor);
    }

    protected IContentAccessor getSelectedImageAccessor(IHtmlWriter htmlWriter) {
        IContentAccessor contentAccessor = getImageButtonAccessors(htmlWriter)
                .getSelectedImageAccessor();
        if (contentAccessor != null || useImageFilterIfNecessery() == false) {
            return contentAccessor;
        }

        IContentAccessor imageContentAccessor = getImageAccessor(htmlWriter);

        if (ImageContentAccessorHandler.isOperationSupported(htmlWriter
                .getComponentRenderContext().getFacesContext(),
                ISelectedOperation.ID, imageContentAccessor) == false) {
            return contentAccessor;
        }

        return ContentAccessorFactory.createFromWebResource(null,
                ISelectedOperation.ID + IContentAccessor.FILTER_SEPARATOR,
                imageContentAccessor);
    }

    protected IContentAccessor getDisabledImageAccessor(IHtmlWriter htmlWriter) {
        IContentAccessor contentAccessor = getImageButtonAccessors(htmlWriter)
                .getDisabledImageAccessor();
        if (contentAccessor != null || useImageFilterIfNecessery() == false) {
            return contentAccessor;
        }

        IContentAccessor imageContentAccessor = getImageAccessor(htmlWriter);

        if (ImageContentAccessorHandler.isOperationSupported(htmlWriter
                .getComponentRenderContext().getFacesContext(),
                IDisableOperation.ID, imageContentAccessor) == false) {
            return contentAccessor;
        }

        return ContentAccessorFactory.createFromWebResource(null,
                IDisableOperation.ID + IContentAccessor.FILTER_SEPARATOR,
                imageContentAccessor);
    }

    protected IContentAccessor getImageAccessor(IHtmlWriter htmlWriter) {
        return getImageButtonAccessors(htmlWriter).getImageAccessor();
    }

    protected void initializeJavaScript(IHtmlWriter writer) {
    }

    protected final ICssStyleClasses getCssStyleClasses() {
        if ((renderer instanceof ICssRenderer) == false) {
            throw new FacesException("Can not compute className !");
        }

        if (cssStyleClasses != null) {
            return cssStyleClasses;
        }

        cssStyleClasses = ((ICssRenderer) renderer).getCssStyleClasses(writer);

        if (cssStyleClasses == null) {
            throw new NullPointerException(
                    "Component cssStyleClasses is null !");
        }

        return cssStyleClasses;
    }

    protected int computeVerticalSpan() {
        if (text == null) {
            return 1;
        }

        if (textPosition == ITextPositionCapability.BOTTOM_POSITION
                || textPosition == ITextPositionCapability.TOP_POSITION) {
            return 2;
        }

        return 1;
    }

    protected int computeHorizontalSpan() {
        if (text == null) {
            return 1;
        }

        if (textPosition == IHorizontalTextPositionCapability.RIGHT_POSITION
                || textPosition == IHorizontalTextPositionCapability.LEFT_POSITION) {
            return 2;
        }
        return 1;
    }

    protected abstract void writeAttributes(ICssStyleClasses cssStyleClasses)
            throws WriterException;

    protected String getInputElement() {
        return IHtmlWriter.A;
    }

    protected String getInputValue(boolean forceValue) {
        UIComponent component = writer.getComponentRenderContext()
                .getComponent();

        String value = null;

        if (component instanceof ValueHolder) {
            value = ValuesTools.valueToString((ValueHolder) component, writer
                    .getComponentRenderContext().getFacesContext());

        } else if (component instanceof UICommand) {
            value = ValuesTools.valueToString((UICommand) component, writer
                    .getComponentRenderContext().getFacesContext());
        }

        if (forceValue == false || value != null) {
            return value;
        }

        return "SELECTED";
    }

    protected boolean hasLabel() {
        return true;
    }

    protected void writeImage() throws WriterException {

        if (imageSrc == null) {
            return;
        }

        String inputElement = getInputElement();
        writer.startElement(inputElement);
        writer.writeRole(getInputRole());
        if (disabled) {
            writer.writeAriaDisabled(disabled);
        }
        if (text != null) {
        writer.writeAriaLabelledBy(getTextId(writer, htmlBorderWriter));
        }

        if (IHtmlWriter.INPUT.equals(inputElement)) {
            writeInputAttributes(writer, true);
            writer.writeType(IHtmlWriter.IMAGE_INPUT_TYPE);

            writer.writeName(writer.getComponentRenderContext()
                    .getComponentClientId());

            String inputId = getImageId(writer, htmlBorderWriter);
            writer.writeId(inputId);
            writer.addSubFocusableComponent(inputId);

            writeImageAttributes();
            writeImageSrc(writer, imageSrc);
            writeImageSize(writer, imageButtonFamilly);

            if (alternateText != null) {
                writer.writeAlt(alternateText);
            }

        } else {
            writeInputAttributes(writer, false);
            writeImageAttributes();
            writer.writeHRef_JavascriptVoid0();

            String inputId = getInputId(writer, htmlBorderWriter);
            writer.writeId(inputId);
            writer.addSubFocusableComponent(inputId);

            writer.startElement(IHtmlWriter.IMG);
            writer.writeId(getImageId(writer, htmlBorderWriter));
            writer.writeClass(getImageClassName(htmlBorderWriter));
            writeImageSrc(writer, imageSrc);
            writeImageSize(writer, imageButtonFamilly);
            if (alternateText != null) {
                writer.writeAlt(alternateText);
            }
            writer.endElement(IHtmlWriter.IMG);
        }

        writer.endElement(inputElement);
    }

    protected void writeImageAttributes() throws WriterException {

        writer.writeClass(getImageClassName(htmlBorderWriter));

        // writer.writeAttribute("align", "baseline");

        if (textPosition == IHorizontalTextPositionCapability.LEFT_POSITION
                || textPosition == IHorizontalTextPositionCapability.RIGHT_POSITION) {
            writer.writeStyle().writeVerticalAlign("middle");
        }

        writeImageAttributes(writer, imageButtonFamilly);

    }

    protected String getImageClassName(IHtmlBorderRenderer htmlBorderWriter) {
        return getCssStyleClasses().getSuffixedMainStyleClass(
                IMAGE_CLASSNAME_SUFFIX);
    }

    protected String getImageId(IHtmlWriter writer,
            IHtmlBorderRenderer htmlBorderWriter) {
        return writer.getComponentRenderContext().getComponentClientId()
                + IMAGE_ID_SUFFIX;
    }

    protected String getInputId(IHtmlWriter writer,
            IHtmlBorderRenderer htmlBorderWriter) {
        return writer.getComponentRenderContext().getComponentClientId()
                + INPUT_ID_SUFFIX;
    }

    protected void writeText() throws WriterException {
        if (text == null) {
            return;
        }

        UIComponent component = writer.getComponentRenderContext()
                .getComponent();
        if (component instanceof IAccessKeyCapability) {
            HtmlTools.writeSpanAccessKey(writer,
                    (IAccessKeyCapability) component, text, false);
            return;
        }

        writer.writeText(text);
    }

    protected void writeBottomPosition() throws WriterException {

        writeStartRow();

        String valign = VALIGN_BOTTOM;
        if (isValidText(text) == false) {
            valign = VALIGN_CENTER;
        }

        writeImage(HALIGN_CENTER, valign, null, null);

        if (text != null) {
            writeEndRow(1);

            writeStartRow();

            writeText(HALIGN_CENTER, VALIGN_TOP, null, null);
        }

        writeEndRow(0);
    }

    protected void writeTopPosition() throws WriterException {

        writeStartRow();

        String valign = VALIGN_CENTER;
        if (isValidText(text)) {
            writeText(HALIGN_CENTER, VALIGN_BOTTOM, null, null);

            writeEndRow(1);

            writeStartRow();

            valign = VALIGN_TOP;
        }

        writeImage(HALIGN_CENTER, valign, null, null);

        writeEndRow(0);
    }

    protected void writeEndRow(int nextRowCount) throws WriterException {
        htmlBorderWriter.endRow(writer);
    }

    protected void writeStartRow() throws WriterException {
        htmlBorderWriter.startRow(writer);
    }

    protected void writeLeftPosition() throws WriterException {

        writeStartRow();

        String width = null;
        String halign = HALIGN_CENTER;
        if (isValidText(text)) {
            if (alignHorizontal) {
                width = "50%";
            }
            writeText(HALIGN_RIGHT, VALIGN_CENTER, width, null);
            halign = HALIGN_LEFT;
        }

        if (width == null && imageWidth >= 0) {
            width = String.valueOf(imageWidth);
        }

        writeImage(halign, VALIGN_CENTER, width, null);

        writeEndRow(0);
    }

    protected void writeRightPosition() throws WriterException {
        writeStartRow();

        String width = null;
        String halign = HALIGN_RIGHT;
        if (isValidText(text) == false) {
            halign = HALIGN_CENTER;

        } else if (alignHorizontal) {
            width = "50%";
        }

        String imgWidth = width;
        if (imgWidth == null && imageWidth >= 0) {
            imgWidth = String.valueOf(imageWidth);
            width = null;
        }

        writeImage(halign, VALIGN_CENTER, imgWidth, null);

        if (text != null) {
            writeText(HALIGN_LEFT, VALIGN_CENTER, width, null);
        }

        writeEndRow(0);
    }

    protected boolean isValidText(String text) {
        return (text != null) && text.length() > 0;
    }

    protected void writeText(String halign, String valign, String width,
            String height) throws WriterException {

        htmlBorderWriter.startChild(writer, AbstractHtmlBorderRenderer.TD_TEXT,
                halign, valign, width, height, 1, 1);

        String clientId = getTextId(writer, htmlBorderWriter);

        if (imageSrc == null) {
            writer.startElement(IHtmlWriter.A);
            writer.writeHRef_JavascriptVoid0();

            writer.addSubFocusableComponent(clientId);
            writer.getJavaScriptEnableMode().enableOnFocus();

        } else {
            writer.startElement(IHtmlWriter.SPAN);
        }

        writer.writeId(clientId);
        writer.writeClass(getTextClassName(htmlBorderWriter));

        UIComponent mainComponent = writer.getComponentRenderContext()
                .getComponent();

        if (mainComponent instanceof ITextDirectionCapability) {
            AbstractHtmlRenderer.writeTextDirection(writer,
                    (ITextDirectionCapability) mainComponent);
        }

        ICssWriter cssWriter = writer.writeStyle(128);
        if (mainComponent instanceof IFontCapability) {
            cssWriter.writeFont((IFontCapability) mainComponent);
        }

        if (mainComponent instanceof IForegroundBackgroundColorCapability) {
            cssWriter
                    .writeForeground((IForegroundBackgroundColorCapability) mainComponent);
        }

        writeText();

        if (imageSrc == null) {
            writer.endElement(IHtmlWriter.A);
        } else {
            writer.endElement(IHtmlWriter.SPAN);
        }

        htmlBorderWriter.endChild(writer);
    }

    protected String getTextId(IHtmlWriter writer,
            IHtmlBorderRenderer htmlBorderWriter) {
        return writer.getComponentRenderContext().getComponentClientId()
                + TEXT_ID_SUFFIX;
    }

    protected String getTextClassName(IHtmlBorderRenderer htmlBorderWriter) {
        return getCssStyleClasses().getSuffixedMainStyleClass(
                TEXT_CLASSNAME_SUFFIX);
    }

    protected void writeImage(String halign, String valign, String width,
            String height) throws WriterException {

        htmlBorderWriter.startChild(writer,
                AbstractHtmlBorderRenderer.TD_IMAGE, halign, valign, width,
                height, 1, 1);

        // En effet notre image est déjà affichée dans ce cas !
        writeImage();

        htmlBorderWriter.endChild(writer);
    }

    protected String computeBlankImageURL() {

        IHtmlRenderContext htmlRenderContext = writer
                .getHtmlComponentRenderContext().getHtmlRenderContext();

        return htmlRenderContext.getHtmlProcessContext().getStyleSheetURI(
                AbstractCssRenderer.BLANK_IMAGE_URL, true);
    }

    protected void writeComboImage(int nextRowCount) throws WriterException {
        htmlBorderWriter.startChild(writer, "_cmarker",
                getComboImageHorizontalAlignment(),
                getComboImageVerticalAlignment(), getComboImageWidth(),
                getComboImageHeight(), 1, 1 + nextRowCount);

        writeComboImage();

        htmlBorderWriter.endChild(writer);
    }

    protected String getComboImageWidth() {
        return "7";
    }

    protected String getComboImageHeight() {
        return null;
    }

    protected String getComboImageHorizontalAlignment() {
        return HALIGN_CENTER;
    }

    protected String getComboImageVerticalAlignment() {
        return VALIGN_CENTER;
    }

    protected void writeComboImage() throws WriterException {
        String mainClassName = getCssStyleClasses().getMainStyleClass();

        if (htmlBorderWriter == null) {
            NoneBorderRenderer.SINGLETON.writeComboImage(writer, mainClassName);
            return;
        }
        htmlBorderWriter.writeComboImage(writer, mainClassName);
    }

}
