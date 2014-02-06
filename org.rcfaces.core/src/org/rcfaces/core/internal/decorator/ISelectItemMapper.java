/*
 * $Id: ISelectItemMapper.java,v 1.1 2011/04/12 09:25:51 oeuillot Exp $
 * 
 */
package org.rcfaces.core.internal.decorator;

import javax.faces.component.UIComponent;
import javax.faces.model.SelectItem;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:51 $
 */
public interface ISelectItemMapper {

    boolean map(SelectItem si);

    void unknownComponent(UIComponent component);

    boolean acceptCollections();
}
