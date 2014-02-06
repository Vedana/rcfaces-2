/*
 * $Id: ClientValidatorTools.java,v 1.4 2013/11/13 12:53:22 jbmeslin Exp $
 */
package org.rcfaces.core.internal.tools;

import java.util.Iterator;
import java.util.Locale;
import java.util.TimeZone;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.component.ValueHolder;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.component.capability.IClientValidationCapability;
import org.rcfaces.core.internal.RcfacesContext;
import org.rcfaces.core.internal.manager.ITransientAttributesManager;
import org.rcfaces.core.internal.util.CommandParserIterator;
import org.rcfaces.core.internal.util.CommandParserIterator.ICommand;
import org.rcfaces.core.internal.validator.IClientValidatorDescriptor;
import org.rcfaces.core.internal.validator.IClientValidatorsRegistry;
import org.rcfaces.core.internal.validator.IServerConverter;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.4 $ $Date: 2013/11/13 12:53:22 $
 */
public class ClientValidatorTools {
    private static final Log LOG = LogFactory.getLog(CheckTools.class);

    private static final String CLIENT_VALIDATION_CONTEXT_PROPERTY = "org.rcfaces.core.CLIENT_VALIDATOR_CONTEXT";

    public static void setClientValidator(FacesContext facesContext,
            IClientValidationCapability clientValidationCapability) {

        if (facesContext == null) {
            facesContext = FacesContext.getCurrentInstance();
        }

        if ((clientValidationCapability instanceof ValueHolder) == false) {
            throw new FacesException("Invalid clientValidationCapability '"
                    + clientValidationCapability + "'.");
        }

        IClientValidationContext clientValidationContext = getClientValidationContext(
                facesContext, clientValidationCapability);

        if (LOG.isDebugEnabled()) {
            LOG.debug("clientValidationContext='" + clientValidationContext
                    + "' for component='" + clientValidationCapability + "'.");
        }

        if (clientValidationContext == null) {
            return;
        }

        ValueHolder valueHolder = (ValueHolder) clientValidationCapability;

        if (valueHolder.getConverter() != null) {

            if (LOG.isDebugEnabled()) {
                LOG.debug("Converter of valueHolder is already setted for component='"
                        + clientValidationCapability + "'.");
            }
            return;
        }

        IServerConverter serverConverter = clientValidationContext
                .getClientValidatorDescriptor().getServerConverter();

        if (serverConverter != null) {
            Converter converter = serverConverter.getInstance(facesContext,
                    (UIComponent) valueHolder);

            if (converter != null) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Set Converter '" + converter
                            + "' to component='" + clientValidationCapability
                            + "'.");
                }

                valueHolder.setConverter(converter);
            }
        }
    }

    public static IClientValidationContext getClientValidationContext(
            FacesContext facesContext,
            IClientValidationCapability clientValidationCapability) {

        ITransientAttributesManager transientAttributesManager = null;

        if (clientValidationCapability instanceof ITransientAttributesManager) {
            transientAttributesManager = (ITransientAttributesManager) clientValidationCapability;

            Object object = transientAttributesManager
                    .getTransientAttribute(CLIENT_VALIDATION_CONTEXT_PROPERTY);

            if (object instanceof IClientValidationContext) {
                return (IClientValidationContext) object;
            }
            if (object != null) {
                return null;
            }
        }

        String validator = clientValidationCapability.getClientValidator();

        if (validator == null) {
            // @XXX TODO On retire le CONVERTER ???

            if (transientAttributesManager != null) {
                transientAttributesManager.setTransientAttribute(
                        CLIENT_VALIDATION_CONTEXT_PROPERTY, Boolean.FALSE);
            }

            return null;
        }

        Iterator<ICommand> it = new CommandParserIterator(validator);
        if (it.hasNext() == false) {
            if (transientAttributesManager != null) {
                transientAttributesManager.setTransientAttribute(
                        CLIENT_VALIDATION_CONTEXT_PROPERTY, Boolean.FALSE);
            }
            return null;
        }

        final CommandParserIterator.ICommand command = it.next();

        if (it.hasNext()) {
            throw new FacesException(
                    "Validator does not support multiple expression.");
        }

        if (facesContext == null) {
            facesContext = FacesContext.getCurrentInstance();
        }

        IClientValidatorsRegistry clientValidatorManager = RcfacesContext
                .getInstance(facesContext).getClientValidatorsRegistry();
        if (clientValidatorManager == null) {
            // throw new FacesException("Can not get descriptorManager from
            // faces context !");

            // Designer mode
            if (transientAttributesManager != null) {
                transientAttributesManager.setTransientAttribute(
                        CLIENT_VALIDATION_CONTEXT_PROPERTY, Boolean.FALSE);
            }
            return null;
        }

        Locale locale = ContextTools.getUserLocale(facesContext);
        TimeZone timeZone = ContextTools.getUserTimeZone(facesContext);

        final IClientValidatorDescriptor validatorDescriptor = clientValidatorManager
                .getClientValidatorById(facesContext, command.getName(),
                        locale, timeZone);
        if (validatorDescriptor == null) {
            throw new FacesException("Can not find validator '"
                    + command.getName() + "' for component '"
                    + ((UIComponent) clientValidationCapability).getId()
                    + "' !");
        }

        IClientValidationContext clientValidatorContext = new IClientValidationContext() {
            public ICommand getClientValidatorCommand() {
                return command;
            }

            public IClientValidatorDescriptor getClientValidatorDescriptor() {
                return validatorDescriptor;
            }
        };

        if (transientAttributesManager != null) {
            transientAttributesManager.setTransientAttribute(
                    CLIENT_VALIDATION_CONTEXT_PROPERTY, clientValidatorContext);
        }

        return clientValidatorContext;
    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.4 $ $Date: 2013/11/13 12:53:22 $
     */
    public static interface IClientValidationContext {
        IClientValidatorDescriptor getClientValidatorDescriptor();

        CommandParserIterator.ICommand getClientValidatorCommand();
    }
}
