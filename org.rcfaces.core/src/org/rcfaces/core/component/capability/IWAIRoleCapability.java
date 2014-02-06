/*
 * $Id: IWAIRoleCapability.java,v 1.1 2011/04/12 09:25:26 oeuillot Exp $
 */
package org.rcfaces.core.component.capability;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:26 $
 */
public interface IWAIRoleCapability {
    String getWaiRole();

    void setWaiRole(String role);
    
    int getAriaLevel();
    
    void setAriaLevel(int ariaLevel);
    
    String getAriaLabel();
    
    void setAriaLabel(String ariaLabel);
}
