/*
 * $Id: AbstractSelectItemsDecorator.java,v 1.3 2013/11/13 12:53:31 jbmeslin Exp $
 */
package org.rcfaces.renderkit.html.internal.decorator;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.component.UISelectItem;
import javax.faces.component.UISelectItems;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import javax.faces.model.SelectItemGroup;
import javax.faces.render.Renderer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.component.capability.IImageCapability;
import org.rcfaces.core.internal.RcfacesContext;
import org.rcfaces.core.internal.contentAccessor.ContentAccessorFactory;
import org.rcfaces.core.internal.contentAccessor.IContentAccessor;
import org.rcfaces.core.internal.decorator.ISelectItemMapper;
import org.rcfaces.core.internal.renderkit.IComponentData;
import org.rcfaces.core.internal.renderkit.IComponentRenderContext;
import org.rcfaces.core.internal.renderkit.IEventData;
import org.rcfaces.core.internal.renderkit.IRequestContext;
import org.rcfaces.core.internal.renderkit.WriterException;
import org.rcfaces.core.internal.tools.ValuesTools;
import org.rcfaces.core.item.BasicImagesSelectItem;
import org.rcfaces.core.item.BasicSelectItem;
import org.rcfaces.core.item.IClientDataItem;
import org.rcfaces.core.item.IImagesItem;
import org.rcfaces.core.item.ISelectItem;
import org.rcfaces.core.lang.IAdaptable;
import org.rcfaces.core.lang.IContentFamily;
import org.rcfaces.core.model.IFilterProperties;
import org.rcfaces.core.model.IFiltredCollection;
import org.rcfaces.core.model.IFiltredCollection.IFiltredIterator;
import org.rcfaces.core.model.IFiltredCollection2;
import org.rcfaces.core.util.SelectItemTools;
import org.rcfaces.renderkit.html.internal.EventDecoders;
import org.rcfaces.renderkit.html.internal.EventDecoders.IEventDecoder;
import org.rcfaces.renderkit.html.internal.EventDecoders.IEventObjectDecoder;
import org.rcfaces.renderkit.html.internal.HtmlTools;
import org.rcfaces.renderkit.html.internal.IHtmlRequestContext;
import org.rcfaces.renderkit.html.internal.IHtmlWriter;
import org.rcfaces.renderkit.html.internal.IJavaScriptWriter;
import org.rcfaces.renderkit.html.internal.IObjectLiteralWriter;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:31 $
 */
public abstract class AbstractSelectItemsDecorator extends
        AbstractComponentDecorator implements ISelectItemNodeWriter,
        IEventObjectDecoder {

    private static final Log LOG = LogFactory
            .getLog(AbstractSelectItemsDecorator.class);

    // private static final String CONTEXT_ATTRIBUTE =
    // "camelia.selectItems.context";

    protected static final SelectItem[] EMPTY_SELECT_ITEM_ARRAY = new SelectItem[0];

    protected final UIComponent component;

    private String componentClientId;

    protected final IFilterProperties filterProperties;

    protected int selectItemCount;

    protected Renderer renderer;

    protected IHtmlWriter htmlWriter;

    private String className;

    protected IJavaScriptWriter javaScriptWriter;

    protected SelectItemsContext selectItemsContext;

    private IComponentData decoratorComponentData;

    protected AbstractSelectItemsDecorator(UIComponent component,
            IFilterProperties filterProperties) {
        this.component = component;
        this.filterProperties = filterProperties;
    }

    public final void encodeContainer(IHtmlWriter writer, Renderer renderer)
            throws WriterException {
        this.htmlWriter = writer;
        this.renderer = renderer;
        try {
            preEncodeContainer();

            SelectItemsContext context = createHtmlContext();
            if (context == null) {
                postEncodeContainer();
                return;
            }

            this.selectItemsContext = context;

            Iterator<UIComponent> it = iterateNodes(component);
            encodeComponents(it, 0, true);

            postEncodeContainer();

        } finally {
            this.htmlWriter = null;
            this.selectItemsContext = null;
            this.renderer = null;
        }

        super.encodeContainer(writer, renderer);
    }

    public void decode(IRequestContext requestContext, UIComponent component,
            IComponentData componentData) {
        super.decode(requestContext, component, componentData);

        if (component != getComponent()) {
            // Un sous-composant ... il peut avoir déclanché un evenement !

            String componentClientId = getComponentClientId(requestContext
                    .getFacesContext());
            if (componentClientId != null) {
                IHtmlRequestContext htmlRequestContext = (IHtmlRequestContext) requestContext;
                if (componentClientId.equals(htmlRequestContext
                        .getEventComponentId())) {

                    decodeDecoratorEvent(requestContext, component,
                            getDecoratorComponentData(requestContext));
                }
            }
        }
    }

    protected void decodeDecoratorEvent(IRequestContext requestContext,
            UIComponent masterComponent, IEventData eventData) {

        IEventDecoder eventDecoder = getEventDecoder(requestContext,
                getComponent(), eventData);

        if (eventDecoder == null) {
            LOG.error("Unknown decoder for event name '"
                    + eventData.getEventName() + "'.");
            return;
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("Decode event type='" + eventData.getEventName()
                    + "' for component='" + component.getId() + "'.");
        }

        eventDecoder.decodeEvent(requestContext, component, eventData, this);
    }

    public Object decodeEventObject(IRequestContext requestContext,
            UIComponent component, IEventData eventData) {
        return null;
    }

    protected IEventDecoder getEventDecoder(IRequestContext requestContext,
            UIComponent decoratorComponent, IEventData eventData) {

        return EventDecoders.get(eventData.getEventName());
    }

    protected IComponentData getDecoratorComponentData(
            IRequestContext requestContext) {
        if (decoratorComponentData != null) {
            return decoratorComponentData;
        }

        decoratorComponentData = requestContext.getComponentData(
                getComponent(),
                getComponentClientId(requestContext.getFacesContext()), null);

        return decoratorComponentData;
    }

    protected String getComponentClientId(FacesContext facesContext) {
        if (componentClientId != null) {
            return componentClientId;
        }

        UIComponent component = getComponent();
        if (component == null) {
            return null;
        }

        if (facesContext == null) {
            facesContext = FacesContext.getCurrentInstance();
        }

        componentClientId = component.getClientId(facesContext);

        return componentClientId;
    }

    public final void encodeContainerEnd(IHtmlWriter writer, Renderer renderer)
            throws WriterException {
        super.encodeContainerEnd(writer, renderer);

        this.htmlWriter = writer;
        this.renderer = renderer;
        try {
            preEncodeContainerEnd();

            postEncodeContainerEnd();

        } finally {
            this.htmlWriter = null;
            this.renderer = null;
        }
    }

    /*
     * protected final String getClassName() { if ((renderer instanceof
     * ICssRenderer) == false) { throw new FacesException("Can not compute
     * className !"); }
     * 
     * if (className != null) { return className; }
     * 
     * className = ((ICssRenderer) renderer).getStyleClassName(writer
     * .getComponentRenderContext());
     * 
     * return className; }
     */
    protected void preEncodeContainerEnd() throws WriterException {
    }

    protected void postEncodeContainerEnd() throws WriterException {
    }

    protected void postEncodeContainer() throws WriterException {
    }

    protected void preEncodeContainer() throws WriterException {
    }

    protected boolean mapSelectItems(
            IComponentRenderContext componentRenderContext,
            ISelectItemMapper mapper) {
        Iterator it = getComponent().getChildren().iterator();

        return mapSelectItems(mapper, it);
    }

    private boolean mapSelectItems(ISelectItemMapper mapper, Iterator it) {
        for (; it.hasNext();) {
            UIComponent component = (UIComponent) it.next();

            if (component instanceof UISelectItems) {
                if (mapSelectItems(mapper, (UISelectItems) component) == false) {
                    return false;
                }
                continue;
            }

            if (component instanceof UISelectItem) {
                UISelectItem uiSelectItem = (UISelectItem) component;

                Object value = uiSelectItem.getValue();

                SelectItem si = convertToSelectItem(value);
                if (si == null) {
                    si = createSelectItem(uiSelectItem);
                }

                if (si == null) {
                    throw new FacesException(
                            "Can not create SelectItem from value (" + value
                                    + ")");
                }

                if (mapSelectItem(mapper, si) == false) {
                    return false;
                }
                continue;
            }

            SelectItem si = getUnknownComponent(component);
            if (si == null) {
                mapper.unknownComponent(component);
                continue;
            }

            if (mapSelectItem(mapper, si) == false) {
                return false;
            }
        }

        return true;
    }

    private boolean mapSelectItems(ISelectItemMapper mapper,
            UISelectItems uiSelectItems) {
        Object value = uiSelectItems.getValue();

        if (value instanceof SelectItem) {
            SelectItem si = (SelectItem) value;

            return mapSelectItem(mapper, si);
        }

        if (value instanceof ISelectItem) {
            ISelectItem si = (ISelectItem) value;

            value = convertToSelectItem(si);
        }

        if (mapper.acceptCollections()) {
            return true;
        }

        if (value != null && value.getClass().isArray()) {
            int length = Array.getLength(value);

            for (int i = 0; i < length; i++) {
                Object item = Array.get(value, i);

                SelectItem si = convertToSelectItem(item);
                if (si == null) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Can not convert '" + item
                                + "' to SelectItem !");
                    }

                    continue;
                }

                if (mapSelectItem(mapper, si) == false) {
                    return false;
                }
            }

            return true;
        }

        if (value instanceof Collection) {
            Collection c = (Collection) value;
            if (c.isEmpty()) {
                return true;
            }

            for (Iterator it2 = c.iterator(); it2.hasNext();) {
                Object item = it2.next();

                SelectItem si = convertToSelectItem(item);
                if (si == null) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Can not convert '" + item
                                + "' to SelectItem !");
                    }
                    continue;
                }

                if (mapSelectItem(mapper, si) == false) {
                    return false;
                }
            }

            return true;
        }

        if (value instanceof Map) {
            Map map = (Map) value;

            if (map.isEmpty()) {
                return true;
            }

            for (Iterator it2 = map.keySet().iterator(); it2.hasNext();) {
                String itemLabel = (String) it2.next();
                // Pas de entrieSet pour garder l'ordre !

                Object itemValue = map.get(itemLabel);

                SelectItem selectItem = new SelectItem(itemValue, itemLabel);

                if (mapSelectItem(mapper, selectItem) == false) {
                    return false;
                }
            }

            return true;
        }

        SelectItem selectItems[] = adapt(value, SelectItem[].class);
        if (selectItems == null) {
            ISelectItem selectItems2[] = adapt(value, ISelectItem[].class);

            if (selectItems2 != null) {
                selectItems = new SelectItem[selectItems2.length];
                for (int i = 0; i < selectItems2.length; i++) {
                    selectItems[i] = convertToSelectItem(selectItems2[i]);
                }
            }
        }
        if (selectItems != null) {
            for (int i = 0; i < selectItems.length; i++) {
                SelectItem si = selectItems[i];
                if (si == null) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("SelectItem #" + i + " is null");
                    }

                    continue;
                }

                if (mapSelectItem(mapper, si) == false) {
                    return false;
                }
            }

            return true;
        }

        SelectItem selectItem = adapt(value, SelectItem.class);
        if (selectItem == null) {
            ISelectItem selectItem2 = (ISelectItem) adapt(value,
                    SelectItem.class);
            if (selectItem2 != null) {
                selectItem = convertToSelectItem(selectItem2);
            }
        }
        if (selectItem != null) {
            return mapSelectItem(mapper, selectItem);
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("Can not convert '" + value + "' to SelectItem !");
        }

        return true;
    }

    private <T> T adapt(Object adaptable, Class<T> adapterClass) {

        if (adaptable instanceof IAdaptable) {
            T adapted = ((IAdaptable) adaptable).getAdapter(adapterClass, null);
            if (adapted != null) {
                return adapted;
            }
        }

        RcfacesContext rcfacesContext = getComponentRenderContext()
                .getRenderContext().getProcessContext().getRcfacesContext();

        return rcfacesContext.getAdapterManager().getAdapter(adaptable,
                adapterClass, null);
    }

    protected SelectItem convertToSelectItem(Object value) {
        return SelectItemTools.convertToSelectItem(getComponentRenderContext()
                .getFacesContext(), value);
    }

    private boolean mapSelectItem(ISelectItemMapper mapper, SelectItem si) {
        if (mapper.map(si) == false) {
            return false;
        }

        if (si instanceof SelectItemGroup) {
            SelectItemGroup sig = (SelectItemGroup) si;

            SelectItem sis[] = sig.getSelectItems();
            for (int i = 0; i < sis.length; i++) {
                if (mapSelectItem(mapper, sis[i]) == false) {
                    return false;
                }
            }
        }

        return true;
    }

    public void encodeJavaScript(IJavaScriptWriter jsWriter)
            throws WriterException {

        this.javaScriptWriter = jsWriter;
        try {
            SelectItemsContext context = createJavaScriptContext();
            if (context == null) {
                return;
            }

            selectItemsContext = context;

            encodeComponentsBegin();

            encodeNodes(getComponent());

            encodeComponentsEnd();

        } finally {
            this.javaScriptWriter = null;
            this.selectItemsContext = null;
        }

        super.encodeJavaScript(jsWriter);
    }

    protected void encodeComponentsEnd() throws WriterException {
    }

    protected void encodeComponentsBegin() throws WriterException {
    }

    public final SelectItemsContext getContext() {
        return selectItemsContext;
    }

    public final UIComponent getComponent() {
        return component;
    }

    protected IComponentRenderContext getComponentRenderContext() {
        if (htmlWriter != null) {
            return htmlWriter.getComponentRenderContext();
        }

        return javaScriptWriter.getHtmlComponentRenderContext();
    }

    protected void encodeNodes(UIComponent component) throws WriterException {
        Iterator<UIComponent> it = iterateNodes(component);

        encodeComponents(it, 0, true);
    }

    protected Iterator<UIComponent> iterateNodes(UIComponent component)
            throws WriterException {
        Iterator<UIComponent> it = component.getChildren().iterator();

        return it;
    }

    protected void encodeComponents(Iterator<UIComponent> it, int depth,
            boolean visible) throws WriterException {

        for (; it.hasNext();) {
            UIComponent component = it.next();

            if (component instanceof UISelectItems) {
                UISelectItems uiSelectItems = (UISelectItems) component;

                encodeUISelectItems(component, uiSelectItems.getValue(), depth,
                        visible);

                continue;
            }

            if (component instanceof UISelectItem) {
                UISelectItem uiSelectItem = (UISelectItem) component;

                Object value = uiSelectItem.getValue();
                if (value == null) {
                    value = createSelectItem(uiSelectItem);
                }

                // On sait jamais, la value peut etre un group !
                encodeUISelectItems(component, value, depth, visible);

                continue;
            }

            SelectItem closeSelectItem = getUnknownComponent(component);
            if (closeSelectItem != null) {

                selectItemCount++;

                encodeSelectItem(component, closeSelectItem, depth, visible);
                continue;
            }

        }
    }

    protected SelectItem getUnknownComponent(UIComponent component) {
        return null;
    }

    protected SelectItem createSelectItem(UISelectItem component) {
        if (component instanceof IImageCapability) {
            return new BasicImagesSelectItem(component);
        }

        return new BasicSelectItem(component);
    }

    private void encodeUISelectItems(UIComponent component, Object value,
            int depth, boolean visible) throws WriterException {

        if (value == null) {
            if (getComponentRenderContext().getRenderContext()
                    .getProcessContext().isDesignerMode()) {

                // En mode Designer ... on reste discret :-)
                return;
            }
            throw new WriterException("UISelectItems value is null !", null,
                    component);
        }

        if (value instanceof SelectItem) {
            SelectItem si = (SelectItem) value;

            encodeSelectItem(component, si, depth, visible);
            return;
        }

        if (value instanceof SelectItem[]) {
            SelectItem sis[] = (SelectItem[]) value;

            for (int i = 0; i < sis.length; i++) {
                SelectItem si = sis[i];

                encodeSelectItem(component, si, depth, visible);
            }

            return;
        }

        if ((value instanceof IFiltredCollection)
                || (value instanceof IFiltredCollection2)) {
            int max = getMaxResultNumber();
            if (max > 0) {
                max -= selectItemCount;
                if (max < 1) {
                    return;
                }
            }

            Iterator< ? > it;
            if (value instanceof IFiltredCollection2) {
                it = ((IFiltredCollection2< ? >) value).iterator(
                        getComponent(), filterProperties, max);
            } else {
                it = ((IFiltredCollection< ? >) value).iterator(
                        filterProperties, max);
            }

            if (it != null) {
                try {
                    int sic = 0;

                    for (; it.hasNext();) {
                        Object item = it.next();

                        SelectItem si = convertToSelectItem(item);
                        if (si == null) {
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("Can not convert '" + item
                                        + "' to SelectItem !");
                            }
                            continue;
                        }

                        encodeSelectItem(component, si, depth, visible);

                        sic++;
                    }

                    if (it instanceof IFiltredIterator) {
                        int s = ((IFiltredIterator) it).getSize();
                        if (s > sic) {
                            selectItemCount += s - sic;
                        }
                    }

                } finally {
                    if (it instanceof IFiltredIterator) {
                        ((IFiltredIterator) it).release();
                    }
                }
            }

            return;
        }

        if (value.getClass().isArray()) {
            int length = Array.getLength(value);

            for (int i = 0; i < length; i++) {
                Object item = Array.get(value, i);

                SelectItem si = convertToSelectItem(item);
                if (si == null) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Can not convert '" + item
                                + "' to SelectItem !");
                    }

                    continue;
                }

                encodeSelectItem(component, si, depth, visible);
            }

            return;
        }

        if (value instanceof Collection) {
            Collection l = (Collection) value;
            if (l.isEmpty()) {
                return;
            }

            for (Iterator it = l.iterator(); it.hasNext();) {
                Object item = it.next();

                SelectItem si = convertToSelectItem(item);
                if (si == null) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Can not convert '" + item
                                + "' to SelectItem !");
                    }
                    continue;
                }

                encodeSelectItem(component, si, depth, visible);
            }

            return;
        }

        if (value instanceof Map) {
            Map map = (Map) value;

            if (map.isEmpty()) {
                return;
            }

            for (Iterator it = map.keySet().iterator(); it.hasNext();) {
                // Pas de entrySet() car le resultat pourrait ne pas �tre le
                // meme !
                String itemLabel = (String) it.next();

                Object itemValue = map.get(itemLabel);

                SelectItem selectItem = new SelectItem(itemValue, itemLabel);

                encodeSelectItem(component, selectItem, depth, visible);
            }

            return;
        }

        SelectItem convertedSelectItem = convertToSelectItem(value);
        if (convertedSelectItem != null) {
            encodeUISelectItems(component, convertedSelectItem, depth, visible);
            return;
        }

        if (getComponentRenderContext().getRenderContext().getProcessContext()
                .isDesignerMode()
                && (value instanceof String[])) {
            String vs[] = (String[]) value;
            if (vs != null) {
                SelectItem sis[] = new SelectItem[vs.length];
                for (int i = 0; i < sis.length; i++) {
                    SelectItem selectItem = new SelectItem("idx" + i, vs[i]);

                    encodeSelectItem(component, selectItem, depth, visible);
                }
            }

            return;
        }

        throw new WriterException("Illegal uiSelectItems value type ! (class='"
                + value.getClass() + "' value='" + value + "')", null,
                component);

    }

    protected int getMaxResultNumber() {
        return IFiltredCollection.NO_MAXIMUM_RESULT_NUMBER;
    }

    private boolean encodeSelectItem(UIComponent component,
            SelectItem selectItem, int depth, boolean visible)
            throws WriterException {

        if (selectItem == null) {
            throw new NullPointerException(
                    "The selectItem is null ! (component.id="
                            + component.getId() + " depth=" + depth
                            + " visible=" + visible + ")");
        }

        selectItem = transformSelectItem(selectItem, depth, visible);

        if (selectItem == null) {
            throw new NullPointerException(
                    "Transformed selectItem is null ! (component.id="
                            + component.getId() + " depth=" + depth
                            + " visible=" + visible + ")");
        }

        selectItemCount++;

        /*
         * System.out.println("Item '" + selectItem.getValue() + "' depth=" +
         * depth + " visible=" + visible);
         */
        if (selectItemsContext.pushSelectItem(component, selectItem, visible) == false) {
            return false;
        }

        boolean v = visible;
        if (v) {
            if (depth > 0
                    && selectItemsContext.isValueExpanded(selectItem,
                            selectItem.getValue()) == false) {
                v = false;
            }
        }

        // On verifie si le selectItem n'est pas lui meme container d'autres
        // SelectItem !
        if (selectItem instanceof SelectItemGroup) {
            SelectItemGroup sig = (SelectItemGroup) selectItem;

            SelectItem selectItems[] = sig.getSelectItems();

            if (selectItems != null && selectItems.length > 0) {
                for (int i = 0; i < selectItems.length; i++) {
                    SelectItem s2 = selectItems[i];

                    if (s2 == null) {
                        throw new NullPointerException("The selectItem #" + i
                                + " is null !");
                    }

                    if (encodeSelectItem(component, s2, depth + 1, v) == false) {
                        break;
                    }
                }
            }
        }

        // On regarde maintenant les enfants du composant qui contient le
        // SelectItem
        if (component.getChildCount() > 0) {
            encodeComponents(iterateNodes(component), depth + 1, v);
        }

        selectItemsContext.popSelectItem();

        return true;
    }

    /**
     * Allows the modification of the selectItem before treatment
     * 
     * @param selectItem
     * @param depth
     * @param visible
     * @return a selectItem
     */
    protected SelectItem transformSelectItem(SelectItem selectItem, int depth,
            boolean visible) {
        return selectItem;
    }

    protected abstract SelectItemsContext createHtmlContext();

    protected abstract SelectItemsContext createJavaScriptContext()
            throws WriterException;

    protected String convertItemValue(IComponentRenderContext componentContext,
            Object selectItemValue) {

        return ValuesTools.valueToString(selectItemValue, getConverter(),
                componentContext.getComponent(),
                componentContext.getFacesContext());
    }

    protected Object convertToItemValue(
            IComponentRenderContext componentContext, String selectItemValue) {

        return convertToItemValue(componentContext.getFacesContext(),
                componentContext.getComponent(), selectItemValue);
    }

    protected Object convertToItemValue(FacesContext facesContext,
            UIComponent component, String selectItemValue) {

        return ValuesTools.convertStringToValue(facesContext, component,
                getConverter(), selectItemValue, null, false);
    }

    protected final List listComponents(Map childrenClientIds, String itemIds,
            Class capability) {

        List<UIComponent> l = null;

        StringTokenizer st = new StringTokenizer(itemIds,
                HtmlTools.LIST_SEPARATORS);
        for (; st.hasMoreTokens();) {
            String key = st.nextToken();

            UIComponent item = (UIComponent) childrenClientIds.get(key);
            if (item == null) {
                continue;
            }

            if (capability != null && capability.isInstance(item) == false) {
                continue;
            }

            if (l == null) {
                l = new ArrayList<UIComponent>();
            }

            l.add(item);
        }

        if (l == null) {
            return Collections.emptyList();
        }

        return l;

    }

    protected final List<SelectItem> listSelectItems(Map childrenValues,
            String itemIds, Class capability) {

        List<SelectItem> l = null;

        StringTokenizer st = new StringTokenizer(itemIds,
                HtmlTools.LIST_SEPARATORS);
        for (; st.hasMoreTokens();) {
            String key = st.nextToken();

            SelectItem item = (SelectItem) childrenValues.get(key);
            if (item == null) {
                continue;
            }

            if (capability != null && capability.isInstance(item) == false) {
                continue;
            }

            if (l == null) {
                l = new ArrayList<SelectItem>();
            }

            l.add(item);
        }

        if (l == null) {
            return Collections.emptyList();
        }

        return l;

    }

    protected final Map<String, UIComponent> mapChildrenClientId(
            Map<String, UIComponent> map, IRequestContext renderContext,
            UIComponent container) {

        List children = container.getChildren();
        for (Iterator it = children.iterator(); it.hasNext();) {
            UIComponent child = (UIComponent) it.next();

            String childClientId = renderContext.getComponentId(child);
            if (childClientId == null) {
                continue;
            }

            if (map == null) {
                map = new HashMap<String, UIComponent>();
            }
            map.put(childClientId, child);

            if (child.getChildCount() < 1) {
                continue;
            }

            map = mapChildrenClientId(map, renderContext, child);
        }

        return map;
    }

    protected final Map<Object, UIComponent> mapChildrenItemValues(
            Map<Object, UIComponent> map, FacesContext context,
            UIComponent container) {

        List children = container.getChildren();
        for (Iterator it = children.iterator(); it.hasNext();) {
            UIComponent child = (UIComponent) it.next();
            if ((child instanceof UISelectItem) == false) {
                continue;
            }

            Object value = ((UISelectItem) child).getItemValue();
            if (value == null) {
                continue;
            }

            if (map == null) {
                map = new HashMap<Object, UIComponent>();
            }
            map.put(value, child);

            if (child.getChildCount() < 1) {
                continue;
            }

            map = mapChildrenItemValues(map, context, child);
        }

        return map;
    }

    public void encodeNodeInit(UIComponent component, SelectItem selectItem) {
    }

    public int getSelectItemCount() {
        return selectItemCount;
    }

    protected Converter getConverter() {
        return null;
    }

    protected static void writeItemClientDatas(IClientDataItem iim,
            IJavaScriptWriter javaScriptWriter, String managerVarId,
            String varId, IObjectLiteralWriter objectLiteralWriter)
            throws WriterException {

        if (iim.isClientDataEmpty()) {
            return;
        }

        if (objectLiteralWriter != null) {
            objectLiteralWriter.writeSymbol("_clientDatas");

        } else {
            if (managerVarId != null) {
                javaScriptWriter
                        .writeCall(managerVarId, "f_setItemClientDatas");

            } else {
                javaScriptWriter.writeMethodCall("f_setItemClientDatas");
            }

            javaScriptWriter.write(varId).write(',');
        }

        Map map = iim.getClientDataMap();

        HtmlTools.writeObjectLiteralMap(javaScriptWriter, map, true);

        if (objectLiteralWriter == null) {
            javaScriptWriter.writeln(");");
        }
    }

    protected static void writeSelectItemImages(IImagesItem iim,
            IJavaScriptWriter javaScriptWriter, String managerVarId,
            String varId, boolean ignoreExpand,
            IObjectLiteralWriter objectLiteralWriter) throws WriterException {

        FacesContext facesContext = javaScriptWriter.getFacesContext();

        String imageURL = iim.getImageURL();
        String disabledImageURL = iim.getDisabledImageURL();
        String hoverImageURL = iim.getHoverImageURL();
        String selectedImageURL = iim.getSelectedImageURL();
        String expandedImageURL = null;
        if (ignoreExpand == false) {
            expandedImageURL = iim.getExpandedImageURL();
        }

        IContentAccessor imageAccessor = null;
        if (imageURL != null) {
            imageAccessor = ContentAccessorFactory.createFromWebResource(
                    facesContext, imageURL, IContentFamily.IMAGE);
        }

        IContentAccessor disabledImageAccessor = null;
        if (disabledImageURL != null) {
            if (imageAccessor == null) {
                disabledImageAccessor = ContentAccessorFactory
                        .createFromWebResource(facesContext, disabledImageURL,
                                IContentFamily.IMAGE);
            } else {
                disabledImageAccessor = ContentAccessorFactory
                        .createFromWebResource(facesContext, disabledImageURL,
                                imageAccessor);
            }
        }

        IContentAccessor hoverImageAccessor = null;
        if (hoverImageURL != null) {
            if (imageAccessor == null) {
                hoverImageAccessor = ContentAccessorFactory
                        .createFromWebResource(facesContext, hoverImageURL,
                                IContentFamily.IMAGE);
            } else {
                hoverImageAccessor = ContentAccessorFactory
                        .createFromWebResource(facesContext, hoverImageURL,
                                imageAccessor);
            }
        }

        IContentAccessor selectedImageAccessor = null;
        if (selectedImageURL != null) {
            if (imageAccessor == null) {
                selectedImageAccessor = ContentAccessorFactory
                        .createFromWebResource(facesContext, selectedImageURL,
                                IContentFamily.IMAGE);
            } else {
                selectedImageAccessor = ContentAccessorFactory
                        .createFromWebResource(facesContext, selectedImageURL,
                                imageAccessor);
            }
        }

        IContentAccessor expandedImageAccessor = null;
        if (expandedImageURL != null) {
            if (imageAccessor == null) {
                expandedImageAccessor = ContentAccessorFactory
                        .createFromWebResource(facesContext, expandedImageURL,
                                IContentFamily.IMAGE);
            } else {
                expandedImageAccessor = ContentAccessorFactory
                        .createFromWebResource(facesContext, expandedImageURL,
                                imageAccessor);
            }
        }

        if (imageAccessor == null && disabledImageAccessor == null
                && hoverImageAccessor == null && selectedImageAccessor == null
                && expandedImageURL == null) {
            return;
        }

        String imageVar = null;
        if (imageAccessor != null) {
            imageURL = imageAccessor.resolveURL(facesContext, null, null);
            if (imageURL != null) {
                if (objectLiteralWriter != null) {
                    imageVar = imageURL;
                } else {
                    imageVar = javaScriptWriter.allocateString(imageURL);
                }
            }
        }

        String disabledVar = null;
        if (disabledImageAccessor != null) {
            disabledImageURL = disabledImageAccessor.resolveURL(facesContext,
                    null, null);
            if (disabledImageURL != null) {
                if (objectLiteralWriter != null) {
                    disabledVar = disabledImageURL;
                } else {
                    disabledVar = javaScriptWriter
                            .allocateString(disabledImageURL);
                }
            }
        }

        String hoverVar = null;
        if (hoverImageAccessor != null) {
            hoverImageURL = hoverImageAccessor.resolveURL(facesContext, null,
                    null);
            if (hoverImageURL != null) {
                if (objectLiteralWriter != null) {
                    hoverVar = hoverImageURL;
                } else {
                    hoverVar = javaScriptWriter.allocateString(hoverImageURL);
                }
            }
        }

        String selectedVar = null;
        if (selectedImageAccessor != null) {
            selectedImageURL = selectedImageAccessor.resolveURL(facesContext,
                    null, null);
            if (selectedImageURL != null) {
                if (objectLiteralWriter != null) {
                    selectedVar = selectedImageURL;
                } else {
                    selectedVar = javaScriptWriter
                            .allocateString(selectedImageURL);
                }
            }
        }

        String expandVar = null;
        if (expandedImageAccessor != null) {
            expandedImageURL = expandedImageAccessor.resolveURL(facesContext,
                    null, null);
            if (expandedImageURL != null) {
                if (objectLiteralWriter != null) {
                    expandVar = expandedImageURL;
                } else {
                    expandVar = javaScriptWriter
                            .allocateString(expandedImageURL);
                }
            }
        }

        if (objectLiteralWriter != null) {
            if (imageVar != null) {
                objectLiteralWriter.writeSymbol("_imageURL").writeString(
                        imageVar);
            }
            if (expandVar != null) {
                objectLiteralWriter.writeSymbol("_expandedImageURL")
                        .writeString(expandVar);
            }
            if (disabledVar != null) {
                objectLiteralWriter.writeSymbol("_disabledImageURL")
                        .writeString(disabledVar);
            }
            if (hoverVar != null) {
                objectLiteralWriter.writeSymbol("_hoverImageURL").writeString(
                        hoverVar);
            }
            if (selectedVar != null) {
                objectLiteralWriter.writeSymbol("_selectedImageURL")
                        .writeString(selectedVar);
            }

            return;
        }

        if (managerVarId != null) {
            javaScriptWriter.writeCall(managerVarId, "f_setItemImages");

        } else {
            javaScriptWriter.writeMethodCall("f_setItemImages");
        }

        javaScriptWriter.write(varId);

        int pred = 0;

        if (imageVar != null) {
            for (; pred > 0; pred--) {
                javaScriptWriter.write(',').writeNull();
            }
            javaScriptWriter.write(',').write(imageVar);
        } else {
            pred++;
        }

        if (ignoreExpand == false) {
            if (expandVar != null) {
                for (; pred > 0; pred--) {
                    javaScriptWriter.write(',').writeNull();
                }
                javaScriptWriter.write(',').write(expandVar);
            } else {
                pred++;
            }
        }

        if (disabledVar != null) {
            for (; pred > 0; pred--) {
                javaScriptWriter.write(',').writeNull();
            }
            javaScriptWriter.write(',').write(disabledVar);
        } else {
            pred++;
        }

        if (hoverVar != null) {
            for (; pred > 0; pred--) {
                javaScriptWriter.write(',').writeNull();
            }
            javaScriptWriter.write(',').write(hoverVar);
        } else {
            pred++;
        }

        if (selectedVar != null) {
            for (; pred > 0; pred--) {
                javaScriptWriter.write(',').writeNull();
            }
            javaScriptWriter.write(',').write(selectedVar);
        } else {
            pred++;
        }

        javaScriptWriter.writeln(");");
    }

    public void refreshNode(UIComponent component) throws WriterException {
    }

}
