package org.rcfaces.core.event;

import javax.faces.component.UIComponent;
import javax.faces.event.ActionEvent;
import javax.faces.event.FacesListener;

/**
 * @author meslin.jb@vedana.com
 */
public class ExpandEvent extends ActionEvent {

    private static final long serialVersionUID = -8338479464413940009L;

    private final int detail;

    private String value;

    private Object item;

    public ExpandEvent(UIComponent component, String value, Object item,
            int detail) {
        super(component);

        this.detail = detail;
        this.value = value;
        this.item = item;
    }

    public int getDetail() {
        return detail;
    }

    public Object getItem() {
        return item;
    }

    public String getValue() {
        return value;
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
        return (listener instanceof IExpandListener);
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
        ((IExpandListener) listener).processExpand(this);
    }

}
