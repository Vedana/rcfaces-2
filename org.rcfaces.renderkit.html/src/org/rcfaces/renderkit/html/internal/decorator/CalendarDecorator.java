/*
 * $Id: CalendarDecorator.java,v 1.2 2013/07/03 12:25:09 jbmeslin Exp $
 * 
 */
package org.rcfaces.renderkit.html.internal.decorator;

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.component.UISelectItem;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.rcfaces.core.component.DateItemComponent;
import org.rcfaces.core.component.capability.IFilterCapability;
import org.rcfaces.core.internal.renderkit.IComponentData;
import org.rcfaces.core.internal.renderkit.IComponentRenderContext;
import org.rcfaces.core.internal.renderkit.IRequestContext;
import org.rcfaces.core.internal.renderkit.WriterException;
import org.rcfaces.core.internal.tools.CalendarTools;
import org.rcfaces.core.item.BasicSelectItem;
import org.rcfaces.core.item.DateItem;
import org.rcfaces.core.item.IClientDataItem;
import org.rcfaces.core.item.IStyleClassItem;
import org.rcfaces.core.lang.Period;
import org.rcfaces.core.model.IFilterProperties;
import org.rcfaces.renderkit.html.internal.AbstractCalendarRenderer;
import org.rcfaces.renderkit.html.internal.HtmlTools;
import org.rcfaces.renderkit.html.internal.IObjectLiteralWriter;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:09 $
 */
public class CalendarDecorator extends AbstractSelectItemsDecorator {
    

    private final int maxResultNumber;

    private final boolean onlyDay;

    private Calendar calendar;

    private int count = 0;

    public CalendarDecorator(UIComponent component, boolean onlyDay,
            IFilterProperties filterProperties, int maxResultNumber) {
        super(component, filterProperties);

        this.onlyDay = onlyDay;

        this.maxResultNumber = maxResultNumber;
    }

    protected SelectItemsContext createHtmlContext() {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.rcfaces.core.internal.renderkit.html.SelectItemsRenderer#createContext(org.rcfaces.core.internal.renderkit.html.IJavaScriptWriter)
     */
    protected SelectItemsContext createJavaScriptContext() {
        IComponentRenderContext componentRenderContext = javaScriptWriter
                .getHtmlComponentRenderContext();

        return new SelectItemsJsContext(this, componentRenderContext, null,
                null);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.rcfaces.core.internal.renderkit.html.SelectItemsRenderer#encodeNodeBegin(org.rcfaces.core.internal.renderkit.html.SelectItemsRenderer.Context,
     *      javax.faces.component.UIComponent, javax.faces.model.SelectItem,
     *      boolean)
     */
    public int encodeNodeBegin(UIComponent component, SelectItem selectItem,
            boolean hasChildren, boolean isVisible) throws WriterException {

        count++;

        if (maxResultNumber > 0 && count > maxResultNumber) {
            return SKIP_NODE;
        }

        Object selectItemValue = selectItem.getValue();
        if (selectItemValue == null) {
            return EVAL_NODE;
        }
        Object sourceValue = selectItemValue;

        if (calendar == null) {
            calendar = CalendarTools.getCalendar(getComponentRenderContext()
                    .getRenderContext().getProcessContext(), getComponent(),
                    false);
        }

        if ((selectItemValue instanceof Date) == false
                && (selectItemValue instanceof Date[]) == false
                && (selectItemValue instanceof Period) == false
                && (selectItemValue instanceof Period[]) == false) {

            FacesContext facesContext = getComponentRenderContext()
                    .getFacesContext();

            selectItemValue = AbstractCalendarRenderer.convertValueToPeriod(
                    facesContext, sourceValue);

            if (selectItemValue == null) {
                selectItemValue = AbstractCalendarRenderer
                        .convertValueToPeriodArray(facesContext, sourceValue);
            }
        }

        String svalue;
        if (selectItemValue instanceof Date) {
            svalue = AbstractCalendarRenderer.convertDate(calendar,
                    (Date) selectItemValue, onlyDay);

        } else if (selectItemValue instanceof Date[]) {
            svalue = AbstractCalendarRenderer.convertDates(calendar,
                    (Date[]) selectItemValue, onlyDay);

        } else if (selectItemValue instanceof Period) {
            svalue = AbstractCalendarRenderer.convertPeriod(calendar,
                    (Period) selectItemValue, onlyDay);

        } else if (selectItemValue instanceof Period[]) {
            svalue = AbstractCalendarRenderer.convertPeriods(calendar,
                    (Period[]) selectItemValue, onlyDay);
        } else {
            throw new FacesException("Invalid value for date '" + sourceValue
                    + "'.");
        }

        javaScriptWriter.writeMethodCall("f_appendDateItem2");

        IObjectLiteralWriter objectLiteralWriter = javaScriptWriter
                .writeObjectLiteral(false);

        objectLiteralWriter.writeSymbol("_value").writeString(svalue);

        String text = selectItem.getLabel();
        if (text != null) {
            objectLiteralWriter.writeSymbol("_label").writeString(text);
        }

        if (selectItem.isDisabled()) {
            objectLiteralWriter.writeSymbol("_disabled").writeBoolean(true);
        }

        if (selectItem instanceof IStyleClassItem) {
            IStyleClassItem dateSelectItem = (IStyleClassItem) selectItem;

            String styleClass = dateSelectItem.getStyleClass();
            if (styleClass != null) {
                objectLiteralWriter.writeSymbol("_styleClass").writeString(
                        styleClass);
            }
        }

        if (selectItem instanceof IClientDataItem) {
            IClientDataItem clientDataItem = (IClientDataItem) selectItem;

            if (clientDataItem.isClientDataEmpty() == false) {
                IObjectLiteralWriter cds = objectLiteralWriter.writeSymbol(
                        "_clientDatas").writeObjectLiteral(false);

                Map map = clientDataItem.getClientDataMap();

                for (Iterator it = map.entrySet().iterator(); it.hasNext();) {
                    Map.Entry entry = (Map.Entry) it.next();

                    String key = (String) entry.getKey();
                    Object data = entry.getValue();
                    if (data != null) {
                        data = String.valueOf(data);
                    }

                    cds.writeProperty(key).writeString((String) data);
                }

                cds.end();
            }
        }

        objectLiteralWriter.end().writeln(");");

        return EVAL_NODE;
    }

    /*
     * protected void encodeComponentsEnd() throws WriterException { if
     * (javaScriptWriter == null) { return; }
     * 
     * int rowCount = getSelectItemCount(); if (rowCount < 1 && service ==
     * false) { // Nous sommes dans un rendu HTML ! return; }
     * 
     * javaScriptWriter.writeCall(null, "f_setRowCount").write(rowCount)
     * .writeln(");");
     * 
     * super.encodeComponentsEnd(); }
     */
    public void encodeNodeEnd(UIComponent component, SelectItem selectItem,
            boolean hasChildren, boolean isVisible) {
    }

    public void decode(IRequestContext context, UIComponent component,
            IComponentData componentData) {
        super.decode(context, component, componentData);

        if (component instanceof IFilterCapability) {
            IFilterCapability filterCapability = (IFilterCapability) component;

            String filterExpression = componentData
                    .getStringProperty("filterExpression");
            if (filterExpression != null) {
                if (filterExpression.length() < 1) {
                    filterExpression = null;
                }

                filterCapability.setFilterProperties(HtmlTools
                        .decodeFilterExpression(context.getProcessContext(),
                                component, filterExpression));
            }
        }
    }

    protected int getMaxResultNumber() {
        return maxResultNumber;
    }

    protected SelectItem createSelectItem(UISelectItem component) {
        if (component instanceof DateItemComponent) {
            return new DateItem(component);
        }

        return new BasicSelectItem(component);
    }
}
