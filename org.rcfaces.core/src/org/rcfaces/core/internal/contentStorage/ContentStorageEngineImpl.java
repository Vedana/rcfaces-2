/*
 * $Id: ContentStorageEngineImpl.java,v 1.4 2013/11/13 12:53:20 jbmeslin Exp $
 */
package org.rcfaces.core.internal.contentStorage;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import javax.faces.FacesException;
import javax.faces.component.StateHolder;
import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.internal.Constants;
import org.rcfaces.core.internal.RcfacesContext;
import org.rcfaces.core.internal.adapter.IAdapterManager;
import org.rcfaces.core.internal.content.ContentAdapterFactory;
import org.rcfaces.core.internal.contentAccessor.BasicContentAccessor;
import org.rcfaces.core.internal.contentAccessor.BasicGeneratedResourceInformation;
import org.rcfaces.core.internal.contentAccessor.BasicGenerationResourceInformation;
import org.rcfaces.core.internal.contentAccessor.ContentAccessorFactory;
import org.rcfaces.core.internal.contentAccessor.IContentAccessor;
import org.rcfaces.core.internal.contentAccessor.IContentPath;
import org.rcfaces.core.internal.contentAccessor.IGeneratedResourceInformation;
import org.rcfaces.core.internal.contentAccessor.IGenerationResourceInformation;
import org.rcfaces.core.internal.lang.ILimitedMap;
import org.rcfaces.core.internal.lang.LimitedMap;
import org.rcfaces.core.internal.lang.StringAppender;
import org.rcfaces.core.lang.IAdaptable;
import org.rcfaces.core.lang.IContentFamily;
import org.rcfaces.core.model.IContentModel;
import org.rcfaces.core.provider.AbstractProvider;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.4 $ $Date: 2013/11/13 12:53:20 $
 */
public class ContentStorageEngineImpl extends AbstractProvider implements
        IContentStorageEngine {

    private static final Log LOG = LogFactory
            .getLog(ContentStorageEngineImpl.class);

    private static final String DISABLE_CACHE_PARAMETER = "org.rcfaces.core.contentStorage.DISABLE_CACHE";

    private final IContentStorageRepository contentStorageRepository = new BasicContentStorageRepository();

    private int contentStorageServletPathType;

    private final Object contentStorageServletURL_LOCK = new Object();

    private volatile String contentStorageServletURL;

    private IAdapterManager adapterManager;

    private boolean disableCache = false;

    private final ILimitedMap<IGenerationResourceInformation, Content> registredContentsByGenerationInformation = new LimitedMap<IGenerationResourceInformation, Content>(
            Constants.CONTENT_STORAGE_CACHE_SIZE,
            Constants.CONTENT_STORAGE_CACHE_SOFT_REFERENCES);

    @Override
    public void startup(FacesContext facesContext) {
        super.startup(facesContext);

        RcfacesContext rcfacesContext = RcfacesContext
                .getInstance(facesContext);

        if (rcfacesContext.getContentStorageEngine() == null) {
            rcfacesContext.setContentStorageEngine(this);
        }

        disableCache = "true"
                .equalsIgnoreCase(facesContext.getExternalContext()
                        .getInitParameter(DISABLE_CACHE_PARAMETER));
        if (disableCache) {
            LOG.info("Content storage cache is disabled. (Parameter '"
                    + DISABLE_CACHE_PARAMETER + "' is setted to true.)");
        }

        contentStorageServletPathType = IContentPath.CONTEXT_PATH_TYPE;

        adapterManager = rcfacesContext.getAdapterManager();
    }

    public IContentStorageRepository getRepository() {
        return contentStorageRepository;
    }

    public String getId() {
        return "ContentStorageEngine";
    }

    public IContentAccessor registerContentModel(FacesContext facesContext,
            IContentModel contentModel,
            IGeneratedResourceInformation generatedInformation,
            IGenerationResourceInformation generationInformation) {

        // boolean modified = false;
        if (generatedInformation == null) {
            generatedInformation = new BasicGeneratedResourceInformation();
        }

        if (generationInformation == null) {
            generationInformation = new BasicGenerationResourceInformation();

            ((BasicGenerationResourceInformation) generationInformation)
                    .setProcessAtRequest(true);
        }

        if (contentStorageServletURL == null) {
            synchronized (contentStorageServletURL_LOCK) {
                // Il faut desynchroniser le startup des servlets !
                if (contentStorageServletURL == null) {
                    contentStorageServletURL = ContentStorageServlet
                            .getContentStorageBaseURI(facesContext
                                    .getExternalContext().getApplicationMap());

                    if (contentStorageServletURL == null) {
                        LOG.info("Content storage engine is disabled. (No started Content Storage Servlet)");

                        return ContentAccessorFactory.UNSUPPORTED_CONTENT_ACCESSOR;
                    }
                }
            }
        }

        if (generatedInformation.getContentFamily() == null) {
            generatedInformation.setContentFamily(IContentFamily.USER);
        }

        // Content content = null;
        boolean contentEngineMustBeRegistred = true;

        IContentStorageRepository repository = getRepository();

        String contentEngineId = null;
        if (disableCache == false) {
            contentEngineId = contentModel.getContentEngineId();
            if (contentEngineId == null) {
                Content content = registredContentsByGenerationInformation
                        .get(generationInformation);
                if (content != null) {
                    contentEngineId = content.getContentEngineId();
                    contentEngineMustBeRegistred = false;

                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Content is already in cache ! information='"
                                + generationInformation + "'");
                    }

                    IGeneratedResourceInformation contentGeneratedResourceInformation = content
                            .getGeneratedInformation();

                    if (contentGeneratedResourceInformation != null) {
                        contentGeneratedResourceInformation
                                .copyTo(generatedInformation);
                    }
                } else {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Cache does not contain information='"
                                + generationInformation + "'");
                    }

                }
            }
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Cache is disabled for information='"
                        + generationInformation + "'");
            }
        }

        generatedInformation.setProcessingAtRequest(generationInformation
                .isProcessAtRequest());
        contentModel.setInformations(generationInformation,
                generatedInformation);

        IResolvedContent resolvedContent = null;

        if (contentEngineId != null) {
            contentModel.setContentEngineId(contentEngineId);

            if (contentModel.checkNotModified()) { // Le modele doit verifier la
                // synchro entre le contentEngineId et le generationInformation
                if (LOG.isDebugEnabled()) {
                    LOG.debug("ContentModel '" + contentModel
                            + "' is not modified !");
                }
                resolvedContent = repository.load(contentEngineId);

                if (resolvedContent == null) {
                    // Le cache ne contient plus la ressource ... trop tard !
                    contentEngineId = null;
                    contentEngineMustBeRegistred = true;

                    registredContentsByGenerationInformation
                            .remove(generationInformation);
                }
            } else {
                // Modifié !

                contentEngineId = null;
                contentEngineMustBeRegistred = true;

                registredContentsByGenerationInformation
                        .remove(generationInformation);
            }
        }

        if (resolvedContent == null) {

            if (generatedInformation.isProcessingAtRequest()) {
                // On verra plus tard (dans la servlet)

                String specifiedSuffix = generationInformation
                        .getResponseSuffix();
                String specifiedContentType = generationInformation
                        .getResponseMimeType();

                String specifiedResourceKey = null;
                if (generationInformation
                        .getComputeResourceKeyFromGenerationInformation()) {
                    specifiedResourceKey = BasicGenerationResourceInformation
                            .generateResourceKeyFromGenerationInformation(generationInformation);
                }

                long specifiedLastModificationDate = generationInformation
                        .getResponseLastModified();

                resolvedContent = new ResolvedContentAtRequest(contentModel,
                        specifiedContentType, specifiedSuffix,
                        specifiedLastModificationDate, specifiedResourceKey,
                        generationInformation);

            } else {
                Object wrappedData = contentModel.getWrappedData();

                // Il nous faut un IResolvedContent !

                if (wrappedData instanceof IResolvedContent) {
                    resolvedContent = (IResolvedContent) wrappedData;

                } else {

                    AdaptationParameters parametrizedAdaptation = new AdaptationParameters(
                            contentModel, generationInformation,
                            generatedInformation);

                    try {
                        if (wrappedData instanceof IAdaptable) {
                            resolvedContent = ((IAdaptable) wrappedData)
                                    .getAdapter(IResolvedContent.class,
                                            parametrizedAdaptation);
                        }

                        if (resolvedContent == null) {
                            resolvedContent = adapterManager.getAdapter(
                                    wrappedData, IResolvedContent.class,
                                    parametrizedAdaptation);
                        }
                    } catch (Exception ex) {
                        LOG.error(
                                "Can not adapt content while render phase. (information='"
                                        + generationInformation + "')", ex);
                    }
                }

                if (resolvedContent == null) {
                    throw new FacesException("Can not transform raw object '"
                            + contentModel.getClass()
                            + "' to IResolvedContentModel !");
                }

                try {
                    // On provoque le calcul du buffer, car c'est du processing
                    // sur le rendu !
                    resolvedContent.getLength();

                } catch (Exception e) {
                    LOG.error(
                            "Can not resolve content while render phase. (information='"
                                    + generationInformation + "')", e);

                    return null;
                }
            }
        }

        // Permet de remettre la ressource dans le cache
        String url = repository.save(resolvedContent, contentModel);

        if (contentEngineMustBeRegistred) {
            contentEngineId = contentModel.getContentEngineId();
            if (contentEngineId != null) {
                registredContentsByGenerationInformation.put(
                        generationInformation, new Content(
                        // generationInformation,
                                generatedInformation, contentEngineId));
            }
        }

        IContentAccessor contentAccessor = new BasicContentAccessor(null,
                contentStorageServletURL + '/' + url,
                generatedInformation.getContentFamily());

        contentAccessor.setPathType(contentStorageServletPathType);

        return contentAccessor;
    }

    public IContentAccessor registerRaw(FacesContext facesContext, Object ref,
            IGeneratedResourceInformation generatedInformation) {

        if (contentStorageServletURL == null) {
            LOG.info("ContentStorage is not initialized. (Servlet path is invalid)");

            return ContentAccessorFactory.UNSUPPORTED_CONTENT_ACCESSOR;
        }

        IResolvedContent resolvedContent = null;
        if (ref instanceof IAdaptable) {
            resolvedContent = ((IAdaptable) ref).getAdapter(
                    IResolvedContent.class, null);
        }

        if (resolvedContent == null) {
            resolvedContent = adapterManager.getAdapter(ref,
                    IResolvedContent.class, null);
        }

        if (resolvedContent == null) {
            throw new FacesException("Can not transform raw object '"
                    + ref.getClass() + "' to IResolvedContent !");
        }

        String url = getRepository().save(resolvedContent, null);

        if (generatedInformation.getContentFamily() == null) {
            generatedInformation.setContentFamily(IContentFamily.USER);
        }

        IContentAccessor contentAccessor = new BasicContentAccessor(
                facesContext, contentStorageServletURL + '/' + url,
                generatedInformation.getContentFamily());

        contentAccessor.setPathType(contentStorageServletPathType);

        return contentAccessor;
    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.4 $ $Date: 2013/11/13 12:53:20 $
     */
    public static class ResolvedContentAtRequest extends
            AbstractResolvedContent implements IResolvedContentWrapper,
            Serializable {

        private static final long serialVersionUID = -7807317078965658005L;

        private final IContentModel contentModel;

        private transient IResolvedContent resolvedContent;

        private transient boolean errorState;

        private final String specifiedContentType;

        private final String specifiedURLSuffix;

        private final long specifiedLastModificationDate;

        private final String specifiedResourceKey;

        private final IGenerationResourceInformation generationInformation;

        public ResolvedContentAtRequest(IContentModel contentModel,
                String specifiedContentType, String specifiedURLSuffix,
                long specifiedLastModificationDate,
                String specifiedResourceKey,
                IGenerationResourceInformation generationInformation) {
            this.contentModel = contentModel;

            if (specifiedURLSuffix == null && specifiedContentType != null) {
                specifiedURLSuffix = ContentAdapterFactory
                        .getSuffixByMimeType(specifiedContentType);
            }

            this.specifiedURLSuffix = specifiedURLSuffix;
            this.specifiedContentType = specifiedContentType;
            this.specifiedLastModificationDate = specifiedLastModificationDate;
            this.specifiedResourceKey = specifiedResourceKey;
            this.generationInformation = generationInformation;
        }

        public String getContentType() {
            // Le contentType peut changer ...
            if (isContentResolved()) {
                return getResolvedContent().getContentType();
            }

            return specifiedContentType;
        }

        protected boolean isContentResolved() {
            if (errorState) {
                return false;
            }

            return resolvedContent != null;
        }

        @Override
        public String getURLSuffix() {
            if (isContentResolved()) {
                return getResolvedContent().getURLSuffix();
            }

            return specifiedURLSuffix;
        }

        public InputStream getInputStream() throws IOException {
            return getResolvedContent().getInputStream();
        }

        @Override
        public long getModificationDate() {
            if (isContentResolved()) {
                return getResolvedContent().getModificationDate();
            }

            return specifiedLastModificationDate;
        }

        @Override
        public int getLength() {
            return getResolvedContent().getLength();
        }

        @Override
        public void appendHashInformations(StringAppender sa) {
            // Il faut les infos du content model ???

            if (specifiedResourceKey != null) {
                // Pas besoin d'informations complementaires
                return;
            }

            getResolvedContent().appendHashInformations(sa);
        }

        public synchronized IResolvedContent getResolvedContent() {
            if (errorState) {
                return null;
            }

            if (resolvedContent != null) {
                return resolvedContent;
            }

            try {
                Object wrappedData = contentModel.getWrappedData();

                AdaptationParameters parametrizedAdaptation = new AdaptationParameters(
                        contentModel, generationInformation, null);

                if (wrappedData instanceof IAdaptable) {
                    resolvedContent = ((IAdaptable) wrappedData).getAdapter(
                            IResolvedContent.class, parametrizedAdaptation);

                    if (resolvedContent != null) {
                        return resolvedContent;
                    }
                }

                RcfacesContext rcfacesContext = RcfacesContext
                        .getCurrentInstance();

                resolvedContent = rcfacesContext.getAdapterManager()
                        .getAdapter(wrappedData, IResolvedContent.class,
                                parametrizedAdaptation);

                if (resolvedContent != null) {
                    return resolvedContent;
                }

                throw new FacesException(
                        "Can not transform wrappedData of content model '"
                                + wrappedData + "' !");

            } catch (RuntimeException ex) {

                errorState = true;

                throw ex;
            }
        }

        @Override
        public boolean isProcessAtRequest() {
            return true;
        }

        @Override
        public boolean isErrored() {
            IResolvedContent resolvedContent = getResolvedContent();

            return errorState || resolvedContent.isErrored();
        }

        @Override
        public String getETag() {
            return getResolvedContent().getETag();
        }

        @Override
        public String getHash() {
            return getResolvedContent().getHash();
        }

        @Override
        public String getResourceKey() {
            if (isContentResolved() == false && isProcessAtRequest()) {
                // C'est traité sur la requete !

                if (specifiedResourceKey != null) {
                    return specifiedResourceKey;
                }

                if (contentModel instanceof IResourceKey) {
                    // L'objet peut tout de même savoir sa clef ???
                    return ((IResourceKey) contentModel).getResourceKey();
                }
                return null;
            }

            return getResolvedContent().getResourceKey();
        }
    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.4 $ $Date: 2013/11/13 12:53:20 $
     */
    public static class Content implements StateHolder, Serializable {
        /**
         * 
         */
        private static final long serialVersionUID = -6436030755767342990L;

        // private IGenerationResourceInformation generationInformation;

        private IGeneratedResourceInformation generatedInformation;

        private String contentEngineId;

        private boolean transientFlag;

        public Content(
                // IGenerationResourceInformation generationInformation,
                IGeneratedResourceInformation generatedInformation,
                String contentEngineId) {
            this.contentEngineId = contentEngineId;
            // this.generationInformation = generationInformation;
            this.generatedInformation = generatedInformation;
        }

        public boolean isTransient() {
            return transientFlag;
        }

        public void setTransient(boolean transientFlag) {
            this.transientFlag = transientFlag;
        }

        public void restoreState(FacesContext context, Object state) {
            Object states[] = (Object[]) state;

            contentEngineId = (String) states[0];

            // generationInformation = (IGenerationResourceInformation)
            // UIComponentBase.restoreAttachedState(context, states[1]);

            generatedInformation = (IGeneratedResourceInformation) UIComponentBase
                    .restoreAttachedState(context, states[2]);
        }

        public Object saveState(FacesContext context) {
            Object states[] = new Object[3];

            states[0] = contentEngineId;

            // states[1] =
            // UIComponentBase.saveAttachedState(context,generationInformation);

            states[2] = UIComponentBase.saveAttachedState(context,
                    generatedInformation);

            return states;
        }

        public final IGeneratedResourceInformation getGeneratedInformation() {
            return generatedInformation;
        }

        public final String getContentEngineId() {
            return contentEngineId;
        }
    }
}
