/*
 * $Id: SuggestTextEntryRenderer.java,v 1.4 2013/12/27 11:16:21 jbmeslin Exp $
 */
package org.rcfaces.renderkit.html.internal.renderer;

import java.util.Iterator;

import javax.faces.component.UIComponent;
import javax.faces.component.UISelectItems;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.component.SuggestTextEntryComponent;
import org.rcfaces.core.component.capability.IFilterCapability;
import org.rcfaces.core.component.iterator.IOrderedIterator;
import org.rcfaces.core.event.PropertyChangeEvent;
import org.rcfaces.core.internal.component.Properties;
import org.rcfaces.core.internal.renderkit.IComponentData;
import org.rcfaces.core.internal.renderkit.IRequestContext;
import org.rcfaces.core.internal.renderkit.WriterException;
import org.rcfaces.core.internal.tools.ValuesTools;
import org.rcfaces.core.model.IClientFilterCollection;
import org.rcfaces.core.model.IFilterProperties;
import org.rcfaces.renderkit.html.internal.IAccessibilityRoles;
import org.rcfaces.renderkit.html.internal.IFilteredItemsRenderer;
import org.rcfaces.renderkit.html.internal.IHtmlWriter;
import org.rcfaces.renderkit.html.internal.IJavaScriptWriter;
import org.rcfaces.renderkit.html.internal.JavaScriptClasses;
import org.rcfaces.renderkit.html.internal.decorator.IComponentDecorator;
import org.rcfaces.renderkit.html.internal.decorator.SuggestTextEntryDecorator;
import org.rcfaces.renderkit.html.internal.service.ItemsBehaviorListener;
import org.rcfaces.renderkit.html.internal.service.ItemsService;
import org.rcfaces.renderkit.html.internal.util.ClientDataModelTools;
import org.rcfaces.renderkit.html.internal.ns.XhtmlNSAttributes;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.4 $ $Date: 2013/12/27 11:16:21 $
 */
@XhtmlNSAttributes({ "maxResultNumber", "suggestionDelayMs",
        "suggestionMinChars", "caseSensitive", "forceProposal",
        "suggestionValue", "moreResultsMessage", "orderedResult",
        "showPopupForOneResult" })
public class SuggestTextEntryRenderer extends TextEntryRenderer implements
        IFilteredItemsRenderer {

    private static final Log LOG = LogFactory
            .getLog(SuggestTextEntryRenderer.class);

    private static final String CLIENT_DB_ENABLED_PROPERTY = "org.rcfaces.html.CLIENT_DB_ENABLED";

    private static final String DEFAULT_CONTENT_PRIMARY_KEY = "value";

    protected void encodeComponent(IHtmlWriter htmlWriter)
            throws WriterException {

        super.encodeComponent(htmlWriter);

        /*
         * htmlWriter.startElement(IHtmlElements.LABEL);
         * htmlWriter.writeId(htmlWriter.getComponentRenderContext()
         * .getComponentClientId() + "::ariaLive");
         * htmlWriter.writeClass("f_suggestTextEntry_live");
         * htmlWriter.writeAriaLabelledBy(htmlWriter.getComponentRenderContext()
         * .getComponentClientId()); htmlWriter.writeAttribute("aria-relevant",
         * "text"); htmlWriter.writeAttribute("aria-live", "polite");
         * htmlWriter.endElement(IHtmlElements.LABEL);
         */

        htmlWriter.getJavaScriptEnableMode().enableOnFocus();
    }

    protected IHtmlWriter writeInputAttributes(IHtmlWriter htmlWriter)
            throws WriterException {
        htmlWriter = super.writeInputAttributes(htmlWriter);

        htmlWriter.writeAutoComplete(IHtmlWriter.AUTOCOMPLETE_OFF);
        htmlWriter.writeAttribute("aria-autocomplete", "list");

        FacesContext facesContext = htmlWriter.getComponentRenderContext()
                .getFacesContext();

        SuggestTextEntryComponent suggestTextEntryComponent = (SuggestTextEntryComponent) htmlWriter
                .getComponentRenderContext().getComponent();

        int maxResultNumber = suggestTextEntryComponent
                .getMaxResultNumber(facesContext);
        if (maxResultNumber > 0) {
            htmlWriter.writeAttributeNS("maxResultNumber", maxResultNumber);
        }

        int suggestionDelayMs = suggestTextEntryComponent
                .getSuggestionDelayMs(facesContext);
        if (suggestionDelayMs > 0) {
            htmlWriter.writeAttributeNS("suggestionDelayMs", suggestionDelayMs);
        }

        int suggestionMinChars = suggestTextEntryComponent
                .getSuggestionMinChars(facesContext);
        if (suggestionMinChars > 0) {
            htmlWriter.writeAttributeNS("suggestionMinChars",
                    suggestionMinChars);
        }

        boolean caseSensitive = suggestTextEntryComponent
                .isCaseSensitive(facesContext);
        if (caseSensitive) {
            htmlWriter.writeAttributeNS("caseSensitive", true);
        }

        boolean forceProposal = suggestTextEntryComponent
                .isForceProposal(facesContext);
        if (forceProposal) {
            htmlWriter.writeAttributeNS("forceProposal", true);
        }

        boolean disableProposals = suggestTextEntryComponent
                .isDisableProposals(facesContext);
        if (disableProposals) {
            htmlWriter.writeAttributeNS("disableProposals", true);
        }

        boolean showPopupForOneResult = suggestTextEntryComponent
                .isShowPopupForOneResult(facesContext);
        if (showPopupForOneResult) {
            htmlWriter.writeAttributeNS("showPopupForOneResult", true);
        }

        Object suggestionValue = suggestTextEntryComponent
                .getSuggestionValue(facesContext);
        if (suggestionValue != null) {
            String value = ValuesTools.convertValueToString(suggestionValue,
                    suggestTextEntryComponent, facesContext);
            htmlWriter.writeAttributeNS("suggestionValue", value);
        }

        String moreResultsMessage = suggestTextEntryComponent
                .getMoreResultsMessage(facesContext);
        if (moreResultsMessage != null) {
            htmlWriter.writeAttributeNS("moreResultsMessage",
                    moreResultsMessage);
        }

        String inputFormat = suggestTextEntryComponent
                .getInputFormat(facesContext);
        if (inputFormat != null) {
            htmlWriter.writeAttributeNS("inputFormat", inputFormat);
        }
        String labelFormat = suggestTextEntryComponent
                .getLabelFormat(facesContext);
        if (labelFormat != null) {
            htmlWriter.writeAttributeNS("labelFormat", labelFormat);
        }
        String descriptionFormat = suggestTextEntryComponent
                .getDescriptionFormat(facesContext);
        if (descriptionFormat != null) {
            htmlWriter.writeAttributeNS("descriptionFormat", descriptionFormat);
        }

        int popupWidth = suggestTextEntryComponent.getPopupWidth(facesContext);
        if (popupWidth > 0) {
            htmlWriter.writeAttributeNS("popupWidth", popupWidth);
        }

        int popupHeight = suggestTextEntryComponent
                .getPopupHeight(facesContext);
        if (popupHeight > 0) {
            htmlWriter.writeAttributeNS("popupHeight", popupHeight);
        }

        boolean orderedResult = suggestTextEntryComponent
                .isOrderedItems(facesContext);

        IClientFilterCollection clientFilterIterator = null;

        Iterator it = suggestTextEntryComponent.getChildren().iterator();
        for (; it.hasNext();) {
            UIComponent component = (UIComponent) it.next();
            if (component instanceof UISelectItems) {
                UISelectItems uiSelectItems = (UISelectItems) component;

                Object itemsValue = uiSelectItems.getValue();
                if (itemsValue != null) {
                    if (orderedResult == false) {
                    IOrderedIterator orderedIterator = getAdapter(
                            IOrderedIterator.class, itemsValue);

                    if (orderedIterator != null) {
                        orderedResult = orderedIterator.isOrdered();
                    }
                  //  break;
                }

                    if (clientFilterIterator == null) {
                        IClientFilterCollection cf = getAdapter(
                                IClientFilterCollection.class, itemsValue);

                        if (cf != null) {
                            clientFilterIterator = cf;
                        }
                    }
                }
            }
        }

        if (orderedResult == false) {
            htmlWriter.writeAttributeNS("orderedResult", false);
        }

        if (clientFilterIterator != null) {
            writeClientFiltredIterator(htmlWriter, clientFilterIterator);
        }

        return htmlWriter;
    }

    protected void writeClientFiltredIterator(IHtmlWriter htmlWriter,
            IClientFilterCollection clientDataModel) throws WriterException {
        if (clientDataModel == null) {
            return;
        }

        String contentName = clientDataModel.getContentName();
        if (contentName == null) {
            LOG.debug("Content name returns NULL, disabled client data model !");
            return;
        }

        String contentKey = clientDataModel.getContentKey();
        int contentRowCount = clientDataModel.getContentRowCount();
        String contentPK = clientDataModel.getContentPrimaryKey();
        if (contentPK == null) {
            contentPK = DEFAULT_CONTENT_PRIMARY_KEY;
        }

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

        String contentIndex = ClientDataModelTools.format(clientDataModel);

        // htmlWriter.writeAttributeNS("indexedDb", true);
        htmlWriter.writeAttributeNS("idbName", contentName);
        htmlWriter.writeAttributeNS("idbKey", contentKey);
        htmlWriter.writeAttributeNS("idbPK", contentPK);
        if (contentRowCount >= 0) {
            htmlWriter.writeAttributeNS("idbCount", contentRowCount);
        }
        if (contentIndex != null) {
            htmlWriter.writeAttributeNS("idbIndex", contentIndex);
        }

        htmlWriter.getComponentRenderContext().setAttribute(
                CLIENT_DB_ENABLED_PROPERTY, Boolean.TRUE);
    }

    protected void encodeJavaScript(IJavaScriptWriter writer)
            throws WriterException {
        super.encodeJavaScript(writer);

        FacesContext facesContext = writer.getFacesContext();

        SuggestTextEntryComponent suggestTextEntryComponent = (SuggestTextEntryComponent) writer
                .getHtmlComponentRenderContext().getComponent();

        IFilterProperties filterProperties = suggestTextEntryComponent
                .getFilterProperties(facesContext);

        int maxResultNumber = suggestTextEntryComponent
                .getMaxResultNumber(facesContext);

        encodeFilteredItems(writer, suggestTextEntryComponent,
                filterProperties, maxResultNumber, false);
        
        ItemsBehaviorListener.addAjaxBehavior(suggestTextEntryComponent, facesContext);

        if (writer.getComponentRenderContext().containsAttribute(
                CLIENT_DB_ENABLED_PROPERTY)) {
            writer.getJavaScriptRenderContext().appendRequiredClass(
                    "f_suggestTextEntry", "indexDb");
        }
    }

    protected String getJavaScriptClassName() {
        return JavaScriptClasses.SUGGEST_TEXT_ENTRY;
    }

    @Override
    protected String getWAIRole() {
        return IAccessibilityRoles.COMBOBOX;
    }

    protected IComponentDecorator createSuggestionDecorator(
            FacesContext facesContext, UIComponent component,
            Converter converter, IFilterProperties filterProperties,
            int maxResultNumber, boolean service) {

        return new SuggestTextEntryDecorator(component, converter,
                filterProperties, maxResultNumber, service);
    }

    public void encodeFilteredItems(IJavaScriptWriter writer,
            IFilterCapability component, IFilterProperties filterProperties,
            int maxResultNumber) throws WriterException {

        encodeFilteredItems(writer, component, filterProperties,
                maxResultNumber, true);
    }

    protected void encodeFilteredItems(IJavaScriptWriter writer,
            IFilterCapability component, IFilterProperties filterProperties,
            int maxResultNumber, boolean service) throws WriterException {

        FacesContext facesContext = writer.getFacesContext();

        Converter converter = null;
        if (component instanceof SuggestTextEntryComponent) {
            converter = ((SuggestTextEntryComponent) component)
                    .getSuggestionConverter(facesContext);
        }

        IComponentDecorator componentDecorator = createSuggestionDecorator(
                facesContext, (UIComponent) component, converter,
                filterProperties, maxResultNumber, service);
        if (componentDecorator == null) {
            return;
        }

        componentDecorator.encodeJavaScript(writer);
    }

    protected void decode(IRequestContext context, UIComponent element,
            IComponentData componentData) {
        super.decode(context, element, componentData);

        FacesContext facesContext = context.getFacesContext();

        SuggestTextEntryComponent suggestTextEntryComponent = (SuggestTextEntryComponent) element;

        Object oldValue = suggestTextEntryComponent
                .getSuggestionValue(facesContext);
        Object newValue = null;

        String suggestionValue = componentData
                .getStringProperty("suggestionValue");
        if (suggestionValue != null) {
            Converter converter = suggestTextEntryComponent
                    .getSuggestionConverter(facesContext);

            newValue = ValuesTools.convertStringToValue(facesContext,
                    suggestTextEntryComponent, converter, suggestionValue,
                    "suggestionValue", false);
        }

        if (newValue != oldValue) {
            if (newValue == null || newValue.equals(oldValue) == false) {
                suggestTextEntryComponent.setSuggestionValue(newValue);

                suggestTextEntryComponent.queueEvent(new PropertyChangeEvent(
                        suggestTextEntryComponent, Properties.SUGGESTION_VALUE,
                        oldValue, newValue));
            }
        }
    }

}
