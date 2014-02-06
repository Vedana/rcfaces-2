/*
 * $Id: TimeEntryRenderer.java,v 1.2 2013/01/11 15:45:02 jbmeslin Exp $
 */
package org.rcfaces.renderkit.html.internal.renderer;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.rcfaces.core.component.TimeEntryComponent;
import org.rcfaces.core.internal.component.Properties;
import org.rcfaces.core.internal.lang.StringAppender;
import org.rcfaces.core.internal.renderkit.IComponentData;
import org.rcfaces.core.internal.renderkit.IComponentRenderContext;
import org.rcfaces.core.internal.renderkit.IComponentWriter;
import org.rcfaces.core.internal.renderkit.IRequestContext;
import org.rcfaces.core.internal.renderkit.WriterException;
import org.rcfaces.core.internal.tools.TimeTools;
import org.rcfaces.core.lang.Time;
import org.rcfaces.renderkit.html.internal.AbstractCompositeRenderer;
import org.rcfaces.renderkit.html.internal.IAccessibilityRoles;
import org.rcfaces.renderkit.html.internal.IHtmlWriter;
import org.rcfaces.renderkit.html.internal.JavaScriptClasses;
import org.rcfaces.renderkit.html.internal.ns.XhtmlNSAttributes;
import org.rcfaces.renderkit.html.internal.util.ListenerTools.INameSpace;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/01/11 15:45:02 $
 */
@XhtmlNSAttributes({ "timeFormat", "minTime", "maxTime", "min", "max",
        "defaultValue", "step", "separators", "cycle", "auto" })
public class TimeEntryRenderer extends AbstractCompositeRenderer {

    protected String getJavaScriptClassName() {
        return JavaScriptClasses.TIME_ENTRY;
    }

    protected void encodeEnd(IComponentWriter writer) throws WriterException {
        IHtmlWriter htmlWriter = (IHtmlWriter) writer;

        encodeComponent(htmlWriter);

        super.encodeEnd(writer);
    }

    protected void encodeComponent(IHtmlWriter htmlWriter)
            throws WriterException {
        IComponentRenderContext componentRenderContext = htmlWriter
                .getComponentRenderContext();
        FacesContext facesContext = componentRenderContext.getFacesContext();

        TimeEntryComponent timeEntryComponent = (TimeEntryComponent) componentRenderContext
                .getComponent();

        htmlWriter.startElement(IHtmlWriter.DIV);

        boolean disabled = timeEntryComponent.isDisabled(facesContext);
        if (disabled) {
            getCssStyleClasses(htmlWriter).addSuffix("_disabled");
        }

        boolean readOnly = timeEntryComponent.isReadOnly(facesContext);
        if (readOnly) {
            getCssStyleClasses(htmlWriter).addSuffix("_readOnly");
        }

        writeHtmlAttributes(htmlWriter);
        writeJavaScriptAttributes(htmlWriter);
        writeCssAttributes(htmlWriter);

        String timeFormat = timeEntryComponent.getTimeFormat(facesContext);
        if (timeFormat == null) {
            Locale locale = htmlWriter.getComponentRenderContext()
                    .getRenderContext().getProcessContext().getUserLocale();

            timeFormat = TimeTools.getDefaultTimeFormatPattern(locale);
        }

        if (timeFormat == null) {
            throw new FacesException("Invalid date format for component '"
                    + timeEntryComponent.getId() + "'.");
        }

        timeFormat = TimeTools.normalizeTimeFormat(componentRenderContext,
                timeFormat);

        // htmlWriter.writeAttributeNS("timeFormat", timeFormat);

        Time minTime = timeEntryComponent.getMinTime(facesContext);
        if (minTime != null) {
            htmlWriter.writeAttributeNS("minTime", minTime.getTime());
        }

        Time maxTime = timeEntryComponent.getMaxTime(facesContext);
        if (maxTime != null) {
            htmlWriter.writeAttributeNS("maxTime", maxTime.getTime());
        }

        writeClientValidatorParams(htmlWriter);

        encodeSubComponents(htmlWriter, timeEntryComponent, timeFormat);

        htmlWriter.endElement(IHtmlWriter.DIV);

        htmlWriter.getJavaScriptEnableMode().enableOnFocus();

        if (timeEntryComponent.isRequired()) {
            htmlWriter.getJavaScriptEnableMode().enableOnSubmit();
        }
    }

    protected String getWAIRole() {
        return IAccessibilityRoles.TEXT_FIELD;
    }

    protected void encodeSubComponents(IHtmlWriter htmlWriter,
            TimeEntryComponent timeEntryComponent, String dateFormat)
            throws WriterException {
        IComponentRenderContext componentRenderContext = htmlWriter
                .getComponentRenderContext();
        FacesContext facesContext = componentRenderContext.getFacesContext();

        Integer tabIndex = timeEntryComponent.getTabIndex(facesContext);
        String accessKey = timeEntryComponent.getAccessKey(facesContext);

        int minHour = -1;
        int maxHour = -1;
        int defaultHour = -1;
        int curHour = -1;

        int minMinute = 0;
        int maxMinute = 59;
        int defaultMinute = -1;
        int curMinute = -1;

        int minSecond = 0;
        int maxSecond = 59;
        int defaultSecond = -1;
        int curSecond = -1;

        int minMillis = 0;
        int maxMillis = 1000;
        int defaultMillis = -1;
        int curMillis = -1;

        Object currentValue = timeEntryComponent.getValue();
        if (currentValue instanceof Time) {
            Time curTime = (Time) currentValue;

            curHour = curTime.getHours();
            curMinute = curTime.getMinutes();
            curSecond = curTime.getSeconds();
            curMillis = curTime.getMillis();
        }

        Time defaultTime = timeEntryComponent.getDefaultTime(facesContext);
        if (defaultTime != null) {
            defaultHour = defaultTime.getHours();
            defaultMinute = defaultTime.getMinutes();
            defaultSecond = defaultTime.getSeconds();
            defaultMillis = defaultTime.getMillis();
        }

        Time minTime = timeEntryComponent.getMinTime(facesContext);
        Time maxTime = timeEntryComponent.getMaxTime(facesContext);

        if (minTime != null && maxTime != null) {
            if (minTime.compareTo(maxTime) > 0) {
                throw new FacesException("minTime (" + minTime
                        + ") is after maxTime (" + maxTime + ")");
            }

            minHour = minTime.getHours();
            maxHour = maxTime.getHours();

            if (minHour == maxHour) {
                minMinute = minTime.getMinutes();
                maxMinute = maxTime.getMinutes();

                if (minMinute == maxMinute) {
                    minSecond = minTime.getSeconds();
                    maxSecond = maxTime.getSeconds();

                    if (minSecond == maxSecond) {
                        minMillis = minTime.getMillis();
                        maxMillis = maxTime.getMillis();
                    }
                }
            }
        } else if (maxTime != null) {
            maxHour = maxTime.getHours();
            minHour = 0;

        } else if (minTime != null) {
            minHour = minTime.getHours();
        }

        boolean disabled = timeEntryComponent.isDisabled(facesContext);
        boolean readOnly = timeEntryComponent.isReadOnly(facesContext);
        boolean defaultAutoComplete = timeEntryComponent
                .isAutoCompletion(facesContext);

        StringAppender sb = new StringAppender(128);

        char chs[] = dateFormat.toCharArray();

        int nbSub = 0;
        char lastChar = 0;
        int nb = 0;
        for (int i = 0; i <= chs.length; i++) {
            char c = 0;
            if (i < chs.length) {
                c = chs[i];

                if (c == lastChar) {
                    nb++;
                    continue;
                }
                if (lastChar == 0) {
                    lastChar = c;
                    nb = 1;
                    continue;
                }
            }

            // C'est le cas si la fin etait quotée !
            if (nb < 1) {
                break;
            }

            int minValue = -1;
            int maxValue = -1;
            int curValue = -1;
            int defaultValue = -1;
            String step = null;
            boolean cycle = false;
            boolean autoComplete = defaultAutoComplete;

            switch (lastChar) {
            case 'H':
                minValue = minHour;
                maxValue = maxHour;
                defaultValue = defaultHour;
                curValue = curHour;

                step = timeEntryComponent.getHourStep(facesContext);

                nb = 2;
                break;

            case 'm':
                nb = 2;
                cycle = true;
                minValue = minMinute;
                maxValue = maxMinute;
                defaultValue = defaultMinute;
                curValue = curMinute;
                step = timeEntryComponent.getMinuteStep(facesContext);
                break;

            case 's':
                nb = 2;
                cycle = true;
                minValue = minSecond;
                maxValue = maxSecond;
                defaultValue = defaultSecond;
                curValue = curSecond;
                step = timeEntryComponent.getSecondStep(facesContext);
                break;

            case 'S':
                nb = 4;
                cycle = true;
                minValue = minMillis;
                maxValue = maxMillis;
                defaultValue = defaultMillis;
                curValue = curMillis;
                step = timeEntryComponent.getMillisStep(facesContext);
                break;

            default:
                for (; nb > 0; nb--) {
                    sb.append(lastChar);
                }
            }

            if (nb > 0) {
                String separators = null;
                int sbLength = sb.length();

                if (sbLength > 0) {
                    if (nbSub > 0) {
                        if (sbLength < 2) {
                            separators = sb.toString();

                        } else {
                            char sb2[] = new char[sbLength];
                            int idx2 = 0;
                            next_separator: for (int j = 0; j < sbLength; j++) {
                                char sep = sb.charAt(j);

                                for (int k = 0; k < idx2; k++) {
                                    if (sb2[k] != sep) {
                                        continue;
                                    }

                                    continue next_separator;
                                }

                                sb2[idx2++] = sep;
                            }

                            separators = new String(sb2, 0, idx2);
                        }
                    }

                    // htmlWriter.startElement(IHtmlWriter.SPAN");
                    htmlWriter.writeText(sb.toString());
                    // htmlWriter.endElement(IHtmlWriter.SPAN");

                    sb.setLength(0);
                }

                String sCurValue = null;
                if (curValue >= 0) {
                    StringAppender s = new StringAppender(
                            String.valueOf(curValue), nb);
                    s.insert(0, '0', nb - s.length());

                    sCurValue = s.toString();
                }

                String ns = htmlWriter.getRcfacesNamespace() + ":";

                Map<String, Object> attributes = new HashMap<String, Object>(8);
                if (minValue >= 0) {
                    attributes.put(ns + "min", String.valueOf(minValue));
                }

                if (maxValue >= 0) {
                    attributes.put(ns + "max", String.valueOf(maxValue));
                }

                if (defaultValue >= 0) {
                    attributes.put(ns + "defaultValue",
                            String.valueOf(defaultValue));
                }

                if (step != null && step.length() > 0) {
                    attributes.put(ns + "step", step);
                }

                if (separators != null && separators.length() > 0) {
                    attributes.put(ns + "separators", separators);
                }

                if (cycle) {
                    attributes.put(ns + "cycle", "true");
                }

                if (autoComplete) {
                    attributes.put(ns + "auto", "true");
                }

                // nb, sminValue, smaxValue, sdefaultValue, sCurValue,
                // step, disabled, readOnly, separators, cycle,
                // (autoComplete) ? "true" : null, false);

                writeSubInput(htmlWriter, accessKey, tabIndex, lastChar, nb,
                        nb, sCurValue, disabled, readOnly, false, attributes);
                accessKey = null; // Un seul accessKey !
                nbSub++;
            }

            if (c == 0) {
                break;
            }

            if (c != '\'') {
                lastChar = c;
                nb = 1;
                continue;
            }

            for (i++; i < chs.length; i++) {
                c = chs[i];

                if (c != '\'') {
                    sb.append(c);
                    continue;
                }

                // double quote ???
                if (i + 1 < chs.length && chs[i + 1] == c) {
                    sb.append(c);
                    i++;
                    continue;
                }
                break;
            }

            nb = 0;
            lastChar = 0;
        }

        if (sb.length() > 0) {
            htmlWriter.writeText(sb.toString());
        }
    }

    protected void decode(IRequestContext context, UIComponent component,
            IComponentData componentData) {
        super.decode(context, component, componentData);

        TimeEntryComponent timeEntryComponent = (TimeEntryComponent) component;

        Time timeValue = (Time) componentData.getProperty(Properties.VALUE);
        Time time = null;
        if (timeValue != null
                && timeEntryComponent.isValueLocked(context.getFacesContext()) == false) {

            time = timeValue;
        }

        timeEntryComponent.setSubmittedExternalValue(time);
    }

    protected void addUnlockProperties(Set<Serializable> unlockedProperties) {
        super.addUnlockProperties(unlockedProperties);

        unlockedProperties.add(Properties.VALUE);
    }

    protected String getActionEventName(INameSpace nameSpace) {
        return nameSpace.getSelectionEventName();
    }
}
