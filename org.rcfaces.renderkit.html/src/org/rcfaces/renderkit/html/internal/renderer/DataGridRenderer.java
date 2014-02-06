/*
 * $Id: DataGridRenderer.java,v 1.8 2013/12/19 15:46:46 jbmeslin Exp $
 * 
 */
package org.rcfaces.renderkit.html.internal.renderer;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.FacesException;
import javax.faces.component.UIColumn;
import javax.faces.component.UIComponent;
import javax.faces.component.ValueHolder;
import javax.faces.context.FacesContext;
import javax.faces.model.DataModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.component.AdditionalInformationComponent;
import org.rcfaces.core.component.DataGridComponent;
import org.rcfaces.core.component.ToolTipComponent;
import org.rcfaces.core.component.capability.IAdditionalInformationValuesCapability;
import org.rcfaces.core.component.capability.IAsyncRenderModeCapability;
import org.rcfaces.core.component.capability.ICellClickableCapability;
import org.rcfaces.core.component.capability.ICellImageCapability;
import org.rcfaces.core.component.capability.ICellStyleClassCapability;
import org.rcfaces.core.component.capability.ICellToolTipTextCapability;
import org.rcfaces.core.component.capability.ICheckedValuesCapability;
import org.rcfaces.core.component.capability.IClientFullStateCapability;
import org.rcfaces.core.component.capability.ICriteriaCountCapability;
import org.rcfaces.core.component.capability.ICriteriaManagerCapability;
import org.rcfaces.core.component.capability.IDragAndDropEffects;
import org.rcfaces.core.component.capability.IKeySearchColumnIdCapability;
import org.rcfaces.core.component.capability.IRowToolTipIdCapability;
import org.rcfaces.core.component.capability.ISelectedValuesCapability;
import org.rcfaces.core.component.capability.IShowValueCapability;
import org.rcfaces.core.component.capability.ISortEventCapability;
import org.rcfaces.core.component.capability.IToolTipIdCapability;
import org.rcfaces.core.component.iterator.IColumnIterator;
import org.rcfaces.core.event.PropertyChangeEvent;
import org.rcfaces.core.internal.capability.IAdditionalInformationComponent;
import org.rcfaces.core.internal.capability.ICellClickableSettings;
import org.rcfaces.core.internal.capability.ICellImageSettings;
import org.rcfaces.core.internal.capability.ICheckComponent;
import org.rcfaces.core.internal.capability.ICheckRangeComponent;
import org.rcfaces.core.internal.capability.ICriteriaConfiguration;
import org.rcfaces.core.internal.capability.ICriteriaContainer;
import org.rcfaces.core.internal.capability.IDraggableGridComponent;
import org.rcfaces.core.internal.capability.IDroppableGridComponent;
import org.rcfaces.core.internal.capability.IGridComponent;
import org.rcfaces.core.internal.capability.ISelectionComponent;
import org.rcfaces.core.internal.capability.ISelectionRangeComponent;
import org.rcfaces.core.internal.component.Properties;
import org.rcfaces.core.internal.renderkit.IComponentData;
import org.rcfaces.core.internal.renderkit.IComponentRenderContext;
import org.rcfaces.core.internal.renderkit.IEventData;
import org.rcfaces.core.internal.renderkit.IProcessContext;
import org.rcfaces.core.internal.renderkit.IRenderContext;
import org.rcfaces.core.internal.renderkit.IRequestContext;
import org.rcfaces.core.internal.renderkit.WriterException;
import org.rcfaces.core.internal.tools.AdditionalInformationTools;
import org.rcfaces.core.internal.tools.ArrayIndexesModel;
import org.rcfaces.core.internal.tools.CheckTools;
import org.rcfaces.core.internal.tools.CollectionTools;
import org.rcfaces.core.internal.tools.CriteriaTools;
import org.rcfaces.core.internal.tools.FilterExpressionTools;
import org.rcfaces.core.internal.tools.FilteredDataModel;
import org.rcfaces.core.internal.tools.GridServerSort;
import org.rcfaces.core.internal.tools.SelectionTools;
import org.rcfaces.core.internal.tools.ValuesTools;
import org.rcfaces.core.lang.provider.ICursorProvider;
import org.rcfaces.core.lang.provider.ISelectionProvider;
import org.rcfaces.core.model.IClientDataModel;
import org.rcfaces.core.model.IClientModel.IContentIndex;
import org.rcfaces.core.model.IComponentRefModel;
import org.rcfaces.core.model.IFilterProperties;
import org.rcfaces.core.model.IFiltredModel;
import org.rcfaces.core.model.IIndexesModel;
import org.rcfaces.core.model.IRangeDataModel;
import org.rcfaces.core.model.IRangeDataModel2;
import org.rcfaces.core.model.ISelectedCriteria;
import org.rcfaces.core.model.ISortedComponent;
import org.rcfaces.core.model.ISortedDataModel;
import org.rcfaces.core.model.ITransactionalDataModel;
import org.rcfaces.renderkit.html.internal.HtmlTools;
import org.rcfaces.renderkit.html.internal.HtmlValuesTools;
import org.rcfaces.renderkit.html.internal.IHtmlComponentRenderContext;
import org.rcfaces.renderkit.html.internal.IHtmlComponentWriter;
import org.rcfaces.renderkit.html.internal.IHtmlWriter;
import org.rcfaces.renderkit.html.internal.IJavaScriptRenderContext;
import org.rcfaces.renderkit.html.internal.IJavaScriptWriter;
import org.rcfaces.renderkit.html.internal.IObjectLiteralWriter;
import org.rcfaces.renderkit.html.internal.JavaScriptClasses;
import org.rcfaces.renderkit.html.internal.ns.XhtmlNSAttributes;
import org.rcfaces.renderkit.html.internal.service.DataGridService;
import org.rcfaces.renderkit.html.internal.service.DataGridUpdateBehaviorListener;
import org.rcfaces.renderkit.html.internal.util.ClientDataModelTools;

/**
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.8 $ $Date: 2013/12/19 15:46:46 $
 */
@XhtmlNSAttributes({ "cellTextWrap", "asyncRender", "cursorValue", "showValue",
        "rowLabelColumnId" })
public class DataGridRenderer extends AbstractGridRenderer {

    private static final Log LOG = LogFactory.getLog(DataGridRenderer.class);

    private static final Map<String, String> SORT_ALIASES = new HashMap<String, String>(
            8);

    static {
        SORT_ALIASES.put(ISortEventCapability.SORT_INTEGER,
                "f_dataGrid.Sort_Integer");
        SORT_ALIASES.put(ISortEventCapability.SORT_NUMBER,
                "f_dataGrid.Sort_Number");
        SORT_ALIASES.put(ISortEventCapability.SORT_ALPHA,
                "f_dataGrid.Sort_Alpha");
        SORT_ALIASES.put(ISortEventCapability.SORT_ALPHA_IGNORE_CASE,
                "f_dataGrid.Sort_AlphaIgnoreCase");
        SORT_ALIASES
                .put(ISortEventCapability.SORT_TIME, "f_dataGrid.Sort_Time");
        SORT_ALIASES
                .put(ISortEventCapability.SORT_DATE, "f_dataGrid.Sort_Date");
        SORT_ALIASES.put(ISortEventCapability.SORT_SERVER, SORT_SERVER_COMMAND);
    }

    private static final String CLIENT_DB_ENABLED_PROPERTY = "org.rcfaces.html.CLIENT_DB_ENABLED";

    private static final String CLIENT_DB_REQUIRES_VB_PROPERTY = "org.rcfaces.html.CLIENT_DB_REQUIRES_VB";

    private static final String DEFAULT_CONTENT_PRIMARY_KEY = "value";

    protected String getJavaScriptClassName() {
        return JavaScriptClasses.DATA_GRID;
    }

    protected ICssStyleClasses createStyleClasses(IHtmlWriter htmlWriter) {
        ICssStyleClasses cssStyleClasses = super.createStyleClasses(htmlWriter);

        IGridComponent dg = (IGridComponent) htmlWriter
                .getComponentRenderContext().getComponent();

        if (dg instanceof DataGridComponent) {
            if (((DataGridComponent) dg).isCellTextWrap(htmlWriter
                    .getComponentRenderContext().getFacesContext())) {

                cssStyleClasses.addSpecificStyleClass(GRID_WRAP_CLASSNAME);
            }
        }

        return cssStyleClasses;
    }

    protected boolean needAdditionalInformationContextState() {
        return true;
    }

    protected void encodeBodyBegin(IHtmlWriter htmlWriter,
            AbstractGridRenderContext data) throws WriterException {

        if (serverTitleGeneration() == false) {
            encodeBodyTableEnd(htmlWriter, data);
        }
        super.encodeBodyBegin(htmlWriter, data);
       
    }

    protected void encodeBodyEnd(IHtmlWriter htmlWriter,
            AbstractGridRenderContext gridRenderContext) throws WriterException {
        encodeBodyTableEnd(htmlWriter, gridRenderContext);
    }

    protected void writeFullStates(IJavaScriptWriter jsWriter,
            AbstractGridRenderContext context, String jsCommand, Set objects)
            throws WriterException {
        if (objects == null || objects.isEmpty()) {
            return;
        }

        DataGridRenderContext dataGridRenderContext = (DataGridRenderContext) context;

        FacesContext facesContext = jsWriter.getFacesContext();
        UIColumn rowValueColumnComponent = dataGridRenderContext
                .getRowValueColumn();

        jsWriter.writeMethodCall(jsCommand).write('[');
        int i = 0;
        for (Iterator it = objects.iterator(); it.hasNext();) {
            Object value = it.next();

            String convert = convertValue(facesContext,
                    rowValueColumnComponent, value);

            if (convert == null) {
                continue;
            }

            if (i > 0) {
                jsWriter.write(',');
            }

            jsWriter.writeString(convert);

            i++;
        }

        jsWriter.writeln("]);");
    }

    protected UIColumn getRowValueColumn(IGridComponent dg) {
        DataGridComponent dataGridComponent = (DataGridComponent) dg;

        String rowValueColumnId = dataGridComponent.getRowValueColumnId();
        if (rowValueColumnId != null) {
            for (IColumnIterator it = dg.listColumns(); it.hasNext();) {
                UIColumn column = it.next();
                if (rowValueColumnId.equals(column.getId()) == false) {
                    continue;
                }

                return column;
            }

            throw new FacesException("Can not find column '" + rowValueColumnId
                    + "'.");
        }

        return null;
    }

    protected UIColumn getKeySearchColumn(IGridComponent dg) {
        if ((dg instanceof IKeySearchColumnIdCapability) == false) {
            return null;
        }

        String keySearchColumnId = ((IKeySearchColumnIdCapability) dg)
                .getKeySearchColumnId();
        if (keySearchColumnId == null) {
            return null;
        }

        for (IColumnIterator it = dg.listColumns(); it.hasNext();) {
            UIColumn column = it.next();
            if (keySearchColumnId.equals(column.getId()) == false) {
                continue;
            }

            return column;
        }

        throw new FacesException("Can not find column '" + keySearchColumnId
                + "'.");
    }

    protected void encodeJsColumns(IJavaScriptWriter htmlWriter,
            AbstractGridRenderContext gridRenderContext) throws WriterException {
        encodeJsColumns(htmlWriter, gridRenderContext, GENERATE_CELL_IMAGES);
    }

    protected void encodeJsColumns(IJavaScriptWriter jsWriter,
            AbstractGridRenderContext tableContext, int generatorMask)
            throws WriterException {

        if (serverTitleGeneration() == false) {
            generatorMask |= GENERATE_CELL_TEXT | GENERATE_CELL_WIDTH;
        }

        super.encodeJsColumns(jsWriter, tableContext, generatorMask);
    }

    protected void writeGridColumnProperties(IObjectLiteralWriter objectWriter,
            AbstractGridRenderContext tableContext, UIColumn columnComponent,
            int columnIndex) throws WriterException {

        super.writeGridColumnProperties(objectWriter, tableContext,
                columnComponent, columnIndex);

        DataGridRenderContext dataGridRenderContext = (DataGridRenderContext) tableContext;

        UIColumn rowValueColumn = dataGridRenderContext.getRowValueColumn();
        if (rowValueColumn == columnComponent) {
            objectWriter.writeSymbol("_valueColumn").writeBoolean(true);
        }

        UIColumn keySearchColumn = dataGridRenderContext.getKeySearchColumn();
        if (keySearchColumn == columnComponent) {
            objectWriter.writeSymbol("_keySearch").writeBoolean(true);
        }

        if (dataGridRenderContext.isAllClickableCell(columnIndex)) {
            objectWriter.writeSymbol("_cellClickable").writeBoolean(true);
        }
    }

    protected void encodeJsBody(IJavaScriptWriter jsWriter,
            AbstractGridRenderContext tableContext) throws WriterException {

        super.encodeJsBody(jsWriter, tableContext);

        if (ENABLE_SERVER_REQUEST) {
            String interactiveComponentClientId = jsWriter
                    .getHtmlComponentRenderContext().getHtmlRenderContext()
                    .getCurrentInteractiveRenderComponentClientId();

            if (interactiveComponentClientId != null) {
                return;
            }
        }

        encodeJsBodyRows(jsWriter, tableContext);
    }

    protected void encodeJsBodyRows(IJavaScriptWriter jsWriter,
            AbstractGridRenderContext tableContext) throws WriterException {

        IJavaScriptRenderContext javascriptRenderContext = jsWriter
                .getHtmlComponentRenderContext().getHtmlRenderContext()
                .getJavaScriptRenderContext();

        String rowVarName = javascriptRenderContext.allocateVarName();
        tableContext.setRowVarName(rowVarName);

        encodeJsTransactionalRows(jsWriter,
                (DataGridRenderContext) tableContext, true, false);
    }

    public void encodeJsTransactionalRows(IJavaScriptWriter jsWriter,
            DataGridRenderContext tableContext, boolean sendFullStates,
            boolean unknownRowCount) throws WriterException {

        DataModel dataModel = tableContext.getDataModel();

        ITransactionalDataModel transactionalDataModel = getAdapter(
                ITransactionalDataModel.class, dataModel);

        if (transactionalDataModel == null) {
            encodeJsRows(jsWriter, tableContext, sendFullStates,
                    unknownRowCount);
            return;
        }

        try {
            transactionalDataModel.enableTransactionalObjects(true);

            encodeJsRows(jsWriter, tableContext, sendFullStates,
                    unknownRowCount);

        } finally {
            transactionalDataModel.enableTransactionalObjects(false);
        }
    }

    private void encodeJsRows(IJavaScriptWriter jsWriter,
            DataGridRenderContext tableContext, boolean sendFullStates,
            boolean unknownRowCount) throws WriterException {

        FacesContext facesContext = jsWriter.getFacesContext();

        IGridComponent gridComponent = tableContext.getGridComponent();
        DataModel dataModel = tableContext.getDataModel();

        boolean filtred = false;
        int firstRowCount = tableContext.getRowCount();

        IComponentRefModel componentRefModel = getAdapter(
                IComponentRefModel.class, dataModel);

        if (componentRefModel != null) {
            componentRefModel.setComponent((UIComponent) gridComponent);
        }

        IFilterProperties filtersMap = tableContext.getFiltersMap();
        IFiltredModel filtredDataModel = getAdapter(IFiltredModel.class,
                dataModel);
        if (filtersMap != null) {
            if (filtredDataModel != null) {

                filtredDataModel.setFilter(filtersMap);
                tableContext.updateRowCount();

                filtred = true;

            } else {
                dataModel = FilteredDataModel.filter(dataModel, filtersMap);
                tableContext.updateRowCount();
            }

        } else if (filtredDataModel != null) {

            filtredDataModel.setFilter(FilterExpressionTools.EMPTY);
            tableContext.updateRowCount();

            filtred = true;
        }

        int rows = tableContext.getForcedRows();
        if (rows < 1) {
            rows = tableContext.getRows();
        }

        boolean searchEnd = (rows > 0);
        // int firstCount = -1;
        int count = -1;
        int fullCriteriaRowCount = -1;
        if (searchEnd) {
            count = firstRowCount;
        }

        int sortTranslations[] = null;

        ISortedComponent sortedComponents[] = tableContext
                .listSortedComponents();
        ISortedDataModel sortedDataModel = getAdapter(ISortedDataModel.class,
                dataModel, sortedComponents);
        if (sortedComponents != null && sortedComponents.length > 0) {

            if (sortedDataModel != null) {
                // On delegue au modele, le tri !

                // Nous devons être OBLIGATOIREMENT en mode rowValueColumnId
                if (tableContext.getRowValueColumn() == null) {
                    throw new FacesException(
                            "Can not sort dataModel without attribute rowValueColumnId specified !");
                }

                sortedDataModel.setSortParameters((UIComponent) gridComponent,
                        sortedComponents);
            } else {
                // Il faut faire le tri à la main !

                sortTranslations = GridServerSort.computeSortedTranslation(
                        facesContext, gridComponent, dataModel,
                        sortedComponents);
            }

            // Apres le tri, on connait peut etre la taille
            tableContext.updateRowCount();
        } else {

            if (sortedDataModel != null) {
                // Reset des parametres de tri !
                sortedDataModel.setSortParameters((UIComponent) gridComponent,
                        null);
            }
        }

        ISelectedCriteria[] selectedCriteria = tableContext
                .listSelectedCriteria();
        if (selectedCriteria != null && selectedCriteria.length == 0) {
            selectedCriteria = null;
        }

        int rowIndex = tableContext.getFirst();
        
        int newFirst = -1; // Dans les cas critiques ou on retourne à la page 1

        IRangeDataModel rangeDataModel = getAdapter(IRangeDataModel.class,
                dataModel, null);
        IRangeDataModel2 rangeDataModel2 = getAdapter(IRangeDataModel2.class,
                dataModel, null);

        if ((rangeDataModel != null || rangeDataModel2 != null) && rows >= 0) {
            // Initializer le IRandgeDataModel avant la
            // selection/check/additionnal
            // informations !
            if (sortTranslations == null && selectedCriteria == null) {
                // Specifie le range que si il n'y a pas de tri !

                int rangeLength = rows;
                if (searchEnd) {
                    // On regardera si il y a bien une suite ...
                    rangeLength++;
                }

                if (LOG.isDebugEnabled()) {
                    LOG.debug("Encode set range rowIndex='" + rowIndex
                            + "' rangeLength='" + rangeLength + "'.");
                }

                if (rangeDataModel != null) {
                    rangeDataModel.setRowRange(rowIndex, rangeLength);
                }

                if (rangeDataModel2 != null) {
                    rangeDataModel2.setRowRange(rowIndex, rangeLength,
                            searchEnd);
                }

            } else {
                // TOUT
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Encode set range to ALL => rowIndex='" + 0
                            + "' rangeLength='" + rows + "'.");
                }

                if (rangeDataModel != null) {
                    rangeDataModel.setRowRange(0, rows);
                }

                if (rangeDataModel2 != null) {
                    rangeDataModel2.setRowRange(0, rows, searchEnd);
                }

            }
        }

        if (selectedCriteria != null) {
            searchEnd = true; // On force la recherche de la fin
        }

        int selectedIndexes[] = null;
        int selectedOffset = -1;
        Set selectedObjects = null;

        if (tableContext.isSelectable()
                && (tableContext.getClientSelectionFullState() == IClientFullStateCapability.NONE_CLIENT_FULL_STATE || sendFullStates)) {

            Object selectionModel = ((ISelectedValuesCapability) gridComponent)
                    .getSelectedValues();

            if (LOG.isDebugEnabled()) {
                LOG.debug("Encode selectionModel='" + selectionModel + "'.");
            }

            if (selectionModel != null) {
                if (selectionModel instanceof IIndexesModel) {
                    selectedIndexes = ((IIndexesModel) selectionModel)
                            .listSortedIndexes();

                    if (tableContext.getClientSelectionFullState() != IClientFullStateCapability.NONE_CLIENT_FULL_STATE) {
                        writeFullStates(jsWriter, "f_setSelectionStates",
                                selectedIndexes);
                        selectedIndexes = null;

                    } else {
                        if (sortTranslations == null) {
                            // Dans le cas ou il n'y a pas de tri
                            // Les indexes sont lineaires ...

                            if (selectedIndexes != null
                                    && selectedIndexes.length > 0) {
                                // Recherche du premier RowOffset
                                for (int i = 0; i < selectedIndexes.length; i++) {
                                    if (selectedIndexes[i] >= rowIndex) {
                                        selectedOffset = i;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                } else {
                    selectedObjects = CollectionTools.valuesToSet(
                            selectionModel, true);

                    if (tableContext.getClientSelectionFullState() != IClientFullStateCapability.NONE_CLIENT_FULL_STATE) {
                        writeFullStates(jsWriter, tableContext,
                                "f_setSelectionStates", selectedObjects);
                        selectedObjects = null;
                    }
                }
            }
        }

        int checkedIndexes[] = null;
        int checkedOffset = -1;
        Set checkedObjects = null;

        if (tableContext.isCheckable()
                && (tableContext.getClientCheckFullState() == IClientFullStateCapability.NONE_CLIENT_FULL_STATE || sendFullStates)) {

            Object checkModel = ((ICheckedValuesCapability) gridComponent)
                    .getCheckedValues();

            if (LOG.isDebugEnabled()) {
                LOG.debug("Encode checkModel='" + checkModel + "'.");
            }

            if (checkModel != null) {
                if (checkModel instanceof IIndexesModel) {
                    checkedIndexes = ((IIndexesModel) checkModel)
                            .listSortedIndexes();

                    if (tableContext.getClientCheckFullState() != IClientFullStateCapability.NONE_CLIENT_FULL_STATE) {
                        writeFullStates(jsWriter, "f_setCheckStates",
                                checkedIndexes);
                        checkedIndexes = null;

                    } else {
                        if (sortTranslations == null) {
                            // Dans le cas ou il n'y a pas de tri
                            // Les indexes sont donc lineaires ...

                            if (checkedIndexes != null
                                    && checkedIndexes.length > 0) {
                                // Recherche du premier RowOffset
                                for (int i = 0; i < checkedIndexes.length; i++) {
                                    if (checkedIndexes[i] >= rowIndex) {
                                        checkedOffset = i;
                                        break;
                                    }
                                }
                            }
                        }
                    }

                } else {
                    checkedObjects = CollectionTools.valuesToSet(checkModel,
                            true);

                    if (tableContext.getClientCheckFullState() != IClientFullStateCapability.NONE_CLIENT_FULL_STATE) {
                        writeFullStates(jsWriter, tableContext,
                                "f_setCheckStates", checkedObjects);
                        checkedObjects = null;
                    }
                }
            }
        }

        int additionalIndexes[] = null;
        int additionalOffset = -1;
        Set additionalObjects = null;

        UIColumn rowValueColumn = tableContext.getRowValueColumn();

        if (tableContext.hasAdditionalInformations()) {

            // Meme si c'est en fullstate il faut envoyer le body du cartouche !

            Object additionalModel = ((IAdditionalInformationValuesCapability) gridComponent)
                    .getAdditionalInformationValues();

            String showAdditionalValues = tableContext
                    .getRequestShowAdditionalValues();
            String hideAdditionalValues = tableContext
                    .getRequestHideAdditionalValues();
            if (showAdditionalValues != null || hideAdditionalValues != null) {
                additionalModel = updateAdditionalValues(facesContext,
                        gridComponent, rowValueColumn, additionalModel,
                        showAdditionalValues, hideAdditionalValues);
            }

            if (LOG.isDebugEnabled()) {
                LOG.debug("Encode additionalModel='" + additionalModel + "'.");
            }

            if (additionalModel != null) {
                if (additionalModel instanceof IIndexesModel) {
                    additionalIndexes = ((IIndexesModel) additionalModel)
                            .listSortedIndexes();

                    if (tableContext.getClientAdditionalFullState() != IClientFullStateCapability.NONE_CLIENT_FULL_STATE) {
                        writeFullStates(jsWriter,
                                "f_setAdditionalInformationStates",
                                additionalIndexes);
                        additionalIndexes = null;

                    } else {
                        if (sortTranslations == null) {
                            // Dans le cas ou il n'y a pas de tri
                            // Les indexes sont donc lineaires ...

                            if (additionalIndexes != null
                                    && additionalIndexes.length > 0) {
                                // Recherche du premier RowOffset
                                for (int i = 0; i < additionalIndexes.length; i++) {
                                    if (additionalIndexes[i] >= rowIndex) {
                                        additionalOffset = i;
                                        break;
                                    }
                                }
                            }
                        }
                    }

                } else {
                    additionalObjects = CollectionTools.valuesToSet(
                            additionalModel, true);

                    if (tableContext.getClientAdditionalFullState() != IClientFullStateCapability.NONE_CLIENT_FULL_STATE) {
                        writeFullStates(jsWriter, tableContext,
                                "f_setAdditionalInformationStates",
                                additionalObjects);
                        additionalObjects = null;
                    }
                }
            }
        }

        UIColumn columns[] = tableContext.listColumns();
        boolean testImageUrls[] = new boolean[columns.length];
        for (int i = 0; i < columns.length; i++) {
            UIColumn column = columns[i];

            if (column instanceof ICellImageSettings) {
                testImageUrls[i] = ((ICellImageSettings) column)
                        .isCellImageURLSetted();
            }
        }

        // On recherche la taille ?
        if (searchEnd) {
            count = tableContext.getRowCount();

            // Le tri a été fait coté serveur,
            // On connait peut être le nombre d'elements !
            if (/* count < 0 && Pour les CRITERES */sortTranslations != null) {
                count = sortTranslations.length;
            }

            if (count >= 0 && selectedCriteria == null) {
                searchEnd = false;
            }
        }

        Map<String, Object> varContext = facesContext.getExternalContext()
                .getRequestMap();
        String rowCountVar = tableContext.getRowCountVar();
        if (rowCountVar != null) {
            varContext.put(rowCountVar, new Integer(count));
        }

        String rowIndexVar = tableContext.getRowIndexVar();

        boolean designerMode = tableContext.isDesignerMode();

        if (LOG.isTraceEnabled()) {
            LOG.trace("Encode grid componentId='"
                    + ((UIComponent) gridComponent).getId() + "' rowIndexVar='"
                    + rowIndexVar + "' designerMode='" + designerMode
                    + "' rowCountVar='" + rowCountVar + "' searchEnd="
                    + searchEnd + " count=" + count + " rows='" + rows + "'.");
        }

        try {
            boolean selected = false;
            boolean checked = false;
            boolean additional = false;
            String rowId = null;

            jsWriter.writeMethodCall("f_preAddRow2").writeln(");");

            int rowValueColumnIndex = -1;
            if (designerMode && rowValueColumn != null) {
                for (int i = 0; i < columns.length; i++) {
                    UIColumn dataColumnComponent = columns[i];
                    if (dataColumnComponent != rowValueColumn) {
                        continue;
                    }

                    rowValueColumnIndex = i;
                    break;
                }

            }
            int criteriaRowCountFirst = 0;
            if (selectedCriteria != null) {

                // On repositionne le FIRST !
                int targetFirst = rowIndex;
                rowIndex = 0;
                for (int i = 0; i < targetFirst;) {

                    int translatedRowIndex = rowIndex;

                    if (sortTranslations != null) {
                        if (translatedRowIndex >= sortTranslations.length) {
                            break;
                        }

                        translatedRowIndex = sortTranslations[translatedRowIndex];
                    }

                    gridComponent.setRowIndex(translatedRowIndex);
                    boolean available = gridComponent.isRowAvailable();
                    if (available == false) {
                    	 // Nous sommes à la fin du tableau, et il n'y a plus
                        // rien a afficher
                        // Finalement on retourne ligne 0
                        rowIndex = 0;
                        newFirst = 0;
                        break;
                    }

                    if (rowIndexVar != null) {
                        varContext.put(rowIndexVar, new Integer(i));
                    }

                    rowIndex++;

                    if (acceptCriteria(facesContext, gridComponent,
                            selectedCriteria) == false) {
                        continue;
                    }

                    i++;
                    criteriaRowCountFirst++;
                }
            }

            int criteriaRowCount = 0;
            for (int i = 0;; i++) {
                if (searchEnd == false) {
                    // Pas de recherche de la fin !
                    // On peut sortir tout de suite ...

                    if (selectedCriteria != null) {
                        if (rows > 0 && criteriaRowCount >= rows) {
                            break;
                        }

                    } else if (rows > 0 && i >= rows) {
                        break;
                    }
                }

                int translatedRowIndex = rowIndex;

                if (rowValueColumn != null) {
                    if (sortTranslations != null) {
                        if (rowIndex >= sortTranslations.length) {
                            if (selectedCriteria != null) {
                                fullCriteriaRowCount = criteriaRowCountFirst
                                        + criteriaRowCount;
                            }
                            break;
                        }

                        translatedRowIndex = sortTranslations[rowIndex];
                    }

                } else {

                    if (sortTranslations == null) {
                        if (selectedOffset >= 0) {
                            if (selectedIndexes[selectedOffset] == rowIndex) {
                                selected = true;

                                selectedOffset++;

                            } else {
                                selected = false;
                            }
                        }
                        if (checkedOffset >= 0) {
                            if (checkedIndexes[checkedOffset] == rowIndex) {
                                checked = true;

                                checkedOffset++;

                            } else {
                                checked = false;
                            }
                        }
                        if (additionalOffset >= 0) {
                            if (additionalIndexes[additionalOffset] == rowIndex) {
                                additional = true;

                                additionalOffset++;

                            } else {
                                additional = false;
                            }
                        }
                    } else {
                        if (rowIndex >= sortTranslations.length) {
                            if (selectedCriteria != null) {
                                fullCriteriaRowCount = criteriaRowCountFirst
                                        + criteriaRowCount;
                            }
                            break;
                        }

                        translatedRowIndex = sortTranslations[rowIndex];

                        if (selectedIndexes != null) {
                            selected = false;

                            for (int j = 0; j < selectedIndexes.length; j++) {
                                if (selectedIndexes[j] != translatedRowIndex) {
                                    continue;
                                }

                                selected = true;
                                break;
                            }
                        }
                        if (checkedIndexes != null) {
                            checked = false;
                            for (int j = 0; j < checkedIndexes.length; j++) {
                                if (checkedIndexes[j] != translatedRowIndex) {
                                    continue;
                                }

                                checked = true;
                                break;
                            }
                        }
                        if (additionalIndexes != null) {
                            additional = false;
                            for (int j = 0; j < additionalIndexes.length; j++) {
                                if (additionalIndexes[j] != translatedRowIndex) {
                                    continue;
                                }

                                additional = true;
                                break;
                            }
                        }
                    }
                }
                
                gridComponent.setRowIndex(translatedRowIndex);
                boolean available = gridComponent.isRowAvailable();
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Set row index " + translatedRowIndex
                            + " returns " + available + " (rowIndexVar="
                            + rowIndexVar + ")");
                }

                if (available == false) {
                    count = rowIndex;
                    fullCriteriaRowCount = criteriaRowCountFirst
                            + criteriaRowCount;
                    break;
                }

                if (searchEnd) {
                    // On teste juste la validité de la fin !
                    if (selectedCriteria != null) {
                        if (rows > 0 && criteriaRowCount >= rows) {
                            break;
                        }

                    } else if (rows > 0 && i >= rows) {
                        break;
                    }
                }

                if (rowIndexVar != null) {
                    varContext.put(rowIndexVar, new Integer(i));
                }

                if (selectedCriteria != null) {
                    if (acceptCriteria(facesContext, gridComponent,
                            selectedCriteria) == false) {
                        rowIndex++;
                        continue;
                    }
                    criteriaRowCount++;
                }

                if (rowValueColumn != null) {
                    Object value;

                    if (designerMode) {
                        String sd[] = (String[]) gridComponent.getRowData();
                        if (sd != null && sd.length > rowValueColumnIndex) {
                            value = sd[rowValueColumnIndex];

                        } else {
                            value = String.valueOf(i);
                        }

                        rowId = (String) value;

                    } else {
                        value = ((ValueHolder) rowValueColumn).getValue();

                        rowId = convertValue(facesContext, rowValueColumn,
                                value);
                    }

                    if (value != null) {
                        if (checkedObjects != null) {
                            checked = checkedObjects.contains(value);
                        }
                        if (selectedObjects != null) {
                            selected = selectedObjects.contains(value);
                        }
                        if (additionalObjects != null) {
                            additional = additionalObjects.contains(value);
                        }
                    }

                    if (rowId == null) {
                        throw new FacesException(
                                "Value associated to the row at index "
                                        + rowIndex + " is null !");
                    }
                }

                encodeJsRow(jsWriter, tableContext, i, rowId, rowIndex,
                        selected, checked, additional, translatedRowIndex);

                if (sortTranslations == null) {
                    if (selectedOffset >= 0
                            && selectedOffset >= selectedIndexes.length) {
                        selectedOffset = -1;
                        selected = false;
                    }
                    if (checkedOffset >= 0
                            && checkedOffset >= checkedIndexes.length) {
                        checkedOffset = -1;
                        checked = false;
                    }
                    if (additionalOffset >= 0
                            && additionalOffset >= additionalIndexes.length) {
                        additionalOffset = -1;
                        additional = false;
                    }
                }

                rowIndex++;
            }

            if (selectedCriteria != null) {
                if (gridComponent instanceof ICriteriaCountCapability) {
                    if (((ICriteriaCountCapability) gridComponent)
                            .isFullCriteriaCount()) {
                        fullCriteriaRowCount = criteriaRowCount
                                + criteriaRowCountFirst;
                        for (int i = rowIndex;; i++) {

                            int translatedRowIndex = i;

                            if (sortTranslations != null) {
                                if (translatedRowIndex >= sortTranslations.length) {
                                    break;
                                }

                                translatedRowIndex = sortTranslations[translatedRowIndex];
                            }

                            gridComponent.setRowIndex(translatedRowIndex);
                            boolean available = gridComponent.isRowAvailable();
                            if (available == false) {
                                // ???
                                break;
                            }

                            if (rowIndexVar != null) {
                                varContext.put(rowIndexVar, new Integer(i));
                            }

                            if (acceptCriteria(facesContext, gridComponent,
                                    selectedCriteria) == false) {
                                continue;
                            }
                            fullCriteriaRowCount++;
                        }
                    }
                }
            }

        } finally {
            gridComponent.setRowIndex(-1);

            if (rowCountVar != null) {
                varContext.remove(rowCountVar);
            }

            if (rowIndexVar != null) {
                varContext.remove(rowIndexVar);
            }

            jsWriter.writeMethodCall("f_postAddRow2").writeln(");");
        }

        // Le count a évolué ?
        // 2 solutions:
        // * mode page par page, nous sommes à la fin, ou il y a eu un tri
        // * en mode liste, le dataModel ne pouvait pas encore donner le nombre
        // de rows

        if (selectedCriteria != null && rows > 0) {
            encodeJsRowCount(jsWriter, tableContext, fullCriteriaRowCount,
                    newFirst);

        } else if ((unknownRowCount && firstRowCount >= 0)) {
            encodeJsRowCount(jsWriter, tableContext, count, newFirst);

        } else if (rows > 0) {
            if (count > firstRowCount
                    || (gridComponent.getFirst() == 0 && count == 0)) {
                encodeJsRowCount(jsWriter, tableContext, count, newFirst);
            }

        } else if (tableContext.getRowCount() < 0) {
            encodeJsRowCount(jsWriter, tableContext, rowIndex, newFirst);

        } else if (filtred) {
            if (searchEnd && count == 0) {
                encodeJsRowCount(jsWriter, tableContext, count, newFirst);
            }
        }
    }

    private boolean acceptCriteria(FacesContext facesContext,
            IGridComponent gridComponent,
            ISelectedCriteria[] selectedCriteriaArray) {

        for (ISelectedCriteria selectedCriteria : selectedCriteriaArray) {
            Set< ? > criteriaValues = selectedCriteria.listSelectedValues();
            if (criteriaValues == null || criteriaValues.isEmpty()) {
                continue;
            }

            ICriteriaConfiguration config = selectedCriteria.getConfig();

            Object dataValue = CriteriaTools.getDataValue(facesContext,
                    gridComponent, config);

            // if (dataValue == null) {
            // continue;
            // }

            if (criteriaValues.contains(dataValue) == false) {

                return false;
            }
        }

        return true;
    }

    private Object updateAdditionalValues(FacesContext facesContext,
            IGridComponent gridComponent, UIColumn rowValueColumn,
            Object additionalModel, String showAdditionalRows,
            String hideAdditionalRows) {

        if (rowValueColumn != null) {
            Set additionalValues = AdditionalInformationTools
                    .additionalInformationValuesToSet(facesContext,
                            (IAdditionalInformationComponent) gridComponent,
                            false);

            Set newAdditionalValues = updateValues(facesContext,
                    rowValueColumn, additionalValues, showAdditionalRows,
                    hideAdditionalRows);

            return newAdditionalValues;

        }

        IIndexesModel indexesModel = (IIndexesModel) additionalModel;
        if (indexesModel == null) {
            indexesModel = new ArrayIndexesModel();

        } else {
            indexesModel = indexesModel.copy();
        }

        if (HtmlTools.ALL_VALUE.equals(hideAdditionalRows)) {
            indexesModel.clearIndexes();

        } else {
            int uindexes[] = parseIndexes(hideAdditionalRows);
            for (int i = 0; i < uindexes.length; i++) {
                indexesModel.removeIndex(uindexes[i]);
            }
        }

        int cindexes[] = parseIndexes(showAdditionalRows);
        for (int i = 0; i < cindexes.length; i++) {
            indexesModel.addIndex(cindexes[i]);
        }

        return indexesModel;
    }

    protected void encodeJsRowCount(IJavaScriptWriter jsWriter,
            AbstractGridRenderContext tableContext, int count, int newFirst)
            throws WriterException {
        jsWriter.writeMethodCall("f_setRowCount").writeInt(count).writeln(");");
        if (newFirst >= 0) {
            jsWriter.writeMethodCall("_changeFirst").writeInt(newFirst)
                    .writeln(");");
        }
    }
    
    @SuppressWarnings("unused")
    protected void encodeJsRow(IJavaScriptWriter jsWriter,
            DataGridRenderContext tableContext, int index, String rowId,
            int iRowId, boolean selected, boolean checked, boolean additional,
            int rowIndex) throws WriterException {

        FacesContext facesContext = jsWriter.getFacesContext();
        IGridComponent dataGridComponent = tableContext.getGridComponent();
        UIColumn dcs[] = tableContext.listColumns();
        int columnNumber = dcs.length;

        String trClassName = null; // Pas d'évaluation pour chaque ligne

        String rowVarName = tableContext.getRowVarName();

        String values[] = null;
        if (ALLOCATE_ROW_STRINGS) {
            values = new String[columnNumber];
        }

        if (ALLOCATE_ROW_STRINGS == false) {
            if (index < 1) {
                jsWriter.write("var ");
            }
            jsWriter.write(rowVarName).write('=').writeMethodCall("f_addRow2");

            if (rowId != null) {
                jsWriter.writeString(rowId);
            } else if (rowIndex >= 0) {
                jsWriter.writeInt(rowIndex);
            } else {
                jsWriter.writeInt(iRowId);
            }

            jsWriter.write(',');

            IObjectLiteralWriter objectLiteralWriter = jsWriter
                    .writeObjectLiteral(true);

            if (selected
                    && tableContext.isSelectable()
                    && tableContext.getClientSelectionFullState() == IClientFullStateCapability.NONE_CLIENT_FULL_STATE) {
                objectLiteralWriter.writeSymbol("_selected").writeBoolean(true);
            }

            if (checked
                    && tableContext.isCheckable()
                    && tableContext.getClientCheckFullState() == IClientFullStateCapability.NONE_CLIENT_FULL_STATE) {
                objectLiteralWriter.writeSymbol("_checked").writeBoolean(true);
            }

            if (additional
                    && tableContext.hasAdditionalInformations()
                    && tableContext.getClientAdditionalFullState() == IClientFullStateCapability.NONE_CLIENT_FULL_STATE) {
                objectLiteralWriter.writeSymbol("_additional").writeBoolean(
                        true);
            }

            if (trClassName != null) {
                objectLiteralWriter.writeSymbol("_styleClass").write(
                        trClassName);
            }

            if (rowIndex >= 0) {
                objectLiteralWriter.writeSymbol("_rowIndex").writeInt(rowIndex);
            }

            objectLiteralWriter.end();
        }

        String images[] = null;
        String cellStyleClasses[] = null;
        String cellToolTipTexts[] = null;
        boolean clickableCells[] = null;
        int visibleColumns = 0;

        boolean designerMode = tableContext.isDesignerMode();
        String designerData[] = null;
        if (designerMode) {
            designerData = (String[]) dataGridComponent.getRowData();
        }

        boolean draggableRows = false;
        if (tableContext.isDraggable()) {
            if (dataGridComponent instanceof IDraggableGridComponent) {
                IDraggableGridComponent ggd = (IDraggableGridComponent) dataGridComponent;

                draggableRows = ggd.isRowDragEffectsSetted()
                        || ggd.isRowDragTypesSetted();
            }
        }

        boolean droppableRows = false;
        if (tableContext.isDroppable()) {
            if (dataGridComponent instanceof IDroppableGridComponent) {
                IDroppableGridComponent ggd = (IDroppableGridComponent) dataGridComponent;

                droppableRows = ggd.isRowDropEffectsSetted()
                        || ggd.isRowDropTypesSetted();
            }
        }

        for (int i = 0; i < columnNumber; i++) {
            UIColumn dc = dcs[i];

            int rowState = tableContext.getColumnState(i);
            if (rowState == AbstractGridRenderContext.SERVER_HIDDEN) {
                continue;
            }

            if (rowId == null || tableContext.getRowValueColumn() != dc) {
                String svalue = null;

                if (designerMode) {
                    if (designerData.length > i) {
                        svalue = designerData[i];
                    }

                } else if (dc instanceof ValueHolder) {
                    Object value = ((ValueHolder) dc).getValue();

                    if (value != null) {
                        svalue = convertValue(facesContext, dc, value);
                    }
                }

                if (ALLOCATE_ROW_STRINGS) {
                    if (svalue == null) {
                        svalue = NULL_VALUE;
                    }

                    values[i] = svalue;

                } else {
                    jsWriter.write(',').writeString(svalue);
                }
            }

            if (rowState != AbstractGridRenderContext.VISIBLE) {
                continue;
            }

            if (tableContext.isColumnImageURL(i)) {
                String imageURL = ((ICellImageCapability) dc).getCellImageURL();
                if (imageURL != null && imageURL.length() > 0) {
                    if (images == null) {
                        images = new String[columnNumber];
                    }

                    images[i] = tableContext.resolveImageURL(imageURL);
                }
            }

            if (tableContext.isCellStyleClass(i)) {
                String cs = ((ICellStyleClassCapability) dc)
                        .getCellStyleClass();
                if (cs != null && cs.length() > 0) {
                    if (cellStyleClasses == null) {
                        cellStyleClasses = new String[columnNumber];
                    }

                    cellStyleClasses[i] = cs;
                }
            }

            if (tableContext.isCellToolTipText(i)) {
                String ct = ((ICellToolTipTextCapability) dc)
                        .getCellToolTipText();
                if (ct != null && ct.length() > 0) {
                    if (cellToolTipTexts == null) {
                        cellToolTipTexts = new String[columnNumber];
                    }

                    cellToolTipTexts[i] = ct;
                }
            }

            boolean cCell = false;
            if (tableContext.isAllClickableCell(i) == false
                    && tableContext.isClickableCellSetted(i)) {
                ICellClickableCapability capability = (ICellClickableCapability) dc;

                cCell = ((ICellClickableCapability) dc).isCellClickable();
            }

            if (cCell) {
                if (clickableCells == null) {
                    clickableCells = new boolean[columnNumber];
                }

                clickableCells[i] = true;
            }

            visibleColumns++;
        }

        if (ALLOCATE_ROW_STRINGS) {

            ToolTipComponent tooltipComponent = null;

            if (dataGridComponent instanceof IRowToolTipIdCapability) {
                String tooltipClientId = ((IRowToolTipIdCapability) dataGridComponent)
                        .getRowToolTipId();

                if (tooltipClientId != null && tooltipClientId.length() > 0) {
                    IRenderContext renderContext = jsWriter
                            .getHtmlRenderContext();

                    if (tooltipClientId.charAt(0) != ':') {
                        tooltipClientId = renderContext
                                .computeBrotherComponentClientId(
                                        (UIComponent) dataGridComponent,
                                        tooltipClientId);
                    }

                    if (tooltipClientId != null) {
                        UIComponent comp = renderContext.getFacesContext()
                                .getViewRoot().findComponent(tooltipClientId);
                        if (comp instanceof ToolTipComponent) {
                            tooltipComponent = (ToolTipComponent) comp;

                            tableContext.registerTooltip(tooltipComponent);

                            if (tooltipComponent.isRendered() == false) {
                                tooltipComponent = null;
                            }
                        }
                    }
                }
            }

            if (tooltipComponent == null) {
                tooltipComponent = tableContext.findTooltipByIdOrName(
                        jsWriter.getComponentRenderContext(),
                        (UIComponent) dataGridComponent, "#row", null);
            }

            String tooltipId = null;
            String tooltipContent = null;
            if (tooltipComponent != null) {
                if (tooltipComponent.getAsyncRenderMode(facesContext) == IAsyncRenderModeCapability.NONE_ASYNC_RENDER_MODE) {
                    tooltipContent = encodeToolTip(jsWriter, tooltipComponent);
                    tooltipId = jsWriter.allocateString("##CONTENT");

                } else {
                    tooltipId = tooltipComponent.getClientId(facesContext);
                }
            }

            if (tooltipId != null) {
                tooltipId = jsWriter.allocateString(tooltipId);
            }
            if (tooltipContent != null) {
                tooltipContent = jsWriter.allocateString(tooltipContent);
            }

            for (int i = 0; i < values.length; i++) {
                String v = values[i];
                if (v == null || v == NULL_VALUE) {
                    continue;
                }

                values[i] = jsWriter.allocateString(v);
            }

            if (index < 1) {
                jsWriter.write("var ");
            }
            jsWriter.write(rowVarName).write('=').writeMethodCall("f_addRow2");

            if (rowId != null) {
                jsWriter.writeString(rowId);

            } else if (rowIndex >= 0) {
                jsWriter.writeInt(rowIndex);

            } else {
                jsWriter.writeInt(iRowId);
            }

            jsWriter.write(',');

            IObjectLiteralWriter objectLiteralWriter = jsWriter
                    .writeObjectLiteral(true);

            if (selected
                    && tableContext.isSelectable()
                    && tableContext.getClientSelectionFullState() == IClientFullStateCapability.NONE_CLIENT_FULL_STATE) {
                objectLiteralWriter.writeSymbol("_selected").writeBoolean(true);
            }

            if (checked
                    && tableContext.isCheckable()
                    && tableContext.getClientCheckFullState() == IClientFullStateCapability.NONE_CLIENT_FULL_STATE) {
                objectLiteralWriter.writeSymbol("_checked").writeBoolean(true);
            }

            if (rowIndex >= 0) {
                objectLiteralWriter.writeSymbol("_rowIndex").writeInt(rowIndex);
            }

            if (tooltipId != null) {
                objectLiteralWriter.writeSymbol("_toolTipId").write(tooltipId);

                if (tooltipContent != null) {
                    objectLiteralWriter.writeSymbol("_toolTipContent").write(
                            tooltipContent);
                }
            }

            if (tableContext.hasAdditionalInformations()) {
                AdditionalInformationComponent additionalInformationComponents[] = tableContext
                        .listAdditionalInformations();

                AdditionalInformationComponent additionalInformationComponent = null;

                for (int i = 0; i < additionalInformationComponents.length; i++) {
                    AdditionalInformationComponent add = additionalInformationComponents[i];

                    if (add.isRendered() == false) {
                        continue;
                    }

                    additionalInformationComponent = add;
                    break;
                }

                if (additionalInformationComponent != null) {
                    if (additional) {
                    	
                    	String content = encodeAdditionalInformation(jsWriter,
                                additionalInformationComponent);

                        if (content != null) {
                            objectLiteralWriter.writeSymbol(
                                    "_additionalContent").writeString(content);
                        }

                    }

                    String addtionalInformationHeight = additionalInformationComponent
                            .getHeight(facesContext);
                    if (addtionalInformationHeight != null) {
                        objectLiteralWriter.writeSymbol("_additionalHeight")
                                .writeString(addtionalInformationHeight);
                    }

                } else {
                    // Pas d'additional information ...
                    objectLiteralWriter.writeSymbol("_additionalContent")
                            .writeBoolean(false);
                }
            }

            if (trClassName != null) {
                objectLiteralWriter.writeSymbol("_styleClass").write(
                        trClassName);
            }

            if (draggableRows) {
                IDraggableGridComponent dgc = (IDraggableGridComponent) dataGridComponent;

                if (dgc.isRowDragTypesSetted()) {
                    String[] types = dgc.getRowDragTypes();
                    if (types != null && types.length > 0) {
                        objectLiteralWriter
                                .writeSymbol("_dragTypes")
                                .writeString(HtmlTools.serializeDnDTypes(types));
                    }
                }
                if (dgc.isRowDragEffectsSetted()) {
                    int effects = dgc.getRowDragEffects();

                    if (effects <= IDragAndDropEffects.UNKNOWN_DND_EFFECT) {
                        effects = IDragAndDropEffects.DEFAULT_DND_EFFECT;
                    }

                    objectLiteralWriter.writeSymbol("_dragEffects").writeInt(
                            effects);
                }
            }

            if (droppableRows) {
                IDroppableGridComponent dgc = (IDroppableGridComponent) dataGridComponent;

                if (dgc.isRowDropTypesSetted()) {
                    String[] types = dgc.getRowDropTypes();
                    if (types != null && types.length > 0) {
                        objectLiteralWriter
                                .writeSymbol("_dropTypes")
                                .writeString(HtmlTools.serializeDnDTypes(types));
                    }
                }
                if (dgc.isRowDropEffectsSetted()) {
                    int effects = dgc.getRowDropEffects();

                    if (effects <= IDragAndDropEffects.UNKNOWN_DND_EFFECT) {
                        effects = IDragAndDropEffects.DEFAULT_DND_EFFECT;
                    }

                    objectLiteralWriter.writeSymbol("_dropEffects").writeInt(
                            effects);
                }
            }

            objectLiteralWriter.end();

            for (int i = 0; i < values.length; i++) {
                String v = values[i];
                if (v == null) {
                    continue;
                }

                jsWriter.write(',');
                if (v == NULL_VALUE) {
                    jsWriter.writeNull();
                    continue;
                }

                jsWriter.write(v);
            }

        }
        jsWriter.writeln(");");

        String[] cellToolTipIds = null;
        String[] cellToolTipContents = null;

        for (int i = 0; i < columnNumber; i++) {
            if (cellToolTipTexts != null && cellToolTipTexts[i] != null) {
                continue;
            }

            String toolTipId = null;
            if (dcs[i] instanceof IToolTipIdCapability) {
                toolTipId = ((IToolTipIdCapability) dcs[i]).getToolTipId();
            }

            ToolTipComponent tooltipComponent = tableContext
                    .findTooltipByIdOrName(
                            jsWriter.getComponentRenderContext(), dcs[i],
                            toolTipId, (UIComponent) dataGridComponent);

            if (tooltipComponent == null) {
                continue;
            }

            String toolTipClientId = null;
            String toolTipContent = null;
            if (tooltipComponent.getAsyncRenderMode(facesContext) == IAsyncRenderModeCapability.NONE_ASYNC_RENDER_MODE) {
                toolTipContent = encodeToolTip(jsWriter, tooltipComponent);
                toolTipClientId = "##CONTENT";

            } else {
                toolTipClientId = tooltipComponent.getClientId(facesContext);

                // Le tooltip contient le numero de la ligne !!!!!
                int idx = toolTipClientId.lastIndexOf(':' + tooltipComponent
                        .getId());
                if (idx > 0) {
                    idx = toolTipClientId.lastIndexOf(':', idx - 1);
                    if (idx > 0) {
                        toolTipClientId = toolTipClientId.substring(0, idx + 1)
                                + tooltipComponent.getId();
                    }
                }

            }

            if (toolTipClientId != null) {
                if (cellToolTipIds == null) {
                    cellToolTipIds = new String[columnNumber];
                }
                cellToolTipIds[i] = jsWriter.allocateString(toolTipClientId);
            }
            if (toolTipContent != null) {
                if (cellToolTipContents == null) {
                    cellToolTipContents = new String[columnNumber];
                }
                cellToolTipContents[i] = jsWriter
                        .allocateString(toolTipContent);
            }
        }

        if (images != null || cellStyleClasses != null
                || cellToolTipIds != null || cellToolTipTexts != null
                || clickableCells != null) {

            allocateStrings(jsWriter, images, images);
            allocateStrings(jsWriter, cellStyleClasses, cellStyleClasses);
            allocateStrings(jsWriter, cellToolTipTexts, cellToolTipTexts);

            boolean setCells2 = false;
            IObjectLiteralWriter contWriter = null;
            for (int i = 0; i < columnNumber; i++) {
                if (tableContext.getColumnState(i) != AbstractGridRenderContext.VISIBLE) {
                    continue;
                }

                String imageURL = null;
                if (images != null) {
                    imageURL = images[i];
                }

                String cellStyleClass = null;
                if (cellStyleClasses != null) {
                    cellStyleClass = cellStyleClasses[i];
                }

                String toolTipText = null;
                if (cellToolTipTexts != null) {
                    toolTipText = cellToolTipTexts[i];
                }

                String toolTipId = null;
                if (cellToolTipIds != null) {
                    toolTipId = cellToolTipIds[i];
                }

                String toolTipContent = null;
                if (cellToolTipContents != null) {
                    toolTipContent = cellToolTipContents[i];
                }

                boolean clickableCell = false;
                if (clickableCells != null) {
                    clickableCell = clickableCells[i];
                }

                if (imageURL == null && cellStyleClass == null
                        && toolTipText == null && toolTipId == null
                        && toolTipContent == null && clickableCell == false) {
                    continue;
                }

                if (setCells2 == false) {
                    setCells2 = true;

                    jsWriter.writeMethodCall("f_setCells2").write(rowVarName);

                    jsWriter.write(',');
                }

                if (contWriter == null) {
                    contWriter = jsWriter.writeObjectLiteral(false);
                }
                contWriter.writeProperty(String.valueOf(i));

                IObjectLiteralWriter objWriter = contWriter.getParent()
                        .writeObjectLiteral(true);

                if (imageURL != null) {
                    objWriter.writeSymbol("_imageURL").write(imageURL);
                }

                if (cellStyleClass != null) {
                    objWriter.writeSymbol("_styleClass").write(cellStyleClass);
                }

                if (toolTipText != null) {
                    objWriter.writeSymbol("_toolTipText").write(toolTipText);
                }

                if (toolTipId != null) {
                    objWriter.writeSymbol("_toolTipId").write(toolTipId);
                }

                if (toolTipContent != null) {
                    objWriter.writeSymbol("_toolTipContent").write(
                            toolTipContent);
                }

                if (clickableCell) {
                    objWriter.writeSymbol("_clickable").writeBoolean(true);
                }

                objWriter.end();
            }

            if (contWriter != null) {
                contWriter.end();
            }
            if (setCells2) {
                jsWriter.writeln(");");
            }

        }
    }

    public AbstractGridRenderContext createTableContext(
            IHtmlComponentRenderContext componentRenderContext) {
        DataGridRenderContext tableContext = new DataGridRenderContext(
                componentRenderContext);

        return tableContext;
    }

    public DataGridRenderContext createTableContext(
            IProcessContext processContext,
            IJavaScriptRenderContext scriptRenderContext, IGridComponent dg,
            int rowIndex, int forcedRows, ISortedComponent sortedComponents[],
            String filterExpression, String showAdditional,
            String hideAdditional, ISelectedCriteria[] criteriaContainers) {

        DataGridRenderContext tableContext = new DataGridRenderContext(
                processContext, scriptRenderContext, dg, rowIndex, forcedRows,
                sortedComponents, filterExpression, showAdditional,
                hideAdditional, criteriaContainers);

        return tableContext;
    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.8 $ $Date: 2013/12/19 15:46:46 $
     */
    public class DataGridRenderContext extends AbstractGridRenderContext {

        private UIColumn rowValueColumn;

        private UIColumn keySearchColumn;

        private boolean[] clickableCellSettedArray;

        private boolean[] allClickableCellArray;

        public DataGridRenderContext(IProcessContext processContext,
                IJavaScriptRenderContext scriptRenderContext,
                IGridComponent dg, int rowIndex, int forcedRows,
                ISortedComponent[] sortedComponents, String filterExpression,
                String showAdditionals, String hideAdditionals,
                ISelectedCriteria[] criteriaContainers) {
            super(processContext, scriptRenderContext, dg, rowIndex,
                    forcedRows, sortedComponents, filterExpression,
                    showAdditionals, hideAdditionals, criteriaContainers);

            initializeDataGrid();
        }

        public DataGridRenderContext(
                IHtmlComponentRenderContext componentRenderContext) {
            super(componentRenderContext);

            initializeDataGrid();
        }

        protected void initializeDataGrid() {
            rowValueColumn = DataGridRenderer.this
                    .getRowValueColumn(getGridComponent());
            keySearchColumn = DataGridRenderer.this
                    .getKeySearchColumn(getGridComponent());

            clickableCellSettedArray = new boolean[columns.length];
            allClickableCellArray = new boolean[columns.length];

            int i = 0;
            for (UIColumn column : columns) {
                if (column instanceof ICellClickableCapability) {
                    allClickableCellArray[i] = ((ICellClickableCapability) column)
                            .isAllCellClickable();
                }

                if (column instanceof ICellClickableSettings) {
                    clickableCellSettedArray[i] = ((ICellClickableSettings) column)
                            .isCellClickableSetted();
                }

                i++;
            }
        }

        protected String convertAliasCommand(String command) {
            return SORT_ALIASES.get(command);
        }

        public UIColumn getRowValueColumn() {
            return rowValueColumn;
        }

        public UIColumn getKeySearchColumn() {
            return keySearchColumn;
        }

        public boolean isClickableCellSetted(int i) {
            return clickableCellSettedArray[i];
        }

        public boolean isAllClickableCell(int i) {
            return allClickableCellArray[i];
        }
    }

    protected void writeGridComponentAttributes(IHtmlWriter htmlWriter,
            AbstractGridRenderContext tableContext, IGridComponent dg)
            throws WriterException {

        super.writeGridComponentAttributes(htmlWriter, tableContext, dg);

        FacesContext facesContext = htmlWriter.getComponentRenderContext()
                .getFacesContext();

        if (dg instanceof DataGridComponent) {
            if (((DataGridComponent) dg).isCellTextWrap(facesContext)) {
                htmlWriter.writeAttributeNS("cellTextWrap", true);
            }
        }

        if (ENABLE_SERVER_REQUEST) {
            DataGridService dataGridServer = DataGridService
                    .getInstance(facesContext);
            if (dataGridServer != null) {
                htmlWriter.writeAttributeNS("asyncRender", true);
            }
        }

        if (dg instanceof ICursorProvider) {
            Object cursorValue = ((ICursorProvider) dg).getCursorValue();
            String clientCursorValue = null;

            if (cursorValue != null) {
                UIColumn rowValueColumn = ((DataGridRenderContext) tableContext)
                        .getRowValueColumn();

                if (rowValueColumn != null) {
                    clientCursorValue = ValuesTools.convertValueToString(
                            cursorValue, rowValueColumn, facesContext);
                } else {
                    clientCursorValue = String.valueOf(cursorValue);
                }
            }

            if (clientCursorValue != null) {
                htmlWriter.writeAttributeNS("cursorValue", clientCursorValue);
            }

        }

        if (dg instanceof IShowValueCapability) {
            Object showValue = ((IShowValueCapability) dg).getShowValue();
            String clientShowValue = null;

            if (showValue != null) {
                UIColumn rowValueColumn = ((DataGridRenderContext) tableContext)
                        .getRowValueColumn();

                if (rowValueColumn != null) {
                    clientShowValue = ValuesTools.convertValueToString(
                            showValue, rowValueColumn, facesContext);

                } else {
                    clientShowValue = String.valueOf(showValue);
                }
            }

            if (clientShowValue != null) {
                htmlWriter.writeAttributeNS("showValue", clientShowValue);
            }
        }

        if (dg instanceof DataGridComponent) {
            String columnId = ((DataGridComponent) dg)
                    .getRowLabelColumnId(facesContext);

            if (columnId != null) {
                htmlWriter.writeAttributeNS("rowLabelColumnId", columnId);
            }
        }

        String addOpenImageURL = getAdditionalInformationOpenImageURL(htmlWriter);
        String addCloseImageURL = getAdditionalInformationCloseImageURL(htmlWriter);
        if (addOpenImageURL != null && addCloseImageURL != null) {
            htmlWriter.writeAttributeNS("addOpenImageURL", addOpenImageURL);
            htmlWriter.writeAttributeNS("addCloseImageURL", addCloseImageURL);
        }

        writeClientDataModel(htmlWriter, tableContext.getDataModel(),
                tableContext);
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

        String contentIndexesString = ClientDataModelTools
                .format(clientDataModel);

        // htmlWriter.writeAttributeNS("indexedDb", true);
        htmlWriter.writeAttributeNS("idbName", contentName);
        htmlWriter.writeAttributeNS("idbKey", contentKey);
        if (contentRowCount >= 0) {
            htmlWriter.writeAttributeNS("idbCount", contentRowCount);
        }
        htmlWriter.writeAttributeNS("idbPK", contentPK);
        if (contentIndexesString != null) {
            htmlWriter.writeAttributeNS("idbIndex", contentIndexesString);
        }

        IContentIndex[] contentIndexes = clientDataModel.listContentIndexes();
        for (IContentIndex contentIndex : contentIndexes) {
            if (contentIndex.isIgnoreAccent()) {

                htmlWriter.getComponentRenderContext().setAttribute(
                        CLIENT_DB_REQUIRES_VB_PROPERTY, Boolean.TRUE);
            }
        }

        htmlWriter.getComponentRenderContext().setAttribute(
                CLIENT_DB_ENABLED_PROPERTY, Boolean.TRUE);
    }

    protected void encodeJavaScript(IJavaScriptWriter writer)
            throws WriterException {
    	
        super.encodeJavaScript(writer);
        
        if (writer.getComponentRenderContext().containsAttribute(
                CLIENT_DB_ENABLED_PROPERTY)) {
            writer.getJavaScriptRenderContext().appendRequiredClass(
                    "f_dataGrid", "indexDb");

            if (writer.getComponentRenderContext().containsAttribute(
                    CLIENT_DB_REQUIRES_VB_PROPERTY)) {
                writer.getJavaScriptRenderContext().appendRequiredClass("f_vb",
                        null);
            }
        }
    }
        
    protected void addUnlockProperties(Set<Serializable> unlockedProperties) {
        super.addUnlockProperties(unlockedProperties);

        unlockedProperties.add("selectedItems");
        unlockedProperties.add("deselectedItems");
        unlockedProperties.add("checkedItems");
        unlockedProperties.add("uncheckedItems");
        unlockedProperties.add("showAdditional");
        unlockedProperties.add("hideAdditional");
        unlockedProperties.add("cursor");
    }

    protected void decode(IRequestContext context, UIComponent component,
            IComponentData componentData) {

        FacesContext facesContext = context.getFacesContext();

        IGridComponent gridComponent = (IGridComponent) component;

        UIColumn rowValueColumn = getRowValueColumn(gridComponent);
        

        if (gridComponent instanceof ISelectionComponent) {
            String selectedRows = componentData
                    .getStringProperty("selectedItems");
            String deselectedRows = componentData
                    .getStringProperty("deselectedItems");
            if (selectedRows != null || deselectedRows != null) {

                if (LOG.isDebugEnabled()) {
                    LOG.debug("selectedItems=" + selectedRows
                            + "  deselectedItems=" + deselectedRows);
                }

                if (rowValueColumn != null) {
                    Set<Object> selectedValues = SelectionTools
                            .selectionValuesToSet(facesContext,
                                    (ISelectionComponent) gridComponent, false);

                    Set<Object> newSelectedValues = updateValues(facesContext,
                            rowValueColumn, selectedValues, selectedRows,
                            deselectedRows);

                    SelectionTools.setSelectionValues(facesContext,
                            (ISelectionProvider) gridComponent,
                            newSelectedValues);

                } else if (gridComponent instanceof ISelectionRangeComponent) {
                    int indexes[] = parseIndexes(selectedRows);
                    int dindexes[] = null;
                    boolean all = false;

                    if (HtmlTools.ALL_VALUE.equals(deselectedRows)) {
                        all = true;
                        dindexes = EMPTY_INDEXES;
                    } else {
                        dindexes = parseIndexes(deselectedRows);
                    }

                    if (indexes.length > 0 || all || dindexes.length > 0) {
                        setSelectedIndexes(facesContext,
                                (ISelectionRangeComponent) gridComponent,
                                indexes, dindexes, all);
                    }
                }
            }
        }

        if (gridComponent instanceof ICheckComponent) {
            String checkedRows = componentData
                    .getStringProperty("checkedItems");
            String uncheckedRows = componentData
                    .getStringProperty("uncheckedItems");

            if (LOG.isDebugEnabled()) {
                LOG.debug("Decode checkedItems=" + checkedRows
                        + "  uncheckedItems=" + uncheckedRows);
            }

            if (checkedRows != null || uncheckedRows != null) {

                if (rowValueColumn != null) {

                    Set<Object> checkedValues = CheckTools.checkValuesToSet(
                            facesContext, (ICheckComponent) gridComponent,
                            false);

                    Set<Object> newCheckedValues = updateValues(facesContext,
                            rowValueColumn, checkedValues, checkedRows,
                            uncheckedRows);

                    CheckTools.setCheckValues(facesContext,
                            (ICheckComponent) gridComponent, newCheckedValues);

                } else if (gridComponent instanceof ICheckRangeComponent) {
                    int cindexes[] = parseIndexes(checkedRows);
                    int uindexes[] = null;
                    boolean all = false;

                    if (HtmlTools.ALL_VALUE.equals(uncheckedRows)) {
                        all = true;
                        uindexes = EMPTY_INDEXES;

                    } else {
                        uindexes = parseIndexes(uncheckedRows);
                    }

                    if (cindexes.length > 0 || uindexes.length > 0 || all) {
                        setCheckedIndexes(facesContext,
                                (ICheckRangeComponent) gridComponent, cindexes,
                                uindexes, all);
                    }
                }
            }
        }

        if (gridComponent instanceof IAdditionalInformationComponent) {
            String showAdditionalRows = componentData
                    .getStringProperty("showAdditional");
            String hideAdditionalRows = componentData
                    .getStringProperty("hideAdditional");

            if (LOG.isDebugEnabled()) {
                LOG.debug("Decode showAdditional=" + showAdditionalRows
                        + "  hideAdditional=" + hideAdditionalRows);
            }

            if (showAdditionalRows != null || hideAdditionalRows != null) {
                if (rowValueColumn != null) {

                    Set<Object> additionalValues = AdditionalInformationTools
                            .additionalInformationValuesToSet(
                                    facesContext,
                                    (IAdditionalInformationComponent) gridComponent,
                                    false);

                    Set<Object> newAdditionalValues = updateValues(
                            facesContext, rowValueColumn, additionalValues,
                            showAdditionalRows, hideAdditionalRows);

                    AdditionalInformationTools.setAdditionalInformationValues(
                            facesContext,
                            (IAdditionalInformationComponent) gridComponent,
                            newAdditionalValues);

                } else {
                    int cindexes[] = parseIndexes(showAdditionalRows);
                    int uindexes[] = null;
                    boolean all = false;

                    if (HtmlTools.ALL_VALUE.equals(hideAdditionalRows)) {
                        all = true;
                        uindexes = EMPTY_INDEXES;

                    } else {
                        uindexes = parseIndexes(hideAdditionalRows);
                    }

                    if (cindexes.length > 0 || uindexes.length > 0 || all) {
                        setAdditionalIndexes(
                                facesContext,
                                (IAdditionalInformationComponent) gridComponent,
                                cindexes, uindexes, all);
                    }
                }
            }
        }

        String cursorValue = componentData.getStringProperty("cursor");
        if (cursorValue != null) {

            Object cursorValueObject = ValuesTools.convertStringToValue(
                    facesContext, rowValueColumn, cursorValue, false);

            Object oldCursorValueObject = ((ICursorProvider) gridComponent)
                    .getCursorValue();

            if (LOG.isDebugEnabled()) {
                LOG.debug("Decode cursorValue=" + cursorValue
                        + "  cursorValueObject=" + cursorValueObject
                        + " oldCursorValueObject=" + oldCursorValueObject);
            }

            if (isEquals(oldCursorValueObject, cursorValueObject) == false) {
                ((ICursorProvider) gridComponent)
                        .setCursorValue(cursorValueObject);

                component.queueEvent(new PropertyChangeEvent(component,
                        Properties.CURSOR_VALUE, oldCursorValueObject,
                        cursorValueObject));
            }
        }

        if (gridComponent instanceof ICriteriaManagerCapability) {

            ICriteriaManagerCapability manager = (ICriteriaManagerCapability) gridComponent;
            ICriteriaContainer[] oldSelectedContainers = manager
                    .listSelectedCriteriaContainers();

            Set<ICriteriaContainer> oldContainerSet = new HashSet<ICriteriaContainer>();
            if (oldSelectedContainers != null
                    && oldSelectedContainers.length > 0) {
                oldContainerSet.addAll(Arrays.asList(oldSelectedContainers));
            }
            String crit = componentData
                    .getStringProperty("selectedCriteriaColumns");
            if (crit != null) {
            	
                ISelectedCriteria[] selectedCriteria = null;
                String criteria_s = componentData
                        .getStringProperty("criteriaValues");
                if (criteria_s != null) {
                    selectedCriteria = CriteriaTools.computeCriteriaConfigs(
                            facesContext, (IGridComponent) component,
                            criteria_s);
                }

                if (selectedCriteria != null) {
                ICriteriaContainer[] newSelectedCriteria = new ICriteriaContainer[selectedCriteria.length];
                for (int i = 0; i < selectedCriteria.length; i++) {
                    ISelectedCriteria iSelectedCriteria = selectedCriteria[i];

                        Set<Object> values = iSelectedCriteria
                                .listSelectedValues();
                    SelectionTools.setSelectionValues(facesContext,
                            iSelectedCriteria.getConfig(), values);

                    ICriteriaContainer criteriaContainer = iSelectedCriteria
                            .getConfig().getCriteriaContainer();
                    newSelectedCriteria[i] = criteriaContainer;
                    oldContainerSet.remove(criteriaContainer);
                }

                component.queueEvent(new PropertyChangeEvent(component,
                        Properties.SELECTED_CRITERIA_COLUMNS,
                        oldSelectedContainers, newSelectedCriteria));

                manager.setSelectedCriteriaContainers(newSelectedCriteria);
                }

                for (ICriteriaContainer criteriaContainer : oldContainerSet) {
                    SelectionTools.setSelectionValues(facesContext,
                            criteriaContainer.getCriteriaConfiguration(),
                            Collections.emptySet());
                }

            }
        }
        
        super.decode(context, component, componentData);
      }
 

    private Set<Object> updateValues(FacesContext facesContext,
            UIColumn columnComponent, Set<Object> values, String selectedRows,
            String deselectedRows) {

        if (HtmlTools.ALL_VALUE.equals(deselectedRows)) {
            values.clear();

        } else if (values.isEmpty() == false && deselectedRows != null
                && deselectedRows.length() > 0) {
            List<Object> deselect = HtmlValuesTools.parseValues(facesContext,
                    columnComponent, true, false, deselectedRows);

            if (deselect.isEmpty() == false) {
                values.removeAll(deselect);
            }
        }

        if (selectedRows != null && selectedRows.length() > 0) {
            List<Object> select = HtmlValuesTools.parseValues(facesContext,
                    columnComponent, true, false, selectedRows);

            if (select.isEmpty() == false) {
                values.addAll(select);
            }

        }

        return values;
    }

    public Object decodeEventObject(IRequestContext requestContext,
            UIComponent component, IEventData eventData) {

        String value = eventData.getEventValue();
        if (value != null) {
            IGridComponent gridComponent = (IGridComponent) component;

            UIColumn rowValueColumn = getRowValueColumn(gridComponent);
            if (rowValueColumn != null) {
                List select = HtmlValuesTools.parseValues(
                        requestContext.getFacesContext(), rowValueColumn, true,
                        false, value);

                if (select.size() == 1) {
                    return select.get(0);
                }

            } else {
                int indexes[] = parseIndexes(value);

                if (indexes.length == 1) {
                    DataModel dataModel = gridComponent.getDataModelValue();

                    if (dataModel != null) {
                        dataModel.setRowIndex(indexes[0]);
                        try {

                            if (dataModel.isRowAvailable()) {
                                return dataModel.getRowData();
                            }

                        } finally {
                            dataModel.setRowIndex(-1);
                        }
                    }
                }
            }
        }

        return super.decodeEventObject(requestContext, component, eventData);
    }

    protected String getAdditionalInformationOpenImageURL(
            IHtmlComponentWriter writer) {
        return null;
    }

    protected String getAdditionalInformationCloseImageURL(
            IHtmlComponentWriter writer) {
        return null;
    }

    
    // attach behavior function
	@Override
	protected void appendGridUpdateBehavior(IHtmlWriter writer) {
		
		IComponentRenderContext renderContext = writer.getComponentRenderContext();
		DataGridComponent gridComponent = (DataGridComponent) renderContext.getComponent();
		
		DataGridUpdateBehaviorListener.addAjaxBehavior(gridComponent, renderContext.getFacesContext());
	}
	
	
}
