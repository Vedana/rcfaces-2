/*
 * $Id: NumberEntryRenderer.java,v 1.2 2013/01/11 15:45:02 jbmeslin Exp $
 * 
 */
package org.rcfaces.renderkit.html.internal.renderer;

import java.text.DecimalFormatSymbols;
import java.util.Currency;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.rcfaces.core.component.NumberEntryComponent;
import org.rcfaces.core.component.capability.INumberFormatTypeCapability;
import org.rcfaces.core.internal.component.Properties;
import org.rcfaces.core.internal.lang.StringAppender;
import org.rcfaces.core.internal.renderkit.IComponentData;
import org.rcfaces.core.internal.renderkit.IComponentRenderContext;
import org.rcfaces.core.internal.renderkit.IComponentWriter;
import org.rcfaces.core.internal.renderkit.IRequestContext;
import org.rcfaces.core.internal.renderkit.WriterException;
import org.rcfaces.core.internal.tools.NumberTools;
import org.rcfaces.core.internal.tools.PageConfiguration;
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
@XhtmlNSAttributes({ "minimum", "maximum", "defaultNumber", "number",
        "separators", "auto" })
public class NumberEntryRenderer extends AbstractCompositeRenderer {

    protected static final String UNDEFINED_CURRENCY = "XXX";

    protected String getJavaScriptClassName() {
        return JavaScriptClasses.NUMBER_ENTRY;
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

        NumberEntryComponent numberEntryComponent = (NumberEntryComponent) componentRenderContext
                .getComponent();

        htmlWriter.startElement(IHtmlWriter.DIV);

        writeHtmlAttributes(htmlWriter);
        writeJavaScriptAttributes(htmlWriter);
        writeCssAttributes(htmlWriter);

        DecimalFormatSymbols decimalFormatSymbols = null;
        Locale locale = PageConfiguration.getLiteralLocale(
                componentRenderContext.getRenderContext().getProcessContext(),
                numberEntryComponent);
        if (locale == null) {
            locale = htmlWriter.getComponentRenderContext().getRenderContext()
                    .getProcessContext().getUserLocale();
        }

        String numberFormat = numberEntryComponent
                .getNumberFormat(facesContext);
        if (numberFormat == null) {
            int formatType = numberEntryComponent
                    .getNumberFormatType(facesContext);

            switch (formatType) {
            case INumberFormatTypeCapability.INTEGER_FORMAT_TYPE:
                numberFormat = NumberTools
                        .getDefaultIntegerFormatPattern(locale);
                break;
            case INumberFormatTypeCapability.PERCENT_FORMAT_TYPE:
                numberFormat = NumberTools
                        .getDefaultPercentFormatPattern(locale);
                break;

            case INumberFormatTypeCapability.CURRENCY_FORMAT_TYPE:
                numberFormat = NumberTools
                        .getDefaultCurrencyFormatPattern(locale);
                break;

            default:
                numberFormat = NumberTools
                        .getDefaultNumberFormatPattern(locale);
                break;
            }

            decimalFormatSymbols = NumberTools.getDecimalFormatSymbols(locale);
        }
        if (numberFormat == null) {
            throw new FacesException("Invalid number format for component '"
                    + numberEntryComponent.getId() + "'.");
        }
        numberFormat = NumberTools.normalizeNumberFormat(
                componentRenderContext, numberFormat);

        if (decimalFormatSymbols == null) {
            decimalFormatSymbols = NumberTools.getDefaultDecimalFormatSymbols();
        }

        String ns = htmlWriter.getRcfacesNamespace() + ":";

        Number minNumber = numberEntryComponent.getMinimum(facesContext);
        if (minNumber != null) {
            writeNumber(htmlWriter, ns + "minimum", minNumber);
        }

        Number maxNumber = numberEntryComponent.getMaximum(facesContext);
        if (maxNumber != null) {
            writeNumber(htmlWriter, ns + "maximum", maxNumber);
        }

        Number defaultNumber = numberEntryComponent
                .getDefaultNumber(facesContext);
        if (defaultNumber != null) {
            writeNumber(htmlWriter, ns + "defaultNumber", defaultNumber);
        }

        Number number = numberEntryComponent.getNumber();
        if (number != null) {
            writeNumber(htmlWriter, ns + "number", number);
        }

        writeClientValidatorParams(htmlWriter);
        encodeSubComponents(htmlWriter, numberEntryComponent, number,
                numberFormat, decimalFormatSymbols, locale);

        htmlWriter.endElement(IHtmlWriter.DIV);

        // htmlWriter.enableJavaScript(); // Sur le focus seulement
    }

    protected String getWAIRole() {
        return IAccessibilityRoles.TEXT_FIELD;
    }

    private void writeNumber(IHtmlWriter htmlWriter, String attributeName,
            Number number) throws WriterException {
        htmlWriter.writeAttribute(attributeName, String.valueOf(number));
    }

    protected void encodeSubComponents(IHtmlWriter htmlWriter,
            NumberEntryComponent numberEntryComponent, Number number,
            String numberFormat, DecimalFormatSymbols decimalFormatSymbols,
            Locale locale) throws WriterException {
        IComponentRenderContext componentRenderContext = htmlWriter
                .getComponentRenderContext();
        FacesContext facesContext = componentRenderContext.getFacesContext();

        Integer tabIndex = numberEntryComponent.getTabIndex(facesContext);
        String accessKey = numberEntryComponent.getAccessKey(facesContext);

        boolean disabled = numberEntryComponent.isDisabled(facesContext);
        boolean readOnly = numberEntryComponent.isReadOnly(facesContext);

        StringAppender sb = new StringAppender(128);

        char chs[] = numberFormat.toCharArray();

        char decimalSeparator = '.';
        // char groupingSeparator = ',';
        if (decimalFormatSymbols != null) {
            decimalSeparator = decimalFormatSymbols.getDecimalSeparator();
            // groupingSeparator = decimalFormatSymbols.getGroupingSeparator();
        }

        int nbSub = 0;
        char lastChar = 0;
        int nb = 0;
        int optional = 0;
        int decimalPart = 0;
        // boolean hasGroupingSeparator = false;
        for (int i = 0; i <= chs.length; i++) {
            char c = 0;
            char originalChar = 0;

            if (i < chs.length) {
                c = chs[i];
                originalChar = c;

                if (c == ',') {
                    // hasGroupingSeparator = true;
                    continue;
                }
                if (c == '#') {
                    /*
                     * if (nb != optional) { throw new FacesException("Invalid
                     * number format '" + numberFormat + "'."); }
                     */
                    optional++;
                    c = '0';
                }
                if (c == '.' && decimalPart == 0) {
                    c = decimalSeparator;
                }
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

            if (lastChar == '#' || lastChar == '0') {
                // Rien ;;

            } else if (lastChar == '¤' && decimalFormatSymbols != null) {
                appendCurrency(sb, nb, decimalFormatSymbols, locale);
                nb = 0;

            } else {
                for (; nb > 0; nb--) {
                    sb.append(lastChar);
                }
            }

            if (nb > 0) {
                String separators = null;
                int sbLength = sb.length();

                if (sbLength > 0) {
                    if (nbSub > 0) {
                        char sb2[] = new char[sbLength + 1];
                        int idx2 = 0;
                        sb2[idx2++] = '.';

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

                    String text = sb.toString();

                    // htmlWriter.startElement(IHtmlWriter.SPAN");
                    htmlWriter.writeText(text);
                    // htmlWriter.endElement(IHtmlWriter.SPAN");

                    sb.setLength(0);
                }

                StringAppender curValue = null;

                if (optional == nb) {
                    optional = nb - 1;
                }

                if (decimalPart == 0) {
                    if (numberEntryComponent.isIntegerDigitsSetted()) {
                        int nbIntegerDigits = numberEntryComponent
                                .getIntegerDigits(facesContext);

                        if (nbIntegerDigits == 0) {
                            nbIntegerDigits = 1;
                        }

                        optional += nbIntegerDigits - nb;
                        nb = nbIntegerDigits;
                    }

                    lastChar = 'I';

                    if (number != null) {
                        curValue = new StringAppender(16);

                        String svalue = String.valueOf(number.longValue());
                        int valueLength = svalue.length();
                        if (nb - optional > valueLength) {
                            curValue.append('0', nb - optional - valueLength);
                        }

                        curValue.append(svalue);
                    }
                } else if (decimalPart == 1) {
                    lastChar = 'D';

                    if (numberEntryComponent.isFractionDigitsSetted()) {
                        int nbFractionDigits = numberEntryComponent
                                .getFractionDigits(facesContext);

                        optional += nbFractionDigits - nb;
                        nb = nbFractionDigits;
                    }

                    if (number != null && nb > 0) {
                        curValue = new StringAppender(16);

                        double d = number.doubleValue() - number.longValue();

                        int valueLength = 0;
                        if (d != 0.0) {
                            String s = String.valueOf(Math.abs(d));
                            s = s.substring(s.indexOf('.') + 1);

                            valueLength = s.length();
                            curValue.append(s);
                        }
                        if (nb - optional > valueLength) {
                            curValue.insert(0, '0', nb - optional - valueLength);
                        }
                    }
                } else {
                    throw new FacesException("Invalid number format '"
                            + numberFormat + "'.");
                }

                if (nb > 0) {
                    Map<String, Object> attributes = new HashMap<String, Object>(
                            3);

                    String ns = htmlWriter.getRcfacesNamespace() + ":";

                    if (separators != null && separators.length() > 0) {
                        attributes.put(ns + "separators", separators);
                    }

                    if (nb > optional) {
                        attributes.put(ns + "auto",
                                String.valueOf(nb - optional));
                    }

                    writeSubInput(htmlWriter, accessKey, tabIndex, lastChar, 1,
                            nb,
                            (curValue != null) ? curValue.toString() : null,
                            disabled, readOnly, true, attributes);
                    accessKey = null; // Un seul accessKey !
                    nbSub++;

                    decimalPart++;
                }
            }

            optional = 0;
            if (originalChar == '#') {
                optional++;

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

    private void appendCurrency(StringAppender sb, int nb,
            DecimalFormatSymbols decimalFormatSymbols, Locale locale) {
        Currency currency = decimalFormatSymbols.getCurrency();
        if (currency == null) {
            String currencySymbol = decimalFormatSymbols.getCurrencySymbol();
            if (currencySymbol != null) {
                sb.append(currencySymbol);

                return;
            }
            sb.append('¤');
            return;
        }

        if (nb > 1) {
            String currencyCode = currency.getCurrencyCode();

            if (currencyCode != null
                    && UNDEFINED_CURRENCY.equals(currencyCode) == false) {
                sb.append(currencyCode);
                return;
            }
        }

        if (locale != null) {
            String symbol = currency.getSymbol(locale);
            if (symbol != null && UNDEFINED_CURRENCY.equals(symbol) == false) {
                sb.append(symbol);
                return;
            }
        }

        String symbol = currency.getSymbol();
        if (symbol != null && UNDEFINED_CURRENCY.equals(symbol) == false) {
            sb.append(symbol);
            return;
        }

        sb.append('¤');
    }

    protected void decode(IRequestContext context, UIComponent component,
            IComponentData componentData) {
        super.decode(context, component, componentData);

        FacesContext facesContext = context.getFacesContext();

        NumberEntryComponent numberEntryComponent = (NumberEntryComponent) component;

        Number numberValue = componentData.getNumberProperty(Properties.VALUE);
        if (numberValue != null
                && numberEntryComponent.isValueLocked(facesContext) == false) {

            if (numberEntryComponent.isFractionDigitsSetted()) {
                int fd = numberEntryComponent.getFractionDigits(facesContext);

                if (fd == 0) {
                    if ((numberValue instanceof Float)
                            || (numberValue instanceof Double)) {
                        numberValue = new Long(numberValue.longValue());
                    }

                } else if (fd > 0) {
                    if ((numberValue instanceof Float) == false
                            && (numberValue instanceof Double) == false) {
                        numberValue = new Double(numberValue.doubleValue());
                    }
                }
            }

            numberEntryComponent.setSubmittedExternalValue(numberValue);
        }
    }

    protected String getActionEventName(INameSpace nameSpace) {
        return nameSpace.getSelectionEventName();
    }

}
