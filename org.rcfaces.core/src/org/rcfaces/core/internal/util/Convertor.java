/*
 * $Id: Convertor.java,v 1.3 2013/07/03 12:25:05 jbmeslin Exp $
 * 
 */

package org.rcfaces.core.internal.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.faces.FacesException;
import javax.faces.context.FacesContext;

import org.rcfaces.core.internal.RcfacesContext;
import org.rcfaces.core.internal.adapter.IAdapterManager;
import org.rcfaces.core.internal.converter.LocaleConverter;
import org.rcfaces.core.internal.converter.TimeZoneConverter;
import org.rcfaces.core.lang.IAdaptable;

/**
 * Classe de conversion de certains types/objets java en d'autres ...
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/07/03 12:25:05 $
 */
public final class Convertor {
    

    // private static final DateFormat dateFormat = new
    // SimpleDateFormat("dd/MM/yyyy", Locale.FRENCH);

    private static boolean logConvertor = false;

    private static void log(String message, Throwable th) {
        FacesContext facesContext = FacesContext.getCurrentInstance();

        facesContext.getExternalContext().log(message, th);
    }

    public static Object convert(Object data, Class< ? > classRequested)
            throws FacesException {

        if (classRequested == null) {
            return data;
        }
        if (data == null) {
            if (classRequested.isPrimitive() == false) {
                return null;
            }

            ClassConvertor cv = (ClassConvertor) classConvertors
                    .get(classRequested);
            if (cv != null) {
                return cv.getNullValue();
            }

            throw new FacesException("Can not convert primitive class '"
                    + classRequested.getName() + "' (value=null) to class '"
                    + classRequested.getName() + "'.", null);
        }

        Class< ? > cl = data.getClass();
        if (cl == classRequested || classRequested.isAssignableFrom(cl)) {
            return data;
        }

        if (cl.equals(String.class)) {
            Callback Callback = (Callback) callbacksFromString
                    .get(classRequested);
            if (Callback != null) {
                return Callback.convert(data);
            }
        }

        ClassConvertor cv = (ClassConvertor) classConvertors
                .get(classRequested);
        if (cv != null) {
            Object ret = cv.convert(cl, data);

            if (ret != null) {
                return ret;
            }
        }

        if (data instanceof IAdaptable) {
            Object ret = ((IAdaptable) data).getAdapter(classRequested, null);
            if (ret != null) {
                return ret;
            }
        }

        IAdapterManager adapterManager = RcfacesContext.getCurrentInstance()
                .getAdapterManager();

        if (adapterManager != null) {
            Object ret = adapterManager.getAdapter(data, classRequested, null);
            if (ret != null) {
                return ret;
            }
        }

        throw new FacesException("Can not convert class '" + cl.getName()
                + "' to class '" + classRequested.getName() + "' value='"
                + data + "'", null);
    }

    private interface ClassConvertor {
        Object convert(Class< ? > cl, Object data);

        Object getNullValue();
    }

    private interface Callback {
        public Object convert(Object toConvert);
    }

    private static final ClassConvertor TO_INTEGER = new ClassConvertor() {
        

        private final Object DEFAULT_INTEGER = new Integer(0);

        public Object convert(Class cl, Object data) {
            if (cl.equals(Integer.class) || cl.equals(Integer.TYPE)) {
                return data;
            }

            if (cl.isAssignableFrom(Number.class)) {
                return new Integer(((Number) data).intValue());
            }

            return null;
        }

        public Object getNullValue() {
            return DEFAULT_INTEGER;
        }
    };

    private static final ClassConvertor TO_LONG = new ClassConvertor() {
        

        private final Object DEFAULT_LONG = new Long(0);

        public Object convert(Class cl, Object data) {
            if (cl.equals(Long.class) || cl.equals(Long.TYPE)) {
                return data;
            }

            if (cl.isAssignableFrom(Number.class)) {
                return new Long(((Number) data).longValue());
            }

            return null;
        }

        public Object getNullValue() {
            return DEFAULT_LONG;
        }
    };

    private static final ClassConvertor TO_DOUBLE = new ClassConvertor() {
        

        private final Object DEFAULT_DOUBLE = new Double(0);

        public Object convert(Class cl, Object data) {
            if (cl.equals(Double.class) || cl.equals(Double.TYPE)) {
                return data;
            }

            if (cl.isAssignableFrom(Number.class)) {
                return new Double(((Number) data).doubleValue());
            }

            return null;
        }

        public Object getNullValue() {
            return DEFAULT_DOUBLE;
        }
    };

    private static final ClassConvertor TO_FLOAT = new ClassConvertor() {
        

        private final Object DEFAULT_FLOAT = new Float(0);

        public Object convert(Class cl, Object data) {
            if (cl.equals(Float.class) || cl.equals(Float.TYPE)) {
                return data;
            }

            if (cl.isAssignableFrom(Number.class)) {
                return new Float(((Number) data).floatValue());
            }

            return null;
        }

        public Object getNullValue() {
            return DEFAULT_FLOAT;
        }
    };

    private static final ClassConvertor TO_SHORT = new ClassConvertor() {
        

        private final Object DEFAULT_SHORT = new Short((short) 0);

        public Object convert(Class cl, Object data) {
            if (cl.equals(Short.class) || cl.equals(Short.TYPE)) {
                return data;
            }

            if (cl.isAssignableFrom(Number.class)) {
                return new Short(((Number) data).shortValue());
            }

            return null;
        }

        public Object getNullValue() {
            return DEFAULT_SHORT;
        }
    };

    private static final ClassConvertor TO_BYTE = new ClassConvertor() {
        

        private final Object DEFAULT_BYTE = new Byte((byte) 0);

        public Object convert(Class cl, Object data) {
            if (cl.equals(Byte.class) || cl.equals(Byte.TYPE)) {
                return data;
            }

            if (cl.isAssignableFrom(Number.class)) {
                return new Short(((Number) data).byteValue());
            }

            return null;
        }

        public Object getNullValue() {
            return DEFAULT_BYTE;
        }
    };

    private static final ClassConvertor TO_BOOLEAN = new ClassConvertor() {
        

        private final Object DEFAULT_BOOLEAN = Boolean.FALSE;

        public Object convert(Class cl, Object data) {
            if (cl.equals(Boolean.class) || cl.equals(Boolean.TYPE)) {
                return data;
            }

            if (cl.isAssignableFrom(Number.class)) {
                if (((Number) data).intValue() == 0) {
                    return Boolean.FALSE;
                }

                return Boolean.TRUE;
            }

            return null;
        }

        public Object getNullValue() {
            return DEFAULT_BOOLEAN;
        }
    };

    private static final ClassConvertor TO_STRING = new ClassConvertor() {
        

        public Object convert(Class cl, Object data) {
            Callback Callback = (Callback) callbacksToString.get(cl);
            if (Callback != null) {
                return Callback.convert(data);
            }

            return String.valueOf(data);
        }

        public Object getNullValue() {
            return null;
        }
    };

    private static final ClassConvertor TO_LIST = new ClassConvertor() {
        

        public Object convert(Class cl, Object data) {

            if (cl.isArray()) {
                return Arrays.asList((Object[]) data);
            }

            return null;
        }

        public Object getNullValue() {
            return null;
        }
    };

    private static final Callback String_to_integer = new Callback() {
        

        public Object convert(Object integer) {
            return new Integer((String) integer);
        }
    };

    private static final Callback String_to_short = new Callback() {
        

        public Object convert(Object integer) {
            return new Short((String) integer);
        }
    };

    private static final Callback String_to_long = new Callback() {
        

        public Object convert(Object integer) {
            return new Long((String) integer);
        }
    };

    private static final Callback String_to_float = new Callback() {
        

        public Object convert(Object integer) {
            return new Float((String) integer);
        }
    };

    private static final Callback String_to_double = new Callback() {
        

        public Object convert(Object integer) {
            return new Double((String) integer);
        }
    };

    private static final Callback String_to_byte = new Callback() {
        

        public Object convert(Object integer) {
            return new Byte((String) integer);
        }
    };

    private static final Callback String_to_boolean = new Callback() {
        

        public Object convert(Object bool) {
            return Boolean.valueOf((String) bool);
        }
    };

    private static final Callback String_to_Date = new Callback() {
        

        public Object convert(Object toConvert) {
            String d = ((String) toConvert).trim();
            if (d.length() < 1) {
                return null;
            }

            DateFormat dateFormat = DateFormat
                    .getDateInstance(DateFormat.SHORT);

            try {
                toConvert = dateFormat.parse((String) toConvert);
                if (logConvertor) {
                    log("Camelia.Convertor:CONVERT '" + d + "' => " + toConvert,
                            null);
                }

                return toConvert;
            } catch (ParseException e) {
                log("Camelia.Convertor:Error parsing date '" + d + "'", e);
                return null;
            }
        }
    };

    private static final Callback String_to_Locale = new Callback() {
        

        public Object convert(Object toConvert) {
            String d = ((String) toConvert).trim();
            if (d.length() < 1) {
                return null;
            }

            return LocaleConverter.SINGLETON.getAsObject(null, null,
                    (String) toConvert);
        }
    };

    private static final Callback String_to_TimeZone = new Callback() {
        

        public Object convert(Object toConvert) {
            String d = ((String) toConvert).trim();
            if (d.length() < 1) {
                return null;
            }

            return TimeZoneConverter.SINGLETON.getAsObject(null, null,
                    (String) toConvert);
        }
    };

    private static final Callback integer_to_String = new Callback() {
        

        public Object convert(Object integer) {
            return String.valueOf(integer);
        }
    };

    private static final Callback short_to_String = new Callback() {
        

        public Object convert(Object integer) {
            return String.valueOf(integer);
        }
    };

    private static final Callback long_to_String = new Callback() {
        

        public Object convert(Object integer) {
            return String.valueOf(integer);
        }
    };

    private static final Callback float_to_String = new Callback() {
        

        public Object convert(Object integer) {
            return String.valueOf(integer);
        }
    };

    private static final Callback double_to_String = new Callback() {
        

        public Object convert(Object integer) {
            return String.valueOf(integer);
        }
    };

    private static final Callback byte_to_String = new Callback() {
        

        public Object convert(Object integer) {
            return String.valueOf(integer);
        }
    };

    private static final Callback boolean_to_String = new Callback() {
        

        public Object convert(Object bool) {
            return String.valueOf(bool);
        }
    };

    private static final Callback Date_to_String = new Callback() {
        

        public Object convert(Object toConvert) {
            Date d = (Date) toConvert;

            DateFormat dateFormat = DateFormat
                    .getDateInstance(DateFormat.SHORT);

            toConvert = dateFormat.format(d);
            if (logConvertor) {
                log("Convertor: CONVERT '" + d + "' => " + toConvert, null);
            }
            return toConvert;
        }
    };

    private static final Callback Locale_to_String = new Callback() {
        

        public Object convert(Object toConvert) {
            return LocaleConverter.SINGLETON.getAsString(null, null, toConvert);
        }
    };

    private static final Callback TimeZone_to_String = new Callback() {
        

        public Object convert(Object toConvert) {
            return TimeZoneConverter.SINGLETON.getAsString(null, null,
                    toConvert);
        }
    };

    private static final Map callbacksToString = new HashMap(32);

    private static final Map classConvertors = new HashMap(32);

    private static final Map callbacksFromString = new HashMap(32);

    static {
        callbacksToString.put(Short.TYPE, short_to_String);
        callbacksToString.put(Short.class, short_to_String);
        callbacksToString.put(Integer.TYPE, integer_to_String);
        callbacksToString.put(Integer.class, integer_to_String);
        callbacksToString.put(Long.TYPE, long_to_String);
        callbacksToString.put(Long.class, long_to_String);
        callbacksToString.put(Double.TYPE, double_to_String);
        callbacksToString.put(Double.class, double_to_String);
        callbacksToString.put(Float.TYPE, float_to_String);
        callbacksToString.put(Float.class, float_to_String);
        callbacksToString.put(Byte.TYPE, byte_to_String);
        callbacksToString.put(Byte.class, byte_to_String);
        callbacksToString.put(Boolean.TYPE, boolean_to_String);
        callbacksToString.put(Boolean.class, boolean_to_String);
        callbacksToString.put(java.util.Date.class, Date_to_String);
        callbacksToString.put(java.util.Locale.class, Locale_to_String);
        callbacksToString.put(java.util.TimeZone.class, TimeZone_to_String);

        callbacksFromString.put(Integer.TYPE, String_to_integer);
        callbacksFromString.put(Integer.class, String_to_integer);
        callbacksFromString.put(Short.TYPE, String_to_short);
        callbacksFromString.put(Short.class, String_to_short);
        callbacksFromString.put(Long.TYPE, String_to_long);
        callbacksFromString.put(Long.class, String_to_long);
        callbacksFromString.put(Double.TYPE, String_to_double);
        callbacksFromString.put(Double.class, String_to_double);
        callbacksFromString.put(Float.TYPE, String_to_float);
        callbacksFromString.put(Float.class, String_to_float);
        callbacksFromString.put(Byte.TYPE, String_to_byte);
        callbacksFromString.put(Byte.class, String_to_byte);
        callbacksFromString.put(Boolean.TYPE, String_to_boolean);
        callbacksFromString.put(Boolean.class, String_to_boolean);
        callbacksFromString.put(java.util.Date.class, String_to_Date);
        callbacksFromString.put(java.util.Locale.class, String_to_Locale);
        callbacksFromString.put(java.util.TimeZone.class, String_to_TimeZone);

        classConvertors.put(Double.class, TO_DOUBLE);
        classConvertors.put(Double.TYPE, TO_DOUBLE);

        classConvertors.put(Float.class, TO_FLOAT);
        classConvertors.put(Float.TYPE, TO_FLOAT);

        classConvertors.put(Long.class, TO_LONG);
        classConvertors.put(Long.TYPE, TO_LONG);

        classConvertors.put(Integer.class, TO_INTEGER);
        classConvertors.put(Integer.TYPE, TO_INTEGER);

        classConvertors.put(Short.class, TO_SHORT);
        classConvertors.put(Short.TYPE, TO_SHORT);

        classConvertors.put(Byte.class, TO_BYTE);
        classConvertors.put(Byte.TYPE, TO_BYTE);

        classConvertors.put(Boolean.class, TO_BOOLEAN);
        classConvertors.put(Boolean.TYPE, TO_BOOLEAN);

        classConvertors.put(String.class, TO_STRING);

        classConvertors.put(java.util.List.class, TO_LIST);
    }
}
