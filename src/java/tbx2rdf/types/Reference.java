package tbx2rdf.types;

import org.w3c.dom.NodeList;
import tbx2rdf.Mapping;
import tbx2rdf.Mappings;
import tbx2rdf.types.abs.impIDLangTypeTgtDtyp;

public class Reference extends impIDLangTypeTgtDtyp {

    public Reference(Mapping type, String language, Mappings mappings, NodeList value) {
        super(type, language, mappings, value);
    }
}