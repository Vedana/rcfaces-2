/*
 * $Id: AbstractFilteredCollection.java,v 1.3 2013/11/13 12:53:20 jbmeslin Exp $
 */
package org.rcfaces.core.model;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import javax.faces.component.UIComponent;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:20 $
 */
public abstract class AbstractFilteredCollection<T> implements Collection<T>,
        IFiltredCollection<T>, IFiltredCollection2<T> {

    public static final IFiltredCollection< ? > EMPTY_FILTERED_COLLECTION = emptyFilteredCollection();

    public static final Collection< ? > EMPTY_COLLECTION = (Collection< ? >) EMPTY_FILTERED_COLLECTION;

    public static <T> IFiltredCollection<T> emptyFilteredCollection() {
        return new AbstractFilteredCollection<T>() {
            @Override
            protected boolean accept(IFilterProperties filter, T selectItem) {
                return false;
            }
        };
    }

    protected final Collection<T> collection;

    public AbstractFilteredCollection() {
        this(Collections.<T> emptyList());
    }

    public AbstractFilteredCollection(Collection<T> collection) {
        this.collection = collection;
    }

    /**
     * 
     * @param filter
     * @param selectItem
     * @return <code>true</code> if the selectItem is sent to the client.
     */
    protected abstract boolean accept(IFilterProperties filter, T selectItem);

    public int size() {
        return collection.size();
    }

    public boolean add(T o) {
        return collection.add(o);
    }

    public boolean addAll(Collection< ? extends T> c) {
        return collection.addAll(c);
    }

    public void clear() {
        collection.clear();
    }

    public boolean contains(Object o) {
        return collection.contains(o);
    }

    public boolean containsAll(Collection< ? > c) {
        return collection.containsAll(c);
    }

    public boolean isEmpty() {
        return collection.isEmpty();
    }

    public Iterator<T> iterator() {
        return collection.iterator();
    }

    public boolean remove(Object o) {
        return collection.remove(o);
    }

    public boolean removeAll(Collection< ? > c) {
        return collection.removeAll(c);
    }

    public boolean retainAll(Collection< ? > c) {
        return collection.retainAll(c);
    }

    public Object[] toArray() {
        return collection.toArray();
    }

    public <V> V[] toArray(V[] a) {
        return collection.toArray(a);
    }

    public Iterator<T> iterator(IFilterProperties filterProperties,
            int maxNumberResult) {
        return new FilteredIterator(filterProperties, maxNumberResult);
    }

    public Iterator<T> iterator(UIComponent component,
            IFilterProperties filterProperties, int maxNumberResult) {
        return new FilteredIterator(component, filterProperties,
                maxNumberResult);
    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:20 $
     */
    protected class FilteredIterator implements IFiltredIterator<T> {

        private final IFilterProperties filterProperties;

        private final UIComponent component;

        private int maxResultNumber;

        private boolean limitTested = false;

        private int currentIndex = 0;

        private int size;

        private Iterator<T> iterator;

        private T currentSelectItem;

        public FilteredIterator(IFilterProperties filterProperties) {
            this(null, filterProperties, NO_MAXIMUM_RESULT_NUMBER);
        }

        public FilteredIterator(IFilterProperties filterProperties,
                int maxResultNumber) {
            this(null, filterProperties, maxResultNumber);
        }

        public FilteredIterator(UIComponent component,
                IFilterProperties filterProperties, int maxResultNumber) {

            this.component = component;
            this.filterProperties = filterProperties;
            this.maxResultNumber = maxResultNumber;
            this.size = 0;

            iterator = collection.iterator();
        }

        public void remove() {
            iterator.remove();
        }

        public boolean hasNext() {
            if (iterator == null) {
                return false;
            }
            if (currentSelectItem != null) {
                return true;
            }

            if (maxResultNumber > 0 && currentIndex >= maxResultNumber) {
                if (limitTested) {
                    return false;
                }

                limitTested = true;

                // La limite est atteinte, mais il en reste peut-etre
                // d'autres ...

                for (; iterator.hasNext();) {
                    T selectItem = iterator.next();
                    if (accept(filterProperties, selectItem) == false) {
                        continue;
                    }

                    size++;
                    break;
                }

                return false;
            }

            for (;;) {
                if (iterator.hasNext() == false) {
                    iterator = null;
                    return false;
                }

                T selectItem = iterator.next();
                if (accept(filterProperties, selectItem) == false) {
                    continue;
                }

                currentSelectItem = selectItem;
                size++;
                return true;
            }
        }

        public T next() {
            if (currentSelectItem == null) {
                throw new IllegalStateException("No more selectItems ...");
            }

            T old = currentSelectItem;
            currentSelectItem = null;
            currentIndex++;

            return old;
        }

        public int getSize() {
            return size;
        }

        public void release() {
        }

        protected UIComponent getComponent() {
            if (component == null) {
                throw new NullPointerException("Component is not known !");
            }

            return component;
        }
    }

}
