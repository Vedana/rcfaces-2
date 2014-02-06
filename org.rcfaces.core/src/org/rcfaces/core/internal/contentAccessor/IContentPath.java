/*
 * $Id: IContentPath.java,v 1.1 2011/04/12 09:25:29 oeuillot Exp $
 */
package org.rcfaces.core.internal.contentAccessor;

import javax.faces.context.FacesContext;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:29 $
 */
public interface IContentPath {

    int UNDEFINED_PATH_TYPE = 0x00;

    int RELATIVE_PATH_TYPE = 0x01;

    int CONTEXT_PATH_TYPE = 0x02;

    int EXTERNAL_PATH_TYPE = 0x08;

    int ABSOLUTE_PATH_TYPE = 0x04;

    int FILTER_PATH_TYPE = 0x10;

    String CONTEXT_KEYWORD = "$context";

    void setPathType(int pathType);

    int getPathType();

    String getPath();

    IContentPath getParentContentPath();

    String convertToPathType(FacesContext facesContext, int targetPathType);
}
