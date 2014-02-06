/*
 * $Id: TreeDecorator.java,v 1.3 2013/11/13 12:53:31 jbmeslin Exp $
 */
package org.rcfaces.renderkit.html.internal.decorator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.faces.component.UIComponent;
import javax.faces.component.UISelectItem;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;

import org.rcfaces.core.component.TreeComponent;
import org.rcfaces.core.component.TreeNodeComponent;
import org.rcfaces.core.component.capability.ICardinality;
import org.rcfaces.core.component.capability.IDragAndDropEffects;
import org.rcfaces.core.component.capability.IDraggableCapability;
import org.rcfaces.core.component.capability.IDroppableCapability;
import org.rcfaces.core.component.capability.IMenuPopupIdCapability;
import org.rcfaces.core.internal.component.IImageAccessors;
import org.rcfaces.core.internal.contentAccessor.IContentAccessor;
import org.rcfaces.core.internal.lang.StringAppender;
import org.rcfaces.core.internal.renderkit.IComponentData;
import org.rcfaces.core.internal.renderkit.IComponentRenderContext;
import org.rcfaces.core.internal.renderkit.IRequestContext;
import org.rcfaces.core.internal.renderkit.WriterException;
import org.rcfaces.core.internal.tools.CheckTools;
import org.rcfaces.core.internal.tools.CollectionTools;
import org.rcfaces.core.internal.tools.ExpansionTools;
import org.rcfaces.core.internal.tools.ImageAccessorTools;
import org.rcfaces.core.internal.tools.SelectItemMappers;
import org.rcfaces.core.internal.tools.SelectionTools;
import org.rcfaces.core.internal.tools.ValuesTools;
import org.rcfaces.core.item.IClientDataItem;
import org.rcfaces.core.item.IDraggableItem;
import org.rcfaces.core.item.IDroppableItem;
import org.rcfaces.core.item.IImagesItem;
import org.rcfaces.core.item.IStyleClassItem;
import org.rcfaces.core.item.ITreeNode;
import org.rcfaces.core.item.IVisibleItem;
import org.rcfaces.core.item.TreeNode;
import org.rcfaces.core.lang.OrderedSet;
import org.rcfaces.core.util.SelectItemTreeTools;
import org.rcfaces.core.util.SelectItemTreeTools.INodeHandler;
import org.rcfaces.core.util.SelectItemTreeTools.SelectItemNode;
import org.rcfaces.renderkit.html.internal.HtmlValuesTools;
import org.rcfaces.renderkit.html.internal.IHtmlRenderContext;
import org.rcfaces.renderkit.html.internal.IHtmlWriter;
import org.rcfaces.renderkit.html.internal.IJavaScriptWriter;
import org.rcfaces.renderkit.html.internal.IObjectLiteralWriter;
import org.rcfaces.renderkit.html.internal.service.TreeService;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:31 $
 */
public class TreeDecorator extends AbstractSelectItemsDecorator {

    private static final String IMAGES_PROPERTY = "treeRender.images";

    private static final Object[] OBJECT_EMPTY_ARRAY = new Object[0];

    private static final boolean USE_VALUE = false;

    private final Converter converter;

    private DragWalker dragWalker;

    private DropWalker dropWalker;

    private Set<String> interactiveParentsVarId;

    private List<SchrodingerNode> schrodingerNodesStack = null;

    private SchrodingerNode unloadedParentSchrodingerNode = null;

    private List<SchrodingerNode> schrodingerNodes;

    public TreeDecorator(TreeComponent component) {
        super(component, component.getFilterProperties());

        converter = component.getConverter();

        if (component.isDraggable()) {
            dragWalker = new DragWalker(component);
        }

        if (component.isDroppable()) {
            dropWalker = new DropWalker(component);
        }

        if (component.isSchrodingerCheckable()) {
            schrodingerNodesStack = new LinkedList<TreeDecorator.SchrodingerNode>();

            schrodingerNodesStack.add(new SchrodingerNode(null));

            schrodingerNodes = new ArrayList<TreeDecorator.SchrodingerNode>();
        }
    }

    protected void preEncodeContainer() throws WriterException {

        IComponentRenderContext componentContext = htmlWriter
                .getComponentRenderContext();

        htmlWriter.getJavaScriptEnableMode().enableOnInit();

        mapSelectItems(componentContext, SelectItemMappers.SEARCH_IMAGE_MAPPER);

        super.preEncodeContainer();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.rcfaces.core.internal.renderkit.html.AbstractSelectItemsRenderer#
     * encodeComponentsBegin
     * (org.rcfaces.core.internal.renderkit.html.SelectItemsContext)
     */
    protected void encodeComponentsBegin() throws WriterException {
        super.encodeComponentsBegin();

        TreeRenderContext treeRenderContext = (TreeRenderContext) getContext();

        TreeComponent treeComponent = (TreeComponent) treeRenderContext
                .getRootComponent();

        FacesContext facesContext = javaScriptWriter.getFacesContext();

        boolean write = false;
        String urls[] = new String[9];

        String defaultImageURL = treeComponent.getDefaultImageURL(facesContext);
        if (defaultImageURL != null) {
            write = true;
            urls[0] = allocateImage(javaScriptWriter, defaultImageURL);
            if (urls[0] != null) {
                write = true;
            }

            // urls[0] = javaScriptWriter.allocateString(defaultImageURL);
        }

        String defaultExpandedImageURL = treeComponent
                .getDefaultExpandedImageURL(facesContext);
        if (defaultExpandedImageURL != null) {
            urls[1] = allocateImage(javaScriptWriter, defaultExpandedImageURL);
            if (urls[1] != null) {
                write = true;
            }

            // urls[1] =
            // javaScriptWriter.allocateString(defaultExpandedImageURL);
        }

        String defaultCollapsedImageURL = treeComponent
                .getDefaultCollapsedImageURL(facesContext);
        if (defaultCollapsedImageURL != null) {
            urls[2] = allocateImage(javaScriptWriter, defaultCollapsedImageURL);
            if (urls[2] != null) {
                write = true;
            }
            // urls[2] =
            // javaScriptWriter.allocateString(defaultCollapsedImageURL);
        }

        String defaultSelectedImageURL = treeComponent
                .getDefaultSelectedImageURL(facesContext);
        if (defaultSelectedImageURL != null) {
            urls[3] = allocateImage(javaScriptWriter, defaultSelectedImageURL);
            if (urls[3] != null) {
                write = true;
            }

            // urls[3] =
            // javaScriptWriter.allocateString(defaultSelectedImageURL);
        }

        String defaultDisabledImageURL = treeComponent
                .getDefaultDisabledImageURL(facesContext);
        if (defaultDisabledImageURL != null) {
            urls[4] = allocateImage(javaScriptWriter, defaultDisabledImageURL);
            if (urls[4] != null) {
                write = true;
            }
            // urls[4] =
            // javaScriptWriter.allocateString(defaultDisabledImageURL);
        }

        String defaultLeafImageURL = treeComponent
                .getDefaultLeafImageURL(facesContext);
        if (defaultLeafImageURL != null) {
            urls[5] = allocateImage(javaScriptWriter, defaultLeafImageURL);
            // urls[5] = javaScriptWriter.allocateString(defaultLeafImageURL);
            if (urls[5] != null) {
                write = true;
            }
        }

        String defaultExpandedLeafImageURL = treeComponent
                .getDefaultExpandedLeafImageURL(facesContext);
        if (defaultExpandedLeafImageURL != null) {
            urls[6] = allocateImage(javaScriptWriter,
                    defaultExpandedLeafImageURL);
            if (urls[6] != null) {
                write = true;
            }
            // urls[6] =
            // javaScriptWriter.allocateString(defaultExpandedLeafImageURL);
        }

        String defaultSelectedLeafImageURL = treeComponent
                .getDefaultSelectedLeafImageURL(facesContext);
        if (defaultSelectedLeafImageURL != null) {
            urls[7] = allocateImage(javaScriptWriter,
                    defaultSelectedLeafImageURL);
            if (urls[7] != null) {
                write = true;
            }
            // urls[7] =
            // javaScriptWriter.allocateString(defaultSelectedLeafImageURL);
        }

        String defaultDisabledLeafImageURL = treeComponent
                .getDefaultDisabledLeafImageURL(facesContext);
        if (defaultDisabledLeafImageURL != null) {
            urls[8] = allocateImage(javaScriptWriter,
                    defaultDisabledLeafImageURL);
            if (urls[8] != null) {
                write = true;
            }
            // urls[8] =
            // javaScriptWriter.allocateString(defaultDisabledLeafImageURL);
        }

        if (write == false) {
            return;
        }

        javaScriptWriter.writeMethodCall("f_setDefaultImages");
        int pred = 0;
        for (int i = 0; i < urls.length; i++) {
            String url = urls[i];
            if (url == null) {
                pred++;
                continue;
            }

            if (i - pred > 0) {
                javaScriptWriter.write(',');
            }

            for (; pred > 0; pred--) {
                javaScriptWriter.writeNull().write(',');
            }

            javaScriptWriter.write(url);
        }

        javaScriptWriter.writeln(");");
    }

    private String allocateImage(IJavaScriptWriter javaScriptWriter,
            String imageURL) throws WriterException {

        FacesContext facesContext = javaScriptWriter.getFacesContext();

        IImageAccessors accessors = (IImageAccessors) ImageAccessorTools
                .createImageAccessor(facesContext, imageURL);
        if (accessors == null) {
            return null;
        }

        IContentAccessor imageAccessor = accessors.getImageAccessor();
        if (imageAccessor == null) {
            return null;
        }

        String contentImageURL = imageAccessor.resolveURL(facesContext, null,
                null);
        if (contentImageURL == null) {
            return null;
        }

        String url = javaScriptWriter.allocateString(contentImageURL);

        return url;
    }

    protected void encodeComponentsEnd() throws WriterException {

        TreeRenderContext treeRenderContext = (TreeRenderContext) getContext();

        if (treeRenderContext.isCheckable()
                && treeRenderContext.writeCheckFullState()) {
            writeFullStates(javaScriptWriter, "f_setCheckStates",
                    treeRenderContext.getCheckValues());
        }

        if (treeRenderContext.isSelectable()
                && treeRenderContext.writeSelectionFullState()) {
            writeFullStates(javaScriptWriter, "f_setSelectionStates",
                    treeRenderContext.getSelectionValues());
        }

        if (schrodingerNodes != null && schrodingerNodes.size() > 0) {
            writeSchrodingerStates(javaScriptWriter);
        }

        if (interactiveParentsVarId != null) {
            writeInteractiveParentsVarId(javaScriptWriter);
        }

        super.encodeComponentsEnd();
    }

    protected void writeInteractiveParentsVarId(
            IJavaScriptWriter javaScriptWriter) throws WriterException {
        javaScriptWriter.writeMethodCall("f_setInteractiveParent");

        boolean first = true;
        for (String id : interactiveParentsVarId) {
            if (first) {
                first = false;
            } else {
                javaScriptWriter.write(',');
            }
            javaScriptWriter.write(id);
        }

        javaScriptWriter.writeln(");");

    }

    protected void writeSchrodingerStates(IJavaScriptWriter javaScriptWriter)
            throws WriterException {

        javaScriptWriter.writeMethodCall("f_setSchrodingerStates");

        boolean first = true;
        for (SchrodingerNode node : schrodingerNodes) {

            if (node.varId == null) {
                continue;
            }
            if (node.checkedCount == 0 || node.checkedCount == node.childCount) {
                continue;
            }

            if (first) {
                first = false;
                javaScriptWriter.write('[');
            } else {
                javaScriptWriter.write(',');
            }
            javaScriptWriter.write(node.varId);
        }
        if (first == false) {
            javaScriptWriter.write(']');
        }

        boolean first2 = true;
        for (SchrodingerNode node : schrodingerNodes) {

            if (node.varId == null) {
                continue;
            }
            if (node.checkedCount != node.childCount) {
                continue;
            }

            if (first2) {
                first2 = false;
                if (first) {
                    javaScriptWriter.writeNull();
                }
                javaScriptWriter.write(",[");
            } else {
                javaScriptWriter.write(',');
            }
            javaScriptWriter.write(node.varId);
        }
        if (first2 == false) {
            javaScriptWriter.write(']');
        }

        javaScriptWriter.writeln(");");
    }

    private void writeFullStates(IJavaScriptWriter jsWriter, String jsCommand,
            Set objects) throws WriterException {
        if (objects == null || objects.isEmpty()) {
            return;
        }

        IComponentRenderContext componentRenderContext = javaScriptWriter
                .getHtmlComponentRenderContext();

        jsWriter.writeMethodCall(jsCommand).write('[');
        int i = 0;
        for (Iterator it = objects.iterator(); it.hasNext();) {
            Object value = it.next();

            String convert = convertItemValue(componentRenderContext, value);

            if (convert == null) {
                continue;
            }

            if (i > 0) {
                jsWriter.write(',');
            }

            jsWriter.writeString(convert);

            i++;
        }

        jsWriter.writeln("]);");

    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.rcfaces.core.internal.renderkit.html.SelectItemsRenderer#
     * encodeTreeNodeBegin
     * (org.rcfaces.core.internal.renderkit.html.SelectItemsRenderer
     * .TreeContext, javax.faces.component.UIComponent,
     * javax.faces.model.SelectItem)
     */
    public int encodeNodeBegin(UIComponent component, SelectItem selectItem,
            boolean hasChild, boolean isVisible) throws WriterException {

        TreeRenderContext treeRenderContext = (TreeRenderContext) getContext();

        SchrodingerNode schrodingerNode = null;
        if (schrodingerNodesStack != null) {
            if (hasChild == false) {
                SchrodingerNode parentSchrodingerNode = schrodingerNodesStack
                        .get(0);

                if (treeRenderContext.isValueChecked(selectItem,
                        selectItem.getValue())) {
                    parentSchrodingerNode.checkedCount++;
                }

                parentSchrodingerNode.childCount++;

            } else {
                schrodingerNode = new SchrodingerNode(selectItem);
                schrodingerNodesStack.add(0, schrodingerNode);

                treeRenderContext.removeValueChecked(selectItem);
            }

            if (unloadedParentSchrodingerNode != null) {
                return EVAL_NODE;
            }
        }

        if (selectItem instanceof IVisibleItem) {
            if (((IVisibleItem) selectItem).isVisible() == false) {
                return SKIP_NODE;
            }
        }

        String parentVarId = null;

        if (treeRenderContext.countVarId() > 0) {
            parentVarId = treeRenderContext.peekVarId();
        }

        /*
         * System.out.println("isv=" + isVisible + "/" + selectItem.getLabel() +
         * "/" + selectItem.getValue());
         */
        if (isVisible == false) {
            int preloadLevelDepth = treeRenderContext.getPreloadedLevelDepth();
            if (preloadLevelDepth > 0
                    && preloadLevelDepth < treeRenderContext.getDepth()) {
                if (treeRenderContext.isFirstInteractiveChild(parentVarId)) {

                    if (interactiveParentsVarId == null) {
                        interactiveParentsVarId = new HashSet<String>();
                    }
                    interactiveParentsVarId.add(parentVarId);
                }

                if (schrodingerNodesStack != null
                        && treeRenderContext.getCheckValues().size() > 0) {

                    // Il faut rechercher les checks ...
                    unloadedParentSchrodingerNode = schrodingerNodesStack
                            .get(0);

                    return EVAL_NODE;
                }

                return SKIP_NODE;
            }
        }

        String varId = javaScriptWriter.getJavaScriptRenderContext()
                .allocateVarName();

        treeRenderContext.pushVarId(varId);

        if (schrodingerNode != null) {
            schrodingerNode.varId = varId;
        }

        javaScriptWriter.write("var ").write(varId);

        if (parentVarId == null) {
            parentVarId = javaScriptWriter.getComponentVarName();
        }

        javaScriptWriter.write('=').writeMethodCall("f_appendNode2")
                .write(parentVarId).write(',');

        writeNodeObject(treeRenderContext, selectItem, false, hasChild);

        javaScriptWriter.writeln(");");

        return EVAL_NODE;
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.rcfaces.core.internal.renderkit.html.SelectItemsRenderer#
     * encodeTreeNodeEnd
     * (org.rcfaces.core.internal.renderkit.html.SelectItemsRenderer
     * .TreeContext, javax.faces.component.UIComponent,
     * javax.faces.model.SelectItem)
     */
    public void encodeNodeEnd(UIComponent component, SelectItem selectItem,
            boolean hasChild, boolean isVisible) {

        TreeRenderContext treeRenderContext = (TreeRenderContext) getContext();

        if (schrodingerNodesStack != null) {
            if (hasChild) {
                SchrodingerNode schrodingerNode = schrodingerNodesStack
                        .remove(0);

                if (schrodingerNode.checkedCount > 0) {
                    schrodingerNodes.add(schrodingerNode);
                }

                SchrodingerNode parent = schrodingerNodesStack.get(0);

                parent.checkedCount += schrodingerNode.checkedCount;
                parent.childCount += schrodingerNode.childCount; // NON PAS LES
                                                                 // PARENTS + 1;
            }

            if (unloadedParentSchrodingerNode != null
                    && unloadedParentSchrodingerNode.selectItem == selectItem) {
                unloadedParentSchrodingerNode = null;
            }
        }

        if (isVisible == false) {
            int preloadLevelDepth = treeRenderContext.getPreloadedLevelDepth();
            if (preloadLevelDepth > 0
                    && preloadLevelDepth < treeRenderContext.getDepth()) {
                return;
            }
        }

        treeRenderContext.popVarId();

        if (dragWalker != null) {
            dragWalker.pop(selectItem);
        }

        if (dropWalker != null) {
            dropWalker.pop(selectItem);
        }
    }

    /**
     * Called from the {@link TreeService} to refresh the father node of the
     * newly rendered nodes
     * 
     * @param selectItem
     *            father node
     * @param hasChild
     * @throws WriterException
     */
    public void refreshNode(SelectItem selectItem, boolean hasChild)
            throws WriterException {

        TreeRenderContext treeRenderContext = (TreeRenderContext) getContext();

        javaScriptWriter.getComponentVarName();

        javaScriptWriter.writeMethodCall("f_refreshNode");

        Object selectItemValue = selectItem.getValue();

        String value = convertItemValue(
                javaScriptWriter.getHtmlComponentRenderContext(),
                selectItemValue);
        javaScriptWriter.writeString(value).write(',');

        writeNodeObject(treeRenderContext, selectItem, true, hasChild);

        javaScriptWriter.writeln(");");

        if (dragWalker != null) {
            dragWalker.pop(selectItem);
        }

        if (dropWalker != null) {
            dropWalker.pop(selectItem);
        }
    }

    private void writeNodeObject(TreeRenderContext treeRenderContext,
            SelectItem selectItem, boolean refreshNode, boolean hasChild)
            throws WriterException {

        IObjectLiteralWriter objectLiteralWriter = javaScriptWriter
                .writeObjectLiteral(false);

        Object selectItemValue = selectItem.getValue();

        if (refreshNode == false) {
            String value = convertItemValue(
                    javaScriptWriter.getHtmlComponentRenderContext(),
                    selectItemValue);
            if (value != null) {
                objectLiteralWriter.writeSymbol("_value").writeString(value);
            }
        }

        String text = selectItem.getLabel();
        if (text != null) {
            objectLiteralWriter.writeSymbol("_label").writeString(text);
        }

        String toolTip = selectItem.getDescription();
        if (toolTip != null) {
            objectLiteralWriter.writeSymbol("_description")
                    .writeString(toolTip);
        }

        if (selectItem.isDisabled()) {
            objectLiteralWriter.writeSymbol("_disabled").writeBoolean(true);
        }

        if (treeRenderContext.isUserExpandable()) {
            if (treeRenderContext.isValueExpanded(selectItem, selectItemValue)) { // Expand
                objectLiteralWriter.writeSymbol("_expanded").writeBoolean(true);
            }
        }

        if (treeRenderContext.writeSelectionFullState() == false) {
            if (treeRenderContext.isValueSelected(selectItem, selectItemValue)) { // SELECTION
                objectLiteralWriter.writeSymbol("_selected").writeBoolean(true);
            }
        }

        if (treeRenderContext.writeCheckFullState() == false) {
            if (treeRenderContext.isValueChecked(selectItem, selectItemValue)) { // CHECK
                objectLiteralWriter.writeSymbol("_checked").writeBoolean(true);
            }
        }

        if (selectItem instanceof IStyleClassItem) {
            String styleClass = ((IStyleClassItem) selectItem).getStyleClass();
            if (styleClass != null) {
                objectLiteralWriter.writeSymbol("_styleClass").writeString(
                        styleClass);
            }
        }

        if (selectItem instanceof IImagesItem) {
            writeSelectItemImages((IImagesItem) selectItem, javaScriptWriter,
                    null, null, false, objectLiteralWriter);
        }

        if (selectItem instanceof IClientDataItem) {
            writeItemClientDatas((IClientDataItem) selectItem,
                    javaScriptWriter, null, null, objectLiteralWriter);
        }

        if (selectItem instanceof IMenuPopupIdCapability) {
            String menuPopupId = ((IMenuPopupIdCapability) selectItem)
                    .getMenuPopupId();
            if (menuPopupId != null) {
                objectLiteralWriter.writeSymbol("_menuPopupId").writeString(
                        menuPopupId);
            }
        }

        if (refreshNode) {
            objectLiteralWriter.writeSymbol("_hasChild").writeBoolean(hasChild);
        }

        if (dragWalker != null) {
            dragWalker.pushAndWriteAttributes(objectLiteralWriter, selectItem);
        }

        if (dropWalker != null) {
            dropWalker.pushAndWriteAttributes(objectLiteralWriter, selectItem);
        }

        objectLiteralWriter.end();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.rcfaces.core.internal.renderkit.html.SelectItemsRenderer#createContext
     * (org.rcfaces.core.internal.renderkit.html.IJavaScriptWriter)
     */
    protected SelectItemsContext createJavaScriptContext() {
        IComponentRenderContext componentRenderContext = javaScriptWriter
                .getHtmlComponentRenderContext();
        TreeComponent treeComponent = (TreeComponent) componentRenderContext
                .getComponent();

        return createContext(componentRenderContext, treeComponent, this, 0,
                true, null);
    }

    private SelectItemsContext createContext(
            IComponentRenderContext componentRenderContext,
            TreeComponent treeComponent, ISelectItemNodeWriter nodeRenderer,
            int depth, boolean sendFullStates, String containerVarId) {
        return new TreeRenderContext(nodeRenderer, componentRenderContext,
                treeComponent, depth, sendFullStates, containerVarId);
    }

    public void encodeNodes(IJavaScriptWriter javaScriptWriter,
            TreeComponent treeComponent, ISelectItemNodeWriter nodeRenderer,
            int depth, String containerVarId) throws WriterException {

        this.javaScriptWriter = javaScriptWriter;
        try {
            SelectItemsContext selectItemsContext = createContext(
                    javaScriptWriter.getHtmlComponentRenderContext(),
                    treeComponent, nodeRenderer, depth, false, containerVarId);

            this.selectItemsContext = selectItemsContext;

            super.encodeNodes(treeComponent);

            if (schrodingerNodes != null && schrodingerNodes.size() > 0) {
                writeSchrodingerStates(javaScriptWriter);
            }

            if (interactiveParentsVarId != null) {
                writeInteractiveParentsVarId(javaScriptWriter);
            }

            nodeRenderer.refreshNode(treeComponent);

        } finally {
            this.javaScriptWriter = null;
            this.selectItemsContext = null;
        }
    }

    protected void encodeNodes(TreeComponent treeComponent)
            throws WriterException {

        IHtmlRenderContext htmlRenderContext = (IHtmlRenderContext) javaScriptWriter
                .getHtmlComponentRenderContext().getRenderContext();

        String interactiveComponentClientId = htmlRenderContext
                .getCurrentInteractiveRenderComponentClientId();

        if (interactiveComponentClientId != null) {
            // Pas de donn�es si nous sommes dans un scope interactif !
            javaScriptWriter.writeMethodCall("_setInteractiveShow").write('"')
                    .write(interactiveComponentClientId).writeln("\");");
            return;
        }

        super.encodeNodes(treeComponent);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.rcfaces.core.internal.renderkit.html.SelectItemsRenderer#createContext
     * (org.rcfaces.core.internal.renderkit.IWriter)
     */

    protected IHtmlRenderContext getHtmlRenderContext(IHtmlWriter writer) {
        return (IHtmlRenderContext) writer.getComponentRenderContext()
                .getRenderContext();
    }

    protected SelectItemsContext createHtmlContext() {
        return null;
    }

    protected void addUnlockProperties(Set<String> unlockedProperties) {
        super.addUnlockProperties(unlockedProperties);

        unlockedProperties.add("checkedItems");
        unlockedProperties.add("uncheckedItems");
        unlockedProperties.add("selectedItems");
        unlockedProperties.add("unselectedItems");
        unlockedProperties.add("expandedItems");
        unlockedProperties.add("collapsedItems");
    }

    @SuppressWarnings("unused")
    public void decode(IRequestContext context, UIComponent component,
            IComponentData componentData) {
        super.decode(context, component, componentData);

        FacesContext facesContext = context.getFacesContext();

        final TreeComponent tree = (TreeComponent) component;

        boolean checkable = tree.isCheckable(facesContext);

        String checkedValues = componentData.getStringProperty("checkedItems");
        String uncheckedValues = componentData
                .getStringProperty("uncheckedItems");
        if (checkable && (checkedValues != null || uncheckedValues != null)) {
            // C'est checkable , le check est forcement sur la VALUE !

            Object v = tree.getCheckedValues(facesContext);
            if (USE_VALUE) {
                if (v == null) {
                    // Le CHECK est sur la value !
                    v = HtmlValuesTools.convertValuesToSet(facesContext, tree,
                            tree.getValue());

                } else {
                    // On retire la checkValue pour n'utiliser que la value !
                    tree.setCheckedValues(null);
                }
            }

            Set<Object> values = CollectionTools.valuesToSet(v, false);

            if (HtmlValuesTools.updateValues(facesContext, tree, false, values,
                    checkedValues, uncheckedValues)) {

                if (USE_VALUE) {
                    ValuesTools.setValues(facesContext, tree, values);

                } else {
                    CheckTools.setCheckValues(facesContext, tree, values);
                }
            }
        }

        String selectedValues = componentData
                .getStringProperty("selectedItems");
        String deselectedValues = componentData
                .getStringProperty("deselectedItems");
        if (selectedValues != null || deselectedValues != null) {
            if (USE_VALUE && checkable == false) {
                // La selection est FORCEMENT sur la value !
                Object v = tree.getSelectedValues(facesContext);
                if (v != null) {
                    // On retire la selectionValue pour n'utiliser que la value
                    // !
                    tree.setSelectedValues(null);
                }

                // Set values = ValuesTools.valueToSet(v, true);

                // On recommence à ZERO ! ???? pourquoi ? nous sommes en full
                // state ?
                Set<Object> values = new OrderedSet<Object>();

                if (HtmlValuesTools.updateValues(facesContext, tree, true,
                        values, selectedValues, deselectedValues)) {

                    int selectionCardinality = tree
                            .getSelectionCardinality(facesContext);

                    if (selectionCardinality == ICardinality.ONE_CARDINALITY
                            || selectionCardinality == ICardinality.OPTIONAL_CARDINALITY) {
                        // On prend le premier seulement !

                        if (values.size() < 2) {
                            ValuesTools.setValues(facesContext, tree, values);

                        } else {
                            // On prend le premier !
                            Set<Object> set = new HashSet<Object>(1);
                            set.add(values.iterator().next());

                            ValuesTools.setValues(facesContext, tree, set);
                        }

                    } else {
                        ValuesTools.setValues(facesContext, tree, values);
                    }
                }
            } else {
                // Il y a un CHECK !

                // Object v = tree.getSelectionValues(facesContext);

                // Set values = ValuesTools.valueToSet(v, true);

                // On recommence à ZERO ! ??? nous sommes en full state ?
                Set<Object> values = new OrderedSet<Object>();

                HtmlValuesTools.updateValues(facesContext, tree, true, values,
                        selectedValues, deselectedValues);

                SelectionTools.setSelectionValues(facesContext, tree, values);
            }
        }

        String expandedValues = componentData
                .getStringProperty("expandedItems");
        String collapsedValues = componentData
                .getStringProperty("collapsedItems");
        if (collapsedValues != null || expandedValues != null) {
            Set<Object> expansionValues = ExpansionTools.expansionValuesToSet(
                    facesContext, component, false);

            if (HtmlValuesTools.updateValues(facesContext, tree, true,
                    expansionValues, expandedValues, collapsedValues)) {

                ExpansionTools.setExpansionValues(facesContext, tree,
                        expansionValues);
            }
        }

        String schrodingerStates = componentData
                .getStringProperty("schrodingerStates");
        if (schrodingerStates != null) {

            final Set<Object> checkedValuesSet = CheckTools.checkValuesToSet(
                    facesContext, tree, true);

            SelectItemNode root = SelectItemTreeTools.constructTree(
                    facesContext, component, null, null);

            if (schrodingerStates != null) {
                List<Object> states = HtmlValuesTools.parseValues(facesContext,
                        component, false, false, schrodingerStates);

                for (Object stateObject : states) {
                    String state = (String) stateObject;
                    final boolean check = (state.charAt(0) == '+');
                    state = state.substring(1);

                    Object value = HtmlValuesTools.convertStringToValue(
                            facesContext, component, state, true);
                    if (value == null) {
                        continue;
                    }

                    SelectItemNode selectItemNode = root.searchByValue(value);
                    if (selectItemNode == null) {
                        continue;
                    }

                    selectItemNode.forEachNode(new INodeHandler<Boolean>() {

                        public Boolean process(SelectItemNode node) {
                            if (node.hasChildren()) {
                                return null;
                            }

                            Object value = node.getSelectItem().getValue();

                            if (check) {
                                if (checkedValuesSet.contains(value)) {
                                    return null;
                                }

                                tree.check(value);
                                return null;
                            }

                            if (checkedValuesSet.contains(value) == false) {
                                return null;
                            }

                            tree.uncheck(value);
                            return null;
                        }
                    });

                }
            }
        }
    }

    protected Converter getConverter() {
        return converter;
    }

    /*
     * private Set convertSelection(Object selection) { if (selection instanceof
     * Object[]) { return new OrderedSet(Arrays.asList((Object[]) selection)); }
     * 
     * if (selection instanceof Collection) { return new OrderedSet((Collection)
     * selection); }
     * 
     * throw new FacesException( "Bad type of value for attribute
     * selectedValues/checkedValues !"); }
     */

    protected SelectItem convertToSelectItem(Object value) {
        if (value instanceof ITreeNode) {
            ITreeNode treeNode = (ITreeNode) value;

            return new TreeNode(treeNode);
        }

        return super.convertToSelectItem(value);
    }

    protected SelectItem createSelectItem(UISelectItem component) {
        if (component instanceof TreeNodeComponent) {
            return new TreeNode((TreeNodeComponent) component);
        }

        return super.createSelectItem(component);
    }

    protected static class DragDropItemState {
        private final SelectItem draggleItem;

        private final UIComponent component;

        private final Integer draggableEffects;

        private final String serializedDraggableTypes;

        private final String[] dragDropTypes;

        public DragDropItemState(UIComponent component,
                Integer draggableEffects, String serializedDraggableTypes,
                String[] dragDropTypes) {

            this.component = component;
            this.draggleItem = null;
            this.draggableEffects = draggableEffects;
            this.serializedDraggableTypes = serializedDraggableTypes;
            this.dragDropTypes = dragDropTypes;
        }

        public DragDropItemState(SelectItem draggleItem,
                Integer draggableEffects, String draggableTypes,
                String[] dragDropTypes) {

            this.component = null;
            this.draggleItem = draggleItem;
            this.draggableEffects = draggableEffects;
            this.serializedDraggableTypes = draggableTypes;
            this.dragDropTypes = dragDropTypes;
        }

        public SelectItem getDragDropItem() {
            return draggleItem;
        }

        public Integer getDragDropEffects() {
            return draggableEffects;
        }

        public String getSerializedDragDropTypes() {
            return serializedDraggableTypes;
        }

        public String[] getDragDropTypes() {
            return dragDropTypes;
        }
    }

    protected abstract class AbstractDragDropWalker {

        private final List<DragDropItemState> dragDropSelectItemStates;

        private final List<Object> selectItemLastDragDropInfos;

        private String[] cachedDragDropTypes;

        private String cachedDerializedDragDropTypes;

        public AbstractDragDropWalker(int dragDropEffects,
                String dragDropTypes[]) {
            dragDropSelectItemStates = new ArrayList<DragDropItemState>(8);
            selectItemLastDragDropInfos = new ArrayList<Object>(8);

            if (dragDropEffects < 0) {
                dragDropEffects = IDragAndDropEffects.DEFAULT_DND_EFFECT;
            }

            Integer dragEffects = new Integer(dragDropEffects);
            String serializedDragTypes = serializeDragType(dragDropTypes);

            dragDropSelectItemStates.add(new DragDropItemState(component,
                    dragEffects, serializedDragTypes, dragDropTypes));

            selectItemLastDragDropInfos.add(serializedDragTypes);
            selectItemLastDragDropInfos.add(dragEffects);

        }

        public void pop(SelectItem selectItem) {

            if (getCurrentDragDropItemState(0).getDragDropItem() == selectItem) {
                dragDropSelectItemStates
                        .remove(dragDropSelectItemStates.size() - 1);
            }

            int size = selectItemLastDragDropInfos.size();
            selectItemLastDragDropInfos.remove(size - 1);
            selectItemLastDragDropInfos.remove(size - 2);
        }

        public void pushAndWriteAttributes(
                IObjectLiteralWriter objectLiteralWriter, SelectItem selectItem)
                throws WriterException {

            Integer dragDropEffects = null;
            String serializedDragDropTypes = null;
            String dragDropTypes[] = null;
            boolean effectsFound = false;
            boolean typesFound = false;

            if (isDragDropItem(selectItem)) {
                int d = getDragDropEffects(selectItem, selectItem);
                if (d >= 0) {
                    dragDropEffects = new Integer(d);
                    effectsFound = true;
                }

                dragDropTypes = getDragDropTypes(selectItem, selectItem);
                if (dragDropTypes != null) {
                    serializedDragDropTypes = serializeDragType(dragDropTypes);
                    typesFound = true;
                }
            }

            for (int i = 0; effectsFound == false && typesFound == false; i++) {
                DragDropItemState draggableItemState = getCurrentDragDropItemState(i);
                if (draggableItemState == null) {
                    break;
                }

                SelectItem draggableItem = draggableItemState.getDragDropItem();
                if (draggableItem == null) { // C'est le component ! (retour à
                    // la racine)
                    if (effectsFound == false) {
                        dragDropEffects = draggableItemState
                                .getDragDropEffects();
                        effectsFound = true;
                    }
                    if (typesFound == false) {
                        dragDropTypes = draggableItemState.getDragDropTypes();
                        serializedDragDropTypes = draggableItemState
                                .getSerializedDragDropTypes();
                        typesFound = true;
                    }

                    break;
                }

                if (effectsFound == false) {
                    int d = getDragDropEffects(draggableItem, selectItem);
                    if (d >= 0) {
                        dragDropEffects = new Integer(d);
                        effectsFound = true;
                    }
                }

                if (typesFound == false) {
                    dragDropTypes = getDragDropTypes(draggableItem, selectItem);
                    if (dragDropTypes != null) {
                        serializedDragDropTypes = serializeDragType(dragDropTypes);
                        typesFound = true;
                    }
                }
            }

            if (isDragDropItem(selectItem)) {
                dragDropSelectItemStates
                        .add(new DragDropItemState(selectItem, dragDropEffects,
                                serializedDragDropTypes, dragDropTypes));
            }

            int depth = selectItemLastDragDropInfos.size();

            if (typesFound && serializedDragDropTypes != null
                    && dragDropTypes != null) {
                if (depth > 0
                        && serializedDragDropTypes
                                .equals(selectItemLastDragDropInfos
                                        .get(depth - 2)) == false) {
                    IJavaScriptWriter locWriter = objectLiteralWriter
                            .writeSymbol(getDragDropTypesSymbol()).write('[');

                    for (int i = 0; i < dragDropTypes.length; i++) {
                        if (i > 0) {
                            locWriter.write(',');
                        }

                        locWriter.writeString(dragDropTypes[i]);
                    }

                    locWriter.write(']');
                }
            }

            if (effectsFound && dragDropEffects != null) {
                if (depth > 0
                        && dragDropEffects.equals(selectItemLastDragDropInfos
                                .get(depth - 1)) == false) {
                    objectLiteralWriter.writeSymbol(getDragDropEffectsSymbol())
                            .writeInt(dragDropEffects.intValue());
                }
            }

            selectItemLastDragDropInfos.add(serializedDragDropTypes);
            selectItemLastDragDropInfos.add(dragDropEffects);
        }

        protected final String serializeDragType(String[] dragDropTypes) {
            if (dragDropTypes == null) {
                return null;
            }
            if (dragDropTypes.length == 0) {
                return "";
            }
            if (dragDropTypes.length == 1) {
                return "[" + dragDropTypes[0] + "]";
            }

            if (cachedDragDropTypes != null
                    && Arrays.equals(cachedDragDropTypes, dragDropTypes)) {
                return cachedDerializedDragDropTypes;
            }

            StringAppender sb = new StringAppender(dragDropTypes[0],
                    dragDropTypes.length * 128);

            for (int i = 0; i < dragDropTypes.length; i++) {
                if (i > 0) {
                    sb.append(',');
                }
                sb.append(dragDropTypes[i]);
            }

            cachedDragDropTypes = dragDropTypes;
            cachedDerializedDragDropTypes = sb.toString();

            return cachedDerializedDragDropTypes;
        }

        protected DragDropItemState getCurrentDragDropItemState(int offset) {
            if (dragDropSelectItemStates == null
                    || dragDropSelectItemStates.size() <= offset) {
                return null;
            }

            return dragDropSelectItemStates.get(dragDropSelectItemStates.size()
                    - offset - 1);
        }

        protected abstract String[] getDragDropTypes(SelectItem mainItem,
                SelectItem childSelectItem);

        protected abstract int getDragDropEffects(SelectItem mainItem,
                SelectItem childSelectItem);

        protected abstract String getDragDropTypesSymbol();

        protected abstract String getDragDropEffectsSymbol();

        protected abstract boolean isDragDropItem(SelectItem selectItem);

    }

    protected class DragWalker extends AbstractDragDropWalker {
        public DragWalker(IDraggableCapability component) {
            super(component.getDragEffects(), component.getDragTypes());
        }

        protected String[] getDragDropTypes(SelectItem mainItem,
                SelectItem childSelectItem) {
            return ((IDraggableItem) mainItem).getDragTypes(childSelectItem);
        }

        protected int getDragDropEffects(SelectItem mainItem,
                SelectItem childSelectItem) {
            return ((IDraggableItem) mainItem).getDragEffects(childSelectItem);
        }

        protected String getDragDropTypesSymbol() {
            return "_dragTypes";
        }

        protected String getDragDropEffectsSymbol() {
            return "_dragEffects";
        }

        protected boolean isDragDropItem(SelectItem selectItem) {
            return selectItem instanceof IDraggableItem;
        }
    }

    protected class DropWalker extends AbstractDragDropWalker {
        public DropWalker(IDroppableCapability component) {
            super(component.getDropEffects(), component.getDropTypes());
        }

        protected String[] getDragDropTypes(SelectItem mainItem,
                SelectItem childSelectItem) {
            return ((IDroppableItem) mainItem).getDropTypes(childSelectItem);
        }

        protected int getDragDropEffects(SelectItem mainItem,
                SelectItem childSelectItem) {
            return ((IDroppableItem) mainItem).getDropEffects(childSelectItem);
        }

        protected String getDragDropTypesSymbol() {
            return "_dropTypes";
        }

        protected String getDragDropEffectsSymbol() {
            return "_dropEffects";
        }

        protected boolean isDragDropItem(SelectItem selectItem) {
            return selectItem instanceof IDroppableItem;
        }
    }

    public class SchrodingerNode {
        private SelectItem selectItem;

        private String varId;

        int checkedCount;

        int childCount;

        public SchrodingerNode(SelectItem selectItem) {
            this.selectItem = selectItem;
        }
    }

}
