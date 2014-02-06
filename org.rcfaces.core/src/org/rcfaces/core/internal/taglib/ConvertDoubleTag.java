/*
 * $Id: ConvertDoubleTag.java,v 1.2 2013/07/03 12:25:06 jbmeslin Exp $
 */
package org.rcfaces.core.internal.taglib;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:06 $
 */
public class ConvertDoubleTag extends ConvertNumberTag {

    

    private static final long serialVersionUID = 6372503657660605243L;

    protected String getDefaultConverterId() {
        return "org.rcfaces.Double";
    }
}
