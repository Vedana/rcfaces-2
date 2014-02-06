/*
 * $Id: CardBoxTools.java,v 1.4 2013/11/13 12:53:22 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.internal.tools;

import java.util.Collections;
import java.util.List;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.model.SelectItem;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.component.CardBoxComponent;
import org.rcfaces.core.component.CardComponent;
import org.rcfaces.core.component.iterator.ICardIterator;
import org.rcfaces.core.internal.listener.CameliaPhaseListener;
import org.rcfaces.core.internal.util.ComponentIterators;

/**
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.4 $ $Date: 2013/11/13 12:53:22 $
 */
public class CardBoxTools {

    private static final Log LOG = LogFactory.getLog(CardBoxTools.class);

    private static final ICardIterator EMPTY_COMPONENT_ITERATOR = new CardListIterator(
            Collections.<CardComponent> emptyList());

    public static ICardIterator listCards(CardBoxComponent component) {
        List<CardComponent> list = ComponentIterators.list(component,
                CardComponent.class);
        if (list.isEmpty()) {
            return EMPTY_COMPONENT_ITERATOR;
        }

        return new CardListIterator(list);
    }

    public static CardComponent getSelectedCard(CardBoxComponent component) {
        Object value = component.getSubmittedExternalValue();

        if (value == null) {
            value = component.getValue();
        }

        if (value instanceof SelectItem) {
            value = ((SelectItem) value).getValue();
        }

        if (value != null) {
            CardComponent byId = null;

            ICardIterator iterator = listCards(component);
            for (; iterator.hasNext();) {
                CardComponent card = iterator.next();

                if (value.equals(card.getValue())) {
                    return card;
                }

                if (value.equals(card.getId())) {
                    byId = card;
                }
            }

            return byId; // On retourne NULL si on trouve pas l'onglet
            /*
             * if (byId != null) { return byId; }
             */

        }

        // On prend le premier tab dans ce cas !

        ICardIterator iterator = listCards(component);
        if (iterator.hasNext() == false) {
            return null;
        }

        return iterator.next();
    }

    private static final class CardListIterator extends
            ComponentIterators.ComponentListIterator<CardComponent> implements
            ICardIterator {

        public CardListIterator(List<CardComponent> list) {
            super(list);
        }

        public final CardComponent next() {
            return nextComponent();
        }

        public CardComponent[] toArray() {
            return (CardComponent[]) toArray(new CardComponent[count()]);
        }
    }

    public static void selectCard(CardBoxComponent component, CardComponent card) {
        boolean applyingRequestValues = CameliaPhaseListener
                .isApplyingRequestValues();

        if (card == null) {
            if (applyingRequestValues) {
                component.setSubmittedExternalValue(null);
                return;
            }
            component.setValue(null);
            return;
        }

        Object value = card.getValue();
        if (value == null) {
            value = card.getId();
        }

        if (applyingRequestValues) {
            component.setSubmittedExternalValue(value);
            return;
        }
        component.setValue(value);
    }

    public static CardBoxComponent getCardBox(CardComponent component) {
        UIComponent parent = component.getParent();

        if (parent == null || (parent instanceof CardBoxComponent) == false) {
            throw new FacesException(
                    "Invalid parent of Tab component. (Must be a CardBox component) parent='"
                            + parent + "'");
        }

        return (CardBoxComponent) parent;
    }
}
