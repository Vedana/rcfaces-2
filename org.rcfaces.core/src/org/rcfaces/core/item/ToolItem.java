/*
 * $Id: ToolItem.java,v 1.2 2013/07/03 12:25:07 jbmeslin Exp $
 */
package org.rcfaces.core.item;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.rcfaces.core.component.ToolItemComponent;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:07 $
 */
public class ToolItem extends DefaultItem implements IToolItem {

    

    private static final long serialVersionUID = 4088175556368874150L;

    public ToolItem() {
    }

    public ToolItem(String label) {
        super(label);
    }

    public ToolItem(String label, String description, boolean disabled,
            SelectItem items[]) {
        super(label, description, disabled, items);
    }

    public ToolItem(IToolItem toolItem) {
        super(toolItem);

        setBorderType(toolItem.getBorderType());

        setTextPosition(toolItem.getTextPosition());
    }

    public ToolItem(ToolItemComponent toolItemComponent) {
        super(toolItemComponent);

        FacesContext facesContext = FacesContext.getCurrentInstance();

        setBorderType(toolItemComponent.getBorderType(facesContext));

        setTextPosition(toolItemComponent.getTextPosition(facesContext));
    }

}
