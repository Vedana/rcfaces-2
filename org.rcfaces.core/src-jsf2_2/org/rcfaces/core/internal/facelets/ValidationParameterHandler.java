/*
 * $Id: ValidationParameterHandler.java,v 1.1 2014/02/05 16:05:53 jbmeslin Exp $
 */
package org.rcfaces.core.internal.facelets;

import javax.faces.component.UIComponent;
import javax.faces.view.facelets.FaceletContext;
import javax.faces.view.facelets.TagAttribute;
import javax.faces.view.facelets.TagConfig;
import javax.faces.view.facelets.TagException;
import javax.faces.view.facelets.TagHandler;

import org.rcfaces.core.internal.manager.IValidationParameters;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.1 $ $Date: 2014/02/05 16:05:53 $
 */
public class ValidationParameterHandler extends TagHandler {
    

    private final TagAttribute name;

    private final TagAttribute value;

    private final TagAttribute clientSide;

    public ValidationParameterHandler(TagConfig config) {
        super(config);
        this.name = this.getRequiredAttribute("name");
        this.value = this.getRequiredAttribute("value");
        this.clientSide = this.getAttribute("clientSide");
    }

    public void apply(FaceletContext ctx, UIComponent parent) {
        if (parent == null) {
            throw new TagException(this.tag, "Parent UIComponent was null");
        }

        // only process if the parent is new to the tree
        if (parent.getParent() != null) {
            return;
        }

        boolean clientSide = this.clientSide.getBoolean(ctx);

        IValidationParameters clientDataCapability = (IValidationParameters) parent;

        String nameValue = name.getValue(ctx);
        if (value.isLiteral()) {
            clientDataCapability.setValidationParameter(nameValue,
                    value.getValue(), clientSide);
            return;
        }

        clientDataCapability.setValidationParameter(nameValue,
                value.getValueExpression(ctx, String.class), clientSide);
    }

}
