/*
 * $Id: ISeverityStyleClassCapability.java,v 1.1 2011/04/12 09:25:25 oeuillot Exp $
 */
package org.rcfaces.core.component.capability;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:25 $
 */
public interface ISeverityStyleClassCapability extends IStyleClassCapability {

    /**
     * Returns a space-separated list of CSS style class(es) to be applied for
     * info messages. This value will be passed through as the "class" attribute
     * on generated markup.
     * 
     * @return list of CSS style classes
     */
    String getInfoStyleClass();

    /**
     * Sets a space-separated list of CSS style class(es) to be applied for info
     * messages. This value will be passed through as the "class" attribute on
     * generated markup.
     * 
     * @param infoStyleClass
     *            list of CSS style classes
     */
    void setInfoStyleClass(String infoStyleClass);

    /**
     * Returns a space-separated list of CSS style class(es) to be applied for
     * warning messages. This value will be passed through as the "class"
     * attribute on generated markup.
     * 
     * @return list of CSS style classes
     */
    String getWarnStyleClass();

    /**
     * Sets a space-separated list of CSS style class(es) to be applied for
     * warning messages. This value will be passed through as the "class"
     * attribute on generated markup.
     * 
     * @param warnStyleClass
     *            list of CSS style classes
     */
    void setWarnStyleClass(String warnStyleClass);

    /**
     * Returns a space-separated list of CSS style class(es) to be applied for
     * error messages. This value will be passed through as the "class"
     * attribute on generated markup.
     * 
     * @return list of CSS style classes
     */
    String getErrorStyleClass();

    /**
     * Sets a space-separated list of CSS style class(es) to be applied for
     * error messages. This value will be passed through as the "class"
     * attribute on generated markup.
     * 
     * @param errorStyleClass
     *            list of CSS style classes
     */
    void setErrorStyleClass(String errorStyleClass);

    /**
     * Returns a space-separated list of CSS style class(es) to be applied for
     * fatal messages. This value will be passed through as the "class"
     * attribute on generated markup.
     * 
     * @return list of CSS style classes
     */
    String getFatalStyleClass();

    /**
     * Sets a space-separated list of CSS style class(es) to be applied for
     * fatal messages. This value will be passed through as the "class"
     * attribute on generated markup.
     * 
     * @param fatalStyleClass
     *            list of CSS style classes
     */
    void setFatalStyleClass(String fatalStyleClass);

}
