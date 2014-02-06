/*
 * $Id: CameliaTagExtraInfo.java,v 1.2 2013/07/03 12:25:06 jbmeslin Exp $
 */
package org.rcfaces.core.internal.taglib;

import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.TagExtraInfo;
import javax.servlet.jsp.tagext.VariableInfo;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:06 $
 */
public class CameliaTagExtraInfo extends TagExtraInfo {

    

    private static final VariableInfo[] VARIABLE_INFO_EMPTY_ARRAY = new VariableInfo[0];

    public VariableInfo[] getVariableInfo(TagData tagData) {
        return VARIABLE_INFO_EMPTY_ARRAY;
    }

}
