package org.rcfaces.renderkit.svg.internal.renderer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.component.UIColumn;
import javax.faces.context.FacesContext;
import javax.faces.model.DataModel;

import org.rcfaces.core.internal.renderkit.IComponentWriter;
import org.rcfaces.core.internal.renderkit.WriterException;
import org.rcfaces.core.internal.tools.GridTools;
import org.rcfaces.core.internal.tools.ValuesTools;
import org.rcfaces.core.internal.util.ComponentIterators;
import org.rcfaces.renderkit.html.internal.IHtmlWriter;
import org.rcfaces.renderkit.html.internal.IJavaScriptWriter;
import org.rcfaces.renderkit.html.internal.IObjectLiteralWriter;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/11/14 10:21:53 $
 */
public class DataSVGRenderer extends SVGRenderer {

    @Override
    protected void encodeEnd(IComponentWriter writer) throws WriterException {
        super.encodeEnd(writer);

        ((IHtmlWriter) writer).enableJavaScript();
    }

    @Override
    protected void encodeJavaScript(IJavaScriptWriter jsWriter)
            throws WriterException {
        super.encodeJavaScript(jsWriter);

//        DataSVGComponent component = (DataSVGComponent) jsWriter
//                .getComponentRenderContext().getComponent();
//        FacesContext facesContext = jsWriter.getFacesContext();
//
//        String var = component.getVar(facesContext);
//
//        Map<String, Object> requestMap = facesContext.getExternalContext()
//                .getRequestMap();
//
//        DataModel dataModel = GridTools.getDataModel(
//                component.getDataModel(facesContext), component, facesContext);
//
//        List<UIColumn> columns = ComponentIterators.list(component,
//                UIColumn.class);
////        Map<String, SVGDataColumnComponent> columnById = new HashMap<String, SVGDataColumnComponent>(
////                columns.size());
////        for (UIColumn column : columns) {
////            if (column instanceof SVGDataColumnComponent) {
////                columnById.put(column.getId(), (SVGDataColumnComponent) column);
////            }
////        }
////
////        SVGDataColumnComponent idColumn = columnById.get("id");
////        SVGDataColumnComponent visibilityColumn = columnById.get("visibility");
////        SVGDataColumnComponent colorColumn = columnById.get("color");
////        SVGDataColumnComponent textColumn = columnById.get("text");
////        SVGDataColumnComponent styleClassColumn = columnById.get("styleClass");
////        SVGDataColumnComponent fillColumn = columnById.get("fill");
////        SVGDataColumnComponent tooltipTextColumn = columnById
////                .get("tooltipText");
////        SVGDataColumnComponent valueColumn = columnById.get("value");
////        SVGDataColumnComponent selectableColumn = columnById.get("selectable");
////        SVGDataColumnComponent audioDescriptionColumn = columnById
////                .get("audioDescription");
//
//        Object oldValue = requestMap.get(var);
//        try {
//            for (int index = 0;; index++) {
//                dataModel.setRowIndex(index);
//
//                if (dataModel.isRowAvailable() == false) {
//                    break;
//                }
//
//                Object periodData = dataModel.getRowData();
//
//                requestMap.put(var, periodData);
//
//                String id = String.valueOf(idColumn.getValue());
//                if (id == null || id.length() == 0) {
//                    continue;
//                }
//
//                Boolean visibility = null;
//                String color = null;
//                String fill = null;
//                String styleClass = null;
//                String text = null;
//                String tooltipText = null;
//                String value = null;
//                Boolean selectable = null;
//                String audioDescription=null;
//
//                if (visibilityColumn != null) {
//                    visibility = ValuesTools.valueToBoolean(visibilityColumn);
//                }
//
//                if (colorColumn != null) {
//                    color = ValuesTools
//                            .valueToString(colorColumn, facesContext);
//                }
//
//                if (styleClassColumn != null) {
//                    styleClass = ValuesTools.valueToString(styleClassColumn,
//                            facesContext);
//                }
//
//                if (fillColumn != null) {
//                    fill = ValuesTools.valueToString(fillColumn, facesContext);
//                }
//
//                if (textColumn != null) {
//                    text = ValuesTools.valueToString(textColumn, facesContext);
//                }
//
//                if (tooltipTextColumn != null) {
//                    tooltipText = ValuesTools.valueToString(tooltipTextColumn,
//                            facesContext);
//                }
//
//                if (valueColumn != null) {
//                    value = ValuesTools
//                            .valueToString(valueColumn, facesContext);
//                }
//
//                if (selectableColumn != null) {
//                    selectable = ValuesTools.valueToBoolean(selectableColumn);
//                }
//
//                if (audioDescriptionColumn != null) {
//                    audioDescription = ValuesTools
//                            .valueToString(audioDescriptionColumn, facesContext);
//                }
//
//                if (visibility == null && color == null && styleClass == null
//                        && fill == null && text == null && tooltipText == null
//                        && value == null && selectable == null) {
//                    continue;
//                }
//
//                jsWriter.writeMethodCall("_update").writeString(id).write(',');
//
//                IObjectLiteralWriter objWriter = jsWriter
//                        .writeObjectLiteral(false);
//
//                if (visibility != null) {
//                    objWriter.writeProperty("_visibility").writeBoolean(
//                            visibility.booleanValue());
//                }
//                if (color != null) {
//                    objWriter.writeProperty("_color").writeString(color);
//                }
//                if (styleClass != null) {
//                    objWriter.writeProperty("_styleClass").writeString(
//                            styleClass);
//                }
//                if (fill != null) {
//                    objWriter.writeProperty("_fill").writeString(fill);
//                }
//                if (text != null) {
//                    objWriter.writeProperty("_text").writeString(text);
//                }
//                if (tooltipText != null) {
//                    objWriter.writeProperty("_tooltipText").writeString(
//                            tooltipText);
//                }
//                if (value != null) {
//                    objWriter.writeProperty("_value").writeString(value);
//                }
//                if (selectable != null) {
//                    objWriter.writeProperty("_selectable").writeBoolean(
//                            selectable.booleanValue());
//                }
//                if (audioDescription != null) {
//                    objWriter.writeProperty("_audioDescription").writeString(
//                            audioDescription);
//                }
//
//                objWriter.end().writeln(");");
//
//            }
//        } finally {
//            requestMap.put(var, oldValue);
//        }

    }
}
