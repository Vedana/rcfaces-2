/*
 * $Id: IImageOperationContentModel.java,v 1.2 2013/01/11 15:47:01 jbmeslin Exp $
 */
package org.rcfaces.core.image;

import java.util.Map;

import org.rcfaces.core.model.IContentModel;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/01/11 15:47:01 $
 */
public interface IImageOperationContentModel extends IContentModel {

    Map<String, Object> getFilterParameters();
}
