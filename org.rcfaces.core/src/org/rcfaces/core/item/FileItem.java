/*
 * $Id: FileItem.java,v 1.2 2013/07/03 12:25:07 jbmeslin Exp $
 */
package org.rcfaces.core.item;

import javax.faces.component.UISelectItem;
import javax.faces.model.SelectItem;

import org.rcfaces.core.component.FileItemComponent;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:07 $
 */
public class FileItem extends SelectItem implements IFileItem {
    

    private static final long serialVersionUID = -691526095374431782L;

    private String charSet;

    public FileItem() {
    }

    public FileItem(IFileItem fileItem) {
        super(fileItem.getSrc(), null, null, false);

        this.charSet = fileItem.getCharSet();
    }

    public FileItem(UISelectItem component) {
        super(component.getItemValue(), component.getItemLabel(), component
                .getItemDescription(), component.isItemDisabled());

        if (component instanceof FileItemComponent) {
            charSet = ((FileItemComponent) component).getCharSet();
        }

    }

    public void setSrc(String src) {
        setValue(src);
    }

    public String getSrc() {
        return (String) getValue();
    }

    public String getCharSet() {
        return charSet;
    }

    public void setCharSet(String charSet) {
        this.charSet = charSet;
    }

}
