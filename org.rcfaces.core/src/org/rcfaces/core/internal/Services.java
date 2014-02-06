/*
 * $Id: Services.java,v 1.4 2013/11/13 12:53:26 jbmeslin Exp $
 */
package org.rcfaces.core.internal;

import java.io.CharArrayReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.Rule;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.Attributes;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.4 $ $Date: 2013/11/13 12:53:26 $
 */
public class Services {

    private static final Log LOG = LogFactory.getLog(Services.class);

    private static final Services SINGLETON = new Services();

    private static final String RESOURCE_NAME = "META-INF/rcfaces-services.xml";

    private boolean servicesLoaded = false;

    private final Map<String, Object> services = new HashMap<String, Object>(32);

    public static Services get() {
        return SINGLETON;
    }

    private Services() {
    }

    public Object getService(String id) {
        synchronized (services) {
            if (servicesLoaded == false) {
                servicesLoaded = true;

                loadServices();
            }
        }

        Object service = services.get(id);

        if (LOG.isDebugEnabled()) {
            LOG.debug("Service '" + id + "' => " + service);
        }

        return service;
    }

    private void loadServices() {
        LOG.info("Load services ...");

        ClassLoader threadClassLoader = Thread.currentThread()
                .getContextClassLoader();

        ClassLoader classLoader = Services.class.getClassLoader();

        if (threadClassLoader != null) {
            loadServices(threadClassLoader);
        }

        if (classLoader != threadClassLoader) {
            loadServices(classLoader);
        }

        LOG.info(services.size() + " service(s) loaded.");
    }

    private void loadServices(ClassLoader classLoader) {
        LOG.info("Load services using '" + classLoader + "'");

        Enumeration<URL> enumeration;
        try {
            enumeration = classLoader.getResources(RESOURCE_NAME);

        } catch (IOException ex) {
            LOG.error("Can not get Resources '" + RESOURCE_NAME + "'.", ex);
            return;
        }

        for (; enumeration.hasMoreElements();) {
            URL url = enumeration.nextElement();

            loadService(classLoader, url);
        }
    }

    private void loadService(final ClassLoader classLoader, URL url) {
        LOG.info("Load service '" + url + "'");

        Digester digester = new Digester();
        digester.setUseContextClassLoader(true);

        digester.setEntityResolver(new EntityResolver() {
            public InputSource resolveEntity(String string, String string1) {
                return new InputSource(new CharArrayReader(new char[0]));
            }

        });

        digester.addRule("rcfaces-services/service", new Rule() {
            @Override
            public void begin(String namespace, String name,
                    Attributes attributes) throws Exception {

                super.digester.push(new Service());
            }

            @Override
            public void end(String namespace, String name) throws Exception {
                Service service = (Service) super.digester.pop();

                LOG.debug("Load service '" + service.getId() + "' className='"
                        + service.getClassName() + "'");

                if (service.getClassName() == null) {
                    return;
                }

                Class< ? > clazz;
                try {
                    clazz = classLoader.loadClass(service.getClassName());

                } catch (Exception ex) {
                    LOG.error("Can not load class '" + service.getClassName()
                            + "'.", ex);
                    return;
                }

                Object serviceObject;
                try {
                    serviceObject = clazz.newInstance();

                } catch (Exception ex) {
                    LOG.error("Can not instanciate class '" + clazz + "'.", ex);
                    return;
                }

                services.put(service.getId(), serviceObject);

                LOG.info("Service '" + service.getId() + "' loaded: "
                        + serviceObject);
            }
        });

        digester.addBeanPropertySetter("rcfaces-services/service/service-id",
                "id");

        digester.addBeanPropertySetter(
                "rcfaces-services/service/service-class", "className");

        InputStream ins;
        try {
            ins = url.openStream();

        } catch (IOException ex) {
            LOG.error("Can not open stream from '" + url + "'.", ex);
            return;
        }

        if (ins == null) {
            LOG.info("Can not open stream from '" + url + "'.");
            return;
        }

        try {
            try {

                digester.parse(ins);

            } finally {
                ins.close();
            }
        } catch (IOException ex) {
            LOG.error("Can not load stream from '" + url + "'.", ex);

        } catch (SAXException ex) {
            LOG.error("Invalid XML resource '" + url + "'.", ex);
        }
    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.4 $ $Date: 2013/11/13 12:53:26 $
     */
    public static class Service {
        private String id;

        private String className;

        public final String getId() {
            return id;
        }

        public final void setId(String id) {
            this.id = id;
        }

        public final String getClassName() {
            return className;
        }

        public final void setClassName(String className) {
            this.className = className;
        }

    }
}
