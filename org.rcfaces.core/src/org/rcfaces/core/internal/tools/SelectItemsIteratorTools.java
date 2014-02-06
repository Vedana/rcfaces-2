/*
 * $Id: SelectItemsIteratorTools.java,v 1.3 2013/11/13 12:53:22 jbmeslin Exp $
 */
package org.rcfaces.core.internal.tools;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.FacesException;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.component.SelectItemsIteratorComponent;
import org.rcfaces.core.internal.component.IDataMapAccessor;
import org.rcfaces.core.internal.converter.InputTypeConverter;
import org.rcfaces.core.item.BasicImagesSelectItem;
import org.rcfaces.core.lang.IAdaptable;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:22 $
 */
public class SelectItemsIteratorTools {

    private static final Log LOG = LogFactory
            .getLog(SelectItemsIteratorTools.class);

    public static SelectItem[] getValue(
            SelectItemsIteratorComponent selectItemsIteratorComponent,
            FacesContext facesContext) {

        if (facesContext == null) {
            facesContext = FacesContext.getCurrentInstance();
        }

        Object items = selectItemsIteratorComponent.getItems(facesContext);
        if (items == null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("getItems() returns null !");
            }
            return null;
        }

        int size = -1;
        if (items instanceof Collection) {
            size = ((Collection< ? >) items).size();

        } else if (items.getClass().isArray()) {
            size = Array.getLength(items);
        }

        Iterator< ? > it = convertToIterator(items);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Convert to iterator (items=" + items + ",  size=" + size
                    + ") returns " + it);
        }
        if (it == null) {
            return null;
        }

        String varName = selectItemsIteratorComponent.getVar(facesContext);
        if (varName == null || varName.length() < 1) {
            // ERROR
            throw new FacesException("VarName is null or empty, componentId='"
                    + selectItemsIteratorComponent.getId() + "' varName='"
                    + varName + "'");
        }

        Map<String, Object> requestMap = facesContext.getExternalContext()
                .getRequestMap();

        List<SelectItem> selectItems;
        if (size > 0) {
            selectItems = new ArrayList<SelectItem>(size);
        } else {
            selectItems = new ArrayList<SelectItem>();
        }

        Object oldIndexVar = null;

        String indexVar = selectItemsIteratorComponent
                .getItemIndexVar(facesContext);

        if (indexVar != null) {
            oldIndexVar = requestMap.get(indexVar);
        }

        Object oldValue = requestMap.get(varName);
        try {
            int idx = 0;
            for (; it.hasNext(); idx++) {
                Object item = it.next();

                if (LOG.isDebugEnabled()) {
                    LOG.debug("Item #" + idx + " = " + item);
                }

                if (item == null) {
                    continue;
                }

                requestMap.put(varName, item);

                if (indexVar != null) {
                    requestMap.put(indexVar, String.valueOf(idx));
                }

                BasicImagesSelectItem selectItem = new BasicImagesSelectItem();
                selectItems.add(selectItem);

                String label = selectItemsIteratorComponent
                        .getItemLabel(facesContext);
                if (label != null) {
                    selectItem.setLabel(label);
                }

                Object value = selectItemsIteratorComponent
                        .getItemValue(facesContext);
                if (value != null) {
                    selectItem.setValue(value);

                } else {
                    throw new FacesException(
                            "Value must be defined, componentId='"
                                    + selectItemsIteratorComponent.getId()
                                    + "' index=#" + idx);
                }

                if (selectItemsIteratorComponent.isItemDisabled(facesContext)) {
                    selectItem.setDisabled(true);
                }

                String description = selectItemsIteratorComponent
                        .getItemDescription(facesContext);
                if (description != null) {
                    selectItem.setDescription(description);
                }

                String acceleratorKey = selectItemsIteratorComponent
                        .getItemAcceleratorKey(facesContext);
                if (acceleratorKey != null) {
                    selectItem.setAcceleratorKey(acceleratorKey);
                }

                String accessKey = selectItemsIteratorComponent
                        .getItemAccessKey(facesContext);
                if (accessKey != null) {
                    selectItem.setAccessKey(accessKey);
                }

                String styleClass = selectItemsIteratorComponent
                        .getItemStyleClass(facesContext);
                if (styleClass != null) {
                    selectItem.setStyleClass(styleClass);
                }

                String type = selectItemsIteratorComponent
                        .getItemInputType(facesContext);
                if (type != null) {
                    Integer typeInt = (Integer) InputTypeConverter.SINGLETON
                            .getAsObject(facesContext,
                                    selectItemsIteratorComponent, type);
                    if (typeInt != null) {
                        selectItem.setInputType(typeInt.intValue());
                    }
                }

                String imageURL = selectItemsIteratorComponent
                        .getItemImageURL(facesContext);
                if (imageURL != null) {
                    selectItem.setImageURL(imageURL);
                }

                String disabledImageURL = selectItemsIteratorComponent
                        .getItemDisabledImageURL(facesContext);
                if (disabledImageURL != null) {
                    selectItem.setDisabledImageURL(disabledImageURL);
                }

                String hoverImageURL = selectItemsIteratorComponent
                        .getItemHoverImageURL(facesContext);
                if (hoverImageURL != null) {
                    selectItem.setHoverImageURL(hoverImageURL);
                }

                String selectedImageURL = selectItemsIteratorComponent
                        .getItemSelectedImageURL(facesContext);
                if (selectedImageURL != null) {
                    selectItem.setSelectedImageURL(selectedImageURL);
                }

                String expandedImageURL = selectItemsIteratorComponent
                        .getItemExpandedImageURL(facesContext);
                if (expandedImageURL != null) {
                    selectItem.setExpandedImageURL(expandedImageURL);
                }

                IDataMapAccessor clientMapAccessor = selectItemsIteratorComponent
                        .getClientMapAccessor(facesContext);
                if (clientMapAccessor != null) {
                    String keys[] = clientMapAccessor
                            .listDataKeys(facesContext);

                    Map<String, String> clientDataMap = selectItem
                            .getClientDataMap();
                    for (int i = 0; i < keys.length; i++) {
                        Object data = clientMapAccessor.getData(keys[i],
                                facesContext);
                        if (data != null) {
                            clientDataMap.put(keys[i], String.valueOf(data));
                        }
                    }
                }

                IDataMapAccessor serverMapAccessor = selectItemsIteratorComponent
                        .getServerMapAccessor(facesContext);
                if (serverMapAccessor != null) {
                    String keys[] = serverMapAccessor
                            .listDataKeys(facesContext);
                    Map<String, Object> serverDataMap = selectItem
                            .getServerDataMap();
                    for (int i = 0; i < keys.length; i++) {
                        Object data = serverMapAccessor.getData(keys[i],
                                facesContext);
                        if (data != null) {
                            serverDataMap.put(keys[i], data);
                        }
                    }
                }
            }

        } finally {
            requestMap.put(varName, oldValue);
            if (indexVar != null) {
                requestMap.put(indexVar, oldIndexVar);
            }
        }

        return selectItems.toArray(new SelectItem[selectItems.size()]);
    }

    private static Iterator< ? > convertToIterator(final Object items) {
        if (items instanceof Iterator) {
            return (Iterator< ? >) items;
        }
        if (items instanceof Collection) {
            return ((Collection< ? >) items).iterator();
        }
        if (items.getClass().isArray()) {
            return new Iterator<Object>() {
                private final int size = Array.getLength(items);

                private int index = 0;

                public boolean hasNext() {
                    return index < size;
                }

                public SelectItem next() {
                    return (SelectItem) Array.get(items, index++);
                }

                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
        }

        if (items instanceof IAdaptable) {
            Object collection = ((IAdaptable) items).getAdapter(
                    Collection.class, null);
            if (collection != null) {
                return ((Collection< ? >) collection).iterator();
            }
            Iterator< ? > iterator = ((IAdaptable) items).getAdapter(
                    Iterator.class, null);
            if (iterator != null) {
                return iterator;
            }
        }

        return null;
    }
}
