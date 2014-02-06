/*
 * $Id: RcfacesContextImpl.java,v 1.3 2013/11/13 12:53:26 jbmeslin Exp $
 */
package org.rcfaces.core.internal.config;

import java.io.CharArrayReader;
import java.io.Externalizable;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.FactoryFinder;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseListener;
import javax.faces.lifecycle.Lifecycle;
import javax.faces.lifecycle.LifecycleFactory;

import org.apache.commons.digester.Digester;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.internal.Constants;
import org.rcfaces.core.internal.RcfacesContext;
import org.rcfaces.core.internal.adapter.AdapterManagerImpl;
import org.rcfaces.core.internal.adapter.IAdapterManager;
import org.rcfaces.core.internal.capability.IListenerStrategy;
import org.rcfaces.core.internal.contentAccessor.ContentAccessorsRegistryImpl;
import org.rcfaces.core.internal.contentAccessor.IContentAccessorRegistry;
import org.rcfaces.core.internal.contentAccessor.IContentProxyHandler;
import org.rcfaces.core.internal.contentAccessor.IContentVersionHandler;
import org.rcfaces.core.internal.contentProxy.IResourceProxyHandler;
import org.rcfaces.core.internal.contentProxy.ResourceProxyHandlerImpl;
import org.rcfaces.core.internal.contentStorage.ContentStorageAccessorHandler;
import org.rcfaces.core.internal.contentStorage.IContentStorageEngine;
import org.rcfaces.core.internal.converter.StrategyListenerConverter;
import org.rcfaces.core.internal.documentBuilder.IDocumentBuilderProvider;
import org.rcfaces.core.internal.renderkit.border.IBorderRenderersRegistry;
import org.rcfaces.core.internal.repository.IRepositoryManager;
import org.rcfaces.core.internal.repository.RepositoryManagerImpl;
import org.rcfaces.core.internal.service.IServicesRegistry;
import org.rcfaces.core.internal.util.ConfigurationLoader;
import org.rcfaces.core.internal.validator.ClientValidatorsRegistryImpl;
import org.rcfaces.core.internal.validator.IClientValidatorsRegistry;
import org.rcfaces.core.internal.version.IResourceVersionHandler;
import org.rcfaces.core.internal.version.ResourceVersionHandlerImpl;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:26 $
 */
public class RcfacesContextImpl extends RcfacesContext implements
        Externalizable {

    private static final long serialVersionUID = -4224530723124583628L;

    private static final Log LOG = LogFactory.getLog(RcfacesContextImpl.class);

    private static final Package[] KERNEL_CONFIG_FILENAMES = new Package[] {
            RcfacesContext.class.getPackage(),
            RcfacesContextImpl.class.getPackage(),
            AdapterManagerImpl.class.getPackage(),
            ResourceVersionHandlerImpl.class.getPackage(),
            ResourceProxyHandlerImpl.class.getPackage(),
            ContentAccessorsRegistryImpl.class.getPackage(),
            // ImageContentAccessorHandlerImpl.class.getPackage(),
            ContentAccessorsRegistryImpl.class.getPackage(),
            ContentStorageAccessorHandler.class.getPackage(),
            RepositoryManagerImpl.class.getPackage() };

    private static final String DESIGNER_MODE_PROPERTY = "com.vedana.nodus3.designer";

    private static final String RCFACES_VERSION_PROPERTY = "org_rcfaces_VERSION";

    private transient ServicesRegistryImpl servicesRegistry;

    private transient ClientValidatorsRegistryImpl clientValidatorsRegistry;

    private transient ProvidersRegistry providersRegistry;

    private transient BorderRenderersRegistryImpl borderRenderersRegistry;

    private final Map<String, Serializable> attributes = new HashMap<String, Serializable>(
            32);

    private transient IContentVersionHandler contentVersionHandler;

    private transient IContentProxyHandler contentProxyHandler;

    private transient IContentAccessorRegistry contentAccessorRegistry;

    private transient IResourceVersionHandler resourceVersionHandler;

    private transient IResourceProxyHandler resourceProxyHandler;

    private transient String applicationVersion;

    private transient int listenerManagerStrategy = IListenerStrategy.DEFAULT;

    private transient IAdapterManager adapterManager;

    private transient IContentStorageEngine indirectContentRepository;

    private transient IDocumentBuilderProvider documentBuilderProvider;

    private transient IRepositoryManager repositoryManager;

    private boolean designerMode;

    public RcfacesContextImpl() {
    }

    @Override
    protected void initialize(FacesContext facesContext) {
        if (facesContext == null) {
            facesContext = FacesContext.getCurrentInstance();
        }

        designerMode = facesContext.getExternalContext().getApplicationMap()
                .containsKey(DESIGNER_MODE_PROPERTY);
        if (designerMode) {
            LOG.info("Designer MODE  detected.");
        }

        facesContext.getExternalContext().getApplicationMap()
                .put(RCFACES_VERSION_PROPERTY, Constants.getVersion());

        initializeRegistries(null);

        loadConfigs(facesContext);

        initializeConfigs(facesContext);
    }

    protected void initializeRegistries(FacesContext facesContext) {

        if (designerMode) {
            LOG.debug("Ignore service registry (designer mode)");

            LOG.debug("Ignore clientValidators registry (designer mode)");

        } else {
            LOG.debug("Initializing service registry");
            servicesRegistry = createServicesRegistry();

            LOG.debug("Initializing clientValidators registry");
            clientValidatorsRegistry = createClientValidatorsRegistry();
        }

        LOG.debug("Initializing border renderers registry");
        borderRenderersRegistry = createBorderRenderersRegistry();

        LOG.debug("Initializing providers registry");
        providersRegistry = createProvidersRegistry();
    }

    @Override
    public final IServicesRegistry getServicesRegistry() {
        return servicesRegistry;
    }

    protected ServicesRegistryImpl createServicesRegistry() {
        LOG.debug("Installing services registry");

        LifecycleFactory lifecycleFactory = (LifecycleFactory) FactoryFinder
                .getFactory(FactoryFinder.LIFECYCLE_FACTORY);

        Iterator<String> it = lifecycleFactory.getLifecycleIds();
        for (; it.hasNext();) {
            String lifecycleId = it.next();

            Lifecycle lifecycle = lifecycleFactory.getLifecycle(lifecycleId);

            PhaseListener phaseListeners[] = lifecycle.getPhaseListeners();
            if (phaseListeners == null || phaseListeners.length < 1) {
                continue;
            }

            for (int i = 0; i < phaseListeners.length; i++) {
                PhaseListener phaseListener = phaseListeners[i];

                if ((phaseListener instanceof ServicesRegistryImpl) == false) {
                    continue;
                }

                servicesRegistry = (ServicesRegistryImpl) phaseListener;
                servicesRegistry.initialize(null);

                LOG.debug("Services registry installed.");
                return servicesRegistry;
            }
        }

        LOG.error("Can not find Services Registry into lifeCycle factories.");

        // On en créée un en attendant !
        ServicesRegistryImpl services = new ServicesRegistryImpl();
        services.initialize(null);

        return services;
    }

    @Override
    public final IClientValidatorsRegistry getClientValidatorsRegistry() {
        return clientValidatorsRegistry;
    }

    protected ClientValidatorsRegistryImpl createClientValidatorsRegistry() {
        return new ClientValidatorsRegistryImpl();
    }

    @Override
    public final IProvidersRegistry getProvidersRegistry() {
        return providersRegistry;
    }

    protected ProvidersRegistry createProvidersRegistry() {
        return new ProvidersRegistry();
    }

    @Override
    public final IBorderRenderersRegistry getBorderRenderersRegistry() {
        return borderRenderersRegistry;
    }

    protected BorderRenderersRegistryImpl createBorderRenderersRegistry() {
        return new BorderRenderersRegistryImpl();
    }

    public final Serializable setAttribute(String property, Serializable value) {
        return attributes.put(property, value);
    }

    public final Serializable getAttribute(String property) {
        return attributes.get(property);
    }

    public final Serializable removeAttribute(String property) {
        return attributes.remove(property);
    }

    private void loadConfigs(FacesContext facesContext) {
        LOG.info("Loading rcfaces config ...");

        Digester digester = new Digester();
        digester.setUseContextClassLoader(true);

        digester.setEntityResolver(new EntityResolver() {

            public InputSource resolveEntity(String string, String string1) {
                return new InputSource(new CharArrayReader(new char[0]));
            }

        });

        LOG.debug("Declare configurations rules.");
        configureRules(digester);

        List<URL> urls = new ArrayList<URL>(32);

        LOG.debug("Search configuration files ...");
        for (int i = 0; i < KERNEL_CONFIG_FILENAMES.length; i++) {
            Package pkg = KERNEL_CONFIG_FILENAMES[i];

            String resourceName = pkg.getName().replace('.', '/') + '/'
                    + RCFACES_CONFIG_FILENAME;

            URL url = getClass().getClassLoader().getResource(resourceName);

            LOG.debug("Configuration file '" + resourceName + "' => "
                    + ((url != null) ? "exists" : "ignore"));
            if (url == null) {
                continue;
            }

            urls.add(url);
        }

        ConfigurationLoader configurationLoader = ConfigurationLoader
                .scanRCFacesConfig(facesContext.getExternalContext(), urls);

        configurationLoader.parse(digester);

        loadProvidersConfiguration(facesContext, configurationLoader);

        LOG.info("Rcfaces config loaded.");
    }

    private void configureRules(Digester digester) {
        if (designerMode == false) {
            ((ServicesRegistryImpl) getServicesRegistry())
                    .configureRules(digester);
            ((ClientValidatorsRegistryImpl) getClientValidatorsRegistry())
                    .configureRules(digester);
        }

        ((ProvidersRegistry) getProvidersRegistry()).configureRules(digester);
        ((BorderRenderersRegistryImpl) getBorderRenderersRegistry())
                .configureRules(digester);
    }

    private void loadProvidersConfiguration(FacesContext facesContext,
            final ConfigurationLoader configurationLoader) {

        IProvidersConfigurator providersConfigurator = new IProvidersConfigurator() {

            public void parseConfiguration(Digester digester) {
                configurationLoader.parse(digester);
            }

        };

        ((ProvidersRegistry) getProvidersRegistry())
                .loadProvidersConfiguration(providersConfigurator);

        ((ProvidersRegistry) getProvidersRegistry())
                .startupProviders(facesContext);
    }

    protected void initializeConfigs(FacesContext facesContext) {

        Map<String, Object> applicationMap = facesContext.getExternalContext()
                .getApplicationMap();

        applicationVersion = (String) applicationMap
                .get(APPLICATION_VERSION_PROPERTY);

        LOG.debug("Set application version to '" + applicationVersion + "'.");

        String strategyName = facesContext.getExternalContext()
                .getInitParameter(LISTENER_MANAGER_STRATEGY_PARAMETER);

        if (strategyName != null) {
            Integer convertedStartegy = ((Integer) StrategyListenerConverter.SINGLETON
                    .getAsObject(facesContext, null, strategyName));

            if (convertedStartegy == null) {
                throw new IllegalArgumentException(
                        "Listener manager strategy: '"
                                + strategyName
                                + "' is not recognized. See IListenerStrategy constants.");
            }

            listenerManagerStrategy = convertedStartegy.intValue();

            LOG.debug("Set listener manager stategy to '" + strategyName + "'.");
        }

        LOG.debug("Initialize all configs: done.");
    }

    @Override
    public final String getApplicationVersion() {
        return applicationVersion;
    }

    @Override
    public void setDefaultContentVersionHandler(
            IContentVersionHandler contentVersionHandler) {
        this.contentVersionHandler = contentVersionHandler;
    }

    @Override
    public final IContentVersionHandler getDefaultContentVersionHandler() {
        return contentVersionHandler;
    }

    @Override
    public IContentProxyHandler getDefaultContentProxyHandler() {
        return contentProxyHandler;
    }

    @Override
    public void setDefaultContentProxyHandler(
            IContentProxyHandler contentProxyHandler) {
        this.contentProxyHandler = contentProxyHandler;
    }

    public void readExternal(ObjectInput in) {
        // On ne serialize rien !
    }

    public void writeExternal(ObjectOutput out) {
        // On ne serialize rien !
    }

    @Override
    public IAdapterManager getAdapterManager() {
        return adapterManager;
    }

    @Override
    public void setAdapterManager(IAdapterManager adapterManager) {
        this.adapterManager = adapterManager;
    }

    @Override
    public boolean isDesignerMode() {
        return designerMode;
    }

    @Override
    public IContentAccessorRegistry getContentAccessorRegistry() {
        return contentAccessorRegistry;
    }

    @Override
    public void setContentAccessorRegistry(
            IContentAccessorRegistry contentAccessorRegistry) {
        this.contentAccessorRegistry = contentAccessorRegistry;
    }

    @Override
    public IResourceVersionHandler getResourceVersionHandler() {
        return resourceVersionHandler;
    }

    @Override
    public void setResourceVersionHandler(
            IResourceVersionHandler resourceVersionHandler) {
        this.resourceVersionHandler = resourceVersionHandler;
    }

    @Override
    public IContentStorageEngine getContentStorageEngine() {
        return indirectContentRepository;
    }

    @Override
    public void setContentStorageEngine(
            IContentStorageEngine indirectContentRepository) {
        this.indirectContentRepository = indirectContentRepository;
    }

    @Override
    public IResourceProxyHandler getResourceProxyHandler() {
        return resourceProxyHandler;
    }

    @Override
    public void setResourceProxyHandler(
            IResourceProxyHandler resourceProxyHandler) {
        this.resourceProxyHandler = resourceProxyHandler;
    }

    @Override
    public IDocumentBuilderProvider getDocumentBuilderProvider() {
        return documentBuilderProvider;
    }

    @Override
    public void setDocumentBuilderProvider(
            IDocumentBuilderProvider documentBuilderProvider) {
        this.documentBuilderProvider = documentBuilderProvider;
    }

    @Override
    public IRepositoryManager getRepositoryManager() {
        return repositoryManager;
    }

    @Override
    public void setRepositoryManager(IRepositoryManager repositoryManager) {
        this.repositoryManager = repositoryManager;
    }

    @Override
    public int getListenerManagerStrategy() {
        return listenerManagerStrategy;
    }
}
