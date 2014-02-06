/*
 * $Id: ValuesTools.java,v 1.3 2013/07/03 12:25:07 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.internal.tools;

import java.io.Externalizable;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.application.Application;
import javax.faces.component.EditableValueHolder;
import javax.faces.component.UICommand;
import javax.faces.component.UIComponent;
import javax.faces.component.ValueHolder;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import org.rcfaces.core.internal.capability.IConvertValueHolder;
import org.rcfaces.core.internal.capability.ISubmittedExternalValue;
import org.rcfaces.core.internal.component.Properties;
import org.rcfaces.core.internal.util.Convertor;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/07/03 12:25:07 $
 */
public class ValuesTools extends CollectionTools {
    

    private static final String[] STRING_EMPTY_ARRAY = new String[0];

    public static final Object NULL_VALUE = new NullValue();

    private static final IValuesAccessor VALUES_PROVIDER_VALUES_ACCESSOR = new IValuesAccessor() {
        

        public int getCount(Object checkProvider) {
            throw new UnsupportedOperationException("Not implemented !");
        }

        public Object getFirst(Object checkProvider, Object refValues) {
            throw new UnsupportedOperationException("Not implemented !");
        }

        public Object getAdaptedValues(Object values) {
            throw new UnsupportedOperationException("Not supported !");
        }

        public void setAdaptedValues(Object value, Object values) {
            throw new UnsupportedOperationException("Not supported !");
        }

        public Object[] listValues(Object checkProvider, Object refValues) {
            return convertToObjectArray(((ValueHolder) checkProvider)
                    .getValue());
        }

        public Object getComponentValues(UIComponent component) {
            return ((ValueHolder) component).getValue();
        }

        public void setComponentValues(UIComponent component, Object values) {
            ((ValueHolder) component).setValue(values);
        }

        public Class getComponentValuesType(FacesContext facesContext,
                UIComponent component) {
            Object value = getComponentValues(component);
            if (value != null) {
                return value.getClass();
            }

            ValueExpression valueBinding = component
                    .getValueExpression(Properties.VALUE.toString());
            if (valueBinding == null) {
                return null;
            }

            return valueBinding.getType(facesContext.getELContext());
        }
    };

    public static final List valueToList(Object value) {
        if (value == null) {
            return Collections.EMPTY_LIST;
        }

        if (value instanceof Object[]) {
            Object array[] = (Object[]) value;
            if (array.length < 1) {
                return Collections.EMPTY_LIST;
            }

            return Arrays.asList(array);
        }

        if (value instanceof List) {
            return (List) value;
        }

        if (value instanceof Collection) {
            Collection col = (Collection) value;
            if (col.isEmpty()) {
                return Collections.EMPTY_LIST;
            }

            return new ArrayList(col);
        }

        return Collections.singletonList(value);
    }

    protected static final Object[] convertStringsToValues(
            FacesContext context, UIComponent component, boolean testValue,
            String values[]) throws ConverterException {

        if (values == null || values.length < 1) {
            return values;
        }

        Converter converter = null;

        if (component instanceof IConvertValueHolder) {
            converter = ((IConvertValueHolder) component).getConverter();

        } else if (component instanceof ValueHolder) {
            converter = ((ValueHolder) component).getConverter();
        }

        if (converter == null && testValue) {
            ValueExpression valueBinding = component
                    .getValueExpression("value");

            if (valueBinding == null) {
                return values;
            }

            if (valueBinding.getValue(context.getELContext()) != null) {
                // On peut avoir le type !
                Class converterType = valueBinding.getType(context
                        .getELContext());
                if (converterType == null || converterType == String.class
                        || converterType == Object.class) {
                    return values;
                }

                try {
                    Application application = context.getApplication();

                    converter = application.createConverter(converterType);

                } catch (Throwable th) {
                    throw new FacesException(
                            "Can not create converter for type '"
                                    + converterType + "'.", th);
                }
            }
        }

        if (converter != null) {
            Object converted[] = new Object[values.length];

            for (int i = 0; i < converted.length; i++) {
                converted[i] = converter.getAsObject(context, component,
                        values[i]);
            }

            return converted;
        }

        // throw new ConverterException("No converter !");

        return values;
    }

    public static Object convertStringToValue(FacesContext context,
            UIComponent component, Object value, boolean testValue) {

        if ((value instanceof String) == false) {
            return value;
        }

        Converter converter = getConverter(component);

        return convertStringToValue(context, component, converter, value,
                "value", testValue);
    }

    public static Converter getConverter(UIComponent component) {

        if (component instanceof IConvertValueHolder) {
            return ((IConvertValueHolder) component).getConverter();
        }

        if (component instanceof ValueHolder) {
            return ((ValueHolder) component).getConverter();
        }

        return null;
    }

    public static Object convertStringToValue(FacesContext context,
            UIComponent component, Converter converter, Object value,
            String attributeName, boolean testValue) {

        if ((value instanceof String) == false) {
            return value;
        }

        if (converter == null && attributeName != null && testValue) {
            ValueExpression valueBinding = component
                    .getValueExpression(attributeName);

            if (valueBinding != null) {

                // On récupère pas la valeur !
                // if (valueBinding.getValue(context) != null) {
                Class converterType = valueBinding.getType(context
                        .getELContext());
                if (converterType == null || converterType == String.class
                        || converterType == Object.class) {
                    return value;
                }

                try {
                    Application application = context.getApplication();

                    converter = application.createConverter(converterType);

                } catch (Exception e) {
                    throw new FacesException(
                            "Can not create converter for type '"
                                    + converterType + "'.", e);
                }
            }
        }
        // }

        if (converter != null) {
            return converter.getAsObject(context, component, (String) value);
        }

        return value;
        // throw new ConverterException("No converter !");
    }

    public static String[] convertValuesToString(Object values[],
            UIComponent component, FacesContext facesContext) {

        if (values instanceof String[]) {
            return (String[]) values;
        }
        if (values.length == 0) {
            return STRING_EMPTY_ARRAY;
        }

        Converter converter = null;
        if (component instanceof IConvertValueHolder) {
            converter = ((IConvertValueHolder) component).getConverter();

        } else if (component instanceof ValueHolder) {
            converter = ((ValueHolder) component).getConverter();
        }

        if (converter == null && values != null) {
            if (facesContext == null) {
                facesContext = FacesContext.getCurrentInstance();
            }

            Application application = facesContext.getApplication();

            converter = application.createConverter(values[0].getClass());
        }

        if (converter != null) {
            if (facesContext == null) {
                facesContext = FacesContext.getCurrentInstance();
            }

            String ret[] = new String[values.length];

            for (int i = 0; i < ret.length; i++) {
                ret[i] = converter.getAsString(facesContext, component,
                        values[i]);
            }

            return ret;
        }

        String ret[] = new String[values.length];

        for (int i = 0; i < ret.length; i++) {
            ret[i] = (String) Convertor.convert(values[i], String.class);
        }

        return ret;
    }

    public static String convertValueToString(Object value,
            UIComponent component, FacesContext facesContext) {

        /*
         * Bah non ! if (value instanceof String) { return (String) value; }
         */

        Converter converter = null;
        if (component instanceof IConvertValueHolder) {
            converter = ((IConvertValueHolder) component).getConverter();

        } else if (component instanceof ValueHolder) {
            converter = ((ValueHolder) component).getConverter();
        }

        return convertValueToString(value, converter, component, facesContext);
    }

    public static String convertValueToString(Object value,
            Converter converter, UIComponent component,
            FacesContext facesContext) {

        if (converter == null
                && (value != null && (value instanceof String) == false)) {
            if (facesContext == null) {
                facesContext = FacesContext.getCurrentInstance();
            }

            Application application = facesContext.getApplication();

            converter = application.createConverter(value.getClass());
        }

        if (converter != null) {
            if (facesContext == null) {
                facesContext = FacesContext.getCurrentInstance();
            }

            return converter.getAsString(facesContext, component, value);
        }

        if (value instanceof String) {
            return (String) value;
        }

        return (String) Convertor.convert(value, String.class);
    }

    public static final boolean valueToBool(ValueHolder valueHolder) {
        Boolean b = valueToBoolean(valueHolder);

        if (b == null) {
            return false;
        }

        return b.booleanValue();
    }

    public static final Boolean valueToBoolean(ValueHolder valueHolder) {
        if (valueHolder instanceof ISubmittedExternalValue) {
            ISubmittedExternalValue submittedExternalValue = (ISubmittedExternalValue) valueHolder;

            if (submittedExternalValue.isSubmittedValueSetted()) {
                Object submittedValue = ((ISubmittedExternalValue) valueHolder)
                        .getSubmittedExternalValue();

                return (Boolean) submittedValue;
            }

        } else if (valueHolder instanceof EditableValueHolder) {
            Object submittedValue = ((EditableValueHolder) valueHolder)
                    .getSubmittedValue();
            if (submittedValue != null) {
                return (Boolean) submittedValue;
            }
        }

        Object value = valueHolder.getValue();

        if (value == null) {
            return null;
        }

        if (value instanceof Boolean) {
            return (Boolean) value;
        }

        return (Boolean) Convertor.convert(value, Boolean.class);
    }

    public static final int valueToInt(ValueHolder valueHolder) {
        if (valueHolder instanceof ISubmittedExternalValue) {
            ISubmittedExternalValue submittedExternalValue = (ISubmittedExternalValue) valueHolder;

            if (submittedExternalValue.isSubmittedValueSetted()) {
                Object submittedValue = ((ISubmittedExternalValue) valueHolder)
                        .getSubmittedExternalValue();
                if (submittedValue != null) {
                    return ((Integer) submittedValue).intValue();
                }

                return 0;
            }

        } else if (valueHolder instanceof EditableValueHolder) {
            Object submittedValue = ((EditableValueHolder) valueHolder)
                    .getSubmittedValue();
            if (submittedValue != null) {
                return ((Integer) submittedValue).intValue();
            }
        }

        Object value = valueHolder.getValue();

        if (value == null) {
            return 0;
        }

        if (value instanceof Integer) {
            return ((Integer) value).intValue();
        }

        Integer i = (Integer) Convertor.convert(value, Integer.TYPE);
        if (i == null) {
            return 0;
        }

        return i.intValue();
    }

    public static String valueToString(ValueHolder valueHolder,
            FacesContext facesContext) {

        if (valueHolder instanceof ISubmittedExternalValue) {
            ISubmittedExternalValue submittedExternalValue = (ISubmittedExternalValue) valueHolder;

            if (submittedExternalValue.isSubmittedValueSetted()) {
                Object submittedValue = ((ISubmittedExternalValue) valueHolder)
                        .getSubmittedExternalValue();
                return ValuesTools.valueToString(submittedValue,
                        (UIComponent) valueHolder, facesContext);
            }

        } else if (valueHolder instanceof EditableValueHolder) {
            Object submittedValue = ((EditableValueHolder) valueHolder)
                    .getSubmittedValue();
            if (submittedValue != null) {
                return ValuesTools.valueToString(submittedValue,
                        (UIComponent) valueHolder, facesContext);
            }
        }

        return ValuesTools.valueToString(valueHolder.getValue(),
                (UIComponent) valueHolder, facesContext);
    }

    public static String valueToString(UICommand valueHolder,
            FacesContext facesContext) {
        return ValuesTools.valueToString(valueHolder.getValue(), valueHolder,
                facesContext);
    }

    public static String valueToString(Object value, UIComponent component,
            FacesContext facesContext) {

        /*
         * Bah non ! if (value instanceof String) { return (String) value; }
         */

        Converter converter = null;
        if (component instanceof IConvertValueHolder) {
            converter = ((IConvertValueHolder) component).getConverter();

        } else if (component instanceof ValueHolder) {
            converter = ((ValueHolder) component).getConverter();
        }

        return valueToString(value, converter, component, facesContext);
    }

    public static String valueToString(Object value, Converter converter,
            UIComponent component, FacesContext facesContext) {

        if (converter == null && value != null
                && (value instanceof String) == false) {
            if (facesContext == null) {
                facesContext = FacesContext.getCurrentInstance();
            }

            Application application = facesContext.getApplication();

            converter = application.createConverter(value.getClass());
        }

        if (converter != null) {
            if (facesContext == null) {
                facesContext = FacesContext.getCurrentInstance();
            }

            return converter.getAsString(facesContext, component, value);
        }

        if (value instanceof String) {
            return (String) value;
        }

        return (String) Convertor.convert(value, String.class);
    }

    public static Object getValue(UIComponent component) {
        if (component instanceof ISubmittedExternalValue) {
            ISubmittedExternalValue submittedExternalValue = (ISubmittedExternalValue) component;

            if (submittedExternalValue.isSubmittedValueSetted()) {
                return ((ISubmittedExternalValue) component)
                        .getSubmittedExternalValue();
            }

        } else if (component instanceof EditableValueHolder) {
            Object submittedValue = ((EditableValueHolder) component)
                    .getSubmittedValue();
            if (submittedValue != null) {
                return submittedValue;
            }
        }

        if (component instanceof ValueHolder) {
            return ((ValueHolder) component).getValue();
        }

        throw new FacesException("Component '" + component.getId()
                + "' does not contain value !");
    }

    public static void setValue(UIComponent component, Object value) {
        if (component instanceof ISubmittedExternalValue) {
            ISubmittedExternalValue submittedExternalValue = (ISubmittedExternalValue) component;

            submittedExternalValue.setSubmittedExternalValue(value);
            return;
        }

        if (component instanceof EditableValueHolder) {
            ((EditableValueHolder) component).setSubmittedValue(value);
            return;
        }

        if (component instanceof ValueHolder) {
            ((ValueHolder) component).setValue(value);
            return;
        }

        throw new FacesException("Component '" + component.getId()
                + "' does not support value !");
    }

    public static void setValues(FacesContext facesContext,
            UIComponent component, Set values) {

        setValues(component, VALUES_PROVIDER_VALUES_ACCESSOR, values);
    }

    public static Set valuesToSet(FacesContext facesContext,
            UIComponent component, boolean immutable) {
        return valuesToSet(component, VALUES_PROVIDER_VALUES_ACCESSOR,
                immutable);
    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.3 $ $Date: 2013/07/03 12:25:07 $
     */
    public static class NullValue implements Externalizable {
        

        private static final long serialVersionUID = -2586084278685773691L;

        public void readExternal(ObjectInput in) {
        }

        public void writeExternal(ObjectOutput out) {
        }

        @Override
        public boolean equals(Object obj) {
            return (obj instanceof NullValue);
        }

        @Override
        public int hashCode() {
            return 0;
        }

    }

}
