/*
 * $Id: IUserAgentVaryCapability.java,v 1.2 2013/01/11 15:45:06 jbmeslin Exp $
 */
package org.rcfaces.renderkit.html.component.capability;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/01/11 15:45:06 $
 */
public interface IUserAgentVaryCapability {

	  String MICROSOFT_INTERNET_EXPLORER = "msie";

	    String INTERNET_EXPLORER = "ie";

	    String FIREFOX = "firefox";

	    String FIREFOX_LITE = "fx";

	    String FIREFOX_LITE2 = "ff";

	    String OPERA = "opera";

	    String SAFARI = "safari";

	    String CHROME = "chrome";

	    String IOS = "ios";

	    String IPHONE = "iphone";

	    String ANDROID = "android";

	    String SUPPORTED_AGENT_NAMES[] = { MICROSOFT_INTERNET_EXPLORER, FIREFOX,
	            OPERA, SAFARI, CHROME, IOS, ANDROID };

	    String getUserAgent();

	    void setUserAgent(String userAgent);
}
