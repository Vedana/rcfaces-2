/*
 * $Id: SuggestTextEntryDecorator.java,v 1.2 2013/07/03 12:25:09 jbmeslin Exp $
 * 
 */
package org.rcfaces.renderkit.html.internal.decorator;

import java.util.Iterator;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;

import org.rcfaces.core.component.SuggestTextEntryComponent;
import org.rcfaces.core.component.capability.IFilterCapability;
import org.rcfaces.core.internal.renderkit.IComponentData;
import org.rcfaces.core.internal.renderkit.IComponentRenderContext;
import org.rcfaces.core.internal.renderkit.IRequestContext;
import org.rcfaces.core.internal.renderkit.WriterException;
import org.rcfaces.core.item.IClientDataItem;
import org.rcfaces.core.item.IImagesItem;
import org.rcfaces.core.model.IFilterProperties;
import org.rcfaces.renderkit.html.internal.HtmlTools;
import org.rcfaces.renderkit.html.internal.IObjectLiteralWriter;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:09 $
 */
public class SuggestTextEntryDecorator extends AbstractSelectItemsDecorator {
    

    private final int maxResultNumber;

    private final boolean service;

    private final Converter converter;

    private int count = 0;

    public SuggestTextEntryDecorator(UIComponent component,
            Converter converter, IFilterProperties filterProperties,
            int maxResultNumber, boolean service) {
        super(component, filterProperties);

        this.converter = converter;

        this.maxResultNumber = maxResultNumber;
        this.service = service;
    }

    protected SelectItemsContext createHtmlContext() {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.rcfaces.core.internal.renderkit.html.SelectItemsRenderer#createContext(org.rcfaces.core.internal.renderkit.html.IJavaScriptWriter)
     */
    protected SelectItemsContext createJavaScriptContext() {
        IComponentRenderContext componentRenderContext = javaScriptWriter
                .getHtmlComponentRenderContext();

        SuggestTextEntryComponent input = (SuggestTextEntryComponent) getComponent();

        return new SelectItemsJsContext(this, componentRenderContext, input,
                null);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.rcfaces.core.internal.renderkit.html.SelectItemsRenderer#encodeNodeBegin(org.rcfaces.core.internal.renderkit.html.SelectItemsRenderer.Context,
     *      javax.faces.component.UIComponent, javax.faces.model.SelectItem,
     *      boolean)
     */
    public int encodeNodeBegin(UIComponent component, SelectItem selectItem,
            boolean hasChildren, boolean isVisible) throws WriterException {

        count++;

        if (maxResultNumber > 0 && count > maxResultNumber) {
            return SKIP_NODE;
        }

        IComponentRenderContext componentRenderContext = getComponentRenderContext();

        Object selectItemValue = selectItem.getValue();
        String value = convertItemValue(componentRenderContext, selectItemValue);
        if (value == null) {
            return EVAL_NODE;
        }

        javaScriptWriter.writeMethodCall("f_appendItem2");

        IObjectLiteralWriter objectLiteralWriter = javaScriptWriter
                .writeObjectLiteral(false);

        String text = selectItem.getLabel();
        if (text != null) {
            objectLiteralWriter.writeSymbol("_label").writeString(text);
        }

        if (value != null) {
            objectLiteralWriter.writeSymbol("_value").writeString(value);
        }

        String description = selectItem.getDescription();
        if (description != null) {
            objectLiteralWriter.writeSymbol("_description").writeString(
                    description);
        }

        if (selectItem instanceof IImagesItem) {
            IImagesItem imagesSelectItem = (IImagesItem) selectItem;

            String imageURL = imagesSelectItem.getImageURL();

            if (imageURL != null) {
                objectLiteralWriter.writeSymbol("_imageURL").writeString(
                        imageURL);
            }
        }

        if (selectItem instanceof IClientDataItem) {
            IClientDataItem clientDataSelectItem = (IClientDataItem) selectItem;

            if (clientDataSelectItem.isClientDataEmpty() == false) {
                Map map = clientDataSelectItem.getClientDataMap();

                IObjectLiteralWriter clientDatas = objectLiteralWriter
                        .writeSymbol("_clientDatas").writeObjectLiteral(true);

                for (Iterator it = map.entrySet().iterator(); it.hasNext();) {
                    Map.Entry entry = (Map.Entry) it.next();

                    String dataKey = (String) entry.getKey();
                    if (dataKey == null || dataKey.length() < 1) {
                        continue;
                    }

                    Object dataValue = entry.getValue();
                    if (dataValue != null) {
                        dataValue = String.valueOf(dataValue);
                    }

                    clientDatas.writeProperty(dataKey).writeString(
                            (String) dataValue);
                }

                clientDatas.end();
            }
        }

        objectLiteralWriter.end().writeln(");");

        return EVAL_NODE;
    }

    protected void encodeComponentsEnd() throws WriterException {
        if (javaScriptWriter == null) {
            super.encodeComponentsEnd();
            return;
        }

        int rowCount = getSelectItemCount();
        if (rowCount < 1 && service == false) {
            // Nous sommes dans un rendu HTML !
            super.encodeComponentsEnd();
            return;
        }

        javaScriptWriter.writeMethodCall("f_setRowCount").writeInt(rowCount)
                .writeln(");");

        super.encodeComponentsEnd();
    }

    public void encodeNodeEnd(UIComponent component, SelectItem selectItem,
            boolean hasChildren, boolean isVisible) throws WriterException {
    }

    public void decode(IRequestContext context, UIComponent component,
            IComponentData componentData) {
        super.decode(context, component, componentData);

        if (component instanceof IFilterCapability) {
            IFilterCapability filterCapability = (IFilterCapability) component;

            String filterExpression = componentData
                    .getStringProperty("filterExpression");
            if (filterExpression != null) {
                if (filterExpression.length() < 1) {
                    filterExpression = null;
                }

                filterCapability.setFilterProperties(HtmlTools
                        .decodeFilterExpression(context.getProcessContext(),
                                component, filterExpression));
            }
        }
    }

    protected int getMaxResultNumber() {
        return maxResultNumber;
    }

    protected Converter getConverter() {
        return converter;
    }

}
