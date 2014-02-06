/*
 * $Id: ICssStyleClasses.java,v 1.1 2011/04/12 09:28:14 oeuillot Exp $
 */
package org.rcfaces.renderkit.html.internal.renderer;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:28:14 $
 */
public interface ICssStyleClasses {
    String getMainStyleClass();

    String getSuffixedMainStyleClass(String suffix);

    String constructClassName();

    String[] listStyleClasses();

    void addStyleClass(String styleClass);

    void addSuffix(String suffixStyleClass);

    void addSpecificStyleClass(String styleClass);

    String constructUserStyleClasses();
}
