package org.rcfaces.core.internal.behaviors;

import javax.faces.component.behavior.AjaxBehavior;
import javax.faces.component.behavior.FacesBehavior;

@FacesBehavior("org.rcfaces.behaviors.RCFacesAjaxBehavior")
public class RCFacesAjaxBehavior extends AjaxBehavior implements IRCFacesBahavior {
	
	public final static String BEHAVIOR_ID = "org.rcfaces.behaviors.RCFacesAjaxBehavior";
}
