/*
 * $Id: ContentKeyGenerator.java,v 1.2 2013/11/13 12:53:25 jbmeslin Exp $
 */
package org.rcfaces.core.util;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.internal.util.Base64;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/11/13 12:53:25 $
 */
public class ContentKeyGenerator {

    private static final Log LOG = LogFactory.getLog(ContentKeyGenerator.class);

    private static final String DIGESTER_NAME = "SHA-256";

    private static final String NULL_STRING = "\u0000";

    public static <T> String computeKeyBySerialization(Serializable... objects) {

        if (objects == null || objects.length == 0) {
            return "";
        }

        final MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance(DIGESTER_NAME);

        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException(ex);
        }

        ObjectOutputStream outputStream;
        try {
            outputStream = new ObjectOutputStream(new OutputStream() {

                @Override
                public void write(int b) throws IOException {
                    messageDigest.update((byte) b);
                }

                @Override
                public void write(byte[] b, int off, int len)
                        throws IOException {
                    messageDigest.update(b, off, len);
                }

            });

            outputStream.writeObject(objects);

            outputStream.close();

        } catch (IOException ex) {
            throw new RuntimeException("Can not serialize objects", ex);
        }

        String contentKey = Base64.encodeBytes(messageDigest.digest());

        return contentKey;
    }

    public static <T> String computeKey(List<T> objects) {

        if (objects == null || objects.isEmpty()) {
            return "";
        }

        MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance(DIGESTER_NAME);

        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException(ex);
        }

        Class< ? > clazz = objects.get(0).getClass();

        BeanInfo beanInfo;
        try {
            beanInfo = Introspector.getBeanInfo(clazz);

        } catch (IntrospectionException ex) {
            throw new RuntimeException(ex);
        }

        for (T t : objects) {
            digest(messageDigest, beanInfo, t);
        }

        String contentKey = Base64.encodeBytes(messageDigest.digest());

        return contentKey;
    }

    public static <T> String computeKey(T... objects) {

        if (objects == null || objects.length == 0) {
            return "";
        }

        MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance(DIGESTER_NAME);

        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException(ex);
        }

        Class< ? > clazz = objects.getClass().getComponentType();

        BeanInfo beanInfo;
        try {
            beanInfo = Introspector.getBeanInfo(clazz);

        } catch (IntrospectionException ex) {
            throw new RuntimeException(ex);
        }

        for (T t : objects) {
            digest(messageDigest, beanInfo, t);
        }

        String contentKey = Base64.encodeBytes(messageDigest.digest());

        return contentKey;
    }

    private static <T> void digest(MessageDigest digest, BeanInfo beanInfo, T t) {

        for (PropertyDescriptor pd : beanInfo.getPropertyDescriptors()) {
            if (pd.getReadMethod() == null) {
                continue;
            }

            if ("class".equals(pd.getName())) {
                continue;
            }

            Object ret;
            try {
                ret = pd.getReadMethod().invoke(t);

            } catch (Exception ex) {
                LOG.error(
                        "Can not inspect bean '" + t + "' property='"
                                + pd.getName() + "'", ex);
                continue;
            }

            String s;
            if (ret == null) {
                s = NULL_STRING;

            } else {
                s = String.valueOf(ret);
            }

            digest.update(s.getBytes());
        }
    }
}
