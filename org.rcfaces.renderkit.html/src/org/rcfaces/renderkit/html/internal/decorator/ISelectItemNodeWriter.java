/*
 * $Id: ISelectItemNodeWriter.java,v 1.2 2013/01/11 15:45:05 jbmeslin Exp $
 * 
 */
package org.rcfaces.renderkit.html.internal.decorator;

import javax.faces.component.UIComponent;
import javax.faces.model.SelectItem;

import org.rcfaces.core.internal.renderkit.WriterException;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/01/11 15:45:05 $
 */
public interface ISelectItemNodeWriter {

    int EVAL_NODE = 0;

    int SKIP_NODE = 1;

    // int SHOW_NODE = 2;

    SelectItemsContext getContext();

    void encodeNodeInit(UIComponent component, SelectItem selectItem);

    int encodeNodeBegin(UIComponent component, SelectItem selectItem,
            boolean hasChild, boolean isVisible) throws WriterException;

    void encodeNodeEnd(UIComponent component, SelectItem selectItem,
            boolean hasChild, boolean isVisible) throws WriterException;
    
    void refreshNode(UIComponent component) throws WriterException;

}
