/*
 * $Id: AbstractJavaScriptRenderContext.java,v 1.4 2013/11/13 12:53:28 jbmeslin Exp $
 * 
 */
package org.rcfaces.renderkit.html.internal;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.faces.FacesException;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.component.capability.IClientDataCapability;
import org.rcfaces.core.internal.lang.StringAppender;
import org.rcfaces.core.internal.renderkit.IComponentRenderContext;
import org.rcfaces.core.internal.renderkit.WriterException;
import org.rcfaces.core.internal.repository.IRepository;
import org.rcfaces.core.internal.repository.IRepository.ICriteria;
import org.rcfaces.core.internal.repository.IRepository.IFile;
import org.rcfaces.core.internal.repository.LocaleCriteria;
import org.rcfaces.core.internal.service.log.LogService;
import org.rcfaces.core.internal.tools.ContextTools;
import org.rcfaces.core.lang.IClientStorage;
import org.rcfaces.core.util.ClientStorageManager;
import org.rcfaces.renderkit.html.internal.javascript.IJavaScriptRepository;
import org.rcfaces.renderkit.html.internal.javascript.IJavaScriptRepository.IClass;
import org.rcfaces.renderkit.html.internal.javascript.JavaScriptRepositoryServlet;
import org.rcfaces.renderkit.html.internal.renderer.MessagesRepository;
import org.rcfaces.renderkit.html.internal.service.LogHtmlService;
import org.rcfaces.renderkit.html.internal.util.JavaScriptTools;
import org.rcfaces.renderkit.html.timing.PerformanceTimingProvider;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.4 $ $Date: 2013/11/13 12:53:28 $
 */
public abstract class AbstractJavaScriptRenderContext implements
        IJavaScriptRenderContext {

    private static final Log LOG = LogFactory
            .getLog(AbstractJavaScriptRenderContext.class);

    protected static final Log LOG_INTERMEDIATE_PROFILING = LogFactory
            .getLog("org.rcfaces.html.javascript.LOG_INTERMEDIATE_PROFILING");

    private static final String LAZY_TAG_USES_BROTHER_PARAMETER = Constants
            .getPackagePrefix() + ".LAZY_TAG_USES_BROTHER";

    private static final String ENABLE_LOG_CLIENT_PARAMETER = Constants
            .getPackagePrefix() + ".client.ENABLE_LOG";

    private static final String ENABLE_SCRIPT_VERIFY_PARAMETER = Constants
            .getPackagePrefix() + ".client.SCRIPT_VERIFY";

    private static final String CLEAN_UP_ON_SUBMIT_PARAMETER = Constants
            .getPackagePrefix() + ".client.CLEAN_UP_ON_SUBMIT";

    private static final String JAVASCRIPT_WRITER = "camelia.html.javascript.writer";

    private static final String VARIABLES_POOL_PROPERTY = "org.rcfaces.renderkit.html.POOL_PROPERTY";

    private static final String JAVASCRIPT_INITIALIZED_PROPERTY = "org.rcfaces.renderkit.html.JAVASCRIPT_INITIALIZED";

    private static final boolean USE_VARIABLE_CACHE = true;

    private static final IFile[] FILE_EMPTY_ARRAY = new IFile[0];

    private static final int STRINGS_INITIAL_SIZE = 64;

    private static final int COMPONENTS_INITIAL_SIZE = 16;

    private static final String SCRIPT_VERIFY = "try { f_core; } catch(x) { alert(\"RCFaces Javascript Components are not initialized properly !\"); }";

    private static final int DECLARED_CLASSES_INIT_SIZE = 512;

    protected final AbstractJavaScriptRenderContext parent;

    private final IJavaScriptRepository repository;

    protected final Set<Object> waitingRequiredClassesNames = new HashSet<Object>(
            DECLARED_CLASSES_INIT_SIZE);

    private final Set<String> declaredClasses;

    private VariablePool variablePool;

    private int varCounter = 1;

    private IRepository.IContext declaredFiles;

    private IFile[] filesToRequire = FILE_EMPTY_ARRAY;

    private boolean symbolsInitialized = false;

    private Map symbols;

    private boolean initialized = false;

    private MessagesRepository facesMessagesRepository;

    private Map<String, String> strings;

    private Map<String, String> componentIds;

    private Locale userLocale;

    private boolean javaScriptStubForced;

    private boolean lazyTagUsesBrother = Constants.LAZY_TAG_USES_BROTHER_DEFAULT_VALUE;

    private boolean transientState;

    private ICriteria criteria;

    public AbstractJavaScriptRenderContext(FacesContext facesContext) {
        parent = null;

        repository = JavaScriptRepositoryServlet.getRepository(facesContext);
        declaredFiles = JavaScriptRepositoryServlet
                .getContextRepository(facesContext);
        declaredClasses = new HashSet<String>(DECLARED_CLASSES_INIT_SIZE);

        Map<String, Object> map = facesContext.getExternalContext()
                .getApplicationMap();
        synchronized (facesContext.getApplication()) {
            variablePool = (VariablePool) map.get(VARIABLES_POOL_PROPERTY);
            if (variablePool == null) {
                variablePool = new VariablePool(facesContext);

                map.put(VARIABLES_POOL_PROPERTY, variablePool);
            }
        }

        String param = facesContext.getExternalContext().getInitParameter(
                LAZY_TAG_USES_BROTHER_PARAMETER);
        if (param != null) {
            if ("false".equalsIgnoreCase(param)) {
                lazyTagUsesBrother = false;

            } else if ("true".equalsIgnoreCase(param)) {
                lazyTagUsesBrother = true;
            }

            if (LOG.isDebugEnabled()) {
                LOG.debug("Set lazyTagUsesBrother=" + lazyTagUsesBrother);
            }
        }

        IRepository.IContext repositoryContext = JavaScriptRepositoryServlet
                .getContextRepository(facesContext);

        criteria = repositoryContext.getCriteria();
        userLocale = LocaleCriteria.getLocale(criteria);
    }

    protected AbstractJavaScriptRenderContext(
            AbstractJavaScriptRenderContext parent) {
        this.parent = parent;
        this.repository = parent.repository;
        this.declaredFiles = parent.declaredFiles.copy();
        this.declaredClasses = new HashSet<String>(parent.declaredClasses);
        this.initialized = parent.initialized;

        this.filesToRequire = parent.filesToRequire;
        isRequiresPending(); // Ca calcule !
    }

    public void computeRequires(IHtmlWriter writer,
            IJavaScriptComponentRenderer renderer) {

        // On recupere les fichiers à inclure ...
        renderer.addRequiredJavaScriptClassNames(writer, this);
    }

    protected IFile[] computeFilesToRequire() {
        if (waitingRequiredClassesNames.isEmpty()) {
            return FILE_EMPTY_ARRAY;
        }

        // Ok on recherche les fichiers à inclure
        IFile filesToRequire[] = repository.computeFiles(
                waitingRequiredClassesNames,
                IJavaScriptRepository.FILE_COLLECTION_TYPE, declaredFiles);

        // Le declaredFiles est altéré !
        waitingRequiredClassesNames.clear();

        return filesToRequire;
    }

    public void appendRequiredClass(String className, String requiredId) {
        if (LOG.isTraceEnabled()) {
            LOG.trace("Append required class '" + className + "' requiredId='"
                    + requiredId + "'.");
        }

        if (requiredId == null) {
            if (declaredClasses.add(className) == false) {
                return;
            }
        } else {
            String key = className + "$" + requiredId;

            if (declaredClasses.add(key) == false) {
                return;
            }
            declaredClasses.add(className);
        }

        IClass clazz = repository.getClassByName(className);
        if (clazz == null) {
            LOG.error("appendRequiredClasses: Unknown class '" + className
                    + "'.");
            return;
        }

        waitingRequiredClassesNames.add(clazz.getFile());

        IClass requiredClasses[] = clazz.listRequiredClasses(requiredId);
        if (requiredClasses == null || requiredClasses.length < 1) {
            if (LOG.isTraceEnabled()) {
                LOG.trace("Append required class '" + className
                        + "' requiredId='" + requiredId
                        + "' => nothing required");
            }

            return;
        }

        boolean trace = LOG.isTraceEnabled();
        for (int i = 0; i < requiredClasses.length; i++) {
            if (trace) {
                LOG.trace("Append required class '" + className
                        + "' requiredId='" + requiredId + "' => add '"
                        + requiredClasses[i].getName() + "'");
            }

            waitingRequiredClassesNames.add(requiredClasses[i].getFile());
            // addWaitingRequiredClassName(requiredClasses[i]);
        }
    }

    /*
     * public void addWaitingRequiredClassName(IClass clazz) {
     * waitingRequiredClassesNames.add(clazz.getFile()); }
     */
    public void appendRequiredFiles(IFile[] files) {
        if (LOG.isTraceEnabled()) {
            for (int i = 0; i < files.length; i++) {
                LOG.trace("Append required files '" + files[i] + "'");
            }
        }

        waitingRequiredClassesNames.addAll(Arrays.asList(files));
    }

    public IRepository.IFile[] popRequiredFiles() {
        if (isRequiresPending() == false) {
            return FILE_EMPTY_ARRAY;
        }

        IRepository.IFile old[] = filesToRequire;

        filesToRequire = FILE_EMPTY_ARRAY;

        return old;
    }

    public boolean isRequiresPending() {
        if (filesToRequire.length > 0) {
            return true;
        }

        filesToRequire = computeFilesToRequire();

        return filesToRequire.length > 0;
    }

    public String allocateVarName() {
        if (parent != null) {
            return parent.allocateVarName();
        }

        return variablePool.getVarName(varCounter++);
    }

    public boolean canLazyTagUsesBrother() {
        if (parent != null) {
            return parent.canLazyTagUsesBrother();
        }

        return lazyTagUsesBrother;
    }

    public String convertSymbol(String className, String memberName) {
        if (symbolsInitialized == false) {
            symbolsInitialized = true;

            if (parent != null) {
                String converted = parent.convertSymbol(className, memberName);
                symbols = parent.symbols;
                return converted;
            }

            FacesContext facesContext = FacesContext.getCurrentInstance();
            // FacesContext est forcement initialisé ici !
            symbols = JavaScriptRepositoryServlet.getSymbols(facesContext);
        }

        if (symbols == null) {
            return memberName;
        }

        String converted;
        if (className != null && className.startsWith("f")) {
            StringAppender sa = new StringAppender(className.length() + 1
                    + memberName.length());
            sa.append(className);
            sa.append(".");
            sa.append(memberName);

            converted = (String) symbols.get(sa.toString());
            if (converted != null) {
                return converted;
            }
        }

        converted = (String) symbols.get(memberName);

        if (converted != null) {
            return converted;
        }

        return memberName;
    }

    public boolean isInitialized() {
        return initialized;
    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.4 $ $Date: 2013/11/13 12:53:28 $
     */
    private static final class VariablePool implements Serializable {

        private static final String REVISION = "$Revision: 1.4 $";

        private static final long serialVersionUID = -3122263677480314082L;

        private static final String JAVASCRIPT_VARIABLE_PREFIX_PARAMETER = Constants
                .getPackagePrefix() + ".JAVASCRIPT_VARIABLE_PREFIX";

        private static final String DEFAULT_JAVASCRIPT_VARIABLE_PREFIX = "_";

        private static final int MAXIMUM_COMPUTED_VARIABLE = 128;

        private final String jsVariablePrefix;

        private final String variables[];

        private int variableCount = 0;

        public VariablePool(FacesContext facesContext) {
            String prefix = facesContext.getExternalContext().getInitParameter(
                    JAVASCRIPT_VARIABLE_PREFIX_PARAMETER);

            if (prefix == null || prefix.length() < 1) {
                prefix = DEFAULT_JAVASCRIPT_VARIABLE_PREFIX;
            }

            if (Character.isJavaIdentifierStart(prefix.charAt(0)) == false) {
                throw new FacesException(
                        "Invalid javascript variable prefix ! ('" + prefix
                                + "')");
            }

            this.jsVariablePrefix = prefix;

            if (USE_VARIABLE_CACHE) {
                this.variables = new String[MAXIMUM_COMPUTED_VARIABLE];

                for (int i = 0; i < variables.length; i++) {
                    variables[i] = jsVariablePrefix + i;
                }
            }
        }

        @SuppressWarnings("unused")
        public String getVarName(int idx) {
            if (USE_VARIABLE_CACHE == false) {
                return jsVariablePrefix + idx;
            }

            if (idx >= variables.length) {
                return jsVariablePrefix + idx;
            }

            return variables[idx];
        }
    }

    protected MessagesRepository getMessagesRepository(boolean create) {
        if (facesMessagesRepository != null) {
            return facesMessagesRepository;
        }

        if (parent == null) {
            if (create == false) {
                return null;
            }

            facesMessagesRepository = new MessagesRepository(this);
            return facesMessagesRepository;
        }

        MessagesRepository mp = parent.getMessagesRepository(create);
        if (create) {
            facesMessagesRepository = new MessagesRepository(this);
            return facesMessagesRepository;
        }

        return mp;
    }

    public String allocateFacesMessage(FacesMessage message,
            boolean mustDeclare[]) {
        MessagesRepository mr = getMessagesRepository(true);

        return mr.allocateFacesMessage(message, mustDeclare);
    }

    public String allocateString(String text, boolean mustDeclare[]) {
        return allocateString(text, mustDeclare, true);
    }

    protected String allocateString(String text, boolean mustDeclare[],
            boolean allocate) {
        String key;

        if (parent != null) {
            key = parent.allocateString(text, null, false);
            if (key != null) {
                return key;
            }
        }

        if (strings == null) {
            if (allocate == false) {
                return null;
            }

            strings = new HashMap<String, String>(STRINGS_INITIAL_SIZE);

        } else {
            key = strings.get(text);
            if (key != null || allocate == false) {
                return key;
            }
        }

        key = allocateVarName();

        strings.put(text, key);

        mustDeclare[0] = true;

        return key;
    }

    public String allocateComponentVarId(String text, boolean mustDeclare[]) {
        return allocateComponentVarId(text, mustDeclare, true);
    }

    protected String allocateComponentVarId(String text, boolean mustDeclare[],
            boolean allocate) {
        String key;

        if (parent != null) {
            key = parent.allocateComponentVarId(text, null, false);
            if (key != null) {
                return key;
            }
        }

        if (componentIds == null) {
            if (allocate == false) {
                return null;
            }

            componentIds = new HashMap<String, String>(COMPONENTS_INITIAL_SIZE);

        } else {
            key = componentIds.get(text);
            if (key != null || allocate == false) {
                return key;
            }
        }

        key = allocateVarName();

        componentIds.put(text, key);

        mustDeclare[0] = true;

        return key;
    }

    public void initializeJavaScriptDocument(IJavaScriptWriter writer)
            throws WriterException {

        if (initialized) {
            return;
        }

        initialized = true;

        initializeJavaScript(writer, repository, true);
    }

    public void restoreState(FacesContext facesContext, Object state) {
        Object ret[] = (Object[]) state;

        initialized = ((Boolean) ret[0]).booleanValue();
        declaredFiles.restoreState(facesContext, repository, ret[1]);
    }

    public Object saveState(FacesContext facesContext) {
        initialized = true; // Il est forcement initialisé !

        return new Object[] { Boolean.valueOf(initialized),
                declaredFiles.saveState(facesContext) };
    }

    public final boolean isTransient() {
        return transientState;
    }

    public final void setTransient(boolean newTransientValue) {
        transientState = newTransientValue;
    }

    public final ICriteria getCriteria() {
        if (parent != null) {
            return parent.getCriteria();
        }
        return criteria;
    }

    public final Locale getUserLocale() {
        if (parent != null) {
            return parent.getUserLocale();
        }
        return userLocale;
    }

    public final IJavaScriptRepository getRepository() {
        return repository;
    }

    protected static boolean isJavaScriptInitialized(FacesContext facesContext) {
        ExternalContext externalContext = facesContext.getExternalContext();

        Map requestMap = externalContext.getRequestMap();
        return requestMap.containsKey(JAVASCRIPT_INITIALIZED_PROPERTY);
    }

    public static void initializeJavaScript(IJavaScriptWriter writer,
            IJavaScriptRepository repository, boolean generateMessages)
            throws WriterException {

        FacesContext facesContext = writer.getFacesContext();
        ExternalContext externalContext = facesContext.getExternalContext();

        Map<String, Object> requestMap = externalContext.getRequestMap();
        if (requestMap.containsKey(JAVASCRIPT_INITIALIZED_PROPERTY)) {
            return;
        }

        LOG.debug("Initializing javascript.");

        requestMap.put(JAVASCRIPT_INITIALIZED_PROPERTY, Boolean.TRUE);

        Map initParameter = externalContext.getInitParameterMap();
        if ("false".equalsIgnoreCase((String) initParameter
                .get(ENABLE_SCRIPT_VERIFY_PARAMETER)) == false) {
            writer.writeln(SCRIPT_VERIFY);
        }

        if (LOG_INTERMEDIATE_PROFILING.isTraceEnabled()) {
            writer.writeCall("f_core", "Profile").writeln(
                    "false,\"javascript.initialize\");");
        }

        IHtmlRenderContext htmlRenderContext = writer.getHtmlRenderContext();

        IHtmlProcessContext processContext = htmlRenderContext
                .getHtmlProcessContext();

        /*
         * Boolean debugMode = processContext.getDebugMode(); if (debugMode !=
         * null) { writer.writeCall("f_core", "SetDebugMode");
         * 
         * if (debugMode.booleanValue() == false) { writer.writeBoolean(false);
         * }
         * 
         * writer.writeln(");"); }
         */

        String cleanUpOnSubmitParameter = externalContext
                .getInitParameter(CLEAN_UP_ON_SUBMIT_PARAMETER);
        if (cleanUpOnSubmitParameter != null) {
            cleanUpOnSubmitParameter = cleanUpOnSubmitParameter.trim()
                    .toLowerCase();

            writer.writeCall("f_core", "SetCleanUpOnSubmit");
            if ("true".equals(cleanUpOnSubmitParameter)) {
                writer.writeBoolean(true);

            } else if ("false".equals(cleanUpOnSubmitParameter)) {
                writer.writeBoolean(false);
            }

            writer.writeln(");");
        }

        Boolean profilerMode = processContext.getProfilerMode();
        if (profilerMode != null) {
            writer.writeCall("f_core", "SetProfilerMode");

            if (profilerMode.booleanValue() == false) {
                writer.writeBoolean(false);
            }

            writer.writeln(");");
        }

        boolean designerMode = processContext.isDesignerMode();
        if (designerMode) {
            writer.writeCall("f_core", "SetDesignerMode").writeln(");");
        }

        String invalidBrowserURL = htmlRenderContext.getInvalidBrowserURL();
        if (invalidBrowserURL != null) {
            writer.writeCall("f_core", "VerifyBrowserCompatibility")
                    .writeString(invalidBrowserURL).writeln(");");
        }

        if (htmlRenderContext.isDisabledContextMenu()) {
            writer.writeCall("f_core", "DisableContextMenu").writeln(");");
        }

        if (htmlRenderContext.isClientValidation() == false) {
            writer.writeCall("f_env", "DisableClientValidation").writeln(");");
        }

        Set<String> systemNames = processContext.getSystemParametersNames();
        if (systemNames != null && systemNames.isEmpty() == false) {
            writer.writeCall("f_env", "SetSystemParameterNames");

            boolean first = true;
            for (String name : systemNames) {
                if (first) {
                    first = false;
                } else {
                    writer.write(',');
                }

                writer.writeString(name);
            }

            writer.writeln(");");
        }

        if (htmlRenderContext.isClientValidation() == false) {
            writer.writeCall("f_env", "DisableClientValidation").writeln(");");
        }

        boolean flatIdentifier = processContext.isFlatIdentifierEnabled();
        if (flatIdentifier) {
            writer.writeCall("fa_namingContainer", "SetSeparator").writeln(
                    "false);");

        } else {
            if (Constants.CLIENT_NAMING_SEPARATOR_SUPPORT) {
                String separator = processContext.getNamingSeparator();

                if (separator != null) {
                    writer.writeCall("fa_namingContainer", "SetSeparator")
                            .writeString(String.valueOf(separator))
                            .writeln(");");
                }
            }
        }

        String baseURI = externalContext.getRequestContextPath();
        writer.writeCall("f_env", "Initialize").writeString(baseURI);

        int pred = 0;

        String requestURI = getRequestURL(facesContext,
                facesContext.getViewRoot());
        if (requestURI != null) {
            for (; pred > 0; pred--) {
                writer.write(',').writeNull();
            }
            writer.write(',').writeString(requestURI);
        } else {
            pred++;
        }

        Locale locale = ContextTools.getUserLocale(facesContext);
        if (locale != null) {
            for (; pred > 0; pred--) {
                writer.write(',').writeNull();
            }
            writer.write(",\"").write(locale.getLanguage());

            String country = locale.getCountry();
            if (country != null && country.length() > 0) {
                writer.write('_').write(country);

                String variant = locale.getLanguage();
                if (variant != null && variant.length() > 0) {
                    writer.write('_').write(variant);
                }
            }

            writer.write('\"');

        } else {
            pred++;
        }

        String jsBaseURI = repository.getBaseURI(processContext);
        if (jsBaseURI != null) {
            for (; pred > 0; pred--) {
                writer.write(',').writeNull();
            }

            writer.write(',').writeString(jsBaseURI);
        } else {
            pred++;
        }

        String styleSheetURI = htmlRenderContext.getHtmlProcessContext()
                .getStyleSheetURI(null, true);
        if (styleSheetURI != null && styleSheetURI.equals(jsBaseURI) == false) {
            for (; pred > 0; pred--) {
                writer.write(',').writeNull();
            }

            writer.write(',').writeString(styleSheetURI);
        } else {
            pred++;
        }

        writer.writeln(");");

        PerformanceTimingProvider performanceTimingProvider = PerformanceTimingProvider
                .get(facesContext);
        if (performanceTimingProvider != null) {
            int features = performanceTimingProvider
                    .getClientPerformanceTimingFeatures();
            if (features > 0) {
                writer.writeCall("f_env", "EnablePerformanceTiming")
                        .writeInt(features).writeln(");");
            }
        }

        String logProperty = (String) initParameter
                .get(ENABLE_LOG_CLIENT_PARAMETER);
        if ("true".equalsIgnoreCase(logProperty)) {
            LogService logService = LogHtmlService.getInstance(facesContext);
            if (logService != null) {
                LogService.IFilter filters[] = logService
                        .listFilters(facesContext);
                if (filters.length > 0) {
                    writer.writeCall("f_log", "AddLevels");

                    for (int i = 0; i < filters.length; i++) {
                        LogService.IFilter filter = filters[i];

                        if (i > 0) {
                            writer.write(',');
                        }

                        writer.writeString(filter.getName());
                        writer.write(',');
                        writer.writeInt(filter.getLevel());
                    }

                    writer.writeln(");");
                }
            }
        }

        IClientStorage clientStorage = ClientStorageManager.get(facesContext,
                false);
        if (clientStorage != null) {
            Iterator it = clientStorage.listAttributeNames();

            for (; it.hasNext();) {

            }
        }

        if (generateMessages) {
            writeMessages(writer);
        }

        if (LOG_INTERMEDIATE_PROFILING.isTraceEnabled()) {
            writer.writeCall("f_core", "Profile").writeln(
                    "true,\"javascript.initialize\");");
        }

        LOG.debug("Javascript initialized.");
    }

    protected static boolean hasMessagesPending(
            IHtmlRenderContext htmlRenderContext) {

        Set clientMessageIdFilters = htmlRenderContext
                .getClientMessageIdFilters();

        if (clientMessageIdFilters
                .contains(IHtmlRenderContext.NO_CLIENT_MESSAGES)) {
            return false;
        }

        FacesContext facesContext = htmlRenderContext.getFacesContext();
        Iterator messageClientIds = facesContext.getClientIdsWithMessages();
        for (; messageClientIds.hasNext();) {
            String clientId = (String) messageClientIds.next();

            if (clientMessageIdFilters.isEmpty() == false
                    && clientMessageIdFilters.contains(clientId) == false) {
                continue;
            }

            Iterator it = facesContext.getMessages(clientId);
            if (it.hasNext()) {
                return true;
            }

        }

        return false;
    }

    protected static void writeMessages(IJavaScriptWriter writer)
            throws WriterException {
        IHtmlRenderContext htmlRenderContext = writer.getHtmlRenderContext();

        Set clientMessageIdFilters = htmlRenderContext
                .getClientMessageIdFilters();

        if (clientMessageIdFilters
                .contains(IHtmlRenderContext.NO_CLIENT_MESSAGES)) {
            return;
        }

        Map<FacesMessage, String> messages = null;
        StringAppender sa = null;

        FacesContext facesContext = htmlRenderContext.getFacesContext();
        Iterator messageClientIds = facesContext.getClientIdsWithMessages();
        for (; messageClientIds.hasNext();) {
            String clientId = (String) messageClientIds.next();

            if (clientMessageIdFilters.isEmpty() == false
                    && clientMessageIdFilters.contains(clientId) == false) {
                continue;
            }

            if (messages == null) {
                messages = new HashMap<FacesMessage, String>();
            }

            Iterator it = facesContext.getMessages(clientId);
            for (; it.hasNext();) {
                FacesMessage facesMessage = (FacesMessage) it.next();

                String varName = messages.get(facesMessage);
                if (varName == null) {
                    varName = JavaScriptTools.writeMessage(facesContext,
                            writer, facesMessage);

                    messages.put(facesMessage, varName);

                }
                if (sa == null) {
                    sa = new StringAppender(32);
                }

                if (sa.length() > 0) {
                    sa.append(',');
                }
                sa.append(varName);
            }

            if (sa == null || sa.length() < 1) {
                continue;
            }

            writer.writeCall("f_messageContext", "AppendMessages")
                    .writeString(clientId).write(',').write(sa.toString())
                    .writeln(");");

            sa.setLength(0);
        }
    }

    public void clearJavaScriptStubForced() {
        javaScriptStubForced = false;
    }

    public void forceJavaScriptStub() {
        javaScriptStubForced = true;
    }

    public boolean isJavaScriptStubForced() {
        return javaScriptStubForced;
    }

    protected void encodeClientData(IJavaScriptWriter writer)
            throws WriterException {
        IClientDataCapability clientDataCapability = (IClientDataCapability) writer
                .getHtmlComponentRenderContext().getComponent();

        String keys[] = clientDataCapability.listClientDataKeys();
        if (keys == null || keys.length < 1) {
            return;
        }

        writer.writeMethodCall("f_setClientData");

        for (int i = 0; i < keys.length; i++) {
            String key = keys[i];
            if (key == null || key.length() < 1) {
                continue;
            }

            if (i > 0) {
                writer.write(',');
            }

            writer.writeString(key);

            String value = clientDataCapability.getClientData(key);

            writer.write(',');
            writer.writeString(value);
        }

        writer.writeln(");");
    }

    public final IJavaScriptWriter removeJavaScriptWriter(IHtmlWriter writer) {
        return (IJavaScriptWriter) writer.getComponentRenderContext()
                .removeAttribute(JAVASCRIPT_WRITER);
    }

    public final IJavaScriptWriter getJavaScriptWriter(IHtmlWriter writer,
            IJavaScriptComponentRenderer javaScriptComponent)
            throws WriterException {
        IJavaScriptWriter js = (IJavaScriptWriter) writer
                .getComponentRenderContext().getAttribute(JAVASCRIPT_WRITER);
        if (js != null) {
            return js;
        }

        js = createJavaScriptWriter(writer, javaScriptComponent);

        writer.getComponentRenderContext().setAttribute(JAVASCRIPT_WRITER, js);

        return js;
    }

    protected abstract IJavaScriptWriter createJavaScriptWriter(
            IHtmlWriter writer, IJavaScriptComponentRenderer javaScriptComponent)
            throws WriterException;

    private static String getRequestURL(FacesContext facesContext,
            UIViewRoot viewRoot) {

        ExternalContext externalContext = facesContext.getExternalContext();

        String url = facesContext.getApplication().getViewHandler()
                .getActionURL(facesContext, viewRoot.getViewId());

        url = externalContext.encodeActionURL(url);

        return url;
    }

    protected IJavaScriptWriter writeJsInitComponent(IJavaScriptWriter jsWriter)
            throws WriterException {

        if (jsWriter.isIgnoreComponentInitialization()) {
            return jsWriter;
        }

        IComponentRenderContext componentRenderContext = jsWriter
                .getHtmlComponentRenderContext();

        String componentId = componentRenderContext.getComponentClientId();

        boolean declare[] = new boolean[1];

        String componentVarName = jsWriter.getComponentVarName();
        if (componentVarName == null) {
            componentVarName = allocateComponentVarId(componentId, declare);

            jsWriter.setComponentVarName(componentVarName);
        } else {
            declare[0] = true;
        }

        String cameliaClassLoader = convertSymbol("f_classLoader",
                "_rcfacesClassLoader");

        if (LOG_INTERMEDIATE_PROFILING.isTraceEnabled()) {
            jsWriter.writeCall("f_core", "Profile").writeln(
                    "false,\"javascript.initComponent\");");
        }

        if (declare[0]) {
            jsWriter.write("var ").write(componentVarName);

            jsWriter.write('=').writeCall(cameliaClassLoader, "f_init");
            jsWriter.writeString(componentId);
            jsWriter.writeln(");");

        } else {
            jsWriter.writeCall(cameliaClassLoader, "f_init");
            jsWriter.write(componentVarName).writeln(");");
        }

        if (LOG_INTERMEDIATE_PROFILING.isTraceEnabled()) {
            jsWriter.writeCall("f_core", "Profile").writeln(
                    "true,\"javascript.initComponent\");");
        }

        return jsWriter;
    }

    public boolean isCollectorMode() {
        return false;
    }

}
