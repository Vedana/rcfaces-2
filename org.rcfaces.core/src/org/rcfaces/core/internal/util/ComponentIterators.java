/*
 * $Id: ComponentIterators.java,v 1.4 2013/11/13 12:53:22 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.internal.util;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import javax.faces.component.UIComponent;

import org.rcfaces.core.component.iterator.IComponentIterator;
import org.rcfaces.core.internal.Constants;
import org.rcfaces.core.internal.manager.IContainerManager;

/**
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.4 $ $Date: 2013/11/13 12:53:22 $
 */
public class ComponentIterators {

    public static final IComponentIterator EMPTY_COMPONENT_ITERATOR = new EmptyComponentIterator();

    public static final UIComponent[] EMPTY_COMPONENT_ARRAY = new UIComponent[0];

    public static <T> int indexOf(IContainerManager parent, UIComponent child,
            Class<T> childClass) {

        if (Constants.CACHED_COMPONENT_ITERATOR
                && Constants.STATED_COMPONENT_CHILDREN_LIST) {
            UIComponent aos[] = CachedChildrenList.getArray(parent, childClass);
            for (int i = 0; i < aos.length; i++) {
                if (child == aos[i] || child.equals(aos[i])) {
                    return i;
                }
            }
            return -1;
        }

        int idx = 0;
        for (Iterator<UIComponent> it = parent.getChildren().iterator(); it
                .hasNext();) {
            UIComponent obj = it.next();
            if (obj == null) {
                continue;
            }

            if (childClass.isInstance(obj) == false) {
                continue;
            }

            if (child == obj || obj.equals(child)) {
                return idx;
            }

            idx++;
        }

        return -1;
    }

    public static <T> UIComponent componentAt(IContainerManager parent,
            Class<T> childClass, int position) {
        if (Constants.CACHED_COMPONENT_ITERATOR
                && Constants.STATED_COMPONENT_CHILDREN_LIST) {
            UIComponent elements[] = CachedChildrenList.getArray(parent,
                    childClass);
            if (position < 0 || position >= elements.length) {
                throw new IndexOutOfBoundsException("Out of range (0 <= "
                        + position + " <= " + (elements.length - 1) + ")");
            }

            return elements[position];
        }

        int idx = 0;
        for (Iterator<UIComponent> it = parent.getChildren().iterator(); it
                .hasNext();) {
            UIComponent obj = it.next();
            if (obj == null) {
                continue;
            }

            if (childClass.isInstance(obj) == false) {
                continue;
            }

            if (idx == position) {
                return obj;
            }

            idx++;
        }

        throw new IndexOutOfBoundsException("Out of range (0 <= " + position
                + " <= " + (idx - 1) + ")");
    }

    public static <T> int count(IContainerManager parent, Class<T> childClass) {
        if (Constants.CACHED_COMPONENT_ITERATOR
                && Constants.STATED_COMPONENT_CHILDREN_LIST) {
            return CachedChildrenList.getCount(parent, childClass);
        }

        int cnt = 0;
        for (Iterator<UIComponent> it = parent.getChildren().iterator(); it
                .hasNext();) {
            UIComponent obj = it.next();
            if (obj == null) {
                continue;
            }

            if (childClass.isInstance(obj) == false) {
                continue;
            }

            cnt++;
        }

        return cnt;
    }

    public static <T> boolean removeAll(IContainerManager parent,
            Class<T> childClass) {
        int count = parent.getChildCount();
        if (count < 1) {
            return false;
        }

        List<T> rev = list((UIComponent) parent, childClass);

        if (rev == null || rev.isEmpty()) {
            return false;
        }

        return parent.getChildren().removeAll(rev);
    }

    public static <T> List<T> list(UIComponent parent, Class<T> childClass) {

        if (Constants.CACHED_COMPONENT_ITERATOR
                && Constants.STATED_COMPONENT_CHILDREN_LIST) {
            if (parent instanceof IContainerManager) {
                return CachedChildrenList.getList((IContainerManager) parent,
                        childClass);
            }
        }

        List<UIComponent> components = parent.getChildren();
        int total = components.size();
        if (total == 0) {
            return Collections.emptyList();
        }

        List<T> rev = null;
        for (Iterator<UIComponent> it = components.iterator(); it.hasNext(); total--) {
            UIComponent component = it.next();

            if (childClass.isInstance(component) == false) {
                continue;
            }

            if (rev == null) {
                rev = new ArrayList<T>(total);
            }

            rev.add((T) component);
        }

        if (rev == null) {
            return Collections.emptyList();
        }

        return rev;
    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.4 $ $Date: 2013/11/13 12:53:22 $
     */
    private static class EmptyComponentIterator implements IComponentIterator {

        /*
         * (non-Javadoc)
         * 
         * @see org.rcfaces.core.iterators.IComponentIterator#count()
         */
        public final int count() {
            return 0;
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.rcfaces.core.iterators.IComponentIterator#hasNext()
         */
        public final boolean hasNext() {
            return false;
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.rcfaces.core.iterators.IComponentIterator#nextComponent()
         */
        public final UIComponent nextComponent() {
            throw new NoSuchElementException("Empty iterator");
        }

        public UIComponent[] toArray(UIComponent[] array) {
            return EMPTY_COMPONENT_ARRAY;
        }

    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.4 $ $Date: 2013/11/13 12:53:22 $
     */
    public static class ComponentListIterator<T> implements
            IComponentIterator<T> {

        private final Iterator<T> iterator;

        private int count;

        protected ComponentListIterator(List<T> list) {
            this.iterator = list.iterator();
            this.count = list.size();
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.rcfaces.core.iterators.IComponentIterator#count()
         */
        public final int count() {
            return count;
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.rcfaces.core.iterators.IComponentIterator#hasNext()
         */
        public final boolean hasNext() {
            return count > 0;
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.rcfaces.core.iterators.IComponentIterator#nextComponent()
         */
        public final T nextComponent() {
            T object = iterator.next();

            count--;

            return object;
        }

        public UIComponent[] toArray(UIComponent[] array) {
            if (count < 1) {
                // Ca doit peter ici ! iterator.next(); // Pourquoi ?

                if (array == null) {
                    return EMPTY_COMPONENT_ARRAY;
                }

                if (array.length == 0) {
                    return array;
                }

                return (UIComponent[]) Array.newInstance(array.getClass()
                        .getComponentType(), 0);
            }

            if (array == null) {
                array = new UIComponent[count];

            } else if (array.length != count) {
                array = (UIComponent[]) Array.newInstance(array.getClass()
                        .getComponentType(), count);
            }

            for (int i = 0; count > 0; i++) {
                array[i] = (UIComponent) iterator.next();

                count--;
            }

            return array;
        }
    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.4 $ $Date: 2013/11/13 12:53:22 $
     */
    public static class ComponentArrayIterator<T> implements
            IComponentIterator<T> {

        private final Object[] array;

        private int count;

        protected ComponentArrayIterator(T object) {
            this.array = new Object[] { object };
            this.count = 1;
        }

        protected ComponentArrayIterator(T array[]) {
            this.array = array;
            this.count = array.length;
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.rcfaces.core.iterators.IComponentIterator#count()
         */
        public final int count() {
            return count;
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.rcfaces.core.iterators.IComponentIterator#hasNext()
         */
        public final boolean hasNext() {
            return count > 0;
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.rcfaces.core.iterators.IComponentIterator#nextComponent()
         */
        public final T nextComponent() {
            if (count < 1) {
                throw new NoSuchElementException(
                        "No more components ! (position="
                                + (array.length - count) + ", arraySize="
                                + array.length + ")");
            }

            T component = (T) array[array.length - count];
            count--;

            return component;
        }

        public UIComponent[] toArray(UIComponent[] array) {
            if (count < 1) {
                throw new NoSuchElementException(
                        "No more components ! (position="
                                + (array.length - count) + ", arraySize="
                                + array.length + ")");
            }

            if (array == null) {
                array = new UIComponent[count];

            } else if (array.length < count) {
                array = (UIComponent[]) Array.newInstance(array.getClass()
                        .getComponentType(), count);
            }

            System.arraycopy(this.array, this.array.length - count, array, 0,
                    count);

            count = 0;

            return array;
        }

    }
}
