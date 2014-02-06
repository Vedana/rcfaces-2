/*
 * $Id: URIParameters.java,v 1.3 2013/11/13 12:53:25 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.internal.webapp;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.faces.FacesException;

import org.rcfaces.core.internal.lang.StringAppender;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:25 $
 */
public class URIParameters implements Cloneable {
    public static final String PARAMETER_URI_SEPARATOR = "__";

    public static final String VERSION_PARAMETER = "V";

    public static final String LOCALE_PARAMETER = "L";

    public static final String AGENT_PARAMETER = "A";

    private final String uri;

    private Map<String, String> parameters;

    private URIParameters(String uri, Map<String, String> parameters) {
        this.uri = uri;
        this.parameters = parameters;
    }

    private URIParameters(String uri) {
        this(uri, null);
    }

    public static URIParameters parseURI(String uri) {
        int separatorPos = uri.indexOf(PARAMETER_URI_SEPARATOR);
        if (separatorPos < 0) {
            return new URIParameters(uri);
        }

        StringAppender sb = new StringAppender(uri.length());
        sb.append(uri.substring(0, separatorPos));

        int lastDot = uri.lastIndexOf('.');
        int lastSlash = uri.lastIndexOf('/');
        if (lastSlash > 0 && lastSlash > lastDot) {
            if (separatorPos < lastSlash) {
                return new URIParameters(uri);
            }

            lastDot = -1;
        }

        Map<String, String> parameters = null;

        int last = 0;
        int uriLength = uri.length();
        for (; last < uriLength;) {
            int pos = uri.indexOf(PARAMETER_URI_SEPARATOR, last);
            if (pos < 0) {
                sb.append(uri.substring(last));
                break;
            }

            pos += PARAMETER_URI_SEPARATOR.length();
            last = uri.indexOf(PARAMETER_URI_SEPARATOR, pos);
            if (lastDot >= 0 && (last < 0 || lastDot < last)) {
                last = lastDot;
            }

            if (last < 0) {
                last = uriLength;
            }

            String p = uri.substring(pos, last);

            if (parameters == null) {
                parameters = new HashMap<String, String>(8);
            }
            parameters.put(p.substring(0, 1), p.substring(1));
        }

        uri = sb.toString();

        return new URIParameters(uri, parameters);
    }

    public String getURI() {
        return uri;
    }

    public URL computeParametredURL() {
        String uri = computeParametredURI();

        try {
            return new URL(uri);

        } catch (MalformedURLException ex) {
            throw new FacesException("Bad uri '" + uri + "'", ex);
        }
    }

    public String computeParametredURI() {
        if (parameters == null || parameters.isEmpty()) {
            return uri;
        }
        StringAppender sb = new StringAppender(uri.length() + parameters.size()
                * 16);

        int idx = uri.lastIndexOf('.');

        if (idx < 0) {
            sb.append(uri);
        } else {
            sb.append(uri.substring(0, idx));
        }

        if (parameters.size() == 1) {

            Map.Entry<String, String> entry = parameters.entrySet().iterator()
                    .next();

            sb.append(PARAMETER_URI_SEPARATOR);
            sb.append(entry.getKey());
            String param = entry.getValue();
            if (param != null) {
                sb.append(param);
            }

        } else {
            List<String> keys = new ArrayList<String>(parameters.keySet());

            Collections.sort(keys);

            for (String key : keys) {
                sb.append(PARAMETER_URI_SEPARATOR);
                sb.append(key);
                String param = parameters.get(key);
                if (param != null) {
                    sb.append(param);
                }
            }
        }

        if (idx >= 0) {
            sb.append(uri.substring(idx));
        }

        return sb.toString();
    }

    private URIParameters append(String command, String parameter) {

        if (parameters == null) {
            parameters = new HashMap<String, String>(8);
        }
        parameters.put(command, parameter);

        return this;
    }

    public URIParameters appendLocale(Locale locale) {
        return append(LOCALE_PARAMETER, locale.toString());
    }

    public URIParameters appendVersion(String version) {
        return append(VERSION_PARAMETER, version);
    }

    public URIParameters appendAgent(String version) {
        return append(AGENT_PARAMETER, version);
    }

    public String getVersion() {
        return getParameterByCommand(VERSION_PARAMETER);
    }

    public String getLocaleName() {
        return getParameterByCommand(LOCALE_PARAMETER);
    }

    public String getAgent() {
        return getParameterByCommand(AGENT_PARAMETER);
    }

    private String getParameterByCommand(String command) {
        if (parameters == null || parameters.isEmpty()) {
            return null;
        }

        return parameters.get(command);
    }

    @Override
    public URIParameters clone() {
        return new URIParameters(uri, parameters);
    }

}
