/*
 * $Id: ComboRenderer.java,v 1.3 2013/12/27 11:16:21 jbmeslin Exp $
 */
package org.rcfaces.renderkit.html.internal.renderer;

import java.util.Iterator;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.component.UISelectItems;
import javax.faces.context.FacesContext;

import org.rcfaces.core.component.capability.IDisabledCapability;
import org.rcfaces.core.component.capability.IFilterCapability;
import org.rcfaces.core.component.capability.ITextDirectionCapability;
import org.rcfaces.core.internal.renderkit.WriterException;
import org.rcfaces.core.model.IFilterProperties;
import org.rcfaces.core.model.IFiltredCollection;
import org.rcfaces.renderkit.html.internal.AbstractSelectItemsRenderer;
import org.rcfaces.renderkit.html.internal.IFilteredItemsRenderer;
import org.rcfaces.renderkit.html.internal.IHtmlWriter;
import org.rcfaces.renderkit.html.internal.IJavaScriptRenderContext;
import org.rcfaces.renderkit.html.internal.IJavaScriptWriter;
import org.rcfaces.renderkit.html.internal.JavaScriptClasses;
import org.rcfaces.renderkit.html.internal.decorator.ComboDecorator;
import org.rcfaces.renderkit.html.internal.decorator.IComponentDecorator;
import org.rcfaces.renderkit.html.internal.ns.XhtmlNSAttributes;
import org.rcfaces.renderkit.html.internal.service.ItemsBehaviorListener;
import org.rcfaces.renderkit.html.internal.util.ListenerTools.INameSpace;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/12/27 11:16:21 $
 */
@XhtmlNSAttributes({ "filtred" })
public class ComboRenderer extends AbstractSelectItemsRenderer implements
        IFilteredItemsRenderer {

    private static final String FILTRED_COLLECTION_PROPERTY = "camelia.combo.filtredCollection";

    protected String getActionEventName(INameSpace nameSpace) {
        return nameSpace.getSelectionEventName();
    }

    protected void encodeBeforeDecorator(IHtmlWriter htmlWriter,
            IComponentDecorator componentDecorator) throws WriterException {


        htmlWriter.startElement(IHtmlWriter.SELECT);
        writeHtmlAttributes(htmlWriter);
        writeJavaScriptAttributes(htmlWriter);
        writeCssAttributes(htmlWriter);

        writeComboAttributes(htmlWriter);

        htmlWriter.addSubFocusableComponent(htmlWriter
                .getComponentRenderContext().getComponentClientId());
    }

    protected void writeComboAttributes(IHtmlWriter htmlWriter)
            throws WriterException {

        UIComponent combo = htmlWriter.getComponentRenderContext()
                .getComponent();

        if (combo instanceof ITextDirectionCapability) {
            writeTextDirection(htmlWriter, (ITextDirectionCapability) combo);
        }

        htmlWriter.writeName(htmlWriter.getComponentRenderContext()
                .getComponentClientId());

        if (isMultipleSelect(combo)) {
            htmlWriter.writeMultiple();
        }

        if (combo instanceof IDisabledCapability) {
            IDisabledCapability enabledCapability = (IDisabledCapability) combo;

            if (enabledCapability.isDisabled()) {
                htmlWriter.writeDisabled();
            }
        }

        int size = getRowNumber(combo);
        if (size > 0) {
            htmlWriter.writeSize(size);
        }

        if (combo instanceof IFilterCapability) {
            if (hasFilteredCollections(combo)) {
                htmlWriter.getComponentRenderContext().setAttribute(
                        FILTRED_COLLECTION_PROPERTY, Boolean.TRUE);

                htmlWriter.writeAttributeNS("filtred", true);
            }
        }
    }

    protected void encodeAfterDecorator(IHtmlWriter htmlWriter,
            IComponentDecorator componentDecorator) throws WriterException {

        htmlWriter.endElement(IHtmlWriter.SELECT);

        super.encodeAfterDecorator(htmlWriter, componentDecorator);
    }

    private boolean hasFilteredCollections(UIComponent combo) {
        List children = combo.getChildren();
        for (Iterator it = children.iterator(); it.hasNext();) {
            UIComponent child = (UIComponent) it.next();

            if ((child instanceof UISelectItems) == false) {
                continue;
            }

            UISelectItems selectItems = (UISelectItems) child;

            Object value = selectItems.getValue();
            if ((value instanceof IFiltredCollection) == false) {
                continue;
            }

            return true;
        }

        return false;
    }

    protected boolean isMultipleSelect(UIComponent component) {
        return false;
    }

    protected int getRowNumber(UIComponent component) {
        return 1;
    }

    protected String getJavaScriptClassName() {
        return JavaScriptClasses.COMBO;
    }

    public void addRequiredJavaScriptClassNames(IHtmlWriter htmlWriter,
            IJavaScriptRenderContext javaScriptRenderContext) {
        super.addRequiredJavaScriptClassNames(htmlWriter,
                javaScriptRenderContext);

        if (htmlWriter.getComponentRenderContext().containsAttribute(
                FILTRED_COLLECTION_PROPERTY)) {

            // On prend .COMBO en dure, car le filter n'est pas defini pour les
            // classes qui en héritent !
            javaScriptRenderContext.appendRequiredClass(
                    JavaScriptClasses.COMBO, "filter");
        }
    }

    protected IComponentDecorator createComponentDecorator(
            FacesContext facesContext, UIComponent component) {

        IFilterProperties filterProperties = null;

        if (component instanceof IFilterCapability) {
            filterProperties = ((IFilterCapability) component)
                    .getFilterProperties();
        }

        return createComboDecorator(facesContext, component, filterProperties,
                false);
    }

    protected IComponentDecorator createComboDecorator(
            FacesContext facesContext, UIComponent component,
            IFilterProperties filterProperties, boolean jsVersion) {

        return new ComboDecorator(component, filterProperties, jsVersion);
    }

    public void encodeFilteredItems(IJavaScriptWriter jsWriter,
            IFilterCapability comboComponent,
            IFilterProperties filterProperties, int maxResultNumber)
            throws WriterException {

        IComponentDecorator componentDecorator = createComboDecorator(
                jsWriter.getFacesContext(), (UIComponent) comboComponent,
                filterProperties, true);
        if (componentDecorator == null) {
            return;
        }
        
        ItemsBehaviorListener.addAjaxBehavior(comboComponent, jsWriter.getFacesContext());

        componentDecorator.encodeJavaScript(jsWriter);
    }
}