/*
 * $Id: AbstractCriteria.java,v 1.1 2013/11/13 12:53:24 jbmeslin Exp $
 */
package org.rcfaces.core.internal.repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.faces.component.StateHolder;
import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;

import org.rcfaces.core.internal.lang.StringAppender;
import org.rcfaces.core.internal.repository.IRepository.ICriteria;
import org.rcfaces.core.internal.webapp.URIParameters;
import org.rcfaces.core.lang.IAdaptable;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.1 $ $Date: 2013/11/13 12:53:24 $
 */
public abstract class AbstractCriteria implements ICriteria, IAdaptable,
        StateHolder {

    private ICriteria parent;

    private boolean transientState;

    /**
     * For serialisation ONLY !
     */
    protected AbstractCriteria() {
    }

    public final ICriteria getParent() {
        return parent;
    }

    protected void setParent(ICriteria parent) {
        this.parent = parent;
    }

    public final void appendSuffix(URIParameters uriParameters) {
        appendSuffix(uriParameters, true);
    }

    public void appendSuffix(URIParameters uriParameters, boolean recursive) {
        if (recursive && parent != null) {
            parent.appendSuffix(uriParameters);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T getAdapter(Class<T> adapter, Object parameter) {
        if (adapter.isInstance(this)) {
            return (T) this;
        }

        if (parent != null) {
            return parent.getAdapter(adapter, parameter);
        }

        return null;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((parent == null) ? 0 : parent.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AbstractCriteria other = (AbstractCriteria) obj;
        if (parent == null) {
            if (other.parent != null)
                return false;
        } else if (!parent.equals(other.parent))
            return false;
        return true;
    }

    @Override
    public String toString() {
        StringAppender builder = new StringAppender(128);
        builder.append("[AbtractCriteria parent='").append(parent).append("']");
        return builder.toString();
    }

    public Object saveState(FacesContext context) {
        if (parent != null) {
            return UIComponentBase.saveAttachedState(context, parent);
        }
        return null;
    }

    public void restoreState(FacesContext context, Object state) {
        if (state == null) {
            return;
        }
        parent = (ICriteria) UIComponentBase.restoreAttachedState(context,
                state);
    }

    public boolean isTransient() {
        return transientState;
    }

    public void setTransient(boolean newTransientValue) {
        this.transientState = newTransientValue;
    }

    public final List<String> listURIs(String uri) {
        URIParameters uriParameters = URIParameters.parseURI(uri);

        if (getParent() == null) {
            appendSuffix(uriParameters);

            String newURI = uriParameters.computeParametredURI();

            return Collections.singletonList(newURI);
        }

        Set<String> list = new HashSet<String>();
        appendURIs(uriParameters, list, this);

        List<String> ret = new ArrayList<String>(list);
        Collections.sort(ret, new Comparator<String>() {

            public int compare(String o1, String o2) {
                int ret = o2.length() - o1.length();
                if (ret < 0 || ret > 0) {
                    return ret;
                }

                return o1.compareTo(o2);
            }
        });

        return ret;
    }

    private static void appendURIs(URIParameters uriParameters,
            Set<String> list, ICriteria criteria) {

        URIParameters p2 = uriParameters.clone();
        criteria.appendSuffix(p2, false);

        list.add(p2.computeParametredURI());

        if (criteria.getParent() != null) {
            appendURIs(p2, list, criteria.getParent());
        }

        if (criteria.getParent() != null) {
            appendURIs(uriParameters, list, criteria.getParent());
        }

    }
}
