/*
 * $Id: ComboExDecorator.java,v 1.2 2013/07/03 12:25:09 jbmeslin Exp $
 * 
 */
package org.rcfaces.renderkit.html.internal.decorator;

import javax.faces.component.UIComponent;
import javax.faces.model.SelectItem;

import org.rcfaces.core.internal.renderkit.WriterException;
import org.rcfaces.core.model.IFilterProperties;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:09 $
 */
public class ComboExDecorator extends AbstractSelectItemsDecorator {

    

    protected ComboExDecorator(UIComponent component,
            IFilterProperties filterProperties) {
        super(component, filterProperties);
    }

    protected SelectItemsContext createHtmlContext() {
        return null;
    }

    protected SelectItemsContext createJavaScriptContext()
            throws WriterException {
        return null;
    }

    public int encodeNodeBegin(UIComponent component, SelectItem selectItem,
            boolean hasChild, boolean isVisible) throws WriterException {
        return 0;
    }

    public void encodeNodeEnd(UIComponent component, SelectItem selectItem,
            boolean hasChild, boolean isVisible) throws WriterException {

    }

    /*
     * private static final String ARROW_BLANK_URI = "comboEx/blank.gif";
     * 
     * private static final String INPUT = "_input";
     * 
     * public ComboExDecorator(UIComponent component, IFilterProperties
     * filterProperties) { super(component, filterProperties); }
     * 
     * public void preEncodeContainer() throws WriterException {
     * IComponentRenderContext componentRenderContext = writer
     * .getComponentRenderContext(); ComboExComponent component =
     * (ComboExComponent) componentRenderContext .getComponent();
     * IHtmlRenderContext htmlRenderContext = (IHtmlRenderContext)
     * componentRenderContext .getRenderContext(); FacesContext facesContext =
     * componentRenderContext.getFacesContext();
     * 
     * writer.enableJavaScript();
     * 
     * writer.startElement(IHtmlWriter.TR");
     * 
     * String blankImageURL = htmlRenderContext.getHtmlExternalContext()
     * .getStyleSheetURI(ARROW_BLANK_URI);
     * 
     * if (mapSelectItems(componentRenderContext,
     * SelectItemMappers.SEARCH_IMAGE_MAPPER) == false) { // Y a une image ...
     * 
     * String className = getClassName();
     * 
     * writer.startElement(IHtmlWriter.TD");
     * writer.startElement(IHtmlWriter.IMG"); writer.writeAttribute("class",
     * className + "_itemImage"); writer.writeAttribute("src", blankImageURL);
     * writer.endElement(IHtmlWriter.TD"); }
     * 
     * writer.startElement(IHtmlWriter.TD"); if (component.getWidth() != null) {
     * writer.writeAttribute("width", "100%"); }
     * 
     * writer.startElement(IHtmlWriter.INPUT"); writer.writeAttribute("type",
     * "text"); writer.writeAttribute("name",
     * componentRenderContext.getComponentId());
     * 
     * int maxTextLength = component.getMaxTextLength(facesContext); if
     * (maxTextLength > 0) { writer.writeAttribute("maxLength", maxTextLength); }
     * 
     * int iCol = component.getColumnNumber(facesContext); if (iCol > 0) {
     * writer.writeAttribute("size", iCol); }
     * 
     * if (component.getWidth(facesContext) != null) {
     * writer.writeAttribute("style", "width:100%"); }
     * 
     * if (component.isEditable(facesContext) == false ||
     * component.isReadOnly(facesContext)) { writer.writeAttribute("READONLY"); }
     * 
     * if (component.isDisabled(facesContext)) {
     * writer.writeAttribute("DISABLED"); }
     * 
     * String txt = component.getText(facesContext); if (txt == null) { txt =
     * ""; }
     * 
     * String className = getClassName();
     * 
     * writer.writeAttribute("value", txt); writer.endElement(IHtmlWriter.TD");
     * writer.startElement(IHtmlWriter.TD"); writer.writeAttribute("class",
     * className + "_cimage");
     * 
     * writer.startElement(IHtmlWriter.IMG"); if
     * (component.isDisabled(facesContext)) { writer.writeAttribute("class",
     * className + "_image_disabled"); } else { writer.writeAttribute("class",
     * className + "_image"); } writer.writeAttribute("src", blankImageURL);
     * 
     * writer.endElement(IHtmlWriter.TD"); writer.endElement(IHtmlWriter.TR");
     * 
     * super.preEncodeContainer(); }
     * 
     * protected SelectItemsContext createHtmlContext() { return null; }
     * 
     * protected SelectItemsContext createJavaScriptContext() {
     * IComponentRenderContext componentRenderContext = javaScriptWriter
     * .getComponentRenderContext();
     * 
     * UIInput input = (UIInput) getComponent();
     * 
     * return new SelectItemsJsContext(this, componentRenderContext, input,
     * input.getValue()); }
     * 
     * public int encodeNodeBegin(UIComponent component, SelectItem selectItem,
     * boolean hasChildren, boolean isVisible) throws WriterException {
     * 
     * SelectItemsJsContext ctx = (SelectItemsJsContext) getContext();
     * 
     * String varId = javaScriptWriter.getJavaScriptRenderContext()
     * .allocateVarName();
     * 
     * String parentVarId = null; if (ctx.getDepth() > 1) { parentVarId =
     * ctx.peekVarId(); }
     * 
     * ctx.pushVarId(varId);
     * 
     * String imageURL = null; if (selectItem instanceof IImagesSelectItem) {
     * imageURL = ((BasicImagesSelectItem) selectItem).getImageURL(); imageURL =
     * javaScriptWriter.allocateString(imageURL); }
     * 
     * javaScriptWriter.write("var ").write(varId); if (parentVarId != null) {
     * javaScriptWriter.write('=').writeMethodCall("_addSubItem").write(
     * parentVarId).write(','); } else {
     * javaScriptWriter.write('=').writeMethodCall("f_addItem"); }
     * 
     * String label = selectItem.getLabel(); if (label == null) { label = ""; }
     * javaScriptWriter.writeString(label);
     * 
     * javaScriptWriter.write(',');
     * 
     * String value = convertItemValue(javaScriptWriter
     * .getComponentRenderContext(), selectItem.getValue()); if (value != null) {
     * javaScriptWriter.writeString(value); } else {
     * javaScriptWriter.writeNull(); }
     * 
     * boolean disabled = selectItem.isDisabled();
     * 
     * if (imageURL != null) { javaScriptWriter.write(',').write(imageURL); }
     * else if (disabled) { javaScriptWriter.write(',').writeNull(); }
     * 
     * if (disabled) { javaScriptWriter.write(',').writeBoolean(true); }
     * 
     * javaScriptWriter.writeln(");");
     * 
     * return EVAL_NODE; }
     * 
     * public void encodeNodeEnd(UIComponent component, SelectItem selectItem,
     * boolean hasChildren, boolean isVisible) {
     * 
     * ((SelectItemsJsContext) getContext()).popVarId(); }
     * 
     */
}
