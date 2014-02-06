/*
 * $Id: IResourceKeyParticipant.java,v 1.1 2011/04/12 09:25:28 oeuillot Exp $
 */
package org.rcfaces.core.internal.contentAccessor;

import org.rcfaces.core.internal.lang.StringAppender;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:28 $
 */
public interface IResourceKeyParticipant {
    String RESOURCE_KEY_SEPARATOR = "\u0001";

    void participeKey(StringAppender sa);
}
