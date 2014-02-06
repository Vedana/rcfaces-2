/*
 * $Id: KeyPressEvent.java,v 1.3 2013/11/13 12:53:20 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.event;

import javax.faces.component.UIComponent;
import javax.faces.event.ActionEvent;
import javax.faces.event.FacesListener;

/**
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:20 $
 */
public class KeyPressEvent extends ActionEvent {

    private static final long serialVersionUID = 2531722435621838354L;

    private int keyCode;

    private int modifiers;

    public KeyPressEvent(UIComponent component, int keyCode, int modifiers) {
        super(component);

        this.keyCode = keyCode;
        this.modifiers = modifiers;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * javax.faces.event.FacesEvent#isAppropriateListener(javax.faces.event.
     * FacesListener)
     */
    @Override
    public boolean isAppropriateListener(FacesListener listener) {
        return (listener instanceof IKeyPressListener);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * javax.faces.event.FacesEvent#processListener(javax.faces.event.FacesListener
     * )
     */
    @Override
    public void processListener(FacesListener listener) {
        ((IKeyPressListener) listener).processKeyPress(this);
    }

    public final int getKeyCode() {
        return keyCode;
    }

    public final int getModifiers() {
        return modifiers;
    }

}
