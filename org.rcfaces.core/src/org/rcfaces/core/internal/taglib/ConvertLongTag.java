/*
 * $Id: ConvertLongTag.java,v 1.2 2013/07/03 12:25:06 jbmeslin Exp $
 */
package org.rcfaces.core.internal.taglib;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:06 $
 */
public class ConvertLongTag extends ConvertNumberTag {
    

    private static final long serialVersionUID = -7314605990150080216L;

    protected String getDefaultConverterId() {
        return "org.rcfaces.Long";
    }

}
