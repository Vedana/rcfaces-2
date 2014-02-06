/*
 * $Id: ListenersTools.java,v 1.3 2013/07/03 12:25:06 jbmeslin Exp $
 */
package org.rcfaces.core.internal.tools;

import javax.el.MethodExpression;
import javax.faces.FacesException;
import javax.faces.application.Application;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.FacesListener;
import javax.faces.event.ValueChangeListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.component.capability.IAdditionalInformationEventCapability;
import org.rcfaces.core.component.capability.ICheckEventCapability;
import org.rcfaces.core.component.capability.IClickEventCapability;
import org.rcfaces.core.component.capability.ICloseEventCapability;
import org.rcfaces.core.component.capability.ICriteriaEventCapability;
import org.rcfaces.core.component.capability.IDoubleClickEventCapability;
import org.rcfaces.core.component.capability.IDragEventCapability;
import org.rcfaces.core.component.capability.IDropCompleteEventCapability;
import org.rcfaces.core.component.capability.IDropEventCapability;
import org.rcfaces.core.component.capability.IErrorEventCapability;
import org.rcfaces.core.component.capability.IExpandEventCapability;
import org.rcfaces.core.component.capability.IFocusBlurEventCapability;
import org.rcfaces.core.component.capability.IInitEventCapability;
import org.rcfaces.core.component.capability.IKeyDownEventCapability;
import org.rcfaces.core.component.capability.IKeyPressEventCapability;
import org.rcfaces.core.component.capability.IKeyUpEventCapability;
import org.rcfaces.core.component.capability.ILoadEventCapability;
import org.rcfaces.core.component.capability.IMenuEventCapability;
import org.rcfaces.core.component.capability.IMouseEventCapability;
import org.rcfaces.core.component.capability.IPreSelectionEventCapability;
import org.rcfaces.core.component.capability.IPropertyChangeEventCapability;
import org.rcfaces.core.component.capability.IResetEventCapability;
import org.rcfaces.core.component.capability.ISelectionEventCapability;
import org.rcfaces.core.component.capability.IServiceEventCapability;
import org.rcfaces.core.component.capability.ISortEventCapability;
import org.rcfaces.core.component.capability.ISuggestionEventCapability;
import org.rcfaces.core.component.capability.IUserEventCapability;
import org.rcfaces.core.component.capability.IValidationEventCapability;
import org.rcfaces.core.component.capability.IValueChangeEventCapability;
import org.rcfaces.core.event.IAdditionalInformationListener;
import org.rcfaces.core.event.IBlurListener;
import org.rcfaces.core.event.ICheckListener;
import org.rcfaces.core.event.IClickListener;
import org.rcfaces.core.event.ICloseListener;
import org.rcfaces.core.event.ICriteriaListener;
import org.rcfaces.core.event.IDoubleClickListener;
import org.rcfaces.core.event.IDragListener;
import org.rcfaces.core.event.IDropCompleteListener;
import org.rcfaces.core.event.IDropListener;
import org.rcfaces.core.event.IExpandListener;
import org.rcfaces.core.event.IFocusListener;
import org.rcfaces.core.event.IInitListener;
import org.rcfaces.core.event.IKeyDownListener;
import org.rcfaces.core.event.IKeyPressListener;
import org.rcfaces.core.event.IKeyUpListener;
import org.rcfaces.core.event.ILoadListener;
import org.rcfaces.core.event.IMenuListener;
import org.rcfaces.core.event.IMouseOutListener;
import org.rcfaces.core.event.IMouseOverListener;
import org.rcfaces.core.event.IPreSelectionListener;
import org.rcfaces.core.event.IPropertyChangeListener;
import org.rcfaces.core.event.IResetListener;
import org.rcfaces.core.event.ISelectionListener;
import org.rcfaces.core.event.IServiceEventListener;
import org.rcfaces.core.event.ISortListener;
import org.rcfaces.core.event.ISuggestionListener;
import org.rcfaces.core.event.IUserEventListener;
import org.rcfaces.core.event.IValidationListener;
import org.rcfaces.core.internal.capability.IComponentLifeCycle;
import org.rcfaces.core.internal.listener.AdditionalInformationActionListener;
import org.rcfaces.core.internal.listener.AdditionalInformationScriptListener;
import org.rcfaces.core.internal.listener.BlurActionListener;
import org.rcfaces.core.internal.listener.BlurScriptListener;
import org.rcfaces.core.internal.listener.ChangeActionListener;
import org.rcfaces.core.internal.listener.ChangeScriptListener;
import org.rcfaces.core.internal.listener.CheckActionListener;
import org.rcfaces.core.internal.listener.CheckScriptListener;
import org.rcfaces.core.internal.listener.ClickActionListener;
import org.rcfaces.core.internal.listener.ClickScriptListener;
import org.rcfaces.core.internal.listener.CloseActionListener;
import org.rcfaces.core.internal.listener.CloseScriptListener;
import org.rcfaces.core.internal.listener.CriteriaActionListener;
import org.rcfaces.core.internal.listener.CriteriaScriptListener;
import org.rcfaces.core.internal.listener.DoubleClickActionListener;
import org.rcfaces.core.internal.listener.DoubleClickScriptListener;
import org.rcfaces.core.internal.listener.DragScriptListener;
import org.rcfaces.core.internal.listener.DropCompleteActionListener;
import org.rcfaces.core.internal.listener.DropCompleteScriptListener;
import org.rcfaces.core.internal.listener.DropScriptListener;
import org.rcfaces.core.internal.listener.ErrorActionListener;
import org.rcfaces.core.internal.listener.ErrorScriptListener;
import org.rcfaces.core.internal.listener.ExpandActionListener;
import org.rcfaces.core.internal.listener.ExpandScriptListener;
import org.rcfaces.core.internal.listener.FocusActionListener;
import org.rcfaces.core.internal.listener.FocusScriptListener;
import org.rcfaces.core.internal.listener.IServerActionListener;
import org.rcfaces.core.internal.listener.InitScriptListener;
import org.rcfaces.core.internal.listener.KeyDownScriptListener;
import org.rcfaces.core.internal.listener.KeyPressActionListener;
import org.rcfaces.core.internal.listener.KeyPressScriptListener;
import org.rcfaces.core.internal.listener.KeyUpScriptListener;
import org.rcfaces.core.internal.listener.LoadActionListener;
import org.rcfaces.core.internal.listener.LoadScriptListener;
import org.rcfaces.core.internal.listener.MenuScriptListener;
import org.rcfaces.core.internal.listener.MouseOutScriptListener;
import org.rcfaces.core.internal.listener.MouseOverScriptListener;
import org.rcfaces.core.internal.listener.PreSelectionActionListener;
import org.rcfaces.core.internal.listener.PreSelectionScriptListener;
import org.rcfaces.core.internal.listener.PropertyChangeActionListener;
import org.rcfaces.core.internal.listener.PropertyChangeScriptListener;
import org.rcfaces.core.internal.listener.ResetActionListener;
import org.rcfaces.core.internal.listener.ResetScriptListener;
import org.rcfaces.core.internal.listener.SelectionActionListener;
import org.rcfaces.core.internal.listener.SelectionScriptListener;
import org.rcfaces.core.internal.listener.ServiceEventActionListener;
import org.rcfaces.core.internal.listener.ServiceEventScriptListener;
import org.rcfaces.core.internal.listener.SortActionListener;
import org.rcfaces.core.internal.listener.SortScriptListener;
import org.rcfaces.core.internal.listener.SuggestionActionListener;
import org.rcfaces.core.internal.listener.SuggestionScriptListener;
import org.rcfaces.core.internal.listener.UnsupportedListenerTypeException;
import org.rcfaces.core.internal.listener.UserEventActionListener;
import org.rcfaces.core.internal.listener.UserEventScriptListener;
import org.rcfaces.core.internal.listener.ValidationActionListener;
import org.rcfaces.core.internal.listener.ValidationScriptListener;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/07/03 12:25:06 $
 */
public class ListenersTools {

	private static final Log LOG = LogFactory.getLog(ListenersTools.class);

	/**
	 * 
	 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
	 * @version $Revision: 1.3 $ $Date: 2013/07/03 12:25:06 $
	 */
	public interface IListenerType {

		IServerActionListener addActionListener(UIComponent component,
				Application application, String expression,
				boolean partialRendering);

		void addScriptListener(UIComponent component, String scriptType,
				String expression);

        Class< ? extends FacesListener> getListenerClass();

		// void clearListeners(UIComponent component);
	}

	/**
	 * 
	 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
	 * @version $Revision: 1.3 $ $Date: 2013/07/03 12:25:06 $
	 */
	private static abstract class AbstractListenerType implements IListenerType {
	}

	public static final IListenerType BLUR_LISTENER_TYPE = new AbstractListenerType() {
		public void addScriptListener(UIComponent component, String scriptType,
				String command) {

			IFocusBlurEventCapability focusBlurEventCapability = (IFocusBlurEventCapability) component;

			focusBlurEventCapability.addBlurListener(new BlurScriptListener(
					scriptType, command));
		}

		public IServerActionListener addActionListener(UIComponent component,
				Application application, String expression,
				boolean partialRendering) {
			IFocusBlurEventCapability focusBlurEventCapability = (IFocusBlurEventCapability) component;

			BlurActionListener blurActionListener = new BlurActionListener(
					expression, partialRendering);
			focusBlurEventCapability.addBlurListener(blurActionListener);

			return blurActionListener;
		}

        public Class< ? extends FacesListener> getListenerClass() {
			return IBlurListener.class;
		}
	};
	
	public static final IListenerType CRITERIA_LISTENER_TYPE = new AbstractListenerType() {
		public void addScriptListener(UIComponent component, String scriptType,
				String command) {

			ICriteriaEventCapability focusBlurEventCapability = (ICriteriaEventCapability) component;

			focusBlurEventCapability
					.addCriteriaListener(new CriteriaScriptListener(scriptType,
							command));
		}

		public IServerActionListener addActionListener(UIComponent component,
				Application application, String expression,
				boolean partialRendering) {
			ICriteriaEventCapability criteriaEventCapability = (ICriteriaEventCapability) component;

			CriteriaActionListener criteriaActionListener = new CriteriaActionListener(
					expression, partialRendering);
			criteriaEventCapability.addCriteriaListener(criteriaActionListener);

			return criteriaActionListener;
		}

        public Class< ? extends FacesListener> getListenerClass() {
			return ICriteriaListener.class;
		}
	};

	public static final IListenerType DRAG_LISTENER_TYPE = new AbstractListenerType() {
		public void addScriptListener(UIComponent component, String scriptType,
				String command) {
			IDragEventCapability dragEventCapability = (IDragEventCapability) component;

			dragEventCapability.addDragListener(new DragScriptListener(
					scriptType, command));
		}

		public IServerActionListener addActionListener(UIComponent component,
				Application application, String expression,
				boolean partialRendering) {
			throw new UnsupportedListenerTypeException("drag");
		}

        public Class< ? extends FacesListener> getListenerClass() {
			return IDragListener.class;
		}
	};

	public static final IListenerType DROP_LISTENER_TYPE = new AbstractListenerType() {
		public void addScriptListener(UIComponent component, String scriptType,
				String command) {
			IDropEventCapability selectEventCapability = (IDropEventCapability) component;

			selectEventCapability.addDropListener(new DropScriptListener(
					scriptType, command));
		}

		public IServerActionListener addActionListener(UIComponent component,
				Application application, String expression,
				boolean partialRendering) {
			throw new UnsupportedListenerTypeException("drop");
		}

        public Class< ? extends FacesListener> getListenerClass() {
			return IDropListener.class;
		}
	};

	public static final IListenerType DROP_COMPLETE_LISTENER_TYPE = new AbstractListenerType() {
		public void addScriptListener(UIComponent component, String scriptType,
				String command) {
			IDropCompleteEventCapability selectEventCapability = (IDropCompleteEventCapability) component;

			selectEventCapability
					.addDropCompleteListener(new DropCompleteScriptListener(
							scriptType, command));
		}

		public IServerActionListener addActionListener(UIComponent component,
				Application application, String expression,
				boolean partialRendering) {
			IDropCompleteEventCapability selectEventCapability = (IDropCompleteEventCapability) component;

			DropCompleteActionListener dropActionListener = new DropCompleteActionListener(
					expression, partialRendering);
			selectEventCapability.addDropCompleteListener(dropActionListener);

			return dropActionListener;
		}

        public Class< ? extends FacesListener> getListenerClass() {
			return IDropCompleteListener.class;
		}
	};

	public static final IListenerType FOCUS_LISTENER_TYPE = new AbstractListenerType() {
		public void addScriptListener(UIComponent component, String scriptType,
				String command) {
			IFocusBlurEventCapability focusBlurEventCapability = (IFocusBlurEventCapability) component;

			focusBlurEventCapability.addFocusListener(new FocusScriptListener(
					scriptType, command));
		}

		public IServerActionListener addActionListener(UIComponent component,
				Application application, String expression,
				boolean partialRendering) {
			IFocusBlurEventCapability expandEventCapability = (IFocusBlurEventCapability) component;

			FocusActionListener focusActionListener = new FocusActionListener(
					expression, partialRendering);
			expandEventCapability.addFocusListener(focusActionListener);

			return focusActionListener;
		}

        public Class< ? extends FacesListener> getListenerClass() {
			return IFocusListener.class;
		}
	};

	public static final IListenerType LOAD_LISTENER_TYPE = new AbstractListenerType() {
		public void addScriptListener(UIComponent component, String scriptType,
				String command) {
			ILoadEventCapability loadEventCapability = (ILoadEventCapability) component;

			loadEventCapability.addLoadListener(new LoadScriptListener(
					scriptType, command));
		}

		public IServerActionListener addActionListener(UIComponent component,
				Application application, String expression,
				boolean partialRendering) {
			ILoadEventCapability loadEventCapability = (ILoadEventCapability) component;

			LoadActionListener loadActionListener = new LoadActionListener(
					expression, partialRendering);
			loadEventCapability.addLoadListener(loadActionListener);

			return loadActionListener;
		}

        public Class< ? extends FacesListener> getListenerClass() {
			return ILoadListener.class;
		}
	};

	public static final IListenerType EXPAND_LISTENER_TYPE = new AbstractListenerType() {
		public void addScriptListener(UIComponent component, String scriptType,
				String command) {
			IExpandEventCapability expandEventCapability = (IExpandEventCapability) component;

			expandEventCapability.addExpandListener(new ExpandScriptListener(
					scriptType, command));
		}

		public IServerActionListener addActionListener(UIComponent component,
				Application application, String expression,
				boolean partialRendering) {
			IExpandEventCapability expandEventCapability = (IExpandEventCapability) component;

			ExpandActionListener expandActionListener = new ExpandActionListener(
					expression, partialRendering);
			expandEventCapability.addExpandListener(expandActionListener);

			return expandActionListener;
		}

        public Class< ? extends FacesListener> getListenerClass() {
			return IExpandListener.class;
		}
	};

	public static final IListenerType ERROR_LISTENER_TYPE = new AbstractListenerType() {
		

		public void addScriptListener(UIComponent component, String scriptType,
				String command) {
			IErrorEventCapability errorEventCapability = (IErrorEventCapability) component;

			errorEventCapability.addErrorListener(new ErrorScriptListener(
					scriptType, command));
		}

		public IServerActionListener addActionListener(UIComponent component,
				Application application, String expression,
				boolean partialRendering) {
			IErrorEventCapability errorEventCapability = (IErrorEventCapability) component;

			ErrorActionListener errorActionListener = new ErrorActionListener(
					expression, partialRendering);
			errorEventCapability.addErrorListener(errorActionListener);

			return errorActionListener;
		}

        public Class< ? extends FacesListener> getListenerClass() {
			return IDropListener.class;
		}
	};

	public static final IListenerType DOUBLE_CLICK_LISTENER_TYPE = new AbstractListenerType() {
		

		public void addScriptListener(UIComponent component, String scriptType,
				String command) {
			IDoubleClickEventCapability doubleClickEventCapability = (IDoubleClickEventCapability) component;

			doubleClickEventCapability
					.addDoubleClickListener(new DoubleClickScriptListener(
							scriptType, command));
		}

		public IServerActionListener addActionListener(UIComponent component,
				Application application, String expression,
				boolean partialRendering) {
			IDoubleClickEventCapability doubleClickEventCapability = (IDoubleClickEventCapability) component;

			DoubleClickActionListener doubleClickActionListener = new DoubleClickActionListener(
					expression, partialRendering);
			doubleClickEventCapability
					.addDoubleClickListener(doubleClickActionListener);

			return doubleClickActionListener;
		}

        public Class< ? extends FacesListener> getListenerClass() {
			return IDoubleClickListener.class;
		}
	};
	
	 public static final IListenerType CLICK_LISTENER_TYPE = new AbstractListenerType() {

	        public void addScriptListener(UIComponent component, String scriptType,
	                String command) {
	            IClickEventCapability clickEventCapability = (IClickEventCapability) component;

	            clickEventCapability.addClickListener(new ClickScriptListener(
	                    scriptType, command));
	        }

	        public IServerActionListener addActionListener(UIComponent component,
	                Application application, String expression,
	                boolean partialRendering) {
	            IClickEventCapability clickEventCapability = (IClickEventCapability) component;

	            ClickActionListener clickActionListener = new ClickActionListener(
	                    expression, partialRendering);
	            clickEventCapability.addClickListener(clickActionListener);

	            return clickActionListener;
	        }

	        public Class< ? extends FacesListener> getListenerClass() {
	            return IClickListener.class;
	        }
	    };

	public static final IListenerType SELECTION_LISTENER_TYPE = new AbstractListenerType() {
		

		public void addScriptListener(UIComponent component, String scriptType,
				String command) {
			ISelectionEventCapability selectEventCapability = (ISelectionEventCapability) component;

			selectEventCapability
					.addSelectionListener(new SelectionScriptListener(
							scriptType, command));
		}

		public IServerActionListener addActionListener(UIComponent component,
				Application application, String expression,
				boolean partialRendering) {
			ISelectionEventCapability selectEventCapability = (ISelectionEventCapability) component;

			SelectionActionListener selectionActionListener = new SelectionActionListener(
					expression, partialRendering);
			selectEventCapability.addSelectionListener(selectionActionListener);

			return selectionActionListener;
		}

        public Class< ? extends FacesListener> getListenerClass() {
			return ISelectionListener.class;
		}
	};

	public static final IListenerType ADDITIONAL_INFORMATION_LISTENER_TYPE = new AbstractListenerType() {
		

		public void addScriptListener(UIComponent component, String scriptType,
				String command) {
			IAdditionalInformationEventCapability additionalInformationEventCapability = (IAdditionalInformationEventCapability) component;

			additionalInformationEventCapability
					.addAdditionalInformationListener(new AdditionalInformationScriptListener(
							scriptType, command));
		}

		public IServerActionListener addActionListener(UIComponent component,
				Application application, String expression,
				boolean partialRendering) {
			IAdditionalInformationEventCapability additionalInformationEventCapability = (IAdditionalInformationEventCapability) component;

			AdditionalInformationActionListener additionalInformationActionListener = new AdditionalInformationActionListener(
					expression, partialRendering);
			additionalInformationEventCapability
					.addAdditionalInformationListener(additionalInformationActionListener);

			return additionalInformationActionListener;
		}

        public Class< ? extends FacesListener> getListenerClass() {
			return IAdditionalInformationListener.class;
		}
	};

	public static final IListenerType CHECK_LISTENER_TYPE = new AbstractListenerType() {
		

		public void addScriptListener(UIComponent component, String scriptType,
				String command) {
			ICheckEventCapability checkEventCapability = (ICheckEventCapability) component;

			checkEventCapability.addCheckListener(new CheckScriptListener(
					scriptType, command));
		}

		public IServerActionListener addActionListener(UIComponent component,
				Application application, String expression,
				boolean partialRendering) {
			ICheckEventCapability checkEventCapability = (ICheckEventCapability) component;

			CheckActionListener checkActionListener = new CheckActionListener(
					expression, partialRendering);
			checkEventCapability.addCheckListener(checkActionListener);

			return checkActionListener;
		}

        public Class< ? extends FacesListener> getListenerClass() {
			return ICheckListener.class;
		}
	};

	public static final IListenerType CLOSE_LISTENER_TYPE = new AbstractListenerType() {
		

		public void addScriptListener(UIComponent component, String scriptType,
				String command) {
			ICloseEventCapability closeEventCapability = (ICloseEventCapability) component;

			closeEventCapability.addCloseListener(new CloseScriptListener(
					scriptType, command));
		}

		public IServerActionListener addActionListener(UIComponent component,
				Application application, String expression,
				boolean partialRendering) {
			ICloseEventCapability closeEventCapability = (ICloseEventCapability) component;

			CloseActionListener closeActionListener = new CloseActionListener(
					expression, partialRendering);
			closeEventCapability.addCloseListener(closeActionListener);

			return closeActionListener;
		}

        public Class< ? extends FacesListener> getListenerClass() {
			return ICloseListener.class;
		}
	};

	public static final IListenerType VALUE_CHANGE_LISTENER_TYPE = new AbstractListenerType() {
		public void addScriptListener(UIComponent component, String scriptType,
				String command) {
			IValueChangeEventCapability changeEventCapability = (IValueChangeEventCapability) component;

			changeEventCapability
					.addValueChangeListener(new ChangeScriptListener(
							scriptType, command));
		}

		public IServerActionListener addActionListener(UIComponent component,
				Application application, String expression,
				boolean partialRendering) {
			IValueChangeEventCapability changeEventCapability = (IValueChangeEventCapability) component;

			ChangeActionListener changeActionListener = new ChangeActionListener(
					expression, partialRendering);
			changeEventCapability.addValueChangeListener(changeActionListener);

			return changeActionListener;
		}

        public Class< ? extends FacesListener> getListenerClass() {
			return ValueChangeListener.class;
		}
	};

	public static final IListenerType SUGGESTION_LISTENER_TYPE = new AbstractListenerType() {
		public void addScriptListener(UIComponent component, String scriptType,
				String command) {
			ISuggestionEventCapability prepareEventCapability = (ISuggestionEventCapability) component;

			prepareEventCapability
					.addSuggestionListener(new SuggestionScriptListener(
							scriptType, command));
		}

		public IServerActionListener addActionListener(UIComponent component,
				Application application, String expression,
				boolean partialRendering) {
			ISuggestionEventCapability prepareEventCapability = (ISuggestionEventCapability) component;

			SuggestionActionListener suggestionActionListener = new SuggestionActionListener(
					expression, partialRendering);
			prepareEventCapability
					.addSuggestionListener(suggestionActionListener);

			return suggestionActionListener;
		}

        public Class< ? extends FacesListener> getListenerClass() {
			return ISuggestionListener.class;
		}
	};

	public static final IListenerType PROPERTY_CHANGE_LISTENER_TYPE = new AbstractListenerType() {
		

		public void addScriptListener(UIComponent component, String scriptType,
				String command) {
			IPropertyChangeEventCapability changeEventCapability = (IPropertyChangeEventCapability) component;

			changeEventCapability
					.addPropertyChangeListener(new PropertyChangeScriptListener(
							scriptType, command));
		}

		public IServerActionListener addActionListener(UIComponent component,
				Application application, String expression,
				boolean partialRendering) {
			IPropertyChangeEventCapability propertyChangeEventCapability = (IPropertyChangeEventCapability) component;

			PropertyChangeActionListener propertyChangeActionListener = new PropertyChangeActionListener(
					expression, partialRendering);
			propertyChangeEventCapability
					.addPropertyChangeListener(propertyChangeActionListener);

			return propertyChangeActionListener;
		}

        public Class< ? extends FacesListener> getListenerClass() {
			return IPropertyChangeListener.class;
		}
	};

	public static final IListenerType KEY_PRESS_LISTENER_TYPE = new AbstractListenerType() {
		

		public void addScriptListener(UIComponent component, String scriptType,
				String command) {
			IKeyPressEventCapability keyEventCapability = (IKeyPressEventCapability) component;

			keyEventCapability.addKeyPressListener(new KeyPressScriptListener(
					scriptType, command));
		}

		public IServerActionListener addActionListener(UIComponent component,
				Application application, String expression,
				boolean partialRendering) {
			IKeyPressEventCapability keyPressEventCapability = (IKeyPressEventCapability) component;

			KeyPressActionListener keyPressActionListener = new KeyPressActionListener(
					expression, partialRendering);
			keyPressEventCapability.addKeyPressListener(keyPressActionListener);

			return keyPressActionListener;
		}

        public Class< ? extends FacesListener> getListenerClass() {
			return IKeyPressListener.class;
		}
	};

	public static final IListenerType KEY_DOWN_LISTENER_TYPE = new AbstractListenerType() {
		

		public void addScriptListener(UIComponent component, String scriptType,
				String command) {
			IKeyDownEventCapability keyEventCapability = (IKeyDownEventCapability) component;

			keyEventCapability.addKeyDownListener(new KeyDownScriptListener(
					scriptType, command));
		}

		public IServerActionListener addActionListener(UIComponent component,
				Application application, String expression,
				boolean partialRendering) {
			throw new UnsupportedListenerTypeException("keyDown");
		}

        public Class< ? extends FacesListener> getListenerClass() {
			return IKeyDownListener.class;
		}
	};

	public static final IListenerType KEY_UP_LISTENER_TYPE = new AbstractListenerType() {
		

		public void addScriptListener(UIComponent component, String scriptType,
				String command) {
			IKeyUpEventCapability keyEventCapability = (IKeyUpEventCapability) component;

			keyEventCapability.addKeyUpListener(new KeyUpScriptListener(
					scriptType, command));
		}

		public IServerActionListener addActionListener(UIComponent component,
				Application application, String expression,
				boolean partialRendering) {
			throw new UnsupportedListenerTypeException("keyUp");
		}

        public Class< ? extends FacesListener> getListenerClass() {
			return IKeyUpListener.class;
		}
	};

	public static final IListenerType INIT_LISTENER_TYPE = new AbstractListenerType() {
		

		public void addScriptListener(UIComponent component, String scriptType,
				String command) {
			IInitEventCapability initEventCapability = (IInitEventCapability) component;

			initEventCapability.addInitListener(new InitScriptListener(
					scriptType, command));
		}

		public IServerActionListener addActionListener(UIComponent component,
				Application application, String expression,
				boolean partialRendering) {
			throw new UnsupportedListenerTypeException("init");
		}

        public Class< ? extends FacesListener> getListenerClass() {
			return IInitListener.class;
		}
	};

	public static final IListenerType MOUSE_OUT_LISTENER_TYPE = new AbstractListenerType() {
		

		public void addScriptListener(UIComponent component, String scriptType,
				String command) {
			IMouseEventCapability mouseEventCapability = (IMouseEventCapability) component;

			mouseEventCapability
					.addMouseOutListener(new MouseOutScriptListener(scriptType,
							command));
		}

		public IServerActionListener addActionListener(UIComponent component,
				Application application, String expression,
				boolean partialRendering) {
			throw new UnsupportedListenerTypeException("mouseOut");
		}

        public Class< ? extends FacesListener> getListenerClass() {
			return IMouseOutListener.class;
		}
	};

	public static final IListenerType MOUSE_OVER_LISTENER_TYPE = new AbstractListenerType() {
		

		public void addScriptListener(UIComponent component, String scriptType,
				String command) {
			IMouseEventCapability mouseEventCapability = (IMouseEventCapability) component;

			mouseEventCapability
					.addMouseOverListener(new MouseOverScriptListener(
							scriptType, command));
		}

		public IServerActionListener addActionListener(UIComponent component,
				Application application, String expression,
				boolean partialRendering) {
			throw new UnsupportedListenerTypeException("mouseOver");
		}

        public Class< ? extends FacesListener> getListenerClass() {
			return IMouseOverListener.class;
		}
	};

	public static final IListenerType PRE_SELECTION_LISTENER_TYPE = new AbstractListenerType() {
		

		public void addScriptListener(UIComponent component, String scriptType,
				String command) {
			IPreSelectionEventCapability preSelectionEventCapability = (IPreSelectionEventCapability) component;

			preSelectionEventCapability
					.addPreSelectionListener(new PreSelectionScriptListener(
							scriptType, command));
		}

		public IServerActionListener addActionListener(UIComponent component,
				Application application, String expression,
				boolean partialRendering) {
            IPreSelectionEventCapability preSelectionEventCapability = (IPreSelectionEventCapability) component;

            PreSelectionActionListener preSelectionActionListener = new PreSelectionActionListener(
                    expression, partialRendering);
            preSelectionEventCapability
                    .addPreSelectionListener(preSelectionActionListener);

            return preSelectionActionListener;
		}

        public Class< ? extends FacesListener> getListenerClass() {
			return IPreSelectionListener.class;
		}
	};

	public static final IListenerType SORT_LISTENER_TYPE = new AbstractListenerType() {
		

		public void addScriptListener(UIComponent component, String scriptType,
				String command) {
			ISortEventCapability sortEventCapability = (ISortEventCapability) component;

			sortEventCapability.addSortListener(new SortScriptListener(
					scriptType, command));
		}

		public IServerActionListener addActionListener(UIComponent component,
				Application application, String expression,
				boolean partialRendering) {

			ISortEventCapability sortEventCapability = (ISortEventCapability) component;

			SortActionListener sortActionListener = new SortActionListener(
					expression, partialRendering);
			sortEventCapability.addSortListener(sortActionListener);

			return sortActionListener;
		}

        public Class< ? extends FacesListener> getListenerClass() {
			return ISortListener.class;
		}
	};

	public static final IListenerType RESET_LISTENER_TYPE = new AbstractListenerType() {
		

		public void addScriptListener(UIComponent component, String scriptType,
				String command) {
			IResetEventCapability resetEventCapability = (IResetEventCapability) component;

			resetEventCapability.addResetListener(new ResetScriptListener(
					scriptType, command));
		}

		public IServerActionListener addActionListener(UIComponent component,
				Application application, String expression,
				boolean partialRendering) {

			IResetEventCapability sortEventCapability = (IResetEventCapability) component;

			ResetActionListener resetActionListener = new ResetActionListener(
					expression, partialRendering);
			sortEventCapability.addResetListener(resetActionListener);

			return resetActionListener;
		}

        public Class< ? extends FacesListener> getListenerClass() {
			return IResetListener.class;
		}
	};

	public static final IListenerType VALIDATION_LISTENER_TYPE = new AbstractListenerType() {
		

		public void addScriptListener(UIComponent component, String scriptType,
				String command) {
			IValidationEventCapability validationEventCapability = (IValidationEventCapability) component;

			validationEventCapability
					.addValidationListener(new ValidationScriptListener(
							scriptType, command));
		}

		public IServerActionListener addActionListener(UIComponent component,
				Application application, String expression,
				boolean partialRendering) {
			IValidationEventCapability validationEventCapability = (IValidationEventCapability) component;

			ValidationActionListener validationActionListener = new ValidationActionListener(
					expression, partialRendering);
			validationEventCapability
					.addValidationListener(validationActionListener);

			return validationActionListener;
		}

        public Class< ? extends FacesListener> getListenerClass() {
			return IValidationListener.class;
		}
	};

	public static final IListenerType MENU_LISTENER_TYPE = new AbstractListenerType() {
		

		public void addScriptListener(UIComponent component, String scriptType,
				String command) {
			IMenuEventCapability menuEventCapability = (IMenuEventCapability) component;

			menuEventCapability.addMenuListener(new MenuScriptListener(
					scriptType, command));
		}

		public IServerActionListener addActionListener(UIComponent component,
				Application application, String expression,
				boolean partialRendering) {
			throw new UnsupportedListenerTypeException("menuListener");
		}

        public Class< ? extends FacesListener> getListenerClass() {
			return IMenuListener.class;
		}
	};

	public static final IListenerType USER_EVENT_LISTENER_TYPE = new AbstractListenerType() {
		

		public void addScriptListener(UIComponent component, String scriptType,
				String command) {
			IUserEventCapability userEventCapability = (IUserEventCapability) component;

			userEventCapability
					.addUserEventListener(new UserEventScriptListener(
							scriptType, command));
		}

		public IServerActionListener addActionListener(UIComponent component,
				Application application, String expression,
				boolean partialRendering) {

			IUserEventCapability userEventCapability = (IUserEventCapability) component;

			UserEventActionListener userEventActionListener = new UserEventActionListener(
					expression, partialRendering);
			userEventCapability.addUserEventListener(userEventActionListener);

			return userEventActionListener;
		}

        public Class< ? extends FacesListener> getListenerClass() {
			return IUserEventListener.class;
		}
	};

	public static final IListenerType SERVICE_EVENT_LISTENER_TYPE = new AbstractListenerType() {
		

		public void addScriptListener(UIComponent component, String scriptType,
				String command) {
			IServiceEventCapability userEventCapability = (IServiceEventCapability) component;

			userEventCapability
					.addServiceEventListener(new ServiceEventScriptListener(
							scriptType, command));
		}

		public IServerActionListener addActionListener(UIComponent component,
				Application application, String expression,
				boolean partialRendering) {

			IServiceEventCapability userEventCapability = (IServiceEventCapability) component;

			ServiceEventActionListener serviceEventActionListener = new ServiceEventActionListener(
					expression, partialRendering);
			userEventCapability
					.addServiceEventListener(serviceEventActionListener);

			return serviceEventActionListener;
		}

        public Class< ? extends FacesListener> getListenerClass() {
			return IServiceEventListener.class;
		}
	};

	private static final String PARTIAL_RENDERING_PREFIX = "ppr:";

	public static void parseListener(FacesContext facesContext,
			UIComponent component, IListenerType listenerType, String expression) {
		parseListener(facesContext, component, listenerType, expression, false,
				null);
	}

	public static void parseListener(FacesContext facesContext,
			UIComponent component, IListenerType listenerType,
			String expression, IMethodExpressionCreator methodExpressionCreator) {
		parseListener(facesContext, component, listenerType, expression, false,
				methodExpressionCreator);
	}

	public static void parseListener(FacesContext facesContext,
			UIComponent component, IListenerType listenerType,
			String expression, boolean defaultAction,
			IMethodExpressionCreator methodExpressionCreator) {
		expression = expression.trim();
		if (expression.length() < 1) {
			return;
		}

		/*
		 * if (defaultAction && (component instanceof UICommand)) { UICommand
		 * command = (UICommand) component;
		 * 
		 * MethodBinding vb; if (isValueReference(expression)) { vb =
		 * application.createMethodBinding(expression, null); } else { vb = new
		 * ForwardMethodBinding(expression); }
		 * 
		 * command.setActionListener(vb); return; }
		 */
		String scriptType = getScriptType(facesContext);

		char chs[] = expression.toCharArray();
		int par = 0;
		int acco = 0;
		int brakets = 0;
		int lastStart = 0;
		int offset;
		nextChar: for (offset = 0; offset < chs.length; offset++) {
			char c = chs[offset];

			if (c == '\"' || c == '\'') {
				for (offset++; offset < chs.length; offset++) {
					char c2 = chs[offset];

					if (c == c2) {
						continue nextChar;
					}

					if (c2 == '\\' && offset + 1 < chs.length) {
						offset++;
					}
				}

				// Mauvaise syntaxe
				throw new FacesException(
						"Syntax error on javascript expression='" + expression
								+ "': quote or double-quote are not balanced.");
			}
			if (c == '(') {
				par++;
				continue;
			}
			if (c == ')') {
				if (par < 1) {
					throw new FacesException(
							"Syntax error on javascript expression='"
									+ expression
									+ "': parentheses are not balanced");
				}
				par--;
				continue;
			}
			if (c == '{') {
				acco++;
				continue;
			}
			if (c == '}') {
				if (acco < 1) {
					throw new FacesException(
							"Syntax error on javascript expression='"
									+ expression
									+ "': braces are not balanced.");
				}
				acco--;
				continue;
			}
			if (c == '[') {
				brakets++;
				continue;
			}
			if (c == ']') {
				if (brakets < 1) {
					throw new FacesException(
							"Syntax error on javascript expression='"
									+ expression
									+ "': brackets are not balanced.");
				}
				brakets--;
				continue;
			}
			if (c != ';') {
				continue;
			}

			if (brakets > 0 || acco > 0 || par > 0) {
				continue;
			}

			parseFunction(chs, lastStart, offset - 1, expression, facesContext,
					component, listenerType, scriptType,
					methodExpressionCreator);
			lastStart = offset + 1;
		}

		if (lastStart < offset) {
			parseFunction(chs, lastStart, offset - 1, expression, facesContext,
					component, listenerType, scriptType,
					methodExpressionCreator);
		}
	}

	private static void parseFunction(char[] chs, int start, int end,
			String listeners, FacesContext facesContext, UIComponent component,
			IListenerType listenerType, String scriptType,
			IMethodExpressionCreator methodExpressionCreator) {
		for (; start < end; start++) {
			char c = chs[start];
			if (Character.isWhitespace(c) == false) {
				break;
			}
		}

		for (; end > start; end--) {
			char c = chs[end];
			if (Character.isWhitespace(c) == false) {
				break;
			}
		}

		if (start >= end) {
			// Que du blanc !
			return;
		}

		String s = listeners.substring(start, end + 1);

		if (start + 4 < end) {
			boolean partialRendering = false;
			String actionExpression = s;

			if (s.startsWith(PARTIAL_RENDERING_PREFIX)) {
				partialRendering = true;
				actionExpression = s.substring(PARTIAL_RENDERING_PREFIX
						.length());
			}

			if (BindingTools.isBindingExpression(actionExpression)
					|| isForwardReference(actionExpression)) {
				// Value reference ? ajouter !
				if (LOG.isDebugEnabled()) {
					LOG.debug("Add server listener to component '"
							+ component.getId() + "' : " + s);
				}

				if (component instanceof IComponentLifeCycle) {
					IComponentLifeCycle componentLifeCycle = (IComponentLifeCycle) component;

					if (componentLifeCycle.confirmListenerAppend(facesContext,
							listenerType.getListenerClass()) == false) {
						return;
					}
				}

				IServerActionListener serverActionListener = listenerType
						.addActionListener(component,
								facesContext.getApplication(),
								actionExpression, partialRendering);

				if (serverActionListener != null
						&& methodExpressionCreator != null) {

					serverActionListener.createMethodExpression(facesContext,
							methodExpressionCreator);
				}

				return;
			}
		}

		if (LOG.isDebugEnabled()) {
			LOG.debug("Add script listener (type=" + scriptType
					+ ") to component '" + component.getId() + "' : " + s);
		}
		if (component instanceof IComponentLifeCycle) {
			IComponentLifeCycle componentLifeCycle = (IComponentLifeCycle) component;

			if (componentLifeCycle.confirmListenerAppend(facesContext,
					listenerType.getListenerClass())) {
				listenerType.addScriptListener(component, scriptType, s);
			}
		} else {
			listenerType.addScriptListener(component, scriptType, s);
		}

	}

	protected static boolean isForwardReference(String s) {
		if (s.startsWith("#[") == false) {
			return false;
		}

		if (s.endsWith("]") == false) {
			return false;
		}

		return true;
	}

	public static final String getScriptType(FacesContext facesContext) {
		return PageConfiguration.getScriptType(facesContext);
	}

	public interface IMethodExpressionCreator {
        MethodExpression create(String expression, Class< ? >[] paramTypes);
	}
}
