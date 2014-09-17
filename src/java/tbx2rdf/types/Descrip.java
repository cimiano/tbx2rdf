package tbx2rdf.types;


import org.w3c.dom.NodeList;

import tbx2rdf.Mapping;
import tbx2rdf.Mappings;
import tbx2rdf.types.abs.impIDLangTypeTgtDtyp;

/**
 * This class provides a textual description
 * <descrip type="context">
 * 
 * www.isocat.org/datcat/DC-149
 * ISO30042: Context - A text which illustrates a concept or a term, by containing the concept designation itself. 
 * Contexts must be authentic, that is, they must be obtained from an existing source, and not created by the terminologist. Contexts are
 * documented very frequently in terminology collections. Contexts can provide information for determining term usage
 * and collocations. In TBX, the context as a term-related data-category. A context can be further categorized according
 * to context type.
 * @author Philipp Cimiano - Universität Bielefeld 
 * @author Victor Rodriguez - Universidad Politécnica de Madrid
 */
public class Descrip extends impIDLangTypeTgtDtyp {

    public Descrip(NodeList value, Mapping type, String lang, Mappings mappings) {
        super(type,lang,mappings, value);
    }
}