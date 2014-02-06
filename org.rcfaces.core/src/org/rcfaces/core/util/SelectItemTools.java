/*
 * $Id: SelectItemTools.java,v 1.3 2013/11/13 12:53:25 jbmeslin Exp $
 */
package org.rcfaces.core.util;

import java.util.Iterator;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.component.UISelectItem;
import javax.faces.component.UISelectItems;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.faces.model.SelectItemGroup;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.component.capability.IImageCapability;
import org.rcfaces.core.internal.Constants;
import org.rcfaces.core.internal.RcfacesContext;
import org.rcfaces.core.item.BasicImagesSelectItem;
import org.rcfaces.core.item.BasicSelectItem;
import org.rcfaces.core.item.ISelectItem;
import org.rcfaces.core.item.ISelectItemGroup;
import org.rcfaces.core.lang.IAdaptable;
import org.rcfaces.core.model.IFilterProperties;
import org.rcfaces.core.model.IFiltredCollection;
import org.rcfaces.core.model.IFiltredCollection.IFiltredIterator;
import org.rcfaces.core.model.IFiltredCollection2;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:25 $
 */
public class SelectItemTools {

    private static final Log LOG = LogFactory.getLog(SelectItemTools.class);

    public static <T> T traverseSelectItemTree(FacesContext facesContext,
            UIComponent component, IFilterProperties filterProperties,
            ISelectItemNodeHandler<T> handler) {

        if (facesContext == null) {
            facesContext = FacesContext.getCurrentInstance();
        }

        SelectItemWalker<T> walker = new SelectItemWalker<T>(facesContext,
                component, filterProperties, handler);

        if (handler != null) {
            T t = handler.beginTree(component);
            if (t != null) {
                return t;
            }
        }

        List<UIComponent> children = component.getChildren();
        for (UIComponent child : children) {
            T t = walker.processComponent(child);
            if (t != null) {
                return t;
            }
        }

        if (handler != null) {
            T t = handler.endTree(component);
            if (t != null) {
                return t;
            }
        }

        return null;
    }

    public static SelectItem convertToSelectItem(FacesContext facesContext,
            Object value) {
        if ((value == null) || (value instanceof SelectItem)) {
            return (SelectItem) value;
        }

        if (value instanceof ISelectItem) {
            if (value instanceof ISelectItemGroup) {
                ISelectItemGroup selectItemGroup = (ISelectItemGroup) value;

                SelectItemGroup sig = new SelectItemGroup(
                        selectItemGroup.getLabel(),
                        selectItemGroup.getDescription(),
                        selectItemGroup.isDisabled(),
                        selectItemGroup.getSelectItems());

                sig.setValue(selectItemGroup.getValue());

                return sig;
            }

            ISelectItem selectItem = (ISelectItem) value;

            return new SelectItem(selectItem.getValue(), selectItem.getLabel(),
                    selectItem.getDescription(), selectItem.isDisabled());
        }

        if (value instanceof IAdaptable) {
            IAdaptable adaptable = (IAdaptable) value;

            SelectItem selectItem = adaptable
                    .getAdapter(SelectItem.class, null);
            if (selectItem != null) {
                return selectItem;
            }
        }

        if (Constants.ADAPT_SELECT_ITEMS) {
            RcfacesContext rcfacesContext = RcfacesContext
                    .getInstance(facesContext);

            SelectItem selectItem = rcfacesContext.getAdapterManager()
                    .getAdapter(value, SelectItem.class, null);
            if (selectItem != null) {
                return selectItem;
            }
        }

        return null;
    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:25 $
     */
    public interface ISelectItemNodeHandler<T> {
        T beginTree(UIComponent component);

        T beginNode(UIComponent component, SelectItem selectItem);

        T endNode(UIComponent component, SelectItem selectItem);

        T endTree(UIComponent component);
    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:25 $
     */
    public static class DefaultSelectItemNodeHandler<T> implements
            ISelectItemNodeHandler<T> {

        public T beginTree(UIComponent component) {
            return null;
        }

        public T beginNode(UIComponent component, SelectItem selectItem) {
            return null;
        }

        public T endNode(UIComponent component, SelectItem selectItem) {
            return null;
        }

        public T endTree(UIComponent component) {
            return null;
        }
    }

    private static class SelectItemWalker<T> {

        private final FacesContext facesContext;

        private final UIComponent mainComponent;

        private final IFilterProperties filterProperties;

        private final ISelectItemNodeHandler<T> handler;

        public SelectItemWalker(FacesContext facesContext,
                UIComponent mainComponent, IFilterProperties filterProperties,
                ISelectItemNodeHandler<T> handler) {

            this.facesContext = facesContext;
            this.mainComponent = mainComponent;
            this.filterProperties = filterProperties;
            this.handler = handler;
        }

        protected T processComponent(UIComponent component) {

            if (component instanceof UISelectItem) {
                UISelectItem uiSelectItem = (UISelectItem) component;

                Object value = uiSelectItem.getValue();
                if (value instanceof SelectItem) {
                    T t = processSelectItem(component, (SelectItem) value, true);
                    if (t != null) {
                        return t;
                    }

                } else if (value instanceof SelectItem[]) {
                    SelectItem selectItems[] = (SelectItem[]) value;
                    for (SelectItem selectItem : selectItems) {
                        T t = processSelectItem(component, selectItem, false);
                        if (t != null) {
                            return t;
                        }
                    }

                } else if (value instanceof SelectItemGroup[]) {
                    SelectItemGroup[] selectItems = (SelectItemGroup[]) value;
                    for (SelectItem selectItem : selectItems) {
                        T t = processSelectItem(component, selectItem, false);
                        if (t != null) {
                            return t;
                        }
                    }

                } else if ((value instanceof IFiltredCollection)
                        || (value instanceof IFiltredCollection2)) {

                    T t = processCollection(facesContext, component,
                            filterProperties, value);
                    if (t != null) {
                        return t;
                    }

                } else if (value == null) {
                    SelectItem selectItem;
                    if (uiSelectItem instanceof IImageCapability) {
                        selectItem = new BasicImagesSelectItem(uiSelectItem);

                    } else {
                        selectItem = new BasicSelectItem(uiSelectItem);
                    }

                    T t = processSelectItem(component, selectItem, true);
                    if (t != null) {
                        return t;
                    }

                } else {
                    SelectItem selectItem = convertToSelectItem(facesContext,
                            value);

                    if (selectItem != null) {
                        T t = processSelectItem(component, selectItem, true);
                        if (t != null) {
                            return t;
                        }
                    } else {
                        if (LOG.isErrorEnabled()) {
                            LOG.error("Unknown type of selectItem value '"
                                    + value + "'");
                        }
                    }
                }

            } else if (component instanceof UISelectItems) {
                Object value = ((UISelectItems) component).getValue();

                if (value instanceof SelectItem[]) {
                    SelectItem selectItems[] = (SelectItem[]) value;
                    for (SelectItem selectItem : selectItems) {
                        T t = processSelectItem(component, selectItem, false);
                        if (t != null) {
                            return t;
                        }
                    }

                } else if ((value instanceof IFiltredCollection)
                        || (value instanceof IFiltredCollection2)) {

                    T t = processCollection(facesContext, component,
                            filterProperties, value);
                    if (t != null) {
                        return t;
                    }
                } else {
                    if (LOG.isErrorEnabled()) {
                        LOG.error("Unknown type of selectItems value '" + value
                                + "'");
                    }
                }
            }

            return null;
        }

        private T processSelectItem(UIComponent component,
                SelectItem selectItem, boolean searchComponentChildren) {

            if (handler != null) {
                T t = handler.beginNode(component, selectItem);
                if (t != null) {
                    return t;
                }
            }

            if (selectItem instanceof SelectItemGroup) {
                SelectItemGroup group = (SelectItemGroup) selectItem;

                for (SelectItem child : group.getSelectItems()) {
                    processSelectItem(component, child, false);
                }

            }

            if (searchComponentChildren && component.getChildCount() > 0) {
                List<UIComponent> children = component.getChildren();
                for (UIComponent child : children) {

                    T t = processComponent(child);
                    if (t != null) {
                        return t;
                    }
                }
            }

            if (handler != null) {
                T t = handler.endNode(component, selectItem);
                if (t != null) {
                    return t;
                }
            }

            return null;
        }

        protected T processCollection(FacesContext facesContext,
                UIComponent component, IFilterProperties filterProperties,
                Object value) {

            Iterator< ? > it;
            if (value instanceof IFiltredCollection2) {
                it = ((IFiltredCollection2< ? >) value).iterator(component,
                        filterProperties, -1);
            } else {
                it = ((IFiltredCollection< ? >) value).iterator(
                        filterProperties, -1);
            }

            try {
                for (; it.hasNext();) {
                    Object item = it.next();
                    if ((item instanceof SelectItem) == false) {
                        item = convertToSelectItem(facesContext, item);

                        if (item == null) {
                            continue;
                        }
                    }

                    T t = processSelectItem(component, (SelectItem) item, false);
                    if (t != null) {
                        return t;
                    }
                }

            } finally {
                if (it instanceof IFiltredIterator) {
                    ((IFiltredIterator< ? >) it).release();
                }
            }

            return null;
        }
    }

}
