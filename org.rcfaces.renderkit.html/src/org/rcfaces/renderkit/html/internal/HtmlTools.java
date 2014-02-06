/*
 * $Id: HtmlTools.java,v 1.2 2013/01/11 15:45:00 jbmeslin Exp $
 * 
 */
package org.rcfaces.renderkit.html.internal;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.faces.FacesException;
import javax.faces.FactoryFinder;
import javax.faces.component.NamingContainer;
import javax.faces.component.UIComponent;
import javax.faces.component.UIData;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.render.RenderKit;
import javax.faces.render.RenderKitFactory;
import javax.faces.render.Renderer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.component.capability.IAccessKeyCapability;
import org.rcfaces.core.internal.RcfacesContext;
import org.rcfaces.core.internal.codec.URLFormCodec;
import org.rcfaces.core.internal.component.UIData2;
import org.rcfaces.core.internal.lang.StringAppender;
import org.rcfaces.core.internal.renderkit.IComponentRenderContext;
import org.rcfaces.core.internal.renderkit.IDecoderContext;
import org.rcfaces.core.internal.renderkit.IProcessContext;
import org.rcfaces.core.internal.renderkit.WriterException;
import org.rcfaces.core.internal.tools.CalendarTools;
import org.rcfaces.core.internal.tools.ComponentTools;
import org.rcfaces.core.internal.tools.FilterExpressionTools;
import org.rcfaces.core.item.IAccessKeyItem;
import org.rcfaces.core.lang.Period;
import org.rcfaces.core.lang.Time;
import org.rcfaces.core.model.IFilterProperties;
import org.w3c.dom.Document;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/01/11 15:45:00 $
 */
public class HtmlTools {

    private static final Log LOG = LogFactory.getLog(HtmlTools.class);

    private static final String ACCESSKEY_CLASSNAME = "f_accessKey";

    private static final String DEFAULT_URL_DECODER_CHARSET = "UTF8";

    public static final String LIST_SEPARATORS = "\u0001";

    public static final String ALL_VALUE = "\u0007all";

    private static final char LF_CHARACTER = '\n';

    private static final char NO_ACCESS_KEY = (char) -1;

    private static final char NULL_TYPE = 'L';

    private static final char TRUE_TYPE = 'T';

    private static final char FALSE_TYPE = 'F';

    private static final char STRING_TYPE = 'S';

    private static final char DATE_TYPE = 'D';

    private static final char TIME_TYPE = 'M';

    private static final char PERIOD_TYPE = 'P';

    private static final char DOCUMENT_TYPE = 'X';

    private static final char COMPONENT_TYPE = 'C';

    private static final char ARRAY_TYPE = '[';

    private static final char ZERO_TYPE = '0';

    private static final Number NUMBER_0 = new Double(0);

    private static final int RADIX = 32;

    private static final String NAMING_SEPARATOR_STRING = ""
            + NamingContainer.SEPARATOR_CHAR;

    private static final char PERIOD_SEPARATOR = ':';

    public static Map decodeParametersToMap(IProcessContext processContext,
            UIComponent component, String values, String separators,
            Object noValue) {
        return decodeParametersToMap(new BasicDecoderContext(processContext,
                component), values, separators, noValue);
    }

    public static Map<String, Object> decodeParametersToMap(
            IProcessContext processContext, UIComponent component,
            Renderer renderer, String values, String separators, Object noValue) {
        return decodeParametersToMap(new BasicDecoderContext(processContext,
                component, renderer), values, separators, noValue);
    }

    public static Map<String, Object> decodeParametersToMap(
            IDecoderContext decoderContext, String values, String separators,
            Object noValue) {
        if (values == null || values.length() < 1) {
            return Collections.emptyMap();
        }

        char cs[] = values.toCharArray();

        Position position = new Position(values);

        Map<String, Object> properties = new HashMap<String, Object>(
                (cs.length / 16) + 1);
        for (; position.start < cs.length;) {
            int nameStart = position.start;

            for (; position.start < cs.length; position.start++) {
                char c = cs[position.start];

                if (c != '=') {
                    continue;
                }

                break;
            }
            if (position.start == cs.length) {
                throw createFormatException("EOF", position.start, values, null);
            }

            String name = values.substring(nameStart, position.start);

            position.start++;
            if (position.start == cs.length) {
                properties.put(name, noValue);
                // System.out.println(">>>decode '"+values+"' => "+properties);
                return properties;
            }

            Object vs = decodeObject(cs, position, decoderContext, separators,
                    name);

            if (LOG.isDebugEnabled()) {
                LOG.debug("Decode '" + name + "'=>" + vs);
            }

            properties.put(name, vs);
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("Decode '" + values + "'=>" + properties);
        }

        return properties;
    }

    public static Object decodeObject(String value,
            IDecoderContext decoderContext, String attributeName) {

        return decodeObject(value.toCharArray(), new Position(value),
                decoderContext, null, attributeName);
    }

    private static Object decodeObject(char cs[], Position position,
            IDecoderContext decoderContext, String separators,
            String attributeName) {

        if (position.start >= cs.length) {
            return "";
        }
        if (separators != null) {
            if (separators.indexOf(cs[position.start]) >= 0) {
                position.start++;
                return "";
            }
        }

        char type = cs[position.start++];

        if (type == ARRAY_TYPE) {
            if (cs[position.start] == '0') {
                position.start += 2; // + le ']'
                return Collections.EMPTY_LIST;
            }

            int length = 0;

            for (; cs[position.start] != ':'; position.start++) {
                length = length * 10 + (cs[position.start] - '0');
            }
            position.start++; // le ':'

            List<Object> l = new ArrayList<Object>(length);

            for (; position.start < cs.length;) {

                Object v = decodeObject(cs, position, decoderContext, ",]",
                        attributeName);

                l.add(v);

                if (position.start <= cs.length & cs[position.start - 1] == ']') {
                    break;
                }
            }

            position.start++; // Fermeture mangée , reste le separateur

            return l;
        }

        int start = position.start;
        int end = start;

        if (separators == null) {
            end = cs.length;

        } else {
            for (; end < cs.length; end++) {
                if (separators.indexOf(cs[end]) >= 0) {
                    break;
                }
            }
        }

        position.start = end + 1;

        switch (type) {
        case STRING_TYPE:
            if (start == end) {
                return "";
            }
            return URLFormCodec.decodeURL(cs, start, end);

        case '0':
        case '1':
        case '2':
        case '3':
        case '4':
        case '5':
        case '6':
        case '7':
        case '8':
        case '9':
            if (start == end) {
                type -= '0';
                if (type == 0) {
                    return NUMBER_0;
                }

                return new Double(type);
            }

            // J'ai pas oublié le break !

        case '-':
            return Double.valueOf(URLFormCodec.decodeURL(cs, start - 1, end));

        case TRUE_TYPE:
            return Boolean.TRUE;

        case FALSE_TYPE:
            return Boolean.FALSE;

        case NULL_TYPE:
            return null;

        case DATE_TYPE:
            String date = URLFormCodec.decodeURL(cs, start, end);

            return parseDate(date, decoderContext, attributeName);

        case TIME_TYPE:
            String time = URLFormCodec.decodeURL(cs, start, end);

            return parseTime(time);

        case PERIOD_TYPE:
            String period = URLFormCodec.decodeURL(cs, start, end);

            Date startDate = null;
            Date endDate = null;

            int idx = period.indexOf(PERIOD_SEPARATOR);
            if (idx < 0) {
                startDate = parseDate(period, decoderContext, attributeName);
                endDate = startDate;

            } else {
                startDate = parseDate(period.substring(0, idx), decoderContext,
                        attributeName);
                endDate = parseDate(period.substring(idx + 1), decoderContext,
                        attributeName);
            }

            return new Period(startDate, endDate);
        }

        FacesContext facesContext = null;
        if (decoderContext != null) {
            facesContext = decoderContext.getProcessContext().getFacesContext();
        }

        if (facesContext == null) {
            facesContext = FacesContext.getCurrentInstance();
        }

        switch (type) {

        case DOCUMENT_TYPE:
            String xml = URLFormCodec.decodeURL(cs, start, end);

            return parseDocument(facesContext, xml);

        case COMPONENT_TYPE:
            UIViewRoot viewRoot = facesContext.getViewRoot();

            String clientId = URLFormCodec.decodeURL(cs, start, end);

            UIComponent component = viewRoot.findComponent(clientId);

            if (component != null) {
                return component;
            }

            return clientId;
        }

        throw createFormatException("Unknown serialized type '" + type + "'.",
                position.start, position.values, null);
    }

    private static FacesException createFormatException(String message, int i,
            String datas, Throwable th) {
        return new FacesException("Bad format of rcfaces serialized datas ! ("
                + message + ": pos=" + i + " data='" + datas + "')", th);
    }

    public static String encodeParametersFromMap(Map map, char sep,
            IProcessContext processContext, UIComponent component) {
        if (map.isEmpty()) {
            return "";
        }

        FacesContext facesContext = null;
        if (processContext != null) {
            facesContext = processContext.getFacesContext();
        }
        if (facesContext == null) {
            facesContext = FacesContext.getCurrentInstance();
        }

        StringAppender sb = new StringAppender(map.size() * 24);

        for (Iterator it = map.entrySet().iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry) it.next();

            String key = (String) entry.getKey();
            Object value = entry.getValue();

            if (sb.length() > 0) {
                sb.append(sep);
            }

            URLFormCodec.encode(sb, key);
            sb.append('=');

            formatField(facesContext, value, sb, processContext, component);
        }

        return sb.toString();
    }

    public static void appendData(StringAppender datas, String key, String value) {
        URLFormCodec.encode(datas, key);
        datas.append('=');
        URLFormCodec.encode(datas, value);
    }

    public static IFilterProperties decodeFilterExpression(
            IProcessContext processContext, UIComponent component,
            String filterExpression) {

        Map filter = HtmlTools.decodeParametersToMap(new BasicDecoderContext(
                processContext, component), filterExpression, "&", "");

        if (LOG.isDebugEnabled()) {
            LOG.debug("Decode filter expression to " + filter);
        }

        return FilterExpressionTools.create(filter);
    }

    public static String encodeFilterExpression(IFilterProperties filterMap,
            IProcessContext processContext, UIComponent component) {

        Map map = filterMap.toMap();

        String encode = HtmlTools.encodeParametersFromMap(map, '&',
                processContext, component);

        if (LOG.isDebugEnabled()) {
            LOG.debug("Encode filter expression to " + encode);
        }

        return encode;
    }

    public static final boolean writeItemSpanAccessKey(IHtmlWriter writer,
            IAccessKeyItem accessKeyItem, String text, boolean escapeLF)
            throws WriterException {
        if (text == null || text.length() < 1) {
            return false;
        }

        String accessKey = accessKeyItem.getAccessKey();
        return writeAccessKey(writer, accessKey, text, escapeLF);
    }

    public static final boolean writeSpanAccessKey(IHtmlWriter writer,
            IAccessKeyCapability accessKeyCapability, String text,
            boolean escapeLF) throws WriterException {
        if (text == null || text.length() < 1) {
            return false;
        }

        String accessKey = accessKeyCapability.getAccessKey();
        return writeAccessKey(writer, accessKey, text, escapeLF);
    }

    public static final boolean writeAccessKey(IHtmlWriter writer,
            String accessKey, String text, boolean escapeLF)
            throws WriterException {

        boolean escapeHTML = (writer.getHtmlComponentRenderContext()
                .getHtmlRenderContext().getHtmlProcessContext()
                .isHtmlEscapingDisabled() == false);

        return writeAccessKey(writer, accessKey, text, escapeLF, escapeHTML);
    }

    public static final boolean writeAccessKey(IHtmlWriter writer,
            String accessKey, String text, boolean escapeLF, boolean escapeHTML)
            throws WriterException {

        if (text == null || text.length() < 1) {
            return false;
        }

        if (accessKey == null || accessKey.length() < 1) {
            if (escapeLF) {
                writeAndEscapeLF(writer, text, NO_ACCESS_KEY, escapeHTML);
                return false;
            }

            writer.writeText(text);

            return false;
        }

        if (escapeLF) {
            writeAndEscapeLF(writer, text, accessKey.charAt(0), escapeHTML);
            return false;
        }

        int idx = text.toLowerCase().indexOf(accessKey.toLowerCase());
        if (idx < 0) {
            writer.writeText(text);
            return false;
        }

        if (idx > 0) {
            String txt = text.substring(0, idx);
            writer.writeText(txt);
        }

        int end = idx + accessKey.length();

        writer.startElement(IHtmlWriter.U);
        writer.writeClass(ACCESSKEY_CLASSNAME);
        writer.writeText(text.substring(idx, end));
        writer.endElement(IHtmlWriter.U);

        if (end < text.length()) {
            String txt = text.substring(end);

            writer.writeText(txt);
        }

        return true;
    }

    private static void writeAndEscapeLF(IHtmlWriter writer, String text,
            char accessKey, boolean escapeHtml) throws WriterException {
        char chs[] = text.toCharArray();
        int p = 0;

        char ak = accessKey;
        if (ak != NO_ACCESS_KEY) {
            ak = Character.toLowerCase(accessKey);
        }

        for (;;) {
            int old = p;
            char c = 0;
            for (; p < chs.length; p++) {
                c = chs[p];
                if (c == LF_CHARACTER) {
                    break;
                }
                if (ak == NO_ACCESS_KEY) {
                    continue;
                }
                if (Character.toLowerCase(c) == ak) {
                    break;
                }
            }

            if (old < p) {
                String s = new String(chs, old, p - old);
                writer.writeText(s);
            }

            if (p == chs.length) {
                break;
            }

            if (c == LF_CHARACTER) {
                writer.startElement(IHtmlWriter.BR);
                writer.endElement(IHtmlWriter.BR);

            } else {
                writer.startElement(IHtmlWriter.U);
                writer.writeClass(ACCESSKEY_CLASSNAME);
                writer.write(c);
                writer.endElement(IHtmlWriter.U);
                ak = NO_ACCESS_KEY;
            }

            p++;
        }
    }

    public static IHtmlWriter writeClientData(IHtmlWriter writer, Map values)
            throws WriterException {

        StringAppender datas = new StringAppender(values.size() * 64);
        for (Iterator it = values.entrySet().iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry) it.next();

            String key = (String) entry.getKey();
            if (key == null || key.length() < 1) {
                continue;
            }

            String value = (String) entry.getValue();
            if (value == null) {
                continue;
            }

            if (datas.length() > 0) {
                datas.append(',');
            }

            HtmlTools.appendData(datas, key, value);
        }

        writer.writeAttributeNS("data", datas.toString());

        return writer;
    }

    public static String replaceSeparator(String id, String separatorChar) {
        int idx = id.indexOf(NamingContainer.SEPARATOR_CHAR);
        if (idx < 0) {
            return id;
        }

        StringAppender sa = new StringAppender(id.length()
                + (separatorChar.length() - 1) * 4);

        char chs[] = id.toCharArray();
        sa.append(chs, 0, idx);
        sa.append(separatorChar);

        for (idx++; idx < chs.length; idx++) {
            char ch = chs[idx];

            if (ch != NamingContainer.SEPARATOR_CHAR) {
                sa.append(ch);
                continue;
            }

            sa.append(separatorChar);
        }

        return sa.toString();
    }

    public static String convertToNamingSeparator(String id, String separator) {
        int idx = id.indexOf(separator);
        if (idx < 0) {
            return id;
        }

        StringAppender sa = new StringAppender(id.length()
                + (separator.length() - 1) * 4);

        sa.append(id, 0, idx);
        sa.append(NamingContainer.SEPARATOR_CHAR);
        idx += separator.length();

        for (;;) {
            int newIdx = id.indexOf(separator, idx);

            if (newIdx < 0) {
                sa.append(id, idx, id.length() - idx);
                break;
            }

            sa.append(id, idx, newIdx - idx);
            sa.append(NamingContainer.SEPARATOR_CHAR);
            idx = newIdx + separator.length();
        }

        return sa.toString();
    }

    public static final String computeComponentId(FacesContext context,
            String componentId) {

        if (Constants.CLIENT_NAMING_SEPARATOR_SUPPORT) {
            IProcessContext processContext = HtmlProcessContextImpl
                    .getHtmlProcessContext(context);

            String separator = processContext.getNamingSeparator();
            if (separator != null) {
                return convertToNamingSeparator(componentId, separator);
            }
        }

        return componentId;
    }

    public static UIComponent getForComponent(FacesContext context,
            String componentId, UIComponent component) {

        componentId = computeComponentId(context, componentId);

        return ComponentTools.getForComponent(context, componentId, component);
    }

    @SuppressWarnings("unused")
    public static String computeGroupName(IHtmlProcessContext processContext,
            UIComponent component, String groupName) {

        if (Constants.GROUP_NAME_NAMESPACE_SUPPORT == false) {
            return groupName;
        }

        if (groupName.startsWith(":")) {
            return groupName.substring(1);
        }

        UIComponent container = component;
        // Recherche un Container
        for (; container != null; container = container.getParent()) {
            if (container instanceof NamingContainer) {
                break;
            }
        }

        StringAppender prefixClientId = new StringAppender(64);

        if (container == null) {
            LOG.error("No naming container for component '" + component.getId()
                    + "'.");
        } else {
            NamingContainer namingContainer = (NamingContainer) container;

            String parentClientId = ((UIComponent) namingContainer)
                    .getClientId(processContext.getFacesContext());
            if (parentClientId != null) {
                prefixClientId.append(parentClientId);
            }
        }

        String separator = processContext.getNamingSeparator();
        if (separator != null) {
            prefixClientId.append(separator);
        } else {
            prefixClientId.append(NamingContainer.SEPARATOR_CHAR);
        }

        prefixClientId.append(groupName);

        String convertedGroupName = prefixClientId.toString();
        if (LOG.isDebugEnabled()) {
            LOG.debug("Convert groupName '" + groupName + "' to '"
                    + convertedGroupName + "'.");
        }

        return convertedGroupName;
    }

    private static void formatTime(Time time, StringAppender sa) {
        int hours = time.getHours();
        int minutes = time.getMinutes();
        int seconds = time.getSeconds();
        int millis = time.getMillis();

        if (millis < 1) {
            if (seconds < 1) {
                if (minutes < 1) {
                    sa.append('H');
                    sa.append(Integer.toString(hours, RADIX));
                    return;
                }

                sa.append('m');
                sa.append(Integer.toString(hours * 60 + minutes, RADIX));
                return;
            }

            sa.append('s');
            sa.append(Integer.toString((hours * 60 + minutes) * 60 + seconds,
                    RADIX));
            return;
        }

        sa.append('S');
        sa.append(Integer.toString(((hours * 60 + minutes) * 60 + seconds)
                * 1000 + millis, RADIX));
    }

    private static Time parseTime(String time) {

        int hours = 0;
        int minutes = 0;
        int seconds = 0;
        int millis = 0;

        char c = time.charAt(0);
        long value = Long.parseLong(time.substring(1), RADIX);

        switch (c) {
        case 'H':
            hours = (int) value;
            break;

        case 'm':
            minutes = (int) (value % 60);
            value /= 60;
            hours = (int) value;
            break;

        case 's':
            seconds = (int) (value % 60);
            value /= 60;
            minutes = (int) (value % 60);
            value /= 60;
            hours = (int) value;
            break;

        case 'S':
            millis = (int) (value % 1000);
            value /= 1000;
            seconds = (int) (value % 60);
            value /= 60;
            minutes = (int) (value % 60);
            value /= 60;
            hours = (int) value;
            break;

        default:
            throw new FacesException("Invalid time format '" + time + "'.");
        }

        return new Time(hours, minutes, seconds, millis);
    }

    public static void formatDate(Date date, StringAppender sa,
            IProcessContext processContext, UIComponent component,
            boolean onlyDate) {

        Calendar calendar = CalendarTools.getCalendar(processContext,
                component, false);

        formatDate(date, sa, calendar, onlyDate);
    }

    public static void formatDate(Date date, StringAppender sa,
            Calendar calendar, boolean onlyDate) {

        int year = 0;
        int month = 0;
        int day = 0;
        int hours = 0;
        int minutes = 0;
        int seconds = 0;
        int millis = 0;

        synchronized (calendar) {

            calendar.setTime(date);

            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            day = calendar.get(Calendar.DATE);

            if (onlyDate == false) {
                hours = calendar.get(Calendar.HOUR_OF_DAY);
                minutes = calendar.get(Calendar.MINUTE);
                seconds = calendar.get(Calendar.SECOND);
                // millis = calendar.get(Calendar.MILLISECOND);
            }
        }

        if (millis < 1) {
            if (seconds < 1) {
                if (minutes < 1) {
                    if (hours < 1) {
                        if (day < 1) {
                            if (month < 1) {
                                sa.append('Y');
                                sa.append(Integer.toString(year, RADIX));
                                return;
                            }
                            sa.append('M');
                            sa.append(Integer
                                    .toString(year * 12 + month, RADIX));
                            return;
                        }
                        sa.append('d');
                        sa.append(Integer.toString((year * 12 + month) * 31
                                + (day - 1), RADIX));
                        return;
                    }
                    sa.append('H');
                    sa.append(Integer
                            .toString(((year * 12 + month) * 31 + (day - 1))
                                    * 24 + hours, RADIX));
                    return;
                }

                sa.append('m');
                sa.append(Long.toString(
                        (((year * 12 + month) * 31 + (day - 1)) * 24 + hours)
                                * 60 + minutes, RADIX));
                return;
            }

            sa.append('s');
            sa.append(Long
                    .toString(
                            ((((year * 12 + month) * 31 + (day - 1)) * 24 + hours) * 60 + minutes)
                                    * 60 + seconds, RADIX));
            return;
        }

        sa.append('S');
        sa.append(Long
                .toString(
                        (((((year * 12 + month) * 31 + (day - 1)) * 24 + hours) * 60 + minutes) * 60 + seconds)
                                * 1000 + millis, RADIX));
    }

    public static Date parseDate(String time, IProcessContext processContext,
            UIComponent component, Renderer renderer, String attributeName) {
        return parseDate(time, new BasicDecoderContext(processContext,
                component, renderer), attributeName);
    }

    public static Date parseDate(String time, IDecoderContext decoderContext,
            String attributeName) {

        int year = 0;
        int month = 0;
        int date = 0;
        int hours = 0;
        int minutes = 0;
        int seconds = 0;
        int millis = 0;

        char c = time.charAt(0);
        long value = Long.parseLong(time.substring(1), RADIX);

        switch (c) {
        case 'Y':
            year = (int) value;
            break;

        case 'M':
            month = (int) (value % 12);
            value /= 12;
            year = (int) value;
            break;

        case 'd':
            date = (int) (value % 31);
            value /= 31;
            month = (int) (value % 12);
            value /= 12;
            year = (int) value;
            break;

        case 'H':
            hours = (int) (value % 24);
            value /= 24;
            date = (int) (value % 31);
            value /= 31;
            month = (int) (value % 12);
            value /= 12;
            year = (int) value;
            break;

        case 'm':
            minutes = (int) (value % 60);
            value /= 60;
            hours = (int) (value % 24);
            value /= 24;
            date = (int) (value % 31);
            value /= 31;
            month = (int) (value % 12);
            value /= 12;
            year = (int) value;
            break;

        case 's':
            seconds = (int) (value % 60);
            value /= 60;
            minutes = (int) (value % 60);
            value /= 60;
            hours = (int) (value % 24);
            value /= 24;
            date = (int) (value % 31);
            value /= 31;
            month = (int) (value % 12);
            value /= 12;
            year = (int) value;

        case 'S':
            millis = (int) (value % 1000);
            value /= 1000;
            seconds = (int) (value % 60);
            value /= 60;
            minutes = (int) (value % 60);
            value /= 60;
            hours = (int) (value % 24);
            value /= 24;
            date = (int) (value % 31);
            value /= 31;
            month = (int) (value % 12);
            value /= 12;
            year = (int) value;
            break;

        default:
            throw new FacesException("Invalid time format '" + time + "'.");
        }

        Calendar calendar = decodeCalendar(decoderContext, attributeName);
        synchronized (calendar) {
            calendar.set(year, month, date + 1, hours, minutes, seconds);
            calendar.set(Calendar.MILLISECOND, millis);

            return calendar.getTime();
        }
    }

    private static Calendar decodeCalendar(IDecoderContext decoderContext,
            String attributeName) {
        Renderer renderer = decoderContext.getRenderer();
        if (renderer instanceof ICalendarDecoderRenderer) {
            Calendar calendar = ((ICalendarDecoderRenderer) renderer)
                    .getCalendar(decoderContext, attributeName);

            if (calendar != null) {
                return calendar;
            }
        }

        return CalendarTools.getCalendar(decoderContext.getProcessContext(),
                decoderContext.getComponent(), false);
    }

    public static void formatField(FacesContext facesContext, Object value,
            StringAppender sb, IProcessContext processContext,
            UIComponent component) {

        if (value == null) {
            sb.append(NULL_TYPE);
            return;
        }

        if (value.equals("")) {
            // String vide !
            sb.append(STRING_TYPE);
            return;
        }

        if (value instanceof Boolean) {
            if (((Boolean) value).booleanValue()) {
                sb.append(TRUE_TYPE);
                return;
            }

            sb.append(FALSE_TYPE);
            return;
        }

        if (value instanceof Date) {
            Date date = (Date) value;

            sb.append(DATE_TYPE);

            formatDate(date, sb, processContext, component, false);
            return;
        }

        if (value instanceof Document) {
            Document document = (Document) value;

            sb.append(DOCUMENT_TYPE);
            formatDocument(facesContext, document, sb);
            return;
        }

        if (value instanceof Time) {
            Time time = (Time) value;

            sb.append(TIME_TYPE);
            formatTime(time, sb);
            return;
        }

        if (value instanceof Period) {
            Period period = (Period) value;

            sb.append(PERIOD_TYPE);

            Date start = period.getStart();
            formatDate(start, sb, processContext, component, false);

            Date end = period.getEnd();
            if (end != null && end.equals(start) == false) {
                sb.append(PERIOD_SEPARATOR);
                formatDate(end, sb, processContext, component, false);
            }

            return;
        }

        if ((value instanceof Number) && ((Number) value).doubleValue() == 0.0) {
            sb.append(ZERO_TYPE);
            return;
        }

        if (value instanceof Number) {
            // sb.append(NUMBER_TYPE);
            // Pas necessaire !

            value = ((Number) value).toString();

        } else {
            sb.append(STRING_TYPE);
        }

        URLFormCodec.encode(sb, (String) value);
    }

    private static final Document parseDocument(FacesContext facesContext,
            String xml) {
        RcfacesContext rcfacesContext = RcfacesContext
                .getInstance(facesContext);

        try {
            return rcfacesContext.getDocumentBuilderProvider().parse(
                    new StringReader(xml));
        } catch (IOException e) {
            throw createFormatException("Can not parse document", 0, xml, e);
        }
    }

    private static final void formatDocument(FacesContext facesContext,
            Document document, StringAppender sa) {
        RcfacesContext rcfacesContext = RcfacesContext
                .getInstance(facesContext);

        try {
            rcfacesContext.getDocumentBuilderProvider().serialize(
                    sa.createWriter(), document);

        } catch (IOException e) {
            throw new FacesException("Can not format xml document !", e);
        }
    }

    public static void writeObjectLiteralMap(
            IJavaScriptWriter javaScriptWriter, Map map,
            boolean writeNullIfEmpty) throws WriterException {

        IObjectLiteralWriter clientObjectLiteralWriter = javaScriptWriter
                .writeObjectLiteral(writeNullIfEmpty);

        for (Iterator it = map.entrySet().iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry) it.next();

            String key = (String) entry.getKey();
            Object data = entry.getValue();

            clientObjectLiteralWriter.writeProperty(key);

            if (data instanceof Integer) {
                javaScriptWriter.writeInt(((Integer) data).intValue());
                continue;
            }
            if (data instanceof Boolean) {
                javaScriptWriter.writeBoolean(((Boolean) data).booleanValue());
                continue;
            }
            if (data != null) {
                data = String.valueOf(data);
            }

            javaScriptWriter.writeString((String) data);
        }

        clientObjectLiteralWriter.end();
    }

    public static String serializeDnDTypes(String types[]) {
        StringAppender sa = new StringAppender(types.length * 32);

        for (int i = 0; i < types.length; i++) {
            if (sa.length() > 0) {
                sa.append(',');
            }

            sa.append(types[i].trim());
        }

        return sa.toString();
    }

    public static ILocalizedComponent localizeComponent(FacesContext context,
            String componentId) {

        componentId = computeComponentId(context, componentId);

        final UIComponent component = ComponentTools.getForComponent(context,
                componentId, context.getViewRoot());

        if (component != null) {

            if (LOG.isDebugEnabled()) {
                LOG.debug("Search component '" + componentId + "' => "
                        + component);
            }

            return new ILocalizedComponent() {

                public UIComponent getComponent() {
                    return component;
                }

                public void end() {
                }
            };
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("Search component '" + componentId
                    + "': not found, try grids ...");
        }

        IHtmlRenderContext htmlRenderContext = HtmlRenderContext
                .getRenderContext(context);

        String separatorChar = htmlRenderContext.getHtmlProcessContext()
                .getNamingSeparator();
        if (separatorChar != null
                && separatorChar.equals(NAMING_SEPARATOR_STRING) == false) {

            if (LOG.isErrorEnabled()) {
                LOG.error("Search component '" + componentId + "': separator '"
                        + separatorChar + "' not supported !");
            }
            return null;
        }

        final List<Object> rowsSetted = new ArrayList<Object>();

        UIComponent parent = context.getViewRoot();

        StringTokenizer st = new StringTokenizer(componentId,
                NAMING_SEPARATOR_STRING);
        for (; st.hasMoreTokens();) {
            String id = st.nextToken();

            boolean onlyDigit = true;
            char chs[] = id.toCharArray();
            for (int i = 0; i < chs.length; i++) {
                if (Character.isDigit(chs[i])) {
                    continue;
                }
                onlyDigit = false;
                break;
            }

            if (onlyDigit == false) {
                UIComponent child = parent.findComponent(id);

                if (LOG.isDebugEnabled()) {
                    LOG.debug("Search component '" + componentId
                            + "': current '" + parent.getClientId(context)
                            + "' id='" + id + "' onlyDigit=" + onlyDigit
                            + " => " + child);
                }

                if (child == null) {
                    parent = null;
                    break;
                }

                parent = child;

                continue;
            }

            int rowIndex = Integer.parseInt(id);

            if (parent instanceof UIData) {
                UIData uiData = (UIData) parent;

                uiData.setRowIndex(rowIndex);

                if (uiData.isRowAvailable()) {
                    rowsSetted.add(uiData);
                    continue;
                }

                parent = null;
                break;
            }

            if (parent instanceof UIData2) {
                UIData2 uiData2 = (UIData2) parent;

                uiData2.setRowIndex(rowIndex);

                if (uiData2.isRowAvailable()) {
                    rowsSetted.add(uiData2);
                    continue;
                }

                parent = null;
                break;
            }

            if (LOG.isErrorEnabled()) {
                LOG.error("Search component '" + componentId + "': current '"
                        + parent.getClientId(context) + "' id='" + id
                        + "' rowIndex=" + rowIndex + " => unknown child "
                        + parent.getClass());
            }

            parent = null;
            break;
        }

        if (parent == null) {
            if (rowsSetted.isEmpty() == false) {
                releaseGrids(rowsSetted);
            }

            return null;
        }

        final UIComponent result = parent;

        return new ILocalizedComponent() {

            public UIComponent getComponent() {
                return result;
            }

            public void end() {
                releaseGrids(rowsSetted);
            }

        };
    }

    private static final void releaseGrids(List rowsSetted) {
        Collections.reverse(rowsSetted);

        for (Iterator it = rowsSetted.iterator(); it.hasNext();) {
            UIComponent component = (UIComponent) it.next();

            if (component instanceof UIData) {
                ((UIData) component).setRowIndex(-1);
                continue;
            }

            if (component instanceof UIData2) {
                ((UIData2) component).setRowIndex(-1);
                continue;
            }
        }
    }

    public static String computeSubInputComponentId(IHtmlWriter htmlWriter,
            String clientId) {
        // FacesContext facesContext =
        // htmlWriter.getComponentRenderContext().getFacesContext();

        return computeSubInputComponentId(
                htmlWriter.getComponentRenderContext(), clientId);
    }

    public static String computeSubInputComponentId(
            IComponentRenderContext componentRenderContext, String clientId) {
        FacesContext facesContext = componentRenderContext.getFacesContext();

        UIComponent component = facesContext.getViewRoot().findComponent(
                clientId);
        if (component == null) {
            return null;
        }

        Renderer renderer = getRenderer(facesContext, component);
        if (renderer == null) {
            return null;
        }

        if ((renderer instanceof ISubInputClientIdRenderer) == false) {
            return null;
        }

        String subClientId = ((ISubInputClientIdRenderer) renderer)
                .computeSubInputClientId(
                        componentRenderContext.getRenderContext(), component,
                        clientId);

        return subClientId;
    }

    public static Renderer getRenderer(FacesContext facesContext,
            UIComponent component) {
        String rendererType = component.getRendererType();
        if (rendererType == null) {
            return null;
        }

        RenderKitFactory rkFactory = (RenderKitFactory) FactoryFinder
                .getFactory(FactoryFinder.RENDER_KIT_FACTORY);
        RenderKit renderKit = rkFactory.getRenderKit(facesContext, facesContext
                .getViewRoot().getRenderKitId());

        return renderKit.getRenderer(component.getFamily(), rendererType);
    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.2 $ $Date: 2013/01/11 15:45:00 $
     */
    public interface ILocalizedComponent {
        UIComponent getComponent();

        void end();
    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.2 $ $Date: 2013/01/11 15:45:00 $
     */
    private static class Position {

        public final String values;

        public int start;

        public Position(String values) {
            this.values = values;
        }

        public Position(Position position) {
            values = position.values;
            start = position.start;
        }
    }

    public static void includeScript(IHtmlWriter writer, String src,
            String javascriptCharset) throws WriterException {

        IHtmlProcessContext htmlProcessContext = writer
                .getHtmlComponentRenderContext().getHtmlRenderContext()
                .getHtmlProcessContext();

        writer.startElement(IHtmlWriter.SCRIPT);

        if (htmlProcessContext.useMetaContentScriptType() == false) {
            writer.writeType(IHtmlRenderContext.JAVASCRIPT_TYPE);
        }

        writer.writeSrc(src);

        if (javascriptCharset != null) {
            writer.writeCharSet(javascriptCharset);
        }

        writer.endElement(IHtmlWriter.SCRIPT);
    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.2 $ $Date: 2013/01/11 15:45:00 $
     */
    public static final class BasicDecoderContext implements IDecoderContext {
        private final IProcessContext processContext;

        private final UIComponent component;

        private final Renderer renderer;

        protected BasicDecoderContext(IProcessContext processContext,
                UIComponent component, Renderer renderer) {
            this.processContext = processContext;
            this.component = component;
            this.renderer = renderer;
        }

        protected BasicDecoderContext(IProcessContext processContext,
                UIComponent component) {
            this(processContext, component, null);
        }

        public final IProcessContext getProcessContext() {
            return processContext;
        }

        public final UIComponent getComponent() {
            return component;
        }

        public final Renderer getRenderer() {
            return renderer;
        }
    }
}
