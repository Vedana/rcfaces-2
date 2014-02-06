/*
 * $Id: IColumnsContainer.java,v 1.1 2013/12/11 10:17:38 jbmeslin Exp $
 */
package org.rcfaces.core.internal.capability;

/**
 * 
 */
import org.rcfaces.core.component.iterator.IColumnIterator;

public interface IColumnsContainer {
    IColumnIterator listColumns();

}
