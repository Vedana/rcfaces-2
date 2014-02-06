/*
 * $Id: ComboGridRenderer.java,v 1.3 2013/11/13 12:53:30 jbmeslin Exp $
 */
package org.rcfaces.renderkit.html.internal.renderer;

import java.util.HashMap;
import java.util.Map;

import javax.faces.component.NamingContainer;
import javax.faces.component.UIComponent;
import javax.faces.component.UINamingContainer;
import javax.faces.context.FacesContext;
import javax.faces.model.DataModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.component.ComboGridComponent;
import org.rcfaces.core.component.TextComponent;
import org.rcfaces.core.component.capability.IEmptyDataMessageCapability;
import org.rcfaces.core.component.capability.IEmptyMessageCapability;
import org.rcfaces.core.component.capability.ISizeCapability;
import org.rcfaces.core.internal.capability.IGridComponent;
import org.rcfaces.core.internal.lang.StringAppender;
import org.rcfaces.core.internal.manager.IValidationParameters;
import org.rcfaces.core.internal.renderkit.IComponentRenderContext;
import org.rcfaces.core.internal.renderkit.IProcessContext;
import org.rcfaces.core.internal.renderkit.IRenderContext;
import org.rcfaces.core.internal.renderkit.IScriptRenderContext;
import org.rcfaces.core.internal.renderkit.WriterException;
import org.rcfaces.core.internal.tools.ValuesTools;
import org.rcfaces.core.internal.util.ParamUtils;
import org.rcfaces.core.model.IFilterProperties;
import org.rcfaces.core.model.IFiltredModel;
import org.rcfaces.core.model.ISelectedCriteria;
import org.rcfaces.core.model.ISortedComponent;
import org.rcfaces.renderkit.html.internal.EventsRenderer;
import org.rcfaces.renderkit.html.internal.HtmlTools;
import org.rcfaces.renderkit.html.internal.IAccessibilityRoles;
import org.rcfaces.renderkit.html.internal.IHtmlComponentRenderContext;
import org.rcfaces.renderkit.html.internal.IHtmlElements;
import org.rcfaces.renderkit.html.internal.IHtmlWriter;
import org.rcfaces.renderkit.html.internal.IJavaScriptRenderContext;
import org.rcfaces.renderkit.html.internal.IJavaScriptWriter;
import org.rcfaces.renderkit.html.internal.ISubInputClientIdRenderer;
import org.rcfaces.renderkit.html.internal.JavaScriptClasses;
import org.rcfaces.renderkit.html.internal.ns.XhtmlNSAttributes;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:30 $
 */
@XhtmlNSAttributes({ "valueColumnId", "labelColumnId", "valueFormat",
        "valueFormatLabel", "rows", "paged", "editable", "readOnly",
        "disabled", "maxTextLength", "rowStyleClass", "suggestionDelayMs",
        "suggestionMinChars", "noValueFormatLabel", "forLabel",
        "pagerStyleClass", "pagerLookId", "popupStyleClass", "gridStyleClass",
        "gridLookId", "searchFieldVisible", "forceValidation", "headerVisible",
        "filtred", "filterExpression", "popupWidth", "popupHeight",
        "emptyMessage", "emptyDataMessage", "selectedValue", "invalidKey",
        "emptyMessage" })
public class ComboGridRenderer extends KeyEntryRenderer implements
        ISubInputClientIdRenderer {

    private static final Log LOG = LogFactory.getLog(ComboGridRenderer.class);

    private static final int ARROW_IMAGE_WIDTH = 16;

    private static final int ARROW_IMAGE_HEIGHT = 16;

    private static final String BUTTON_ID_SUFFIX = ""
            + UINamingContainer.SEPARATOR_CHAR
            + UINamingContainer.SEPARATOR_CHAR + "button";

    private static final String INPUT_ID_SUFFIX = ""
            + UINamingContainer.SEPARATOR_CHAR
            + UINamingContainer.SEPARATOR_CHAR + "input";

    protected String getJavaScriptClassName() {
        return JavaScriptClasses.COMBO_GRID;
    }

    protected void encodeGrid(IHtmlWriter htmlWriter) throws WriterException {

        IHtmlComponentRenderContext componentRenderContext = htmlWriter
                .getHtmlComponentRenderContext();

        FacesContext facesContext = componentRenderContext.getFacesContext();

        ComboGridComponent comboGridComponent = (ComboGridComponent) componentRenderContext
                .getComponent();

        AbstractGridRenderContext gridRenderContext = getGridRenderContext(componentRenderContext);

        Map formattedValues = null;
        String formattedValue = null;
        String formattedValueLabel = null;
        String formattedValueTooltip = null;
        String formattedValueDescription = null;
        String convertedSelectedValue = null;
        Object selectedValue = comboGridComponent
                .getSelectedValue(facesContext);
        String valueColumnId = comboGridComponent
                .getValueColumnId(facesContext);

        String labelColumnId = comboGridComponent
                .getLabelColumnId(facesContext);

        Map<String, String> formatValues = new HashMap<String, String>();

        String valueFormat = comboGridComponent.getValueFormat(facesContext);
        if (valueFormat == null) {
            if (labelColumnId != null) {
                valueFormat = "{" + labelColumnId + "}";
            } else {
                valueFormat = "{" + valueColumnId + "}";
            }
        } else {
            htmlWriter.writeAttributeNS("valueFormat", valueFormat);
        }

        formatValues.put("valueFormat", valueFormat);

        // Si on a déja un toolTipText, on ignore valueFormatTooltip
        String valueFormatTooltip = null;
        if (true || comboGridComponent.getToolTipText(facesContext) == null) {
            valueFormatTooltip = comboGridComponent
                    .getValueFormatTooltip(facesContext);
            if (valueFormatTooltip != null) {
                htmlWriter.writeAttributeNS("valueFormatTooltip",
                        valueFormatTooltip);
                formatValues.put("valueFormatTooltip", valueFormatTooltip);
            }
        }
        String valueFormatDescription = comboGridComponent
                .getValueFormatDescription(facesContext);
        if (valueFormatDescription == null) {
            StringAppender sa = new StringAppender(128);

            if (valueColumnId != null) {
                sa.append('{').append(valueColumnId).append('}');
            }
            if (labelColumnId != null) {
                if (sa.length() > 0) {
                    sa.append(' ');
                }
                sa.append('{').append(labelColumnId).append('}');
            }

            if (sa.length() == 0) {
                sa.append("{0}");
            }

            valueFormatDescription = sa.toString();
        }
        if (valueFormatDescription != null) {
            htmlWriter.writeAttributeNS("valueFormatDescription",
                    valueFormatDescription);
            formatValues.put("valueFormatDescription", valueFormatDescription);
        }

        String valueFormatLabel = comboGridComponent
                .getValueFormatLabel(facesContext);
        if (valueFormatLabel == null) {
            if (labelColumnId != null) {
                valueFormatLabel = "{" + labelColumnId + "}";
            } else {
                valueFormatLabel = "{" + valueColumnId + "}";
            }
        }
        formatValues.put("valueFormatLabel", valueFormatLabel);

        if (selectedValue != null) {
            UIComponent converterComponent = getColumn(comboGridComponent,
                    valueColumnId);

            convertedSelectedValue = ValuesTools.convertValueToString(
                    selectedValue, converterComponent, facesContext);

            if (convertedSelectedValue != null
                    && convertedSelectedValue.length() > 0) {

                formattedValues = formatValue(facesContext, comboGridComponent,
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

        boolean disabled = comboGridComponent.isDisabled(facesContext);
        boolean readOnly = comboGridComponent.isReadOnly(facesContext);
        boolean editable = comboGridComponent.isEditable(facesContext);

        ICssStyleClasses cssStyleClasses = getCssStyleClasses(htmlWriter);

        if (disabled) {
            cssStyleClasses.addSuffix("_disabled");

        } else if (readOnly) {
            cssStyleClasses.addSuffix("_readOnly");
        }

        if (componentRenderContext.containsAttribute(INPUT_ERRORED_PROPERTY)) {
            cssStyleClasses.addSuffix("_errored");
        }

        String width = comboGridComponent.getWidth(facesContext);

        int colWidth = -1;
        if (width != null) {
            int totalWidth = computeSize(width, 0, 2);

            colWidth = totalWidth - ARROW_IMAGE_WIDTH - 4;
        }

        htmlWriter.startElement(IHtmlWriter.TABLE);
        htmlWriter.writeCellPadding(0);
        htmlWriter.writeCellSpacing(0);

        writeHtmlAttributes(htmlWriter);
        writeJavaScriptAttributes(htmlWriter);
        writeCssAttributes(htmlWriter, cssStyleClasses, CSS_ALL_MASK);

        if (valueColumnId != null) {
            htmlWriter.writeAttributeNS("valueColumnId", valueColumnId);
        }

        if (labelColumnId != null) {
            htmlWriter.writeAttributeNS("labelColumnId", labelColumnId);
        }

        if (valueFormat != null) {
            htmlWriter.writeAttributeNS("valueFormat", valueFormat);
        }

        if (valueFormatLabel != null) {
            htmlWriter.writeAttributeNS("valueFormatLabel", valueFormatLabel);
        }

        if (valueFormatTooltip != null) {
            htmlWriter.writeAttributeNS("valueFormatTooltip",
                    valueFormatTooltip);
        }

        int rows = gridRenderContext.getRows();
        if (rows > 0) {
            htmlWriter.writeAttributeNS("rows", rows);
        }
        if (gridRenderContext.isPaged() == false) {
            htmlWriter.writeAttributeNS("paged", false);
        }
        if (editable == false) {
            htmlWriter.writeAttributeNS("editable", false);
        }
        if (readOnly) {
            htmlWriter.writeAttributeNS("readOnly", true);
        }
        if (disabled) {
            htmlWriter.writeAttributeNS("disabled", true);
        }

        int maxTextLength = comboGridComponent.getMaxTextLength(facesContext);
        if (maxTextLength > 0) {
            htmlWriter.writeAttributeNS("maxTextLength", maxTextLength);
        }

        String rowStyleClasses[] = gridRenderContext.getRowStyleClasses();

        if (rowStyleClasses != null) {
            StringAppender sa = new StringAppender(rowStyleClasses.length * 32);

            for (int i = 0; i < rowStyleClasses.length; i++) {
                String token = rowStyleClasses[i];

                if (sa.length() > 0) {
                    sa.append(',');
                }
                sa.append(token);
            }

            htmlWriter.writeAttributeNS("rowStyleClass", sa.toString());
        }

        int suggestionDelayMs = comboGridComponent
                .getSuggestionDelayMs(facesContext);
        if (suggestionDelayMs > 0) {
            htmlWriter.writeAttributeNS("suggestionDelayMs", suggestionDelayMs);
        }

        int suggestionMinChars = comboGridComponent
                .getSuggestionMinChars(facesContext);
        if (suggestionMinChars > 0) {
            htmlWriter.writeAttributeNS("suggestionMinChars",
                    suggestionMinChars);
        }

        String noValueFormatLabel = comboGridComponent
                .getNoValueFormatLabel(facesContext);
        if (noValueFormatLabel != null) {
            htmlWriter.writeAttributeNS("noValueFormatLabel",
                    noValueFormatLabel);
        }

        String clientValidatorParameters = constructClientValidatorParameters(
                htmlWriter, comboGridComponent);
        if (clientValidatorParameters != null
                && clientValidatorParameters.length() > 0) {
            htmlWriter.writeAttributeNS("clientValidator",
                    clientValidatorParameters);
        }

        String ac = comboGridComponent.getForLabel(facesContext);

        IRenderContext renderContext = componentRenderContext
                .getRenderContext();

        String forId = renderContext.computeBrotherComponentClientId(
                comboGridComponent, ac);

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

        String pagerStyleClass = comboGridComponent
                .getPagerStyleClass(facesContext);
        if (pagerStyleClass != null) {
            htmlWriter.writeAttributeNS("pagerStyleClass", pagerStyleClass);
        }

        String pagerLookId = comboGridComponent.getPagerLookId(facesContext);
        if (pagerLookId != null) {
            htmlWriter.writeAttributeNS("pagerLookId", pagerLookId);
        }

        String popupStyleClass = comboGridComponent
                .getPopupStyleClass(facesContext);
        if (popupStyleClass != null) {
            htmlWriter.writeAttributeNS("popupStyleClass", popupStyleClass);
        }

        String gridStyleClass = comboGridComponent
                .getGridStyleClass(facesContext);
        if (gridStyleClass != null) {
            htmlWriter.writeAttributeNS("gridStyleClass", gridStyleClass);
        }

        String gridLookId = comboGridComponent.getGridLookId(facesContext);
        if (gridLookId != null) {
            htmlWriter.writeAttributeNS("gridLookId", gridLookId);
        }

        boolean searchField = comboGridComponent
                .isSearchFieldVisible(facesContext);
        if (searchField == false) {
            htmlWriter.writeAttributeNS("searchFieldVisible", searchField);
        }

        boolean forceValidation = comboGridComponent
                .isForceValidation(facesContext);
        if (forceValidation == true) {
            htmlWriter.writeAttributeNS("forceValidation", forceValidation);
        }

        boolean headerVisible = gridRenderContext.isHeaderVisible();
        if (headerVisible == true && comboGridComponent.isHeaderVisibleSetted()) {
            htmlWriter.writeAttributeNS("headerVisible", true);
        }

        DataModel dataModel = gridRenderContext.getDataModel();
        IFiltredModel filtredDataModel = getAdapter(IFiltredModel.class,
                dataModel);
        if (filtredDataModel != null) {
            htmlWriter.writeAttributeNS("filtred", true);

            IFilterProperties filterMap = gridRenderContext.getFiltersMap();
            if (filterMap != null && filterMap.isEmpty() == false) {
                String filterExpression = HtmlTools.encodeFilterExpression(
                        filterMap, componentRenderContext.getRenderContext()
                                .getProcessContext(), componentRenderContext
                                .getComponent());
                htmlWriter.writeAttributeNS("filterExpression",
                        filterExpression);
            }
        }

        int popupWidth = gridRenderContext.getGridWidth();
        if (popupWidth > 0) {
            htmlWriter.writeAttributeNS("popupWidth", popupWidth);
        }

        int popupHeight = gridRenderContext.getGridHeight();
        if (popupHeight > 0) {
            htmlWriter.writeAttributeNS("popupHeight", popupHeight);
        }

        // if (comboGridComponent instanceof IEmptyMessageCapability) {
        String emptyMessage = ((IEmptyMessageCapability) comboGridComponent)
                .getEmptyMessage();
        if (emptyMessage != null) {
            emptyMessage = ParamUtils.formatMessage(comboGridComponent,
                    emptyMessage);
            htmlWriter.writeAttributeNS("emptyMessage", emptyMessage);
        }
        // }

        // if (comboGridComponent instanceof IEmptyDataMessageCapability) {
        String emptyDataMessage = ((IEmptyDataMessageCapability) comboGridComponent)
                .getEmptyDataMessage();
        if (emptyDataMessage != null) {
            emptyDataMessage = ParamUtils.formatMessage(comboGridComponent,
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
            String fv = (String) comboGridComponent.getAttributes().get(
                    INVALID_INPUT_TEXT_PROPERTY);
            if (fv != null) {
                htmlWriter.writeAttributeNS("invalidKey", true);
                formattedValue = fv;
            }
        }

        writePagerMessage(htmlWriter, comboGridComponent);

        htmlWriter.startElement(IHtmlWriter.COL);
        if (colWidth > 0) {
            htmlWriter.writeWidth(colWidth);
        } else {
            htmlWriter.writeWidth("1*");
        }
        htmlWriter.endElement(IHtmlWriter.COL);

        htmlWriter.startElement(IHtmlWriter.COL);
        htmlWriter.writeWidth(ARROW_IMAGE_WIDTH);
        htmlWriter.endElement(IHtmlWriter.COL);

        htmlWriter.startElement(IHtmlWriter.TBODY);

        htmlWriter.startElement(IHtmlWriter.TR);

        htmlWriter.startElement(IHtmlWriter.TD);
        htmlWriter.writeClass(getMainStyleClassName() + "_inputCell");

        writeInputSubComponent(htmlWriter, formattedValue, colWidth,
                formattedValueTooltip);

        writeDescriptionComponent(htmlWriter, formattedValueDescription);

        htmlWriter.endElement(IHtmlWriter.TD);

        htmlWriter.startElement(IHtmlWriter.TD);
        htmlWriter.writeWidth(ARROW_IMAGE_WIDTH);
        htmlWriter.writeClass(getMainStyleClassName() + "_buttonCell");

        writeImageSubComponent(htmlWriter);

        htmlWriter.endElement(IHtmlWriter.TD);

        htmlWriter.endElement(IHtmlWriter.TR);
        htmlWriter.endElement(IHtmlWriter.TBODY);
        htmlWriter.endElement(IHtmlWriter.TABLE);

        htmlWriter.getJavaScriptEnableMode().enableOnInit();

        htmlWriter.endComponent();
    }

    private String constructClientValidatorParameters(IHtmlWriter htmlWriter,
            IValidationParameters validationParameters) {

        Map<String, String> parameters = validationParameters
                .getValidationParametersMap();

        StringAppender sb = new StringAppender(128);
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            String key = entry.getKey();

            if (sb.length() > 0) {
                sb.append(':');
            }
            if (key == null) {
                key = "%";
            }

            EventsRenderer.appendCommand(sb, key);

            String value = entry.getValue();

            if (sb.length() > 0) {
                sb.append(':');
            }
            if (value == null) {
                value = "%";
            }

            EventsRenderer.appendCommand(sb, value);
        }

        return sb.toString();
    }

    protected void writeImageSubComponent(IHtmlWriter htmlWriter)
            throws WriterException {

        IHtmlComponentRenderContext componentRenderContext = htmlWriter
                .getHtmlComponentRenderContext();

        htmlWriter.startElement(IHtmlWriter.IMG);
        htmlWriter.writeId(componentRenderContext.getComponentClientId()
                + BUTTON_ID_SUFFIX);
        htmlWriter.writeClass(getMainStyleClassName() + "_button");
        htmlWriter.writeWidth(ARROW_IMAGE_WIDTH);
        htmlWriter.writeHeight(ARROW_IMAGE_HEIGHT);

        String url = componentRenderContext.getHtmlRenderContext()
                .getHtmlProcessContext()
                .getStyleSheetURI(BLANK_IMAGE_URL, true);

        htmlWriter.writeSrc(url);

        htmlWriter.endElement(IHtmlWriter.IMG);
    }

    protected void writeInputSubComponent(IHtmlWriter htmlWriter,
            String formattedValue, int colWidth, String title)
            throws WriterException {

        IComponentRenderContext componentRenderContext = htmlWriter
                .getComponentRenderContext();

        FacesContext facesContext = componentRenderContext.getFacesContext();

        ComboGridComponent comboGridComponent = (ComboGridComponent) componentRenderContext
                .getComponent();

        htmlWriter.startElement(IHtmlElements.INPUT);
        if (colWidth > 0) {
            htmlWriter.writeStyle().writeWidthPx(colWidth - 4);
        }

        htmlWriter.writeType(IHtmlWriter.TEXT_INPUT_TYPE);
        htmlWriter.writeRole(IAccessibilityRoles.COMBOBOX);
        htmlWriter.writeAutoComplete(IHtmlElements.AUTOCOMPLETE_OFF);

        htmlWriter.writeId(componentRenderContext.getComponentClientId()
                + INPUT_ID_SUFFIX);

        String toolTipText = comboGridComponent.getToolTipText(facesContext);
        if (toolTipText != null) {
            htmlWriter.writeTitle(toolTipText);
        }

        String labelId = computeDescriptionClientId(htmlWriter);
        htmlWriter.writeAttribute("aria-describedby", labelId);

        StringAppender sa = new StringAppender(128);
        sa.append(getMainStyleClassName());
        sa.append("_input");

        String emptyMessage = null;

        if (componentRenderContext.containsAttribute(INPUT_ERRORED_PROPERTY)) {
            sa.append(' ').append(getMainStyleClassName())
                    .append("_input_errored");

        } else if ((formattedValue == null || formattedValue.length() == 0)) {
            emptyMessage = comboGridComponent.getEmptyMessage(facesContext);

            if (emptyMessage != null) {
                sa.append(' ').append(getMainStyleClassName())
                        .append("_input_empty_message");

                htmlWriter.writeAttributeNS("emptyMessage", true);
            }
        }

        htmlWriter.writeClass(sa.toString());

        if (title != null) {
            htmlWriter.writeTitle(title);
        }

        writeInputAttributes(htmlWriter);

        Integer tabIndex = comboGridComponent
                .getTabIndex(componentRenderContext.getFacesContext());
        if (tabIndex != null) {
            htmlWriter.writeTabIndex(tabIndex.intValue());
        }

        if (formattedValue != null) {
            htmlWriter.writeValue(formattedValue);

        } else if (emptyMessage != null) {
            htmlWriter.writeValue(emptyMessage);
        }

        htmlWriter.endElement(IHtmlWriter.INPUT);
    }

    protected void writeInputAttributes(IHtmlWriter htmlWriter)
            throws WriterException {

        IHtmlComponentRenderContext componentRenderContext = htmlWriter
                .getHtmlComponentRenderContext();

        FacesContext facesContext = componentRenderContext.getFacesContext();

        ComboGridComponent comboGridComponent = (ComboGridComponent) componentRenderContext
                .getComponent();

        boolean disabled = comboGridComponent.isDisabled(facesContext);
        boolean readOnly = comboGridComponent.isReadOnly(facesContext);
        boolean editable = comboGridComponent.isEditable(facesContext);

        if (disabled) {
            htmlWriter.writeDisabled();
        }
        if (readOnly || editable == false) {
            htmlWriter.writeReadOnly();
        }
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

    protected void encodeJsHeader(IJavaScriptWriter jsWriter,
            AbstractGridRenderContext gridRenderContext) throws WriterException {

        String htmlContent = (String) jsWriter.getComponentRenderContext()
                .removeAttribute(GRID_HTML_CONTENT);

        if (htmlContent != null) {
            jsWriter.writeMethodCall("f_setGridInnerHTML")
                    .writeString(htmlContent).writeln(");");
        }
    }

    public AbstractGridRenderContext createTableContext(
            IHtmlComponentRenderContext componentRenderContext) {
        AbstractGridRenderContext tableContext = new ComboGridRenderContext(
                componentRenderContext);

        return tableContext;
    }

    public DataGridRenderContext createTableContext(
            IProcessContext processContext,
            IJavaScriptRenderContext scriptRenderContext, IGridComponent dg,
            int rowIndex, int forcedRows, ISortedComponent sortedComponents[],
            String filterExpression, String showAdditionals,
            String hideAdditionals, ISelectedCriteria[] criteriaContainers) {
        DataGridRenderContext tableContext = new ComboGridRenderContext(
                processContext, scriptRenderContext, dg, rowIndex, forcedRows,
                sortedComponents, filterExpression, showAdditionals,
                hideAdditionals, criteriaContainers);

        return tableContext;
    }

    public String computeSubInputClientId(IRenderContext renderContext,
            UIComponent component, String clientId) {
        return clientId + INPUT_ID_SUFFIX;
    }

    protected void encodeJsColumns(IJavaScriptWriter htmlWriter,
            AbstractGridRenderContext gridRenderContext) throws WriterException {
        encodeJsColumns(htmlWriter, gridRenderContext, GENERATE_CELL_IMAGES
                | GENERATE_CELL_TEXT | GENERATE_CELL_WIDTH);
    }

    protected String getWAIRole() {
        return IAccessibilityRoles.PRESENTATION;
    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:30 $
     */
    public class ComboGridRenderContext extends DataGridRenderContext {

        public ComboGridRenderContext(IProcessContext processContext,
                IJavaScriptRenderContext scriptRenderContext,
                IGridComponent dg, int rowIndex, int forcedRows,
                ISortedComponent[] sortedComponents, String filterExpression,
                String showAdditionals, String hideAdditionals,
                ISelectedCriteria[] criteriaContainers) {
            super(processContext, scriptRenderContext, dg, rowIndex,
                    forcedRows, sortedComponents, filterExpression,
                    showAdditionals, hideAdditionals, criteriaContainers);
        }

        public ComboGridRenderContext(
                IHtmlComponentRenderContext componentRenderContext) {
            super(componentRenderContext);
        }

        protected void computeGridSize(ISizeCapability sizeCapability) {
            this.gridWidth = ((ComboGridComponent) gridComponent)
                    .getPopupWidth();
            this.gridHeight = ((ComboGridComponent) gridComponent)
                    .getPopupHeight();
        }

    }
}
