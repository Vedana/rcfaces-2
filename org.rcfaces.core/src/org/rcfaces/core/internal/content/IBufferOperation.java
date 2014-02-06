/*
 * $Id: IBufferOperation.java,v 1.2 2013/01/11 15:47:02 jbmeslin Exp $
 */
package org.rcfaces.core.internal.content;

import java.util.Map;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/01/11 15:47:02 $
 */
public interface IBufferOperation {

    String getName();

    void setName(String name);

    void configure(Map<String, Object> configuration);

}
