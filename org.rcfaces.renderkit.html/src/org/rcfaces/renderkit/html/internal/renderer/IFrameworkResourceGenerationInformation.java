/*
 * $Id: IFrameworkResourceGenerationInformation.java,v 1.2 2013/01/11 15:45:01 jbmeslin Exp $
 */
package org.rcfaces.renderkit.html.internal.renderer;

import org.rcfaces.core.internal.contentAccessor.IGenerationResourceInformation;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/01/11 15:45:01 $
 */
public interface IFrameworkResourceGenerationInformation extends
        IGenerationResourceInformation {

    String FRAMEWORK_ATTRIBUTE = "org.rcfaces.FrameworkResource";

    boolean isFrameworkResource();

}
