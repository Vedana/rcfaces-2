/*
 * $Id: SelectItemMappers.java,v 1.3 2013/11/13 12:53:22 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.internal.tools;

import javax.faces.component.UIComponent;
import javax.faces.model.SelectItem;

import org.rcfaces.core.internal.decorator.ISelectItemMapper;
import org.rcfaces.core.item.IImagesItem;

/**
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:22 $
 */
public class SelectItemMappers {

    public static final ISelectItemMapper SEARCH_IMAGE_MAPPER = new ISelectItemMapper() {

        public boolean map(SelectItem si) {
            if (si instanceof IImagesItem) {
                return false;
            }
            return true;
        }

        public void unknownComponent(UIComponent component) {
        }

        public boolean acceptCollections() {
            return true;
        }

    };

}
