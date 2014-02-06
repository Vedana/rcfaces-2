/*
 * $Id: OrderedSet.java,v 1.3 2013/07/03 12:25:08 jbmeslin Exp $
 */
package org.rcfaces.core.lang;

import java.io.Serializable;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.rcfaces.core.model.ICommitableObject;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/07/03 12:25:08 $
 */
public class OrderedSet<T> extends AbstractSet<T> implements Cloneable,
        Serializable, ICommitableObject {
    

    private static final long serialVersionUID = -8481215239333898818L;

    private List<T> order;

    private boolean commited;

    public OrderedSet() {
        order = new ArrayList<T>();
    }

    public OrderedSet(int size) {
        order = new ArrayList<T>(size);
    }

    public OrderedSet(Collection<T> collection) {
        this(collection.size());

        addAll(collection);
    }

    @Override
    public Iterator<T> iterator() {
        final Iterator<T> it = order.iterator();

        return new Iterator<T>() {

            @Override
            public boolean hasNext() {
                return it.hasNext();
            }

            @Override
            public T next() {
                return it.next();
            }

            @Override
            public void remove() {
                if (commited) {
                    throw new IllegalStateException("Already commited set.");
                }

                it.remove();
            }

        };
    }

    @Override
    public boolean contains(Object o) {
        return order.contains(o);
    }

    @Override
    public boolean containsAll(Collection< ? > c) {
        return order.containsAll(c);
    }

    @Override
	public boolean add(T o) {
        if (commited) {
            throw new IllegalStateException("Already commited set.");
        }

        if (order.contains(o)) {
            return false;
        }

		return order.add(o);
    }

    @Override
    public boolean remove(Object o) {
        if (commited) {
            throw new IllegalStateException("Already commited set.");
        }

        return order.remove(o);
    }

    @Override
    public boolean removeAll(Collection< ? > c) {
        if (commited) {
            throw new IllegalStateException("Already commited set.");
        }

        return order.removeAll(c);
    }

    @Override
    public int size() {
        return order.size();
    }

    @Override
    public void clear() {
        if (commited) {
            throw new IllegalStateException("Already commited set.");
        }

        order.clear();
    }

    @Override
    public Object[] toArray() {
        return order.toArray();
    }

	@Override
	public <U> U[] toArray(U[] a) {
        return order.toArray(a);
    }

	@Override
    public int hashCode() {
        return order.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

		final OrderedSet<?> other = (OrderedSet<?>) obj;
        if (order == null) {
            if (other.order != null) {
                return false;
            }

        } else if (!order.equals(other.order)) {
            return false;
        }

        return true;
    }

    @Override
    public boolean retainAll(Collection< ? > c) {
        if (commited) {
            throw new IllegalStateException("Already commited set.");
        }

        return order.retainAll(c);
    }

    @Override
    public String toString() {
        return order.toString();
    }

    @Override
    public Object clone() {
        try {
			@SuppressWarnings("unchecked")
            OrderedSet<T> newSet = (OrderedSet<T>) super.clone();
            newSet.order = new ArrayList<T>(order);

            return newSet;

        } catch (CloneNotSupportedException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void commit() {
        if (order instanceof ArrayList) {
            ((ArrayList<T>) order).trimToSize();
        }
        this.commited = true;
    }

    @Override
    public boolean isCommited() {
        return commited;
    }

}
