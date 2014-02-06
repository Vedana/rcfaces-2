/*
 * $Id: BasicGeneratedResourceInformation.java,v 1.4 2013/11/13 12:53:23 jbmeslin Exp $
 */
package org.rcfaces.core.internal.contentAccessor;

import java.util.Map;

import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.lang.IContentFamily;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.4 $ $Date: 2013/11/13 12:53:23 $
 */
public class BasicGeneratedResourceInformation extends AbstractInformation
        implements IGeneratedResourceInformation {
    
    private static final Log LOG = LogFactory
            .getLog(BasicGeneratedResourceInformation.class);

    private static final String FILTRED_MODEL_PROPERTY = "org.rcfaces.response.FILTRED_MODEL";

    private static final String PROCESSED_AT_REQUEST_PROPERTY = "org.rcfaces.response.PROCESSED_AT_REQUEST";

    private IContentFamily contentFamily;

    public BasicGeneratedResourceInformation() {
    }

    public final String getResponseMimeType() {
        return (String) getAttribute(RESPONSE_MIME_TYPE_PROPERTY);
    }

    public final void setResponseMimeType(String contentType) {
        setAttribute(RESPONSE_MIME_TYPE_PROPERTY, contentType);
    }

    public final String getSourceMimeType() {
        return (String) getAttribute(SOURCE_MIME_TYPE_PROPERTY);
    }

    public final void setSourceMimeType(String contentType) {
        setAttribute(SOURCE_MIME_TYPE_PROPERTY, contentType);
    }

    public boolean isFiltredModel() {
        Boolean val = (Boolean) getAttribute(FILTRED_MODEL_PROPERTY);
        if (val == null) {
            return false;
        }

        return val.booleanValue();
    }

    public void setProcessingAtRequest(boolean processedAtRequest) {
        setAttribute(PROCESSED_AT_REQUEST_PROPERTY,
                Boolean.valueOf(processedAtRequest));
    }

    public boolean isProcessingAtRequestSetted() {
        return getAttribute(PROCESSED_AT_REQUEST_PROPERTY) != null;
    }

    public boolean isProcessingAtRequest() {
        Boolean val = (Boolean) getAttribute(PROCESSED_AT_REQUEST_PROPERTY);
        if (val == null) {
            return false;
        }

        return val.booleanValue();
    }

    public void setFiltredModel(boolean filtredModel) {
        setAttribute(FILTRED_MODEL_PROPERTY, Boolean.valueOf(filtredModel));
    }

    public IContentFamily getContentFamily() {
        return contentFamily;
    }

    public void setContentFamily(IContentFamily contentFamilly) {
        this.contentFamily = contentFamilly;
    }

    public String getResponseSuffix() {
        return (String) getAttribute(RESPONSE_URL_SUFFIX_PROPERTY);
    }

    public void setResponseSuffix(String suffix) {
        setAttribute(RESPONSE_URL_SUFFIX_PROPERTY, suffix);
    }

    @Override
    public void restoreState(FacesContext context, Object state) {
        Object states[] = (Object[]) state;

        super.restoreState(context, states[0]);

        if (states[1] != null) {
            contentFamily = ContentFamilies
                    .getContentFamillyByOrdinal(((Integer) states[1])
                            .intValue());
        }
    }

    @Override
    public Object saveState(FacesContext context) {
        Object states[] = new Object[2];

        states[0] = super.saveState(context);
        if (contentFamily != null) {
            states[1] = new Integer(contentFamily.getOrdinal());
        }

        return states;
    }

    public void copyTo(IGeneratedResourceInformation generatedInformation) {
        Map<String, Object> attributes = getAttributes();

        for (Map.Entry<String, Object> entry : attributes.entrySet()) {

            generatedInformation.setAttribute(entry.getKey(), entry.getValue());
        }
    }

}
