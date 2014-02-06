/*
 * $Id: KeyLabelRenderer.java,v 1.3 2014/01/03 16:24:30 jbmeslin Exp $
 */
package org.rcfaces.renderkit.html.internal.renderer;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.component.KeyEntryComponent;
import org.rcfaces.core.component.KeyLabelComponent;
import org.rcfaces.core.internal.lang.StringAppender;
import org.rcfaces.core.internal.renderkit.IComponentRenderContext;
import org.rcfaces.core.internal.renderkit.IComponentWriter;
import org.rcfaces.core.internal.renderkit.WriterException;
import org.rcfaces.core.internal.tools.ValuesTools;
import org.rcfaces.core.internal.util.ParamUtils;
import org.rcfaces.core.item.IStyleClassItem;
import org.rcfaces.core.lang.IAdaptable;
import org.rcfaces.core.model.IFilterProperties;
import org.rcfaces.core.model.IFiltredCollection;
import org.rcfaces.core.model.IFiltredCollection2;
import org.rcfaces.renderkit.html.internal.AbstractCssRenderer;
import org.rcfaces.renderkit.html.internal.HtmlTools;
import org.rcfaces.renderkit.html.internal.IHtmlWriter;
import org.rcfaces.renderkit.html.internal.IJavaScriptRenderContext;
import org.rcfaces.renderkit.html.internal.JavaScriptClasses;
import org.rcfaces.renderkit.html.internal.ns.XhtmlNSAttributes;
import org.rcfaces.renderkit.html.internal.service.KeyLabelBehaviorListener;

/**
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2014/01/03 16:24:30 $
 */
@XhtmlNSAttributes({ "selectedStyleClass", "parentsStyleClass", "showParents",
        "filtred", "filterExpression", "className", "value", "disabled" })
public class KeyLabelRenderer extends AbstractCssRenderer {

    private static final Log LOG = LogFactory.getLog(KeyLabelRenderer.class);

    private static final String FILTRED_CONTENT_PROPERTY = "camelia.keyLabel.filtredContent";

    protected String getJavaScriptClassName() {
        return JavaScriptClasses.KEY_LABEL;
    }

    protected void encodeEnd(IComponentWriter writer) throws WriterException {

        IHtmlWriter htmlWriter = (IHtmlWriter) writer;

        IComponentRenderContext componentRenderContext = htmlWriter
                .getComponentRenderContext();

        FacesContext facesContext = componentRenderContext.getFacesContext();

        KeyLabelComponent keyLabelComponent = (KeyLabelComponent) componentRenderContext
                .getComponent();

        htmlWriter.startElement(IHtmlWriter.DIV);
        writeHtmlAttributes(htmlWriter);
        writeJavaScriptAttributes(htmlWriter);
        writeCssAttributes(htmlWriter);

        String ssc = keyLabelComponent.getSelectedStyleClass(facesContext);
        if (ssc != null && ssc.length() > 0) {
            htmlWriter.writeAttributeNS("selectedStyleClass", ssc);
        }

        boolean showParents = keyLabelComponent.isShowParents(facesContext);
        if (showParents) {
            String psc = keyLabelComponent.getParentsStyleClass(facesContext);
            if (psc != null && psc.length() > 0) {
                htmlWriter.writeAttributeNS("parentsStyleClass", psc);
            }

            htmlWriter.writeAttributeNS("showParents", true);
        }

        Object value = keyLabelComponent.getValue();

        if ((value instanceof IFiltredCollection)
                || (value instanceof IFiltredCollection2)) {

            componentRenderContext.setAttribute(FILTRED_CONTENT_PROPERTY,
                    Boolean.TRUE);

            htmlWriter.writeAttributeNS("filtred", true);

            IFilterProperties filterProperties = keyLabelComponent
                    .getFilterProperties(facesContext);

            if (filterProperties != null && filterProperties.isEmpty() == false) {
                String filterExpression = HtmlTools.encodeFilterExpression(
                        filterProperties, componentRenderContext
                                .getRenderContext().getProcessContext(),
                        keyLabelComponent);
                htmlWriter.writeAttributeNS("filterExpression",
                        filterExpression);
            }

        }

        SelectItem items[] = convertValue(keyLabelComponent, value, null);

        if (items == null || items.length == 0) {
            LOG.debug("Convert value returns NULL or empty array");

        } else if (showParents == false) {
            int idx = items.length - 1;

            renderItem(facesContext, htmlWriter, keyLabelComponent, items, idx,
                    true, true);

        } else {
            for (int i = 0; i < items.length; i++) {
                renderItem(facesContext, htmlWriter, keyLabelComponent, items,
                        i, i == 0, i + 1 == items.length);
            }
        }

        htmlWriter.endElement(IHtmlWriter.DIV);

        super.encodeEnd(htmlWriter);
    }

    protected String getItemStyleClass(KeyLabelComponent keyLabelComponent,
            SelectItem selectItem, int index, boolean last) {
        return "f_keyLabel_item";
    }

    protected void renderItem(FacesContext facesContext,
            IHtmlWriter htmlWriter, KeyLabelComponent keyLabelComponent,
            SelectItem[] selectItems, int index, boolean first, boolean last)
            throws WriterException {

        SelectItem selectItem = selectItems[index];

        htmlWriter.startElement(IHtmlWriter.LABEL);

        writeItemAttributes(htmlWriter, keyLabelComponent, selectItem, index,
                first, last);

        // htmlWriter.startElement(IHtmlWriter.LABEL);
        // htmlWriter.writeClass("f_keyLabel_label");

        writeText(htmlWriter, keyLabelComponent, selectItem.getLabel());

        // htmlWriter.endElement(IHtmlWriter.LABEL);

        htmlWriter.endElement(IHtmlWriter.LABEL);
    }

    protected void writeItemAttributes(IHtmlWriter htmlWriter,
            KeyLabelComponent keyLabelComponent, SelectItem selectItem,
            int index, boolean first, boolean last) throws WriterException {

        String className = computeClassName(keyLabelComponent, selectItem,
                index, first, last);

        htmlWriter.writeClass(className);

        if (selectItem instanceof IStyleClassItem) {
            String siClass = ((IStyleClassItem) selectItem).getStyleClass();

            if (siClass != null && siClass.length() > 0) {
                htmlWriter.writeAttributeNS("className", siClass);
            }
        }

        if (selectItem.getLabel().equals(selectItem.getValue()) == false) {

            String value = ValuesTools.valueToString(selectItem.getValue(),
                    keyLabelComponent, htmlWriter.getComponentRenderContext()
                            .getFacesContext());

            if (value != null) {
                htmlWriter.writeAttributeNS("value", value);
            }
        }

        if (selectItem.isDisabled()) {
            htmlWriter.writeAttributeNS("disabled", true);
        }
    }

    protected String computeClassName(KeyLabelComponent keyLabelComponent,
            SelectItem selectItem, int index, boolean first, boolean last) {

        String baseStyleClass = getItemStyleClass(keyLabelComponent,
                selectItem, index, last);

        StringAppender clazz = new StringAppender(baseStyleClass, 128);

        String disabled = (selectItem.isDisabled()) ? "_disabled" : null;
        if (disabled != null) {
            clazz.append(' ').append(baseStyleClass).append(disabled);
        }

        if (last) {
            clazz.append(' ').append(baseStyleClass).append("_last");
        }

        if (first) {
            clazz.append(' ').append(baseStyleClass).append("_first");
        }

        String customStyleClass = null;
        if (last) {
            customStyleClass = keyLabelComponent.getSelectedStyleClass();
        } else {
            customStyleClass = keyLabelComponent.getParentsStyleClass();
        }

        if (customStyleClass != null && customStyleClass.length() > 0) {
            clazz.append(' ').append(customStyleClass);

            if (disabled != null) {
                clazz.append(' ').append(customStyleClass).append(disabled);
            }
        }

        if (selectItem instanceof IStyleClassItem) {
            String siClass = ((IStyleClassItem) selectItem).getStyleClass();

            if (siClass != null && siClass.length() > 0) {
                clazz.append(' ').append(siClass);
                if (last) {
                    clazz.append(' ').append(siClass).append("_last");
                }
                if (first) {
                    clazz.append(' ').append(siClass).append("_first");
                }

                if (disabled != null) {
                    clazz.append(' ').append(siClass).append(disabled);
                }
            }
        }

        return clazz.toString();
    }

    protected void writeText(IHtmlWriter htmlWriter,
            KeyLabelComponent keyLabelComponent, String text)
            throws WriterException {
        if (text == null || text.trim().length() < 1) {
            return;
        }

        text = ParamUtils.formatMessage(keyLabelComponent, text);

        htmlWriter.writeText(text);
    }

    protected SelectItem[] convertValue(KeyLabelComponent keyLabelComponent,
            Object value, IFilterProperties filterProperties) {
        if (value == null) {
            return new SelectItem[0];
        }

        if (value instanceof SelectItem[]) {
            return (SelectItem[]) value;
        }

        if ((value instanceof IFiltredCollection)
                || (value instanceof IFiltredCollection2)) {

            int max = keyLabelComponent.isShowParents() ? -1 : 0;

            Iterator it;
            if (value instanceof IFiltredCollection2) {

                it = ((IFiltredCollection2) value).iterator(keyLabelComponent,
                        filterProperties, max);
            } else {
                it = ((IFiltredCollection) value).iterator(filterProperties,
                        max);
            }

            List<SelectItem> l = new ArrayList<SelectItem>();
            for (; it.hasNext();) {
                Object item = it.next();

                SelectItem selectItem = convertItem(item);
                if (selectItem == null) {
                    continue;
                }

                l.add(selectItem);
            }

            return l.toArray(new SelectItem[l.size()]);
        }

        if (value instanceof IAdaptable) {
            IAdaptable adaptable = (IAdaptable) value;

            SelectItem sis[] = adaptable.getAdapter(SelectItem[].class, null);
            if (sis != null) {
                return sis;
            }
        }

        if (value.getClass().isArray()) {
            int length = Array.getLength(value);
            List<SelectItem> l = new ArrayList<SelectItem>(length);

            for (int i = 0; i < length; i++) {
                Object element = Array.get(value, i);

                SelectItem si = convertItem(element);

                if (si == null) {
                    continue;
                }

                l.add(si);
            }

            return l.toArray(new SelectItem[l.size()]);
        }

        if (value instanceof Collection) {
            Collection c = (Collection) value;
            List<SelectItem> l = new ArrayList<SelectItem>();

            for (Iterator it = c.iterator(); it.hasNext();) {
                Object element = it.next();

                SelectItem si = convertItem(element);

                if (si == null) {
                    continue;
                }

                l.add(si);
            }

            return l.toArray(new SelectItem[l.size()]);
        }

        SelectItem si = convertItem(value);
        if (si == null) {
            return null;
        }

        return new SelectItem[] { si };
    }

    protected SelectItem convertItem(Object item) {
        if (item == null) {
            return null;
        }
        if (item instanceof SelectItem) {
            return (SelectItem) item;
        }

        if (item instanceof IAdaptable) {
            IAdaptable adaptable = (IAdaptable) item;

            SelectItem si = adaptable.getAdapter(SelectItem.class, null);
            if (si != null) {
                return si;
            }
        }

        String st = String.valueOf(item);

        return new SelectItem(item, st);
    }

    public SelectItem[] computeSelectItems(KeyLabelComponent keyLabelComponent,
            IFilterProperties filterProperties,
            Map<SelectItem, String> styleClasses) {
        Object value = keyLabelComponent.getValue();

        SelectItem sis[] = convertValue(keyLabelComponent, value,
                filterProperties);

        if (keyLabelComponent.isShowParents() == false && sis.length > 1) {
            int len = sis.length;

            if (styleClasses != null) {
                String sc = computeClassName(keyLabelComponent, sis[len - 1],
                        len - 1, true, true);

                styleClasses.put(sis[len - 1], sc);
            }

            sis = new SelectItem[] { sis[len - 1] };

        } else if (styleClasses != null) {
            for (int i = 0; i < sis.length; i++) {
                String sc = computeClassName(keyLabelComponent, sis[i], i,
                        i == 0, i == sis.length - 1);

                styleClasses.put(sis[i], sc);
            }
        }

        return sis;
    }

    public void addRequiredJavaScriptClassNames(IHtmlWriter htmlWriter,
            IJavaScriptRenderContext javaScriptRenderContext) {
        super.addRequiredJavaScriptClassNames(htmlWriter,
                javaScriptRenderContext);

        if (htmlWriter.getComponentRenderContext().containsAttribute(
                FILTRED_CONTENT_PROPERTY)) {
        	
        	KeyLabelBehaviorListener.addAjaxBehavior((KeyLabelComponent) htmlWriter.getHtmlComponentRenderContext().getComponent(), htmlWriter.getHtmlComponentRenderContext().getFacesContext());

            javaScriptRenderContext.appendRequiredClass(
                    JavaScriptClasses.FILTRED_COMPONENT, "filter");
        }
    }
}