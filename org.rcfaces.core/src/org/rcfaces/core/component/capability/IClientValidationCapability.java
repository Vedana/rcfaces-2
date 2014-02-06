/*
 * $Id: IClientValidationCapability.java,v 1.1 2011/04/12 09:25:25 oeuillot Exp $
 * 
 */
package org.rcfaces.core.component.capability;

/**
 * A key identifying a validation process to apply to the component. this
 * validation process can handle parameters. cf. the clientValidator doc.
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:25 $
 */
public interface IClientValidationCapability {

    /**
     * Returns a key identifying a validation process to apply to the component.
     * this validation process can handle parameters. cf. the clientValidator
     * doc.
     * 
     * @return client validator key
     */
    String getClientValidator();

    /**
     * Sets a key identifying a validation process to apply to the component.
     * this validation process can handle parameters. cf. the clientValidator
     * doc.
     * 
     * @param validator
     *            client validator key
     */
    void setClientValidator(String validator);
}
