/*
 * $Id: AbstractComponentsListRenderer.java,v 1.3 2013/12/19 15:46:45 jbmeslin Exp $
 */
package org.rcfaces.renderkit.html.internal.renderer;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.faces.component.UIColumn;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.model.DataModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.component.ComponentsListComponent;
import org.rcfaces.core.component.iterator.IMenuIterator;
import org.rcfaces.core.event.PropertyChangeEvent;
import org.rcfaces.core.internal.component.Properties;
import org.rcfaces.core.internal.renderkit.IComponentData;
import org.rcfaces.core.internal.renderkit.IComponentRenderContext;
import org.rcfaces.core.internal.renderkit.IComponentWriter;
import org.rcfaces.core.internal.renderkit.IRenderContext;
import org.rcfaces.core.internal.renderkit.IRequestContext;
import org.rcfaces.core.internal.renderkit.WriterException;
import org.rcfaces.core.internal.tools.ComponentTools;
import org.rcfaces.core.model.IComponentRefModel;
import org.rcfaces.core.model.ISortedComponent;
import org.rcfaces.renderkit.html.internal.AbstractCssRenderer;
import org.rcfaces.renderkit.html.internal.ICssWriter;
import org.rcfaces.renderkit.html.internal.IHtmlWriter;
import org.rcfaces.renderkit.html.internal.IJavaScriptRenderContext;
import org.rcfaces.renderkit.html.internal.JavaScriptClasses;
import org.rcfaces.renderkit.html.internal.ns.INamespaceConfiguration;
import org.rcfaces.renderkit.html.internal.service.ComponentsListService;
import org.rcfaces.renderkit.html.internal.service.ComponentsListUpdateBehaviorListener;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/12/19 15:46:45 $
 */
public abstract class AbstractComponentsListRenderer extends
        AbstractCssRenderer {

    private static final Log LOG = LogFactory
            .getLog(AbstractComponentsListRenderer.class);

    private static final String[] STRING_EMPTY_ARRAY = new String[0];

    private static final boolean ENABLE_SERVER_REQUEST = true;

    private static final String LIST_CONTEXT = "componentsList.listContext";

    protected void encodeBegin(IComponentWriter writer) throws WriterException {
        super.encodeBegin(writer);

        IComponentRenderContext componentRenderContext = writer
                .getComponentRenderContext();

        FacesContext facesContext = componentRenderContext.getFacesContext();

        ComponentsListComponent componentsListComponent = (ComponentsListComponent) componentRenderContext
                .getComponent();

        componentsListComponent.setRowIndex(-1);

        ListContext listContext = new ListContext(facesContext,
                componentsListComponent);
        componentRenderContext.setAttribute(LIST_CONTEXT, listContext);

        IHtmlWriter htmlWriter = (IHtmlWriter) writer;

        htmlWriter.startElement(IHtmlWriter.DIV, componentsListComponent);
        writeHtmlAttributes(htmlWriter);
        writeJavaScriptAttributes(htmlWriter);
        writeCssAttributes(htmlWriter);

        if (ENABLE_SERVER_REQUEST) {
            ComponentsListService componentsListServer = ComponentsListService
                    .getInstance(facesContext);
            if (componentsListServer != null) {
                htmlWriter.writeAttributeNS("asyncRender", true);
            }

            /* Si le tableau n'est pas visible ! */

            String interactiveComponentClientId = htmlWriter
                    .getHtmlComponentRenderContext().getHtmlRenderContext()
                    .getCurrentInteractiveRenderComponentClientId();

            if (interactiveComponentClientId != null) {
                // Pas de donn�es si nous sommes dans un scope interactif !
                htmlWriter.writeAttributeNS("interactiveShow",
                        interactiveComponentClientId);

                listContext.setInteractiveShow(true);
            }
        }

        /*
         * if (listContext.getDataModel() instanceof IFiltredDataModel) {
         * htmlWriter.writeAttributeNS("filtred", "true");
         * 
         * IFilterProperties filterMap = listContext.getFiltersMap(); if
         * (filterMap != null && filterMap.isEmpty() == false) { String
         * filterExpression = HtmlTools .encodeFilterExpression(filterMap);
         * htmlWriter.writeAttributeNS("filterExpression", filterExpression); }
         * }
         */

        int rows = listContext.getRows();
        if (rows > 0) {
            htmlWriter.writeAttributeNS("rows", rows);
        }

        int rowCount = listContext.getRowCount();
        if (rowCount >= 0) {
            htmlWriter.writeAttributeNS("rowCount", rowCount);
        }

        int first = listContext.getFirst();
        if (first > 0) {
            htmlWriter.writeAttributeNS("first", first);
        }

        encodeListBegin(htmlWriter, componentsListComponent, listContext);
    }

    protected String getTBodyClassName(IHtmlWriter htmlWriter) {
        return getMainStyleClassName() + "_tbody";
    }

    public boolean getRendersChildren() {
        return true;
    }

    public void encodeChildren(FacesContext facesContext, UIComponent component)
            throws IOException {

        IRenderContext renderContext = getRenderContext(facesContext);

        IHtmlWriter htmlWriter = (IHtmlWriter) renderContext
                .getComponentWriter();

        IComponentRenderContext componentRenderContext = htmlWriter
                .getComponentRenderContext();

        ListContext listContext = (ListContext) componentRenderContext
                .getAttribute(LIST_CONTEXT);

        // Dans tous les cas il faut positionner le renderContext !
        ComponentsListService componentsListServer = ComponentsListService
                .getInstance(facesContext);
        if (componentsListServer != null) {
            componentsListServer.setupComponent(componentRenderContext);
        }

        if (listContext.isInteractiveShow()) {
            return;
        }

        encodeChildren(htmlWriter, listContext);
    }

    public int encodeChildren(IComponentWriter writer, ListContext listContext)
            throws WriterException {
        FacesContext facesContext = writer.getComponentRenderContext()
                .getFacesContext();
        IComponentRenderContext componentRenderContext = writer
                .getComponentRenderContext();

        ComponentsListComponent componentsListComponent = (ComponentsListComponent) componentRenderContext
                .getComponent();

        IHtmlWriter htmlWriter = (IHtmlWriter) writer;

        DataModel dataModel = componentsListComponent.getDataModelValue();

        IComponentRefModel componentRefModel = getAdapter(
                IComponentRefModel.class, dataModel);

        if (componentRefModel != null) {
            componentRefModel.setComponent(componentsListComponent);
        }

        // boolean filtred = false;
        /*
         * DataModel dataModel = listContext.getDataModel();
         * 
         * IFilterProperties filtersMap = listContext.getFiltersMap(); if
         * (filtersMap != null) { if (dataModel instanceof IFiltredDataModel) {
         * IFiltredDataModel filtredDataModel = (IFiltredDataModel) dataModel;
         * 
         * filtredDataModel.setFilter(filtersMap); listContext.updateRowCount();
         * 
         * filtred = true; } else { dataModel =
         * FiltredDataModel.filter(dataModel, filtersMap);
         * listContext.updateRowCount(); } } else if (dataModel instanceof
         * IFiltredDataModel) { IFiltredDataModel filtredDataModel =
         * (IFiltredDataModel) dataModel;
         * 
         * filtredDataModel.setFilter(FilterExpressionTools.EMPTY);
         * listContext.updateRowCount();
         * 
         * filtred = true; }
         */

        UIColumn columns[] = listContext.listColumns();

        // Debut
        int rowIndex = listContext.getFirst();

        // Nombre de ligne a lire !
        int rows = listContext.getRows();

        int columnNumber = listContext.getColumnNumber();

        int rowCount = listContext.getRowCount();

        String rcls = componentsListComponent.getRowStyleClass(facesContext);
        String rowClasses[] = parseClasses(rcls);

        // String ccls =
        // componentsListComponent.getColumnStyleClass(facesContext);
        // String columnClasses[] = parseClasses(ccls);

        String tdClass = getCellClassName(htmlWriter);

        int count = listContext.getRowCount();

        Map<String, Object> varContext = facesContext.getExternalContext()
                .getRequestMap();
        String rowCountVar = listContext.getRowCountVar();
        if (rowCountVar != null) {
            varContext.put(rowCountVar, new Integer(count));
        }

        String rowIndexVar = listContext.getRowIndexVar();

        try {
            int processed = 0;

            encodeChildrenComponentBegin(htmlWriter, componentsListComponent,
                    processed, columnNumber, rowClasses, tdClass);

            for (; rows <= 0 || processed < rows; processed++, rowIndex++) {

                componentsListComponent.setRowIndex(rowIndex);
                if (componentsListComponent.isRowAvailable() == false) {
                    if (rowCount >= 0) {
                        return rowCount;
                    }
                    return rowIndex;
                }

                if (rowIndexVar != null) {
                    varContext.put(rowIndexVar, new Integer(processed));
                }

                encodeComponentBegin(htmlWriter, componentsListComponent,
                        processed, columnNumber, rowClasses, tdClass);

                htmlWriter.endComponent();

                UIColumn column = columns[processed % columns.length];

                ComponentTools.encodeChildrenRecursive(facesContext, column);

                htmlWriter.writeln();

                encodeComponentEnd(htmlWriter, componentsListComponent,
                        processed, columnNumber, rowClasses, tdClass);
            }

            encodeChildrenComponentEnd(htmlWriter, componentsListComponent,
                    processed, columnNumber, rowClasses, tdClass);

            if (rowCount < 0) {
                componentsListComponent.setRowIndex(rowIndex);
                if (componentsListComponent.isRowAvailable() == false) {
                    rowCount = rowIndex;
                }
            }

            return rowCount;

        } finally {
            componentsListComponent.setRowIndex(-1);
        }
    }

    protected String getCellClassName(IHtmlWriter htmlWriter) {
        return getMainStyleClassName() + "_cell";
    }

    public void encodeEnd(IComponentWriter writer) throws WriterException {

        ComponentsListComponent componentsListComponent = (ComponentsListComponent) writer
                .getComponentRenderContext().getComponent();

        componentsListComponent.setRowIndex(-1);

        IHtmlWriter htmlWriter = (IHtmlWriter) writer;

        encodeListEnd(htmlWriter, componentsListComponent);

        htmlWriter.endElement(IHtmlWriter.DIV);

        htmlWriter.getJavaScriptEnableMode().enableOnInit();

        super.encodeEnd(htmlWriter);
    }

    protected final String[] parseClasses(String classes) {

        if (classes == null || classes.length() < 1) {
            return STRING_EMPTY_ARRAY;
        }

        List<String> l = null;
        StringTokenizer st = new StringTokenizer(classes, ",");
        for (; st.hasMoreTokens();) {
            String cls = st.nextToken();

            if (l == null) {
                l = new ArrayList<String>(4);
            }

            l.add(cls);
        }

        if (l == null) {
            return STRING_EMPTY_ARRAY;
        }

        return l.toArray(new String[l.size()]);
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.rcfaces.core.internal.renderkit.html.AbstractHtmlRenderer#
     * getJavaScriptClassName()
     */
    protected String getJavaScriptClassName() {
        return JavaScriptClasses.COMPONENTS_LIST;
    }

    protected void writeCustomCss(IHtmlWriter componentWriter,
            ICssWriter cssWriter) {
        super.writeCustomCss(componentWriter, cssWriter);

        IComponentRenderContext componentRenderContext = componentWriter
                .getComponentRenderContext();
        ComponentsListComponent componentsListComponent = (ComponentsListComponent) componentRenderContext
                .getComponent();

        FacesContext facesContext = componentRenderContext.getFacesContext();

        if (componentsListComponent.isBorder(facesContext) == false) {
            cssWriter.writeBorderStyle(ICssWriter.NONE);
        }

        String w = componentsListComponent.getWidth(facesContext);
        String h = componentsListComponent.getHeight(facesContext);
        if (w != null || h != null) {
            cssWriter.writeSize(componentsListComponent);
            if (h != null) {
                cssWriter.writeOverflow(ICssWriter.AUTO);
            }
        }

        cssWriter.writeMargin(componentsListComponent);
    }

    protected void addUnlockProperties(Set<Serializable> unlockedProperties) {
        super.addUnlockProperties(unlockedProperties);

        unlockedProperties.add("first");
    }

    protected void decode(IRequestContext context, UIComponent component,
            IComponentData componentData) {
        super.decode(context, component, componentData);

        // FacesContext facesContext = context.getFacesContext();

        ComponentsListComponent dg = (ComponentsListComponent) component;

        Number first = componentData.getNumberProperty("first");
        if (first != null) {
            int old = dg.getFirst();

            dg.setFirst(first.intValue());

            component.queueEvent(new PropertyChangeEvent(component,
                    Properties.FIRST, new Integer(old), first));
        }

        /*
         * String filterExpression =
         * componentData.getStringProperty("filterExpression"); if
         * (filterExpression != null) { if (filterExpression.length() < 1) {
         * filterExpression = null; }
         * 
         * dg.setFilterProperties(HtmlTools
         * .decodeFilterExpression(filterExpression)); }
         */

        /*
         * IComponentPreference preference = dg.getPreference(facesContext); if
         * (preference != null) { preference.savePreference(facesContext, dg); }
         */
    }

    public static class ListContext {

        private final FacesContext facesContext;

        private final ComponentsListComponent componentsListComponent;

        private final String rowIndexVar;

        private final String rowCountVar;

        // private DataModel dataModel;

        private UIColumn columns[];

        private int columnNumber;

        private int first;

        private int rowCount;

        private int rows = -2;

        // private IFilterProperties filtersMap;

        private boolean interactiveShow;

        ListContext(FacesContext facesContext,
                ComponentsListComponent componentsListComponent) {
            this(facesContext, componentsListComponent, false);

            first = componentsListComponent.getFirst();

            // filtersMap =
            // componentsListComponent.getFilterProperties(facesContext);
        }

        public String getRowIndexVar() {
            return rowIndexVar;
        }

        public String getRowCountVar() {
            return rowCountVar;
        }

        ListContext(FacesContext facesContext,
                ComponentsListComponent componentsListComponent, int rowIndex,
                String filterExpression) {
            this(facesContext, componentsListComponent, true);

            first = rowIndex;
            // this.filtersMap =
            // HtmlTools.decodeFilterExpression(filterExpression);
        }

        private ListContext(FacesContext facesContext,
                ComponentsListComponent componentsListComponent, boolean dummy) {
            this.facesContext = facesContext;
            this.componentsListComponent = componentsListComponent;

            columnNumber = componentsListComponent
                    .getColumnNumber(facesContext);

            rowIndexVar = componentsListComponent.getRowIndexVar(facesContext);

            rowCountVar = componentsListComponent.getRowCountVar(facesContext);

            List<UIComponent> children = componentsListComponent.getChildren();
            List<UIColumn> cols = null;
            for (Iterator it = children.iterator(); it.hasNext();) {
                UIComponent child = (UIComponent) it.next();

                if ((child instanceof UIColumn) == false) {
                    continue;
                }

                if (cols == null) {
                    cols = new ArrayList<UIColumn>(children.size());
                }

                cols.add((UIColumn) child);
            }

            if (cols != null) {
                columns = cols.toArray(new UIColumn[cols.size()]);

            } else {
                UIColumn column = new UIColumn();

                column.getChildren().addAll(children);

                children.clear();
                children.add(column);

                columns = new UIColumn[] { column };
            }

            if (columnNumber < 1) {
                columnNumber = columns.length;
            }

            rows = componentsListComponent.getRows();
            rowCount = componentsListComponent.getRowCount();

            /*
             * Object value = componentsListComponent.getCachedValue(); if
             * (value instanceof DataModel) { dataModel = (DataModel) value; }
             */
        }

        public UIColumn[] listColumns() {
            return columns;
        }

        public void updateRowCount() {
            rowCount = -2;
        }

        public int getRowCount() {
            if (rowCount == -2) {
                rowCount = componentsListComponent.getRowCount();
            }

            return rowCount;
        }

        public void setInteractiveShow(boolean interactiveShow) {
            this.interactiveShow = interactiveShow;
        }

        public boolean isInteractiveShow() {
            return interactiveShow;
        }

        /*
         * public DataModel getDataModel() { return dataModel; }
         */

        public int getColumnNumber() {
            return columnNumber;
        }

        public int getFirst() {
            return first;
        }

        public int getRows() {
            return rows;
        }

        /*
         * public IFilterProperties getFiltersMap() { return filtersMap; }
         */

    }

    public ListContext createListContext(FacesContext facesContext,
            ComponentsListComponent dgc, int rowIndex,
            ISortedComponent[] sortedComponents, String filterExpression) {
        return new ListContext(facesContext, dgc, rowIndex, filterExpression);
    }

    public void addRequiredJavaScriptClassNames(IHtmlWriter writer,
            IJavaScriptRenderContext javaScriptRenderContext) {
        super.addRequiredJavaScriptClassNames(writer, javaScriptRenderContext);

        ComponentsListComponent componentsListComponent = (ComponentsListComponent) writer
                .getComponentRenderContext().getComponent();

        IMenuIterator menuIterator = componentsListComponent.listMenus();
        if (menuIterator.hasNext()) {
            javaScriptRenderContext.appendRequiredClass(
                    JavaScriptClasses.COMPONENTS_LIST, "menu");
        }

        if (componentsListComponent.getRows() > 0) {
            javaScriptRenderContext.appendRequiredClass(
                    JavaScriptClasses.COMPONENTS_LIST, "ajax");
            
            ComponentsListUpdateBehaviorListener.addAjaxBehavior(componentsListComponent, writer.getComponentRenderContext().getFacesContext());
        }
    }

    public void declare(INamespaceConfiguration nameSpaceProperties) {
        super.declare(nameSpaceProperties);

        nameSpaceProperties.addAttributes(null, new String[] { "asyncRender",
                "interactiveShow", "filtred", "filterExpression", "rows",
                "rowCount", "first", "nc" });
    }

    /* Les renderers */

    protected abstract void encodeListBegin(IHtmlWriter htmlWriter,
            ComponentsListComponent componentsListComponent,
            ListContext listContext) throws WriterException;

    protected void encodeChildrenComponentBegin(IHtmlWriter htmlWriter,
            ComponentsListComponent componentsListComponent, int processed,
            int columnNumber, String[] rowClasses, String tdClass)
            throws WriterException {

    }

    protected abstract void encodeComponentBegin(IHtmlWriter htmlWriter,
            ComponentsListComponent componentsListComponent, int processed,
            int columnNumber, String[] rowClasses, String tdClass)
            throws WriterException;

    protected abstract void encodeComponentEnd(IHtmlWriter htmlWriter,
            ComponentsListComponent componentsListComponent, int processed,
            int columnNumber, String[] rowClasses, String tdClass)
            throws WriterException;

    protected void encodeChildrenComponentEnd(IHtmlWriter htmlWriter,
            ComponentsListComponent componentsListComponent, int processed,
            int columnNumber, String[] rowClasses, String tdClass)
            throws WriterException {

    }

    protected abstract void encodeListEnd(IHtmlWriter htmlWriter,
            ComponentsListComponent componentsListComponent)
            throws WriterException;

}
