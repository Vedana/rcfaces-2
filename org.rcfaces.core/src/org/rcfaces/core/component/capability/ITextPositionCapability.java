/*
 * $Id: ITextPositionCapability.java,v 1.1 2011/04/12 09:25:24 oeuillot Exp $
 * 
 */
package org.rcfaces.core.component.capability;

/**
 * A string value specifying the position of the text in the component :
 * <ul>
 * <li> left </li>
 * <li> right </li>
 * <li> top </li>
 * <li> bottom </li>
 * </ul>
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:24 $
 */
public interface ITextPositionCapability extends
        IHorizontalTextPositionCapability {

    int TOP_POSITION = 0x20;

    int BOTTOM_POSITION = 0x40;
}
