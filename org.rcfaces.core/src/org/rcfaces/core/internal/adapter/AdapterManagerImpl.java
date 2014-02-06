/*
 * $Id: AdapterManagerImpl.java,v 1.4 2013/11/13 12:53:26 jbmeslin Exp $
 */
package org.rcfaces.core.internal.adapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.FacesException;
import javax.faces.context.FacesContext;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.Rule;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.internal.RcfacesContext;
import org.rcfaces.core.internal.util.ClassLocator;
import org.rcfaces.core.lang.IAdapterFactory;
import org.rcfaces.core.provider.AbstractProvider;
import org.xml.sax.Attributes;

/**
 * 
 * @author Eclipse project (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.4 $ $Date: 2013/11/13 12:53:26 $
 */
public class AdapterManagerImpl extends AbstractProvider implements
        IAdapterManager {
    private static final Log LOG = LogFactory.getLog(AdapterManagerImpl.class);

    protected final Map<String, List<IAdapterFactory>> factories;

    protected Map<String, Map<String, IAdapterFactory>> adapterLookup;

    protected Map<Class< ? >, List<Class< ? >>> classSearchOrderLookup;

    public AdapterManagerImpl() {
        factories = new HashMap<String, List<IAdapterFactory>>(5);
        adapterLookup = null;

        RcfacesContext rcfacesContext = RcfacesContext.getCurrentInstance();

        if (rcfacesContext.getAdapterManager() == null) {
            rcfacesContext.setAdapterManager(this);
        }
    }

    public String getId() {
        return "AdapterManager";
    }

    @Override
    public void configureRules(Digester digester) {
        super.configureRules(digester);

        digester.addRule("rcfaces-config/adapters/adapter", new Rule() {
            @Override
            public void begin(String namespace, String name,
                    Attributes attributes) throws Exception {

                super.digester.push(new AdapterBean());
            }

            @Override
            public void end(String namespace, String name) throws Exception {
                AdapterBean adapterBean = (AdapterBean) super.digester.pop();

                declareAdapter(adapterBean);
            }
        });

        digester.addBeanPropertySetter(
                "rcfaces-config/adapters/adapter/adaptable-class", "className");

        digester.addBeanPropertySetter(
                "rcfaces-config/adapters/adapter/adapterFactory-class",
                "adapterFactoryClassName");

    }

    protected void declareAdapter(AdapterBean adapterBean) {
        String adapterClassName = adapterBean.getClassName();
        String factoryClassName = adapterBean.getAdapterFactoryClassName();

        LOG.debug("Declare adapter adapterClassName='" + adapterClassName
                + "' factoryClassName='" + factoryClassName + "'.");

        if (adapterClassName == null || adapterClassName.length() < 1
                || factoryClassName == null || factoryClassName.length() < 1) {
            throw new FacesException(
                    "Invalid adapter configuration. (adapter-class='"
                            + adapterClassName + "' adapterFactory-class='"
                            + factoryClassName + "')");
        }

        FacesContext facesContext = FacesContext.getCurrentInstance();

        /*
         * int dimension = 0; for (;;) { int dim =
         * adapterClassName.lastIndexOf('['); if (dim < 0) { break; }
         * dimension++; adapterClassName = adapterClassName.substring(0, dim); }
         */

        Class< ? > adapterClass;
        try {
            adapterClass = ClassLocator.load(adapterClassName, this,
                    facesContext);

        } catch (ClassNotFoundException e) {
            LOG.info("Adapter class not found '" + adapterClassName
                    + "', ignore adapter !", e);
            return;
        }

        Class< ? extends IAdapterFactory> factoryClass;
        try {
            factoryClass = ClassLocator.load(factoryClassName, this,
                    facesContext, IAdapterFactory.class);

        } catch (ClassNotFoundException e) {
            LOG.info("Factory class '" + factoryClassName
                    + "' for adapterClass='" + adapterClassName
                    + "' is not found, ignore adapter !", e);
            return;
        }

        IAdapterFactory adapterFactory;
        try {
            adapterFactory = factoryClass.newInstance();

        } catch (Throwable th) {
            LOG.info("Can not instanciate factory class '" + factoryClassName
                    + "' for adapterClass='" + adapterClassName
                    + "', ignore adapter !", th);
            return;
        }

        registerAdapters(adapterFactory, adapterClass);
    }

    private Map<String, IAdapterFactory> getFactories(Class< ? > adaptable) {
        // cache reference to lookup to protect against concurrent flush
        Map<String, Map<String, IAdapterFactory>> lookup = adapterLookup;
        if (lookup == null) {
            lookup = new HashMap<String, Map<String, IAdapterFactory>>(30);
            adapterLookup = lookup;
        }

        Map<String, IAdapterFactory> table = lookup.get(adaptable.getName());
        if (table == null) {
            // calculate adapters for the class
            table = new HashMap<String, IAdapterFactory>(4);
            Class< ? >[] classes = computeClassOrder(adaptable);
            for (int i = 0; i < classes.length; i++) {
                addFactoriesFor(classes[i].getName(), table);
            }

            // cache the table
            lookup.put(adaptable.getName(), table);
        }
        return table;
    }

    @SuppressWarnings("unchecked")
    public <T> T getAdapter(Object adaptable, Class<T> adapterType,
            Object parameter) {
        IAdapterFactory factory = getFactories(adaptable.getClass()).get(
                adapterType.getName());
        T result = null;
        if (factory != null) {
            result = factory.getAdapter(adaptable, adapterType, parameter);
        }

        if (result == null && adapterType.isInstance(adaptable)) {
            result = (T) adaptable;
        }

        return result;
    }

    private Class< ? >[] computeClassOrder(Class< ? > adaptable) {
        List<Class< ? >> classes = null;
        // cache reference to lookup to protect against concurrent flush
        Map<Class< ? >, List<Class< ? >>> lookup = classSearchOrderLookup;
        if (lookup != null) {
            classes = lookup.get(adaptable);
        }

        // compute class order only if it hasn't been cached before
        if (classes == null) {
            classes = new ArrayList<Class< ? >>();
            computeClassOrder(adaptable, classes);
            if (lookup == null) {
                lookup = new HashMap<Class< ? >, List<Class< ? >>>();
                classSearchOrderLookup = lookup;
            }
            lookup.put(adaptable, classes);
        }

        return classes.toArray(new Class[classes.size()]);
    }

    private void computeClassOrder(Class< ? > adaptable,
            Collection<Class< ? >> classes) {
        Class< ? > clazz = adaptable;
        Set<Class< ? >> seen = new HashSet<Class< ? >>(4);
        while (clazz != null) {
            classes.add(clazz);
            computeInterfaceOrder(clazz.getInterfaces(), classes, seen);
            clazz = clazz.getSuperclass();
        }
    }

    private void computeInterfaceOrder(Class< ? >[] interfaces,
            Collection<Class< ? >> classes, Set<Class< ? >> seen) {
        List<Class< ? >> newInterfaces = new ArrayList<Class< ? >>(
                interfaces.length);
        for (int i = 0; i < interfaces.length; i++) {
            Class< ? > interfac = interfaces[i];
            if (seen.add(interfac)) {
                // note we cannot recurse here without changing the resulting
                // interface order
                classes.add(interfac);
                newInterfaces.add(interfac);
            }
        }

        for (Class< ? > clazz : newInterfaces) {
            computeInterfaceOrder(clazz.getInterfaces(), classes, seen);
        }
    }

    private void addFactoriesFor(String typeName,
            Map<String, IAdapterFactory> table) {
        List<IAdapterFactory> factoryList;
        synchronized (factories) {
            factoryList = factories.get(typeName);
        }

        if (factoryList == null) {
            return;
        }

        synchronized (factoryList) {
            int size = factoryList.size();
            for (int i = 0; i < size; i++) {
                IAdapterFactory factory = factoryList.get(i);

                Class< ? >[] adapters = factory.getAdapterList();
                for (int j = 0; j < adapters.length; j++) {
                    String adapterName = adapters[j].getName();
                    if (table.containsKey(adapterName)) {
                        continue;
                    }

                    table.put(adapterName, factory);
                }
            }
        }
    }

    public void registerAdapters(IAdapterFactory factory, Class< ? > adaptable) {
        registerFactory(factory, adaptable.getName());

        flushLookup();
    }

    private synchronized void flushLookup() {
        adapterLookup = null;
        classSearchOrderLookup = null;
    }

    private void registerFactory(IAdapterFactory factory, String adaptableType) {
        List<IAdapterFactory> list;
        synchronized (factories) {
            list = factories.get(adaptableType);
            if (list == null) {
                list = new ArrayList<IAdapterFactory>(5);
                factories.put(adaptableType, list);
            }
        }
        synchronized (list) {
            list.add(factory);
        }
    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.4 $ $Date: 2013/11/13 12:53:26 $
     */
    public static class AdapterBean {
        private String className;

        private String adapterFactoryClassName;

        public final String getAdapterFactoryClassName() {
            return adapterFactoryClassName;
        }

        public final String getClassName() {
            return className;
        }

        public final void setAdapterFactoryClassName(
                String adapterFactoryClassName) {
            this.adapterFactoryClassName = adapterFactoryClassName;
        }

        public final void setClassName(String className) {
            this.className = className;
        }

    }

}
