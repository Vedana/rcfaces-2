/*
 * $Id: AbstractGridRenderer.java,v 1.8 2013/12/19 15:46:45 jbmeslin Exp $
 * 
 */
package org.rcfaces.renderkit.html.internal.renderer;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.faces.FacesException;
import javax.faces.component.UIColumn;
import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.component.UINamingContainer;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseStream;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.Converter;
import javax.faces.event.FacesListener;
import javax.faces.model.DataModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.component.AdditionalInformationComponent;
import org.rcfaces.core.component.IMenuComponent;
import org.rcfaces.core.component.MenuComponent;
import org.rcfaces.core.component.ToolTipComponent;
import org.rcfaces.core.component.capability.IAdditionalInformationCardinalityCapability;
import org.rcfaces.core.component.capability.IAlignmentCapability;
import org.rcfaces.core.component.capability.IAsyncRenderModeCapability;
import org.rcfaces.core.component.capability.IAutoFilterCapability;
import org.rcfaces.core.component.capability.IBorderCapability;
import org.rcfaces.core.component.capability.ICardinality;
import org.rcfaces.core.component.capability.ICheckEventCapability;
import org.rcfaces.core.component.capability.IClientFullStateCapability;
import org.rcfaces.core.component.capability.ICriteriaManagerCapability;
import org.rcfaces.core.component.capability.IDragAndDropEffects;
import org.rcfaces.core.component.capability.IDraggableCapability;
import org.rcfaces.core.component.capability.IDroppableCapability;
import org.rcfaces.core.component.capability.IEmptyDataMessageCapability;
import org.rcfaces.core.component.capability.IFilterCapability;
import org.rcfaces.core.component.capability.IForegroundBackgroundColorCapability;
import org.rcfaces.core.component.capability.IGridCaptionCapability;
import org.rcfaces.core.component.capability.IImageSizeCapability;
import org.rcfaces.core.component.capability.IMenuCapability;
import org.rcfaces.core.component.capability.IMenuPopupIdCapability;
import org.rcfaces.core.component.capability.IOrderCapability;
import org.rcfaces.core.component.capability.IPreSelectionEventCapability;
import org.rcfaces.core.component.capability.IPreferencesCapability;
import org.rcfaces.core.component.capability.IReadOnlyCapability;
import org.rcfaces.core.component.capability.IRequiredCapability;
import org.rcfaces.core.component.capability.IResizableCapability;
import org.rcfaces.core.component.capability.IShowValueCapability;
import org.rcfaces.core.component.capability.ISortManagerCapability;
import org.rcfaces.core.component.capability.ISortedChildrenCapability;
import org.rcfaces.core.component.capability.IStyleClassCapability;
import org.rcfaces.core.component.capability.ITabIndexCapability;
import org.rcfaces.core.component.capability.ITextCapability;
import org.rcfaces.core.component.capability.ITitleToolTipIdCapability;
import org.rcfaces.core.component.capability.IToolTipTextCapability;
import org.rcfaces.core.component.capability.IVerticalAlignmentCapability;
import org.rcfaces.core.component.capability.IWidthCapability;
import org.rcfaces.core.component.capability.IWidthRangeCapability;
import org.rcfaces.core.component.familly.IContentAccessors;
import org.rcfaces.core.component.iterator.IColumnIterator;
import org.rcfaces.core.component.iterator.IMenuIterator;
import org.rcfaces.core.event.PropertyChangeEvent;
import org.rcfaces.core.internal.RcfacesContext;
import org.rcfaces.core.internal.capability.IAdditionalInformationComponent;
import org.rcfaces.core.internal.capability.ICellImageSettings;
import org.rcfaces.core.internal.capability.ICellStyleClassSettings;
import org.rcfaces.core.internal.capability.ICheckRangeComponent;
import org.rcfaces.core.internal.capability.IColumnsContainer;
import org.rcfaces.core.internal.capability.ICriteriaConfiguration;
import org.rcfaces.core.internal.capability.ICriteriaContainer;
import org.rcfaces.core.internal.capability.IDroppableGridComponent;
import org.rcfaces.core.internal.capability.IGridComponent;
import org.rcfaces.core.internal.capability.IImageAccessorsCapability;
import org.rcfaces.core.internal.capability.IPreferencesSettings;
import org.rcfaces.core.internal.capability.ISelectionRangeComponent;
import org.rcfaces.core.internal.component.CameliaDataComponent;
import org.rcfaces.core.internal.component.IImageAccessors;
import org.rcfaces.core.internal.component.IStatesImageAccessors;
import org.rcfaces.core.internal.component.Properties;
import org.rcfaces.core.internal.component.UIData2;
import org.rcfaces.core.internal.contentAccessor.IContentAccessor;
import org.rcfaces.core.internal.lang.StringAppender;
import org.rcfaces.core.internal.listener.CheckScriptListener;
import org.rcfaces.core.internal.listener.IScriptListener;
import org.rcfaces.core.internal.listener.IServerActionListener;
import org.rcfaces.core.internal.listener.PreSelectionScriptListener;
import org.rcfaces.core.internal.renderkit.IComponentData;
import org.rcfaces.core.internal.renderkit.IComponentRenderContext;
import org.rcfaces.core.internal.renderkit.IComponentWriter;
import org.rcfaces.core.internal.renderkit.IEventData;
import org.rcfaces.core.internal.renderkit.IRenderContext;
import org.rcfaces.core.internal.renderkit.IRequestContext;
import org.rcfaces.core.internal.renderkit.ISgmlWriter;
import org.rcfaces.core.internal.renderkit.WriterException;
import org.rcfaces.core.internal.tools.ComponentTools;
import org.rcfaces.core.internal.tools.CriteriaTools;
import org.rcfaces.core.internal.tools.FilterExpressionTools;
import org.rcfaces.core.internal.tools.FilteredDataModel;
import org.rcfaces.core.internal.tools.ValuesTools;
import org.rcfaces.core.internal.util.ParamUtils;
import org.rcfaces.core.lang.provider.ICheckProvider;
import org.rcfaces.core.model.IComponentRefModel;
import org.rcfaces.core.model.IFilterProperties;
import org.rcfaces.core.model.IFiltredModel;
import org.rcfaces.core.model.ISelectedCriteria;
import org.rcfaces.core.model.ISortedComponent;
import org.rcfaces.core.model.ISortedDataModel;
import org.rcfaces.core.preference.GridPreferences;
import org.rcfaces.core.preference.IComponentPreferences;
import org.rcfaces.renderkit.html.internal.AbstractCssRenderer;
import org.rcfaces.renderkit.html.internal.Constants;
import org.rcfaces.renderkit.html.internal.HtmlRenderContext;
import org.rcfaces.renderkit.html.internal.HtmlTools;
import org.rcfaces.renderkit.html.internal.IAccessibilityRoles;
import org.rcfaces.renderkit.html.internal.ICssWriter;
import org.rcfaces.renderkit.html.internal.IHtmlComponentRenderContext;
import org.rcfaces.renderkit.html.internal.IHtmlComponentWriter;
import org.rcfaces.renderkit.html.internal.IHtmlElements;
import org.rcfaces.renderkit.html.internal.IHtmlProcessContext;
import org.rcfaces.renderkit.html.internal.IHtmlRenderContext;
import org.rcfaces.renderkit.html.internal.IHtmlWriter;
import org.rcfaces.renderkit.html.internal.IJavaScriptRenderContext;
import org.rcfaces.renderkit.html.internal.IJavaScriptWriter;
import org.rcfaces.renderkit.html.internal.IObjectLiteralWriter;
import org.rcfaces.renderkit.html.internal.JavaScriptClasses;
import org.rcfaces.renderkit.html.internal.agent.IClientBrowser;
import org.rcfaces.renderkit.html.internal.agent.IUserAgent.BrowserType;
import org.rcfaces.renderkit.html.internal.decorator.CriteriaMenuDecorator;
import org.rcfaces.renderkit.html.internal.decorator.IComponentDecorator;
import org.rcfaces.renderkit.html.internal.decorator.SubMenuDecorator;
import org.rcfaces.renderkit.html.internal.ns.XhtmlNSAttributes;
import org.rcfaces.renderkit.html.internal.service.AdditionalInformationBehaviorListener;
import org.rcfaces.renderkit.html.internal.service.CriteriaGridBehaviorListener;
import org.rcfaces.renderkit.html.internal.service.TooltipBehaviorListener;
import org.rcfaces.renderkit.html.internal.util.ListenerTools;

/**
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.8 $ $Date: 2013/12/19 15:46:45 $
 */
@XhtmlNSAttributes({ "additionalInformationCardinality", "additionalInformationValues",
        "clientAdditionalFullState", "selectionCardinality",
        "clientSelectionFullState", "checkCardinality", "checkedCount",
        "clientCheckFullState", "readOnly", "emptyDataMessage", "disabled",
        "required", "wheelSelection", "alertLoadingMessage", "headerVisible",
        "sortManager", "filtred", "filterExpression", "rows", "rowCount",
        "first", "paged", "rowStyleClass", "sortPadding", "dragEffects",
        "dragTypes", "dragTypes", "dropEffects", "dropTypes", "dropTypes",
        "bodyDroppable", "resizable", "sb", "sb", "tabIndex" })
public abstract class AbstractGridRenderer extends AbstractCssRenderer
	implements IAdditionalInformationGridRenderer, ITooltipGridRenderer {

    private static final Log LOG = LogFactory
            .getLog(AbstractGridRenderer.class);

    protected static final boolean _GENERATE_HEADERS = false;

    protected static final String SORT_SERVER_COMMAND = "f_grid.Sort_Server";

    private static final String TABLE = "_table";

    private static final String TITLE_ROW = "_title";

    private static final String TITLE_CELL = "_tcell";

    private static final String TITLE_IMAGE = "_timage";

    private static final String TITLE_TTEXT = "_ttext";

    private static final String TITLE_TSORTER = "_tsorter";

    private static final String TITLE_STEXT = "_stext";

    private static final String TABLE_BODY = "_table_tbody";

    private static final String TABLE_CONTEXT = "camelia.table.context";

    private static final String SCROLLBARS_PROPERTY = "camelia.scrollbars";

    protected static final boolean ENABLE_SERVER_REQUEST = true;

    protected static final int[] EMPTY_INDEXES = new int[0];

    protected static final String NULL_VALUE = "*$& NULL VALUE *$& O.O.";

    protected static final boolean ALLOCATE_ROW_STRINGS = true;

    private static final int TEXT_RIGHT_PADDING = 4;

    private static final int TEXT_LEFT_PADDING = 4;

    private static final int SORT_PADDING = 18;

    private static final String GRID_MAIN_STYLE_CLASS = "f_grid";

    protected static final int GENERATE_CELL_STYLE_CLASS = 0x0001;

    protected static final int GENERATE_CELL_IMAGES = 0x0002;

    protected static final int GENERATE_CELL_TEXT = 0x0004;

    protected static final int GENERATE_CELL_WIDTH = 0x0008;

    private static final String ADDITIONAL_INFORMATIONS_RENDER_CONTEXT_STATE = "org.rcfaces.html.AI_CONTEXT";

    private static final String TOOLTIPS_RENDER_CONTEXT_STATE = "org.rcfaces.html.TT_CONTEXT";

    private static final String DATA_BODY_SCROLL_ID_SUFFIX = ""
            + UINamingContainer.SEPARATOR_CHAR
            + UINamingContainer.SEPARATOR_CHAR + "dataBody_scroll";

    private static final String DATA_TITLE_SCROLL_ID_SUFFIX = ""
            + UINamingContainer.SEPARATOR_CHAR
            + UINamingContainer.SEPARATOR_CHAR + "dataTitle_scroll";

    private static final String EMPTY_DATA_MESSAGE_ID_SUFFIX = ""
            + UINamingContainer.SEPARATOR_CHAR
            + UINamingContainer.SEPARATOR_CHAR + "emptyDataMessage";

    private static final String DATA_TABLE_ID_SUFFIX = ""
            + UINamingContainer.SEPARATOR_CHAR
            + UINamingContainer.SEPARATOR_CHAR + "dataTable";

    private static final String FIXED_HEADER_ID_SUFFIX = ""
            + UINamingContainer.SEPARATOR_CHAR
            + UINamingContainer.SEPARATOR_CHAR + "fixedHeader";

    private static final String FIXED_FAKE_COLUMN_ID_SUFFIX = ""
            + UINamingContainer.SEPARATOR_CHAR
            + UINamingContainer.SEPARATOR_CHAR + "fakeCol";

    private static final String TITLE_TTEXT_ID_SUFFIX = ""
            + UINamingContainer.SEPARATOR_CHAR
            + UINamingContainer.SEPARATOR_CHAR + "text";

    private static final String TITLE_TSORTER_ID_SUFFIX = ""
            + UINamingContainer.SEPARATOR_CHAR
            + UINamingContainer.SEPARATOR_CHAR + "sorter";

    protected static final int GRID_BODY_BORDER_SIZE_IN_PIXEL = 2;

    private static final int GRID_HEADER_SCROLLBAR_WIDTH = 16;

    protected static final String GRID_WRAP_CLASSNAME = "f_grid_wrap";

    protected static final String GRID_HAS_SORTER_IMAGE_URL_PROPERTY = "rcfaces.grid.HAS_SORTER_IMAGE";

    /*
     * private static final String FIXED_FAKE_CELL_ID_SUFFIX = "" +
     * UINamingContainer.SEPARATOR_CHAR + UINamingContainer.SEPARATOR_CHAR +
     * "fakeCell";
     */

    public String getComponentStyleClassName(IHtmlWriter htmlWriter) {
        return GRID_MAIN_STYLE_CLASS;
    }

    protected final AbstractGridRenderContext getGridRenderContext(
            IHtmlComponentRenderContext componentRenderContext) {
        AbstractGridRenderContext gridRenderContext = (AbstractGridRenderContext) componentRenderContext
                .getAttribute(TABLE_CONTEXT);

        if (gridRenderContext != null) {
            return gridRenderContext;
        }

        gridRenderContext = createTableContext(componentRenderContext);
        componentRenderContext.setAttribute(TABLE_CONTEXT, gridRenderContext);

        return gridRenderContext;
    }

    public void addRequiredJavaScriptClassNames(IHtmlWriter writer,
            IJavaScriptRenderContext javaScriptRenderContext) {
        super.addRequiredJavaScriptClassNames(writer, javaScriptRenderContext);

        IGridComponent dataGridComponent = (IGridComponent) writer
                .getComponentRenderContext().getComponent();

        AbstractGridRenderContext gridRenderContext = getGridRenderContext(writer
                .getHtmlComponentRenderContext());

        if (dataGridComponent instanceof IMenuCapability) {
            IMenuIterator menuIterator = ((IMenuCapability) dataGridComponent)
                    .listMenus();
            if (menuIterator.hasNext()) {
                javaScriptRenderContext.appendRequiredClass(
                        JavaScriptClasses.GRID, "menu");
            }
        }

        boolean ajax = needAjaxJavaScriptClasses(writer, dataGridComponent, gridRenderContext);
        
        if (ajax ) {
        	appendGridUpdateBehavior(writer);
        }
        
        if (gridRenderContext.listSelectedCriteria().length > 0 ) {
        	CriteriaGridBehaviorListener.addAjaxBehavior((ICriteriaManagerCapability) dataGridComponent, writer
                        .getHtmlComponentRenderContext().getHtmlRenderContext().getFacesContext());
        }
        

        if (gridRenderContext.hasAdditionalInformations()) {
        	
        	appendAdditionalInformationBehavior(writer);
        	
            ajax = true;
            javaScriptRenderContext.appendRequiredClass(JavaScriptClasses.GRID,
                    "additional");

            if (needAdditionalInformationContextState()) {
                IHtmlRenderContext htmlRenderContext = writer
                        .getHtmlComponentRenderContext().getHtmlRenderContext();
                
                Object state = htmlRenderContext.saveState(htmlRenderContext
                        .getFacesContext());

                if (LOG.isDebugEnabled()) {
                    LOG.debug("Save context state on "
                            + ((UIComponent) dataGridComponent).getId()
                            + " for additionalInformations");
                }

                if (state != null) {
                    String contentType = htmlRenderContext.getFacesContext()
                            .getResponseWriter().getContentType();

                   
                    ((CameliaDataComponent) dataGridComponent).getAttributes().put(
                            ADDITIONAL_INFORMATIONS_RENDER_CONTEXT_STATE,
                            new Object[] { state, contentType });
                }
                
            }
        }

        if (gridRenderContext.containsTooltips()) {
        	
        	appendTooltipBehavior(writer);
        	
            ajax = true;
            javaScriptRenderContext.appendRequiredClass(JavaScriptClasses.GRID,
                    "toolTip");

            if (getAdditionalInformationsRenderContextState(dataGridComponent) == null) {
                IHtmlRenderContext htmlRenderContext = writer
                        .getHtmlComponentRenderContext().getHtmlRenderContext();
                Object state = htmlRenderContext.saveState(htmlRenderContext
                        .getFacesContext());

                if (LOG.isDebugEnabled()) {
                    LOG.debug("Save context state on "
                            + ((UIComponent) dataGridComponent).getId()
                            + " for tooltips");
                }

                if (state != null) {
                    String contentType = htmlRenderContext.getFacesContext()
                            .getResponseWriter().getContentType();

                    ((UIComponent) dataGridComponent).getAttributes().put(
                            TOOLTIPS_RENDER_CONTEXT_STATE,
                            new Object[] { state, contentType });
                }
            }
        }

        if (ajax == false) {
            // On a des colonnes triables coté serveur ?
            int columnCount = gridRenderContext.getColumnCount();
            for (int columnIndex = 0; columnIndex < columnCount; columnIndex++) {
                Object sort = gridRenderContext.getSortCommand(columnIndex);
                if (sort == null) {
                    continue;
                }

                if (sort instanceof IServerActionListener) {
                    ajax = true;
                    break;
                }

                if (gridRenderContext.getSortClientSide(columnIndex) == false) {
                    ajax = true;
                    break;
                }
                if (sort instanceof String) {
                    if (((String) sort).trim().equals(SORT_SERVER_COMMAND)) {
                        ajax = true;
                        break;
                    }
                }
            }
            
        }

        if (ajax) {
            addAjaxRequiredClasses(javaScriptRenderContext);
            appendGridUpdateBehavior(writer);
        }

        if (true) {
            javaScriptRenderContext.appendRequiredClass(
                    "f_criteriaPopupManager", null);
        }

        if (dataGridComponent instanceof ISortManagerCapability) {
            String sortManager = ((ISortManagerCapability) dataGridComponent)
                    .getSortManager();
            if (sortManager != null && sortManager.indexOf('(') < 0) {
                if ("dialog".equals(sortManager)) {
                    javaScriptRenderContext.appendRequiredClass(
                            "f_columnSortDialog", null);
                }
            }
        }

        if (((dataGridComponent instanceof IDraggableCapability) && ((IDraggableCapability) dataGridComponent)
                .isDraggable())
                || ((dataGridComponent instanceof IDroppableCapability) && ((IDroppableCapability) dataGridComponent)
                        .isDroppable())) {
            javaScriptRenderContext.appendRequiredClass(JavaScriptClasses.GRID,
                    "dnd");
        }
    }

    protected void addAjaxRequiredClasses(
            IJavaScriptRenderContext javaScriptRenderContext) {
        javaScriptRenderContext.appendRequiredClass(JavaScriptClasses.GRID,
                "ajax");
    }

    protected boolean needAjaxJavaScriptClasses(IHtmlWriter writer,
            IGridComponent dataGridComponent, AbstractGridRenderContext gridRenderContext) {
        if (dataGridComponent.getRows() > 0 
        		|| gridRenderContext.isDroppable()) {
            return true;
        }

        
        DataModel dataModel = dataGridComponent.getDataModelValue();
        IFiltredModel filtredModel = getAdapter(IFiltredModel.class, dataModel);
        if (filtredModel != null) {
            return true;
        }
        
        
        return false;
    }

    public Object[] getAdditionalInformationsRenderContextState(
            IGridComponent component) {
        return (Object[]) ((CameliaDataComponent) component).getAttributes().get(
                ADDITIONAL_INFORMATIONS_RENDER_CONTEXT_STATE);
    }

    public Object[] getTooltipsRenderContextState(IColumnsContainer component) {
        Object[] state = (Object[]) ((UIComponent) component).getAttributes()
                .get(TOOLTIPS_RENDER_CONTEXT_STATE);

        if (state != null) {
            return state;
        }

        return getAdditionalInformationsRenderContextState((IGridComponent) component);
    }

    protected boolean needAdditionalInformationContextState() {
        return false;
    }

    protected String encodeAdditionalInformation(IJavaScriptWriter jsWriter,
            AdditionalInformationComponent additionalInformationComponent)
            throws WriterException {

        CharArrayWriter writer = new CharArrayWriter(8000);
        
        encodeAdditionalInformation(jsWriter.getFacesContext(), writer,
                (IGridComponent) jsWriter.getComponentRenderContext()
                        .getComponent(), additionalInformationComponent,
                jsWriter.getResponseCharacterEncoding());

        writer.close();

        return writer.toString();
    }

    protected String encodeToolTip(IJavaScriptWriter jsWriter,
            ToolTipComponent tooltipComponent) throws WriterException {

        CharArrayWriter writer = new CharArrayWriter(8000);

        encodeToolTip(jsWriter.getFacesContext(), writer,
                (IGridComponent) jsWriter.getComponentRenderContext()
                        .getComponent(), tooltipComponent,
                jsWriter.getResponseCharacterEncoding());

        writer.close();

        return writer.toString();
    }

    public void encodeChildren(FacesContext facesContext, UIComponent component)
            throws IOException {
        // Pas de rendu des enfants !
        // super.encodeChildren(facesContext, component);
    }
    
    
    protected void encodeAdditionalInformation(FacesContext facesContext,
            Writer writer, IGridComponent component,
            AdditionalInformationComponent additionalInformationComponent,
            String responseCharacterEncoding) throws WriterException {
    
    	if (RcfacesContext.isJSF1_2()) {
    		oldEncodeAdditionalInformation(facesContext, writer, component, 
    				additionalInformationComponent, responseCharacterEncoding);
    		return;
    	}
    	
    	ResponseWriter oldResponseWriter = facesContext.getResponseWriter();
        ResponseWriter newResponseWriter = oldResponseWriter.cloneWithWriter(writer);
        ResponseStream oldResponseStream = facesContext.getResponseStream();

        Object states[] = getAdditionalInformationsRenderContextState(component);
        if (states == null) {
            throw new FacesException(
                    "Can not get render context state for additional informations of gridComponent='"
                            + component + "'");
        }

        
        HtmlRenderContext.restoreRenderContext(facesContext, states[0],
                true);

        facesContext.setResponseWriter(newResponseWriter);
        try {
            ComponentTools.encodeRecursive(facesContext,
                    additionalInformationComponent);

        } finally {
            if (oldResponseWriter != null) {
                facesContext.setResponseWriter(oldResponseWriter);
            }

            if (oldResponseStream != null) {
                facesContext.setResponseStream(oldResponseStream);
            }
        }
    	
    	
    }

    private void oldEncodeAdditionalInformation(FacesContext facesContext,
            Writer writer, IGridComponent component,
            AdditionalInformationComponent additionalInformationComponent,
            String responseCharacterEncoding) throws WriterException {

        ResponseWriter oldResponseWriter = facesContext.getResponseWriter();
        ResponseWriter newResponseWriter;
        ResponseStream oldResponseStream = null;
        if (oldResponseWriter == null) {
            // Appel AJAX pour un TRI par exemple ... (ou changement de page)

            oldResponseStream = facesContext.getResponseStream();

            Object states[] = getAdditionalInformationsRenderContextState(component);
            if (states == null) {
                throw new FacesException(
                        "Can not get render context state for additional informations of gridComponent='"
                                + component + "'");
            }

            newResponseWriter = facesContext.getRenderKit()
                    .createResponseWriter(writer, (String) states[1],
                            responseCharacterEncoding);

            HtmlRenderContext.restoreRenderContext(facesContext, states[0],
                    true);

        } else {
            newResponseWriter = oldResponseWriter.cloneWithWriter(writer);
        }

        facesContext.setResponseWriter(newResponseWriter);
        try {
            ComponentTools.encodeRecursive(facesContext,
                    additionalInformationComponent);

        } finally {
            if (oldResponseWriter != null) {
                facesContext.setResponseWriter(oldResponseWriter);
            }

            if (oldResponseStream != null) {
                facesContext.setResponseStream(oldResponseStream);
            }
        }
    }

    // peut surment ne pas recopier se code
    protected void encodeToolTip(FacesContext facesContext, Writer writer,
            IGridComponent component, ToolTipComponent tooltipComponent,
            String responseCharacterEncoding) throws WriterException {

        ResponseWriter oldResponseWriter = facesContext.getResponseWriter();
        ResponseWriter newResponseWriter;

        ResponseStream oldResponseStream = null;
        if (oldResponseWriter == null) {
            // Appel AJAX pour un TRI par exemple ... (ou changement de page)

            oldResponseStream = facesContext.getResponseStream();

            Object states[] = getTooltipsRenderContextState(component);
            if (states == null) {
                throw new FacesException(
                        "Can not get render context state for tooltip of gridComponent='"
                                + component + "'");
            }

            newResponseWriter = facesContext.getRenderKit()
                    .createResponseWriter(writer, (String) states[1],
                            responseCharacterEncoding);

            HtmlRenderContext.restoreRenderContext(facesContext, states[0],
                    true);

        } else {
            newResponseWriter = oldResponseWriter.cloneWithWriter(writer);
        }

        facesContext.setResponseWriter(newResponseWriter);
        try {
            ComponentTools.encodeChildrenRecursive(facesContext,
                    tooltipComponent);

        } finally {
            if (oldResponseWriter != null) {
                facesContext.setResponseWriter(oldResponseWriter);
            }

            if (oldResponseStream != null) {
                facesContext.setResponseStream(oldResponseStream);
            }
        }
    }

    public boolean isDataModelRowAvailable(
            IHtmlRenderContext htmlRenderContext, IGridComponent gridComponent,
            String responseCharacterEncoding, String rowValue2, String rowIndex)
            throws WriterException {

        IHtmlWriter htmlWriter = (IHtmlWriter) htmlRenderContext
                .getComponentWriter();

        // On prepare le DataModel !
        AbstractGridRenderContext tableContext = getGridRenderContext(htmlWriter
                .getHtmlComponentRenderContext());
        DataModel dataModel = tableContext.getDataModel();

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
            } else {
                dataModel = FilteredDataModel.filter(dataModel, filtersMap);
            }

        } else if (filtredDataModel != null) {
            filtredDataModel.setFilter(FilterExpressionTools.EMPTY);
        }

        int translatedRowIndex = Integer.parseInt(rowIndex);

        ISortedComponent sortedComponents[] = tableContext
                .listSortedComponents();
        ISortedDataModel sortedDataModel = getAdapter(ISortedDataModel.class,
                dataModel, sortedComponents);
        if (sortedComponents != null && sortedComponents.length > 0) {
            if (sortedDataModel != null) {
                sortedDataModel.setSortParameters((UIComponent) gridComponent,
                        sortedComponents);
            } else {
                // Il faut faire le tri à la main !

               	// pas de tri , le js donne le bon index

//              int sortTranslations[] = GridServerSort
//                      .computeSortedTranslation(
//                              htmlRenderContext.getFacesContext(),
//                              gridComponent, dataModel, sortedComponents);
//
//              if (sortTranslations != null) {
//                  translatedRowIndex = sortTranslations[translatedRowIndex];
//              } 
            }
        } else if (sortedDataModel != null) {
            // Reset des parametres de tri !
            sortedDataModel
                    .setSortParameters((UIComponent) gridComponent, null);
        }

        gridComponent.setRowIndex(translatedRowIndex);
        return gridComponent.isRowAvailable();
    }

    public void renderAdditionalInformation(
            IHtmlRenderContext htmlRenderContext, Writer writer,
            IGridComponent gridComponent, String responseCharacterEncoding,
            String rowValue2, String rowIndex) throws WriterException {
        IHtmlWriter htmlWriter = (IHtmlWriter) htmlRenderContext
                .getComponentWriter();

        // On prepare le DataModel !
        AbstractGridRenderContext tableContext = getGridRenderContext(htmlWriter
                .getHtmlComponentRenderContext());

        try {
            if (isDataModelRowAvailable(htmlRenderContext, gridComponent,
                    responseCharacterEncoding, rowValue2, rowIndex) == false) {
                return;
            }

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

            if (additionalInformationComponent == null) {
                return;
            }

            
            
            encodeAdditionalInformation(htmlRenderContext.getFacesContext(),
                    writer, gridComponent, additionalInformationComponent,
                    responseCharacterEncoding);

        } finally {
            gridComponent.setRowIndex(-1);
        }
    }

    public void renderTooltip(IHtmlWriter htmlWriter,
            IColumnsContainer columnsContainer, String responseCharacterEncoding,
            String rowValue2, String rowIndex, String tooltipId)
            throws WriterException {
    	
    	 IGridComponent gridComponent = (IGridComponent) columnsContainer;

        IHtmlRenderContext htmlRenderContext = htmlWriter
                .getHtmlComponentRenderContext().getHtmlRenderContext();

        // On prepare le DataModel !
        // AbstractGridRenderContext tableContext =
        // getGridRenderContext(htmlWriter.getHtmlComponentRenderContext());
        try {
            if (isDataModelRowAvailable(htmlRenderContext, gridComponent,
                    responseCharacterEncoding, rowValue2, rowIndex) == false) {
                return;
            }

            FacesContext facesContext = htmlRenderContext.getFacesContext();

            ToolTipComponent tooltipComponent = (ToolTipComponent) facesContext
                    .getViewRoot().findComponent(tooltipId);

            if (tooltipComponent == null) {
                return;
            }

            encodeToolTip(facesContext, facesContext.getResponseWriter(),
                    gridComponent, tooltipComponent, responseCharacterEncoding);

        } finally {
            gridComponent.setRowIndex(-1);
        }
    }

    protected void writeCustomCss(IHtmlWriter writer, ICssWriter cssWriter) {
        super.writeCustomCss(writer, cssWriter);

        IComponentRenderContext componentRenderContext = writer
                .getComponentRenderContext();
        UIComponent dataGridComponent = componentRenderContext.getComponent();

        if (dataGridComponent instanceof IBorderCapability) {
            if (((IBorderCapability) dataGridComponent).isBorder() == false) {
                cssWriter.writeBorderStyle(ICssWriter.NONE);
            }
        }
    }

    protected void encodeBegin(IComponentWriter writer) throws WriterException {
        super.encodeBegin(writer);

        encodeGrid((IHtmlWriter) writer);
    }

    protected void encodeGrid(IHtmlWriter htmlWriter) throws WriterException {

        IHtmlComponentRenderContext componentRenderContext = htmlWriter
                .getHtmlComponentRenderContext();

        FacesContext facesContext = componentRenderContext.getFacesContext();

        IGridComponent gridComponent = (IGridComponent) componentRenderContext
                .getComponent();

        if (gridComponent instanceof IPreferencesCapability) {
            IPreferencesCapability preferenceCapability = (IPreferencesCapability) gridComponent;

            IComponentPreferences preference = preferenceCapability
                    .getPreferences();
            if (preference != null) {
                preference.loadPreferences(facesContext,
                        (UIComponent) gridComponent);
            }
        }

        AbstractGridRenderContext gridRenderContext = getGridRenderContext(componentRenderContext);

        htmlWriter.startElement(IHtmlWriter.DIV, (UIComponent) gridComponent);

        writeHtmlAttributes(htmlWriter);
        writeJavaScriptAttributes(htmlWriter);
        writeCssAttributes(htmlWriter, getCssStyleClasses(htmlWriter),
                ~CSS_SIZE_MASK);

        if (gridRenderContext.hasAdditionalInformations()) {
            int cardinality = gridRenderContext
                    .getAdditionalInformationCardinality();
            if (cardinality == 0) {
                cardinality = IAdditionalInformationCardinalityCapability.DEFAULT_CARDINALITY;
            }

            htmlWriter.writeAttributeNS("additionalInformationCardinality",
                    cardinality);

            int cfs = gridRenderContext.getClientAdditionalFullState();
            if (cfs != IClientFullStateCapability.DEFAULT_CLIENT_FULL_STATE) {
                htmlWriter.writeAttributeNS("clientAdditionalFullState", cfs);
            }
        }

        if (gridRenderContext.isSelectable()) {
            htmlWriter.writeAttributeNS("selectionCardinality",
                    gridRenderContext.getSelectionCardinality());

            int cfs = gridRenderContext.getClientSelectionFullState();
            if (cfs != IClientFullStateCapability.DEFAULT_CLIENT_FULL_STATE) {
                htmlWriter.writeAttributeNS("clientSelectionFullState", cfs);
            }
        }

        if (gridRenderContext.isCheckable()) {
            htmlWriter.writeAttributeNS("checkCardinality",
                    gridRenderContext.getCheckCardinality());

            if (gridRenderContext.getCheckCardinality() == ICardinality.ONEMANY_CARDINALITY) {
                int checkedCount = ((ICheckProvider) gridComponent)
                        .getCheckedValuesCount();

                if (checkedCount > 0) {
                    htmlWriter.writeAttributeNS("checkedCount", checkedCount);
                }
            }

            int cfs = gridRenderContext.getClientCheckFullState();
            if (cfs != IClientFullStateCapability.DEFAULT_CLIENT_FULL_STATE) {
                htmlWriter.writeAttributeNS("clientCheckFullState", cfs);
            }
        }

        if (gridComponent instanceof IReadOnlyCapability) {
            if (((IReadOnlyCapability) gridComponent).isReadOnly()) {
                htmlWriter.writeAttributeNS("readOnly", true);
            }
        }

        String emptyDataMessage = null;

        if (gridComponent instanceof IEmptyDataMessageCapability) {
            emptyDataMessage = ((IEmptyDataMessageCapability) gridComponent)
                    .getEmptyDataMessage();
            if (emptyDataMessage != null) {
                emptyDataMessage = ParamUtils.formatMessage(
                        (UIComponent) gridComponent, emptyDataMessage);

                htmlWriter.writeAttributeNS("emptyDataMessage",
                        emptyDataMessage);
            }
        }

        if (gridRenderContext.isDisabled()) {
            htmlWriter.writeAttributeNS("disabled", true);
        }

        if (gridComponent instanceof IRequiredCapability) {
            if (((IRequiredCapability) gridComponent).isRequired()) {
                htmlWriter.writeAttributeNS("required", true);
            }
        }

        boolean wheelSelection = gridRenderContext.isWheelSelection();
        if (wheelSelection == false) {
            htmlWriter.writeAttributeNS("wheelSelection", wheelSelection);
        }

        String alertLoadingMessage = gridRenderContext.getAlertLoadingMessage();
        if (alertLoadingMessage != null) {
            htmlWriter.writeAttributeNS("alertLoadingMessage",
                    alertLoadingMessage);
        }

        boolean headerVisible = gridRenderContext.isHeaderVisible();
        if (headerVisible == false) {
            htmlWriter.writeAttributeNS("headerVisible", false);
        }

        String sortManager = gridRenderContext.getSortManager();
        if (sortManager != null) {
            htmlWriter.writeAttributeNS("sortManager", sortManager);
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

        int rows = gridRenderContext.getRows();
        if (rows > 0) {
            htmlWriter.writeAttributeNS("rows", rows);
        }

        int rowCount = gridRenderContext.getRowCount();
        if (rowCount >= 0) {
            htmlWriter.writeAttributeNS("rowCount", rowCount);
        }

        int first = gridRenderContext.getFirst();
        if (first > 0) {
            if (rowCount < 0 || first <= rowCount) {
                htmlWriter.writeAttributeNS("first", first);
            } else {
                // reset First to 0 if rowCount is smaller than first
                gridRenderContext.resetFirst();
            }
        }

        if (gridRenderContext.isPaged() == false) {
            htmlWriter.writeAttributeNS("paged", false);
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

        if (getSortPadding() != SORT_PADDING) {
            htmlWriter.writeAttributeNS("sortPadding", getSortPadding());
        }

        if (gridRenderContext.isDraggable()) {
            IDraggableCapability draggableCapability = (IDraggableCapability) gridComponent;

            int dragEffects = draggableCapability.getDragEffects();

            if (dragEffects <= IDragAndDropEffects.UNKNOWN_DND_EFFECT) {
                dragEffects = IDragAndDropEffects.DEFAULT_DND_EFFECT;
            }
            htmlWriter.writeAttributeNS("dragEffects", dragEffects);

            String dragTypes[] = draggableCapability.getDragTypes();
            if (dragTypes != null && dragTypes.length > 0) {
                htmlWriter.writeAttributeNS("dragTypes",
                        HtmlTools.serializeDnDTypes(dragTypes));
            } else {
                htmlWriter.writeAttributeNS("dragTypes", "x-RCFaces/row");
            }
        }

        if (gridRenderContext.isDroppable()) {
            IDroppableCapability droppableCapability = (IDroppableCapability) gridComponent;

            int dropEffects = droppableCapability.getDropEffects();

            if (dropEffects <= IDragAndDropEffects.UNKNOWN_DND_EFFECT) {
                dropEffects = IDragAndDropEffects.DEFAULT_DND_EFFECT;
            }
            htmlWriter.writeAttributeNS("dropEffects", dropEffects);

            String dropTypes[] = droppableCapability.getDropTypes();
            if (dropTypes != null && dropTypes.length > 0) {
                htmlWriter.writeAttributeNS("dropTypes",
                        HtmlTools.serializeDnDTypes(dropTypes));
            } else {
                htmlWriter.writeAttributeNS("dropTypes", "x-RCFaces/row");
            }

            if (gridComponent instanceof IDroppableGridComponent) {
                if (((IDroppableGridComponent) gridComponent).isBodyDroppable()) {
                    htmlWriter.writeAttributeNS("bodyDroppable", true);
                }
            }
        }

        String sorterImageURL = getNormalSorterImageURL(htmlWriter);
        if (sorterImageURL != null) {
            String descendingSorterImageURL = getDescendingSorterImageURL(htmlWriter);
            String ascendingSorterImageURL = getAscendingSorterImageURL(htmlWriter);

            if (descendingSorterImageURL != null
                    && ascendingSorterImageURL != null) {
                htmlWriter.writeAttributeNS("descSorter",
                        descendingSorterImageURL);
                htmlWriter.writeAttributeNS("ascSorter",
                        ascendingSorterImageURL);
                htmlWriter.writeAttributeNS("defSorter", sorterImageURL);

                htmlWriter.getComponentRenderContext().setAttribute(
                        GRID_HAS_SORTER_IMAGE_URL_PROPERTY, Boolean.TRUE);
            }
        }

        writeToolTipId(htmlWriter, gridRenderContext, "#table");

        writeGridComponentAttributes(htmlWriter, gridRenderContext,
                gridComponent);

        int gridHeight = gridRenderContext.getGridHeight();
        int gridWidth = gridRenderContext.getGridWidth();
        if (gridWidth <= 0) {
            gridWidth = gridRenderContext.getColumnWidthTotalSize();

            if (gridWidth > 0) {
                // Border DIV // Border TABLE
                gridWidth += GRID_BODY_BORDER_SIZE_IN_PIXEL
                        + GRID_BODY_BORDER_SIZE_IN_PIXEL;

                if (gridRenderContext.hasScrollBars() && headerVisible) {
                    gridWidth += GRID_HEADER_SCROLLBAR_WIDTH;
                }
            }
        }

        if (gridHeight > 0 || gridWidth > 0) {
            /*
             * writer.setComponentRenderAttribute(SCROLLBARS_PROPERTY,
             * Boolean.TRUE);
             */

            ICssWriter cssWriter = htmlWriter.writeStyle();
            if (gridWidth > 0) {
                cssWriter.writeWidthPx(gridWidth);
            }
            if (gridHeight > 0) {
                cssWriter.writeHeightPx(gridHeight);
            }
        }

        boolean resizable = gridRenderContext.isResizable();
       

        if (resizable) {
            htmlWriter.writeAttributeNS("resizable", true);
        }

        if (gridRenderContext.hasScrollBars() == false) {
            htmlWriter.writeAttributeNS("sb", false);
        }

        if (serverTitleGeneration() == false) {
            htmlWriter
                    .writeAttributeNS("sb", gridRenderContext.hasScrollBars());
            return;
        }

        if (gridRenderContext.hasScrollBars() == false
                || headerVisible == false) {
            htmlWriter.writeStyle().writeOverflow(ICssWriter.AUTO);
        }

        if (emptyDataMessage != null) {
            htmlWriter.startElement(IHtmlWriter.DIV);
            htmlWriter.writeId(getEmptyDataMessageId(htmlWriter));
            htmlWriter.writeClass(getEmptyDataMessageClassName(htmlWriter));

            htmlWriter.writeText(emptyDataMessage);

            htmlWriter.endElement(IHtmlWriter.DIV);
        }

        String dataTitleClassName = getDataTitleScrollClassName(htmlWriter);
        if (htmlWriter.getComponentRenderContext().getAttribute(
                GRID_HAS_SORTER_IMAGE_URL_PROPERTY) != null) {
            dataTitleClassName += " "
                    + getGridHasSorterImageURLClassName(htmlWriter);
        }

        int w = gridWidth - GRID_BODY_BORDER_SIZE_IN_PIXEL;
        // boolean mainComponentScrollable = true;
        if (gridRenderContext.hasScrollBars()) {

            if (headerVisible) {
                htmlWriter.startElement(IHtmlWriter.DIV);
                htmlWriter.writeId(getDataTitleScrollId(htmlWriter));

                htmlWriter.writeClass(dataTitleClassName);
                if (w > 0) {
                    htmlWriter.writeStyle().writeWidthPx(w);
                }

                writeToolTipId(htmlWriter, gridRenderContext, "#head");

                encodeFixedHeader(htmlWriter, gridRenderContext, gridWidth,
                        true);
                htmlWriter.endElement(IHtmlWriter.DIV);

                // Finalement le BODY aussi n'a pas de DIV !

                htmlWriter.startElement(IHtmlWriter.DIV);
                htmlWriter.writeId(getDataBodyScrollId(htmlWriter));
                htmlWriter.writeClass(getDataBodyScrollClassName(htmlWriter));

                ICssWriter cssWriter = htmlWriter.writeStyle(32);

                if (gridHeight > 0) {
                    int gh = gridHeight - 2; // Border !
                    if (headerVisible) {
                        gh -= getTitleHeight();
                    }

                    cssWriter.writeHeightPx(gh);
                }
                if (w > 0) {
                    cssWriter.writeWidthPx(w);
                }

                // mainComponentScrollable = false;
            }
        } else {
            // Pas de scroll vertical ...
            if (headerVisible) {
                htmlWriter.startElement(IHtmlWriter.DIV);
                htmlWriter.writeId(getDataTitleScrollId(htmlWriter));
                htmlWriter.writeClass(dataTitleClassName);
                if (w > 0) {
                    htmlWriter.writeStyle().writeWidthPx(w);
                }

                writeToolTipId(htmlWriter, gridRenderContext, "#head");

                encodeFixedHeader(htmlWriter, gridRenderContext, gridWidth,
                        false);
                htmlWriter.endElement(IHtmlWriter.DIV);
            }
        }

        htmlWriter.startElement(IHtmlWriter.TABLE);
        htmlWriter.writeAttribute("role", "grid");
        htmlWriter.writeId(getDataTableId(htmlWriter));
        htmlWriter.writeClass(getDataTableClassName(htmlWriter,
                gridRenderContext.isDisabled()));

        writeToolTipId(htmlWriter, gridRenderContext, "#body");

        // Pas de support CSS de border-spacing pour IE: on est oblig� de le
        // cod� en HTML ...
        htmlWriter.writeCellSpacing(0);

        int tableWidth = gridRenderContext.getColumnWidthTotalSize();
        // if (tableWidth > 0) {
        // if (gridWidth > 0 && gridWidth > tableWidth) {
        // // tableWidth = -1;
        // }
        // }

        if (tableWidth > 0) {
            htmlWriter.writeWidth(tableWidth);
        } else if (w > 0) {
            htmlWriter.writeWidth(w);
        } else {
            htmlWriter.writeWidth("100%");
        }

        // Accessibility : summary
        if (gridComponent instanceof IGridCaptionCapability) {
            String summary = ((IGridCaptionCapability) gridComponent)
                    .getSummary();
            if (summary != null) {
                htmlWriter.writeAttribute("summary", summary);
            }
            String caption = ((IGridCaptionCapability) gridComponent)
                    .getCaption();
            if (caption != null && isHiddenCaptionSupported(htmlWriter)) {
                encodeHiddenCaption(htmlWriter, gridRenderContext, caption);
            }
        }

        encodeHeader(htmlWriter, gridRenderContext);
        if (isHiddenTitleSupported(htmlWriter)) {
            encodeHiddenTitle(htmlWriter, gridRenderContext);
        }

        htmlWriter.startElement(IHtmlWriter.TBODY);
        htmlWriter.writeClass(getTitleTableBodyClassName(htmlWriter));

        encodeBodyBegin(htmlWriter, gridRenderContext);
    }

    

	protected boolean isHiddenCaptionSupported(IHtmlWriter htmlWriter) {
        IHtmlProcessContext htmlProcessContext = htmlWriter
                .getHtmlComponentRenderContext().getHtmlRenderContext()
                .getHtmlProcessContext();

        IClientBrowser clientBrowser = htmlProcessContext.getClientBrowser();
        if (clientBrowser == null) {
            return false;
        }

        BrowserType browserType = clientBrowser.getBrowserType();
        switch (browserType) {
        case UNKNOWN:
            return false;

        case MICROSOFT_INTERNET_EXPLORER:
            if (clientBrowser.getMajorVersion() < 7) {
                return false;
            }
            break;
        }

        return true;
    }

    protected boolean isHiddenTitleSupported(IHtmlWriter htmlWriter) {
        IHtmlProcessContext htmlProcessContext = htmlWriter
                .getHtmlComponentRenderContext().getHtmlRenderContext()
                .getHtmlProcessContext();

        IClientBrowser clientBrowser = htmlProcessContext.getClientBrowser();
        if (clientBrowser == null) {
            return false;
        }

        BrowserType browserType = clientBrowser.getBrowserType();
        switch (browserType) {
        case UNKNOWN:
            return false;

        case MICROSOFT_INTERNET_EXPLORER:
            if (clientBrowser.getMajorVersion() < 9) {
                return false;
            }
            break;
        }

        return true;
    }

    protected void writeToolTipId(IHtmlWriter htmlWriter,
            AbstractGridRenderContext gridRenderContext, String tooltipIdOrName)
            throws WriterException {

        IComponentRenderContext componentRenderContext = htmlWriter
                .getComponentRenderContext();

        ToolTipComponent tooltipComponent = gridRenderContext
                .findTooltipByIdOrName(componentRenderContext,
                        componentRenderContext.getComponent(), tooltipIdOrName,
                        null);
        if (tooltipComponent == null) {
            return;
        }

        IRenderContext renderContext = componentRenderContext
                .getRenderContext();

        FacesContext facesContext = renderContext.getFacesContext();

        String tooltipClientId = tooltipComponent.getClientId(facesContext);

        htmlWriter.writeAttribute("v:toolTipId", tooltipClientId);
    }

    protected boolean serverTitleGeneration() {
        return true;
    }

    protected void encodeEnd(IComponentWriter writer) throws WriterException {

        AbstractGridRenderContext tableContext = getGridRenderContext(((IHtmlWriter) writer)
                .getHtmlComponentRenderContext());

        encodeBodyEnd((IHtmlWriter) writer, tableContext);

        super.encodeEnd(writer);

        if (tableContext.hasTooltips()) {
            Collection<ToolTipComponent> toolTipComponents = tableContext
                    .listToolTips();

            for (ToolTipComponent tooltip : toolTipComponents) {

                ToolTipRenderer.render((IHtmlWriter) writer, tooltip);
            }
        }
    }

    protected void writeGridComponentAttributes(IHtmlWriter htmlWriter,
            AbstractGridRenderContext tableContext, IGridComponent dg)
            throws WriterException {
        if (dg instanceof ITabIndexCapability) {
            ITabIndexCapability tic = (ITabIndexCapability) dg;
            Integer tabIndex = tic.getTabIndex();
            if (tabIndex != null) {
                htmlWriter.writeAttributeNS("tabIndex", tabIndex.intValue());
            }
        }

    }

    protected String getGridHasSorterImageURLClassName(IHtmlWriter htmlWriter) {
        return GRID_MAIN_STYLE_CLASS + "_sorterHasImage";
    }

    protected String getDataBodyScrollClassName(IHtmlWriter htmlWriter) {
        return GRID_MAIN_STYLE_CLASS + "_dataBody_scroll";
    }

    protected String getColgroupClassName(IHtmlWriter htmlWriter) {
        return GRID_MAIN_STYLE_CLASS + "_colgroup";
    }

    protected String getDataBodyScrollId(IHtmlWriter htmlWriter) {
        return htmlWriter.getComponentRenderContext().getComponentClientId()
                + DATA_BODY_SCROLL_ID_SUFFIX;
    }

    protected String getDataTableId(IHtmlWriter htmlWriter) {
        return htmlWriter.getComponentRenderContext().getComponentClientId()
                + DATA_TABLE_ID_SUFFIX;
    }

    protected String getDataTableClassName(IHtmlWriter htmlWriter,
            boolean disabled) {
        String className = GRID_MAIN_STYLE_CLASS + TABLE;
        if (disabled) {
            className += " " + className + "_disabled";
        }

        return className;
    }

    protected String getEmptyDataMessageClassName(IHtmlWriter htmlWriter) {
        return GRID_MAIN_STYLE_CLASS + "_empty_data_message";
    }

    protected String getEmptyDataMessageId(IHtmlWriter htmlWriter) {
        return htmlWriter.getComponentRenderContext().getComponentClientId()
                + EMPTY_DATA_MESSAGE_ID_SUFFIX;
    }

    protected String getDataTitleScrollClassName(IHtmlWriter htmlWriter) {
        return GRID_MAIN_STYLE_CLASS + "_dataTitle_scroll";
    }

    protected String getDataTitleScrollId(IHtmlWriter htmlWriter) {
        return htmlWriter.getComponentRenderContext().getComponentClientId()
                + DATA_TITLE_SCROLL_ID_SUFFIX;
    }

    protected String getHiddenTitleClassName(IHtmlWriter htmlWriter) {
        return GRID_MAIN_STYLE_CLASS + "_hidden_title";
    }

    protected String getHiddenCaptionClassName(IHtmlWriter htmlWriter) {
        return GRID_MAIN_STYLE_CLASS + "_hidden_caption";
    }

    protected int getTitleHeight() {
        return 20;
    }

    protected void encodeFixedHeader(IHtmlWriter htmlWriter,
            AbstractGridRenderContext tableContext, int dataGridWidth,
            boolean fixedHeader) throws WriterException {

        htmlWriter.startElement(IHtmlWriter.UL);
        htmlWriter.writeId(getFixedHeaderTableId(htmlWriter));

        if (fixedHeader) {
            htmlWriter.writeClass(getFixedHeaderTableClassName(htmlWriter));
        } else {
            htmlWriter.writeClass(getTitleRowClassName(htmlWriter));
        }

        UIColumn columns[] = tableContext.listColumns();

        /*
         * htmlWriter.writeCellPadding(0); htmlWriter.writeCellSpacing(0); if
         * (tableContext.isResizable()) { // OO2:
         * htmlWriter.writeAttribute("width", //
         * tableContext.getResizeTotalSize());
         * htmlWriter.writeStyle().writeWidth( tableContext.getTotalSize() +
         * "px"); }
         * 
         * for (int i = 0; i < columns.length; i++) { if
         * (tableContext.getColumnState(i) != AbstractGridRenderContext.VISIBLE)
         * { continue; }
         * 
         * UIColumn column = columns[i];
         * 
         * htmlWriter.startElement(IHtmlWriter.COL); String width = null; if
         * (column instanceof IWidthCapability) { width = ((IWidthCapability)
         * column).getWidth(); if (width != null) {
         * htmlWriter.writeStyle().writeWidth(getSize(width)); } }
         * 
         * htmlWriter.endElement(IHtmlWriter.COL); }
         * 
         * htmlWriter.startElement(IHtmlWriter.COL); // Fake col !
         * htmlWriter.writeId(getFakeColumnClientId(htmlWriter));
         * htmlWriter.endElement(IHtmlWriter.COL);
         * 
         * htmlWriter.startElement(IHtmlWriter.THEAD);
         * htmlWriter.startElement(IHtmlWriter.TR);
         * htmlWriter.writeClass(getTitleRowClassName(htmlWriter));
         */
        boolean first = true;
        int columnHeaderIndex = 0;
        for (int i = 0; i < columns.length; i++) {
            if (tableContext.getColumnState(i) != AbstractGridRenderContext.VISIBLE) {
                continue;
            }

            UIColumn column = columns[i];

            encodeFixedTitleCol(htmlWriter, tableContext, column, first, i,
                    ++columnHeaderIndex);
            first = false;
        }

        if (fixedHeader) {
            htmlWriter.startElement(IHtmlWriter.LI); // Fake TD
            // htmlWriter.writeId(getFakeHeadCellClientId(htmlWriter));
            htmlWriter.writeClass("f_grid_tcell f_grid_tcell_right");
            htmlWriter.write("&nbsp;");
            htmlWriter.endElement(IHtmlWriter.LI);
        }

        /*
         * htmlWriter.startElement(IHtmlWriter.TH); // Fake TD //
         * htmlWriter.writeId(getFakeHeadCellClientId(htmlWriter));
         * htmlWriter.writeClass("f_grid_tcell"); htmlWriter.write("&nbsp;");
         * htmlWriter.endElement(IHtmlWriter.TH);
         * 
         * // Fin des titres .... htmlWriter.endElement(IHtmlWriter.TR);
         * htmlWriter.endElement(IHtmlWriter.THEAD);
         * htmlWriter.startElement(IHtmlWriter.TBODY);
         * 
         * htmlWriter.endElement(IHtmlWriter.TBODY);
         * htmlWriter.endElement(IHtmlWriter.TABLE);
         */

        htmlWriter.endElement(IHtmlWriter.UL);
    }

    /*
     * private String getFakeHeadCellClientId(IHtmlWriter htmlWriter) { return
     * htmlWriter.getComponentRenderContext().getComponentClientId() +
     * FIXED_FAKE_CELL_ID_SUFFIX; }
     */

    private String getFakeColumnClientId(IHtmlWriter htmlWriter) {
        return htmlWriter.getComponentRenderContext().getComponentClientId()
                + FIXED_FAKE_COLUMN_ID_SUFFIX;
    }

    private String getTitleTableBodyClassName(IHtmlWriter htmlWriter) {
        return GRID_MAIN_STYLE_CLASS + TABLE_BODY;
    }

    protected String getTitleRowClassName(IHtmlWriter htmlWriter) {
        return GRID_MAIN_STYLE_CLASS + TITLE_ROW;
    }

    protected String getFixedHeaderTableClassName(IHtmlWriter htmlWriter) {
        return GRID_MAIN_STYLE_CLASS + "_fttitle";
    }

    protected String getFixedHeaderTableId(IHtmlWriter htmlWriter) {
        return htmlWriter.getComponentRenderContext().getComponentClientId()
                + FIXED_HEADER_ID_SUFFIX;
    }

    private void encodeFixedTitleCol(IHtmlWriter htmlWriter,
            AbstractGridRenderContext tableContext, UIColumn column,
            boolean first, int columnIndex, int columnHeaderIndex)
            throws WriterException {

        /*
         * htmlWriter.startElement(IHtmlWriter.LI);
         * 
         * htmlWriter.writeId(column.getClientId(htmlWriter
         * .getComponentRenderContext().getFacesContext()));
         * 
         * String className = getTitleCellClassName(htmlWriter, column, first,
         * tableContext.isDisabled()); htmlWriter.writeClass(className);
         * 
         * if (column instanceof IToolTipCapability) { String toolTip =
         * ((IToolTipCapability) column).getToolTipText(); if (toolTip != null)
         * { toolTip = ParamUtils.formatMessage(column, toolTip);
         * 
         * htmlWriter.writeTitle(toolTip); } }
         */

        encodeTitleCell(htmlWriter, tableContext, column, columnIndex,
                columnHeaderIndex);

        /*
         * htmlWriter.endElement(IHtmlWriter.LI);
         */
    }

    protected String getTitleCellClassName(IHtmlWriter htmlWriter,
            UIColumn column, boolean firstColumn, boolean disabled) {
        String mainClassName = GRID_MAIN_STYLE_CLASS + TITLE_CELL;

        StringAppender sa = null;

        if (column instanceof IStyleClassCapability) {
            String cl = ((IStyleClassCapability) column).getStyleClass();

            if (cl != null) {
                if (sa == null) {
                    sa = new StringAppender(mainClassName);
                }
                sa.append(' ').append(cl);
            }
        }

        /*
         * Ca sert à qq chose ? if (firstColumn) { if (sa == null) { sa = new
         * StringAppender(mainClassName); } sa.append('
         * ').append(mainClassName).append("_left"); }
         */

        if (disabled) {
            if (sa == null) {
                sa = new StringAppender(mainClassName);
            }
            sa.append(' ').append(mainClassName).append("_disabled");
        }

        if (sa != null) {
            return sa.toString();
        }

        return mainClassName;
    }

    protected void encodeTitleCellBody(IHtmlWriter htmlWriter,
            AbstractGridRenderContext tableContext, UIColumn column,
            int columnIndex) throws WriterException {
        FacesContext facesContext = htmlWriter.getComponentRenderContext()
                .getFacesContext();

        htmlWriter.startElement(IHtmlWriter.DIV);
        htmlWriter.writeClass(getTitleDivContainerClassName(htmlWriter));

        int width = tableContext.getColumnWidthInPixel(columnIndex);

        if (width > 0) {
            htmlWriter.writeStyle().writeWidthPx(
                    width - getTextRightPadding() - getTextLeftPadding());

        } else {
            String literalWidth = tableContext.getColumnWidth(columnIndex);
            if (literalWidth != null) {
                htmlWriter.writeStyle().writeWidth(literalWidth);
            }
        }

        boolean sorted = false;
        boolean hasSortImageURL = (htmlWriter.getComponentRenderContext()
                .getAttribute(GRID_HAS_SORTER_IMAGE_URL_PROPERTY) != null);
        String sortImageURL = null;
        String titleText = null;

        if (column instanceof IOrderCapability) {

            ISortedComponent sortedComponents[] = tableContext
                    .listSortedComponents();
            for (int i = 0; i < sortedComponents.length; i++) {
                if (sortedComponents[i].getComponent() != column) {
                    continue;
                }

                if (((IOrderCapability) column).isAscending()) {
                    titleText = getResourceBundleValue(htmlWriter,
                            "f_grid.DESCENDING_SORT");

                    if (hasSortImageURL) {
                        sortImageURL = getAscendingSorterImageURL(htmlWriter);
                    }

                } else {
                    titleText = getResourceBundleValue(htmlWriter,
                            "f_grid.ASCENDING_SORT");

                    if (hasSortImageURL) {
                        sortImageURL = getDescendingSorterImageURL(htmlWriter);
                    }
                }

                sorted = true;
                break;
            }
            if (hasSortImageURL) {
                sortImageURL = getNormalSorterImageURL(htmlWriter);
            }

        }

        String columnTagName = IHtmlWriter.DIV;
        boolean command = false;
        if (tableContext.getSortCommand(columnIndex) != null) {
            columnTagName = IHtmlWriter.A;
            command = true;
        }

        if (hasSortImageURL && command) {
            htmlWriter.startElement(IHtmlElements.IMG);

            htmlWriter.writeId(getTitleDivSorterId(htmlWriter, column));
            htmlWriter
                    .writeClass(getTitleDivSorterClassName(htmlWriter, column));

            if (sortImageURL == null) {
                sortImageURL = htmlWriter.getHtmlComponentRenderContext()
                        .getHtmlRenderContext().getHtmlProcessContext()
                        .getStyleSheetURI(BLANK_IMAGE_URL, true);
            }

            htmlWriter.writeSrc(sortImageURL);

            int imageWidth = getSorterImageWidth(htmlWriter);
            int imageHeight = getSorterImageHeight(htmlWriter);
            if (imageWidth >= 0 && imageHeight >= 0) {
                htmlWriter.writeWidth(imageWidth).writeHeight(imageHeight);
            }

            htmlWriter.writeAlt("");
            htmlWriter.endElement(IHtmlElements.IMG);
        }

        htmlWriter.startElement(columnTagName);

        htmlWriter.writeId(getTitleDivTextId(htmlWriter, column));
        htmlWriter.writeClass(getTitleDivTextClassName(htmlWriter, column));

        String text = null;
        if (column instanceof ITextCapability) {
            text = ((ITextCapability) column).getText();
        }

        if (command) {
            htmlWriter.writeHRef_JavascriptVoid0();

            UIComponent component = htmlWriter.getComponentRenderContext()
                    .getComponent(); // On prend le même TabIndex
            if (component instanceof ITabIndexCapability) {
                Integer tabIndex = ((ITabIndexCapability) component)
                        .getTabIndex();

                if (tabIndex != null) {
                    htmlWriter.writeTabIndex(tabIndex.intValue());
                }
            }

            if (titleText == null) {
                titleText = getResourceBundleValue(htmlWriter, "f_grid.NO_SORT");
                if (text != null) {
                    titleText = text + " " + titleText;
                }
            }

            htmlWriter.writeTitle(titleText);
        }

        String halign = null;
        if (column instanceof IAlignmentCapability) {
            halign = ((IAlignmentCapability) column).getAlignment();
        }
        if (halign == null) {
            halign = "left";
        }

        htmlWriter.writeAlign(halign);

        if (width > 0) { // SORTER

            int w = width - getTextRightPadding() - getTextLeftPadding();

            if (sorted) {
                w -= getSortPadding();
            }

            if (w < 0) {
                w = 0;
            }

            htmlWriter.writeStyle().writeWidthPx(w);
        }

        if (column instanceof IImageAccessorsCapability) {
            IContentAccessors contentAccessors = ((IImageAccessorsCapability) column)
                    .getImageAccessors(facesContext);
            if (contentAccessors instanceof IImageAccessors) {
                IImageAccessors imageAccessors = (IImageAccessors) contentAccessors;

                IContentAccessor imageAccessor = imageAccessors
                        .getImageAccessor();
                if (imageAccessor != null) {
                    String imageURL = imageAccessor.resolveURL(facesContext,
                            null, null);

                    if (imageURL != null) {
                        htmlWriter.startElement(IHtmlWriter.IMG);
                        htmlWriter
                                .writeClass(getTitleImageClassName(htmlWriter));

                        String disabledImageURL = null;

                        if (tableContext.isDisabled()) {
                            if (imageAccessors instanceof IStatesImageAccessors) {
                                IStatesImageAccessors is = (IStatesImageAccessors) imageAccessors;

                                IContentAccessor disabledImageContentAccessor = is
                                        .getDisabledImageAccessor();
                                if (disabledImageContentAccessor != null) {
                                    disabledImageURL = disabledImageContentAccessor
                                            .resolveURL(facesContext, null,
                                                    null);
                                }
                            }
                        }

                        if (disabledImageURL != null) {
                            htmlWriter.writeSrc(disabledImageURL);

                        } else {
                            htmlWriter.writeSrc(imageURL);
                        }

                        int imageWidth = ((IImageSizeCapability) column)
                                .getImageWidth();
                        if (imageWidth > 0) {
                            htmlWriter.writeWidth(imageWidth);
                        }

                        int imageHeight = ((IImageSizeCapability) column)
                                .getImageHeight();
                        if (imageHeight > 0) {
                            htmlWriter.writeHeight(imageHeight);
                        }

                        htmlWriter.endElement(IHtmlWriter.IMG);
                    }
                }
            }
        }

        // htmlWriter.startElement(IHtmlElements.SPAN);
        if (text != null && text.trim().length() > 0) {
            htmlWriter.writeText(text);

        } else {
            htmlWriter.write(' ');
            htmlWriter.writeText(ISgmlWriter.NBSP);
            htmlWriter.write(' ');
        }
        // htmlWriter.endElement(IHtmlElements.SPAN);

        htmlWriter.endElement(columnTagName);

        htmlWriter.endElement(IHtmlWriter.DIV);
    }

    protected int getSortPadding() {
        return SORT_PADDING;
    }

    protected int getTextLeftPadding() {
        return TEXT_LEFT_PADDING;
    }

    protected int getTextRightPadding() {
        return TEXT_RIGHT_PADDING;
    }

    protected String getTitleDivTextClassName(IHtmlWriter htmlWriter,
            UIColumn column) {
        return GRID_MAIN_STYLE_CLASS + TITLE_TTEXT;
    }

    protected String getTitleDivTextId(IHtmlWriter htmlWriter, UIColumn column) {
        return column.getClientId(htmlWriter.getComponentRenderContext()
                .getFacesContext()) + TITLE_TTEXT_ID_SUFFIX;
    }

    protected String getTitleDivSorterId(IHtmlWriter htmlWriter, UIColumn column) {
        return column.getClientId(htmlWriter.getComponentRenderContext()
                .getFacesContext()) + TITLE_TSORTER_ID_SUFFIX;
    }

    protected String getTitleDivSorterClassName(IHtmlWriter htmlWriter,
            UIColumn column) {
        return GRID_MAIN_STYLE_CLASS + TITLE_TSORTER;
    }

    protected String getTitleDivContainerClassName(IHtmlWriter htmlWriter) {
        return GRID_MAIN_STYLE_CLASS + TITLE_STEXT;
    }

    protected String getTitleImageClassName(IHtmlWriter htmlWriter) {
        return GRID_MAIN_STYLE_CLASS + TITLE_IMAGE;
    }

    protected void encodeJavaScript(IJavaScriptWriter htmlWriter)
            throws WriterException {
        super.encodeJavaScript(htmlWriter);

        AbstractGridRenderContext tableContext = (AbstractGridRenderContext) htmlWriter
                .getHtmlComponentRenderContext().setAttribute(TABLE_CONTEXT,
                        null);

        encodeJsHeader(htmlWriter, tableContext);
        encodeJsBody(htmlWriter, tableContext);
        encodeJsFooter(htmlWriter, tableContext);
    }

    public abstract AbstractGridRenderContext createTableContext(
            IHtmlComponentRenderContext componentRenderContext);

    protected void encodeHeader(IHtmlWriter htmlWriter,
            AbstractGridRenderContext gridRenderContext) throws WriterException {

        // Le tableau n'a pas de taille et les colonnes ne sont pas retaillable

        UIColumn columns[] = gridRenderContext.listColumns();

        UIColumn dcs[] = new UIColumn[columns.length];

        htmlWriter.startElement(IHtmlElements.COLGROUP);
        htmlWriter.writeClass(getColgroupClassName(htmlWriter));
        // Colgroup
        int is = 0;
        for (int i = 0; i < columns.length; i++) {
            UIColumn dc = columns[i];

            if (gridRenderContext.getColumnState(i) != AbstractGridRenderContext.VISIBLE) {
                continue;
            }

            dcs[is++] = dc;
            encodeTitleCol(htmlWriter, dc, gridRenderContext, i);
        }
        htmlWriter.endElement(IHtmlElements.COLGROUP);
    }

    /**
     * Accessibility : write hidden <CAPTION>
     * 
     * @param htmlWriter
     *            Writer
     * @param gridRenderContext
     *            Context
     * @param caption
     *            Caption content
     * @throws WriterException
     */
    protected void encodeHiddenCaption(IHtmlWriter htmlWriter,
            AbstractGridRenderContext gridRenderContext, String caption)
            throws WriterException {
        htmlWriter.startElement(IHtmlWriter.CAPTION);
        htmlWriter.writeClass(getHiddenCaptionClassName(htmlWriter));
        htmlWriter.writeText(caption);
        htmlWriter.endElement(IHtmlWriter.CAPTION);
    }

    /**
     * Accessibility : write hidden <THEAD>
     * 
     * @param htmlWriter
     *            Writer
     * @param gridRenderContext
     *            Context
     * @throws WriterException
     */
    protected void encodeHiddenTitle(IHtmlWriter htmlWriter,
            AbstractGridRenderContext gridRenderContext) throws WriterException {
        UIColumn columns[] = gridRenderContext.listColumns();

        htmlWriter.startElement(IHtmlWriter.THEAD);
        htmlWriter.writeClass(getHiddenTitleClassName(htmlWriter));

        htmlWriter.startElement(IHtmlWriter.TR);
        for (int i = 0; i < columns.length; i++) {
            UIColumn dc = columns[i];
            if (gridRenderContext.getColumnState(i) != AbstractGridRenderContext.VISIBLE) {
                continue;
            }

            String headerId = htmlWriter.getHtmlComponentRenderContext()
                    .getComponentClientId() + "::ch" + i;

            encodeHiddenTitleCol(htmlWriter, dc, gridRenderContext, headerId);
        }
        htmlWriter.endElement(IHtmlWriter.TR);
        htmlWriter.endElement(IHtmlWriter.THEAD);
    }

    /**
     * Accessibility : Write <TD>for a column inside the hidden <THEAD>
     * 
     * @param htmlWriter
     *            writer
     * @param column
     *            Column
     * @param gridRenderContext
     *            COntext
     * @throws WriterException
     */
    private void encodeHiddenTitleCol(IHtmlWriter htmlWriter, UIColumn column,
            AbstractGridRenderContext gridRenderContext, String headerId)
            throws WriterException {
        htmlWriter.startElement(IHtmlWriter.TH);

        if (_GENERATE_HEADERS) {
            if (headerId != null) {
                htmlWriter.writeId(headerId);
            }
        } else {
            htmlWriter.writeId(headerId); // ARIA

            htmlWriter.writeAttribute("scope", "col");
        }

        String text = null;
        if (column instanceof ITextCapability) {
            text = ((ITextCapability) column).getText();
        }
        if (text != null && text.trim().length() > 0) {
            htmlWriter.writeText(text);
        }
        htmlWriter.endElement(IHtmlWriter.TH);
    }

    private void encodeTitleCol(IHtmlWriter htmlWriter, UIColumn column,
            AbstractGridRenderContext gridRenderContext, int columnIndex)
            throws WriterException {
        htmlWriter.startElement(IHtmlWriter.COL);

        String width = gridRenderContext.getColumnWidth(columnIndex);
        if (width != null) {
            // 2: htmlWriter.writeAttribute("width", width);
            htmlWriter.writeStyle().writeWidth(getSize(width));
        }

        String halign = null;
        if (column instanceof IAlignmentCapability) {
            halign = ((IAlignmentCapability) column).getAlignment();
        }
        if (halign == null) {
            halign = "left";
        }
        htmlWriter.writeAlign(halign);

        if (column instanceof IVerticalAlignmentCapability) {
            String valign = ((IVerticalAlignmentCapability) column)
                    .getVerticalAlignment();
            if (valign != null) {
                htmlWriter.writeVAlign(valign);
            }
        }

        if (column instanceof IForegroundBackgroundColorCapability) {
            String foregroundColor = ((IForegroundBackgroundColorCapability) column)
                    .getForegroundColor();
            String backgroundColor = ((IForegroundBackgroundColorCapability) column)
                    .getBackgroundColor();

            if (foregroundColor != null || backgroundColor != null) {
                ICssWriter cssWriter = htmlWriter.writeStyle(128);
                if (foregroundColor != null) {
                    cssWriter.writeColor(foregroundColor);
                }
                if (backgroundColor != null) {
                    cssWriter.writeBackgroundColor(backgroundColor);
                }
            }
        }

        htmlWriter.endElement(IHtmlWriter.COL);
    }

    protected void encodeTitleCell(IHtmlWriter htmlWriter,
            AbstractGridRenderContext tableContext, UIColumn column,
            int columnIndex, int columnHeaderIndex) throws WriterException {
        htmlWriter.startElement(IHtmlWriter.LI);

        htmlWriter.writeRole(IAccessibilityRoles.COLUMNHEADER);
        // au propre
        htmlWriter.writeId(getDataTableId(htmlWriter) + ":columnHeader:"
                + columnHeaderIndex);

        String thClassName = getTitleCellClassName(htmlWriter, column,
                columnIndex == 0, tableContext.isDisabled());
        htmlWriter.writeClass(thClassName);

        String halign = null;
        if (column instanceof IAlignmentCapability) {
            halign = ((IAlignmentCapability) column).getAlignment();
        }
        if (halign == null) {
            halign = "left";
        }
        htmlWriter.writeAlign(halign);

        if (column instanceof IToolTipTextCapability) {
            String toolTip = ((IToolTipTextCapability) column).getToolTipText();

            if (toolTip != null) {
                toolTip = ParamUtils.formatMessage(column, toolTip);

                htmlWriter.writeTitle(toolTip);
            }
        }

        int width = tableContext.getColumnWidthInPixel(columnIndex);
        String literalWidth = tableContext.getColumnWidth(columnIndex);
        if (width > 0) {
            htmlWriter.writeStyle().writeWidthPx(width);

        } else if (literalWidth != null) {
            htmlWriter.writeStyle().writeWidth(literalWidth);
        }

        encodeTitleCellBody(htmlWriter, tableContext, column, columnIndex);

        htmlWriter.endElement(IHtmlWriter.LI);
    }

    protected void encodeBodyBegin(IHtmlWriter htmlWriter,
            AbstractGridRenderContext data) throws WriterException {

        htmlWriter.getJavaScriptEnableMode().enableOnInit();
    }

    protected void encodeBodyEnd(IHtmlWriter writer,
            AbstractGridRenderContext gridRenderContext) throws WriterException {
    }

    protected void encodeBodyTableEnd(IHtmlWriter htmlWriter,
            AbstractGridRenderContext gridRenderContext) throws WriterException {

        if (serverTitleGeneration()) {
            htmlWriter.endElement(IHtmlWriter.TBODY);

            htmlWriter.endElement(IHtmlWriter.TABLE);

            if (gridRenderContext.hasScrollBars()
                    && gridRenderContext.isHeaderVisible()) {
                htmlWriter.endElement(IHtmlWriter.DIV);
            }
        }

        htmlWriter.endElement(IHtmlWriter.DIV);
    }

    protected void encodeJsHeader(IJavaScriptWriter htmlWriter,
            AbstractGridRenderContext gridRenderContext) throws WriterException {
    }

    protected void encodeJsBody(IJavaScriptWriter jsWriter,
            AbstractGridRenderContext gridRenderContext) throws WriterException {

        encodeJsColumns(jsWriter, gridRenderContext);

        /* Si le tableau n'est pas visible ! */

        if (ENABLE_SERVER_REQUEST) {
            String interactiveComponentClientId = jsWriter
                    .getHtmlComponentRenderContext().getHtmlRenderContext()
                    .getCurrentInteractiveRenderComponentClientId();

            if (interactiveComponentClientId != null) {
                // Pas de donn�es si nous sommes dans un scope interactif !
                jsWriter.writeMethodCall("f_setInteractiveShow").write('"')
                        .write(interactiveComponentClientId).writeln("\");");
                return;
            }
        }
    }

    protected abstract void encodeJsColumns(IJavaScriptWriter htmlWriter,
            AbstractGridRenderContext gridRenderContext) throws WriterException;

    protected void encodeJsColumns(IJavaScriptWriter jsWriter,
            AbstractGridRenderContext gridRenderContext, int generationMask)
            throws WriterException {

        String defaultCellImageURLs[] = null;
        String defaultCellStyleClasses[][] = null;
        String defaultCellToolTipTexts[] = null;
        String defaultCellHorizontalAligments[] = null;
        String imageURLs[] = null;
        String disabledImageURLs[] = null;
        String hoverImageURLs[] = null;
        String selectedImageURLs[] = null;
        String columnStyleClasses[] = null;

        FacesContext facesContext = jsWriter.getFacesContext();

        ISelectedCriteria[] selectedCriteriaArray = gridRenderContext
                .listSelectedCriteria();

        if (selectedCriteriaArray != null && selectedCriteriaArray.length > 0) {
            jsWriter.writeMethodCall("fa_setSelectedCriteria").write('[');

            for (int i = 0; i < selectedCriteriaArray.length; i++) {
                ISelectedCriteria criteria = selectedCriteriaArray[i];

                UIComponent criteriaComponent = (UIComponent) criteria
                        .getConfig();

                Converter criteriaConverter = criteria.getConfig()
                        .getCriteriaConverter();

                Converter labelConverter = criteria.getConfig()
                        .getLabelConverter();

                if (i > 0) {
                    jsWriter.write(',');
                }

                IObjectLiteralWriter oj = jsWriter.writeObjectLiteral(true);

                oj.writeSymbol("id").writeString(
                        criteria.getConfig().getCriteriaContainer().getId());

                IJavaScriptWriter jsWriterOj = oj.writeSymbol("values").write(
                        '[');
                Set criteriaSet = criteria.listSelectedValues();
                boolean first = true;
                for (Iterator iterator = criteriaSet.iterator(); iterator
                        .hasNext();) {
                    Object criteriaValue = iterator.next();

                    String sValue = ValuesTools.convertValueToString(
                            criteriaValue, criteriaConverter,
                            criteriaComponent, facesContext);

                    if (first) {
                        first = false;
                    } else {
                        jsWriterOj.write(',');
                    }

                    IObjectLiteralWriter ol = jsWriterOj
                            .writeObjectLiteral(false);

                    if (criteriaValue == null) {
                        ol.writeSymbol("value").write("'")
                                .write(CriteriaTools.DEFAULT_NULL_VALUE)
                                .write("'");

                    } else {
                        ol.writeSymbol("value").writeString(sValue);
                    }

                    if (labelConverter != null) {
                        String label = ValuesTools.convertValueToString(
                                criteriaValue, labelConverter,
                                criteriaComponent, facesContext);
                        if (label != null) {
                            ol.writeSymbol("label").writeString(label);
                        }
                    }

                    ol.end();
                }
                jsWriterOj.write(']');

                oj.end();

            }

            jsWriter.writeln("], false);");
        }

        if ((generationMask & GENERATE_CELL_IMAGES) > 0) {
            defaultCellImageURLs = gridRenderContext.getDefaultCellImageURLs();
            if (defaultCellImageURLs != null) {
                defaultCellImageURLs = allocateStrings(jsWriter,
                        defaultCellImageURLs, null);
            }

            imageURLs = gridRenderContext.getCellTitleImageURLs();
            if (imageURLs != null) {
                imageURLs = allocateStrings(jsWriter, imageURLs, null);
            }

            disabledImageURLs = gridRenderContext
                    .getCellTitleDisabledImageURLs();
            if (disabledImageURLs != null) {
                disabledImageURLs = allocateStrings(jsWriter,
                        disabledImageURLs, null);
            }

            hoverImageURLs = gridRenderContext.getCellTitleHoverImageURLs();
            if (hoverImageURLs != null) {
                hoverImageURLs = allocateStrings(jsWriter, hoverImageURLs, null);
            }

            selectedImageURLs = gridRenderContext
                    .getCellTitleSelectedImageURLs();
            if (selectedImageURLs != null) {
                selectedImageURLs = allocateStrings(jsWriter,
                        selectedImageURLs, null);
            }
        }

        columnStyleClasses = gridRenderContext.getColumnStyleClasses();
        if (columnStyleClasses != null) {
            columnStyleClasses = allocateStrings(jsWriter, columnStyleClasses,
                    null);
        }

        defaultCellStyleClasses = gridRenderContext
                .getDefaultCellStyleClasses();
        if (defaultCellStyleClasses != null) {
            String s[][] = new String[defaultCellStyleClasses.length][];
            for (int i = 0; i < defaultCellStyleClasses.length; i++) {
                s[i] = allocateStrings(jsWriter, defaultCellStyleClasses[i],
                        null);
            }

            defaultCellStyleClasses = s;
        }

        defaultCellToolTipTexts = gridRenderContext
                .getDefaultCellToolTipTexts();
        if (defaultCellToolTipTexts != null) {
            defaultCellToolTipTexts = allocateStrings(jsWriter,
                    defaultCellToolTipTexts, null);
        }

        defaultCellHorizontalAligments = gridRenderContext
                .getDefaultCellHorizontalAlignments();
        if (defaultCellHorizontalAligments != null) {
            defaultCellHorizontalAligments = allocateStrings(jsWriter,
                    defaultCellHorizontalAligments, null);
        }

        UIColumn columns[] = gridRenderContext.listColumns();

        String[] titleToolTipIds = null;
        String[] titleToolTipContents = null;
        for (int i = 0; i < columns.length; i++) {
            UIColumn column = columns[i];

            ToolTipComponent toolTipComponent = null;

            if (column instanceof ITitleToolTipIdCapability) {
                String tooltipClientId = ((ITitleToolTipIdCapability) column)
                        .getTitleToolTipId();

                if (tooltipClientId != null && tooltipClientId.length() > 0) {
                    IRenderContext renderContext = jsWriter
                            .getHtmlRenderContext();

                    if (tooltipClientId.charAt(0) != ':') {
                        tooltipClientId = renderContext
                                .computeBrotherComponentClientId(jsWriter
                                        .getComponentRenderContext()
                                        .getComponent(), tooltipClientId);
                    }

                    if (tooltipClientId != null) {
                        UIComponent comp = renderContext.getFacesContext()
                                .getViewRoot().findComponent(tooltipClientId);
                        if (comp instanceof ToolTipComponent) {
                            toolTipComponent = (ToolTipComponent) comp;

                            gridRenderContext.registerTooltip(toolTipComponent);

                            if (toolTipComponent.isRendered() == false) {
                                toolTipComponent = null;
                            }
                        }
                    }
                }
            }

            if (toolTipComponent == null) {
                toolTipComponent = gridRenderContext.findTooltipByIdOrName(
                        jsWriter.getComponentRenderContext(), columns[i],
                        "#title", null);
            }

            if (toolTipComponent != null) {
            String toolTipClientId = null;
            String toolTipContent = null;
            if (toolTipComponent.getAsyncRenderMode(facesContext) == IAsyncRenderModeCapability.NONE_ASYNC_RENDER_MODE) {
                toolTipContent = encodeToolTip(jsWriter, toolTipComponent);
                toolTipClientId = "##CONTENT";

            } else {
                    toolTipClientId = toolTipComponent
                            .getClientId(facesContext);
            }

            if (toolTipClientId != null) {
                if (titleToolTipIds == null) {
                    titleToolTipIds = new String[columns.length];
                }
                    titleToolTipIds[i] = jsWriter
                            .allocateString(toolTipClientId);
            }
            if (toolTipContent != null) {
                if (titleToolTipContents == null) {
                    titleToolTipContents = new String[columns.length];
                }
                titleToolTipContents[i] = jsWriter
                        .allocateString(toolTipContent);
            }
        }

        }

        int autoFilterIndex = 0;
        jsWriter.writeMethodCall("f_setColumns2");
        for (int i = 0; i < columns.length; i++) {
            UIColumn columnComponent = columns[i];

            if (i > 0) {
                jsWriter.write(',');
            }

            IObjectLiteralWriter objectWriter = jsWriter
                    .writeObjectLiteral(true);

            String columnId = gridRenderContext.getColumnId(i);
            if (columnId != null) {
                objectWriter.writeSymbol("_id").writeString(columnId);
            }
            /*
            objectWriter.writeSymbol("_cid").writeString(
                    columnComponent.getClientId(facesContext));
*/
            String columnText = null;
            if (columnComponent instanceof ITextCapability) {
                columnText = ((ITextCapability) columnComponent).getText();
            }

            int rowState = gridRenderContext.getColumnState(i);
            if (rowState == AbstractGridRenderContext.SERVER_HIDDEN) {
                objectWriter.writeSymbol("_visibility").writeNull();

                if (columnText != null) {
                    objectWriter.writeSymbol("_text").writeString(columnText);
                }

                objectWriter.end();
                continue;

            } else if (rowState == AbstractGridRenderContext.CLIENT_HIDDEN) {
                objectWriter.writeSymbol("_visibility").writeBoolean(false);

                if (((generationMask & GENERATE_CELL_TEXT) == 0)
                        && (columnText != null)) {
                    objectWriter.writeSymbol("_text").writeString(columnText);
                }
            }

            if (columnStyleClasses != null) {
                String styleClass = columnStyleClasses[i];
                if (styleClass != null) {
                    objectWriter.writeSymbol("_styleClass").write(styleClass);
                }
            }

            if (defaultCellImageURLs != null) {
                String url = defaultCellImageURLs[i];
                if (url != null) {
                    objectWriter.writeSymbol("_defaultCellImageURL").write(url);
                }
            }

            if (columnComponent instanceof ICellImageSettings) {
                if (((ICellImageSettings) columnComponent)
                        .isCellImageURLSetted()) {
                    objectWriter.writeSymbol("_cellImage").writeBoolean(true);
                }
            }

            if ((generationMask & GENERATE_CELL_STYLE_CLASS) > 0) {
                if (columnComponent instanceof ICellStyleClassSettings) {
                    if (((ICellStyleClassSettings) columnComponent)
                            .isCellStyleClassSetted()) {
                        objectWriter.writeSymbol("_cellStyleClassSetted")
                                .writeBoolean(true);
                    }
                }
            }

            if (defaultCellStyleClasses != null) {
                String scs[] = defaultCellStyleClasses[i];
                if (scs != null) {
                    objectWriter.writeSymbol("_cellStyleClasses").write('[');

                    for (int j = 0; j < scs.length; j++) {
                        if (j > 0) {
                            jsWriter.write(',');
                        }
                        jsWriter.write(scs[j]);
                    }

                    jsWriter.write(']');
                }
            }

            if (columnComponent instanceof IAutoFilterCapability) {
                if (((IAutoFilterCapability) columnComponent).isAutoFilter()) {
                    objectWriter.writeSymbol("_autoFilter").writeInt(
                            autoFilterIndex++);
                }
            }

            if (columnComponent instanceof IMenuPopupIdCapability) {
                String menuPopupId = ((IMenuPopupIdCapability) columnComponent)
                        .getMenuPopupId();
                if (menuPopupId != null) {
                    objectWriter.writeSymbol("_menuPopupId").writeString(
                            menuPopupId);
                }
            }

            if (columnComponent instanceof ICriteriaContainer) {
                // ((ICriteriaContainer)
                // columnComponent).getCriteriaConfiguration().getCriteriaValue();
                ICriteriaConfiguration criteriaConfiguration = ((ICriteriaContainer) columnComponent)
                        .getCriteriaConfiguration();

                if (criteriaConfiguration != null) {
                    objectWriter.writeSymbol("_criteriaCardinality").writeInt(
                            criteriaConfiguration.getCriteriaCardinality());

                    String criteriaTitle = criteriaConfiguration
                            .getCriteriaTitle();
                    if (criteriaTitle != null) {
                        objectWriter.writeSymbol("_criteriaTitle").writeString(
                                criteriaTitle);
                    }
                }
            }

            boolean hasToolTip = false;

            if (defaultCellToolTipTexts != null) {
                String tooltip = defaultCellToolTipTexts[i];
                if (tooltip != null) {
                    objectWriter.writeSymbol("_cellToolTipText").writeString(
                            tooltip);
                    hasToolTip = true;
                }
            }

            if (hasToolTip == false && titleToolTipIds != null) {
                String tooltip = titleToolTipIds[i];
                if (tooltip != null) {
                    objectWriter.writeSymbol("_titleToolTipId").write(tooltip);
                    hasToolTip = true;

                    if (titleToolTipContents != null) {
                        String tooltipContent = titleToolTipContents[i];
                        if (tooltipContent != null) {
                            objectWriter.writeSymbol("_titleToolTipContent")
                                    .write(tooltipContent);
                        }
                    }
                }
            }

            if (defaultCellHorizontalAligments != null) {
                String halign = defaultCellHorizontalAligments[i];
                if (halign != null) {
                    objectWriter.writeSymbol("_align").write(halign);
                }
            }

            if (imageURLs != null) {
                String imageURL = imageURLs[i];
                if (imageURL != null) {
                    objectWriter.writeSymbol("_titleImageURL").write(imageURL);
                }
            }

            if (disabledImageURLs != null) {
                String disabledImageURL = disabledImageURLs[i];
                if (disabledImageURL != null) {
                    objectWriter.writeSymbol("_titleDisabledImageURL").write(
                            disabledImageURL);
                }
            }

            if (selectedImageURLs != null) {
                String selectedImageURL = selectedImageURLs[i];
                if (selectedImageURL != null) {
                    objectWriter.writeSymbol("_titleSelectedImageURL").write(
                            selectedImageURL);
                }
            }

            if (hoverImageURLs != null) {
                String hoverImageURL = hoverImageURLs[i];
                if (hoverImageURL != null) {
                    objectWriter.writeSymbol("_titleHoverImageURL").write(
                            hoverImageURL);
                }
            }

            if (gridRenderContext.isResizable()) {
                if (columnComponent instanceof IResizableCapability) {
                    if (((IResizableCapability) columnComponent).isResizable()) {
                        objectWriter.writeSymbol("_resizable").writeBoolean(
                                true);

                        if (columnComponent instanceof IWidthRangeCapability) {
                            int min = ((IWidthRangeCapability) columnComponent)
                                    .getMinWidth();
                            if (min > 0) {
                                objectWriter.writeSymbol("_minWidth").writeInt(
                                        min);
                            }

                            int max = ((IWidthRangeCapability) columnComponent)
                                    .getMaxWidth();
                            if (max > 0) {
                                objectWriter.writeSymbol("_maxWidth").writeInt(
                                        max);
                            }
                        }
                    }
                }
            }

            if ((generationMask & GENERATE_CELL_WIDTH) > 0) {
                if (columnComponent instanceof IWidthCapability) {
                    String width = ((IWidthCapability) columnComponent)
                            .getWidth();
                    if (width != null) {
                        objectWriter.writeSymbol("_width").writeString(width);
                    }
                }
            }

            if ((generationMask & GENERATE_CELL_TEXT) > 0) {
                if (columnText != null) {
                    objectWriter.writeSymbol("_text").writeString(columnText);
                }
                if (columnComponent instanceof IToolTipTextCapability) {
                    String toolTip = ((IToolTipTextCapability) columnComponent)
                            .getToolTipText();
                    if (toolTip != null) {
                        objectWriter.writeSymbol("_toolTip").writeString(
                                toolTip);
                    }
                }
            }

            String scopeColId = gridRenderContext.getScopeColId();
            if (scopeColId != null
                    && scopeColId.equals(columnComponent.getId())) {
                objectWriter.writeSymbol("_scopeCol").writeBoolean(true);
            }

            writeGridColumnProperties(objectWriter, gridRenderContext,
                    columnComponent, i);

            objectWriter.end();
        }
        jsWriter.writeln(");");

        ISortedComponent sortedComponents[] = gridRenderContext
                .listSortedComponents();
        if (sortedComponents != null && sortedComponents.length > 0) {
            jsWriter.writeMethodCall("f_enableSorters");
            int pred = 0;
            for (int j = 0; j < sortedComponents.length; j++) {
                ISortedComponent sortedComponent = sortedComponents[j];

                if (j > 0) {
                    for (; pred > 0; pred--) {
                        jsWriter.write(',').writeNull();
                    }

                    jsWriter.write(',');
                }
                jsWriter.writeInt(sortedComponent.getIndex());

                if (sortedComponent.isAscending()) {
                    jsWriter.write(',').writeBoolean(true);

                } else {
                    pred++;
                }
            }
            jsWriter.writeln(");");
        }
    }

    protected void writeGridColumnProperties(IObjectLiteralWriter objectWriter,
            AbstractGridRenderContext tableContext, UIColumn columnComponent,
            int columnIndex) throws WriterException {

        Map<String, FacesListener[]> listenersByType = ListenerTools
                .getListenersByType(ListenerTools.ATTRIBUTE_NAME_SPACE,
                        columnComponent);

        if (listenersByType.isEmpty() == false) {
            StringAppender sa = new StringAppender(128);

            listenersByType.remove(ListenerTools.ATTRIBUTE_NAME_SPACE
                    .getSortEventName());

            appendAttributeEventForm(sa, objectWriter.getParent().getWriter(),
                    listenersByType);

            if (sa.length() > 0) {
                IJavaScriptWriter jsWriter = objectWriter
                        .writeSymbol("_events");

                jsWriter.writeString(sa.toString());
            }
        }

        Object sort = tableContext.getSortCommand(columnIndex);
        if (sort != null) {
            String command = null;

            if (sort instanceof String) {
                if (tableContext.getSortClientSide(columnIndex) == false) {
                    command = tableContext
                            .translateJavascriptMethod(SORT_SERVER_COMMAND);

                } else {
                    command = ((String) sort).trim();
                }

            } else if (sort instanceof IScriptListener) {
                IScriptListener scriptListener = (IScriptListener) sort;

                command = scriptListener.getCommand();

            } else if (sort instanceof IServerActionListener) {
                // Le tri se fait coté serveur !

                command = tableContext
                        .translateJavascriptMethod(SORT_SERVER_COMMAND);
            }

            if (command != null) {
                IJavaScriptWriter jsWriter = objectWriter
                        .writeSymbol("_sorter");

                if (Constants.VERIFY_SORT_COMMAND) {
                    String delimiters = " (),;:";
                    StringTokenizer st = new StringTokenizer(command,
                            delimiters, true);
                    if (st.countTokens() > 1) {
                        throw new FacesException(
                                "The comparator must be a function name ! ('"
                                        + command + "')");
                    }
                }

                jsWriter.write(command);
            }
        }
    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.8 $ $Date: 2013/12/19 15:46:45 $
     */
    private interface IBooleanStateCallback {
        boolean test(AbstractGridRenderContext tableContext, int index);
    }

    private static final IBooleanStateCallback CELL_STYLE_CLASS = new IBooleanStateCallback() {

        public boolean test(AbstractGridRenderContext tableContext, int index) {
            return tableContext.isCellStyleClass(index);
        }
    };

    private static final IBooleanStateCallback CELL_TOOLTIP_TEXT = new IBooleanStateCallback() {

        public boolean test(AbstractGridRenderContext tableContext, int index) {
            return tableContext.isCellToolTipText(index);
        }
    };

    private void writeBooleanArray(IJavaScriptWriter htmlWriter,
            AbstractGridRenderContext tableContext, int cnt,
            IBooleanStateCallback callback) throws WriterException {
        int pred = 0;
        boolean first = true;
        for (int i = 0; i < cnt; i++) {

            if (tableContext.getColumnState(i) != AbstractGridRenderContext.VISIBLE) {
                continue;
            }

            if (pred > 0) {
                if (first) {
                    pred--;
                    htmlWriter.writeBoolean(false).write(',');
                    first = false;
                }

                for (; pred > 0; pred--) {
                    htmlWriter.write(',').writeBoolean(false);
                }
            }

            if (first) {
                first = false;

            } else {
                htmlWriter.write(',');
            }

            htmlWriter.writeBoolean(callback.test(tableContext, i));
        }
    }

    protected final String[] allocateStrings(IJavaScriptWriter htmlWriter,
            String[] values, String ret[]) throws WriterException {

        if (values == null) {
            return null;
        }

        if (ret == null) {
            ret = new String[values.length];
        }

        for (int i = 0; i < values.length; i++) {
            if (values[i] == null) {
                continue;
            }

            ret[i] = htmlWriter.allocateString(values[i]);
        }

        return ret;
    }

    protected void encodeJsFooter(IJavaScriptWriter htmlWriter,
            AbstractGridRenderContext data) {
    }

    public boolean getDecodesChildren() {
        return true;
    }

    public boolean getRendersChildren() {
        return true;
    }

    protected void addUnlockProperties(Set<Serializable> unlockedProperties) {
        super.addUnlockProperties(unlockedProperties);

        unlockedProperties.add("serializedIndexes");
        unlockedProperties.add("first");
        unlockedProperties.add("sortIndex");
        unlockedProperties.add("columnWidths");
        unlockedProperties.add("filterExpression");
    }

    protected void decode(IRequestContext context, UIComponent component,
            IComponentData componentData) {
        super.decode(context, component, componentData);

        FacesContext facesContext = context.getFacesContext();

        IGridComponent gridComponent = (IGridComponent) component;
        if (component instanceof UIData2) {
            String serializedIndexes = componentData
                    .getStringProperty("serializedIndexes");
            
            if (serializedIndexes == null) {
            	/* On recupère les indexes par la request map
            	 * 
            	 *  typiquement le comportement d'une request ajax*/
            	
            	Map parameters = facesContext.getExternalContext()
                        .getRequestParameterMap();
            	serializedIndexes = (String) parameters.get("serializedIndexes");
            }
            if (serializedIndexes != null) {
                for (StringTokenizer st = new StringTokenizer(
                        serializedIndexes, ","); st.hasMoreTokens();) {

                    int first = Integer.parseInt(st.nextToken());
                    int rows = Integer.parseInt(st.nextToken());

                    ((UIData2) component).addDecodedIndexes(first, rows);
                }
            }
            
        }

        Number first = componentData.getNumberProperty("first");
        if (first != null) {
            int old = gridComponent.getFirst();

            int f = first.intValue();
            if (old != f) {
                gridComponent.setFirst(f);

                component.queueEvent(new PropertyChangeEvent(component,
                        Properties.FIRST, new Integer(old), first));
            }
       
        }

        if (gridComponent instanceof ISortedChildrenCapability) {
            ISortedChildrenCapability sortedChildrenCapability = (ISortedChildrenCapability) gridComponent;

            String sortIndex = componentData.getStringProperty("sortIndex");
            if (sortIndex != null) {
                UIColumn columns[] = gridComponent.listColumns().toArray();

                List<UIColumn> sortedColumns = new ArrayList<UIColumn>(
                        columns.length);
                StringTokenizer st1 = new StringTokenizer(sortIndex, ",");

                for (; st1.hasMoreTokens();) {
                    String tok1 = st1.nextToken();
                    String tok2 = st1.nextToken();

                    int idx = Integer.parseInt(tok1);
                    boolean order = "true".equalsIgnoreCase(tok2);

                    UIColumn dataColumn = columns[idx];

                    sortedColumns.add(dataColumn);

                    if ((dataColumn instanceof IOrderCapability) == false) {
                        continue;
                    }

                    IOrderCapability orderCapability = (IOrderCapability) dataColumn;

                    if (orderCapability.isAscending() == order) {
                        continue;
                    }

                    orderCapability.setAscending(order);

                    dataColumn.queueEvent(new PropertyChangeEvent(dataColumn,
                            Properties.ASCENDING, Boolean.valueOf(!order),
                            Boolean.valueOf(order)));
                }

                UIComponent old[] = sortedChildrenCapability
                        .getSortedChildren();

                UIComponent news[] = sortedColumns
                        .toArray(new UIComponent[sortedColumns.size()]);

                sortedChildrenCapability.setSortedChildren(news);

                if (isEquals(old, news) == false) {
                    component.queueEvent(new PropertyChangeEvent(component,
                            Properties.SORTED_CHILDREN, old, news));
                }
            }
        }

        String columnWidths = componentData.getStringProperty("columnWidths");
        if (columnWidths != null) {
            StringTokenizer st = new StringTokenizer(columnWidths, ",");
            IColumnIterator it = gridComponent.listColumns();

            for (; st.hasMoreTokens();) {
                String width = st.nextToken();

                for (; it.hasNext();) {
                    UIColumn col = it.next();

                    if ((col instanceof IResizableCapability) == false) {
                        continue;
                    }

                    if (((IResizableCapability) col).isResizable() == false) {
                        continue;
                    }

                    if ((col instanceof IWidthCapability) == false) {
                        continue;
                    }

                    String old = ((IWidthCapability) col).getWidth();
                    if (isEquals(old, width)) {
                        break;
                    }

                    ((IWidthCapability) col).setWidth(width);

                    col.queueEvent(new PropertyChangeEvent(col,
                            Properties.WIDTH, old, width));
                    break;
                }
            }
        }

        if (gridComponent instanceof IFilterCapability) {
            IFilterCapability filterCapability = (IFilterCapability) gridComponent;

            String filterExpression = componentData
                    .getStringProperty("filterExpression");
            if (filterExpression != null) {
                if (filterExpression.length() < 1) {
                    filterExpression = null;
                }

                IFilterProperties fexp = HtmlTools.decodeFilterExpression(
                        context.getProcessContext(), component,
                        filterExpression);

                IFilterProperties old = filterCapability.getFilterProperties();
                if (isEquals(fexp, old) == false) {
                    filterCapability.setFilterProperties(fexp);

                    component.queueEvent(new PropertyChangeEvent(component,
                            Properties.FILTER_PROPERTIES, old, fexp));
                }
            }
        }

        if (gridComponent instanceof IPreferencesCapability) {
            IPreferencesCapability preferenceCapability = (IPreferencesCapability) gridComponent;

            IComponentPreferences preferences = preferenceCapability
                    .getPreferences();

            if (preferences == null
                    && (gridComponent instanceof IPreferencesSettings)) {
                if (((IPreferencesSettings) gridComponent)
                        .isPreferencesSetted()) {

                    preferences = new GridPreferences();

                    preferenceCapability.setPreferences(preferences);
                }
            }
            if (preferences != null) {
                preferences.savePreferences(facesContext,
                        (UIComponent) preferenceCapability);
            }
        }

        if (gridComponent instanceof IShowValueCapability) {
            ((IShowValueCapability) gridComponent).setShowValue(null);
        }
        
    }

    protected void decodeEvent(IRequestContext context, UIComponent component,
            IEventData eventData) {

        if (eventData != null
                && JavaScriptClasses.EVENT_VALUE_CHANGE.equals(eventData
                        .getEventName())) {

            // Ok on change de page ...
            return;
        }

        super.decodeEvent(context, component, eventData);
    }

    protected static final int[] parseIndexes(String indexes) {
        if (indexes == null) {
            return EMPTY_INDEXES;
        }
        StringTokenizer st = new StringTokenizer(indexes,
                HtmlTools.LIST_SEPARATORS);

        int cnt = st.countTokens();
        if (cnt < 1) {
            return EMPTY_INDEXES;
        }

        int ret[] = new int[cnt];

        int idx = 0;
        for (; st.hasMoreTokens();) {
            String s_index = st.nextToken();
            try {
                int index = Integer.parseInt(s_index);
                ret[idx++] = index;

            } catch (NumberFormatException ex) {
                throw new FacesException("Can not parse index '" + s_index
                        + ".", ex);
            }
        }

        for (; idx < ret.length;) {
            ret[idx++] = -1;
        }

        return ret;
    }

    protected void setCheckedIndexes(FacesContext facesContext,
            ICheckRangeComponent checkComponent, int[] indexes, int uindexes[],
            boolean uncheckAll) {

        if (uncheckAll) {
            checkComponent.uncheckAll();

        } else if (uindexes.length > 0) {
            checkComponent.uncheck(uindexes);
        }

        if (indexes.length > 0) {
            checkComponent.check(indexes);
        }
    }

    protected void setAdditionalIndexes(FacesContext facesContext,
            IAdditionalInformationComponent additionalInformationComponent,
            int[] indexes, int uindexes[], boolean all) {

        if (all) {
            additionalInformationComponent.hideAllAdditionalInformations();

        } else if (uindexes.length > 0) {
            additionalInformationComponent.hideAdditionalInformation(uindexes);
        }

        if (indexes.length > 0) {
            additionalInformationComponent.showAdditionalInformation(indexes);
        }
    }

    protected void setSelectedIndexes(FacesContext facesContext,
            ISelectionRangeComponent selectionComponent, int[] indexes,
            int dindexes[], boolean deselectAll) {

        if (deselectAll) {
            selectionComponent.deselectAll();

        } else if (dindexes.length > 0) {
            selectionComponent.deselect(dindexes);
        }

        if (indexes.length > 0) {
            selectionComponent.select(indexes);
        }
    }

    protected boolean hasComponenDecoratorSupport() {
        return true;
    }

    protected IComponentDecorator createComponentDecorator(
            FacesContext facesContext, UIComponent component) {

        IComponentDecorator decorator = null;

        IGridComponent gridComponent = (IGridComponent) component;

        if (gridComponent instanceof IMenuCapability) {
            IMenuIterator menuIterator = ((IMenuCapability) gridComponent)
                    .listMenus();

            for (; menuIterator.hasNext();) {
                MenuComponent menuComponent = menuIterator.next();

                IComponentDecorator menuDecorator = new SubMenuDecorator(
                        menuComponent, menuComponent.getMenuId(), null,
                        menuComponent.isRemoveAllWhenShown(facesContext),
                        getItemImageWidth(menuComponent),
                        getItemImageHeight(menuComponent));

                if (decorator == null) {
                    decorator = menuDecorator;
                    continue;
                }

                menuDecorator.addChildDecorator(decorator);
                decorator = menuDecorator;
            }
        }

        IColumnIterator columnIterator = gridComponent.listColumns();
        for (; columnIterator.hasNext();) {
            UIColumn column = columnIterator.next();
            if (column instanceof IMenuCapability) {
                IMenuIterator menuIterator = ((IMenuCapability) column)
                        .listMenus();

                for (; menuIterator.hasNext();) {
                    MenuComponent menuComponent = menuIterator.next();

                    String id = menuComponent.getMenuId();
                    if (null == id || id.length() < 1) {
                        id = column.getId() + "_menu";
                    }

                    if (menuComponent instanceof IPreSelectionEventCapability) {

                        ((IPreSelectionEventCapability) menuComponent)
                                .addPreSelectionListener(new PreSelectionScriptListener(
                                        HtmlRenderContext.JAVASCRIPT_TYPE,
                                        "f_criteriaPopupManager.OnPreSelectedCriteriaChange(event)"));
                    }

                    if (menuComponent instanceof ICheckEventCapability) {
                        ((ICheckEventCapability) menuComponent)
                                .addCheckListener(new CheckScriptListener(
                                        HtmlRenderContext.JAVASCRIPT_TYPE,
                                        "f_criteriaPopupManager.OnSelectedCriteriaChange(event)"));
                    }

                    if (column instanceof IMenuPopupIdCapability) {
                        ((IMenuPopupIdCapability) column).setMenuPopupId(id);
                    }

                    IComponentDecorator menuDecorator = new CriteriaMenuDecorator(
                            menuComponent, id, null,
                            menuComponent.isRemoveAllWhenShown(facesContext),
                            getItemImageWidth(menuComponent),
                            getItemImageHeight(menuComponent));

                    if (decorator == null) {
                        decorator = menuDecorator;
                        continue;
                    }
                    menuDecorator.addChildDecorator(decorator);
                    decorator = menuDecorator;
                }

            }
        }

        return decorator;
    }

    protected int getItemImageHeight(IMenuComponent menuComponent) {
        return -1;
    }

    protected int getItemImageWidth(IMenuComponent menuComponent) {
        return -1;
    }

    protected void writeFullStates(IJavaScriptWriter jsWriter,
            AbstractGridRenderContext context, String jsCommand, Set objects)
            throws WriterException {
    }

    protected void writeFullStates(IJavaScriptWriter jsWriter,
            String jsCommand, int[] indexes) throws WriterException {

        if (indexes == null || indexes.length < 1) {
            return;
        }

        jsWriter.writeMethodCall(jsCommand).write('[');
        for (int i = 0; i < indexes.length; i++) {
            if (i > 0) {
                jsWriter.write(',');
            }

            jsWriter.writeInt(indexes[i]);
        }

        jsWriter.writeln("]);");
    }

    protected IHtmlWriter writeTabIndex(IHtmlWriter writer,
            ITabIndexCapability tabIndexCapability) throws WriterException {
        // Do nothing : check the v:tabIndex attribute
        return writer;
    }

    protected String getAscendingSorterImageURL(IHtmlComponentWriter writer) {
        return null;
    }

    protected String getDescendingSorterImageURL(IHtmlComponentWriter writer) {
        return null;
    }

    protected String getNormalSorterImageURL(IHtmlComponentWriter writer) {
        return null;
    }

    protected int getSorterImageHeight(IHtmlComponentWriter htmlWriter) {
        return -1;
    }

    protected int getSorterImageWidth(IHtmlComponentWriter htmlWriter) {
        return -1;
    }
    
    //behavior
    abstract protected void appendGridUpdateBehavior(IHtmlWriter htmlWriter);
    
    protected void appendAdditionalInformationBehavior(IHtmlWriter htmlWriter) {
    	
    	IComponentRenderContext renderContext = htmlWriter.getComponentRenderContext();
		
    	AdditionalInformationBehaviorListener.addAjaxBehavior((UIComponentBase) renderContext.getComponent(),
				renderContext.getFacesContext());
    }

    protected void appendTooltipBehavior(IHtmlWriter htmlWriter) {
    	
    	IComponentRenderContext renderContext = htmlWriter.getComponentRenderContext();
		
    	TooltipBehaviorListener.addAjaxBehavior((UIComponentBase) renderContext.getComponent(),
				renderContext.getFacesContext());
    }  
}