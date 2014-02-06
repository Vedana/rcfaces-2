package org.rcfaces.renderkit.html.internal.decorator;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.rcfaces.core.component.MessageDialogComponent;
import org.rcfaces.core.internal.renderkit.IComponentRenderContext;
import org.rcfaces.core.internal.renderkit.WriterException;

public class MessageDialogDecorator extends AbstractSelectItemsDecorator {

    // private String newVar;
    private String defaultValue;

    public MessageDialogDecorator(UIComponent component) {
        super(component, null);
    }

    protected SelectItemsContext createHtmlContext() {
        return null;
    }

    protected void preEncodeContainer() throws WriterException {
        super.preEncodeContainer();
    }

    protected SelectItemsContext createJavaScriptContext()
            throws WriterException {

        IComponentRenderContext componentRenderContext = javaScriptWriter
                .getHtmlComponentRenderContext();

        return new SelectItemsJsContext(this, componentRenderContext,
                getComponent(), null);
    }

    protected void encodeComponentsBegin() throws WriterException {
        super.encodeComponentsBegin();

        MessageDialogComponent component = (MessageDialogComponent) getComponent();
        FacesContext facesContext = javaScriptWriter.getFacesContext();

        defaultValue = component.getDefaultValue(facesContext);

    }

    protected void encodeComponentsEnd() throws WriterException {

        MessageDialogComponent component = (MessageDialogComponent) getComponent();
        FacesContext facesContext = javaScriptWriter.getFacesContext();

        // call open only if the component is visible
        if (component.isVisible(facesContext)) {
            // titi.open()
            // le callback sera déclenché par le fireEvent (fa_event)
            javaScriptWriter.writeMethodCall("f_openMessage").writeln(");");
        }
        super.encodeComponentsEnd();
    }

    public int encodeNodeBegin(UIComponent component, SelectItem selectItem,
            boolean hasChildren, boolean isVisible) throws WriterException {
        IComponentRenderContext componentRenderContext = getComponentRenderContext();

        Object selectItemValue = selectItem.getValue();
        String value = convertItemValue(componentRenderContext, selectItemValue);
        if (value == null) {
            return EVAL_NODE;
        }

        if (hasChildren) {
            if (getContext().getDepth() > 1) {
                throw new WriterException(
                        "MessageDialog select items do not support more 1 level !",
                        null, component);
            }
        }

        String text = selectItem.getLabel();

        javaScriptWriter.writeMethodCall("f_addAction").writeString(value)
                .write(',').writeString(text).write(',').writeBoolean(
                        selectItem.isDisabled()).write(',').writeBoolean(
                        value == defaultValue).writeln(");");

        return EVAL_NODE;

    }

    public void encodeNodeEnd(UIComponent component, SelectItem selectItem,
            boolean hasChild, boolean isVisible) throws WriterException {

    }

}
