/*
 * $Id: CalendarRenderer.java,v 1.3 2013/11/13 12:53:30 jbmeslin Exp $
 */
package org.rcfaces.renderkit.html.internal.renderer;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.rcfaces.core.component.CalendarComponent;
import org.rcfaces.core.component.capability.IBorderCapability;
import org.rcfaces.core.component.capability.ICalendarModeCapability;
import org.rcfaces.core.internal.lang.StringAppender;
import org.rcfaces.core.internal.renderkit.IComponentData;
import org.rcfaces.core.internal.renderkit.IComponentRenderContext;
import org.rcfaces.core.internal.renderkit.IRequestContext;
import org.rcfaces.core.internal.renderkit.WriterException;
import org.rcfaces.core.internal.tools.CalendarTools;
import org.rcfaces.core.lang.Period;
import org.rcfaces.renderkit.html.internal.AbstractCalendarRenderer;
import org.rcfaces.renderkit.html.internal.ICssWriter;
import org.rcfaces.renderkit.html.internal.IHtmlWriter;
import org.rcfaces.renderkit.html.internal.JavaScriptClasses;
import org.rcfaces.renderkit.html.internal.decorator.IComponentDecorator;
import org.rcfaces.renderkit.html.internal.ns.XhtmlNSAttributes;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:30 $
 */
@XhtmlNSAttributes({ "mode", "value", "autoSelection" })
public class CalendarRenderer extends AbstractCalendarRenderer {
   
    protected String getJavaScriptClassName() {
        return JavaScriptClasses.CALENDAR;
    }

    protected boolean hasComponenDecoratorSupport() {
        return true;
    }

    protected void encodeBeforeDecorator(IHtmlWriter htmlWriter,
            IComponentDecorator componentDecorator) throws WriterException {

        htmlWriter.startElement(IHtmlWriter.DIV);

        writeHtmlAttributes(htmlWriter);
        writeJavaScriptAttributes(htmlWriter);
        writeCssAttributes(htmlWriter);

        Calendar componentCalendar = CalendarTools.getCalendar(htmlWriter);
        writeCalendarAttributes(htmlWriter, componentCalendar);

        htmlWriter.getJavaScriptEnableMode().enableOnInit();

        super.encodeBeforeDecorator(htmlWriter, componentDecorator);
    }

    protected void encodeAfterDecorator(IHtmlWriter htmlWriter,
            IComponentDecorator componentDecorator) throws WriterException {
        super.encodeAfterDecorator(htmlWriter, componentDecorator);

        htmlWriter.endElement(IHtmlWriter.DIV);
    }

    protected void writeCustomCss(IHtmlWriter writer, ICssWriter cssWriter) {
        super.writeCustomCss(writer, cssWriter);

        IComponentRenderContext componentRenderContext = writer
                .getComponentRenderContext();

        UIComponent component = componentRenderContext.getComponent();
        if (component instanceof IBorderCapability) {
            IBorderCapability borderCapability = (IBorderCapability) component;

            if (borderCapability.isBorder() == false) {
                cssWriter.writeBorderStyle(ICssWriter.NONE);
            }
        }
    }

    protected void writeCalendarAttributes(IHtmlWriter htmlWriter,
            Calendar componentCalendar) throws WriterException {
        super.writeCalendarAttributes(htmlWriter, componentCalendar);

        writeCalendarMode(htmlWriter, componentCalendar);

    }

    protected void writeCalendarMode(IHtmlWriter htmlWriter,
            Calendar componentCalendar) throws WriterException {
        IComponentRenderContext componentRenderContext = htmlWriter
                .getComponentRenderContext();

        FacesContext facesContext = componentRenderContext.getFacesContext();

        CalendarComponent calendarComponent = (CalendarComponent) componentRenderContext
                .getComponent();

        int mode = calendarComponent.getMode(facesContext);
        if (mode != 0) {
            htmlWriter.writeAttributeNS("mode", mode);
        }

        Object value = calendarComponent.getValue();
        String s_value = null;
        Object sourceValue = value;

        StringAppender sb = new StringAppender(32);
        switch (mode) {
        case ICalendarModeCapability.DATE_CALENDAR_MODE:
            if ((value instanceof Date) == false
                    && (value instanceof Date[]) == false) {
                if (calendarComponent.isMultipleSelect(facesContext)) {
                    value = convertValueToDateArray(facesContext, value);

                } else {
                    value = convertValueToDate(facesContext, value);
                }
            }

            if (value instanceof Date) {
                Date d = (Date) value;

                sb.setLength(0);
                appendDate(componentCalendar, d, sb, true);

                s_value = sb.toString();

            } else if (value instanceof Date[]) {
                Date[] d = (Date[]) value;

                sb.setLength(0);
                appendDates(componentCalendar, d, sb, true);

                s_value = sb.toString();

            } else if (sourceValue != null) {
                throw new FacesException(
                        "Value for calendarMode "
                                + mode
                                + " must be a Date object or an array of Date object. ("
                                + sourceValue + ")");
            }

            break;

        case ICalendarModeCapability.PERIOD_CALENDAR_MODE:
            if ((value instanceof Period) == false
                    && (value instanceof Period[]) == false) {
                if (calendarComponent.isMultipleSelect(facesContext)) {
                    value = convertValueToPeriodArray(facesContext, value);

                } else {
                    value = convertValueToPeriod(facesContext, value);
                }
            }

            if (value instanceof Period) {
                s_value = convertPeriod(componentCalendar, (Period) value, true);

            } else if (value instanceof Period[]) {
                s_value = convertPeriods(componentCalendar, (Period[]) value,
                        true);

            } else if (sourceValue != null) {
                throw new FacesException("Value for calendarMode " + mode
                        + " must be a Period object. (" + sourceValue + ")");
            }

            break;

        default:
            throw new FacesException("Unknown calendarMode ! (" + mode + ")");
        }

        if (s_value != null) {
            htmlWriter.writeAttributeNS("value", s_value);
        }

        if (calendarComponent.isAutoSelection(facesContext)) {
            htmlWriter.writeAttributeNS("autoSelection", true);
        }
    }

    @SuppressWarnings("unchecked")
    protected void decode(IRequestContext context, UIComponent component,
            IComponentData componentData) {
        super.decode(context, component, componentData);

        CalendarComponent calendarComponent = (CalendarComponent) component;

        Object dateValue = componentData.getProperty("value");

        if (dateValue instanceof Collection) {
            Collection<Date> c = (Collection<Date>) dateValue;

            switch (calendarComponent.getMode(context.getFacesContext())) {
            case ICalendarModeCapability.DATE_CALENDAR_MODE:
                dateValue = c.toArray(new Date[c.size()]);
                break;

            case ICalendarModeCapability.PERIOD_CALENDAR_MODE:
                dateValue = c.toArray(new Period[c.size()]);
                break;
            }
        }

        Object date = null;
        if (dateValue != null
                && calendarComponent.isValueLocked(context.getFacesContext()) == false) {
            date = dateValue;
        }

        calendarComponent.setSubmittedExternalValue(date);
    }
}
