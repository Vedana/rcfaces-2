/*
 * $Id: IStateChildrenList.java,v 1.1 2011/04/12 09:25:38 oeuillot Exp $
 * 
 */
package org.rcfaces.core.internal.capability;

import java.util.List;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:38 $
 */
public interface IStateChildrenList extends List {

    void setChildren(List list);

    int getState();
}
