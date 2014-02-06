/*
 * $Id: ISelected3StatesCapability.java,v 1.1 2011/04/12 09:25:26 oeuillot Exp $
 * 
 */
package org.rcfaces.core.component.capability;

/**
 * 
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:26 $
 */
public interface ISelected3StatesCapability {

    String NOT_SELECTED_STATE = "notSelected";

    String SELECTED_STATE = "selected";

    String UNDETERMINATED_STATE = "undeterminated";

    /**
     * Returns a string value indicating wether the component is selected.
     * 
     * @return NOT_SELECTED_STATE:notSelected|SELECTED_STATE:selected|UNDETERMINATED_STATE:undeterminated
     */
    String getSelectedState();

    /**
     * Sets a string value indicating wether the component is selected.
     * 
     * @param selectedState
     *            NOT_SELECTED_STATE:notSelected|SELECTED_STATE:selected|UNDETERMINATED_STATE:undeterminated
     */
    void setSelectedState(String selectedState);

    /**
     * Returns a boolean value indicating wether the component is selected.
     * 
     * @return true if selected, false otherwise
     */
    boolean isSelected();

    /**
     * Sets a boolean value indicating wether the component is in a
     * undeterminated select state.
     * 
     * @return true if undeterminate select state, false otherwise
     */
    boolean isUndeterminated();
}
