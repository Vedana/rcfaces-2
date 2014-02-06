/*
 * $Id: IAlignmentCapability.java,v 1.2 2013/01/11 15:46:57 jbmeslin Exp $
 */
package org.rcfaces.core.component.capability;

/**
 * A string that represents the alignement of the data in the component.
 * <ul>
 * <li> left </li>
 * <li> right </li>
 * <li> center </li>
 * </ul>
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/01/11 15:46:57 $
 */
public interface IAlignmentCapability {

    /**
	 *  valid values for Alignment
	 *  @see AlignmentNormalizer
	 */
	String LEFT = "left";
	String RIGHT = "right";
	String CENTER = "center";

    /**
     * Returns a string that represents the alignement of the data in the
     * component.
     * 
     * @return right|left|center
     */
    String getAlignment();

    /**
     * Sets a string that represents the alignement of the data in the
     * component.
     * 
     * @param textAlignment
     *            right|left|center
     */
    void setAlignment(String textAlignment);
}