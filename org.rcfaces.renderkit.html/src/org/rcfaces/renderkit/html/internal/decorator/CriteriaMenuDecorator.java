package org.rcfaces.renderkit.html.internal.decorator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.component.UISelectItems;

import org.rcfaces.core.component.MenuComponent;
import org.rcfaces.core.component.capability.IInputTypeCapability;
import org.rcfaces.core.component.capability.ISelectionCardinalityCapability;
import org.rcfaces.core.internal.capability.ICriteriaContainer;
import org.rcfaces.core.internal.renderkit.WriterException;
import org.rcfaces.core.internal.tools.CollectionTools;
import org.rcfaces.core.internal.tools.CriteriaTools;
import org.rcfaces.core.item.CriteriaItem;
import org.rcfaces.core.model.ISelectedCriteria;

public class CriteriaMenuDecorator extends SubMenuDecorator {

    public CriteriaMenuDecorator(UIComponent component, String menuId,
            String suffixMenuId, boolean removeAllWhenShown,
            int itemImageWidth, int itemImageHeight) {
        super(component, menuId, suffixMenuId, removeAllWhenShown,
                itemImageWidth, itemImageHeight);
    }

    protected SelectItemsContext createJavaScriptContext()
            throws WriterException {

        UIComponent component = getComponent();
        UIComponent parentComponent = component.getParent();

        if (parentComponent instanceof ICriteriaContainer) {

            ICriteriaContainer container = (ICriteriaContainer) parentComponent;
            Object selectedCriteriaValues = CollectionTools.valuesToSet(
                    container.getCriteriaConfiguration().getSelectedValues(),
                    true);
            if (selectedCriteriaValues != null) {
                ((MenuComponent) component)
                        .setCheckedValues(selectedCriteriaValues);
            }
        }

        return super.createJavaScriptContext();
    }

    protected Iterator<UIComponent> iterateNodes(UIComponent component)
            throws WriterException {
        UISelectItems selectItems = new UISelectItems();

        UIComponent parentComponent = component.getParent();

        if (parentComponent instanceof ICriteriaContainer) {
            ICriteriaContainer container = (ICriteriaContainer) parentComponent;
            ISelectedCriteria[] selectedCritera = CriteriaTools
                    .listSelectedCriteria(container.getCriteriaManager());
            CriteriaItem[] criteriaItem = CriteriaTools
                    .listAvailableCriteriaItems(
                            container.getCriteriaConfiguration(),
                            selectedCritera);

            int criteriaCardinality = container.getCriteriaConfiguration()
                    .getCriteriaCardinality();
            int inputType = IInputTypeCapability.AS_RADIO_BUTTON;

            switch (criteriaCardinality) {

            case ISelectionCardinalityCapability.ONEMANY_CARDINALITY:
            case ISelectionCardinalityCapability.ZEROMANY_CARDINALITY:
            case ISelectionCardinalityCapability.OPTIONAL_CARDINALITY:

                inputType = IInputTypeCapability.AS_CHECK_BUTTON;
                break;

            default:
                inputType = IInputTypeCapability.AS_RADIO_BUTTON;
                break;
            }

            for (int i = 0; i < criteriaItem.length; i++) {

                CriteriaItem criteriaItem2 = criteriaItem[i];
                criteriaItem2.setInputType(inputType);
                if (inputType == IInputTypeCapability.AS_RADIO_BUTTON) {
                    criteriaItem2.setGroupName("criteriaGroup");
                }
            }

            selectItems.setValue(criteriaItem);

        }

        List<UIComponent> l = new ArrayList<UIComponent>();
        l.add(selectItems);
        return l.iterator();
    }

}
