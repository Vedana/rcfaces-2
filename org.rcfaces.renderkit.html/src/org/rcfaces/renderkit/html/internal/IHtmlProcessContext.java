/*
 * $Id: IHtmlProcessContext.java,v 1.3 2013/11/13 12:53:28 jbmeslin Exp $
 * 
 */
package org.rcfaces.renderkit.html.internal;

import java.util.Set;

import org.rcfaces.core.internal.contentAccessor.IContentAccessor;
import org.rcfaces.core.internal.renderkit.IProcessContext;
import org.rcfaces.core.lang.IContentFamily;
import org.rcfaces.renderkit.html.internal.agent.IClientBrowser;
import org.rcfaces.renderkit.html.internal.util.CarriageReturnNormalizerMode;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:28 $
 */
public interface IHtmlProcessContext extends IProcessContext {

    String HTML_FLAT_IDENTIFIER_PARAMETER = Constants.getPackagePrefix()
            + ".FLAT_IDENTIFIER";

    String HTML_SEPARATOR_CHAR_PARAMETER = Constants.getPackagePrefix()
            + ".SEPARATOR_CHAR";

    String USE_META_CONTENT_SCRIPT_TYPE_PARAMETER = Constants
            .getPackagePrefix() + ".USE_META_CONTENT_SCRIPT_TYPE";

    String USE_META_CONTENT_STYLE_TYPE_PARAMETER = Constants.getPackagePrefix()
            + ".USE_META_CONTENT_STYLE_TYPE";

    String USE_SCRIPT_CDATA_PARAMETER = Constants.getPackagePrefix()
            + ".USE_SCRIPT_CDATA";

    String FORCE_DATE_TO_GMT_PARAMETER = Constants.getPackagePrefix()
            + ".FORCE_DATE_TO_GMT";

    // String KEEP_DISABLED_STATE_PARAMETER = Constants.getPackagePrefix()+
    // ".KEEP_DISABLED_STATE";

    String DEBUG_MODE_APPLICATION_PARAMETER = Constants.getPackagePrefix()
            + ".client.DEBUG_MODE";

    String MULTI_WINDOW_MODE_APPLICATION_PARAMETER = Constants
            .getPackagePrefix() + ".client.MULTI_WINDOW_MODE";

    String PROFILER_MODE_APPLICATION_PARAMETER = Constants.getPackagePrefix()
            + ".client.PROFILER_MODE";

    String SYSTEM_PARAMETERS_NAMES_APPLICATION_PARAMETER = Constants
            .getPackagePrefix() + ".client.SYSTEM_PARAMETERS_NAMES";

    String HTML_PROCESS_RULES_FORCED_PARAMETER = Constants.getPackagePrefix()
            + ".PROCESS_RULES_FORCED";

    boolean isFlatIdentifierEnabled();

    String getStyleSheetURI(String uri, boolean containsContextPath);

    String getModuleStyleSheetURI(String moduleName, String uri,
            boolean containsContextPath);

    IContentAccessor getStyleSheetContentAccessor(String uri,
            IContentFamily contentType);

    IContentAccessor getModuleStyleSheetContentAccessor(String moduleName,
            String uri, IContentFamily contentType);

    String getNameSpaceURI();

    boolean useMetaContentScriptType();

    boolean useMetaContentStyleType();

    boolean useScriptCData();

    IClientBrowser getClientBrowser();

    Set<String> getSystemParametersNames();

    boolean isHtmlEscapingDisabled();

    Set<String> listCssProcessRulesForced();

    CarriageReturnNormalizerMode getCarriageReturnNormalizerMode();

    // boolean keepDisabledState();
}
