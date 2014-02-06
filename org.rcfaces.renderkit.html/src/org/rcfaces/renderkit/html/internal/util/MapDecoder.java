/*
 * $Id: MapDecoder.java,v 1.1 2013/11/13 12:53:32 jbmeslin Exp $
 */
package org.rcfaces.renderkit.html.internal.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.1 $ $Date: 2013/11/13 12:53:32 $
 */
public class MapDecoder {

    private static final Log LOG = LogFactory.getLog(MapDecoder.class);

    private static final String REQUEST_CHARSET = "UTF-8";

    public static Map<String, Object> parse(String text) {
        Map<String, Object> map = new HashMap<String, Object>(8);

        StringTokenizer st = new StringTokenizer(text, ",");
        for (; st.hasMoreTokens();) {
            String token = st.nextToken();

            int idx = token.indexOf('=');
            try {
                String key = URLDecoder.decode(token.substring(0, idx),
                        REQUEST_CHARSET);
                String value = URLDecoder.decode(token.substring(idx + 1),
                        REQUEST_CHARSET);

                if (LOG.isDebugEnabled()) {
                    LOG.debug("Parse map text='" + text + "' => key='" + key
                            + "' value='" + value + "'");
                }

                map.put(key, value);

            } catch (UnsupportedEncodingException ex) {
                LOG.error("Can not parse token '" + token + "' from map '"
                        + text + "'", ex);
            }
        }

        return map;
    }
}
