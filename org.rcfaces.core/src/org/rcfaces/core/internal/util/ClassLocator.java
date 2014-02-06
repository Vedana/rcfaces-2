/*
 * $Id: ClassLocator.java,v 1.3 2013/11/13 12:53:22 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.internal.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.faces.FacesException;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:22 $
 */
public class ClassLocator {
    private static final Log LOG = LogFactory.getLog(ClassLocator.class);

    @SuppressWarnings("unchecked")
    public static final <T> Class< ? extends T> load(String className,
            Object fallback, Object context, Class<T> clazz)
            throws ClassNotFoundException {

        Class< ? > clz = load(className, fallback, context);

        if (clazz.isAssignableFrom(clz) == false) {
            throw new FacesException("Specified Class '" + className
                    + "' specified must implement '" + clazz.getName() + "'.");
        }

        return (Class<T>) clz;
    }

    public static final Class< ? > load(String className, Object fallback,
            Object context) throws ClassNotFoundException {

        ClassNotFoundException thOrigin = null;
        RuntimeException rtOrigin = null;

        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if (cl != null) {
            try {
                return cl.loadClass(className);

            } catch (ClassNotFoundException ex) {
                if (thOrigin == null) {
                    thOrigin = ex;
                }

                if (LOG.isDebugEnabled()) {
                    LOG.debug("Find class '" + className + "'", ex);
                }

            } catch (RuntimeException ex) {
                if (rtOrigin == null) {
                    rtOrigin = ex;
                }

                if (LOG.isDebugEnabled()) {
                    LOG.debug("Find class '" + className + "'", ex);
                }
            }
        }

        if (fallback != null) {
            Class< ? > cls;
            if (fallback instanceof Class) {
                cls = (Class< ? >) fallback;
            } else {
                cls = fallback.getClass();
            }

            try {
                return cls.getClassLoader().loadClass(className);

            } catch (ClassNotFoundException ex) {
                if (thOrigin == null) {
                    thOrigin = ex;
                }

                if (LOG.isDebugEnabled()) {
                    LOG.debug("Find class '" + className + "' fallbackClass="
                            + cls, ex);
                }

            } catch (RuntimeException ex) {
                if (rtOrigin == null) {
                    rtOrigin = ex;
                }

                if (LOG.isDebugEnabled()) {
                    LOG.debug("Find class '" + className + "' fallbackClass="
                            + cls, ex);
                }
            }
        }

        try {
            return ClassLocator.class.getClassLoader().loadClass(className);

        } catch (ClassNotFoundException ex) {
            if (thOrigin == null) {
                thOrigin = ex;
            }

            if (LOG.isDebugEnabled()) {
                LOG.debug("Find class '" + className + "' (classLocator)", ex);
            }

        } catch (RuntimeException ex) {
            if (rtOrigin == null) {
                rtOrigin = ex;
            }

            if (LOG.isDebugEnabled()) {
                LOG.debug("Find class '" + className + "' (classLocator)", ex);
            }
        }

        if (context instanceof FacesContext) {
            context = ((FacesContext) context).getExternalContext()
                    .getContext();
        }

        if (context instanceof ServletContext) {
            try {
                context = ((ServletContext) context).getClass()
                        .getClassLoader();

            } catch (RuntimeException ex) {
                LOG.debug("Can not get classLoader of servletContext.", ex);
            }
        }

        if (context instanceof ClassLoader) {
            try {
                return ((ClassLoader) context).loadClass(className);

            } catch (ClassNotFoundException ex) {
                if (thOrigin == null) {
                    thOrigin = ex;
                }

                if (LOG.isDebugEnabled()) {
                    LOG.debug("Find class '" + className + "' (context="
                            + context + ")", ex);
                }

            } catch (RuntimeException ex) {
                if (rtOrigin == null) {
                    rtOrigin = ex;
                }

                if (LOG.isDebugEnabled()) {
                    LOG.debug("Find class '" + className + "' (context="
                            + context + ")", ex);
                }
            }
        }

        if (thOrigin == null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Can not find class '" + className + "'.");
            }

            thOrigin = new ClassNotFoundException("Can not find class '"
                    + className + "'.", rtOrigin);
        }

        throw thOrigin;
    }

    public static final URL getResource(String resourceLocation,
            Object fallback, Object context) {

        IOException thOrigin[] = new IOException[1];

        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if (cl != null) {
            URL url = null;
            try {
                url = cl.getResource(resourceLocation);

            } catch (RuntimeException ex) {
                LOG.debug("Resource not found '" + resourceLocation + "'.", ex);
            }

            if (url != null && testURL(url, thOrigin)) {
                return url;
            }
        }

        if (fallback != null) {
            try {
                URL url = fallback.getClass().getClassLoader()
                        .getResource(resourceLocation);
                if (url != null && testURL(url, thOrigin)) {
                    return url;
                }

            } catch (RuntimeException ex) {
                LOG.debug("Resource not found '" + resourceLocation + "'.", ex);
            }
        }

        try {
            URL url = ClassLocator.class.getClassLoader().getResource(
                    resourceLocation);

            if (url != null && testURL(url, thOrigin)) {
                return url;
            }

        } catch (RuntimeException ex) {
            LOG.debug("Resource not found '" + resourceLocation + "'.", ex);
        }

        if (context instanceof FacesContext) {
            context = ((FacesContext) context).getExternalContext()
                    .getContext();
        }

        if (context instanceof ServletContext) {
            try {
                URL url = ((ServletContext) context)
                        .getResource(resourceLocation);
                if (url != null && testURL(url, thOrigin)) {
                    return url;
                }

            } catch (MalformedURLException ex) {
                LOG.debug("Malformed URL for '" + resourceLocation + "'.", ex);

            } catch (RuntimeException ex) {
                LOG.debug("Resource not found '" + resourceLocation + "'.", ex);
            }

            try {
                context = ((ServletContext) context).getClass()
                        .getClassLoader();

            } catch (RuntimeException ex) {
                LOG.debug("Can not get classLoader of ServletContext", ex);
            }
        }

        if (context instanceof ClassLoader) {
            try {
                URL url = ((ClassLoader) context).getResource(resourceLocation);

                if (url != null && testURL(url, thOrigin)) {
                    return url;
                }

            } catch (RuntimeException ex) {
                LOG.debug("Resource not found '" + resourceLocation + "'.", ex);
            }
        }

        if (thOrigin[0] == null) {
            thOrigin[0] = new IOException("Can not find resource '"
                    + resourceLocation + "'.");
        }

        return null;
    }

    protected static boolean testURL(URL url, IOException exs[]) {
        if (url == null) {
            return false;
        }
        try {
            InputStream ins = url.openStream();

            ins.close();

            return true;

        } catch (IOException ex) {
            if (exs != null && exs[0] == null) {
                exs[0] = ex;
            }
        }

        return false;
    }

    public static ClassLoader getCurrentLoader(Object fallbackClass) {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        if (loader != null) {
            return loader;
        }

        return fallbackClass.getClass().getClassLoader();
    }
}
