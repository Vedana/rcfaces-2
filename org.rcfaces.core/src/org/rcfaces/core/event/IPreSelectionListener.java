package org.rcfaces.core.event;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.FacesListener;

/**
 * @author meslin.jb@vedana.com
 */
public interface IPreSelectionListener extends FacesListener {

    void processPreSelection(PreSelectionEvent event) throws AbortProcessingException;
}
