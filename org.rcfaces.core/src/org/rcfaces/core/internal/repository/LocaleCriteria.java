/*
 * $Id: LocaleCriteria.java,v 1.1 2013/11/13 12:53:25 jbmeslin Exp $
 */
package org.rcfaces.core.internal.repository;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.faces.context.FacesContext;

import org.rcfaces.core.internal.Constants;
import org.rcfaces.core.internal.lang.StringAppender;
import org.rcfaces.core.internal.repository.IRepository.ICriteria;
import org.rcfaces.core.internal.webapp.URIParameters;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.1 $ $Date: 2013/11/13 12:53:25 $
 */
public class LocaleCriteria extends AbstractCriteria {

    private static final Map<String, Locale> defaultConvertedLocales = new HashMap<String, Locale>(
            256);

    private static final Locale unknownLocale = new Locale("**UNKNOWN**");

    private static final Map<Locale, ICriteria> predefinedCriterias = new HashMap<Locale, ICriteria>();
    static {
        Locale[] locales = Locale.getAvailableLocales();
        for (Locale locale : locales) {
            Locale localeCriteria = locale;

            if (Constants.LOCALE_CRITERIA_LANGUAGE_ONLY) {
                if (locale.getCountry() != null) {
                    localeCriteria = new Locale(locale.getLanguage());
                }
            }

            predefinedCriterias.put(locale, LocaleCriteria.get(localeCriteria));
        }
    }

    private Locale locale;

    public LocaleCriteria() {
    }

    private LocaleCriteria(ICriteria parent, Locale locale) {
        setParent(parent);
        this.locale = locale;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getAdapter(Class<T> adapter, Object parameter) {
        if (adapter == Locale.class) {
            return (T) locale;
        }
        return super.getAdapter(adapter, parameter);
    }

    @Override
    public void appendSuffix(URIParameters uriParameters, boolean recursive) {
        if (locale != null) {
            uriParameters.appendLocale(locale);
        }

        super.appendSuffix(uriParameters, recursive);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((locale == null) ? 0 : locale.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        LocaleCriteria other = (LocaleCriteria) obj;
        if (locale == null) {
            if (other.locale != null)
                return false;
        } else if (!locale.equals(other.locale))
            return false;
        return true;
    }

    @Override
    public String toString() {
        StringAppender builder = new StringAppender();
        builder.append("[LocaleCriteria locale='").append(locale).append("'");
        if (getParent() != null) {
            builder.append(" parent='").append(getParent()).append("'");
        }

        builder.append(']');
        return builder.toString();
    }

    public static ICriteria get(Locale locale) {
        return get(null, locale);
    }

    public static ICriteria get(ICriteria parent, Locale locale) {
        if (parent == null) {
            ICriteria criteria = predefinedCriterias.get(locale);
            if (criteria != null) {
                return criteria;
            }
        }

        return new LocaleCriteria(parent, locale);
    }

    public static Locale getLocale(ICriteria criteria) {
        Locale locale = criteria.getAdapter(Locale.class, null);
        return locale;
    }

    public static ICriteria keepLocale(ICriteria criteria) {
        Locale locale = getLocale(criteria);
        if (locale == null) {
            return null;
        }
        if (criteria.getParent() == null) {
            return criteria;
        }
        ICriteria pc = keepLocale(criteria.getParent());
        if (pc != null) {
            return pc;
        }

        return get(locale);
    }

    @Override
    public Object saveState(FacesContext context) {
        Object[] obj = new Object[2];

        obj[0] = super.saveState(context);
        if (locale != null) {
            obj[1] = locale.toString();
        }
        return obj;
    }

    @Override
    public void restoreState(FacesContext context, Object state) {
        Object[] obj = (Object[]) state;

        super.restoreState(context, obj[0]);

        if (obj[1] != null) {
            locale = convertLocaleName((String) obj[1]);
        }
    }

    public static final Locale convertLocaleName(String localeName) {
        return convertLocaleName(localeName, null, null);
    }

    public static final Locale convertLocaleName(String localeName,
            Map<String, Locale> convertedLocales, Set<Locale> filtredLocales) {

        if (convertedLocales == null) {
            convertedLocales = LocaleCriteria.defaultConvertedLocales;
        }

        localeName = localeName.toLowerCase();

        Locale locale;
        synchronized (convertedLocales) {
            locale = convertedLocales.get(localeName);
        }

        if (locale != null) {
            if (locale == unknownLocale) {
                return null;
            }
            return locale;
        }

        // On synchronise pas le bloc, histore de pas bloquer le reste des
        // Threads ...
        // Et tanpis pour les put multiple de la meme valeur !

        StringTokenizer st = new StringTokenizer(localeName, "_");
        String language = st.nextToken().toLowerCase();
        String country = (st.hasMoreTokens()) ? st.nextToken().toLowerCase()
                : "";
        String variant = (st.hasMoreTokens()) ? st.nextToken().toLowerCase()
                : "";

        Locale bestLocale = null;
        int bestHit = 0;

        Locale locales[] = Locale.getAvailableLocales();
        for (int i = 0; i < locales.length; i++) {
            locale = locales[i];
            if (filtredLocales != null
                    && filtredLocales.contains(locale) == false) {
                continue;
            }

            if (locale.getLanguage().equalsIgnoreCase(language) == false) {
                continue;
            }
            int hit = 1;

            String lcountry = locale.getCountry();
            if (lcountry.equalsIgnoreCase(country)) {
                hit += 2;

                String lvariant = locale.getVariant();
                if (lvariant.equalsIgnoreCase(variant)) {
                    hit += 2;

                } else if (lvariant.length() < 1) {
                    hit++;
                }

            } else if (lcountry.length() < 1) {
                hit++;
            }

            if (hit < bestHit) {
                continue;
            }

            bestLocale = locale;
            bestHit = hit;
        }

        if (bestLocale == null) {
            synchronized (convertedLocales) {
                convertedLocales.put(localeName, unknownLocale);
            }
            return null;
        }

        synchronized (convertedLocales) {
            convertedLocales.put(localeName, bestLocale);
        }

        return bestLocale;
    }

    public ICriteria merge(ICriteria criteria) {
        if (equals(criteria)) {
            return this;
        }

        Locale mergeLocale = LocaleCriteria.getLocale(criteria);
        if (mergeLocale == null) {
            return LocaleCriteria.get(criteria, locale);
        }

        if (mergeLocale.equals(locale) == false) {
            throw new IllegalStateException("Can not merge locale '" + locale
                    + "' with '" + mergeLocale + "'");
        }

        return criteria;
    }

}
