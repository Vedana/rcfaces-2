/*
 */
package com.vedana.camelia.generator.components_1_1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

/**
 * @author Olivier Oeuillot
 * @version $Revision: 1.3 $
 */
public class TemplateTools {

    private static final String REVISION = "$Revision: 1.3 $";

    public static String getContent(File url, Map<String, String> properties,
            boolean systemProperties) throws IOException {
        InputStream ins = new FileInputStream(url);
        if (ins == null) {
            throw new IOException("No content for URL '" + url + "'.");
        }

        StringBuffer sb = new StringBuffer(16000);

        BufferedReader br = new BufferedReader(new InputStreamReader(ins));

        char buffer[] = new char[4096];
        for (;;) {
            int ret = br.read(buffer);
            if (ret <= 0) {
                break;
            }

            sb.append(buffer, 0, ret);
        }

        if (sb.length() < 1) {
            throw new IOException("Content is empty for URL '" + url + "'.");
        }

        String content = sb.toString();
        sb.setLength(0);
        sb.ensureCapacity(content.length() * 3 / 2);

        int ret = 0;
        for (;;) {
            int pos = content.indexOf("${", ret);

            if (pos < 0) {
                sb.append(content.substring(ret));
                break;
            }

            if (pos > ret) {
                sb.append(content.substring(ret, pos));
            }

            int end = content.indexOf('}', pos);
            if (end < 0) {
                break;
            }

            String cmd = content.substring(pos + 2, end);
            String defolt = null;
            int idx = cmd.indexOf(':');
            if (idx > 0) {
                defolt = cmd.substring(idx + 1);
                cmd = cmd.substring(0, idx);
            }

            String s = null;
            Object value = properties.get(cmd);
            if (value != null) {
                s = String.valueOf(value);

            } else if (systemProperties == true) {
                s = System.getProperty(cmd);
            }

            if (s == null) {
                s = defolt;
            }

            if (s != null) {
                sb.append(s);
            }

            pos = end + 1;
            ret = pos;
        }

        return sb.toString();
    }

}
