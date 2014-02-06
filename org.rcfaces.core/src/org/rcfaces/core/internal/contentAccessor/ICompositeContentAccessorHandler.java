/*
 * $Id: ICompositeContentAccessorHandler.java,v 1.1 2011/04/12 09:25:28 oeuillot Exp $
 */
package org.rcfaces.core.internal.contentAccessor;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:28 $
 */
public interface ICompositeContentAccessorHandler extends
        IContentAccessorHandler {

    String COMPOSITE_OPERATION_ID = "composite";

    ICompositeURLDescriptor createCompositeURLDescriptor(String mainCharSet);

    ICompositeURLDescriptor parseCompositeURLDescriptor(String compositeURL);

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
     * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:28 $
     */
    public interface ICompositeURLDescriptor {
        void addUrl(String url, String charSet);

        String generateURL();

        IURLInformation[] listURLs();

        String getMainCharSet();

        public interface IURLInformation {
            String getURL();

            String getCharSet();
        }
    }
}
