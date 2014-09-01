package tbx2rdf.types.abs;

import tbx2rdf.Mapping;
import tbx2rdf.Mappings;

/**
 *
 * @author John McCrae
 */
public abstract class impIDLangTypeTgtDtyp extends impIDLang {
    public final Mapping type;
    public String target;
    public String datatype;

    public impIDLangTypeTgtDtyp(Mapping type, String lang, Mappings mappings) {
        super(lang, mappings);
        this.type = type;
    }
    
    
}
