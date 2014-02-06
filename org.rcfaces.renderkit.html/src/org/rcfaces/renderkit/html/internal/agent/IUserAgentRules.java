/*
 * $Id: IUserAgentRules.java,v 1.1 2013/01/11 15:45:04 jbmeslin Exp $
 */
package org.rcfaces.renderkit.html.internal.agent;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.1 $ $Date: 2013/01/11 15:45:04 $
 */
public interface IUserAgentRules {
    boolean accepts(IUserAgent proposal);

    void textForm(StringBuilder sb, String separator);

    int rulesCount();

    IUserAgentRules merge(IUserAgentRules featureAgentRules);

    IUserAgent reduce(IUserAgent userAgent);
}
