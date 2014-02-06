/*
 * $Id: CameliaConverterHandler.java,v 1.1 2014/02/05 16:05:53 jbmeslin Exp $
 */
package org.rcfaces.core.internal.facelets;

import java.util.Locale;

import javax.faces.convert.Converter;
import javax.faces.convert.NumberConverter;
import javax.faces.view.facelets.ConverterConfig;
import javax.faces.view.facelets.ConverterHandler;
import javax.faces.view.facelets.FaceletContext;
import javax.faces.view.facelets.MetaRuleset;
import javax.faces.view.facelets.TagAttribute;

import org.rcfaces.core.converter.AbstractNumberConverter;
import org.rcfaces.core.internal.converter.LocaleConverter;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.1 $ $Date: 2014/02/05 16:05:53 $
 */
public abstract class CameliaConverterHandler extends ConverterHandler {
    

    private final TagAttribute localeAttribute;

    private final TagAttribute defaultNumberAttribute;

    public CameliaConverterHandler(ConverterConfig config) {
        super(config);

        this.localeAttribute = this.getAttribute("locale");
        this.defaultNumberAttribute = this.getAttribute("defaultNumber");
    }

    protected Converter createConverter(FaceletContext ctx) {
        return ctx.getFacesContext().getApplication()
                .createConverter(getConverterId());
    }

    protected abstract String getConverterId();

    @Override
    public void setAttributes(FaceletContext ctx, Object converter) {
        super.setAttributes(ctx, converter);

        if (localeAttribute == null && (converter instanceof NumberConverter)) {
            Locale locale = null;
            Object localeValue = localeAttribute.getObject(ctx);
            if (localeValue instanceof Locale) {
                locale = (Locale) localeValue;

            } else if (localeValue instanceof String) {
                locale = (Locale) LocaleConverter.SINGLETON.getAsObject(
                        ctx.getFacesContext(), null, (String) localeValue);
            }

            if (locale != null) {
                ((NumberConverter) converter).setLocale(locale);
            }
        }

        if (defaultNumberAttribute != null
                && (converter instanceof AbstractNumberConverter)) {
            Object defaultValue = defaultNumberAttribute.getObject(ctx);

            if (defaultValue != null) {
                Object defValue = defaultValue;

                ((AbstractNumberConverter) converter).setDefaultValue(String
                        .valueOf(defValue));
            }
        }
    }

    @Override
    protected MetaRuleset createMetaRuleset(Class type) {
        return super.createMetaRuleset(type).ignore("locale")
                .ignore("defaultNumber");
    }
}
