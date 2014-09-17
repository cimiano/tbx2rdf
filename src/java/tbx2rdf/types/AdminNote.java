package tbx2rdf.types;

import org.w3c.dom.NodeList;
import tbx2rdf.Mapping;
import tbx2rdf.Mappings;
import tbx2rdf.types.abs.impIDLangTypeTgtDtyp;

/**
 *
 * @author John McCrae
 */
public class AdminNote extends impIDLangTypeTgtDtyp {

    public AdminNote(NodeList value, Mapping type, String lang, Mappings mappings) {
        super(type, lang, mappings, value);
    }
}