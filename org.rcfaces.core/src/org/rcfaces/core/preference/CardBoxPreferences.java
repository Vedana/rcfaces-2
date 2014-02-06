/*
 * $Id: CardBoxPreferences.java,v 1.3 2013/11/13 12:53:25 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.preference;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.rcfaces.core.component.CardBoxComponent;
import org.rcfaces.core.component.CardComponent;
import org.rcfaces.core.component.iterator.ICardIterator;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:25 $
 */
public class CardBoxPreferences extends AbstractComponentPreferences {

    private static final long serialVersionUID = -8616195336828413449L;

    private String selectedCardId;

    public void loadPreferences(FacesContext facesContext, UIComponent component) {
        if ((component instanceof CardBoxComponent) == false) {
            throw new FacesException("Can not load cardBox preferences !");
        }

        CardBoxComponent cardBoxComponent = (CardBoxComponent) component;

        if (selectedCardId != null) {
            ICardIterator cardIterator = cardBoxComponent.listCards();
            for (; cardIterator.hasNext();) {
                CardComponent cardComponent = cardIterator.next();

                if (selectedCardId.equals(cardComponent.getId()) == false) {
                    continue;
                }

                cardBoxComponent.select(cardComponent);
                break;
            }
        }
    }

    public void savePreferences(FacesContext facesContext, UIComponent component) {
        if ((component instanceof CardBoxComponent) == false) {
            throw new FacesException("Can not save cardBox preferences !");
        }

        CardBoxComponent cardBoxComponent = (CardBoxComponent) component;

        selectedCardId = null;
        CardComponent cardComponent = cardBoxComponent
                .getSelectedCard(facesContext);
        if (cardComponent != null) {
            selectedCardId = cardComponent.getId();
        }
    }

    public Object saveState(FacesContext context) {
        return new Object[] { selectedCardId };
    }

    public void restoreState(FacesContext context, Object state) {
        Object p[] = (Object[]) state;

        this.selectedCardId = (String) p[0];
    }

}
