/*
 * $Id: CameliaComponentHandler.java,v 1.1 2014/02/05 16:05:53 jbmeslin Exp $
 */
package org.rcfaces.core.internal.facelets;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.el.ExpressionFactory;
import javax.el.MethodExpression;
import javax.faces.component.UIComponent;
import javax.faces.view.facelets.ComponentConfig;
import javax.faces.view.facelets.FaceletContext;
import javax.faces.view.facelets.FaceletException;
import javax.faces.view.facelets.MetaRule;
import javax.faces.view.facelets.MetaRuleset;
import javax.faces.view.facelets.Metadata;
import javax.faces.view.facelets.MetadataTarget;
import javax.faces.view.facelets.TagAttribute;
import javax.faces.view.facelets.TextHandler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.component.AcceleratorComponent;
import org.rcfaces.core.internal.lang.StringAppender;
import org.rcfaces.core.internal.tools.ListenersTools;
import org.rcfaces.core.internal.tools.ListenersTools1_2;
import org.rcfaces.core.internal.tools.ListenersTools.IListenerType;

import com.sun.faces.facelets.el.TagMethodExpression;
import com.sun.faces.facelets.tag.TagHandlerImpl;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.1 $ $Date: 2014/02/05 16:05:53 $
 */
public class CameliaComponentHandler extends CameliaComponentHandler0 {
    private static final Log LOG = LogFactory
            .getLog(CameliaComponentHandler.class);

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.1 $ $Date: 2014/02/05 16:05:53 $
     */
    private static interface IAttributeMetaData {
        Metadata processAttribute(String expression,
                IListenerType defaultListenerType, String attributeName,
                TagAttribute tagAttribute);
    }
    

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.1 $ $Date: 2014/02/05 16:05:53 $
     */
    private static class ListenerAttributeMetaData implements
            IAttributeMetaData {
        

        private final IListenerType listenerType;

        private ListenerAttributeMetaData(IListenerType listenerType) {
            this.listenerType = listenerType;
        }

        public Metadata processAttribute(final String expression,
                final IListenerType defaultListenerType, String attributeName,
                final TagAttribute tagAttribute) {
            Metadata metadata = new Metadata() {
                @Override
                public void applyMetadata(final FaceletContext ctx,
                        Object instance) {
                    ListenersTools.parseListener(ctx.getFacesContext(),
                            (UIComponent) instance, listenerType, expression,
                            listenerType == defaultListenerType,
                            new ListenersTools.IMethodExpressionCreator() {

                                public MethodExpression create(
                                        String expression,
                                        Class< ? >[] paramTypes) {

                                    ExpressionFactory expressionFactory = ctx
                                            .getExpressionFactory();

                                    MethodExpression methodExpression = expressionFactory
                                            .createMethodExpression(ctx,
                                                    expression, null,
                                                    paramTypes);

                                    return new TagMethodExpression(
                                            tagAttribute, methodExpression);
                                }
                            });
                }
            };

            return metadata;
        }
    }
    
    private static class ActionListenerAttributeMetaData implements
    IAttributeMetaData {
	
	private ActionListenerAttributeMetaData() {
	}
	
	static HashMap<Class<? extends UIComponent>, IListenerType> componentsListener = new HashMap<Class<? extends UIComponent>, ListenersTools.IListenerType>();
	static {
		componentsListener.put(AcceleratorComponent.class, ListenersTools.KEY_PRESS_LISTENER_TYPE);
	}
	
	protected IListenerType getComponentsListener(Class<?> clazz){
		
		IListenerType listener = null;
		
		 while (listener == null && clazz != Object.class) {
			 listener = componentsListener.get(clazz);
			 clazz =  clazz.getSuperclass();
		 }
		
		if (listener == null) {
			listener = ListenersTools.SELECTION_LISTENER_TYPE;
		}
		 return listener;
	}
	
	
	public Metadata processAttribute(final String expression,
	        final IListenerType defaultListenerType, String attributeName,
	        final TagAttribute tagAttribute) {
		
		
	    Metadata metadata = new Metadata() {
	        @Override
	        public void applyMetadata(final FaceletContext ctx,
	                Object instance) {
	        	
	            ListenersTools.parseListener(ctx.getFacesContext(),
	                    (UIComponent) instance, getComponentsListener((Class<? extends UIComponent>) instance.getClass()), expression,
	                    
	                    new ListenersTools.IMethodExpressionCreator() {
	
	                        public MethodExpression create(
	                                String expression,
	                                Class< ? >[] paramTypes) {
	
	                            ExpressionFactory expressionFactory = ctx
	                                    .getExpressionFactory();
	
	                            MethodExpression methodExpression = expressionFactory
	                                    .createMethodExpression(ctx,
	                                            expression, null,
	                                            paramTypes);
	
	                            return new TagMethodExpression(
	                                    tagAttribute, methodExpression);
	                        }
	                    });
	        }
	    };
	
	    return metadata;
	}
}
    
    

    private static final Class< ? >[] ENUMERATION_PARAMETERS = new Class[] { String.class };

    private static IAttributeMetaData ENUMERATION_ATTRIBUTE_METADATA = new IAttributeMetaData() {
        

        public Metadata processAttribute(final String expression,
                IListenerType defaultListenerType, final String attributeName,
                TagAttribute tagAttribute) {

            Metadata metadata = new Metadata() {
                @Override
                public void applyMetadata(FaceletContext ctx, Object instance) {
                    try {
                        String setterMethodName = "set"
                                + Character
                                        .toUpperCase(attributeName.charAt(0))
                                + attributeName.substring(1);

                        Method method = instance.getClass().getMethod(
                                setterMethodName, ENUMERATION_PARAMETERS);

                        method.invoke(instance, new Object[] { expression });

                    } catch (Throwable th) {
                        throw new FaceletException(
                                "Can not set the value of attribute '"
                                        + attributeName
                                        + "'. (Use String parameter setter)",
                                th);
                    }
                }

            };

            return metadata;
        }
    };
    private static final Class[] OJBECT_PARAMETERS = new Class[] { Object.class };

    private static IAttributeMetaData OJBECT_ATTRIBUTE_METADATA = new IAttributeMetaData() {
        

        public Metadata processAttribute(final String expression,
                IListenerType defaultListenerType, final String attributeName,
                TagAttribute tagAttribute) {

            Metadata metadata = new Metadata() {
                @Override
                public void applyMetadata(FaceletContext ctx, Object instance) {
                    try {
                        String setterMethodName = "set"
                                + Character
                                        .toUpperCase(attributeName.charAt(0))
                                + attributeName.substring(1);

                        Method method = instance.getClass().getMethod(
                                setterMethodName, OJBECT_PARAMETERS);

                        method.invoke(instance, new Object[] { expression });

                    } catch (Throwable th) {
                        throw new FaceletException(
                                "Can not set the value of attribute '"
                                        + attributeName
                                        + "'. (Use String parameter setter)",
                                th);
                    }
                }

            };

            return metadata;
        }
    };

    private static final Map<String, IAttributeMetaData> ATTRIBUTES_METADATA = new HashMap<String, IAttributeMetaData>(
            32);
    static {
        ATTRIBUTES_METADATA.put("blurListener", new ListenerAttributeMetaData(
                ListenersTools.BLUR_LISTENER_TYPE));
        ATTRIBUTES_METADATA.put("checkListener", new ListenerAttributeMetaData(
                ListenersTools.CHECK_LISTENER_TYPE));
        ATTRIBUTES_METADATA.put("closeListener", new ListenerAttributeMetaData(
                ListenersTools.CLOSE_LISTENER_TYPE));
        ATTRIBUTES_METADATA.put("doubleClickListener",
                new ListenerAttributeMetaData(
                        ListenersTools.DOUBLE_CLICK_LISTENER_TYPE));
        ATTRIBUTES_METADATA.put("focusListener", new ListenerAttributeMetaData(
                ListenersTools.FOCUS_LISTENER_TYPE));
        ATTRIBUTES_METADATA.put("initListener", new ListenerAttributeMetaData(
                ListenersTools.INIT_LISTENER_TYPE));
        ATTRIBUTES_METADATA.put("keyDownListener",
                new ListenerAttributeMetaData(
                        ListenersTools.KEY_DOWN_LISTENER_TYPE));
        ATTRIBUTES_METADATA.put("keyPressListener",
                new ListenerAttributeMetaData(
                        ListenersTools.KEY_PRESS_LISTENER_TYPE));
        ATTRIBUTES_METADATA.put("keyUpListener", new ListenerAttributeMetaData(
                ListenersTools.KEY_UP_LISTENER_TYPE));
        ATTRIBUTES_METADATA.put("loadListener", new ListenerAttributeMetaData(
                ListenersTools.LOAD_LISTENER_TYPE));
        ATTRIBUTES_METADATA.put("errorListener", new ListenerAttributeMetaData(
                ListenersTools.ERROR_LISTENER_TYPE));
        ATTRIBUTES_METADATA.put("validationListener",
                new ListenerAttributeMetaData(
                        ListenersTools.VALIDATION_LISTENER_TYPE));
        ATTRIBUTES_METADATA.put("menuListener", new ListenerAttributeMetaData(
                ListenersTools.MENU_LISTENER_TYPE));
        ATTRIBUTES_METADATA.put("mouseOutListener",
                new ListenerAttributeMetaData(
                        ListenersTools.MOUSE_OUT_LISTENER_TYPE));
        ATTRIBUTES_METADATA.put("mouseOverListener",
                new ListenerAttributeMetaData(
                        ListenersTools.MOUSE_OVER_LISTENER_TYPE));
        ATTRIBUTES_METADATA.put("propertyChangeListener",
                new ListenerAttributeMetaData(
                        ListenersTools.PROPERTY_CHANGE_LISTENER_TYPE));
        ATTRIBUTES_METADATA.put("resetListener", new ListenerAttributeMetaData(
                ListenersTools.RESET_LISTENER_TYPE));
        ATTRIBUTES_METADATA.put("selectionListener",
                new ListenerAttributeMetaData(
                        ListenersTools.SELECTION_LISTENER_TYPE));
        ATTRIBUTES_METADATA.put("actionListener",
                new ActionListenerAttributeMetaData());
        ATTRIBUTES_METADATA.put("serviceEventListener",
                new ListenerAttributeMetaData(
                        ListenersTools.SERVICE_EVENT_LISTENER_TYPE));
        ATTRIBUTES_METADATA.put("sortListener", new ListenerAttributeMetaData(
                ListenersTools.SORT_LISTENER_TYPE));
        ATTRIBUTES_METADATA.put("suggestionListener",
                new ListenerAttributeMetaData(
                        ListenersTools.SUGGESTION_LISTENER_TYPE));
        ATTRIBUTES_METADATA.put("userEventListener",
                new ListenerAttributeMetaData(
                        ListenersTools.USER_EVENT_LISTENER_TYPE));
        ATTRIBUTES_METADATA.put("valueChangeListener",
                new ListenerAttributeMetaData(
                        ListenersTools.VALUE_CHANGE_LISTENER_TYPE));
        ATTRIBUTES_METADATA.put("expandListener",
                new ListenerAttributeMetaData(
                        ListenersTools.EXPAND_LISTENER_TYPE));
        ATTRIBUTES_METADATA.put("preSelectionListener",
                new ListenerAttributeMetaData(
                        ListenersTools.PRE_SELECTION_LISTENER_TYPE));
        ATTRIBUTES_METADATA.put("dragListener", new ListenerAttributeMetaData(
                ListenersTools.DRAG_LISTENER_TYPE));
        ATTRIBUTES_METADATA.put("dropListener", new ListenerAttributeMetaData(
                ListenersTools.DROP_LISTENER_TYPE));
        ATTRIBUTES_METADATA.put("dropCompleteListener",
                new ListenerAttributeMetaData(
                        ListenersTools.DROP_COMPLETE_LISTENER_TYPE));

        ATTRIBUTES_METADATA.put("action", new IAttributeMetaData() {
            

            public Metadata processAttribute(final String expression,
                    final IListenerType defaultListenerType,
                    String attributeName, final TagAttribute tagAttribute) {
                return new Metadata() {
                    @Override
                    public void applyMetadata(FaceletContext ctx,
                            Object instance) {

                        actionApplyMetaData(ctx, (UIComponent) instance,
                                expression, defaultListenerType, tagAttribute);
                    }
                };
            }
        });

        ATTRIBUTES_METADATA.put("hiddenMode", ENUMERATION_ATTRIBUTE_METADATA);
        ATTRIBUTES_METADATA.put("asyncRenderMode",
                ENUMERATION_ATTRIBUTE_METADATA);
        ATTRIBUTES_METADATA.put("checkCardinality",
                ENUMERATION_ATTRIBUTE_METADATA);
        ATTRIBUTES_METADATA.put("selectionCardinality",
                ENUMERATION_ATTRIBUTE_METADATA);
        ATTRIBUTES_METADATA.put("textPosition", ENUMERATION_ATTRIBUTE_METADATA);
        ATTRIBUTES_METADATA.put("order", ENUMERATION_ATTRIBUTE_METADATA);
        ATTRIBUTES_METADATA.put("mode", ENUMERATION_ATTRIBUTE_METADATA);
        ATTRIBUTES_METADATA
                .put("literalLocale", ENUMERATION_ATTRIBUTE_METADATA);
        ATTRIBUTES_METADATA.put("literalTimeZone",
                ENUMERATION_ATTRIBUTE_METADATA);
        ATTRIBUTES_METADATA.put("componentLocale",
                ENUMERATION_ATTRIBUTE_METADATA);
        ATTRIBUTES_METADATA.put("componentTimeZone",
                ENUMERATION_ATTRIBUTE_METADATA);
        ATTRIBUTES_METADATA.put("clientDatesStrategy",
                ENUMERATION_ATTRIBUTE_METADATA);
        ATTRIBUTES_METADATA.put("numberFormatType",
                ENUMERATION_ATTRIBUTE_METADATA);
        ATTRIBUTES_METADATA.put("disabledWeekDays",
                ENUMERATION_ATTRIBUTE_METADATA);
        ATTRIBUTES_METADATA.put("inputType", ENUMERATION_ATTRIBUTE_METADATA);

        ATTRIBUTES_METADATA.put("date", ENUMERATION_ATTRIBUTE_METADATA);
        ATTRIBUTES_METADATA.put("maxDate", ENUMERATION_ATTRIBUTE_METADATA);
        ATTRIBUTES_METADATA.put("minDate", ENUMERATION_ATTRIBUTE_METADATA);
        ATTRIBUTES_METADATA.put("defaultDate", ENUMERATION_ATTRIBUTE_METADATA);

        ATTRIBUTES_METADATA.put("time", ENUMERATION_ATTRIBUTE_METADATA);
        ATTRIBUTES_METADATA.put("maxTime", ENUMERATION_ATTRIBUTE_METADATA);
        ATTRIBUTES_METADATA.put("minTime", ENUMERATION_ATTRIBUTE_METADATA);
        ATTRIBUTES_METADATA.put("defaultTime", ENUMERATION_ATTRIBUTE_METADATA);
        
        ATTRIBUTES_METADATA.put("dragEffects", ENUMERATION_ATTRIBUTE_METADATA);
        ATTRIBUTES_METADATA.put("dropEffects", ENUMERATION_ATTRIBUTE_METADATA);
        ATTRIBUTES_METADATA.put("dragTypes", ENUMERATION_ATTRIBUTE_METADATA);
        ATTRIBUTES_METADATA.put("dropTypes", ENUMERATION_ATTRIBUTE_METADATA);
        ATTRIBUTES_METADATA.put("rowDragTypes", ENUMERATION_ATTRIBUTE_METADATA);
        ATTRIBUTES_METADATA.put("rowDropTypes", ENUMERATION_ATTRIBUTE_METADATA);
        
        //ATTRIBUTES_METADATA.put("cellStyleClass", ENUMERATION_ATTRIBUTE_METADATA);
        
        //ATTRIBUTES_METADATA.put("additionalInformationValues", OJBECT_ATTRIBUTE_METADATA);
    }

    public CameliaComponentHandler(ComponentConfig config) {
        super(config);
    }

    @Override
    protected MetaRuleset createMetaRuleset(Class type) {
        MetaRuleset metaRuleset = super.createMetaRuleset(type);

        metaRuleset.addRule(new ComponentMetaRule(getDefaultListenerType()));

        return metaRuleset;
    }

    protected IListenerType getDefaultListenerType() {
        return null;
    }

    protected final void setTextBody(FaceletContext ctx, UIComponent c) {
        StringAppender content = null;

        @SuppressWarnings("unchecked")
        Iterator<TextHandler> iter = TagHandlerImpl.findNextByType(
                this.nextHandler, TextHandler.class);

        for (; iter.hasNext();) {
            TextHandler text = iter.next();

            if (content == null) {
                content = new StringAppender();
            }
            content.append(text.getText(ctx));
        }

        if (content != null && content.length() > 0) {
            c.getAttributes().put("text", content.toString());
        }
    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.1 $ $Date: 2014/02/05 16:05:53 $
     */
    private static class ComponentMetaRule extends MetaRule {
        

        private final IListenerType defaultListenerType;

        protected ComponentMetaRule(IListenerType defaultListenerType) {
            this.defaultListenerType = defaultListenerType;
        }

        @Override
        public Metadata applyRule(String name, TagAttribute attribute,
                MetadataTarget meta) {

            if (LOG.isDebugEnabled()) {
                LOG.debug("Apply AttributeMetaData rule for attribute '" + name
                        + "', tagAttribute='" + attribute + "', meta='" + meta
                        + "', defaultListenerType='" + defaultListenerType
                        + "'.");
            }

            final String expression = attribute.getValue();
            if (expression == null) {
                return null;
            }

            IAttributeMetaData attributeMetaData = ATTRIBUTES_METADATA
                    .get(name);

            if (attributeMetaData == null) {
                if (LOG.isDebugEnabled()) {
                    if (name.endsWith("Listener")) {
                        LOG.error("No attributeMetaData for attribute '" + name
                                + "'");
                    } else {
                        LOG.debug("No attributeMetaData for attribute '" + name
                                + "'");
                    }
                }
                return null;
            }

            if (LOG.isDebugEnabled()) {
                LOG.debug("AttributeMetaData for attribute '" + name + "' => "
                        + attributeMetaData);
            }

            return attributeMetaData.processAttribute(expression,
                    defaultListenerType, name, attribute);
        }
    }
}
