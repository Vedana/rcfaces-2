package org.rcfaces.core.event;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.FacesListener;

/**
 * @author meslin.jb@vedana.com
 */
public interface IExpandListener extends FacesListener {

    void processExpand(ExpandEvent event) throws AbortProcessingException;
}
