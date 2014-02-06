/*
 * $Id: ComponentTools.java,v 1.5 2013/12/11 10:17:38 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.internal.tools;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.faces.application.FacesMessage;
import javax.faces.component.NamingContainer;
import javax.faces.component.UICommand;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;
import javax.faces.event.FacesEvent;
import javax.faces.event.FacesListener;
import javax.faces.event.PhaseId;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.component.capability.IAsyncDecodeModeCapability;
import org.rcfaces.core.component.capability.IPrependIdCapability;
import org.rcfaces.core.internal.RcfacesContext;
import org.rcfaces.core.internal.adapter.IAdapterManager;
import org.rcfaces.core.internal.capability.IComponentEngine;
import org.rcfaces.core.internal.lang.StringAppender;
import org.rcfaces.core.internal.listener.IScriptListener;
import org.rcfaces.core.internal.renderkit.WriterException;
import org.rcfaces.core.internal.util.ComponentIterators;
import org.rcfaces.core.lang.IAdaptable;
import org.rcfaces.core.lang.provider.ICursorProvider;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.5 $ $Date: 2013/12/11 10:17:38 $
 */
public final class ComponentTools extends ComponentTools0 {

	private static final Log LOG = LogFactory.getLog(ComponentTools.class);

	public static final String[] STRING_EMPTY_ARRAY = new String[0];

	static final String VARIABLE_SCOPE_VALUE = "org.rcfaces.core.internal.VARIABLE_SCOPE_VALUE";

	static final String NONE_VARIABLE_SCOPE = "##~NONE~"
			+ System.currentTimeMillis();

	private static final UIComponent[] COMPONENT_EMPTY_ARRAY = new UIComponent[0];

	public static final String PARTIAL_ASYNC_DECODE_PROPERTY = "org.rcfaces.async.partial";

	public static final boolean isAnonymousComponentId(String componentId) {
		if (componentId == null) {
			return false;
		}

		int idx = componentId.lastIndexOf(NamingContainer.SEPARATOR_CHAR);
		if (idx >= 0) {
			componentId = componentId.substring(idx + 1);
		}

		return componentId.startsWith(UIViewRoot.UNIQUE_ID_PREFIX);
	}

	public static final void encodeRecursive(FacesContext context,
			UIComponent component) throws WriterException {
		if (component.isRendered() == false) {
			return;
		}

		try {
			component.encodeBegin(context);

			if (component.getRendersChildren()) {
				component.encodeChildren(context);

			} else {
				encodeChildrenRecursive(context, component);
			}

			component.encodeEnd(context);

		} catch (WriterException ex) {
			throw ex;

		} catch (IOException ex) {
			throw new WriterException("Can not encode component '"
					+ component.getId() + "'.", ex, component);
		}
	}

	public static final void encodeChildrenRecursive(FacesContext context,
			UIComponent component) throws WriterException {
		Iterator children = component.getChildren().iterator();

		for (; children.hasNext();) {
			UIComponent child = (UIComponent) children.next();
			if (child.isRendered() == false) {
				continue;
			}

			try {
				child.encodeBegin(context);

				if (child.getRendersChildren()) {
					child.encodeChildren(context);

				} else {
					encodeChildrenRecursive(context, child);
				}

				child.encodeEnd(context);

			} catch (WriterException ex) {
				throw ex;

			} catch (IOException ex) {
				throw new WriterException("Can not encode child component '"
						+ child.getId() + "'.", ex, child);
			}
		}
	}

	public static UIComponent getForComponent(FacesContext context,
			String forComponent, UIComponent component) {
		if (forComponent == null || forComponent.length() < 1) {
			return null;
		}

		try {
			// Check the naming container of the current
			// component for component identified by
			// 'forComponent'
			for (UIComponent currentParent = component; currentParent != null; currentParent = currentParent
					.getParent()) {

				try {
					UIComponent result = currentParent
							.findComponent(forComponent);
					if (result != null) {
						if (LOG.isDebugEnabled()) {
							LOG.debug("Get for component '" + forComponent
									+ "' returns " + result.getId());
						}
						return result;
					}

				} catch (IllegalArgumentException ex) {
					LOG.error("Compute for component '" + component.getId()
							+ "'", ex);

					break;
				}
			}

			return findUIComponentBelow(context.getViewRoot(), forComponent);

		} catch (RuntimeException ex) {
			LOG.error("Compute for component '" + component.getId() + "'", ex);

			throw ex;
		}
	}

	private static UIComponent findUIComponentBelow(UIComponent startPoint,
			String forComponent) {
		List children = startPoint.getChildren();

		if (LOG.isDebugEnabled()) {
			LOG.debug("Find component below '" + startPoint.getId() + "' to '"
					+ forComponent + "'.");
		}

		int size = children.size();
		for (int i = 0; i < size; i++) {
			UIComponent comp = (UIComponent) children.get(i);

			boolean nc = (comp instanceof NamingContainer);
			if (nc) {
				if (comp instanceof IPrependIdCapability) {
					nc = ((IPrependIdCapability) comp).isPrependId();
				}
			}

			if (nc) {
				try {
					UIComponent retComp = comp.findComponent(forComponent);

					if (retComp != null) {
						if (LOG.isDebugEnabled()) {
							LOG.debug("Find component below '" + forComponent
									+ "' returns " + retComp.getId());
						}

						return retComp;
					}

				} catch (IllegalArgumentException ex) {
					LOG.error("Find component below '" + startPoint.getId()
							+ "' to '" + forComponent + "' exception:", ex);
				}
			}

			if (comp.getChildCount() > 0) {
				UIComponent retComp = findUIComponentBelow(comp, forComponent);
				if (retComp != null) {
					return retComp;
				}
			}
		}

		return null;
	}

	public static UIViewRoot getViewRoot(UIComponent component) {
		for (; component != null;) {

			if (component instanceof UIViewRoot) {
				return (UIViewRoot) component;
			}

			component = component.getParent();
		}

		return null;
	}

	public static Converter createConverter(FacesContext facesContext,
			String converterId) {
		if (facesContext == null) {
			facesContext = FacesContext.getCurrentInstance();
		}

		Converter converter = facesContext.getApplication().createConverter(
				converterId);

		if (LOG.isDebugEnabled()) {
			LOG.debug("Get converter id=" + converterId + "' object="
					+ converter);
		}

		return converter;
	}

	/**
	 * 
	 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
	 * @version $Revision: 1.5 $ $Date: 2013/12/11 10:17:38 $
	 */
	private static class Entry implements Map.Entry {
		private final Object key;

		private Object value;

		/**
		 * Create new entry.
		 */
		Entry(Object k, Object v) {
			value = v;
			key = k;
		}

		public Object getKey() {
			return key;
		}

		public Object getValue() {
			return value;
		}

		public boolean equals(Object o) {
			if (!(o instanceof Map.Entry)) {
				return false;
			}

			Map.Entry e = (Map.Entry) o;
			Object k1 = getKey();
			Object k2 = e.getKey();
			if (k1 == k2 || (k1 != null && k1.equals(k2))) {
				Object v1 = getValue();
				Object v2 = e.getValue();
				if (v1 == v2 || (v1 != null && v1.equals(v2))) {
					return true;
				}
			}
			return false;
		}

		public int hashCode() {
			return key.hashCode() ^ (value == null ? 0 : value.hashCode());
		}

		public String toString() {
			return getKey() + "=" + getValue();
		}

		public Object setValue(Object value) {
			throw new UnsupportedOperationException("Not implemented !");
		}
	}

	public static UIComponent findComponent(UIComponent startPoint, Class clazz) {
		UIComponent retComp = null;
		List children = startPoint.getChildren();

		int size = children.size();
		for (int i = 0; i < size; i++) {
			UIComponent comp = (UIComponent) children.get(i);

			if (clazz.isInstance(comp)) {
				return comp;
			}

			if (comp.getChildCount() > 0) {
				retComp = findComponent(comp, clazz);
				if (retComp != null) {
					return retComp;
				}
			}
		}

		return null;
	}

	/**
	 * 
	 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
	 * @version $Revision: 1.5 $ $Date: 2013/12/11 10:17:38 $
	 */
	public interface IVarScope {
		void popVar(FacesContext facesContext);
	}

	/**
	 * 
	 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
	 * @version $Revision: 1.5 $ $Date: 2013/12/11 10:17:38 $
	 */
	static class VarScope implements IVarScope {

		private final String var;

		private final Object previousObject;

		public VarScope(String var, Object previousObject) {
			this.var = var;
			this.previousObject = previousObject;
		}

		public void popVar(FacesContext facesContext) {
			Map requestMap = facesContext.getExternalContext().getRequestMap();

			if (previousObject == null) {
				requestMap.remove(var);
				return;
			}

			requestMap.put(var, previousObject);
		}
	}

	public static void broadcast(UIComponent component, FacesEvent facesEvent,
			FacesListener listeners[]) {

		if (facesEvent == null) {
			throw new NullPointerException("Event is null");
		}
		
		

		if (listeners == null || listeners.length < 1) {
			return;
		}

		for (int i = 0; i < listeners.length; i++) {
			FacesListener listener = listeners[i];

			if (facesEvent.isAppropriateListener(listener) == false) {
				continue;
			}

			if (listener instanceof IScriptListener) {
				continue;
			}

			facesEvent.processListener(listener);
		}
	}

	public static Object getCursorValue(Object value, UIComponent component,
			FacesContext facesContext) {
		if (value == null) {
			return null;
		}

		ICursorProvider cursorProvider = null;

		if (value instanceof IAdaptable) {
			cursorProvider = (ICursorProvider) ((IAdaptable) value).getAdapter(
					ICursorProvider.class, component);

			if (LOG.isDebugEnabled()) {
				LOG.debug("Try to adapt '" + value + "' to ICursorProvider => "
						+ cursorProvider);
			}
		}

		if (cursorProvider == null) {
			IAdapterManager adapterManager = RcfacesContext.getInstance(
					facesContext).getAdapterManager();

			cursorProvider = (ICursorProvider) adapterManager.getAdapter(value,
					ICursorProvider.class, component);

			if (LOG.isDebugEnabled()) {
				LOG.debug("Ask adapterManager to adapt '" + value
						+ "' to ICursorProvider => " + cursorProvider);
			}
		}

		if (cursorProvider == null) {
			return null;
		}

		return cursorProvider.getCursorValue();
	}

	public static void addConversionErrorMessage(FacesContext context,
			UIInput input, ConverterException ce, Object submittedValue) {
		FacesMessage message = ce.getFacesMessage();
		if (message == null) {
			message = new FacesMessage("Conversion error !"); // TODO
			// MessageFactory.getMessage(context,
			// CONVERSION_MESSAGE_ID);
			if (message.getDetail() == null) {
				message.setDetail(ce.getMessage());
			}
		}

		message.setSeverity(FacesMessage.SEVERITY_ERROR);
		context.addMessage(input.getClientId(context), message);

	}

	public static void broadcastCommand(UICommand component,
			ActionEvent facesEvent, FacesListener listeners[]) {

		if (facesEvent == null) {
			throw new NullPointerException("Event is null");
		}

		boolean found = false;

		FacesContext facesContext = FacesContext.getCurrentInstance();

		if (listeners != null && listeners.length > 0) {
			for (int i = 0; i < listeners.length; i++) {
				FacesListener listener = listeners[i];

				if (listener instanceof IScriptListener) {
					continue;
				}

				if (facesEvent.isAppropriateListener(listener) == false) {
					continue;
				}

				facesEvent.processListener(listener);

				found = true;

				/*
				 * On les appelle tous ? if (facesContext.getRenderResponse()) {
				 * break; }
				 */
			}
		}

		if (found == false) {
			BindingTools.invokeActionListener(facesContext, component,
					facesEvent);

			// Invoke the default ActionListener
			ActionListener listener = facesContext.getApplication()
					.getActionListener();
			if (listener != null) {
				listener.processAction(facesEvent);
			}
		}

		if (facesContext.getRenderResponse()) {
			return;
		}

		broadcastActionCommand(facesContext, component);
	}

	public static UIComponent[] listChildren(FacesContext facesContext,
			UIComponent component, IComponentEngine engine, Class childClass,
			Serializable propertyName) {

		String ids = engine.getStringProperty(propertyName, facesContext);

		return listChildren(facesContext, component, ids, childClass);
	}

	public static UIComponent[] listChildren(FacesContext facesContext,
			 UIComponent component, String ids, Class< ? > childClass) {

        if (ids == null || ids.length() == 0) {
            if (childClass == null || childClass == UIComponent.class) {
                return COMPONENT_EMPTY_ARRAY;
            }
            return (UIComponent[]) Array.newInstance(UIComponent.class, 0);
		}

		StringTokenizer st = new StringTokenizer(ids, ",");

		if (st.hasMoreTokens() == false) {
			if (childClass == null || childClass == UIComponent.class) {
				return COMPONENT_EMPTY_ARRAY;
			}
			return (UIComponent[]) Array.newInstance(UIComponent.class, 0);
		}

		Object children[] = ComponentIterators.list(component, childClass)
				.toArray();

		List l = new ArrayList(st.countTokens());

		for (; st.hasMoreTokens();) {
			String id = st.nextToken().trim();

			if (id.startsWith("#")) {
				int idx = Integer.parseInt(id.substring(1));

				if (idx >= children.length) {
					continue;
				}

				UIComponent child = (UIComponent) children[idx];

				l.add(child);
				continue;
			}

			for (int i = 0; i < children.length; i++) {
				UIComponent child = (UIComponent) children[i];

				if (id.equals(child.getId()) == false) {
					continue;
				}

				l.add(child);
				break;
			}
		}

		if (childClass == null) {
			childClass = UIComponent.class;
		}

		return (UIComponent[]) l.toArray((UIComponent[]) Array.newInstance(
				UIComponent.class, l.size()));
	}

	public static void setChildren(UIComponent component,
			IComponentEngine engine, Class classOfChild,
			UIComponent[] children, Serializable propertyName) {

		String list = createStringFromList(children, classOfChild, component);

		engine.setProperty(propertyName, list);
	}

	private static String createStringFromList(UIComponent[] components,
			Class classOfChild, UIComponent component) {
		if (components == null) {
			return null;
		}

		if (components.length < 1) {
			return "";
		}

		Object children[] = null;

		StringAppender sa = new StringAppender(components.length * 32);
		for (int i = 0; i < components.length; i++) {
			UIComponent child = components[i];

			if (classOfChild.isInstance(child) == false) {
				continue;
			}

			if (sa.length() > 0) {
				sa.append(',');
			}

			String id = child.getId();

			if (id.startsWith(UIViewRoot.UNIQUE_ID_PREFIX)) {
				int childIndex = -1;

				if (children == null) {
					children = ComponentIterators.list(component, classOfChild)
							.toArray();
				}

				for (int j = 0; j < children.length; j++) {
					if (children[j] != child) {
						continue;
					}

					childIndex = j;
					break;
				}
				if (childIndex < 0) {
					LOG.error("Can not find index of component '" + child
							+ "'.");
					continue;
				}

				sa.append('#').append(childIndex);
				continue;
			}

			sa.append(child.getId());
		}

		return sa.toString();
	}

	public static boolean hasValidationServerListeners(
			FacesListener facesListeners[]) {
		for (int i = 0; i < facesListeners.length; i++) {
			FacesListener facesListener = facesListeners[i];

			if (facesListener instanceof IScriptListener) {
				continue;
			}

			return true;
		}

		return false;
	}

	public static boolean verifyAsyncDecode(FacesContext facesContext,
			IAsyncDecodeModeCapability asyncDecodeModeCapability,
			PhaseId phaseId) {

		if (asyncDecodeModeCapability.getAsyncDecodeMode() != IAsyncDecodeModeCapability.PARTIAL_ASYNC_DECODE_MODE) {
			return true;
		}

		Map parameters = facesContext.getExternalContext()
				.getRequestParameterMap();

		UIComponent component = (UIComponent) asyncDecodeModeCapability;

		String clientId = component.getClientId(facesContext);

		String value = (String) parameters.get(PARTIAL_ASYNC_DECODE_PROPERTY
				+ "." + clientId);

		if (value != null) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("Partial async decode enabled for component '"
						+ clientId + "' phase='" + phaseId + "'.");
			}

			return true;
		}

		if (LOG.isDebugEnabled()) {
			LOG.debug("Partial async decode DISABLED for component '"
					+ clientId + "' phase='" + phaseId + "'.");
		}
		return false;
	}
}
