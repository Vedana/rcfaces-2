/*
 * $Id: ComboExRenderer.java,v 1.2 2013/01/11 15:45:02 jbmeslin Exp $
 */
package org.rcfaces.renderkit.html.internal.renderer;

import org.rcfaces.renderkit.html.internal.AbstractSelectItemsRenderer;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/01/11 15:45:02 $
 */
public class ComboExRenderer extends AbstractSelectItemsRenderer {
   

    /*
     * protected void encodeBeforeDecorator(IHtmlWriter writer,
     * IComponentDecorator componentDecorator) throws WriterException {
     * super.encodeBeforeDecorator(writer, componentDecorator);
     * 
     * IComponentRenderContext componentRenderContext = writer
     * .getComponentRenderContext(); ComboExComponent component =
     * (ComboExComponent) componentRenderContext .getComponent(); FacesContext
     * facesContext = componentRenderContext.getFacesContext();
     * 
     * writer.enableJavaScript();
     * 
     * writer.startElement(IHtmlWriter.TABLE"); writeHtmlAttributes(writer);
     * writeJavaScriptAttributes(writer); writeCssAttributes(writer);
     * 
     * writer.writeAttribute("cellspacing", "0");
     * writer.writeAttribute("cellpadding", "0");
     * 
     * if (component.isDisabled(facesContext)) {
     * writer.writeAttributeNS("disabled", "true"); } if
     * (component.isEditable(facesContext) == false) {
     * writer.writeAttributeNS("editable", "false"); } if
     * (component.isReadOnly(facesContext)) {
     * writer.writeAttributeNS("readOnly", "true"); } if
     * (component.isAutoCompletion(facesContext)) {
     * writer.writeAttributeNS("autoCompletion", "true"); } int rowNumber =
     * component.getPopupRowNumber(); if (rowNumber > 0) {
     * writer.writeAttributeNS("popupRowNumber", rowNumber); } }
     * 
     * protected void encodeAfterDecorator(IHtmlWriter writer,
     * IComponentDecorator componentDecorator) throws WriterException {
     * writer.endElement(IHtmlWriter.TABLE");
     * 
     * super.encodeAfterDecorator(writer, componentDecorator); }
     * 
     * protected String getJavaScriptClassName() { return
     * JavaScriptClasses.COMBO_EX; }
     * 
     * protected IComponentDecorator createComponentDecorator( FacesContext
     * facesContext, UIComponent component) { return new
     * ComboExDecorator(component, null); }
     * 
     * protected void decode(IRequestContext context, UIComponent component,
     * IComponentData componentData) {
     * 
     * ComboExComponent comboExComponent = (ComboExComponent) component;
     * 
     * String parameter = componentData.getComponentParameter(); if (parameter !=
     * null) { comboExComponent.setText(parameter); } else { // Le composant
     * etait peut �tre disabled !
     * 
     * String text = componentData.getStringProperty("text"); if (text != null) {
     * comboExComponent.setText(text); } }
     * 
     * super.decode(context, component, componentData); }
     */
}