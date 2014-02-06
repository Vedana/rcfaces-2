/*
 * $Id: BasicSelectItemPath.java,v 1.1 2013/11/13 12:53:25 jbmeslin Exp $
 */
package org.rcfaces.core.item;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Collection;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.1 $ $Date: 2013/11/13 12:53:25 $
 */
public class BasicSelectItemPath implements ISelectItemPath {

    private static final String DEFAULT_CHARSET = "UTF-8";

    private final SelectItem[] segments;

    public BasicSelectItemPath(SelectItem[] segments) {
        this.segments = segments;
    }

    public BasicSelectItemPath(Collection<SelectItem> segments) {
        this(segments.toArray(new SelectItem[segments.size()]));
    }

    public SelectItem[] segments() {
        return segments;
    }

    public SelectItem segment(int index) {
        return segments[index];
    }

    public int segmentCount() {
        return segments.length;
    }

    public SelectItem getSelectItem() {
        return segments[segments.length - 1];
    }

    public String normalizePath() {
        return normalizePath(null, null, null);
    }

    public String normalizePath(FacesContext facesContext,
            UIComponent component, Converter valueConverter) {

        StringBuilder sb = new StringBuilder(1024);

        for (int i = 0; i < segments.length; i++) {
            SelectItem seg = segments[i];

            Object value = seg.getValue();

            if (sb.length() > 0) {
                sb.append('/');
            }

            String convertedValue = null;

            if (value != null) {
                if (valueConverter != null) {
                    if (facesContext == null) {
                        facesContext = FacesContext.getCurrentInstance();
                    }

                    convertedValue = valueConverter.getAsString(facesContext,
                            component, convertedValue);
                } else {
                    convertedValue = String.valueOf(value);
                }
            }

            if (convertedValue == null) {
                sb.append('-');
                continue;
            }

            String encodeURI;
            try {
                encodeURI = URLEncoder.encode(convertedValue, DEFAULT_CHARSET);

            } catch (UnsupportedEncodingException ex) {
                throw new FacesException(ex);
            }

            sb.append(encodeURI);
        }

        return sb.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(segments);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        BasicSelectItemPath other = (BasicSelectItemPath) obj;
        if (!Arrays.equals(segments, other.segments))
            return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("[BasicSelectItemPath segments='")
                .append(Arrays.toString(segments)).append("']");
        return builder.toString();
    }

}
