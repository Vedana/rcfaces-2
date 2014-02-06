/*
 * $Id: AbstractCompositeContentAccessorHandler.java,v 1.3 2013/11/13 12:53:22 jbmeslin Exp $
 */
package org.rcfaces.core.internal.contentAccessor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.internal.lang.StringAppender;
import org.rcfaces.core.provider.AbstractProvider;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:22 $
 */
public abstract class AbstractCompositeContentAccessorHandler extends
        AbstractProvider implements ICompositeContentAccessorHandler {
    private static final Log LOG = LogFactory
            .getLog(AbstractCompositeContentAccessorHandler.class);

    private static final Set<String> DEFAULT_CHARSETS = new HashSet<String>(8);
    static {
        DEFAULT_CHARSETS.add("utf8");
        DEFAULT_CHARSETS.add("utf-8");
        DEFAULT_CHARSETS.add("utf 8");
        DEFAULT_CHARSETS.add("utf_8");
    }

    public ICompositeURLDescriptor createCompositeURLDescriptor(
            String mainCharSet) {
        CompositeURLDescriptor compositeURLDescriptor = new CompositeURLDescriptor();

        compositeURLDescriptor.initialize(mainCharSet);

        return compositeURLDescriptor;
    }

    public ICompositeURLDescriptor parseCompositeURLDescriptor(
            String compositeURL) {

        CompositeURLDescriptor compositeURLDescriptor = new CompositeURLDescriptor();

        compositeURLDescriptor.parse(compositeURL);

        return compositeURLDescriptor;
    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:22 $
     */
    protected static class CompositeURLDescriptor implements
            ICompositeURLDescriptor {

        private static final String MERGE_URL = COMPOSITE_OPERATION_ID
                + IContentAccessor.FILTER_SEPARATOR;

        private StringAppender sa;

        private boolean first;

        private String mainCharSet;

        private IURLInformation[] urls;

        protected CompositeURLDescriptor() {
        }

        public void initialize(String mainCharSet) {
            sa = new StringAppender(MERGE_URL, 128);

            if (mainCharSet != null
                    && DEFAULT_CHARSETS.contains(mainCharSet.toLowerCase()) == false) {

                encodeParameter(sa, mainCharSet);
            }

            sa.append(':');

            first = true;
        }

        public void addUrl(String src, String charSet) {

            if (first) {
                first = false;
            } else {
                sa.append('+');
            }

            if (charSet != null
                    && DEFAULT_CHARSETS.contains(charSet.toLowerCase()) == false) {
                encodeParameter(sa, charSet);
            }

            sa.append(':');
            encodeParameter(sa, src);
        }

        public String generateURL() {
            return sa.toString();
        }

        public String getMainCharSet() {
            return mainCharSet;
        }

        public IURLInformation[] listURLs() {
            return urls;
        }

        public void parse(String compositedURL) {
            StringTokenizer st = new StringTokenizer(compositedURL, ":", true);

            String nextToken = st.nextToken();
            if (nextToken.equals(":") == false) {
                st.nextToken();

                mainCharSet = decodeParameter(nextToken);
            }

            List<IURLInformation> us = new ArrayList<IURLInformation>(
                    st.countTokens() / 2);
            for (; st.hasMoreTokens();) {
                String urlCharSet = null;
                nextToken = st.nextToken();
                if (nextToken.equals(":") == false) {
                    st.nextToken();

                    urlCharSet = decodeParameter(nextToken);
                }

                final String _charSet = urlCharSet;
                final String url = decodeParameter(nextToken);

                us.add(new IURLInformation() {

                    public String getCharSet() {
                        return _charSet;
                    }

                    public String getURL() {
                        return url;
                    }
                });
            }

            this.urls = us.toArray(new IURLInformation[us.size()]);
        }

        private void encodeParameter(StringAppender sa, String value) {
            char chs[] = value.toCharArray();

            for (int i = 0; i < chs.length; i++) {
                char c = chs[i];

                if (Character.isLetterOrDigit(c) || c == '/' || c == '.') {
                    sa.append(c);
                    continue;
                }

                sa.append('%');
                String hex = Integer.toHexString(c);
                for (int l = 4 - hex.length(); l > 0; l--) {
                    sa.append('0');
                }
                sa.append(hex);
            }
        }

        private String decodeParameter(String value) {
            StringAppender sa = new StringAppender(value.length());

            char chs[] = value.toCharArray();
            for (int i = 0; i < chs.length;) {
                char c = chs[i++];

                if (c != '%') {
                    sa.append(c);
                    continue;
                }

                int hex = Integer.parseInt(value.substring(i, i + 4), 16);
                sa.append((char) hex);
                i += 4;
            }

            return sa.toString();
        }

    }

}
