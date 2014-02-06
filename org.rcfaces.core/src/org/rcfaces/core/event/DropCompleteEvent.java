/*
 * $Id: DropCompleteEvent.java,v 1.3 2013/11/13 12:53:20 jbmeslin Exp $
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
public class DropCompleteEvent extends ActionEvent {
    private static final long serialVersionUID = 975274504874873948L;

    private final String[] types;

    private final int effect;

    private final Object sourceItemValue;

    private final Object targetItemValue;

    private final UIComponent sourceComponent;

    public DropCompleteEvent(UIComponent targetComponent, Object targetItem,
            UIComponent sourceComponent, Object sourceItem, int effect,
            String types[]) {

        super(targetComponent);

        this.targetItemValue = targetItem;
        this.sourceComponent = sourceComponent;
        this.sourceItemValue = sourceItem;
        this.effect = effect;
        this.types = types;
    }

    public String[] getTypes() {
        return types;
    }

    public int getEffect() {
        return effect;
    }

    public Object getSourceItemValue() {
        return sourceItemValue;
    }

    public Object getTargetItemValue() {
        return targetItemValue;
    }

    public UIComponent getSourceComponent() {
        return sourceComponent;
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
        return (listener instanceof IDropCompleteListener);
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
        ((IDropCompleteListener) listener).componentCompleteDropped(this);
    }

}
