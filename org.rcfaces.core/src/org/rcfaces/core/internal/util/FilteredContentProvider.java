/*
 * $Id: FilteredContentProvider.java,v 1.4 2013/11/13 12:53:22 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.internal.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URL;

import org.rcfaces.core.internal.lang.ByteBufferInputStream;
import org.rcfaces.core.internal.lang.StringAppender;
import org.rcfaces.core.internal.repository.IContentRef;
import org.rcfaces.core.internal.repository.IRepository.IContent;
import org.rcfaces.core.internal.repository.IRepository.ICriteria;
import org.rcfaces.core.internal.repository.URLContentRef;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.4 $ $Date: 2013/11/13 12:53:22 $
 */
public class FilteredContentProvider extends URLContentProvider {

    private static final String CONTENT_DEFAULT_CHARSET = "UTF-8";

    @Override
    public IContent getContent(IContentRef contentReference) {
        URLContentRef urlContentRef = (URLContentRef) contentReference;

        return new FilteredURLContent(urlContentRef);
    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.4 $ $Date: 2013/11/13 12:53:22 $
     */
    protected class FilteredURLContent extends URLContent {

        public FilteredURLContent(URL url, ICriteria criteria) {
            super(url, criteria);
        }

        public FilteredURLContent(URLContentRef urlContentRef) {
            super(urlContentRef);
        }

        @Override
        protected InputStream openInputStream(boolean toClose)
                throws IOException {
            InputStream ins = super.openInputStream(toClose);
            if (ins == null || toClose) {
                return ins;
            }

            StringWriter writer = new StringWriter(8000);
            InputStreamReader reader = new InputStreamReader(ins, getCharset());
            try {

                char buffer[] = new char[4096];
                for (;;) {
                    int ret = reader.read(buffer);
                    if (ret < 1) {
                        break;
                    }

                    writer.write(buffer, 0, ret);
                }
            } finally {
                reader.close();
            }

            String file = writer.toString();

            file = updateBuffer(file, url, criteria);

            return new ByteBufferInputStream(file.getBytes(getCharset()));
        }
    }

    protected String getCharset() {
        return CONTENT_DEFAULT_CHARSET;
    }

    protected String updateBuffer(String buffer, URL url, ICriteria criteria) {
        return buffer;
    }

    public static String replace(String source, String oldString,
            String newString) {
        int index = source.indexOf(oldString);
        if (index < 0) {
            return source;
        }

        int lf = oldString.length();
        char[] dest = source.toCharArray();

        StringAppender sb = new StringAppender(source.length());

        for (int last = 0;;) {
            sb.append(dest, last, index - last);
            sb.append(newString);
            last = index + lf;

            index = source.indexOf(oldString, last);
            if (index >= 0) {
                continue;
            }

            if (last < dest.length) {
                sb.append(dest, last, dest.length - last);
            }

            return sb.toString();
        }
    }
}
