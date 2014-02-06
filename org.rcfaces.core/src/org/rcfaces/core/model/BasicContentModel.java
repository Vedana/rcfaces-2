/*
 * $Id: BasicContentModel.java,v 1.4 2013/11/13 12:53:20 jbmeslin Exp $
 */
package org.rcfaces.core.model;

import org.rcfaces.core.internal.contentAccessor.IGeneratedResourceInformation;
import org.rcfaces.core.internal.contentAccessor.IGenerationResourceInformation;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.4 $ $Date: 2013/11/13 12:53:20 $
 */
public class BasicContentModel extends AbstractContentModel {
    protected transient IGeneratedResourceInformation generatedInformation;

    protected transient IGenerationResourceInformation generationInformation;

    public BasicContentModel() {
    }

    public BasicContentModel(Object value) {
        super(value);
    }

    @Override
    public void setInformations(
            IGenerationResourceInformation generationInformation,
            IGeneratedResourceInformation generatedInformation) {
        super.setInformations(generationInformation, generatedInformation);

        this.generationInformation = generationInformation;
        this.generatedInformation = generatedInformation;
    }
}