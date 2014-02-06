/*
 * $Id: IToolTipComponent.java,v 1.1 2013/01/11 15:47:01 jbmeslin Exp $
 */
package org.rcfaces.core.internal.capability;

import org.rcfaces.core.component.iterator.IToolTipIterator;

/**
 * 
 * @author jbmeslin@vedana.com (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.1 $ $Date: 2013/01/11 15:47:01 $
 */
public interface IToolTipComponent {

    IToolTipIterator listToolTips();
}
