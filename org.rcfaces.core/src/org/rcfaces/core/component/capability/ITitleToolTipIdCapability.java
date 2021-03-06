/*
 * $Id: ITitleToolTipIdCapability.java,v 1.1 2013/01/11 15:46:57 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.component.capability;

/**
 * ToolTip capability.
 * 
 * @version $Revision: 1.1 $ $Date: 2013/01/11 15:46:57 $
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 */
public interface ITitleToolTipIdCapability {

	/**
	 * Returns the receiver's tool tip text, or <code>null</code> if it has not
	 * been set.
	 * 
	 * @return the receiver's tool tip client Id
	 */
	String getTitleToolTipId();

	/**
	 * Sets the receiver's tool tip text to the argument, which may be
	 * <code>null</code> indicating that no tool tip text should be shown.
	 * 
	 * @param text
	 *            the new tool tip client Id (or <code>null</code>)
	 */
	void setTitleToolTipId(String clientId);
}
