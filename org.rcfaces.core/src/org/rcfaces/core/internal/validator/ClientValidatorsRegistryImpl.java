/*
 * $Id: ClientValidatorsRegistryImpl.java,v 1.1 2013/11/13 12:53:26 jbmeslin Exp $
 */
package org.rcfaces.core.internal.validator;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.faces.FacesException;
import javax.faces.application.Application;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.Rule;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.internal.config.AbstractRenderKitRegistryImpl;
import org.rcfaces.core.internal.lang.StringAppender;
import org.rcfaces.core.internal.tools.BindingTools;
import org.rcfaces.core.internal.util.ClassLocator;
import org.rcfaces.core.internal.util.Convertor;
import org.rcfaces.core.internal.validator.impl.RegExpFilter;
import org.rcfaces.core.lang.IParametredConverter;
import org.rcfaces.core.validator.IClientValidatorTask;
import org.rcfaces.core.validator.IParameter;
import org.xml.sax.Attributes;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.1 $ $Date: 2013/11/13 12:53:26 $
 */
public class ClientValidatorsRegistryImpl extends AbstractRenderKitRegistryImpl
        implements IClientValidatorsRegistry {

    private static final Log LOG = LogFactory
            .getLog(ClientValidatorsRegistryImpl.class);

    private static final IParameter[] PARAMETER_EMPTY_ARRAY = new IParameter[0];

    private static final String[] STRING_EMPTY_ARRAY = new String[0];

    public IClientValidatorDescriptor getClientValidatorById(
            FacesContext facesContext, String validatorId, Locale locale,
            TimeZone timeZone) {

        RenderKit renderKit = (RenderKit) getRenderKit(facesContext, null);
        if (renderKit == null) {
            return null;
        }

        return renderKit.getValidatorById(validatorId, locale, timeZone);
    }

    /*
     * public IStringAdapterDescriptor getStringFormatterById( FacesContext
     * facesContext, String formatterId) {
     * 
     * RenderKit renderKit = (RenderKit) getRenderKit(facesContext, null); if
     * (renderKit == null) { return null; }
     * 
     * return renderKit.getStringFormatterById(formatterId); }
     */

    public void configureRules(Digester digester) {

        digester.addRule("rcfaces-config/clientValidators/render-kit",
                new Rule() {

                    @Override
                    public void begin(String namespace, String name,
                            Attributes attributes) throws Exception {

                        String renderKitId = attributes
                                .getValue("render-kit-id");

                        RenderKit renderKit = (RenderKit) allocate(renderKitId);

                        super.digester.push(renderKit);
                    }

                    @Override
                    public void end(String namespace, String name)
                            throws Exception {
                        super.digester.pop();
                    }
                });

        digester.addObjectCreate(
                "rcfaces-config/clientValidators/render-kit/clientValidator",
                ClientValidator.class);
        digester.addSetProperties(
                "rcfaces-config/clientValidators/render-kit/clientValidator",
                "id", "id");
        digester.addSetProperties(
                "rcfaces-config/clientValidators/render-kit/clientValidator",
                "package", "packageName");

        digester.addRule(
                "rcfaces-config/clientValidators/render-kit/clientValidator",
                new Rule() {

                    @Override
                    public void end(String namespace, String name)
                            throws Exception {
                        ClientValidator clientValidator = (ClientValidator) getDigester()
                                .peek();

                        clientValidator.prepare();
                    }

                });

        digester.addRule(
                "rcfaces-config/clientValidators/render-kit/clientValidator/filter",
                new Rule() {

                    @Override
                    public void begin(String namespace, String name,
                            Attributes attributes) throws Exception {
                        ClientValidator clientValidator = (ClientValidator) getDigester()
                                .peek();

                        clientValidator
                                .setFilter(new TaskDescriptor(attributes) {

                                    @Override
                                    protected IClientValidatorTask computeClientValidatorTask(
                                            String serverTaskClassName,
                                            Attributes attributes) {
                                        if (serverTaskClassName == null
                                                && clientTaskExpression != null
                                                && clientTaskExpression
                                                        .length() > 1) {

                                            if (clientTaskExpression
                                                    .startsWith("/")
                                                    && clientTaskExpression
                                                            .endsWith("/")) {

                                                return new RegExpFilter(
                                                        clientTaskExpression
                                                                .substring(
                                                                        1,
                                                                        clientTaskExpression
                                                                                .length() - 1));
                                            }
                                        }
                                        return super
                                                .computeClientValidatorTask(
                                                        serverTaskClassName,
                                                        attributes);
                                    }

                                });
                    }
                });

        digester.addRule(
                "rcfaces-config/clientValidators/render-kit/clientValidator/translator",
                new Rule() {

                    @Override
                    public void begin(String namespace, String name,
                            Attributes attributes) throws Exception {
                        ClientValidator clientValidator = (ClientValidator) getDigester()
                                .peek();

                        clientValidator.setTranslator(new TaskDescriptor(
                                attributes));
                    }
                });

        digester.addRule(
                "rcfaces-config/clientValidators/render-kit/clientValidator/checker",
                new Rule() {

                    @Override
                    public void begin(String namespace, String name,
                            Attributes attributes) throws Exception {
                        ClientValidator clientValidator = (ClientValidator) getDigester()
                                .peek();

                        clientValidator.setChecker(new TaskDescriptor(
                                attributes));
                    }
                });
        digester.addRule(
                "rcfaces-config/clientValidators/render-kit/clientValidator/formatter",
                new Rule() {

                    @Override
                    public void begin(String namespace, String name,
                            Attributes attributes) throws Exception {
                        ClientValidator clientValidator = (ClientValidator) getDigester()
                                .peek();

                        clientValidator.setFormatter(new TaskDescriptor(
                                attributes));
                    }
                });
        digester.addRule(
                "rcfaces-config/clientValidators/render-kit/clientValidator/behavior",
                new Rule() {

                    @Override
                    public void begin(String namespace, String name,
                            Attributes attributes) throws Exception {
                        ClientValidator clientValidator = (ClientValidator) getDigester()
                                .peek();

                        clientValidator.setBehavior(new TaskDescriptor(
                                attributes));
                    }
                });
        digester.addRule(
                "rcfaces-config/clientValidators/render-kit/clientValidator/onerror",
                new Rule() {

                    @Override
                    public void begin(String namespace, String name,
                            Attributes attributes) throws Exception {
                        ClientValidator clientValidator = (ClientValidator) getDigester()
                                .peek();

                        clientValidator.setOnerror(new TaskDescriptor(
                                attributes));
                    }
                });
        digester.addRule(
                "rcfaces-config/clientValidators/render-kit/clientValidator/oncheckerror",
                new Rule() {

                    @Override
                    public void begin(String namespace, String name,
                            Attributes attributes) throws Exception {
                        ClientValidator clientValidator = (ClientValidator) getDigester()
                                .peek();

                        clientValidator.setOncheckerror(new TaskDescriptor(
                                attributes));
                    }
                });
        digester.addRule(
                "rcfaces-config/clientValidators/render-kit/clientValidator/processor",
                new Rule() {

                    @Override
                    public void begin(String namespace, String name,
                            Attributes attributes) throws Exception {
                        ClientValidator clientValidator = (ClientValidator) getDigester()
                                .peek();

                        clientValidator.setProcessor(new TaskDescriptor(
                                attributes));
                    }
                });
        digester.addRule(
                "rcfaces-config/clientValidators/render-kit/clientValidator/required-class",
                new Rule() {

                    @Override
                    public void begin(String namespace, String name,
                            Attributes attributes) throws Exception {
                        ClientValidator clientValidator = (ClientValidator) getDigester()
                                .peek();

                        String className = attributes.getValue("name");
                        if (className == null) {
                            throw new IllegalStateException(
                                    "Name attribute is not defined for required-class element.");
                        }

                        clientValidator.addRequiredClass(className);
                    }
                });

        digester.addSetProperties(
                "rcfaces-config/clientValidators/render-kit/clientValidator/converter",
                "object", "converter");

        digester.addObjectCreate(
                "rcfaces-config/clientValidators/render-kit/clientValidator/parameter",
                Parameter.class);
        digester.addSetProperties(
                "rcfaces-config/clientValidators/render-kit/clientValidator/parameter",
                "name", "name");
        digester.addSetProperties(
                "rcfaces-config/clientValidators/render-kit/clientValidator/parameter",
                "value", "value");
        digester.addSetNext(
                "rcfaces-config/clientValidators/render-kit/clientValidator/parameter",
                "addParameter");

        digester.addObjectCreate(
                "rcfaces-config/clientValidators/render-kit/clientValidator/server-converter",
                ServerConverter.class);
        digester.addSetProperties(
                "rcfaces-config/clientValidators/render-kit/clientValidator/server-converter",
                "id", "id");
        digester.addSetProperties(
                "rcfaces-config/clientValidators/render-kit/clientValidator/server-converter",
                "class", "className");

        digester.addObjectCreate(
                "rcfaces-config/clientValidators/render-kit/clientValidator/server-converter/parameter",
                Parameter.class);
        digester.addSetProperties(
                "rcfaces-config/clientValidators/render-kit/clientValidator/server-converter/parameter",
                "name", "name");
        digester.addSetProperties(
                "rcfaces-config/clientValidators/render-kit/clientValidator/server-converter/parameter",
                "value", "value");
        digester.addSetNext(
                "rcfaces-config/clientValidators/render-kit/clientValidator/server-converter/parameter",
                "addParameter");

        digester.addSetNext(
                "rcfaces-config/clientValidators/render-kit/clientValidator/server-converter",
                "setServerConverter");

        digester.addSetNext(
                "rcfaces-config/clientValidators/render-kit/clientValidator",
                "addclientValidator");

    }

    @Override
    protected AbstractRenderKitRegistryImpl.RenderKit createRenderKit() {
        return new RenderKit();
    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.1 $ $Date: 2013/11/13 12:53:26 $
     */
    public static class RenderKit extends
            AbstractRenderKitRegistryImpl.RenderKit {

        private final Map<String, IClientValidatorDescriptor> clientValidatorsById = new HashMap<String, IClientValidatorDescriptor>(
                128);

        public RenderKit() {
        }

        /*
         * public final IStringAdapterDescriptor getStringFormatterById( String
         * formatterId) { return (IStringAdapterDescriptor) stringAdaptersById
         * .get(formatterId); }
         */

        /*
         * (non-Javadoc)
         * 
         * @see
         * org.rcfaces.core.internal.validator.IDescriptorManager#getValidatorById
         * (java.lang.String)
         */
        public final IClientValidatorDescriptor getValidatorById(
                String validatorId, Locale locale, TimeZone timeZone) {

            StringAppender sa = new StringAppender(validatorId, 24);
            if (locale != null) {
                sa.append('$');
                sa.append(locale.toString());

                if (timeZone != null) {
                    sa.append('$');
                    sa.append(timeZone.toString());
                }

                IClientValidatorDescriptor clientValidatorDescriptor = clientValidatorsById
                        .get(sa.toString());
                if (clientValidatorDescriptor != null) {
                    return clientValidatorDescriptor;
                }

                sa.setLength(0);
                sa.append(validatorId);

                if (timeZone != null) {
                    sa.append('$');
                    sa.append(locale.toString());

                    clientValidatorDescriptor = clientValidatorsById.get(sa
                            .toString());
                    if (clientValidatorDescriptor != null) {
                        return clientValidatorDescriptor;
                    }

                    sa.setLength(0);
                    sa.append(validatorId);
                }
            } else if (timeZone != null) {
                sa.append('$');
                sa.append(timeZone.toString());

                IClientValidatorDescriptor clientValidatorDescriptor = clientValidatorsById
                        .get(sa.toString());
                if (clientValidatorDescriptor != null) {
                    return clientValidatorDescriptor;
                }

                sa.setLength(0);
                sa.append(validatorId);
            }

            IClientValidatorDescriptor clientValidatorDescriptor = clientValidatorsById
                    .get(sa.toString());
            return clientValidatorDescriptor;
        }

        public final void addclientValidator(ClientValidator validator) {
            validator.commitParameters();

            LOG.trace("addclientValidator(" + validator.getId() + ", "
                    + validator + ")");

            clientValidatorsById.put(validator.getId(), validator);
        }
    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.1 $ $Date: 2013/11/13 12:53:26 $
     */
    public static class ClientValidator extends ParametersContainer implements
            IClientValidatorDescriptor {

        private String id;

        private List<String> requiredClassesList;

        private String requiredClasses[] = STRING_EMPTY_ARRAY;

        private ITaskDescriptor filter;

        private ITaskDescriptor translator;

        private ITaskDescriptor checker;

        private ITaskDescriptor formatter;

        private ITaskDescriptor behavior;

        private ITaskDescriptor processor;

        private ITaskDescriptor onerror;

        private ITaskDescriptor oncheckerror;

        private String converter;

        private IServerConverter serverConverter;

        public final ITaskDescriptor getBehaviorTask() {
            return behavior;
        }

        public void addRequiredClass(String className) {
            if (requiredClassesList == null) {
                requiredClassesList = new ArrayList<String>();
            }

            requiredClassesList.add(className);
        }

        public final ITaskDescriptor getCheckerTask() {
            return checker;
        }

        public final ITaskDescriptor getFilterTask() {
            return filter;
        }

        public final ITaskDescriptor getFormatterTask() {
            return formatter;
        }

        public final ITaskDescriptor getOnCheckErrorTask() {
            return oncheckerror;
        }

        public final ITaskDescriptor getOnErrorTask() {
            return onerror;
        }

        public final String[] listRequiredClasses() {
            return requiredClasses;
        }

        public final ITaskDescriptor getTranslatorTask() {
            return translator;
        }

        public ITaskDescriptor getProcessorTask() {
            return processor;
        }

        public void setProcessor(ITaskDescriptor processor) {
            this.processor = processor;
        }

        public final void setBehavior(ITaskDescriptor behavior) {
            this.behavior = behavior;
        }

        public final void setChecker(ITaskDescriptor checker) {
            this.checker = checker;
        }

        public final void setFilter(ITaskDescriptor filter) {
            this.filter = filter;
        }

        public final void setFormatter(ITaskDescriptor formatter) {
            this.formatter = formatter;
        }

        public final void setOncheckerror(ITaskDescriptor oncheckerror) {
            this.oncheckerror = oncheckerror;
        }

        public final void setOnerror(ITaskDescriptor onerror) {
            this.onerror = onerror;
        }

        public final void setRequiredClasses(String requiredClasses[]) {
            this.requiredClasses = requiredClasses;
        }

        public final void setTranslator(ITaskDescriptor translator) {
            this.translator = translator;
        }

        public final String getId() {
            return id;
        }

        public final void setId(String id) {
            this.id = id;
        }

        public final String getConverter() {
            return converter;
        }

        public final void setConverter(String converter) {
            this.converter = converter;
        }

        public final IServerConverter getServerConverter() {
            return serverConverter;
        }

        public final void setServerConverter(ServerConverter serverConverter) {
            serverConverter.commitParameters();

            this.serverConverter = serverConverter;
        }

        public void prepare() {
            if (requiredClassesList != null) {
                requiredClasses = requiredClassesList
                        .toArray(new String[requiredClassesList.size()]);
                requiredClassesList = null;
            }
        }
    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.1 $ $Date: 2013/11/13 12:53:26 $
     */
    public static class TaskDescriptor implements ITaskDescriptor {

        protected final String clientTaskExpression;

        private final IClientValidatorTask clientValidatorTask;

        public TaskDescriptor(Attributes attributes) {
            clientTaskExpression = attributes.getValue("call");

            String serverTaskClassName = attributes.getValue("class");

            clientValidatorTask = computeClientValidatorTask(
                    serverTaskClassName, attributes);
        }

        protected IClientValidatorTask computeClientValidatorTask(
                String serverTaskClassName, Attributes attributes) {

            if (serverTaskClassName != null) {
                LOG.debug("Instanciate filter '" + serverTaskClassName + "'.");

                Class< ? extends IClientValidatorTask> clazz;
                try {
                    clazz = ClassLocator.load(serverTaskClassName, null,
                            FacesContext.getCurrentInstance(),
                            IClientValidatorTask.class);

                } catch (Throwable th) {
                    LOG.error("Can not load client validator task class '"
                            + serverTaskClassName + "'.", th);

                    throw new FacesException(
                            "Can not initialize server filter.", th);
                }

                try {
                    return clazz.newInstance();

                } catch (Throwable th) {
                    LOG.error("Can not instanciate client validator task '"
                            + clazz + "'.", th);

                    throw new FacesException(
                            "Can not initialize server filter.", th);
                }

            }

            return null;
        }

        public String getClientTaskExpression() {
            return clientTaskExpression;
        }

        public String getClientTaskExpressionType() {
            return null;
        }

        public IClientValidatorTask getServerTask() {
            return clientValidatorTask;
        }
    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.1 $ $Date: 2013/11/13 12:53:26 $
     */
    public static class ParametersContainer {
        private IParameter[] parameters;

        private List<IParameter> parametersList;

        public IParameter[] listParameters() {
            return parameters;
        }

        public void commitParameters() {
            if (parametersList == null) {
                parameters = PARAMETER_EMPTY_ARRAY;
                return;
            }

            parameters = parametersList.toArray(new IParameter[parametersList
                    .size()]);
            parametersList = null;
        }

        public void addParameter(Parameter parameter) {
            if (parameters != null) {
                throw new IllegalStateException();
            }

            if (parametersList == null) {
                parametersList = new ArrayList<IParameter>(8);
            }

            parametersList.add(parameter);
        }

    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.1 $ $Date: 2013/11/13 12:53:26 $
     */
    public static class Parameter implements IParameter {
        private String name;

        private String value;

        public final String getName() {
            return name;
        }

        public final void setName(String name) {
            this.name = name;
        }

        public final String getValue() {
            return value;
        }

        public final void setValue(String value) {
            this.value = value;
        }

    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.1 $ $Date: 2013/11/13 12:53:26 $
     */
    public static class ServerConverter extends ParametersContainer implements
            IServerConverter {
        private String id;

        private String className;

        public final String getClassName() {
            return className;
        }

        public final void setClassName(String className) {
            this.className = className;
        }

        public final String getId() {
            return id;
        }

        public final void setId(String id) {
            this.id = id;
        }

        public Converter getInstance(FacesContext facesContext,
                UIComponent component) {

            Converter converter = null;

            boolean setParameters = false;

            String id = getId();
            if (id != null && id.length() > 0) {
                Application application = facesContext.getApplication();

                if (BindingTools.isBindingExpression(id)) {
                    converter = (Converter) BindingTools.evalBinding(
                            facesContext, id, Converter.class);

                } else {
                    converter = application.createConverter(id);
                    // C'est une nouvelle instance à chaque fois !
                    setParameters = true;
                }
            }

            if (converter == null) {
                String className = getClassName();
                if (className != null) {
                    try {
                        Class< ? extends Converter> clazz = ClassLocator.load(
                                className, null, facesContext, Converter.class);
                        converter = clazz.newInstance();

                        setParameters = true;

                    } catch (Throwable th) {
                        LOG.error("Can not instanciate converter class='"
                                + className + "'.", th);

                        throw new FacesException(th);
                    }
                }
            }

            if (converter == null) {
                return converter;
            }

            IParameter parameters[] = listParameters();

            if (setParameters == false) {
                if (parameters.length > 1) {
                    throw new FacesException(
                            "Can not set parameters to a 'value binding' Converter.");
                }
                return converter;
            }

            BeanInfo beanInfo;
            try {
                beanInfo = Introspector.getBeanInfo(converter.getClass());

            } catch (IntrospectionException e) {
                throw new FacesException(
                        "Can not introspect bean from validator id='" + getId()
                                + "' className='" + getClassName() + "'.", e);
            }

            Map<String, PropertyDescriptor> listProperties = propertyDescriptorsByName(beanInfo);

            IParametredConverter parametredConverter = null;

            for (int i = 0; i < parameters.length; i++) {
                IParameter parameter = parameters[i];

                String name = parameter.getName();

                PropertyDescriptor propertyDescriptor = listProperties
                        .get(name);

                if (propertyDescriptor != null) {
                    Class< ? > propertyType = propertyDescriptor
                            .getPropertyType();

                    Object value = Convertor.convert(parameter.getValue(),
                            propertyType);

                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Set parameter '" + name
                                + "', converted value='" + value
                                + "' to converter " + converter
                                + " [IParametredConverter method]");
                    }

                    try {
                        propertyDescriptor.getWriteMethod().invoke(converter,
                                new Object[] { value });

                    } catch (Throwable th) {
                        LOG.error("Can not set property '" + name
                                + "' converted value='" + value
                                + "' to converter " + converter, th);
                    }

                    continue;
                }

                if (parametredConverter != null) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Set parameter '" + name + "' value='"
                                + parameter.getValue() + "' to converter "
                                + converter + " [IParametredConverter method]");
                    }

                    try {
                        parametredConverter.setParameter(name,
                                parameter.getValue());

                    } catch (Throwable th) {
                        LOG.error("Can not set parameter '" + name + "' to "
                                + parameter.getValue(), th);
                    }
                    continue;
                }

                if (LOG.isDebugEnabled()) {
                    LOG.debug("Can not set parameter '" + name + "' ("
                            + parameter.getValue() + ") to converter "
                            + converter);
                }
            }

            return converter;
        }

        private Map<String, PropertyDescriptor> propertyDescriptorsByName(
                BeanInfo beanInfo) {

            PropertyDescriptor propertyDescriptors[] = beanInfo
                    .getPropertyDescriptors();

            Map<String, PropertyDescriptor> map = new HashMap<String, PropertyDescriptor>(
                    propertyDescriptors.length);

            for (int j = 0; j < propertyDescriptors.length; j++) {
                PropertyDescriptor p = propertyDescriptors[j];

                map.put(p.getName(), p);
            }

            return map;
        }
    }
}
