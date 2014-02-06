/*
 * $Id: TreeRenderer.java,v 1.9 2013/12/13 13:55:04 jbmeslin Exp $
 */
package org.rcfaces.renderkit.html.internal.renderer;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.EnumSet;
import java.util.Set;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.ConverterException;

import org.rcfaces.core.component.IMenuComponent;
import org.rcfaces.core.component.MenuComponent;
import org.rcfaces.core.component.TreeComponent;
import org.rcfaces.core.component.capability.ICheckCardinalityCapability;
import org.rcfaces.core.component.capability.IClientFullStateCapability;
import org.rcfaces.core.component.capability.IDragAndDropEffects;
import org.rcfaces.core.component.capability.IOutlinedLabelCapability;
import org.rcfaces.core.component.capability.ISelectionCardinalityCapability;
import org.rcfaces.core.component.iterator.IMenuIterator;
import org.rcfaces.core.event.PropertyChangeEvent;
import org.rcfaces.core.internal.component.Properties;
import org.rcfaces.core.internal.renderkit.IComponentData;
import org.rcfaces.core.internal.renderkit.IComponentRenderContext;
import org.rcfaces.core.internal.renderkit.IComponentWriter;
import org.rcfaces.core.internal.renderkit.IRequestContext;
import org.rcfaces.core.internal.renderkit.WriterException;
import org.rcfaces.core.internal.tools.OutlinedLabelTools;
import org.rcfaces.core.internal.tools.ValuesTools;
import org.rcfaces.renderkit.html.internal.AbstractSelectItemsRenderer;
import org.rcfaces.renderkit.html.internal.HtmlTools;
import org.rcfaces.renderkit.html.internal.IAccessibilityRoles;
import org.rcfaces.renderkit.html.internal.ICssWriter;
import org.rcfaces.renderkit.html.internal.IHtmlComponentWriter;
import org.rcfaces.renderkit.html.internal.IHtmlRenderContext;
import org.rcfaces.renderkit.html.internal.IHtmlWriter;
import org.rcfaces.renderkit.html.internal.IJavaScriptRenderContext;
import org.rcfaces.renderkit.html.internal.IJavaScriptWriter;
import org.rcfaces.renderkit.html.internal.IObjectLiteralWriter;
import org.rcfaces.renderkit.html.internal.JavaScriptClasses;
import org.rcfaces.renderkit.html.internal.agent.IClientBrowser;
import org.rcfaces.renderkit.html.internal.decorator.IComponentDecorator;
import org.rcfaces.renderkit.html.internal.decorator.ISelectItemNodeWriter;
import org.rcfaces.renderkit.html.internal.decorator.SubMenuDecorator;
import org.rcfaces.renderkit.html.internal.decorator.TreeDecorator;
import org.rcfaces.renderkit.html.internal.service.TreeBehaviorListener;
import org.rcfaces.renderkit.html.internal.util.HeadingTools;
import org.rcfaces.renderkit.html.internal.ns.XhtmlNSAttributes;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.9 $ $Date: 2013/12/13 13:55:04 $
 */
@XhtmlNSAttributes({ "checkCardinality", "clientCheckFullState",
        "selectionCardinality", "clientSelectionFullState", "userExpandable",
        "hideRootExpandSign", "preloadedLevelDepth", "cursorValue",
        "overStyleClass", "dragEffects", "dragTypes", "dragTypes",
        "dropEffects", "dropTypes", "dropTypes", "bodyDroppable" })
public class TreeRenderer extends AbstractSelectItemsRenderer {

    private static final String NODE_ROW_ID = "#node";

    private static final String FOCUS_ID_SUFFIX = "::focus";

    private static final String BODY_ID_SUFFIX = "::body";

    private static final boolean LINK_TREE_NODE_FOCUS = true;

    private static final String NEED_ACCENTS_API_PROPERTY = "org.rcfaces.html.NEED_ACCENTS_API";

    @Override
    @SuppressWarnings("unused")
    protected void encodeBeforeDecorator(IHtmlWriter htmlWriter,
            IComponentDecorator componentDecorator) throws WriterException {
        super.encodeBeforeDecorator(htmlWriter, componentDecorator);

        IComponentRenderContext componentContext = htmlWriter
                .getComponentRenderContext();

        TreeComponent treeComponent = (TreeComponent) componentContext
                .getComponent();
        FacesContext facesContext = componentContext.getFacesContext();

        htmlWriter.startElement(IHtmlWriter.DIV);

        writeHtmlAttributes(htmlWriter);
        writeJavaScriptAttributes(htmlWriter);
        writeCssAttributes(htmlWriter);

        if (treeComponent.isReadOnly(facesContext)) {
            htmlWriter.writeAttributeNS("readOnly", true);
        }

        if (treeComponent.isDisabled(facesContext)) {
            htmlWriter.writeAttributeNS("disabled", true);
        }

        if (treeComponent.isCheckable(facesContext)) {
            int cardinality = treeComponent.getCheckCardinality(facesContext);
            if (cardinality == 0) {
                cardinality = ICheckCardinalityCapability.DEFAULT_CARDINALITY;
            }

            htmlWriter.writeAttributeNS("checkCardinality", cardinality);

            int ccfs = treeComponent.getClientCheckFullState(facesContext);
            if (ccfs != IClientFullStateCapability.NONE_CLIENT_FULL_STATE) {
                htmlWriter.writeAttributeNS("clientCheckFullState", ccfs);
            }
        }

        if (treeComponent.isSelectable(facesContext)) {
            int cardinality = treeComponent
                    .getSelectionCardinality(facesContext);
            if (cardinality == 0) {
                cardinality = ISelectionCardinalityCapability.DEFAULT_CARDINALITY;
            }

            htmlWriter.writeAttributeNS("selectionCardinality", cardinality);

            int csfs = treeComponent.getClientSelectionFullState(facesContext);
            if (csfs != IClientFullStateCapability.NONE_CLIENT_FULL_STATE) {
                htmlWriter.writeAttributeNS("clientSelectionFullState", csfs);
            }
        }

        if (treeComponent.isSchrodingerCheckable(facesContext)) {
            if (treeComponent.isCheckable(facesContext) == false) {
                throw new FacesException(
                        "A schrodingerCheckable tree must be checkable !");
            }
            htmlWriter.writeAttributeNS("schrodingerCheckable", true);

            String cbIndeterminate = getCheckboxInderminateImageURL(htmlWriter);
            if (cbIndeterminate != null) {
                htmlWriter.writeAttributeNS("cbxInd", cbIndeterminate);
                htmlWriter.writeAttributeNS("cbxCheck",
                        getCheckboxCheckedImageURL(htmlWriter));
                htmlWriter.writeAttributeNS("cbxUncheck",
                        getCheckboxUncheckedImageURL(htmlWriter));
            }
        }

        String outlinedLabel = treeComponent.getOutlinedLabel(facesContext);
        if (outlinedLabel != null) {
            htmlWriter.writeAttributeNS("outlinedLabel", outlinedLabel);
        }

        EnumSet<IOutlinedLabelCapability.Method> outlinedLabelMethods = treeComponent
                .getOutlinedLabelMethodSet();
        if (outlinedLabelMethods != null
                && outlinedLabelMethods.isEmpty() == false) {

            String ms = OutlinedLabelTools.format(outlinedLabelMethods);

            htmlWriter.writeAttributeNS("outlinedLabelMethod", ms);

            if (outlinedLabelMethods
                    .contains(IOutlinedLabelCapability.Method.IgnoreAccents)) {
                htmlWriter.getComponentRenderContext().setAttribute(
                        NEED_ACCENTS_API_PROPERTY, Boolean.TRUE);
            }
        }

        if (treeComponent.isExpandable(facesContext) == false) {
            htmlWriter.writeAttributeNS("userExpandable", false);
        }

        if (treeComponent.isHideRootExpandSign(facesContext)) {
            htmlWriter.writeAttributeNS("hideRootExpandSign", true);
        }

        int depthLevel = treeComponent.getPreloadedLevelDepth(facesContext);
        if (depthLevel > 0) {
            htmlWriter.writeAttributeNS("preloadedLevelDepth", depthLevel);
        }

        Object cursorValue = treeComponent.getCursorValue(facesContext);
        String clientCursorValue = null;

        if (cursorValue != null) {
            clientCursorValue = ValuesTools.convertValueToString(cursorValue,
                    treeComponent, facesContext);
        }

        if (clientCursorValue != null) {
            htmlWriter.writeAttributeNS("cursorValue", clientCursorValue);
        }

        htmlWriter.getJavaScriptEnableMode().enableOnInit();

        String overStyleClass = treeComponent.getOverStyleClass(facesContext);
        if (overStyleClass != null) {
            htmlWriter.writeAttributeNS("overStyleClass", overStyleClass);

            // htmlWriter.getJavaScriptEnableMode().enableOnOver();
        }

        if (treeComponent.isDraggable(facesContext)) {
            int dragEffects = treeComponent.getDragEffects(facesContext);

            if (dragEffects <= IDragAndDropEffects.UNKNOWN_DND_EFFECT) {
                dragEffects = IDragAndDropEffects.DEFAULT_DND_EFFECT;
            }
            htmlWriter.writeAttributeNS("dragEffects", dragEffects);

            String dragTypes[] = treeComponent.getDragTypes(facesContext);
            if (dragTypes != null && dragTypes.length > 0) {
                htmlWriter.writeAttributeNS("dragTypes",
                        HtmlTools.serializeDnDTypes(dragTypes));
            } else {
                htmlWriter.writeAttributeNS("dragTypes", "x-RCFaces/treeNode");
            }
        }

        if (treeComponent.isDroppable(facesContext)) {
            int dropEffects = treeComponent.getDropEffects(facesContext);

            if (dropEffects <= IDragAndDropEffects.UNKNOWN_DND_EFFECT) {
                dropEffects = IDragAndDropEffects.DEFAULT_DND_EFFECT;
            }
            htmlWriter.writeAttributeNS("dropEffects", dropEffects);

            String dropTypes[] = treeComponent.getDropTypes(facesContext);
            if (dropTypes != null && dropTypes.length > 0) {
                htmlWriter.writeAttributeNS("dropTypes",
                        HtmlTools.serializeDnDTypes(dropTypes));
            } else {
                htmlWriter.writeAttributeNS("dropTypes", "*/*");
            }

            if (treeComponent.isBodyDroppable(facesContext)) {
                htmlWriter.writeAttributeNS("bodyDroppable", true);
            }
        }

        String commandImageURL = getCommandNodeImageURL(htmlWriter);
        if (false && commandImageURL != null) {
            htmlWriter.writeAttributeNS("cmdNode", commandImageURL);

            String url = getCommandNodeOpenedImageURL(htmlWriter);
            if (url != null) {
                htmlWriter.writeAttributeNS("cmdNodeOpened", url);
            }
            url = getCommandNodeDisabledImageURL(htmlWriter);
            if (url != null) {
                htmlWriter.writeAttributeNS("cmdNodeDisabled", url);
            }
            url = getCommandRootImageURL(htmlWriter);
            if (url != null) {
                htmlWriter.writeAttributeNS("cmdRoot", url);
            }
            url = getCommandRootOpenedImageURL(htmlWriter);
            if (url != null) {
                htmlWriter.writeAttributeNS("cmdRootOpened", url);
            }
            url = getCommandRootDisabledImageURL(htmlWriter);
            if (url != null) {
                htmlWriter.writeAttributeNS("cmdRootDisabled", url);
            }
            url = getCommandLeafImageURL(htmlWriter);
            if (url != null) {
                htmlWriter.writeAttributeNS("cmdLeaf", url);
            }
            url = getCommandLeafDisabledImageURL(htmlWriter);
            if (url != null) {
                htmlWriter.writeAttributeNS("cmdLeafDisabled", url);
            }
        }

        if (LINK_TREE_NODE_FOCUS == true) {
            // C'est le role TREE

        } else {
        htmlWriter.startElement(IHtmlWriter.A);
        htmlWriter.writeId(componentContext.getComponentClientId()
                + FOCUS_ID_SUFFIX);
        htmlWriter.writeClass("f_tree_focus");

        Integer tabIndex = treeComponent.getTabIndex();
        if (tabIndex != null) {
            htmlWriter.writeTabIndex(tabIndex.intValue());
        } else {
            htmlWriter.writeTabIndex(0);
        }
        htmlWriter.writeRole(IAccessibilityRoles.TREE);
        htmlWriter.endElement(IHtmlWriter.A);
        }

        String caption = treeComponent.getCaption(facesContext);
        String captionClientId = null;
        if (caption != null) {
            String captionComponent = IHtmlWriter.LABEL;
            int level = HeadingTools.computeHeadingLevel(treeComponent);
            if (level > 0) {
                if (level > IHtmlWriter.MAX_HEADING_LEVEL) {
                    level = IHtmlWriter.MAX_HEADING_LEVEL;
                }

                captionComponent = IHtmlWriter.H_BASE + level;
            }

            captionClientId = componentContext.getComponentClientId()
                    + "::caption";

            htmlWriter.startElement(captionComponent);
            htmlWriter.writeId(captionClientId);
            htmlWriter.writeClass(getMainStyleClassName() + "_caption");

            if (IHtmlWriter.LABEL.equals(captionComponent)) {
                htmlWriter.writeFor(componentContext.getComponentClientId());
            }

            htmlWriter.writeText(caption);
            htmlWriter.endElement(captionComponent);
        }

        htmlWriter.startElement(IHtmlWriter.UL);
        htmlWriter.writeId(componentContext.getComponentClientId()
                + BODY_ID_SUFFIX);

        htmlWriter.writeRole(IAccessibilityRoles.TREE);
        if (captionClientId != null) {
            htmlWriter.writeAriaLabelledBy(captionClientId);
        }

        String className = "f_tree_body";
        if (commandImageURL != null) {
            className += " f_tree_commandHasImages";
        }
        if (treeComponent.isSelectable(facesContext)) {
            className += " f_tree_selectable";
        }
        if (treeComponent.isCheckable(facesContext)) {
            className += " f_tree_checkable";
        }

        htmlWriter.writeClass(className);
    }
    
    
    
    
    @Override
    protected String getWAIRole() {
        return null;
    }

    @Override
    protected void addUnlockProperties(Set<Serializable> unlockedProperties) {
        super.addUnlockProperties(unlockedProperties);

        unlockedProperties.add("cursor");
    }

    @Override
    protected void encodeJavaScript(IJavaScriptWriter jsWriter)
            throws WriterException {

        super.encodeJavaScript(jsWriter);

        if (true) {
            String commandImageURL = getCommandNodeImageURL(jsWriter);
            if (commandImageURL != null) {
                IObjectLiteralWriter objWriter = jsWriter.writeMethodCall(
                        "_setCommandImagesURL").writeObjectLiteral(false);

                objWriter.writeSymbol("_cmdNodeImageURL").writeString(
                        commandImageURL);

                String url = getCommandNodeOpenedImageURL(jsWriter);
                if (url != null) {
                    objWriter.writeSymbol("_cmdNodeOpenedImageURL")
                            .writeString(url);
                }
                url = getCommandNodeDisabledImageURL(jsWriter);
                if (url != null) {
                    objWriter.writeSymbol("_cmdNodeDisabledImageURL")
                            .writeString(url);
                }
                url = getCommandRootImageURL(jsWriter);
                if (url != null) {
                    objWriter.writeSymbol("_cmdRootImageURL").writeString(url);
                }
                url = getCommandRootOpenedImageURL(jsWriter);
                if (url != null) {
                    objWriter.writeSymbol("_cmdRootOpenedImageURL")
                            .writeString(url);
                }
                url = getCommandRootDisabledImageURL(jsWriter);
                if (url != null) {
                    objWriter.writeSymbol("_cmdRootDisabledImageURL")
                            .writeString(url);
                }
                url = getCommandLeafImageURL(jsWriter);
                if (url != null) {
                    objWriter.writeSymbol("_cmdLeafImageURL").writeString(url);
                }
                url = getCommandLeafDisabledImageURL(jsWriter);
                if (url != null) {
                    objWriter.writeSymbol("_cmdLeafDisabledImageURL")
                            .writeString(url);
                }

                objWriter.end().writeln(");");
            }

        }

    }

    @Override
    protected void decode(IRequestContext context, UIComponent component,
            IComponentData componentData) {
        super.decode(context, component, componentData);

        FacesContext facesContext = context.getFacesContext();

        TreeComponent treeComponent = (TreeComponent) component;

        String cursorValue = componentData.getStringProperty("cursor");
        if (cursorValue != null) {
            Object cursorValueObject = ValuesTools.convertStringToValue(
                    facesContext, treeComponent, cursorValue, false);

            Object oldCursorValueObject = treeComponent
                    .getCursorValue(facesContext);
            if (isEquals(oldCursorValueObject, cursorValueObject) == false) {

                treeComponent.setCursorValue(cursorValueObject);

                component.queueEvent(new PropertyChangeEvent(component,
                        Properties.CURSOR_VALUE, oldCursorValueObject,
                        cursorValueObject));

            }
        }

        treeComponent.setShowValue(null);
    }

    protected void encodeAfterDecorator(IHtmlWriter htmlWriter,
            IComponentDecorator componentDecorator) throws WriterException {
        super.encodeAfterDecorator(htmlWriter, componentDecorator);

        htmlWriter.endElement(IHtmlWriter.UL);
        htmlWriter.endElement(IHtmlWriter.DIV);
    }

    @Override
    public void addRequiredJavaScriptClassNames(IHtmlWriter htmlWriter,
            IJavaScriptRenderContext javaScriptRenderContext) {
        super.addRequiredJavaScriptClassNames(htmlWriter,
                javaScriptRenderContext);

        FacesContext facesContext = htmlWriter.getComponentRenderContext()
                .getFacesContext();

        TreeComponent treeComponent = (TreeComponent) htmlWriter
                .getComponentRenderContext().getComponent();
        IMenuIterator menuIterator = treeComponent.listMenus();
        if (menuIterator.hasNext()) {

            javaScriptRenderContext.appendRequiredClass(JavaScriptClasses.TREE,
                    "menu");
        }

        if (treeComponent.getPreloadedLevelDepth(facesContext) > 0) {
            javaScriptRenderContext.appendRequiredClass(JavaScriptClasses.TREE,
                    "ajax");
            TreeBehaviorListener.addTreeRefreshBehavior(treeComponent, facesContext);
        }

        if (treeComponent.isDraggable(facesContext)
                || treeComponent.isDroppable(facesContext)) {
            javaScriptRenderContext.appendRequiredClass(JavaScriptClasses.TREE,
                    "dnd");
        }

        if (htmlWriter.getComponentRenderContext().getAttribute(
                NEED_ACCENTS_API_PROPERTY) != null) {
            javaScriptRenderContext.appendRequiredClass(JavaScriptClasses.TREE,
                    "accents");
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.rcfaces.core.internal.renderkit.html.AbstractHtmlRenderer#
     * getJavaScriptClassName()
     */
    @Override
    protected String getJavaScriptClassName() {
        return JavaScriptClasses.TREE;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.rcfaces.core.internal.renderkit.html.AbstractCssRenderer#writeCustomCss
     * (org.rcfaces.core.internal.renderkit.IWriter,
     * org.rcfaces.core.internal.renderkit.html.AbstractCssRenderer.CssWriter)
     */
    @Override
    protected void writeCustomCss(IHtmlWriter htmlWriter, ICssWriter cssWriter) {
        super.writeCustomCss(htmlWriter, cssWriter);

        IComponentRenderContext componentRenderContext = htmlWriter
                .getComponentRenderContext();

        TreeComponent treeComponent = (TreeComponent) componentRenderContext
                .getComponent();
        FacesContext facesContext = componentRenderContext.getFacesContext();

        if (treeComponent.getWidth(facesContext) != null
                || treeComponent.getHeight(facesContext) != null) {
            cssWriter.writeOverflow(ICssWriter.AUTO);
        }

        if (treeComponent.isBorder(facesContext) == false) {
            cssWriter.writeBorderStyle(ICssWriter.NONE);
        }

    }

    @Override
    protected IComponentDecorator createComponentDecorator(
            FacesContext facesContext, UIComponent component) {

        IComponentDecorator decorator = new TreeDecorator(
                (TreeComponent) component);

        TreeComponent treeComponent = (TreeComponent) component;

        IComponentDecorator menuDecorators = null;
        IMenuIterator menuIterator = treeComponent.listMenus();
        for (; menuIterator.hasNext();) {
            MenuComponent menuComponent = menuIterator.next();

            IComponentDecorator menuDecorator = new SubMenuDecorator(
                    menuComponent, menuComponent.getMenuId(), null,
                    menuComponent.isRemoveAllWhenShown(facesContext),
                    getItemImageWidth(menuComponent),
                    getItemImageHeight(menuComponent));

            if (menuDecorators == null) {
                menuDecorators = menuDecorator;
                continue;
            }

            menuDecorator.addChildDecorator(menuDecorators);
            menuDecorators = menuDecorator;
        }

        if (menuDecorators != null) {
            decorator.addChildDecorator(menuDecorators);
        }

        return decorator;
    }

    protected int getItemImageHeight(IMenuComponent menuComponent) {
        return -1;
    }

    protected int getItemImageWidth(IMenuComponent menuComponent) {
        return -1;
    }

    public void encodeNodes(IJavaScriptWriter jsWriter,
            TreeComponent treeComponent, ISelectItemNodeWriter nodeRenderer,
            int depth, String containerVarId) throws WriterException {

        TreeDecorator selectItemNodeWriter = (TreeDecorator) getComponentDecorator(jsWriter
                .getHtmlComponentRenderContext());

        selectItemNodeWriter.encodeNodes(jsWriter, treeComponent, nodeRenderer,
                depth, containerVarId);
    }

    public ISelectItemNodeWriter getSelectItemNodeWriter(
            IComponentRenderContext componentRenderContext) {
        return (ISelectItemNodeWriter) getComponentDecorator(componentRenderContext);
    }

    @Override
    public Object getConvertedValue(FacesContext context,
            UIComponent component, Object submittedValue)
            throws ConverterException {

        if (submittedValue == null
                || submittedValue.getClass().isArray() == false
                || Array.getLength(submittedValue) < 1) {
            return super.getConvertedValue(context, component, submittedValue);
        }

        Object array[] = (Object[]) submittedValue;
        Object ret[] = new Object[array.length];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = super.getConvertedValue(context, component, array[i]);
        }

        return ret;
    }

    protected String getCommandNodeImageURL(IHtmlComponentWriter htmlWriter) {
        return null;
    }

    protected String getCommandNodeOpenedImageURL(
            IHtmlComponentWriter htmlWriter) {
        return null;
    }

    protected String getCommandNodeDisabledImageURL(
            IHtmlComponentWriter htmlWriter) {
        return null;
    }

    protected String getCommandRootImageURL(IHtmlComponentWriter htmlWriter) {
        return null;
    }

    protected String getCommandRootOpenedImageURL(
            IHtmlComponentWriter htmlWriter) {
        return null;
    }

    protected String getCommandRootDisabledImageURL(
            IHtmlComponentWriter htmlWriter) {
        return null;
    }

    protected String getCommandLeafImageURL(IHtmlComponentWriter htmlWriter) {
        return null;
    }

    protected String getCommandLeafDisabledImageURL(
            IHtmlComponentWriter htmlWriter) {
        return null;
    }

    protected boolean isCheckboxInderminateSupported(
            IHtmlRenderContext htmlRenderContext) {

        IClientBrowser clientBrowser = htmlRenderContext
                .getHtmlProcessContext().getClientBrowser();

        if (clientBrowser == null) {
            return false;
        }

        switch (clientBrowser.getBrowserType()) {
        case FIREFOX:
            if (clientBrowser.getMajorVersion() > 3) {
                return true;
            }
            if (clientBrowser.getMajorVersion() == 3
                    && clientBrowser.getMinorVersion() >= 6) {
                return true;
            }

            break;

        case CHROME:
            return true;

        case MICROSOFT_INTERNET_EXPLORER:
            if (clientBrowser.getMajorVersion() >= 9) {
                return true;
            }
            break;

        case SAFARI:
            if (clientBrowser.getMajorVersion() >= 3) {
                return true;
            }
            break;

        case OPERA:
            if (clientBrowser.getMajorVersion() > 10) {
                return true;
            }
            if (clientBrowser.getMajorVersion() == 10
                    && clientBrowser.getMinorVersion() >= 60) {
                return true;
            }
            break;
  
}
        return false;
    }

    protected String getCheckboxInderminateImageURL(IHtmlWriter htmlWriter) {
        if (isCheckboxInderminateSupported(htmlWriter
                .getHtmlComponentRenderContext().getHtmlRenderContext())) {
            return null;
        }

        return computeAndCacheImageURL(htmlWriter, null,
                "button/3states_indeterminated_xp.gif");
    }

    protected String getCheckboxCheckedImageURL(IHtmlWriter htmlWriter) {

        return computeAndCacheImageURL(htmlWriter, null,
                "button/3states_checked_xp.gif");
    }

    protected String getCheckboxUncheckedImageURL(IHtmlWriter htmlWriter) {

        return computeAndCacheImageURL(htmlWriter, null,
                "button/3states_unchecked_xp.gif");
    }

  
}