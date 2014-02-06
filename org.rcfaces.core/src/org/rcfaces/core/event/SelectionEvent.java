/*
 * $Id: SelectionEvent.java,v 1.3 2013/11/13 12:53:20 jbmeslin Exp $
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
public class SelectionEvent extends ActionEvent implements ITypedEvent {

    private static final long serialVersionUID = 3488075281502266801L;

    public static final int SELECTED_EVENT_TYPE = 0x100;

    public static final int DEFAULT_SELECTED_EVENT_TYPE = 0x102;

    public static final int UNKNOWN_POSITION = -1;

    public static final int UNKNOWN_KEY = -1;

    public static final int UNKNOWN_BUTTONS = 0x8000;

    public static final int UNKNOWN_MODIFIERS = 0x8000;

    public static final int BUTTON_1 = 0x01;

    public static final int BUTTON_2 = 0x02;

    public static final int BUTTON_3 = 0x04;

    public static final int MODIFIER_ALT = 0x01;

    public static final int MODIFIER_CTRL = 0x02;

    public static final int MODIFIER_SHIFT = 0x04;

    private final int type;

    private final int keyCode;

    private final int mouseX;

    private final int mouseY;

    private final int buttonsMask;

    private final int modifiersMask;

    private String value;

    private Object valueObject;

    private Object item;

    public SelectionEvent(UIComponent component, String value,
            Object valueObject, Object item, int detail) {
        this(component, computeTypeFromDetail(detail), UNKNOWN_POSITION,
                UNKNOWN_POSITION, UNKNOWN_BUTTONS, UNKNOWN_MODIFIERS,
                UNKNOWN_KEY);

        this.value = value;
        this.valueObject = valueObject;
        this.item = item;
    }

    private static int computeTypeFromDetail(int detail) {
        // XXX A terminer !
        return 0;
    }

    public SelectionEvent(UIComponent component, int type, int mouseX,
            int mouseY, int buttonsMask, int modifiersMask) {
        this(component, type, mouseX, mouseY, buttonsMask, modifiersMask,
                UNKNOWN_KEY);
    }

    public SelectionEvent(UIComponent component, int type, int mouseX,
            int mouseY, int buttonsMask, int modifiersMask, int keyCode) {
        super(component);

        this.type = type;

        this.mouseX = mouseX;
        this.mouseY = mouseY;

        this.buttonsMask = buttonsMask;
        this.modifiersMask = modifiersMask;
        this.keyCode = keyCode;
    }

    public int getType() {
        return type;
    }

    public Object getItem() {
        return item;
    }

    public String getValue() {
        return value;
    }

    public Object getValueObject() {
        return valueObject;
    }

    public int getButtonsMask() {
        return buttonsMask;
    }

    public int getKeyCode() {
        return keyCode;
    }

    public int getModifiersMask() {
        return modifiersMask;
    }

    public int getMouseX() {
        return mouseX;
    }

    public int getMouseY() {
        return mouseY;
    }

    @Override
    public boolean isAppropriateListener(FacesListener listener) {

        if (listener instanceof ISelectionListener) {
            return true;
        }

        return super.isAppropriateListener(listener);
    }

    @Override
    public void processListener(FacesListener listener) {
        if (listener instanceof ISelectionListener) {
            ((ISelectionListener) listener).componentSelected(this);
            return;
        }

        super.processListener(listener);
    }

}
