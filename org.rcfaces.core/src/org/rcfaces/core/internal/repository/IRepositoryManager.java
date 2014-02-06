/*
 * $Id: IRepositoryManager.java,v 1.1 2011/04/12 09:25:48 oeuillot Exp $
 */
package org.rcfaces.core.internal.repository;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:48 $
 */
public interface IRepositoryManager {

    String[] listFamilies();

    String[] listRepositoryLocations(String family);

}
