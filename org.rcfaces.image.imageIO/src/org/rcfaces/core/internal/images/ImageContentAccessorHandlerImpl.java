/*
 * $Id: ImageContentAccessorHandlerImpl.java,v 1.2 2013/11/14 10:55:22 jbmeslin Exp $
 */
package org.rcfaces.core.internal.images;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.FacesException;
import javax.faces.context.FacesContext;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.Rule;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.image.IGeneratedImageInformation;
import org.rcfaces.core.image.IImageOperation;
import org.rcfaces.core.internal.Constants;
import org.rcfaces.core.internal.RcfacesContext;
import org.rcfaces.core.internal.contentAccessor.BasicGenerationResourceInformation;
import org.rcfaces.core.internal.contentAccessor.IContentAccessor;
import org.rcfaces.core.internal.contentAccessor.IContentPath;
import org.rcfaces.core.internal.contentAccessor.IFiltredContentAccessor;
import org.rcfaces.core.internal.contentAccessor.IGenerationResourceInformation;
import org.rcfaces.core.internal.contentProxy.IResourceProxyHandler;
import org.rcfaces.core.internal.contentStorage.ContentStorageServlet;
import org.rcfaces.core.internal.contentStorage.IContentStorageEngine;
import org.rcfaces.core.internal.images.operation.GIFConversionImageOperation;
import org.rcfaces.core.internal.util.ClassLocator;
import org.rcfaces.core.internal.util.PathTypeTools;
import org.rcfaces.core.internal.version.IResourceVersionHandler;
import org.xml.sax.Attributes;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/11/14 10:55:22 $
 */
public class ImageContentAccessorHandlerImpl extends
        ImageContentAccessorHandler {

    private static final Log LOG = LogFactory
            .getLog(ImageContentAccessorHandlerImpl.class);

    private static final String GIF_WRITER_PARAMETER = "org.rcfaces.core.images.GIF_WRITER";

    private static final IImageResourceAdapter[] IMAGE_RESOURCE_ADAPTER_EMPTY_ARRAY = new IImageResourceAdapter[0];

    public static final int NO_VALIDATION = 0;

    public static final int WRITER_VALIDATION = 1;

    public static final int RESOURCE_ADAPTER_VALIDATION = 2;

    private static final String NO_OPERATION_ID = "noOperation";

    private final Map<String, IImageOperation> operationsById = new HashMap<String, IImageOperation>(
            32);

    private final Map<String, Boolean> validContentTypes = new HashMap<String, Boolean>(
            8);

    private final FileNameMap fileNameMap;

    private boolean contentAccessorAvailable;

    private RcfacesContext rcfacesContext;

    private Boolean gifWriterEnabled;

    private List<ImageResourceAdapterBean> imageResourceAdaptersList;

    private ImageResourceAdapterBean imageResourceAdapters[];

    public ImageContentAccessorHandlerImpl() {
        fileNameMap = URLConnection.getFileNameMap();
    }

    public String getId() {
        return "ImageContentAccessorHandler";
    }

    @Override
    public void startup(FacesContext facesContext) {
        super.startup(facesContext);

        rcfacesContext = RcfacesContext.getInstance(facesContext);

        contentAccessorAvailable = ContentStorageServlet
                .getContentStorageBaseURI(facesContext.getExternalContext()
                        .getApplicationMap()) != null;

        if (contentAccessorAvailable == false) {
            LOG.info("ImageContentAccessorImpl is not available");

        } else {
            LOG.debug("ImageContentAccessorImpl available");
        }

        String gifWriterValue = facesContext.getExternalContext()
                .getInitParameter(GIF_WRITER_PARAMETER);

        if (gifWriterValue != null) {
            gifWriterValue = gifWriterValue.trim().toLowerCase();

            if ("true".equals(gifWriterValue)) {
                gifWriterEnabled = Boolean.TRUE;
                LOG.info("ImageContentAccessorImpl force gif writer enabled.");

            } else if ("false".equals(gifWriterValue)) {
                gifWriterEnabled = Boolean.FALSE;
                LOG.info("ImageContentAccessorImpl force gif writer disabled.");

            } else if (gifWriterValue.length() > 0
                    && "auto".equals(gifWriterValue) == false) {
                throw new FacesException("Invalid value for parameter '"
                        + GIF_WRITER_PARAMETER + ".");
            }
        }

        if (gifWriterEnabled == null) {
            Iterator<ImageWriter> it = ImageIO
                    .getImageWritersByMIMEType(GIFConversionImageOperation.MIME_TYPE);

            gifWriterEnabled = Boolean.valueOf(it.hasNext());
            LOG.info("ImageContentAccessorImpl gif writer auto detection: "
                    + gifWriterEnabled);

        }

        if (imageResourceAdaptersList.size() > 0) {
            imageResourceAdapters = imageResourceAdaptersList
                    .toArray(new ImageResourceAdapterBean[imageResourceAdaptersList
                            .size()]);
        }
        imageResourceAdaptersList = null;
    }

    @Override
    public void configureRules(Digester digester) {
        super.configureRules(digester);

        imageResourceAdaptersList = new ArrayList<ImageResourceAdapterBean>();

        digester.addRule("rcfaces-config/image-operations/operation",
                new Rule() {

                    @Override
                    public void begin(String namespace, String name,
                            Attributes attributes) throws Exception {

                        super.digester.push(new OperationBean());
                    }

                    @Override
                    public void end(String namespace, String name)
                            throws Exception {
                        OperationBean operationBean = (OperationBean) super.digester
                                .pop();

                        declareOperation(operationBean);
                    }
                });
        digester.addBeanPropertySetter(
                "rcfaces-config/image-operations/operation/operation-id", "id");
        digester.addBeanPropertySetter(
                "rcfaces-config/image-operations/operation/operation-name",
                "name");
        digester.addBeanPropertySetter(
                "rcfaces-config/image-operations/operation/operation-class",
                "className");
        digester.addBeanPropertySetter(
                "rcfaces-config/image-operations/operation/operation-force-suffix",
                "forceSuffix");
        digester.addBeanPropertySetter(
                "rcfaces-config/image-operations/operation/operation-response-mimeType",
                "responseMimeType");
        digester.addBeanPropertySetter(
                "rcfaces-config/image-operations/operation/operation-source-mimeType",
                "sourceMimeType");
        digester.addBeanPropertySetter(
                "rcfaces-config/image-operations/operation/operation-encoder-mimeType",
                "encoderMimeType");

        digester.addRule(
                "rcfaces-config/image-resource-adapters/resource-adapter",
                new Rule() {

                    @Override
                    public void begin(String namespace, String name,
                            Attributes attributes) throws Exception {

                        super.digester.push(new ImageResourceAdapterBean());
                    }

                    @Override
                    public void end(String namespace, String name)
                            throws Exception {
                        ImageResourceAdapterBean imageResourceAdapterBean = (ImageResourceAdapterBean) super.digester
                                .pop();

                        addImageResourceAdapter(imageResourceAdapterBean);
                    }
                });
        digester.addBeanPropertySetter(
                "rcfaces-config/image-resource-adapters/resource-adapter/adapter-id",
                "id");
        digester.addBeanPropertySetter(
                "rcfaces-config/image-resource-adapters/resource-adapter/adapter-name",
                "name");
        digester.addBeanPropertySetter(
                "rcfaces-config/image-resource-adapters/resource-adapter/adapter-class",
                "className");

        digester.addRule(
                "rcfaces-config/image-resource-adapters/resource-adapter/content-type",
                new Rule() {

                    @Override
                    public void body(String namespace, String name, String text)
                            throws Exception {

                        ImageResourceAdapterBean imageResourceAdapterBean = (ImageResourceAdapterBean) super.digester
                                .peek();

                        imageResourceAdapterBean.addContentType(text);
                    }

                });

        digester.addRule(
                "rcfaces-config/image-resource-adapters/resource-adapter/suffix",
                new Rule() {

                    @Override
                    public void body(String namespace, String name, String text)
                            throws Exception {

                        ImageResourceAdapterBean imageResourceAdapterBean = (ImageResourceAdapterBean) super.digester
                                .peek();

                        imageResourceAdapterBean.addSuffix(text);
                    }

                });
    }

    private void declareOperation(OperationBean operationBean) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Initialize imageOperation '" + operationBean.getId()
                    + "', name='" + operationBean.getName() + "', classname='"
                    + operationBean.getClassName() + "'.");
        }

        Class< ? extends IImageOperation> clazz;
        try {
            clazz = ClassLocator.load(operationBean.getClassName(), null,
                    FacesContext.getCurrentInstance(), IImageOperation.class);

        } catch (ClassNotFoundException ex) {
            LOG.error(
                    "Can not load class '" + operationBean.getClassName()
                            + "' specified by imageOperation id='"
                            + operationBean.getId() + "'.", ex);

            return;
        }

        if (IImageOperation.class.isAssignableFrom(clazz) == false) {
            LOG.error(new FacesException("Class '"
                    + operationBean.getClassName()
                    + "' specified by imageOperation id='"
                    + operationBean.getId()
                    + "' must implement interface 'IImageOperation'."));
            return;
        }

        if ((clazz.getModifiers() & Modifier.ABSTRACT) > 0) {
            LOG.error(new FacesException("Class '"
                    + operationBean.getClassName()
                    + "' specified by imageOperation id='"
                    + operationBean.getId() + "' is abstract !"));
            return;
        }

        Constructor< ? extends IImageOperation> constructor;

        try {
            constructor = clazz.getConstructor((Class[]) null);

        } catch (NoSuchMethodException ex) {
            LOG.error(
                    "Can not get constructor for imageOperation id='"
                            + operationBean.getId() + "' class='"
                            + operationBean.getClassName() + "'.", ex);
            return;
        }

        IImageOperation operation;
        try {
            operation = constructor.newInstance((Object[]) null);

        } catch (Throwable ex) {
            LOG.error(
                    "Can not instanciate class '"
                            + operationBean.getClassName()
                            + "' specified by imageOperation id='"
                            + operationBean.getId() + "' using constructor '"
                            + constructor + "'.", ex);
            return;
        }

        try {
            if (operationBean.getName() != null) {
                operation.setName(operationBean.getName());
            }

            if (operationBean.getResponseMimeType() != null) {
                operation.setResponseMimeType(operationBean
                        .getResponseMimeType());
            }

            if (operationBean.getSourceMimeType() != null) {
                operation.setSourceMimeType(operationBean.getSourceMimeType());
            }

            if (operationBean.getEncoderMimeType() != null) {
                operation
                        .setEncoderMimeType(operationBean.getEncoderMimeType());
            }

            if (operationBean.getForceSuffix() != null) {
                operation.setResponseSuffix(operationBean.getForceSuffix());
            }

            LOG.trace("addImageOperation(" + operationBean.getId() + ","
                    + operation + ")");

            operation.configure(operationBean.getParameterMap());

        } catch (Throwable ex) {
            LOG.error(
                    "Can not configure bean, class '"
                            + operationBean.getClassName()
                            + "' specified by imageOperation id='"
                            + operationBean.getId() + "' using constructor '"
                            + constructor + "'.", ex);
            return;
        }

        operationsById.put(operationBean.getId(), operation);
    }

    @Override
    public IImageOperation getImageOperation(String operationId) {
        IImageOperation imageOperation = operationsById.get(operationId);

        if (LOG.isDebugEnabled()) {
            LOG.debug("Operation id='" + operationId + "' => " + imageOperation);
        }

        return imageOperation;
    }

    @Override
    public IContentAccessor formatImageURL(FacesContext facesContext,
            IContentAccessor contentAccessor,
            IGeneratedImageInformation generatedImageInformation,
            IGenerationResourceInformation generationInformation) {

        String operationId = null;
        IImageOperation imageOperation = null;
        String parameters = null;

        if (contentAccessor instanceof IFiltredContentAccessor) {
            IFiltredContentAccessor filtredContentAccessor = (IFiltredContentAccessor) contentAccessor;
            operationId = filtredContentAccessor.getFilter();

            int pf = operationId.indexOf('(');
            if (pf >= 0) {
                int pfe = operationId.lastIndexOf(')');
                if (pfe < 0) {
                    parameters = operationId.substring(pf + 1);
                } else {
                    parameters = operationId.substring(pf + 1, pfe);
                }

                operationId = operationId.substring(0, pf);
            }

            imageOperation = getImageOperation(operationId);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Image operation id='" + operationId + "' filter='"
                        + filtredContentAccessor.getFilter() + "' => "
                        + imageOperation);
            }
        }

        String resourceURL = (String) contentAccessor.getContentRef();
        int resourcePathType = contentAccessor.getPathType();

        if (resourceURL == null) {
            IContentAccessor rootAccessor = contentAccessor.getParentAccessor();
            resourceURL = (String) rootAccessor.getContentRef();
            resourcePathType = rootAccessor.getPathType();
        }

        String sourceContentType = getMimeType(resourceURL);
        if (sourceContentType == null) {
            if (LOG.isInfoEnabled()) {
                LOG.info("Not supported content type '" + sourceContentType
                        + "' for url '" + contentAccessor + "'.");
            }
            return null;
        }

        int validation = getValidContenType(sourceContentType);

        if (validation == NO_VALIDATION) {
            if (LOG.isInfoEnabled()) {
                LOG.info("Not supported content type '" + sourceContentType
                        + "' for url '" + contentAccessor + "'.");
            }
            return null;
        }

        if (validation == RESOURCE_ADAPTER_VALIDATION) {
            if (operationId == null) {
                operationId = NO_OPERATION_ID;
            }
        }

        if (operationId == null) {
            return null;
        }

        generatedImageInformation.setSourceMimeType(sourceContentType);
        generationInformation.setAttribute(
                IGenerationResourceInformation.SOURCE_URL, resourceURL);

        IContentStorageEngine contentStorageEngine = rcfacesContext
                .getContentStorageEngine();

        // Il nous faut un path en relatif !
        switch (resourcePathType) {
        case IContentPath.EXTERNAL_PATH_TYPE:
            throw new FacesException(
                    "Can not make operation on an external URL !");

        case IContentPath.CONTEXT_PATH_TYPE:
            break;

        case IContentPath.ABSOLUTE_PATH_TYPE:
            String relativeURL = PathTypeTools
                    .convertAbsolutePathToContextType(facesContext, resourceURL);

            if (relativeURL == null) {
                throw new FacesException(
                        "Can not transform Absolute path to Context path !");
            }

            resourceURL = relativeURL;
            break;

        case IContentPath.RELATIVE_PATH_TYPE:
            resourceURL = PathTypeTools.convertRelativePathToContextPath(
                    facesContext, resourceURL, null);
            break;

        default:
            throw new FacesException("Invalid state !");
        }

        String versionId = null;
        if (Constants.RESOURCE_CONTENT_VERSION_SUPPORT) {
            IResourceVersionHandler resourceVersionHandler = rcfacesContext
                    .getResourceVersionHandler();
            if (resourceVersionHandler != null) {
                versionId = resourceVersionHandler.getResourceVersion(
                        facesContext, resourceURL, null);
            }
        }

        String specifiedResourceKey = null;
        if (generationInformation
                .getComputeResourceKeyFromGenerationInformation()) {
            specifiedResourceKey = BasicGenerationResourceInformation
                    .generateResourceKeyFromGenerationInformation(generationInformation);
        }

        ImageOperationContentModel imageOperationContentModel = new ImageOperationContentModel(
                resourceURL, versionId, operationId, parameters,
                imageOperation, specifiedResourceKey);

        if (imageOperation != null) {
            imageOperation.prepare(imageOperationContentModel,
                    generationInformation, generatedImageInformation);
        }

        IContentAccessor newContentAccessor = contentStorageEngine
                .registerContentModel(facesContext, imageOperationContentModel,
                        generatedImageInformation, generationInformation);

        // pas de versionning dans ce content Accessor !
        IResourceProxyHandler resourceProxyHandler = rcfacesContext
                .getResourceProxyHandler();
        if (resourceProxyHandler != null && resourceProxyHandler.isEnabled()
                && resourceProxyHandler.isFiltredResourcesEnabled()) {

            newContentAccessor.setContentProxyHandler(contentAccessor
                    .getContentProxyHandler());
        }

        return newContentAccessor;
    }

    @Override
    public String getMimeType(String url) {
        int idx = url.lastIndexOf('/');
        if (idx >= 0) {
            url = url.substring(idx + 1);
        }

        String mimeType = fileNameMap.getContentTypeFor(url);
        if (mimeType != null) {
            return mimeType;
        }

        if (imageResourceAdapters == null) {
            return null;
        }

        idx = url.lastIndexOf('.');
        if (idx < 0) {
            return null;
        }

        url = url.substring(idx + 1).toLowerCase();

        for (int i = 0; i < imageResourceAdapters.length; i++) {
            ImageResourceAdapterBean imageResourceAdapterBean = imageResourceAdapters[i];

            if (imageResourceAdapterBean.suffixes.contains(url) == false) {
                continue;
            }

            if (imageResourceAdapterBean.mainMimeType != null) {
                return imageResourceAdapterBean.mainMimeType;
            }
        }

        return null;
    }

    @Override
    public boolean isProviderEnabled() {
        return contentAccessorAvailable;
    }

    @Override
    public int getValidContenType(String contentType) {
        IImageResourceAdapter imageResourceAdapters[] = listImageResourceAdapters(
                contentType, null);
        if (imageResourceAdapters != null && imageResourceAdapters.length > 0) {
            return RESOURCE_ADAPTER_VALIDATION;
        }

        Boolean valid;
        synchronized (validContentTypes) {
            valid = validContentTypes.get(contentType);

            if (valid == null) {
                Iterator<ImageWriter> it = ImageIO
                        .getImageWritersByMIMEType(contentType);
                valid = (it.hasNext()) ? Boolean.TRUE : Boolean.FALSE;

                validContentTypes.put(contentType, valid);
            }
        }

        if (valid.booleanValue()) {
            return WRITER_VALIDATION;
        }

        return NO_VALIDATION;
    }

    @Override
    protected boolean isOperationSupported(String operationId,
            IContentAccessor imageContentAccessor) {

        Object ref = imageContentAccessor.getContentRef();
        if (ref instanceof String) {
            String mimeType = getMimeType((String) ref);

            if (GIFConversionImageOperation.MIME_TYPE.equals(mimeType)) {
                if (gifWriterEnabled != null) {
                    return gifWriterEnabled.booleanValue();
                }
            }
        }

        return true;
    }

    protected void addImageResourceAdapter(
            ImageResourceAdapterBean imageResourceAdapterBean) {

        imageResourceAdapterBean.resolveInstance();

        imageResourceAdaptersList.add(imageResourceAdapterBean);
    }

    @Override
    public IImageResourceAdapter[] listImageResourceAdapters(
            String contentType, String suffix) {

        if (imageResourceAdapters == null) {
            return IMAGE_RESOURCE_ADAPTER_EMPTY_ARRAY;
        }

        List<IImageResourceAdapter> ret = null;

        for (int i = 0; i < imageResourceAdapters.length; i++) {
            ImageResourceAdapterBean imageResourceAdapterBean = imageResourceAdapters[i];

            if (imageResourceAdapterBean.isSupported(contentType, suffix) == false) {
                continue;
            }

            IImageResourceAdapter imageResourceAdapter = imageResourceAdapterBean
                    .getInstance();

            if (imageResourceAdapter.isContentSupported(contentType, suffix) == false) {
                continue;
            }

            if (ret == null) {
                ret = new ArrayList<IImageResourceAdapter>();
            }

            ret.add(imageResourceAdapter);
        }

        if (ret == null) {
            return IMAGE_RESOURCE_ADAPTER_EMPTY_ARRAY;
        }

        return ret.toArray(new IImageResourceAdapter[ret.size()]);
    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.2 $ $Date: 2013/11/14 10:55:22 $
     */
    public static final class OperationBean {

        private String name;

        private String id;

        private String className;

        private String forceSuffix;

        private String responseMimeType;

        private String sourceMimeType;

        private String encoderMimeType;

        private Map<String, Object> parameters = new HashMap<String, Object>();

        public String getClassName() {
            return className;
        }

        public Map<String, Object> getParameterMap() {
            return parameters;
        }

        public void setClassName(String className) {
            this.className = className;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getResponseMimeType() {
            return responseMimeType;
        }

        public void setResponseMimeType(String forceResponseContentType) {
            this.responseMimeType = forceResponseContentType;
        }

        public String getSourceMimeType() {
            return sourceMimeType;
        }

        public void setSourceMimeType(String forceContentType) {
            this.sourceMimeType = forceContentType;
        }

        public final String getEncoderMimeType() {
            return encoderMimeType;
        }

        public final void setEncoderMimeType(String encoderMimeType) {
            this.encoderMimeType = encoderMimeType;
        }

        public String getForceSuffix() {
            return forceSuffix;
        }

        public void setForceSuffix(String forceSuffix) {
            this.forceSuffix = forceSuffix;
        }

        public void addParameter(String name, String value) {
            parameters.put(name, value);
        }
    }

    public static final class ImageResourceAdapterBean {
        private String id;

        private String name;

        private String className;

        private Set<String> contentTypes;

        private Set<String> suffixes;

        private IImageResourceAdapter imageResourceAdapter;

        private boolean allResource;

        private String mainMimeType;

        public final String getId() {
            return id;
        }

        public IImageResourceAdapter getInstance() {
            return imageResourceAdapter;
        }

        public final void setId(String id) {
            this.id = id;
        }

        public final String getName() {
            return name;
        }

        public final void setName(String name) {
            this.name = name;
        }

        public final String getClassName() {
            return className;
        }

        public final void setClassName(String className) {
            this.className = className;
        }

        protected void resolveInstance() {
            Class< ? extends IImageResourceAdapter> clazz;
            try {
                clazz = ClassLocator.load(className, null,
                        FacesContext.getCurrentInstance(),
                        IImageResourceAdapter.class);

            } catch (Exception ex) {
                throw new FacesException("Can not load class '" + className
                        + "' specified by imageAdapter id='" + getId() + "'.",
                        ex);
            }

            if ((clazz.getModifiers() & Modifier.ABSTRACT) > 0) {
                throw new FacesException("Class '" + getClassName()
                        + "' specified by imageAdapter id='" + getId()
                        + "' is abstract !");
            }

            try {
                imageResourceAdapter = clazz.newInstance();

            } catch (Exception ex) {
                throw new FacesException("Can not load class '" + className
                        + "' specified by imageAdapter id='" + getId() + "'.",
                        ex);
            }
        }

        public void addSuffix(String suffix) {

            if ("*".equals(suffixes)) {
                allResource = true;
                return;
            }

            if (suffixes == null) {
                suffixes = new HashSet<String>();
            }

            suffixes.add(suffix.toLowerCase().trim());
        }

        public void addContentType(String contentType) {

            if ("*/*".equals(contentType)) {
                allResource = true;
                return;
            }

            contentType = contentType.toLowerCase().trim();

            if (mainMimeType == null) {
                mainMimeType = contentType;
            }

            if (contentTypes == null) {
                contentTypes = new HashSet<String>();
            }

            contentTypes.add(contentType);
        }

        public boolean isSupported(String contentType, String suffix) {
            if (allResource) {
                return true;
            }

            if (contentTypes != null && contentType != null
                    && contentTypes.contains(contentType.toLowerCase())) {
                return true;
            }

            if (suffixes != null && suffix != null
                    && suffixes.contains(suffix.toLowerCase())) {
                return true;
            }

            return false;
        }
    }
}
