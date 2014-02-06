/*
 * $Id: KeyEntryRenderer.java,v 1.5 2014/01/03 11:12:23 jbmeslin Exp $
 */
package org.rcfaces.renderkit.html.internal.renderer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javax.faces.FacesException;
import javax.faces.application.FacesMessage;
import javax.faces.component.NamingContainer;
import javax.faces.component.UIColumn;
import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.component.ValueHolder;
import javax.faces.context.FacesContext;
import javax.faces.model.DataModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.component.KeyEntryComponent;
import org.rcfaces.core.component.TextComponent;
import org.rcfaces.core.component.capability.IEmptyDataMessageCapability;
import org.rcfaces.core.component.capability.IEmptyMessageCapability;
import org.rcfaces.core.component.iterator.IColumnIterator;
import org.rcfaces.core.event.PropertyChangeEvent;
import org.rcfaces.core.internal.capability.IGridComponent;
import org.rcfaces.core.internal.component.Properties;
import org.rcfaces.core.internal.lang.StringAppender;
import org.rcfaces.core.internal.renderkit.IComponentData;
import org.rcfaces.core.internal.renderkit.IComponentRenderContext;
import org.rcfaces.core.internal.renderkit.IRenderContext;
import org.rcfaces.core.internal.renderkit.IRequestContext;
import org.rcfaces.core.internal.renderkit.WriterException;
import org.rcfaces.core.internal.tools.FilterExpressionTools;
import org.rcfaces.core.internal.tools.FilteredDataModel;
import org.rcfaces.core.internal.tools.ValuesTools;
import org.rcfaces.core.internal.util.ParamUtils;
import org.rcfaces.core.lang.FilterPropertiesMap;
import org.rcfaces.core.model.IClientDataModel;
import org.rcfaces.core.model.IComponentRefModel;
import org.rcfaces.core.model.IFilterProperties;
import org.rcfaces.core.model.IFiltredModel;
import org.rcfaces.renderkit.html.internal.HtmlTools;
import org.rcfaces.renderkit.html.internal.IAccessibilityRoles;
import org.rcfaces.renderkit.html.internal.IHtmlComponentRenderContext;
import org.rcfaces.renderkit.html.internal.IHtmlWriter;
import org.rcfaces.renderkit.html.internal.IJavaScriptWriter;
import org.rcfaces.renderkit.html.internal.IObjectLiteralWriter;
import org.rcfaces.renderkit.html.internal.JavaScriptClasses;
import org.rcfaces.renderkit.html.internal.ns.XhtmlNSAttributes;
import org.rcfaces.renderkit.html.internal.service.DataGridUpdateBehaviorListener;
import org.rcfaces.renderkit.html.internal.service.KeyEntryUpdateBehaviorListener;
import org.rcfaces.renderkit.html.internal.service.PopupGridUpdateBehaviorListener;
import org.rcfaces.renderkit.html.internal.util.ClientDataModelTools;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.5 $ $Date: 2014/01/03 11:12:23 $
 */
@XhtmlNSAttributes({ "valueFormat", "valueFormatLabel", "editable", "readOnly",
        "disabled", "maxTextLength", "suggestionDelayMs", "suggestionMinChars",
        "noValueFormatLabel", "forLabel", "forceValidation", "valueColumnId",
        "labelColumnId", "filtred", "filterExpression", "emptyMessage",
        "emptyDataMessage", "selectedValue", "invalidKey" })
public class KeyEntryRenderer extends DataGridRenderer {

    private static final Log LOG = LogFactory.getLog(KeyEntryRenderer.class);

    protected static final String GRID_HTML_CONTENT = "org.rcfaces.renderkit.html.GRID_HTML_CONTENT";

    protected static final String INPUT_ERRORED_PROPERTY = "org.rcfaces.html.COMBO_GRID_ERRORED";

    protected static final String INVALID_INPUT_TEXT_PROPERTY = "keyEntry.INVALID_INPUT";

    private static final String CLIENT_DB_ENABLED_PROPERTY = "org.rcfaces.html.CLIENT_DB_ENABLED";

    private static final String DEFAULT_CONTENT_PRIMARY_KEY = "value";

    protected String getJavaScriptClassName() {
        return JavaScriptClasses.KEY_ENTRY;
    }

    protected String getWAIRole() {
        return IAccessibilityRoles.TEXTBOX;
    }

    @SuppressWarnings("unused")
    protected void encodeGrid(IHtmlWriter htmlWriter) throws WriterException {

        IHtmlComponentRenderContext componentRenderContext = htmlWriter
                .getHtmlComponentRenderContext();

        FacesContext facesContext = componentRenderContext.getFacesContext();

        KeyEntryComponent keyEntryComponent = (KeyEntryComponent) componentRenderContext
                .getComponent();

        boolean disabled = keyEntryComponent.isDisabled(facesContext);
        boolean readOnly = keyEntryComponent.isReadOnly(facesContext);
        boolean editable = keyEntryComponent.isEditable(facesContext);

        htmlWriter.startElement(IHtmlWriter.INPUT);

        htmlWriter.writeType(IHtmlWriter.TEXT_INPUT_TYPE);

        writeHtmlAttributes(htmlWriter);
        writeJavaScriptAttributes(htmlWriter);

        String labelId = computeDescriptionClientId(htmlWriter);
        htmlWriter.writeAttribute("aria-describedby", labelId);

        AbstractGridRenderContext gridRenderContext = getGridRenderContext(componentRenderContext);

        Map<String, String> formatValues = new HashMap<String, String>();

        String valueFormat = keyEntryComponent.getValueFormat(facesContext);
        if (valueFormat != null) {
            htmlWriter.writeAttributeNS("valueFormat", valueFormat);
            formatValues.put("valueFormat", valueFormat);
        }

        // Si on a déja un toolTipText, on ignore valueFormatTooltip
        if (true || keyEntryComponent.getToolTipText(facesContext) == null) {
            String valueFormatTooltip = keyEntryComponent
                    .getValueFormatTooltip(facesContext);
            if (valueFormatTooltip != null) {
                htmlWriter.writeAttributeNS("valueFormatTooltip",
                        valueFormatTooltip);
                formatValues.put("valueFormatTooltip", valueFormatTooltip);
            }
        }

        String valueFormatDescription = keyEntryComponent
                .getValueFormatDescription(facesContext);
        if (valueFormatDescription == null) {
            valueFormatDescription = "{key} {label}";
        }
        if (valueFormatDescription != null) {
            htmlWriter.writeAttributeNS("valueFormatDescription",
                    valueFormatDescription);
            formatValues.put("valueFormatDescription", valueFormatDescription);
        }

        String valueFormatLabel = keyEntryComponent
                .getValueFormatLabel(facesContext);
        if (valueFormatLabel != null) {
            htmlWriter.writeAttributeNS("valueFormatLabel", valueFormatLabel);
            formatValues.put("valueFormatLabel", valueFormatLabel);
        }

        Map formattedValues = null;
        String formattedValue = null;
        String formattedValueLabel = null;
        String formattedValueTooltip = null;
        String formattedValueDescription = null;
        String convertedSelectedValue = null;
        Object selectedValue = keyEntryComponent.getSelectedValue(facesContext);
        String valueColumnId = keyEntryComponent.getValueColumnId(facesContext);

        if (selectedValue != null) {
            UIComponent converterComponent = getColumn(keyEntryComponent,
                    valueColumnId);

            convertedSelectedValue = ValuesTools.convertValueToString(
                    selectedValue, converterComponent, facesContext);

            if (convertedSelectedValue != null
                    && convertedSelectedValue.length() > 0) {

                formattedValues = formatValue(facesContext, keyEntryComponent,
                        selectedValue, convertedSelectedValue, formatValues);
                if (formattedValues != null) {
                    formattedValue = (String) formattedValues
                            .get("valueFormat");
                    formattedValueLabel = (String) formattedValues
                            .get("valueFormatLabel");
                    formattedValueTooltip = (String) formattedValues
                            .get("valueFormatTooltip");
                    formattedValueDescription = (String) formattedValues
                            .get("valueFormatDescription");
                }

                if (formattedValue == null) {
                    componentRenderContext.setAttribute(INPUT_ERRORED_PROPERTY,
                            Boolean.TRUE);

                    formattedValue = convertedSelectedValue;
                }
            }
        }

        ICssStyleClasses cssStyleClasses = getCssStyleClasses(htmlWriter);

        if (disabled) {
            cssStyleClasses.addSuffix("_disabled");

        } else if (readOnly) {
            cssStyleClasses.addSuffix("_readOnly");
        }

        if (componentRenderContext.containsAttribute(INPUT_ERRORED_PROPERTY)) {
            cssStyleClasses.addSuffix("_errored");
        }

        writeCssAttributes(htmlWriter, cssStyleClasses, CSS_ALL_MASK);

        if (editable == false) {
            htmlWriter.writeAttributeNS("editable", false);
        }
        if (readOnly) {
            htmlWriter.writeAttributeNS("readOnly", true);
        }
        if (disabled) {
            htmlWriter.writeAttributeNS("disabled", true);
        }

        int maxTextLength = keyEntryComponent.getMaxTextLength(facesContext);
        if (maxTextLength > 0) {
            htmlWriter.writeAttributeNS("maxTextLength", maxTextLength);
        }

        int suggestionDelayMs = keyEntryComponent
                .getSuggestionDelayMs(facesContext);
        if (suggestionDelayMs > 0) {
            htmlWriter.writeAttributeNS("suggestionDelayMs", suggestionDelayMs);
        }

        int suggestionMinChars = keyEntryComponent
                .getSuggestionMinChars(facesContext);
        if (suggestionMinChars > 0) {
            htmlWriter.writeAttributeNS("suggestionMinChars",
                    suggestionMinChars);
        }

        String noValueFormatLabel = keyEntryComponent
                .getNoValueFormatLabel(facesContext);
        if (noValueFormatLabel != null) {
            htmlWriter.writeAttributeNS("noValueFormatLabel",
                    noValueFormatLabel);
        }

        String ac = keyEntryComponent.getForLabel(facesContext);

        IRenderContext renderContext = componentRenderContext
                .getRenderContext();

        String forId = renderContext.computeBrotherComponentClientId(
                keyEntryComponent, ac);

        if (forId != null) {
            htmlWriter.writeAttributeNS("forLabel", forId);

            UIComponent label = facesContext.getViewRoot().findComponent(forId);
            if (null != label && label instanceof TextComponent) {
                if (formattedValueLabel != null) {
                    ((TextComponent) label).setValue(formattedValueLabel);
                } else if (noValueFormatLabel != null) {
                    ((TextComponent) label).setValue(noValueFormatLabel);
                }
            }
        }

        boolean forceValidation = keyEntryComponent
                .isForceValidation(facesContext);
        if (forceValidation == true) {
            htmlWriter.writeAttributeNS("forceValidation", forceValidation);
        }

        if (valueColumnId != null) {
            htmlWriter.writeAttributeNS("valueColumnId", valueColumnId);
        }

        String labelColumnId = keyEntryComponent.getLabelColumnId(facesContext);
        if (labelColumnId != null) {
            htmlWriter.writeAttributeNS("labelColumnId", labelColumnId);
        }

        DataModel dataModel = gridRenderContext.getDataModel();
        writeFiltredModel(htmlWriter, dataModel, gridRenderContext);

        writeClientDataModel(htmlWriter, dataModel, gridRenderContext);

        // if (comboGridComponent instanceof IEmptyMessageCapability) {
        String emptyMessage = ((IEmptyMessageCapability) keyEntryComponent)
                .getEmptyMessage();
        if (emptyMessage != null) {
            emptyMessage = ParamUtils.formatMessage(keyEntryComponent,
                    emptyMessage);
            htmlWriter.writeAttributeNS("emptyMessage", emptyMessage);
        }
        // }

        // if (comboGridComponent instanceof IEmptyDataMessageCapability) {
        String emptyDataMessage = ((IEmptyDataMessageCapability) keyEntryComponent)
                .getEmptyDataMessage();
        if (emptyDataMessage != null) {
            emptyDataMessage = ParamUtils.formatMessage(keyEntryComponent,
                    emptyDataMessage);

            htmlWriter.writeAttributeNS("emptyDataMessage", emptyDataMessage);
        }

        if (convertedSelectedValue != null
                && convertedSelectedValue.length() > 0) {

            htmlWriter
                    .writeAttributeNS("selectedValue", convertedSelectedValue);

            if (componentRenderContext
                    .containsAttribute(INPUT_ERRORED_PROPERTY)) {
                // La clef est inconnue !
                htmlWriter.writeAttributeNS("invalidKey", true);
            }

        } else if (formattedValue == null) {
            String fv = (String) keyEntryComponent.getAttributes().get(
                    INVALID_INPUT_TEXT_PROPERTY);
            if (fv != null) {
                htmlWriter.writeAttributeNS("invalidKey", true);
                formattedValue = fv;
            }
        }

        if (formattedValue != null) {
            htmlWriter.writeValue(formattedValue);
        }

        if (formattedValueTooltip != null) {
            htmlWriter.writeTitle(formattedValueTooltip);
        }
        htmlWriter.endComponent();

        htmlWriter.endElement(IHtmlWriter.INPUT);

        writeDescriptionComponent(htmlWriter, formattedValueDescription);

        htmlWriter.getJavaScriptEnableMode().enableOnInit();
    }

    protected void writeClientDataModel(IHtmlWriter htmlWriter,
            DataModel dataModel, AbstractGridRenderContext gridRenderContext)
            throws WriterException {
        IClientDataModel clientDataModel = getAdapter(IClientDataModel.class,
                dataModel);
        if (clientDataModel == null) {
            return;
    }

        String contentName = clientDataModel.getContentName();
        if (contentName == null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("ContentName() returns NULL, disabled client data model !");
        }
            return;
            }
        String contentKey = clientDataModel.getContentKey();
        String contentPK = clientDataModel.getContentPrimaryKey();
        if (contentPK == null) {
            contentPK = DEFAULT_CONTENT_PRIMARY_KEY;
            }
        int contentRowCount = clientDataModel.getContentRowCount();

        if (contentName == null || contentKey == null || contentPK == null) {
            LOG.error("IClientDataModel disabled, contentName='"
                    + contentName
                    + "' contentKey='"
                    + contentKey
                    + "' contentRowCount="
                    + contentRowCount
                    + " contentPrimaryKey='"
                    + contentPK
                    + "' gridId="
                    + htmlWriter.getComponentRenderContext()
                            .getComponentClientId());
            return;
            }

        String contentIndex = ClientDataModelTools.format(clientDataModel);

        // htmlWriter.writeAttributeNS("indexedDb", true);
        htmlWriter.writeAttributeNS("idbName", contentName);
        htmlWriter.writeAttributeNS("idbKey", contentKey);
        if (contentRowCount >= 0) {
            htmlWriter.writeAttributeNS("idbCount", contentRowCount);
        }
        htmlWriter.writeAttributeNS("idbPK", contentPK);
        if (contentIndex != null) {
            htmlWriter.writeAttributeNS("idbIndex", contentIndex);
                    }

        htmlWriter.getComponentRenderContext().setAttribute(
                CLIENT_DB_ENABLED_PROPERTY, Boolean.TRUE);

                    
              }

    protected void writeFiltredModel(IHtmlWriter htmlWriter,
            DataModel dataModel, AbstractGridRenderContext gridRenderContext)
            throws WriterException {
        IFiltredModel filtredDataModel = getAdapter(IFiltredModel.class,
                dataModel);
        if (filtredDataModel == null) {
            return;
                }

        htmlWriter.writeAttributeNS("filtred", true);

        IFilterProperties filterMap = gridRenderContext.getFiltersMap();
        if (filterMap != null && filterMap.isEmpty() == false) {
            String filterExpression = HtmlTools.encodeFilterExpression(
                    filterMap, gridRenderContext.getProcessContext(),
                    (UIComponent) gridRenderContext.getGridComponent());
            htmlWriter.writeAttributeNS("filterExpression", filterExpression);
        }

       
        }

    protected String computeDescriptionClientId(IHtmlWriter htmlWriter) {
        String labelId = htmlWriter.getComponentRenderContext()
                .getComponentClientId() + "::description";
        return labelId;
            }

    protected void writeDescriptionComponent(IHtmlWriter htmlWriter,
            String computedDescription) throws WriterException {
        String labelId = computeDescriptionClientId(htmlWriter);
        if (labelId == null) {
            return;
            }

        htmlWriter.startElement(IHtmlWriter.LABEL);
        htmlWriter.writeId(labelId);
        htmlWriter.writeClass("f_keyEntry_description");
        htmlWriter.writeAttribute("aria-live", "polite");

        if (computedDescription != null) {
            htmlWriter.writeText(computedDescription);
        }

        htmlWriter.endElement(IHtmlWriter.LABEL);
            }

    protected boolean needAjaxJavaScriptClasses(IHtmlWriter writer,
            IGridComponent dataGridComponent) {
        return true;
            }

    protected final Map formatValue(FacesContext facesContext,
            KeyEntryComponent comboGridComponent, Object selectedValue,
            String convertedSelectedValue,
            final Map<String, String> formatValues) {

        return (Map) filterValue(facesContext, comboGridComponent,
                selectedValue, convertedSelectedValue, new IFilterProcessor() {

                    public Object process(FacesContext facesContext,
                            KeyEntryComponent comboGridComponent,
                            String convertedSelectedValue, Object rowData) {

                        return formatValue(facesContext, comboGridComponent,
                                rowData, formatValues);
        }
                });
    }

    protected final Map<String, String> formatValue(FacesContext facesContext,
            KeyEntryComponent comboGridComponent, Object rowData,
            Map<String, String> formatValues) {

        Map<String, String> columnValues = new HashMap<String, String>();

        IColumnIterator it = comboGridComponent.listColumns();
        for (int idx = 0; it.hasNext(); idx++) {
            UIColumn column = it.next();
            if ((column instanceof ValueHolder) == false) {
                continue;
            }

            String columnId = column.getId();
            if (columnId == null) {
                continue;
            }

            Object value = ((ValueHolder) column).getValue();
            String svalue = ValuesTools.convertValueToString(value, column,
                    facesContext);

            columnValues.put(columnId, svalue);
            columnValues.put(String.valueOf(idx), svalue);
        }

        Map<String, String> results = new HashMap<String, String>();
        String labelColumnId = comboGridComponent.getLabelColumnId();
        if (formatValues.size() > 0) {
            Iterator iterator = formatValues.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry entry = (Map.Entry) iterator.next();
                String key = (String) entry.getKey();
                String valueFormat = (String) entry.getValue();
                if (valueFormat == null) {

                    if (labelColumnId != null) {
                        valueFormat = "{" + labelColumnId + "}";
                    } else {
                        valueFormat = "{0}";
                    }
                }
                results.put(key, formatMessage(valueFormat, columnValues));
            }
        } else if (labelColumnId != null) {
            results.put("valueFormat",
                    formatMessage("{" + labelColumnId + "}", columnValues));
        }
        return results;

    }

    protected final String formatMessage(String message,
            Map<String, String> parameters) {
        StringAppender ret = new StringAppender(message.length()
                + parameters.size() * 8);

        int pos = 0;
        for (; pos < message.length();) {
            int idx = message.indexOf('{', pos);
            int idx2 = message.indexOf('\'', pos);

            if (idx2 < 0 && idx < 0) {
                ret.append(message, pos, message.length() - pos);

                return ret.toString();
            }

            if (idx2 < 0 || (idx >= 0 && idx < idx2)) {
                idx2 = message.indexOf('}', idx);
                if (idx2 < 0) {
                    throw new FacesException("Invalid expression \"" + message
                            + "\".");
                }

                ret.append(message, pos, idx - pos);

                String p = message.substring(idx + 1, idx2);

                if (p.length() > 0 && Character.isDigit(p.charAt(0))) {
                    int num = Integer.parseInt(p);
                    if (num >= 0 && num < parameters.size()) {
                        ret.append(parameters.get(String.valueOf(num)));
                    }

                } else if (parameters.containsKey(p)) {
                    ret.append(parameters.get(p));
                }

                pos = idx2 + 1;
                continue;
            }

            ret.append(message, pos, idx2 - pos);

            idx = message.indexOf('\'', idx2 + 1);
            if (idx < 0) {
                throw new Error("Invalid expression \"" + parameters + "\".");
            }
            pos = idx + 1;

            if (idx == idx2 + 1) {
                ret.append('\'');

            } else {
                ret.append(message, idx2 + 1, idx - idx2 - 1);

                if (message.charAt(pos) == '\'') {
                    ret.append('\'');
                }
            }
        }

        return ret.toString();
    }

    protected UIColumn getColumn(KeyEntryComponent comboGridComponent,
            String valueColumnId) {

        if (valueColumnId == null) {
            return null;
        }

        IColumnIterator columnIterator = comboGridComponent.listColumns();
        for (; columnIterator.hasNext();) {
            UIColumn column = columnIterator.next();
            if (valueColumnId.equals(column.getId()) == false) {
                continue;
            }

            return column;
        }

        return null;
    }

    protected IHtmlWriter writeIdAttribute(IHtmlWriter htmlWriter)
            throws WriterException {
        IHtmlComponentRenderContext componentRenderContext = htmlWriter
                .getHtmlComponentRenderContext();

        if (isDataGridRenderer(htmlWriter) == false) {
            return super.writeIdAttribute(htmlWriter);
        }

        StringAppender id = new StringAppender(
                componentRenderContext.getComponentClientId(), 16);

        String separator = componentRenderContext.getRenderContext()
                .getProcessContext().getNamingSeparator();
        if (separator != null) {
            id.append(separator);
        } else {
            id.append(NamingContainer.SEPARATOR_CHAR);
        }

        id.append("grid");

        htmlWriter.writeId(id.toString());

        return htmlWriter;
    }

    protected void encodeJsBodyRows(IJavaScriptWriter jsWriter,
            AbstractGridRenderContext tableContext) {
        // On génère rien
    }

    protected void encodeBodyTableEnd(IHtmlWriter htmlWriter,
            AbstractGridRenderContext gridRenderContext) throws WriterException {
        // On ferme pas les DIV
    }

    public String getComponentStyleClassName(IHtmlWriter htmlWriter) {
        if (isDataGridRenderer(htmlWriter)) {
            return super.getComponentStyleClassName(htmlWriter);
        }

        return getJavaScriptClassName();
    }

    protected boolean isDataGridRenderer(IHtmlWriter htmlWriter) {
        return Boolean.TRUE.equals(htmlWriter.getComponentRenderContext()
                .getAttribute(GRID_HTML_CONTENT));
    }

    protected UIColumn getRowValueColumn(IGridComponent dg) {
        KeyEntryComponent dataGridComponent = (KeyEntryComponent) dg;

        String valueColumnId = dataGridComponent.getValueColumnId();
        if (valueColumnId != null) {
            for (IColumnIterator it = dg.listColumns(); it.hasNext();) {
                UIColumn column = it.next();
                if (valueColumnId.equals(column.getId()) == false) {
                    continue;
                }

                return column;
            }

            throw new FacesException("Can not find column '" + valueColumnId
                    + "'.");
        }

        return null;
    }

    protected void encodeJsColumns(IJavaScriptWriter htmlWriter,
            AbstractGridRenderContext gridRenderContext) throws WriterException {
    }

    public void encodeRowByKey(IJavaScriptWriter jsWriter,
            DataGridRenderContext tableContext) throws WriterException {

        FacesContext facesContext = jsWriter.getFacesContext();
        KeyEntryComponent keyEntryComponent = (KeyEntryComponent) tableContext
                .getGridComponent();

        DataModel dataModel = tableContext.getDataModel();

        if (dataModel instanceof IComponentRefModel) {
            ((IComponentRefModel) dataModel).setComponent(keyEntryComponent);
        }

        IFilterProperties filtersMap = tableContext.getFiltersMap();
        IFiltredModel filtredDataModel = getAdapter(IFiltredModel.class,
                dataModel);

        if (filtersMap != null) {
            if (filtredDataModel != null) {

                filtredDataModel.setFilter(filtersMap);
                tableContext.updateRowCount();

            } else {
                dataModel = FilteredDataModel.filter(dataModel, filtersMap);
                tableContext.updateRowCount();
            }

        } else if (filtredDataModel != null) {
            filtredDataModel.setFilter(FilterExpressionTools.EMPTY);
            tableContext.updateRowCount();
        }

        Map<String, String> columnValues = new HashMap<String, String>();
        List<String> colValues = new ArrayList<String>();

        String rowId = null;

        keyEntryComponent.setRowIndex(0);
        try {
            if (keyEntryComponent.isRowAvailable() == false) {
                // No result
                jsWriter.writeMethodCall("fa_valueSelected").write(");");
                return;
            }

            UIColumn rowValueColumn = tableContext.getRowValueColumn();

            if (rowValueColumn != null) {
                Object value = ((ValueHolder) rowValueColumn).getValue();

                rowId = convertValue(facesContext, rowValueColumn, value);
            }

            IColumnIterator it = keyEntryComponent.listColumns();
            for (int idx = 0; it.hasNext(); idx++) {
                UIColumn column = it.next();
                if ((column instanceof ValueHolder) == false) {
                    continue;
                }

                String columnId = column.getId();
                if (columnId == null) {
                    continue;
                }

                Object value = ((ValueHolder) column).getValue();
                String svalue = ValuesTools.convertValueToString(value, column,
                        facesContext);

                columnValues.put(columnId, svalue);
                columnValues.put(String.valueOf(idx), svalue);
                colValues.add(columnId);
            }

        } finally {
            keyEntryComponent.setRowIndex(-1);
        }

        String valueFormat = keyEntryComponent.getValueFormat(facesContext);
        if (valueFormat == null) {
            String labelColumnId = keyEntryComponent.getLabelColumnId();
            if (labelColumnId != null) {
                valueFormat = "{" + labelColumnId + "}";
            } else {
                valueFormat = "{0}";
            }
        }

        jsWriter.writeMethodCall("fa_valueSelected").writeString(rowId)
                .write(',')
                .writeString(formatMessage(valueFormat, columnValues))
                .write(',');

        IObjectLiteralWriter objWriter = jsWriter.writeObjectLiteral(false);
        for (Iterator it = colValues.iterator(); it.hasNext();) {
            String colId = (String) it.next();

            objWriter.writeProperty(colId).writeString(columnValues.get(colId));
        }

        objWriter.end().writeln(");");
    }

    protected void encodeJavaScript(IJavaScriptWriter writer)
            throws WriterException {
        super.encodeJavaScript(writer);

        if (writer.getComponentRenderContext().containsAttribute(
                CLIENT_DB_ENABLED_PROPERTY)) {
            writer.getJavaScriptRenderContext().appendRequiredClass(
                    "f_keyEntry", "indexDb");
        }
    }

    protected void decode(IRequestContext context, UIComponent component,
            IComponentData componentData) {
        super.decode(context, component, componentData);

        KeyEntryComponent keyEntryComponent = (KeyEntryComponent) component;

        FacesContext facesContext = context.getFacesContext();

        keyEntryComponent.getAttributes().remove(INVALID_INPUT_TEXT_PROPERTY);

        Object convertedSelectedValue = null;
        String selectedValue = componentData.getStringProperty("selected");
        if (selectedValue != null && selectedValue.length() > 0) {
            convertedSelectedValue = filterValue(facesContext,
                    keyEntryComponent, null, selectedValue,
                    new IFilterProcessor() {

                        public Object process(FacesContext facesContext,
                                KeyEntryComponent comboGridComponent,
                                String convertedSelectedValue, Object rowData) {

                            ValueHolder vh = (ValueHolder) getRowValueColumn(comboGridComponent);

                            if (vh == null) {
                                throw new FacesException(
                                        "Can identify the value column of keyEntry '"
                                                + comboGridComponent
                                                        .getClientId(facesContext)
                                                + "'");
                            }

                            return vh.getValue();
                        }
                    });

        } else if (keyEntryComponent.isForceValidation(facesContext) == false) {
            // Verifier qu'il n'y a pas de converter
            if (ValuesTools.getConverter(keyEntryComponent) == null) {
                convertedSelectedValue = componentData
                        .getStringProperty("text");
            }

        } else {
            // pas le temps de valider le selectedValue ????
            // On le fait coté serveur alors

            String notVerifiedKey = componentData.getStringProperty("text");
            ResourceBundle rb = ResourceBundle.getBundle(
                    "org.rcfaces.renderkit.html.internal.LocalStrings", context
                            .getProcessContext().getUserLocale());
            String errorMessage = null;

            if (notVerifiedKey != null && notVerifiedKey.length() > 0) {

                convertedSelectedValue = filterValue(facesContext,
                        keyEntryComponent, null, notVerifiedKey,
                        new IFilterProcessor() {

                            public Object process(FacesContext facesContext,
                                    KeyEntryComponent comboGridComponent,
                                    String convertedSelectedValue,
                                    Object rowData) {

                                ValueHolder vh = (ValueHolder) getRowValueColumn(comboGridComponent);

                                return vh.getValue();
                            }
                        });

                if (convertedSelectedValue == null) {
                    errorMessage = keyEntryComponent
                            .getValidationParameter("INVALIDKEY_ERROR_SUMMARY");

                    if (errorMessage == null) {
                        errorMessage = rb
                                .getString("f_keyEntry.INVALIDKEY_ERROR_SUMMARY");
                    }

                    keyEntryComponent.getAttributes().put(
                            INVALID_INPUT_TEXT_PROPERTY, notVerifiedKey);
        }

            } else if (keyEntryComponent.isRequired(facesContext)) {
                errorMessage = keyEntryComponent
                        .getValidationParameter("REQUIRED_ERROR_SUMMARY");

                if (errorMessage == null) {
                    errorMessage = rb
                            .getString("f_keyEntry.REQUIRED_ERROR_SUMMARY");
                }
            }

            if (errorMessage != null) {
                // Error de Validation !

                FacesMessage facesMessage = new FacesMessage(
                        FacesMessage.SEVERITY_ERROR, errorMessage, errorMessage);

                facesContext.addMessage(
                        keyEntryComponent.getClientId(facesContext),
                        facesMessage);
            }
        }

        Object old = keyEntryComponent.getSelectedValue(facesContext);

        if (convertedSelectedValue != old
                && (old == null || old.equals(convertedSelectedValue) == false)) {
            keyEntryComponent.setSelectedValue(convertedSelectedValue);

            component.queueEvent(new PropertyChangeEvent(component,
                    Properties.SELECTED_VALUE, old, convertedSelectedValue));
        }
    }

    protected void addUnlockProperties(Set<Serializable> unlockedProperties) {
        super.addUnlockProperties(unlockedProperties);

        unlockedProperties.add("selected");
        unlockedProperties.add("selectedValue");
    }

    protected interface IFilterProcessor {

        Object process(FacesContext facesContext,
                KeyEntryComponent comboGridComponent,
                String convertedSelectedValue, Object rowData);

    }

    @SuppressWarnings("unused")
    protected final Object filterValue(FacesContext facesContext,
            KeyEntryComponent comboGridComponent, Object selectedValue,
            String convertedSelectedValue, IFilterProcessor processor) {

        DataModel dataModel = comboGridComponent.getDataModelValue();

        IComponentRefModel componentRefModel = getAdapter(
                IComponentRefModel.class, dataModel);

        if (componentRefModel != null) {
            componentRefModel.setComponent(comboGridComponent);
        }

        IFiltredModel filtredDataModel = getAdapter(IFiltredModel.class,
                dataModel);

        if (filtredDataModel == null) {
            if (false) {
                LOG.error("Model does not implement IFiltredModel, returns *not found*");
                return null;
            }

            if (LOG.isInfoEnabled()) {
                LOG.info("Search a row value in a not filtred DataModel ! (comboGridComponent="
                        + comboGridComponent.getId() + ")");
            }

            UIComponent rowValueColumn = getRowValueColumn(comboGridComponent);
            if ((rowValueColumn instanceof ValueHolder) == false) {
                throw new FacesException("Can not get row value for column '"
                        + rowValueColumn + "'.");
            }
            ValueHolder columnValueHolder = (ValueHolder) rowValueColumn;

            String var = comboGridComponent.getVar(facesContext);
            if (var == null) {
                throw new FacesException("Var attribute is null !");
            }

            Map<String, Object> requestMap = facesContext.getExternalContext()
                    .getRequestMap();
            Object oldValue = requestMap.get(var);

            try {
                for (int rowIndex = 0;; rowIndex++) {
                    dataModel.setRowIndex(rowIndex);

                    if (dataModel.isRowAvailable() == false) {
                        break;
                    }

                    Object rowData = dataModel.getRowData();

                    requestMap.put(var, rowData);

                    Object value = columnValueHolder.getValue();

                    String convertedValue = ValuesTools.convertValueToString(
                            value, rowValueColumn, facesContext);

                    if (convertedSelectedValue.equals(convertedValue) == false) {
                        continue;
                    }

                    return processor.process(facesContext, comboGridComponent,
                            convertedSelectedValue, rowData);
                }

            } finally {
                requestMap.put(var, oldValue);
                dataModel.setRowIndex(-1);
            }

            return null;
        }

        IFilterProperties filterProperties = comboGridComponent
                .getFilterProperties(facesContext);
        if (filterProperties == null) {
            filterProperties = new FilterPropertiesMap();
        } else {
            filterProperties = new FilterPropertiesMap(filterProperties);
        }

        filterProperties.put("key", convertedSelectedValue);
        filterProperties.put("text", convertedSelectedValue);

        filterProperties.put("convertedValue", convertedSelectedValue);
        if (selectedValue != null) {
            filterProperties.put("value", selectedValue);
        }

        filtredDataModel.setFilter(filterProperties);

        try {
            dataModel.setRowIndex(0);

            boolean available = dataModel.isRowAvailable();

            if (LOG.isDebugEnabled()) {
                LOG.debug("formatValue index=0 available=" + available);
            }

            if (available == false) {
                return null;
            }

            Object rowData = dataModel.getRowData();

            String var = comboGridComponent.getVar(facesContext);

            if (LOG.isDebugEnabled()) {
                LOG.debug("formatValue rowData='" + rowData + "' var='" + var
                        + "'");
            }

            if (var == null) {
                throw new FacesException("Var attribute is null !");
            }

            Map<String, Object> requestMap = facesContext.getExternalContext()
                    .getRequestMap();
            Object oldValue = requestMap.put(var, rowData);

            try {
                return processor.process(facesContext, comboGridComponent,
                        convertedSelectedValue, rowData);

            } finally {
                requestMap.put(var, oldValue);
            }

        } finally {
            dataModel.setRowIndex(-1);
        }
    }
    
    @Override
	protected void appendGridUpdateBehavior(IHtmlWriter writer) {
		
		IComponentRenderContext renderContext = writer.getComponentRenderContext();
		KeyEntryComponent keyEntryComponent = (KeyEntryComponent) renderContext.getComponent();
		
		KeyEntryUpdateBehaviorListener.addAjaxBehavior(keyEntryComponent, renderContext.getFacesContext());
		PopupGridUpdateBehaviorListener.addAjaxBehavior(keyEntryComponent, renderContext.getFacesContext());
	}
    
}
