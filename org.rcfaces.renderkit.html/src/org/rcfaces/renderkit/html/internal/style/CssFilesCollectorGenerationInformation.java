/*
 * $Id: CssFilesCollectorGenerationInformation.java,v 1.1 2013/01/11 15:45:04 jbmeslin Exp $
 */
package org.rcfaces.renderkit.html.internal.style;

import org.rcfaces.renderkit.html.internal.renderer.FilesCollectorGenerationInformation;
import org.rcfaces.renderkit.html.internal.renderer.IFrameworkResourceGenerationInformation;
import org.rcfaces.renderkit.html.internal.util.FileItemSource;

public class CssFilesCollectorGenerationInformation extends
        FilesCollectorGenerationInformation implements
        IFrameworkResourceGenerationInformation,
        IProcessRulesGenerationInformation {

    public CssFilesCollectorGenerationInformation(FileItemSource[] sources,
            boolean frameworkResource, boolean processRules) {
        super(sources);

        if (sources != null && frameworkResource == false) {
            for (int i = 0; i < sources.length; i++) {
                if (sources[i].isFrameworkResource()) {
                    frameworkResource = true;
                    break;
                }
            }
        }

        if (frameworkResource) {
            setAttribute(FRAMEWORK_ATTRIBUTE, Boolean.TRUE);
        }
        if (processRules) {
            setAttribute(PROCESS_RULES_ATTRIBUTE, Boolean.TRUE);
        }
    }

    public boolean isFrameworkResource() {
        return Boolean.TRUE.equals(getAttribute(FRAMEWORK_ATTRIBUTE));
    }

    public boolean isProcessRulesEnabled() {
        return Boolean.TRUE.equals(getAttribute(PROCESS_RULES_ATTRIBUTE));
    }
}