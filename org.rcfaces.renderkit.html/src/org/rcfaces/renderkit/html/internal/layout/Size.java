package org.rcfaces.renderkit.html.internal.layout;

import java.io.Serializable;

import javax.faces.FacesException;
import javax.faces.component.StateHolder;
import javax.faces.context.FacesContext;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.1 $ $Date: 2013/01/11 15:45:05 $
 */
public class Size implements Serializable, StateHolder {

    private static final long serialVersionUID = -2531527106142521042L;

    private boolean transientState;

    private double width;

    private double height;

    public Size() {

    }

    public Size(double width, double height) {
        this.width = width;
        this.height = height;
    }

    public Size(Length w, Length h) {
        if (w == null || w.getUnit() == Length.PERCENT_UNIT) {
            throw new FacesException("Invalid width length '" + w + "'");
        }

        if (h == null || h.getUnit() == Length.PERCENT_UNIT) {
            throw new FacesException("Invalid height length '" + h + "'");
        }

        this.width = w.getNumber();
        this.height = h.getNumber();
    }

    public Object saveState(FacesContext context) {
        return new Object[] { new Double(width), new Double(height) };
    }

    public void restoreState(FacesContext context, Object state) {
        Object[] ps = (Object[]) state;

        width = ((Double) ps[0]).doubleValue();
        height = ((Double) ps[1]).doubleValue();
    }

    public boolean isTransient() {
        return transientState;
    }

    public void setTransient(boolean newTransientValue) {
        this.transientState = newTransientValue;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

}