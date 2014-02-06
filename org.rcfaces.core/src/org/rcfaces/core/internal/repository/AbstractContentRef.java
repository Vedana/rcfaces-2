/*
 * $Id: AbstractContentRef.java,v 1.1 2013/11/13 12:53:25 jbmeslin Exp $
 */
package org.rcfaces.core.internal.repository;

import org.rcfaces.core.internal.repository.IRepository.ICriteria;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.1 $ $Date: 2013/11/13 12:53:25 $
 */
public abstract class AbstractContentRef implements IContentRef {

    private final ICriteria criteria;

    protected AbstractContentRef(ICriteria criteria) {
        this.criteria = criteria;
    }

    public final ICriteria getCriteria() {
        return criteria;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((criteria == null) ? 0 : criteria.hashCode());
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
        AbstractContentRef other = (AbstractContentRef) obj;
        if (criteria == null) {
            if (other.criteria != null)
                return false;
        } else if (!criteria.equals(other.criteria))
            return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(128);
        builder.append("[AbstractContentRef criteria='").append(criteria)
                .append("']");
        return builder.toString();
    }

}
