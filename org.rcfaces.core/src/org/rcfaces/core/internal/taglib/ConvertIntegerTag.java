/*
 * $Id: ConvertIntegerTag.java,v 1.2 2013/07/03 12:25:06 jbmeslin Exp $
 */
package org.rcfaces.core.internal.taglib;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:06 $
 */
public class ConvertIntegerTag extends ConvertNumberTag {
    

    private static final long serialVersionUID = 2036254497469923597L;

    protected String getDefaultConverterId() {
        return "org.rcfaces.Integer";
    }

}
