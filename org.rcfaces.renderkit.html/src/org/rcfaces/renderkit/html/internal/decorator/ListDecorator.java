/*
 * $Id: ListDecorator.java,v 1.3 2013/07/03 12:25:09 jbmeslin Exp $
 */
package org.rcfaces.renderkit.html.internal.decorator;

import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;

import org.rcfaces.core.internal.renderkit.IComponentData;
import org.rcfaces.core.internal.tools.ValuesTools;
import org.rcfaces.core.model.IFilterProperties;
import org.rcfaces.renderkit.html.internal.HtmlTools;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/07/03 12:25:09 $
 */
public class ListDecorator extends ComboDecorator {
    

    public ListDecorator(UIComponent component,
            IFilterProperties filterProperties, boolean jsVersion) {
        super(component, filterProperties, jsVersion);
    }

    protected void decodeList(FacesContext facesContext, UIInput component,
            IComponentData componentData) {
        String value = componentData.getStringProperty("value");
        if (value != null) {
            if (value.length() < 1) {
                ValuesTools.setValue(component, null);
                return;
            }

            StringTokenizer st = new StringTokenizer(value,
                    HtmlTools.LIST_SEPARATORS);
            Set<String> s = new HashSet<String>(st.countTokens());
            for (; st.hasMoreTokens();) {
                s.add(st.nextToken());
            }

            ValuesTools.setValue(component, s.toArray());
            return;
        }

        String values[] = componentData.getComponentParameters();

        ValuesTools.setValue(component, values);
    }

}
