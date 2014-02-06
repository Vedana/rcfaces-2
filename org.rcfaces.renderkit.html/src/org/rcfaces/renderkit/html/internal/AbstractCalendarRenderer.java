/*
 * $Id: AbstractCalendarRenderer.java,v 1.2 2013/01/11 15:45:00 jbmeslin Exp $
 * 
 */
package org.rcfaces.renderkit.html.internal;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.rcfaces.core.component.AbstractCalendarComponent;
import org.rcfaces.core.component.capability.ICalendarLayoutCapability;
import org.rcfaces.core.component.capability.IClientDatesStrategyCapability;
import org.rcfaces.core.component.capability.IMultipleSelectCapability;
import org.rcfaces.core.event.PropertyChangeEvent;
import org.rcfaces.core.internal.RcfacesContext;
import org.rcfaces.core.internal.component.Properties;
import org.rcfaces.core.internal.lang.StringAppender;
import org.rcfaces.core.internal.renderkit.IComponentData;
import org.rcfaces.core.internal.renderkit.IComponentRenderContext;
import org.rcfaces.core.internal.renderkit.IProcessContext;
import org.rcfaces.core.internal.renderkit.IRequestContext;
import org.rcfaces.core.internal.renderkit.WriterException;
import org.rcfaces.core.lang.IAdaptable;
import org.rcfaces.core.lang.Period;
import org.rcfaces.core.model.IFilterProperties;
import org.rcfaces.renderkit.html.internal.decorator.CalendarDecorator;
import org.rcfaces.renderkit.html.internal.decorator.IComponentDecorator;
import org.rcfaces.renderkit.html.internal.ns.XhtmlNSAttributes;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/01/11 15:45:00 $
 */
@XhtmlNSAttributes({ "maxDate", "minDate", "cursorDate", "twoDigitYearStart",
        "disabledWeekDays", "clientDatesStrategy", "layout", "multiple" })
public abstract class AbstractCalendarRenderer extends AbstractCssRenderer {

    protected void decode(IRequestContext context, UIComponent component,
            IComponentData componentData) {
        super.decode(context, component, componentData);

        AbstractCalendarComponent calendarComponent = (AbstractCalendarComponent) component;

        Date cursorDate = (Date) componentData.getProperty("cursor");
        if (cursorDate != null) {
            Date old = calendarComponent.getCursorDate(context
                    .getFacesContext());

            if (cursorDate.equals(old) == false) {
                calendarComponent.setCursorDate(cursorDate);

                component.queueEvent(new PropertyChangeEvent(component,
                        Properties.CURSOR_DATE, old, cursorDate));
            }
        }
    }

    protected void writeCalendarAttributes(IHtmlWriter htmlWriter,
            Calendar componentCalendar) throws WriterException {

        IComponentRenderContext componentRenderContext = htmlWriter
                .getComponentRenderContext();

        FacesContext facesContext = componentRenderContext.getFacesContext();

        IProcessContext processContext = componentRenderContext
                .getRenderContext().getProcessContext();

        AbstractCalendarComponent calendarComponent = (AbstractCalendarComponent) componentRenderContext
                .getComponent();

        Date maxDate = calendarComponent.getMaxDate(facesContext);
        Date minDate = calendarComponent.getMinDate(facesContext);
        Date twoDigitYearStart = calendarComponent
                .getTwoDigitYearStart(facesContext);
        Date cursorDate = calendarComponent.getCursorDate(facesContext);


        if (maxDate != null || minDate != null || twoDigitYearStart != null
                || cursorDate != null) {
            StringAppender sb = new StringAppender(12);

            if (maxDate != null) {
                sb.setLength(0);
                HtmlTools.formatDate(maxDate, sb, processContext,
                        calendarComponent, true);
                htmlWriter.writeAttributeNS("maxDate", sb.toString());
            }

            if (minDate != null) {
                sb.setLength(0);
                HtmlTools.formatDate(minDate, sb, processContext,
                        calendarComponent, true);
                htmlWriter.writeAttributeNS("minDate", sb.toString());
            }

            if (cursorDate != null) {
                sb.setLength(0);
                HtmlTools.formatDate(cursorDate, sb, processContext,
                        calendarComponent, true);
                htmlWriter.writeAttributeNS("cursorDate", sb.toString());
            }

            if (twoDigitYearStart != null) {
                sb.setLength(0);
                HtmlTools.formatDate(twoDigitYearStart, sb, processContext,
                        calendarComponent, true);
                htmlWriter.writeAttributeNS("twoDigitYearStart", sb.toString());
            }
        }

        int wdays = calendarComponent.getDisabledWeekDays(facesContext);
        if (wdays > 0) {
        	htmlWriter.writeAttributeNS("disabledWeekDays", wdays);
        }

        int clientDatesStrategy = calendarComponent
                .getClientDatesStrategy(facesContext);
        if (clientDatesStrategy == IClientDatesStrategyCapability.MONTH_DATES_STRATEGY
                || clientDatesStrategy == IClientDatesStrategyCapability.YEAR_DATES_STRATEGY) {

        	htmlWriter.writeAttributeNS("clientDatesStrategy",
                    clientDatesStrategy);
        }

        if (calendarComponent instanceof ICalendarLayoutCapability) {
            ICalendarLayoutCapability calendarLayout = (ICalendarLayoutCapability) calendarComponent;

            int layout = calendarLayout.getCalendarLayout();
            if (layout != ICalendarLayoutCapability.DEFAULT_LAYOUT) {
            	htmlWriter.writeAttributeNS("layout", layout);
            }
        }

        if (calendarComponent instanceof IMultipleSelectCapability) {
            if (((IMultipleSelectCapability) calendarComponent)
                    .isMultipleSelect()) {
            	htmlWriter.writeAttributeNS("multiple", true);
            }
        }
    }

    public static String convertPeriod(Calendar calendar, Period period,
            boolean onlyDate) {
        StringAppender sb = new StringAppender(2 * 10);

        appendPeriod(calendar, period, sb, onlyDate);

        return sb.toString();
    }

    public static String convertPeriods(Calendar calendar, Period periods[],
            boolean onlyDate) {
        StringAppender sb = new StringAppender(periods.length * 20);

        appendPeriods(calendar, periods, sb, onlyDate);

        return sb.toString();
    }

    public static String convertDate(Calendar calendar, Date date,
            boolean onlyDate) {
        StringAppender sb = new StringAppender(10);

        HtmlTools.formatDate(date, sb, calendar, onlyDate);

        return sb.toString();
    }

    public static String convertDates(Calendar calendar, Date dates[],
            boolean onlyDate) {
        StringAppender sb = new StringAppender(10 * dates.length);

        appendDates(calendar, dates, sb, onlyDate);

        return sb.toString();
    }

    public static void appendDate(Calendar calendar, Date date,
            StringAppender sb, boolean onlyDate) {

        HtmlTools.formatDate(date, sb, calendar, onlyDate);
    }

    public static void appendDates(Calendar calendar, Date dates[],
            StringAppender sb, boolean onlyDate) {

        for (int i = 0; i < dates.length; i++) {
            Date d = dates[i];
            if (d == null) {
                continue;
            }

            if (sb.length() > 0) {
                sb.append(',');
            }

            HtmlTools.formatDate(d, sb, calendar, onlyDate);
        }
    }

    public static void appendPeriod(Calendar calendar, Period period,
            StringAppender sb, boolean onlyDate) {

        Date d = period.getStart();
        if (d != null) {
            HtmlTools.formatDate(d, sb, calendar, onlyDate);

            d = period.getEnd();
            if (d != null) {
                sb.append(':');

                HtmlTools.formatDate(d, sb, calendar, onlyDate);
            }
        }

    }

    public static void appendPeriods(Calendar calendar, Period periods[],
            StringAppender sb, boolean onlyDate) {

        for (int i = 0; i < periods.length; i++) {
            Period d = periods[i];
            if (d == null) {
                continue;
            }

            if (sb.length() > 0) {
                sb.append(',');
            }

            HtmlTools.formatDate(d.getStart(), sb, calendar, onlyDate);
            if (d.getEnd() == null || d.getStart().equals(d.getEnd())) {
                continue;
            }

            sb.append(':');

            HtmlTools.formatDate(d.getEnd(), sb, calendar, onlyDate);
        }
    }

    protected IComponentDecorator createComponentDecorator(
            FacesContext facesContext, UIComponent component) {

        boolean dayOnly = true;
        int maxResultNumber = -1;
        IFilterProperties filterProperties = null;

        return new CalendarDecorator(component, dayOnly, filterProperties,
                maxResultNumber);
    }

    public void addRequiredJavaScriptClassNames(IHtmlWriter writer,
            IJavaScriptRenderContext javaScriptRenderContext) {
        super.addRequiredJavaScriptClassNames(writer, javaScriptRenderContext);

        FacesContext facesContext = writer.getComponentRenderContext()
                .getFacesContext();

        AbstractCalendarComponent calendarComponent = (AbstractCalendarComponent) writer
                .getComponentRenderContext().getComponent();

        int clientDatesStrategy = calendarComponent
                .getClientDatesStrategy(facesContext);
        if (clientDatesStrategy == IClientDatesStrategyCapability.MONTH_DATES_STRATEGY
                || clientDatesStrategy == IClientDatesStrategyCapability.YEAR_DATES_STRATEGY) {

            javaScriptRenderContext.appendRequiredClass(
                    JavaScriptClasses.CALENDAR_OBJECT, "ajax");
        }
    }

    public static Date convertValueToDate(FacesContext facesContext,
            Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof Date) {
            return (Date) value;
        }

        if (value instanceof Date[]) {
            Date d[] = (Date[]) value;

            if (d.length == 0) {
                return null;
            }

            return d[0];
        }

        if (value instanceof Date[][]) {
            Date d[][] = (Date[][]) value;

            if (d.length == 0) {
                return null;
            }

            if (d[0] == null || d[0].length == 0) {
                return null;
            }

            return d[0][0];
        }

        if (value instanceof IAdaptable) {
            return ((IAdaptable) value).getAdapter(Date.class, null);
        }

        return RcfacesContext.getInstance(facesContext).getAdapterManager()
                .getAdapter(value, Date.class, null);
    }

    public static Date[] convertValueToDateArray(FacesContext facesContext,
            Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof Date[]) {
            return (Date[]) value;
        }

        if (value.getClass().isArray()) {
            Date ds[] = new Date[Array.getLength(value)];

            for (int i = 0; i < ds.length; i++) {
                Date next = convertValueToDate(facesContext,
                        Array.get(value, i));

                if (next == null) {
                    return null;
                }

                ds[i] = next;
            }

            return ds;
        }

        if (value instanceof Collection) {
            Collection c = (Collection) value;

            if (c.isEmpty()) {
                return null;
            }

            List<Date> ds = new ArrayList<Date>(c.size());

            for (Iterator it = c.iterator(); it.hasNext();) {
                Date next = convertValueToDate(facesContext, it.next());

                if (next == null) {
                    return null;
                }

                ds.add(next);
            }

            return ds.toArray(new Date[ds.size()]);
        }

        return RcfacesContext.getCurrentInstance().getAdapterManager()
                .getAdapter(value, Date[].class, null);
    }

    public static Period convertValueToPeriod(FacesContext facesContext,
            Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof Period) {
            return (Period) value;
        }

        if (value instanceof Period[]) {
            Period d[] = (Period[]) value;

            if (d.length == 0) {
                return null;
            }

            return d[0];
        }

        if (value instanceof Period[][]) {
            Period d[][] = (Period[][]) value;

            if (d.length == 0) {
                return null;
            }

            if (d[0] == null || d[0].length == 0) {
                return null;
            }

            return d[0][0];
        }

        if (value instanceof IAdaptable) {
            return ((IAdaptable) value).getAdapter(Period.class, null);
        }

        return RcfacesContext.getCurrentInstance().getAdapterManager()
                .getAdapter(value, Period.class, null);
    }

    public static Period[] convertValueToPeriodArray(FacesContext facesContext,
            Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof Period[]) {
            return (Period[]) value;
        }

        if (value.getClass().isArray()) {
            Period ds[] = new Period[Array.getLength(value)];

            for (int i = 0; i < ds.length; i++) {
                Period next = convertValueToPeriod(facesContext,
                        Array.get(value, i));

                if (next == null) {
                    return null;
                }

                ds[i] = next;
            }

            return ds;
        }

        if (value instanceof Collection) {
            Collection c = (Collection) value;

            if (c.isEmpty()) {
                return null;
            }

            List<Period> ds = new ArrayList<Period>(c.size());

            for (Iterator it = c.iterator(); it.hasNext();) {
                Period next = convertValueToPeriod(facesContext, it.next());

                if (next == null) {
                    return null;
                }

                ds.add(next);
            }

            return ds.toArray(new Period[ds.size()]);
        }

        return RcfacesContext.getCurrentInstance().getAdapterManager()
                .getAdapter(value, Period[].class, null);
    }

   
}
