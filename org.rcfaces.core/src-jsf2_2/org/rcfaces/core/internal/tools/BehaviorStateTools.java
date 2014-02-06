package org.rcfaces.core.internal.tools;

import java.util.List;
import java.util.Map;

import javax.faces.component.UIComponentBase;
import javax.faces.component.behavior.ClientBehavior;
import javax.faces.context.FacesContext;

import org.rcfaces.core.internal.behaviors.IRCFacesBahavior;

public class BehaviorStateTools {

	public static Object saveBehaviorsState(UIComponentBase component,
			FacesContext context) {

		Map<String, List<ClientBehavior>> behaviors = component
				.getClientBehaviors();
		
		if (behaviors.isEmpty()) {
			return null;
		}

		Object state[] = new Object[2];
		String names[] = new String[behaviors.size()];
		Object clazz[] = new Object[behaviors.size()];

		state[0] = names;
		state[1] = clazz;

		int i = 0;
		for (String eventName : behaviors.keySet()) {
			names[i] = eventName;

			List<ClientBehavior> clientBehaviors = behaviors.get(eventName);
			Object behaviorsClassName[] = new Object[clientBehaviors.size()];

			int j = 0;
			for (ClientBehavior cb : clientBehaviors) {
				if (cb instanceof IRCFacesBahavior) {
					behaviorsClassName[j++] = cb.getClass().getName(); //dolist
				}
			}

			clazz[i++] = behaviorsClassName;

		}
		return state;
	}

	public static void restoreBehaviorsState(UIComponentBase component,
			FacesContext context, Object state) {

		if(state == null) {
			return;
		}
		
		Object values[] = (Object[]) state;

		String[] names = (String[]) values[0];
		Object[] attachedBehaviors = (Object[]) values[1];

		for (int i = 0; i < names.length; i++) {

			String eventName = names[i];
			Object behaviorsClassName[] = (Object[]) attachedBehaviors[i];

			for (int j = 0; j < behaviorsClassName.length; j++) {

				try {

					Class<?> clazz = Class.forName((String) behaviorsClassName[j]);
					Object behavior = clazz.newInstance();
					component.addClientBehavior(eventName,(ClientBehavior) behavior);

				} catch (ClassNotFoundException e1) {
					e1.printStackTrace();
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
	}
}