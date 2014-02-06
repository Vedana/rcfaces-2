/*
 * $Id: FilesCollectorDecorator.java,v 1.3 2013/07/03 12:25:09 jbmeslin Exp $
 */
package org.rcfaces.renderkit.html.internal.decorator;

import java.util.ArrayList;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.component.UISelectItem;
import javax.faces.model.SelectItem;

import org.rcfaces.core.component.FileItemComponent;
import org.rcfaces.core.internal.renderkit.IComponentRenderContext;
import org.rcfaces.core.internal.renderkit.WriterException;
import org.rcfaces.core.item.BasicSelectItem;
import org.rcfaces.core.item.FileItem;
import org.rcfaces.renderkit.html.internal.agent.UserAgentRuleTools;
import org.rcfaces.renderkit.html.internal.util.FileItemSource;
import org.rcfaces.renderkit.html.item.IUserAgentVaryFileItem;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/07/03 12:25:09 $
 */
public class FilesCollectorDecorator extends AbstractSelectItemsDecorator {

    

    private static final FileItemSource SOURCE_EMPTY_ARRAY[] = new FileItemSource[0];

    private List<FileItemSource> sources;

    public FilesCollectorDecorator(UIComponent component) {
        super(component, null);
    }

    protected SelectItemsContext createHtmlContext() {
        IComponentRenderContext componentRenderContext = htmlWriter
                .getComponentRenderContext();

        return new SelectItemsContext(this, componentRenderContext,
                getComponent(), null);
    }

    protected SelectItemsContext createJavaScriptContext()
            throws WriterException {
        return null;
    }

    public int encodeNodeBegin(UIComponent component, SelectItem selectItem,
            boolean hasChild, boolean isVisible) throws WriterException {

        if (isVisible == false || selectItem.isDisabled()) {
            return SKIP_NODE;
        }

        Object value = selectItem.getValue();
        if (value instanceof String) {
            String src = ((String) value).trim();

            if (src.length() > 0) {
                boolean rendered = true;

                if (selectItem instanceof IUserAgentVaryFileItem) {
                    IUserAgentVaryFileItem userAgentVaryFileItem = (IUserAgentVaryFileItem) selectItem;

                    if (UserAgentRuleTools.accept(getComponentRenderContext()
                            .getFacesContext(), userAgentVaryFileItem) == false) {
                        rendered = false;
                    }
                }

                if (rendered) {
                    FileItemSource source = new FileItemSource(selectItem);

                    if (sources == null) {
                        sources = new ArrayList<FileItemSource>(8);
                    }
                    if (sources.indexOf(source) < 0) {
                        sources.add(source);
                    }
                }
            }
        }

        return EVAL_NODE;
    }

    public void encodeNodeEnd(UIComponent component, SelectItem selectItem,
            boolean hasChild, boolean isVisible) throws WriterException {
    }

    protected SelectItem createSelectItem(UISelectItem component) {
        if (component instanceof FileItemComponent) {
            return new FileItem(component);
        }

        return new BasicSelectItem(component);
    }

    public FileItemSource[] listSources() {
        if (sources == null || sources.isEmpty()) {
            return SOURCE_EMPTY_ARRAY;
        }

        return sources.toArray(new FileItemSource[sources.size()]);
    }
}
