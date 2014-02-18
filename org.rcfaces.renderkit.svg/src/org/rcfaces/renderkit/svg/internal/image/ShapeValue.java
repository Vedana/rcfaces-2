/*
 * $Id: ShapeValue.java,v 1.2 2013/11/13 15:52:39 jbmeslin Exp $
 */
package org.rcfaces.renderkit.svg.internal.image;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.faces.component.StateHolder;
import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.internal.lang.StringAppender;
import org.rcfaces.renderkit.svg.item.INodeItem;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/11/13 15:52:39 $
 */
public class ShapeValue implements StateHolder {

    private static final Log LOG = LogFactory.getLog(ShapeValue.class);

    private Shape shape;

    private Object value;

    private String label;

    private String description;

    private boolean disabled;

    private String alternateText;

    private boolean transientState;

    private Map<String, String> clientDatas;

    public ShapeValue(Shape shape, INodeItem nodeItem) {
        this.shape = shape;
        this.value = nodeItem.getValue();

        this.label = nodeItem.getLabel();
        this.description = nodeItem.getDescription();
        this.disabled = nodeItem.isDisabled();

        this.alternateText = nodeItem.getAlternateText();

        if (nodeItem.isClientDataEmpty() == false) {
            this.clientDatas = nodeItem.getClientDataMap();
        }
    }

    public final Shape getShape() {
        return shape;
    }

    public final Object getValue() {
        return value;
    }

    public final String getLabel() {
        return label;
    }

    public final String getDescription() {
        return description;
    }

    public final boolean isDisabled() {
        return disabled;
    }

    public final String getAlternateText() {
        return alternateText;
    }

    public final Map<String, String> getClientDatas() {
        if (clientDatas == null) {
            return Collections.emptyMap();
        }
        return clientDatas;
    }

    public String[] computeOutline(AffineTransform transform, double flatness) {
        PathIterator pathIterator = shape.getPathIterator(transform, flatness);

        StringAppender sa = new StringAppender(256);

        List<String> l = null;

        float fs[] = new float[6];
        for (; pathIterator.isDone() == false; pathIterator.next()) {
            int type = pathIterator.currentSegment(fs);

            if (type == PathIterator.SEG_CLOSE) {
                // Pas de point !
                continue;
            }

            if (type == PathIterator.SEG_MOVETO) {
                // Le premier !

                if (sa.length() > 0) {
                    l = new ArrayList<String>();
                    l.add(sa.toString());

                    sa.setLength(0);
                }

                sa.append(String.valueOf((int) fs[0])).append(',')
                        .append(String.valueOf((int) fs[1]));

                continue;
            }

            if (type == PathIterator.SEG_LINETO) {

                sa.append(',').append(String.valueOf((int) fs[0])).append(',')
                        .append(String.valueOf((int) fs[1]));
                continue;
            }

            LOG.error("Invalid path iterator ... ? " + type);
        }

        if (l == null) {
            if (sa.length() == 0) {
                return null;
            }

            return new String[] { sa.toString() };
        }

        if (sa.length() > 0) {
            l.add(sa.toString());
        }

        return l.toArray(new String[l.size()]);
    }

    public boolean isTransient() {
        return transientState;
    }

    public void setTransient(boolean transientState) {
        this.transientState = transientState;
    }

    @SuppressWarnings("unchecked")
    public void restoreState(FacesContext facesContext, Object state) {
        Object states[] = (Object[]) state;

        shape = (Shape) UIComponentBase.restoreAttachedState(facesContext,
                states[0]);

        value = UIComponentBase.restoreAttachedState(facesContext, states[1]);

        label = (String) states[2];

        description = (String) states[3];

        disabled = (states[4] != null);

        alternateText = (String) states[5];

        if (states[6] != null) {
            clientDatas = (Map<String, String>) UIComponentBase
                    .restoreAttachedState(facesContext, states[6]);
        }

    }

    public Object saveState(FacesContext facesContext) {
        Object states[] = new Object[7];

        states[0] = UIComponentBase.saveAttachedState(facesContext, shape);
        states[1] = UIComponentBase.saveAttachedState(facesContext, value);
        states[2] = label;
        states[3] = description;
        states[4] = disabled ? Boolean.TRUE : null;
        states[5] = alternateText;

        if (clientDatas != null) {
            states[6] = UIComponentBase.saveAttachedState(facesContext,
                    clientDatas);
        }

        return states;
    }

}
