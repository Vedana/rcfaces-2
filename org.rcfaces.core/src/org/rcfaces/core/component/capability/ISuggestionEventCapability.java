/*
 * $Id: ISuggestionEventCapability.java,v 1.1 2011/04/12 09:25:27 oeuillot Exp $
 * 
 */

package org.rcfaces.core.component.capability;

import javax.faces.event.FacesListener;

import org.rcfaces.core.event.ISuggestionListener;

/**
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:27 $
 */
public interface ISuggestionEventCapability {

    /**
     * Adds a listener to the component for the suggestion event
     * 
     * @param suggestionListener
     *            the suggestion listener to add
     */
    void addSuggestionListener(ISuggestionListener suggestionListener);

    /**
     * Removes a listener from the component for the suggestion event
     * 
     * @param suggestionListener
     *            the suggestion listener to remove
     */
    void removeSuggestionListener(ISuggestionListener suggestionListener);

    /**
     * Returns a list of suggestion listener for the component
     * 
     * @return suggestion listeners' list
     */
    FacesListener[] listSuggestionListeners();
}
