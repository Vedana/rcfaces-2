/*
 * $Id: ServerDataHandler.java,v 1.1 2014/02/05 16:05:53 jbmeslin Exp $
 */
package org.rcfaces.core.internal.facelets;

import javax.faces.component.UIComponent;
import javax.faces.view.facelets.FaceletContext;
import javax.faces.view.facelets.TagAttribute;
import javax.faces.view.facelets.TagConfig;
import javax.faces.view.facelets.TagException;
import javax.faces.view.facelets.TagHandler;

import org.rcfaces.core.internal.manager.IServerDataManager;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.1 $ $Date: 2014/02/05 16:05:53 $
 */
public class ServerDataHandler extends TagHandler {
    

    private final TagAttribute name;

    private final TagAttribute value;

    public ServerDataHandler(TagConfig config) {
        super(config);
        this.name = this.getRequiredAttribute("name");
        this.value = this.getRequiredAttribute("value");
    }

    public void apply(FaceletContext ctx, UIComponent parent) {
        if (parent == null) {
            throw new TagException(this.tag, "Parent UIComponent was null");
        }

        // only process if the parent is new to the tree
        if (parent.getParent() != null) {
            return;
        }

        IServerDataManager serverDataCapability = (IServerDataManager) parent;

        String nameValue = name.getValue(ctx);
        if (value.isLiteral()) {
            serverDataCapability.setServerData(nameValue, value.getValue());
            return;
        }

        serverDataCapability.setServerData(nameValue,
                value.getValueExpression(ctx, Object.class));
    }
}
