package tbx2rdf.types.abs;

import tbx2rdf.Mappings;

/**
 *
 * @author John McCrae
 */
public abstract class impIDLang extends impID {
    public final String lang;

    public impIDLang(String lang, Mappings mappings) {
        if(lang == null || lang.equals("")) {
            if(mappings.defaultLanguage != null) {
                this.lang = mappings.defaultLanguage;
            } else {
                this.lang = "en";
            }
        } else {
            this.lang = lang;
        }
    }
}
